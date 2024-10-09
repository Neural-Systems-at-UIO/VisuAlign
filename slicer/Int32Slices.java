package slicer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import nii.Nifti1Dataset;

public class Int32Slices {
    //public final boolean bigendian;
    public final short type;
    public final short XDIM;
    public final short YDIM;
    public final short ZDIM;
    //public final long offset;
    //public final String blob;
    private final ByteBuffer blob;
    public final short BPV;
    
    private final int[][][] blob10;

    /**
     * Class for generating 2D slices from NIfTI datasets
     * 
     * @param aNiftiFile path and filename to the actual NIfTI file
     * @throws Exception if file access fails, unsupported data format encountered,
     *                   or XML does not contain description for id.
     */
    public Int32Slices(String aNiftiFile) throws Exception {
        Nifti1Dataset n1d = new Nifti1Dataset(aNiftiFile);
        n1d.readHeader();
//        blob = n1d.ds_datname;
//        if (blob.endsWith(".gz"))
//            throw new Exception("Compressed Nifti is not supported.");
        type = n1d.datatype;
        BPV = Nifti1Dataset.bytesPerVoxel(type);
        if (BPV > 4)
            throw new Exception(Nifti1Dataset.decodeDatatype(type) + " is not supported.");
//        bigendian = n1d.big_endian;
//        offset = (long) n1d.vox_offset;
        XDIM = n1d.XDIM;
        YDIM = n1d.YDIM;
        ZDIM = n1d.ZDIM;
//        System.out.println(aNiftiFile);
//        System.out.println(XDIM+" "+YDIM+" "+ZDIM);

//        try(RandomAccessFile raf=new RandomAccessFile(n1d.ds_datname,"r");
//                FileChannel fc=raf.getChannel()){
//            blob=fc.map(MapMode.READ_ONLY, (long)n1d.vox_offset, raf.length()-(long)n1d.vox_offset);
//            blob.order(n1d.big_endian?ByteOrder.BIG_ENDIAN:ByteOrder.LITTLE_ENDIAN);
//        }
        
        if(aNiftiFile.endsWith("_10um.cutlas\\labels.nii.gz")) {
        	if(n1d.big_endian || n1d.getDatatype()!=Nifti1Dataset.NIFTI_TYPE_UINT32)
        		throw new Exception("?!");
        	blob=null;
        	blob10=new int[ZDIM][YDIM][XDIM];
        	var a=new Alert(AlertType.INFORMATION, aNiftiFile);
        	a.setTitle("Please wait");
        	a.setHeaderText("Loading high-resolution atlas, this will take some time");
        	a.show();
        	var task=new Task<Void>() {
				@Override
				protected Void call() throws Exception {
			        try(DataInputStream dis=new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(aNiftiFile),1024*1024),1024*1024))) {
//			            dis.skipBytes((int)n1d.vox_offset);
//			            System.out.println(n1d.vox_offset);
			        	dis.skipNBytes(352);
			            for(int z=0;z<ZDIM;z++) {
			            	//System.out.println(ZDIM-z);
			            	updateMessage(z+"/"+ZDIM);
			            	for(int y=0;y<YDIM;y++)
			            		for(int x=0;x<XDIM;x++)
			            			blob10[z][y][x]=dis.readInt();
			            }
			        }
					return null;
				}
			};
			task.setOnSucceeded(event->Platform.exitNestedEventLoop(aNiftiFile, null));
			a.contentTextProperty().bind(task.messageProperty());
			new Thread(task).start();
			Platform.enterNestedEventLoop(aNiftiFile);
	        a.close();
        } else {
        	blob10=null;
	        byte bytes[]=new byte[XDIM*YDIM*ZDIM*BPV];
	        try(DataInputStream dis=new DataInputStream(new GZIPInputStream(new FileInputStream(aNiftiFile)))) {
	            dis.skipBytes((int)n1d.vox_offset);
	            dis.readFully(bytes);
	        }
	        blob=ByteBuffer.wrap(bytes);
	        blob.order(n1d.big_endian?ByteOrder.BIG_ENDIAN:ByteOrder.LITTLE_ENDIAN);
        }
    }

    /**
     * @param ox        origin, x coordinate
     * @param oy        origin, y coordinate
     * @param oz        origin, z coordinate
     * @param ux        horizontal axis of the slice, x component
     * @param uy        horizontal axis of the slice, y component
     * @param uz        horizontal axis of the slice, z component
     * @param vx        vertical axis of the slice, x component
     * @param vy        vertical axis of the slice, y component
     * @param vz        vertical axis of the slice, z component
     * @param grayscale scale result into the range of 0-65535. Ignored for RGB
     *                  data. Implied for floating point data
     * @return 2D integer array containing the slice
     * @throws IOException when a file operation fails
     */
    public final int[][] getInt32Slice(double ox, double oy, double oz, double ux, double uy, double uz, double vx,
            double vy, double vz, boolean grayscale) {
    	if(blob10!=null) {
    		ox*=2.5;oy*=2.5;oz*=2.5;
    		ux*=2.5;uy*=2.5;uz*=2.5;
    		vx*=2.5;vy*=2.5;vz*=2.5;
    	}
    	
        final int width = (int) Math.sqrt(ux * ux + uy * uy + uz * uz) + 1;
        final int height = (int) Math.sqrt(vx * vx + vy * vy + vz * vz) + 1;

        int slice[][] = new int[height][width];

        final int dataxlinelen = XDIM * BPV;
        final int datazslicesize = dataxlinelen * YDIM;

        for (int y = 0; y < height; y++) {
            final double hx = ox + vx * y / (height);
            final double hy = oy + vy * y / (height);
            final double hz = oz + vz * y / (height);
            for (int x = 0; x < width; x++) {
                final int lx = (int) (hx + ux * x / (width));
                final int ly = (int) (hy + uy * x / (width));
                final int lz = (int) (hz + uz * x / (width));
                if (lx >= 0 && lx < XDIM && ly >= 0 && ly < YDIM && lz >= 0 && lz < ZDIM)
                	if(blob!=null) {
	                    blob.position(BPV * lx + ly * dataxlinelen + lz * datazslicesize);
	                    switch (type) {
	                    case Nifti1Dataset.NIFTI_TYPE_INT8:
	                        slice[y][x] = blob.get();
	                        break;
	                    case Nifti1Dataset.NIFTI_TYPE_UINT8:
	                        slice[y][x] = blob.get() & 0xFF;
	                        break;
	                    case Nifti1Dataset.NIFTI_TYPE_INT16:
	                        slice[y][x] = blob.getShort();
	                        break;
	                    case Nifti1Dataset.NIFTI_TYPE_UINT16:
	                        slice[y][x] = blob.getShort() & 0xFFFF;
	                        break;
	                    case Nifti1Dataset.NIFTI_TYPE_INT32:
	                    case Nifti1Dataset.NIFTI_TYPE_UINT32:
	                    case Nifti1Dataset.NIFTI_TYPE_FLOAT32:
	                        slice[y][x] = blob.getInt();
	                        break;
	                    case Nifti1Dataset.NIFTI_TYPE_RGB24:
	                        slice[y][x] = ((blob.get() & 0xFF) << 16) + (blob.getShort() & 0xFFFF);
	                        break;
	                    }
	                } else {
	                	slice[y][x]=blob10[lz][YDIM-1-ly][lx];
	                }
            }
        }

        if (type == Nifti1Dataset.NIFTI_TYPE_FLOAT32) {
            float min = Float.MAX_VALUE;
            float max = -min;
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    float w = Float.intBitsToFloat(slice[y][x]);
                    if (w < min)
                        min = w;
                    if (w > max)
                        max = w;
                }
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    slice[y][x] = (int) (65535 * (Float.intBitsToFloat(slice[y][x]) - min) / (max - min));
        } else if (grayscale && type != Nifti1Dataset.NIFTI_TYPE_RGB24) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    int w = slice[y][x];
                    if (w < min)
                        min = w;
                    if (w > max)
                        max = w;
                }
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    slice[y][x] = 65535 * (slice[y][x] - min) / (max - min);
        }

        return slice;
    }
}

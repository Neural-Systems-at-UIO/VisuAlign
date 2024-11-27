package data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import math.Estimator;
import nonlin.Triangle;

import static data.Marker.marker;

public class Slice implements Comparable<Slice> {
    public String filename;
    public List<String> filenames = new ArrayList<>();
    public double nr;
    public double width;
    public double height;
    public List<Double> anchoring = new ArrayList<>();
    public List<ArrayList<Double>> markers = new ArrayList<>();

    public String toString() {
		var line = "\n{";
		if (filename != null)
			line += String.format("\"filename\":\"%s\"", filename);
		else
			line += "\"filenames\":" + filenames.stream().collect(Collectors.joining("\",\"", "[\"", "\"]"));
		line += String.format(",\"nr\":%d,\"width\":%d,\"height\":%d", (int) nr, (int) width, (int) height);
		if (!noanchoring)
			line += ",\"anchoring\":" + anchoring;
		if (markers.size() > 0)
			line += ",\"markers\":" + markers;
		line += "}";
		return line;
//        return "\n{\"filename\":\"" + filename + "\",\"nr\":" + (int) nr + ",\"width\":" + (int) width + ",\"height\":"
//                + (int) height + (noanchoring ? "" : ",\"anchoring\":" + anchoring)
//                + (markers.size() > 0 ? ",\"markers\":" + markers : "") + "}";
    }

    public List<List<Double>> trimarkers;
    public List<Triangle> triangles;

    public void triangulate() {
        trimarkers = new ArrayList<>();
//        trimarkers.add(marker(0, 0));
//        trimarkers.add(marker(width, 0));
//        trimarkers.add(marker(0, height));
//        trimarkers.add(marker(width, height));
        trimarkers.add(marker(-width*0.1, -height*0.1));
        trimarkers.add(marker(width*1.1, -height*0.1));
        trimarkers.add(marker(-width*0.1, height*1.1));
        trimarkers.add(marker(width*1.1, height*1.1));
        triangles = new ArrayList<Triangle>();
        triangles.add(new Triangle(0, 1, 2, trimarkers));
        triangles.add(new Triangle(1, 2, 3, trimarkers));
        byte edges[][] = new byte[markers.size() + 4][markers.size() + 4];
        edges[0][1] = edges[0][2] = edges[1][2] = edges[1][3] = edges[2][3] = 2;

        for (List<Double> m : markers) {
            double x = m.get(2);
            double y = m.get(3);
            boolean found=false;
            ArrayList<Triangle> remove=new ArrayList<>();
            for(Triangle tri:triangles) {
                if(found || tri.intri(x, y)!=null)
                    found=true;
                if(tri.incirc(x, y))
                    remove.add(tri);
            }
            if(found) {
                for(Triangle tri:remove) {
                    edges[tri.a][tri.b]--;
                    edges[tri.a][tri.c]--;
                    edges[tri.b][tri.c]--;
                }
                triangles.removeAll(remove);
                trimarkers.add(m);
                ArrayList<Triangle> newtriangles = new ArrayList<>();
                for (int i = 0; i < edges.length; i++)
                    for (int j = 0; j < edges.length; j++)
                        if (edges[i][j] == 1) {
                            Triangle tri = new Triangle(i, j, trimarkers.size() - 1, trimarkers);
                            if (tri.decomp != null)
                                newtriangles.add(tri);
                        }
                triangles.addAll(newtriangles);
                for (Triangle tri : newtriangles) {
                    edges[tri.a][tri.b]++;
                    edges[tri.a][tri.c]++;
                    edges[tri.b][tri.c]++;
                }
            }
        }
    }

    @Override
    public int compareTo(Slice s) {
        return (int) (nr - s.nr);
    }

    private double normalize(int idx) {
        double len = 0;
        for (int i = 0; i < 3; i++)
            len += anchoring.get(idx + i) * anchoring.get(idx + i);
        len = Math.sqrt(len);
        for (int i = 0; i < 3; i++)
            anchoring.set(idx + i, anchoring.get(idx + i) / len);
        return len;
    }

    private void orthonormalize() {
        normalize(3);
        double dot = 0;
        for (int i = 0; i < 3; i++)
            dot += anchoring.get(i + 3) * anchoring.get(i + 6);
        for (int i = 0; i < 3; i++)
            anchoring.set(i + 6, anchoring.get(i + 6) - anchoring.get(i + 3) * dot);
        normalize(6);
    }

    boolean decompose() {
        if (anchoring.size() == 9) {
            for (int i = 0; i < 3; i++)
                anchoring.set(i, anchoring.get(i) + (anchoring.get(i + 3) + anchoring.get(i + 6)) / 2);
            anchoring.add(normalize(3) / width);
            anchoring.add(normalize(6) / height);
            return true;
        }
        return false;
    }

    boolean recompose() {
        if (anchoring.size() == 11) {
            orthonormalize();
            final double wr = anchoring.get(9);
            final double hr = anchoring.get(10);
            for (int i = 0; i < 3; i++) {
                anchoring.set(i + 3, anchoring.get(i + 3) * wr * width);
                anchoring.set(i + 6, anchoring.get(i + 6) * hr * height);
                anchoring.set(i, anchoring.get(i) - (anchoring.get(i + 3) + anchoring.get(i + 6)) / 2);
            }
            anchoring.remove(10);
            anchoring.remove(9);
            return true;
        }
        return false;
    }

    public boolean noanchoring;

    public boolean from(Estimator ests[]) {
        if (anchoring.size() == 0) {
            noanchoring = true;
            for (int i = 0; i < ests.length; i++)
                anchoring.add(ests[i].get(nr));
            orthonormalize();
            return true;
        }
        return false;
    }
    
    public int layerindex = 0;
}

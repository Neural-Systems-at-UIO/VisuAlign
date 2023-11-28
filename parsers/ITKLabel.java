package parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Palette;
import data.SegLabel;

public class ITKLabel {
//    public final String name;
//    public final byte red;
//    public final byte green;
//    public final byte blue;
//    public final short remap;
//
//    public ITKLabel(short remap, String name, byte red, byte green, byte blue) {
//        this.name = name;
//        this.red = red;
//        this.green = green;
//        this.blue = blue;
//        this.remap=remap;
//    }
//
	static Random rnd = new Random();

	static int clampRnd(String s) {
		int i = Integer.parseInt(s);
		i += rnd.nextInt(41) - 20;
		if (i < 0)
			i = 0;
		if (i > 255)
			i = 0;
		return i;
	}

	public static Palette parseLabels(String labelFile, boolean rainbow) throws Exception {
		TreeMap<Integer, SegLabel> palette = new TreeMap<>();
		int next = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(labelFile))) {
			Pattern p = Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)[^\\\"]*\"(.*)\"\\s*");
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					int id = Integer.parseInt(m.group(1));
					if (palette.containsKey(id))
						throw new Exception("Duplicate label #" + id + " (" + m.group(4) + ")");
					if (!rainbow || id==0)
						palette.put(id, new SegLabel(id, Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)),
								Integer.parseInt(m.group(4)), m.group(5), next++));
					else
						palette.put(id, new SegLabel(id, clampRnd(m.group(2)), clampRnd(m.group(3)),
								clampRnd(m.group(4)), m.group(5), next++));
				}
			}
		}
		return new Palette(palette);
	}
}

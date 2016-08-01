package ch.aiko.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PropertyUtil {

	protected ArrayList<String> lines;
	protected File file;

	public PropertyUtil(File f) {
		lines = new ArrayList<String>(Arrays.asList(FileUtil.ReadFile(f).split("\n")));
		file = f;
	}

	public PropertyUtil(String lines, String path) {
		file = FileUtil.LoadFileInClassPath(path);
		this.lines = new ArrayList<String>(Arrays.asList(lines.split("\n")));
	}

	/**
	 * Create new Instance of the PropertyUtil class The Properties are getting loaded from the file like this:
	 * 
	 * key=value
	 * 
	 * @param f
	 *            The File with the properties in it
	 * @return The Instance of PropertyUtil, which can read the Files properties
	 */
	public static PropertyUtil LoadFile(File f) {
		return new PropertyUtil(f);
	}

	/**
	 * Create new Instance of the PropertyUtil class The Properties are getting loaded from the file like this:
	 * 
	 * key=value
	 * 
	 * @param f
	 *            The File with the properties in it
	 * @return The Instance of PropertyUtil, which can read the Files properties
	 */
	public static PropertyUtil LoadFile(String f) {
		return new PropertyUtil(FileUtil.LoadFile(f));
	}

	/**
	 * Create new Instance of the PropertyUtil class The Properties are getting loaded from the file like this:
	 * 
	 * key=value
	 * 
	 * @param f
	 *            The File with the properties in it (in ClassPath)
	 * @return The Instance of PropertyUtil, which can read the Files properties
	 */
	public static PropertyUtil LoadFileInClassPath(String f) {
		return new PropertyUtil(FileUtil.ReadFileInClassPath(f), f);
	}

	/**
	 * Searches in the File for the key and returns the value If it doesn't find the key, it returns the key
	 * 
	 * @param key
	 *            The Key of the Property
	 * @return The Value of the Property
	 */
	public String getValue(String key) {
		for (String line : lines) {
			if (!line.contains("=") || line.replace(" ", "").startsWith("//") || line.replace(" ", "").startsWith("#")) continue;
			if (line.split("=")[0].replace(" ", "").trim().equalsIgnoreCase(key)) return line.substring(line.split("=")[0].length() + 1);
		}

		return key;
	}

	/**
	 * Searches in the File for the key and returns the value as Integer. If it doesn't find the key, it returns 0
	 * 
	 * @param key
	 *            The Key of the Property
	 * @return The Value of the Property
	 */
	public int getIntegerValue(String key) {
		try {
			return Integer.parseInt(getValue(key));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Searches in the File for the key and returns the value as Boolean. If it doesn't find the key, it returns false
	 * 
	 * @param key
	 *            The Key of the Property
	 * @return The Value of the Property
	 */
	public boolean getBooleanValue(String key) {
		try {
			return Boolean.parseBoolean(getValue(key));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Searches in the File for the key and returns the value as Double. If it doesn't find the key, it returns 0
	 * 
	 * @param key
	 *            The Key of the Property
	 * @return The Value of the Property
	 */
	public double getDoubleValue(String key) {
		try {
			return Double.parseDouble(getValue(key));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Searches in the File for the key and returns the value as Float. If it doesn't find the key, it returns 0
	 * 
	 * @param key
	 *            The Key of the Property
	 * @return The Value of the Property
	 */
	public float getFloatValue(String key) {
		try {
			return Float.parseFloat(getValue(key));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Searches in the File for the value and returns the key If it doesn't find the value, it returns the argument
	 * 
	 * @param key
	 *            The Value of the Property
	 * @return The Key of the Property
	 */
	public String getKey(String value) {
		for (String line : lines) {
			if (!line.contains("=") || line.replace(" ", "").startsWith("//") || line.replace(" ", "").startsWith("#")) continue;
			if (line.substring(line.split("=")[0].length() + 1).replace(" ", "").trim().equalsIgnoreCase(value)) return line.split("=")[0];
		}

		return value;
	}

	/**
	 * Searches for comments in the document. Comments are either marked with '//' or with '#' A good use for this is for a code with more than one file:
	 * 
	 * #lang_code=1 //config_file_code=2
	 * 
	 * @return The value of the comment with the key key
	 * @param key
	 *            The key of the comment
	 */
	public String getCommentValue(String key) {
		for (String line : lines) {
			if (!line.contains("=")) continue;
			if (line.replace(" ", "").startsWith("//") || line.replace(" ", "").startsWith("#")) {
				if (line.split("=")[0].replace(" ", "").replace("//", "/").substring(1).trim().equalsIgnoreCase(key)) return line.substring(line.split("=")[0].length() + 1);
			}
		}
		return key;
	}

	public void setValue(String key, String value) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (!line.contains("=") || line.replace(" ", "").startsWith("//") || line.replace(" ", "").startsWith("#")) continue;
			if (line.split("=")[0].replace(" ", "").trim().equalsIgnoreCase(key)) {
				line = key + "=" + value;
				lines.set(i, line);
				if (lines.get(0).trim().replace(" ", "").replace("\n", "").trim().equalsIgnoreCase("")) ArrayUtil.delete(lines.toArray(new String[lines.size()]), 0);
				FileUtil.WriteFile(ArrayUtil.combineToFile(lines.toArray(new String[lines.size()])), file);
				return;
			}
		}

		FileUtil.WriteFile(ArrayUtil.combineToFile(lines.toArray(new String[lines.size()])) + key + "=" + value, file);
		lines = new ArrayList<String>(Arrays.asList(FileUtil.ReadFile(file).split("\n")));
	}

	public void printFile() {
		for (String s : lines) {
			System.out.println(s);
		}
	}

	public HashMap<String, String> getEntrySet() {
		HashMap<String, String> entries = new HashMap<String, String>();

		for (String line : lines) {
			String key = line.split("=")[0].replace(" ", "").trim();
			String value = line.substring(key.length() + 1);

			entries.put(key, value);
		}

		return entries;
	}

	public boolean exists(String key) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.split("=")[0].replace(" ", "").trim().equalsIgnoreCase(key)) return true;
		}
		return false;
	}

	public boolean isIntegerValue(String key) {
		try {
			Integer.parseInt(getValue(key));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isBooleanValue(String key) {
		try {
			Boolean.parseBoolean(getValue(key));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void addValue(String key, String value) {
		lines.add(key + "=" + value);
	}
}

package ch.aiko.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import ch.aiko.modloader.LoadedMod;
import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.server.PokemonServer;

public abstract class FileUtil {

	/**
	 * Loads a File. You have to give it the entire path of the file
	 * 
	 * @param path
	 *            The entire path to the File
	 * @return The File as File object
	 */
	public static File LoadFile(String path) {
		return new File(path);
	}
	
	public static File getRunningJar() {
		File Me = null;
		try {
			Me = new File(FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			PokemonServer.out.err("Error while searching for running file.");
			return null;
		}
		return Me;
	}
	
	/**
	 * Loads a File, which is in your ClassPath. Try to use a slash at the beginning of the String
	 * 
	 * @param path
	 *            The Path to the File from your ClassPath (e.g. "/ + package + filename")
	 * @return The File as File object
	 */
	public static File LoadFileInClassPath(String path) {
		return new File(new File("").getAbsolutePath() + path);
	}
	
	/**
	 * For Pokemon project
	 * 
	 * @param path
	 * @return
	 */
	public static InputStream getResourceAsStream(String path) {
		for (LoadedMod mod : ModLoader.loadedMods) {
			InputStream inStream = mod.loader.getResourceAsStream(path);
			if (inStream != null) return inStream;
		}
		PokemonServer.out.err("Error loading " + path + ". Resource cannot be found. EVERYBODY PANIC!!!");
		return null;
	}

	public static InputStream LoadFileInClassPathAsStream(String path) {
		return FileUtil.class.getResourceAsStream("/" + path);
	}

	/**
	 * Reads the text from a Text file Needs the entire path to the File
	 * 
	 * @param f
	 *            The path to the File, which should be read.
	 * @return The Text from the file
	 */
	public static String ReadFile(String f) {
		return ReadFile(LoadFile(f));
	}

	/**
	 * Reads a File in the ClassPath. Should start with a slash
	 * 
	 * @param f
	 *            The path to the File from your ClassPath
	 * @return The Text of the File
	 */
	public static String ReadFileInClassPath(String f) {
		if (FileUtil.class.getResource(f) == null) CreateNewFileInClassPath(f);
		String file = "";
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtil.class.getClass().getResourceAsStream(f)));
			String line = "";
			while ((line = reader.readLine()) != null) {
				file += line + "\n";
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace(PokemonServer.out);
		}
		return file;
	}

	/**
	 * Reads the Text from a File object
	 * 
	 * @param f
	 *            The File
	 * @return The Text from the File
	 * @see FileUtils.LoadFile(String path)
	 */
	public static String ReadFile(File f) {
		String file = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				file += line + "\n";
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace(PokemonServer.out);
		}

		return file;
	}

	/**
	 * Creates a new File. If it exists it'll do nothing
	 * 
	 * @param file
	 *            The File which should be created
	 */
	public static void CreateNewFile(File file) {
		if (file.exists()) return;
		if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace(PokemonServer.out);
		}
	}

	/**
	 * Creates a new File. If it exists it'll do nothing
	 * 
	 * @param s
	 *            The Path to the File which should be created.
	 */
	public static void CreateNewFile(String s) {
		CreateNewFile(LoadFile(s));
	}

	/**
	 * Creates a new File in the ClassPath. If it exists it'll do nothing
	 * 
	 * @param s
	 *            The Path to the File from the ClassPath
	 */
	public static void CreateNewFileInClassPath(String s) {
		CreateNewFile(new File("").getAbsolutePath() + s);
	}

	/**
	 * Creates a new File with the Text from arg1 if it exists it will be deleted
	 * 
	 * @param entireFile
	 *            The text that should stand in the new created file
	 * @param file
	 *            The File which should be written
	 */
	public static void WriteFile(String entireFile, File file) {
		if (file.exists()) file.delete();
		CreateNewFile(file);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

			writer.write(entireFile);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace(PokemonServer.out);
		}
	}

	/**
	 * Creates a new File with the Text from arg1 if it exists it will be deleted
	 * 
	 * @param entireFile
	 *            The text that should stand in the new created file
	 * @param file
	 *            The File which should be written as entire path
	 */
	public static void WriteFile(String entireFile, String file) {
		WriteFile(entireFile, LoadFile(file));
	}

	/**
	 * Appends a text to a existing File. If it doesn't exist it'll be created
	 * 
	 * @param addedText
	 *            The Text that should be added
	 * @param file
	 *            The File which the Text should be added
	 */
	public static void AddToFile(String addedText, File file) {
		if (!file.exists()) CreateNewFile(file);

		String entire = ReadFile(file) + addedText;
		WriteFile(entire, file);
	}

	/**
	 * Appends a text to a existing File. If it doesn't exist it'll be created
	 * 
	 * @param addedText
	 *            The Text that should be added
	 * @param file
	 *            The File which the Text should be added
	 */
	public static void AddToFile(String addedText, String file) {
		AddToFile(addedText, LoadFile(file));
	}

	/**
	 * Appends a text to a existing File in ClassPath. If it doesn't exist it'll be created
	 * 
	 * @param addedText
	 *            The Text that should be added
	 * @param file
	 *            The File which the Text should be added
	 */
	public static void AddToFileInClassPath(String addedText, String file) {
		AddToFile(addedText, LoadFileInClassPath(file));
	}

	/**
	 * Copies a File or Directory to another Destination
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFile(File src, File dest) {
		if (dest.exists()) dest.delete();

		if (src.isDirectory()) {
			dest.mkdirs();
			File[] files = src.listFiles();
			dest.mkdirs();
			for (File f : files) {
				CopyFile(f, new File(dest.getAbsolutePath() + "/" + f.getName()));
			}
		} else {
			CreateNewFile(dest);
			try {
				if (!dest.exists()) CreateNewFile(dest);

				BufferedOutputStream ou = new BufferedOutputStream(new FileOutputStream(dest.getAbsolutePath()));
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(src.getAbsolutePath()));

				int len = 0;
				byte[] buffer = new byte[1024];

				while ((len = in.read(buffer)) > 0) {
					ou.write(buffer, 0, len);
				}

				in.close();
				ou.close();
			} catch (Exception e) {
				e.printStackTrace(PokemonServer.out);
			}
		}
	}

	/**
	 * Copies a File or Directory to another Destination
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFile(String src, String dest) {
		CopyFile(LoadFile(src), LoadFile(dest));
	}

	/**
	 * Copies a File or Directory to another Destination in ClassPath
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFileInClassPath(String src, String dest) {
		CopyFile(LoadFileInClassPath(src), LoadFileInClassPath(dest));
	}

	/**
	 * Copies a File or Directory to another Destination
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFile(File src, String dest) {
		CopyFile(src, LoadFile(dest));
	}

	/**
	 * Copies a File or Directory to another Destination
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFile(String src, File dest) {
		CopyFile(LoadFile(src), dest);
	}

	/**
	 * Copies a File or Directory to another Destination in ClassPath
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFileToClassPath(File src, String dest) {
		CopyFile(src, LoadFileInClassPath(dest));
	}

	/**
	 * Copies a File or Directory from ClassPath to another Destination
	 * 
	 * @param src
	 *            The File which should be copied
	 * @param dest
	 *            The File which gets replaced by the new File
	 */
	public static void CopyFileFromClassPath(String src, File dest) {
		CopyFile(LoadFileInClassPath(src), dest);
	}

	/**
	 * Deletes A File or Directory
	 * 
	 * @param file
	 *            The File to be deleted
	 */
	public static void DeleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				DeleteFile(f);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

	public static File[] listFilesInClassPath(String dir) {
		return new File(new File("").getAbsolutePath() + dir).listFiles();
	}

	public static ArrayList<File> listFiles(String dir) {
		ArrayList<File> files = new ArrayList<File>();
		File f = new File(dir);
		if(!f.isDirectory()) {files.add(f); return files;}
		for (File file : f.listFiles()) {
			if (f.isDirectory()) {
				ArrayList<File> second = listFiles(file.getAbsolutePath());
				files.addAll(second);
			} else files.add(file);
		}
		return files;
	}
	
	public static ArrayList<File> listFiles(String dir, String endFileName) {
		ArrayList<File> files = new ArrayList<File>();
		File f = new File(dir);
		for (File file : f.listFiles()) {
			if (file.isDirectory()) {
				ArrayList<File> second = listFiles(file.getAbsolutePath());
				files.addAll(second);
			} else if(file.getName().endsWith(endFileName)) files.add(file);
		}
		return files;
	}

	public static final File ROOT = new File(".");

	public static File saveTmp(String text) {
		try {
			File f = File.createTempFile("tmpText" + new Random().nextInt(), "txt");
			FileUtil.WriteFile(text, f);
			return f;
		} catch (IOException e) {
			e.printStackTrace(PokemonServer.out);
		}
		return null;
	}

	private static File createFile(String ref) {
		File file = new File(ROOT, ref);
		if (!file.exists()) {
			file = new File(ref);
		}

		return file;
	}

	public static URL getResource(String ref) {
		URL url = FileUtil.class.getClassLoader().getResource(ref);
		if (url == null) {
			try {
				File f = createFile(ref);
				if (f.exists()) return f.toURI().toURL();
			} catch (IOException e) {}
		}
		return url;
	}
}

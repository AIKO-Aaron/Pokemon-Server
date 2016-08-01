package ch.aiko.pokemon.language;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ch.aiko.pokemon.server.PokemonServer;
import ch.aiko.util.FileUtil;
import ch.aiko.util.PropertyUtil;

public class Language extends PropertyUtil {

	public static final String defaultLang = "de_de";
	private static HashMap<String, Language> languages = new HashMap<String, Language>();
	public static Language current;

	private String lang_id;
	private String __lang;

	public Language(String pathToFile) {
		super(FileUtil.ReadFileInClassPath((pathToFile.startsWith("/") ? "" : "/") + pathToFile), pathToFile);
		PokemonServer.out.println("Successfully loaded: " + pathToFile + " as a Language File");
		lang_id = pathToFile.replace("lang", "").replace(".", "");
		lang_id = lang_id.split("/")[lang_id.split("/").length - 1];
		__lang = pathToFile.split("/")[pathToFile.split("/").length - 1].split("\\.")[0];
	}

	public static void appendTranslations(String lang, String file) {
		if (languages.containsKey(lang)) {
			Language l = languages.get(lang);
			l.appendTranslations(new ArrayList<String>(Arrays.asList(file.split("\n"))));
		}
	}

	public void appendTranslation(String key, String value) {
		if (exists(key)) setValue(key, value);
		else addValue(key, value);
	}

	public void appendTranslations(ArrayList<String> lines) {
		this.lines.addAll(lines);
	}

	public static void setup() {
		File Me = null;
		try {
			Me = new File(Language.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			PokemonServer.out.err("Error while searching for language files.");
			return;
		}
		PokemonServer.out.println("Path to me: " + Me.getAbsolutePath());
		ArrayList<String> langFiles = new ArrayList<String>();
		if (Me.isDirectory()) {
			ArrayList<File> files = FileUtil.listFiles(Me.getAbsolutePath(), ".lang");
			for (File f : files)
				langFiles.add(f.getAbsolutePath().substring(Me.getAbsolutePath().length()));
		} else {
			try {
				JarFile file = new JarFile(Me);
				Enumeration<JarEntry> entries = file.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.isDirectory()) continue;
					if (entry.getName().endsWith(".lang")) langFiles.add(entry.getName());
				}

				file.close();
			} catch (IOException e) {
				e.printStackTrace(PokemonServer.out);
			}
		}

		PokemonServer.out.println("Init Languages");
		languages.clear();
		for (String f : langFiles) {
			if (f.startsWith(".") || !f.endsWith(".lang")) continue;
			String key = f.replace("lang", "").replace(".", "").split("/")[f.replace("lang", "").replace(".", "").split("/").length - 1].replace("/", "");
			languages.put(key, new Language(f));
		}
		loadLanguage(defaultLang);
	}

	public static ArrayList<String> getLanguageNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Entry<String, Language> l : languages.entrySet()) {
			names.add(l.getKey());
		}
		return names;
	}

	public static ArrayList<Language> getLanguages() {
		ArrayList<Language> langs = new ArrayList<Language>();
		for (Entry<String, Language> l : languages.entrySet()) {
			langs.add(l.getValue());
		}
		return langs;
	}

	public static void setCurrentLanguage(int index) {
		System.out.println("Setting language: " + getLanguageNames().get(index));
		current = languages.get(getLanguageNames().get(index));
	}

	public static void loadLanguage(String lang) {
		if (!languages.containsKey(lang)) return;

		current = languages.get(lang);
	}

	public static String translate(String s) {
		s = s.replace("/", " / ");
		String ret = "";
		for (String part : s.split(" ")) {
			ret += current.getValue(part) + " ";
		}
		ret = ret.substring(0, ret.length() - 1);
		ret = ret.replace(" / ", "/");
		ret = ret.substring(0, 1).toUpperCase() + ret.substring(1);
		return ret;
	}

	public String getName() {
		return (lang_id);
	}

	public String getLanguageName() {
		return getValue(__lang);
	}
}

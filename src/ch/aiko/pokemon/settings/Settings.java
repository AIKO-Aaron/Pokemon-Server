package ch.aiko.pokemon.settings;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map.Entry;

import ch.aiko.engine.graphics.Renderer;
import ch.aiko.pokemon.server.PokemonServer;
import ch.aiko.util.FileUtil;
import ch.aiko.util.PropertyUtil;

public class Settings extends PropertyUtil {
		
	public static final boolean CREATE_NEW_CONFIG_ON_START = false;
	
	public Settings(String path) {
		super(FileUtil.LoadFile(path));
	}

	public static boolean isUserFont = true;
	public static String font;
	public static Font userFont;
	public static boolean isFirstLaunch = false;
	
	public static Settings instance;
	public static float GAIN = -10F;
	
	public static Settings getInstance() {
		return instance;
	}
	
	public static void load() {
		if(CREATE_NEW_CONFIG_ON_START) new File(getPath() + "/settings/config.cfg").delete();
		if(!new File(getPath() + "/settings/config.cfg").exists()) FileUtil.CreateNewFile(getPath() + "/settings/config.cfg");
		instance = new Settings(getPath() + "/settings/config.cfg");
		
		isFirstLaunch = !instance.getBooleanValue("setupCorrect");
		
		if(isFirstLaunch) {
			instance.setValue("setupCorrect", "true");
		}
		
		for(Entry<String, String> entry : PropertyUtil.LoadFileInClassPath("/ch/aiko/pokemon/settings/fields").getEntrySet().entrySet()) {
			PokemonServer.out.println("Checking for key: " + entry.getKey() + " default value: " + entry.getValue());
			if(!instance.exists(entry.getKey())) instance.setValue(entry.getKey(), entry.getValue());
		}		
		
		font = get("font");
		if(font.startsWith("user/")) {
			isUserFont = true;
			font = font.substring(5);
			String font1 = font.contains("fonts/") ? font.split("/")[font.split("/").length - 1].split("\\.")[0].replace("-", " ") : font;
			if (!existsFont(font1)) {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				try {
					userFont = Font.createFont(Font.TRUETYPE_FONT, Settings.class.getResourceAsStream("/" + font));
					ge.registerFont(userFont);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			font = font1;
		} else {
			userFont = new Font(font, Font.PLAIN, 1);
		}
		
		GAIN = getFloat("gain");
	}
	
	public static String getPath() {
		/**String s = System.getProperty("user.home") + "/";
		
		if(System.getProperty("os.name").contains("Mac")) s += "/Library/Application Support/Pokemon/";
		else if(System.getProperty("os.name").contains("Windows")) s += "/AppData/Roaming/Pokemon/";
		else s += "Pokemon/";*/
		
		return FileUtil.getRunningJar().getParent();
	}
	
	public static String get(String key) {
		return instance.getValue(key);
	}
	
	public static int getInteger(String key) {
		return instance.getValue(key).equalsIgnoreCase(key) ? PropertyUtil.LoadFileInClassPath("/settings/fields").getIntegerValue(key) : instance.getIntegerValue(key);
	}
	
	public static float getFloat(String key) {
		return instance.getValue(key).equalsIgnoreCase(key) ? PropertyUtil.LoadFileInClassPath("/settings/fields").getFloatValue(key) : instance.getFloatValue(key);
	}

	public static double getDouble(String key) {
		return instance.getValue(key).equalsIgnoreCase(key) ? PropertyUtil.LoadFileInClassPath("/settings/fields").getDoubleValue(key) : instance.getDoubleValue(key);
	}
	
	public static boolean getBoolean(String key) {
		return instance.getValue(key).equalsIgnoreCase(key) ? PropertyUtil.LoadFileInClassPath("/settings/fields").getBooleanValue(key) : instance.getBooleanValue(key);
	}
	
	public static void set(String key, String value) {
		instance.setValue(key, value);
		
		if(key.equalsIgnoreCase("font")) {
			font = get("font");
			if(font.startsWith("user/")) {
				isUserFont = true;
				font = font.substring(5);
				String font1 = font.contains("fonts/") ? font.split("/")[font.split("/").length - 1].split("\\.")[0].replace("-", " ") : font;
				if (!existsFont(font1)) {
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					try {
						userFont = Font.createFont(Font.TRUETYPE_FONT, Settings.class.getResourceAsStream("/" + font));
						ge.registerFont(userFont);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				font = font1;
			} else {
				userFont = new Font(font, Font.PLAIN, 1);
			}
		}
	}
	
	protected static boolean existsFont(String font) {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (Font f : e.getAllFonts()) {
			if (f.getFontName().equalsIgnoreCase(font)) return true;
		}

		return false;
	}
	
	public static File extract(String filePath) {
		try {
			File f = File.createTempFile(filePath, null);
			FileOutputStream resourceOS = new FileOutputStream(f);
			byte[] byteArray = new byte[1024];
			int i;
			InputStream classIS = Renderer.class.getResourceAsStream("/" + filePath);
			while ((i = classIS.read(byteArray)) > 0) {
				resourceOS.write(byteArray, 0, i);
			}
			classIS.close();
			resourceOS.close();
			return f;
		} catch (Exception e) {
			return null;
		}
	}
}

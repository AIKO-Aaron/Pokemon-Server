package ch.aiko.modloader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadedMod {

	public String name = "Add a name in the .amf file: name=<insertNameHere>";
	public String pathToJar;
	public ArrayList<Method> preInits = new ArrayList<>();
	public ArrayList<Method> Inits = new ArrayList<>();
	public ArrayList<Method> postInits = new ArrayList<>();
	public ModInfo modInfoList = null;
	public HashMap<String, Object> instances = new HashMap<>();
	public ClassLoader loader;
	
	public String toString() {
		return name;
	}
	
}

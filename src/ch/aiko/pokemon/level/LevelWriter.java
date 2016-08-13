package ch.aiko.pokemon.level;

import java.io.File;
import java.util.ArrayList;

import ch.aiko.as.ASDataBase;
import ch.aiko.engine.sprite.Sprite;

public class LevelWriter {

	// Just to write the maps --> Testing only
	public static void main(String[] args) {
		LevelWriter writer = new LevelWriter("level2", "/ch/aiko/pokemon/level/test.layout");
		writer.write(System.getProperty("user.home") + "/Desktop/level2.bin");
		System.out.println("Done");
	}

	ASDataBase db;
	ArrayList<Sprite> spriteCodings;
	ArrayList<Integer> solids;
	ArrayList<Integer> ids;

	public LevelWriter(String name, String layoutFile) {
		db = new ASDataBase(name);
		Level level = new Level();
		level.type.name = "LevelData";
		level.type.reload();
		spriteCodings = new ArrayList<Sprite>();
		solids = new ArrayList<Integer>();
		ids = new ArrayList<Integer>();

		new LayoutLoader(layoutFile).writeToLevel(level, db);
	}

	public void write(String string) {
		if (new File(string).exists()) new File(string).delete();
		db.saveToFile(string);
	}

}

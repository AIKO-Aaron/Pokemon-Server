package ch.aiko.pokemon.level;

import java.util.ArrayList;
import java.util.regex.Pattern;

import ch.aiko.as.ASArray;
import ch.aiko.as.ASDataType;
import ch.aiko.as.ASObject;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSerialization;
import ch.aiko.engine.sprite.Tile;

public class LevelPalette extends ASDataType {

	private ASDataType level;
	private ArrayList<Short> shortCodes;
	private ArrayList<String> stringCodes;

	public LevelPalette(ASObject levelData, ASDataType l) {
		this.level = l;
		init(levelData);
	}

	public LevelPalette(String levelData, ASDataType l) {
		this.level = l;
		init(levelData);
	}

	public LevelPalette(ASDataType l) {
		this("NoData", l);
	}

	public void addCoding(short c, Tile s) {
		addCoding(c, SpriteSerialization.getSpriteID(s.sprite) + "\\$" + s.layer);
	}

	public void addCoding(short c, Sprite s) {
		addCoding(c, SpriteSerialization.getSpriteID(s) + "\\$0");
	}

	public void addCoding(short c, Sprite s, int layer) {
		addCoding(c, SpriteSerialization.getSpriteID(s) + "\\$" + layer);
	}

	public void addCoding(short c, String s) {
		shortCodes.add(new Short(c));
		stringCodes.add(s);
		// reload();
		if (level != null) {
			level.removeObject(name);
			level.addObject(this);
		}
	}

	// Sprite t2 = ((ASDataArray<Sprite>) base.getObject("SpriteArrayHolder").getArray("SpriteArray")).getData(0)
	@Override
	public void load(ASObject c) {
		if (shortCodes == null) shortCodes = new ArrayList<Short>();
		ASArray shor = c.getArray("shortCodes");
		if (shor != null) for (short s : shor.getShortData())
			shortCodes.add(s);

		if (stringCodes == null) stringCodes = new ArrayList<String>();
		ASArray spri = c.getArray("sprites");
		if (spri != null) for (String s : spri.getStringData())
			stringCodes.add(s);
	}

	@Override
	public void getData(ASObject thisObject) {
		if (shortCodes == null) shortCodes = new ArrayList<Short>();
		short[] shorts = new short[shortCodes.size()];
		for (int i = 0; i < shorts.length; i++)
			shorts[i] = shortCodes.get(i);
		thisObject.addArray(ASArray.Short("shortCodes", shorts));
		if (stringCodes == null) stringCodes = new ArrayList<String>();
		String[] st = stringCodes.toArray(new String[stringCodes.size()]);
		thisObject.addArray(ASArray.String("sprites", st));
	}

	@Override
	public String toString() {
		return ((shortCodes.size() > 0 && stringCodes.size() > 0) ? shortCodes.get(0) + ": " + stringCodes.get(0) : super.toString());
	}

	public Tile getCoding(short s, int x, int y) {
		if (!shortCodes.contains(s)) return null;
		String code = stringCodes.get(shortCodes.indexOf((Object) s));
		int id = Integer.parseInt(code.split(Pattern.quote("\\$"))[0]);
		int layer = Integer.parseInt(code.split(Pattern.quote("\\$"))[1]);

		return new Tile(SpriteSerialization.getSprite(id), x, y, layer);
		// return new Tile(id, layer, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
		// return new Tile(stringCodes.get(shortCodes.indexOf((Object) s)), x, y, Level.TILE_SIZE, Level.TILE_SIZE);
	}

	public Tile getCoding(int s, int x, int y) {
		return getCoding((short) s, x, y);
	}
}

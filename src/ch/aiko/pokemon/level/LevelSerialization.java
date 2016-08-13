package ch.aiko.pokemon.level;

import java.util.ArrayList;

import ch.aiko.as.ASArray;
import ch.aiko.as.ASDataType;
import ch.aiko.as.ASField;
import ch.aiko.as.ASObject;
import ch.aiko.as.SerializationReader;

public class LevelSerialization extends ASDataType {

	private Level level;

	public LevelSerialization(Level l) {
		level = l;
		name = "LevelData";
		object = new ASObject(name);
	}

	@Override
	public void load(ASObject c) {
		ASField layers = c.getField("Layers");
		if (layers != null) level.layerCount = SerializationReader.readInt(layers.data, 0);
		for (int i = 0; i < level.layerCount; i++) {
			ASArray array = c.getArray("Tiles" + i);
			if (array != null) level.tileData.add(array.getShortData());
		}
		ASObject palette = c.getObject("Palette");
		if (palette != null) level.lp = new LevelPalette(palette, this);
		ASField width = c.getField("FieldWidth");
		ASField height = c.getField("FieldHeight");
		if (width != null) level.fieldWidth = SerializationReader.readInt(width.data, 0);
		if (height != null) level.fieldHeight = SerializationReader.readInt(height.data, 0);
		if (width == null && height == null) {
			ASField size = c.getField("Size");
			if (size != null) level.fieldHeight = level.fieldWidth = SerializationReader.readInt(size.data, 0);
		}

		level.decode();
	}

	@Override
	public void getData(ASObject thisObject) {
		if (level.tileData == null) level.tileData = new ArrayList<short[]>();
		if (level.lp == null) level.lp = new LevelPalette("Palette", this);
		thisObject.addField(ASField.Integer("Layers", level.tileData.size()));
		for (int i = 0; i < level.tileData.size(); i++) {
			ASArray tiles = ASArray.Short("Tiles" + i, level.tileData.get(i));
			thisObject.addArray(tiles);
		}
		thisObject.addObject(level.lp.toObject());
		thisObject.addField(ASField.Integer("FieldWidth", level.fieldWidth));
		thisObject.addField(ASField.Integer("FieldHeight", level.fieldHeight));
	}

}

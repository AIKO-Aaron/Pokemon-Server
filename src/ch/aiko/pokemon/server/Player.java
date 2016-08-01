package ch.aiko.pokemon.server;

import ch.aiko.as.ASDataType;
import ch.aiko.as.ASField;
import ch.aiko.as.ASObject;
import ch.aiko.as.ASString;
import ch.aiko.as.SerializationReader;

public class Player extends ASDataType {

	protected String uuid;
	protected String currentLevel; // Path to level
	protected int x = 128, y = 128, dir;
	// TODO team-pokemon storing...

	public Player() {
		init("Player");
		if(currentLevel == null) currentLevel = "/ch/aiko/pokemon/level/test.layout";
	}
	
	public Player(ASObject obj) {
		init(obj, "Player");
		if(currentLevel == null) currentLevel = "/ch/aiko/pokemon/level/test.layout";
	}

	public void load(ASObject c) {
		ASString uu = c.getString("UUID");
		ASString pp = c.getString("LVLPATH");
		ASField xx = c.getField("X");
		ASField yy = c.getField("Y");
		ASField dd = c.getField("DIR");
		if (uu != null) uuid = uu.toString();
		if (pp != null) currentLevel.toString();
		if (xx != null) y = SerializationReader.readInt(xx.data, 0);
		if (yy != null) x = SerializationReader.readInt(yy.data, 0);
		if (dd != null) dir = SerializationReader.readInt(dd.data, 0);
	}

	public void getData(ASObject thisObject) {
		thisObject.addString(ASString.Create("UUID", uuid));
		thisObject.addString(ASString.Create("LVLPATH", currentLevel));
		thisObject.addField(ASField.Integer("X", x));
		thisObject.addField(ASField.Integer("Y", y));
		thisObject.addField(ASField.Integer("DIR", dir));
	}

}

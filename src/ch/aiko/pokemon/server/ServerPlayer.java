package ch.aiko.pokemon.server;

import java.util.ArrayList;

import ch.aiko.as.ASArray;
import ch.aiko.as.ASDataBase;
import ch.aiko.as.ASDataType;
import ch.aiko.as.ASField;
import ch.aiko.as.ASObject;
import ch.aiko.as.ASString;
import ch.aiko.as.SerializationReader;
import ch.aiko.pokemon.basic.ModUtils;
import ch.aiko.pokemon.pokemons.PokemonType;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.pokemon.pokemons.TeamPokemon;

public class ServerPlayer extends ASDataType {

	public String uuid;
	public String currentLevel; // Path to level
	public int x = 128, y = 128, dir;
	public int gender;
	public String name;
	public boolean online;
	public ArrayList<Integer> trainersDefeated = new ArrayList<Integer>();
	// TODO team-pokemon storing...
	public TeamPokemon[] team = new TeamPokemon[PokemonServer.TeamSize];

	public ServerPlayer(String uuid) {
		this.uuid = uuid;
		if (currentLevel == null) currentLevel = "";
		init("Player");
	}

	public ServerPlayer(ASObject obj) {
		init(obj, "Player");
		if (currentLevel == null) currentLevel = "";
	}

	public void load(ASObject c) {
		ASString uu = c.getString("UUID");
		ASString pp = c.getString("LVLPATH");
		ASField xx = c.getField("X");
		ASField yy = c.getField("Y");
		ASField dd = c.getField("DIR");
		ASField ts = c.getField("TEAMSIZE");
		ASObject teamP = c.getObject("TEAM");
		ASArray tdo = c.getArray("TD");
		ASString name = c.getString("NAME");
		ASField gender = c.getField("GENDER");
		if (ts != null) team = new TeamPokemon[SerializationReader.readInt(ts.data, 0)];
		if (uu != null) uuid = uu.toString();
		if (pp != null) currentLevel = pp.toString();
		if (xx != null) y = SerializationReader.readInt(xx.data, 0);
		if (yy != null) x = SerializationReader.readInt(yy.data, 0);
		if (dd != null) dir = SerializationReader.readInt(dd.data, 0);
		if (name != null) this.name = name.toString();
		if (gender != null) this.gender = SerializationReader.readInt(gender.data, 0);
		int index = 0;
		if (teamP != null) {
			for (int i = 0; i < teamP.objectCount; i++) {
				ASObject obj = teamP.objects.get(i);
				if (obj != null) team[index++] = new TeamPokemon(obj);
			}
		}
		if (index == 0 || teamP == null) {
			team = new TeamPokemon[PokemonServer.TeamSize];
			team[0] = new TeamPokemon(Pokemons.get(6), PokemonType.OWNED, "Pokemon", ModUtils.convertToAttacks("Tackle", "Verzweifler"), 5, 10, 10, 10, 10, 10, 10, 10);
		}
		if (tdo != null) {
			trainersDefeated = new ArrayList<Integer>();
			for (int i : tdo.getIntData()) {
				trainersDefeated.add(i);
			}
		}
	}

	public void getData(ASObject thisObject) {
		thisObject.addString(ASString.Create("UUID", uuid));
		thisObject.addString(ASString.Create("LVLPATH", currentLevel));
		thisObject.addField(ASField.Integer("X", x));
		thisObject.addField(ASField.Integer("Y", y));
		thisObject.addField(ASField.Integer("DIR", dir));
		thisObject.addField(ASField.Integer("TEAMSIZE", PokemonServer.TeamSize));
		thisObject.addField(ASField.Integer("GENDER", gender));
		thisObject.addString(ASString.Create("NAME", name));
		ASObject teamP = new ASObject("TEAM");
		for (TeamPokemon pok : team) {
			if (pok != null) {
				pok.reload();
				teamP.addObject(pok.toObject());
			}
		}
		thisObject.addObject(teamP);
		int[] data = new int[trainersDefeated.size()];
		for (int i = 0; i < data.length; i++)
			data[i] = trainersDefeated.get(i);
		thisObject.addArray(ASArray.Integer("TD", data));

	}

	public ASDataBase toBase() {
		ASDataBase base = new ASDataBase("P");
		base.addObject(this);
		return base;
	}

}

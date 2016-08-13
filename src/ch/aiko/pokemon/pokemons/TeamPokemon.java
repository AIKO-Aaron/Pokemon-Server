package ch.aiko.pokemon.pokemons;

import ch.aiko.as.ASDataType;
import ch.aiko.as.ASField;
import ch.aiko.as.ASObject;
import ch.aiko.as.ASString;
import ch.aiko.as.SerializationReader;
import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.graphics.Updatable;
import ch.aiko.pokemon.attacks.Attack;
import ch.aiko.pokemon.attacks.AttackUtil;
import ch.aiko.pokemon.server.PokemonServer;

public class TeamPokemon extends ASDataType implements Renderable, Updatable {

	protected PokemonType holder;
	protected Pokemons type;
	protected String nickname;
	private Attack[] moveSet;
	protected PokemonState currentState;

	protected float damageToDeal = 0;

	// STATS
	protected float healthPoints;
	protected int maxHP;
	protected int attack;
	protected int specAttack;
	protected int defense;
	protected int specDefense;
	protected int speed;
	protected int level;
	protected int xp;

	public TeamPokemon(ASObject obj1) {
		init(obj1);
	}

	public TeamPokemon(Pokemons type, PokemonType holder, String nickname, Attack[] moveSet, int hp, int maxHP, int atk, int satk, int def, int sdef, int speed, int xp) {
		this.name = "Pok";
		this.healthPoints = hp;
		this.maxHP = maxHP;
		this.type = type;
		this.holder = holder;
		this.nickname = nickname;
		attack = atk;
		specAttack = satk;
		defense = def;
		specDefense = sdef;
		this.speed = speed;
		this.xp = xp;
		level = (int) Math.pow(xp, 1F / 3F);
		currentState = PokemonState.NORMAL;
		this.setMoveSet(moveSet);
	}

	public void load(ASObject c) {
		attack = SerializationReader.readInt(c.getField("ATK").data, 0);
		specAttack = SerializationReader.readInt(c.getField("SAT").data, 0);
		defense = SerializationReader.readInt(c.getField("DFS").data, 0);
		specDefense = SerializationReader.readInt(c.getField("SDF").data, 0);
		speed = SerializationReader.readInt(c.getField("SPD").data, 0);
		xp = SerializationReader.readInt(c.getField("XP").data, 0);
		healthPoints = SerializationReader.readInt(c.getField("HP").data, 0);
		maxHP = SerializationReader.readInt(c.getField("MHP").data, 0);
		type = PokeUtil.get(SerializationReader.readInt(c.getField("NUM").data, 0));
		holder = PokeUtil.getType(SerializationReader.readInt(c.getField("TYP").data, 0));
		currentState = PokeUtil.getState(SerializationReader.readInt(c.getField("STATE").data, 0));
		nickname = c.getString("NCN").toString();
		ASObject atks = c.getObject("ATKS");
		if (atks != null) {
			moveSet = (new Attack[atks.stringCount]);
			for (int i = 0; i < getMoveSet().length; i++) {
				moveSet[i] = AttackUtil.getAttack(atks.strings.get(i).toString());
			}
		} else moveSet = new Attack[] { AttackUtil.getAttack("Tackle"), AttackUtil.getAttack("Flammenwurf") };
		level = (int) Math.pow(xp, 1F / 3F);
	}

	public void getData(ASObject thisObject) {
		thisObject.addField(ASField.Integer("ATK", attack));
		thisObject.addField(ASField.Integer("SAT", specAttack));
		thisObject.addField(ASField.Integer("DFS", defense));
		thisObject.addField(ASField.Integer("SDF", specDefense));
		thisObject.addField(ASField.Integer("SPD", speed));
		thisObject.addField(ASField.Integer("XP", xp));
		thisObject.addField(ASField.Integer("HP", (int) healthPoints));
		thisObject.addField(ASField.Integer("MHP", maxHP));
		thisObject.addField(ASField.Integer("NUM", type.getPokedexNumber()));
		thisObject.addField(ASField.Integer("TYP", holder.in));
		thisObject.addField(ASField.Integer("STATE", currentState.getID()));
		thisObject.addString(ASString.Create("NCN", nickname.toCharArray()));
		ASObject atks = new ASObject("ATKS");
		for (Attack a : getMoveSet()) {
			atks.addString(ASString.Create("MOVE", a.attackName));
		}
		thisObject.addObject(atks);
	}

	public void gainXP(int amount) {
		if (xp >= 1000000) return;
		xp += amount;
		if (xp >= 1000000) xp = 1000000;
		int ol = level;
		level = (int) Math.pow(xp, 1F / 3F);
		if (level > ol) System.out.println("Level up to: " + level);
		if (type.canEvolve(level)) evolve();
	}

	public int getXPToLevel() {
		return (int) Math.pow(level + 1, 3) - xp;
	}

	private void evolve() {
		int t = type.getEvolvesInto();
		if (t < 0 || t > PokeUtil.pokemons.size()) PokemonServer.out.err("I don't do a thing and I'm proud of it!");
		else {
			setType(PokeUtil.get(t));
		}
	}

	@Override
	public void render(Renderer renderer) {}

	public void setType(Pokemons t) {
		type = t;
	}

	public void advance() {
		if (type.isMegaEvolution()) type = PokeUtil.get(type.getChild().getPokedexNumber() + 1);
		else type = PokeUtil.get(type.getPokedexNumber() + 1);
	}

	@Override
	public void update(Screen screen, Layer l) {}

	public void ko() {

	}

	public void hit(int damage) {
		damageToDeal += damage;
	}

	public void mega() {
		if (type.hasMegaEvolution()) {
			int index = 1;
			setType(type.getMegaEvolution(index));
		} else if (type.isMegaEvolution()) {
			Pokemons sub = type.getChild();
			if (sub.getMegaEvolutions().size() > type.getIndex()) setType(sub.getMegaEvolution(type.getIndex() + 1));
		} else System.err.println("This pokmon doesn't have a mega-evolution");
	}

	public String getNickName() {
		return nickname;
	}

	public int getHP() {
		return (int) healthPoints;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setHP(int i) {
		healthPoints = i;
	}

	public void addHP(int i) {
		healthPoints += i;
	}

	public Attack[] getMoveSet() {
		return moveSet;
	}

	public void setMoveSet(Attack[] moveSet) {
		this.moveSet = moveSet;
	}

	public float getCurrentHealthPoints() {
		return healthPoints;
	}

	public int getLevel() {
		return (int) Math.pow(xp, 1F / 3F);
	}
}

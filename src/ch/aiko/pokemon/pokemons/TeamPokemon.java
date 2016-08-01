package ch.aiko.pokemon.pokemons;

import ch.aiko.as.ASDataType;
import ch.aiko.as.ASField;
import ch.aiko.as.ASObject;
import ch.aiko.as.ASString;
import ch.aiko.as.SerializationReader;
import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.graphics.Updatable;
import ch.aiko.pokemon.graphics.GIFAnimation;
import ch.aiko.pokemon.server.PokemonServer;

public class TeamPokemon extends ASDataType implements Renderable, Updatable {

	protected GIFAnimation animation;

	protected PokemonType holder;
	protected Pokemons type;
	protected String nickname;

	// STATS
	protected int healthPoints;
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

	public TeamPokemon(Pokemons type, PokemonType holder, String nickname, int hp, int maxHP, int atk, int satk, int def, int sdef, int speed, int xp) {
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

		animation = new GIFAnimation(PokeUtil.getAnimation(type, holder), 0, 0);
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
		nickname = c.getString("NCN").toString();

		level = (int) Math.pow(xp, 1F / 3F);
		//animation = new GIFAnimation(type.getPathToAnimation(holder), 0, 0, holder == PokemonType.OWNED ? OWN_MOD * SCALE : SCALE).replaceColor(0xFFFFFFFF, 0);
		animation = new GIFAnimation(PokeUtil.getAnimation(type, holder), 0, 0);
	}

	public void getData(ASObject thisObject) {
		thisObject.addField(ASField.Integer("ATK", attack));
		thisObject.addField(ASField.Integer("SAT", specAttack));
		thisObject.addField(ASField.Integer("DFS", defense));
		thisObject.addField(ASField.Integer("SDF", specDefense));
		thisObject.addField(ASField.Integer("SPD", speed));
		thisObject.addField(ASField.Integer("XP", xp));
		thisObject.addField(ASField.Integer("HP", healthPoints));
		thisObject.addField(ASField.Integer("MHP", maxHP));
		thisObject.addField(ASField.Integer("NUM", type.getPokedexNumber()));
		thisObject.addField(ASField.Integer("TYP", holder.in));
		thisObject.addString(ASString.Create("NCN", nickname.toCharArray()));
	}

	public void gainXP(int amount) {
		if(xp >= 1000000) return;
		xp += amount;
		if(xp >= 1000000) xp = 1000000;
		int ol = level;
		level = (int) Math.pow(xp, 1F / 3F);
		if(level > ol) System.out.println("Level up to: " + level);
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

	public void render(Renderer renderer) {}

	public void setType(Pokemons t) {
		type = t;
		animation = new GIFAnimation(PokeUtil.getAnimation(type, holder), 0, 0);
	}

	public void advance() {
		if (type.isMegaEvolution()) type = PokeUtil.get(type.getChild().getPokedexNumber() + 1);
		else type = PokeUtil.get(type.getPokedexNumber() + 1);
		animation = new GIFAnimation(PokeUtil.getAnimation(type, holder), 0, 0);
	}

	public void update(Screen screen) {
		animation.update(screen);
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
		return healthPoints;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setHP(int i) {healthPoints = i;}
	public void addHP(int i) {healthPoints += i;}

}

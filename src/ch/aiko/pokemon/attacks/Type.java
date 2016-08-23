package ch.aiko.pokemon.attacks;

public class Type {

	public static Type FIRE, GRASS, WATER, NORMAL, FIGHT, FLYING, POISON, GROUND, ROCK, BUG, GHOST, STEEL, ELECTRO, PSYCHO, ICE, DRAGON, DARK, FAIRY;

	static {
		NORMAL = new Type(combine(GHOST), combine(), combine(ROCK, STEEL));
		FIGHT = new Type(combine(), combine(NORMAL, ROCK, STEEL, ICE, DARK), combine(FLYING, POISON, BUG, PSYCHO, FAIRY));
		FLYING = new Type(combine(GROUND), combine(FIGHT, BUG, GRASS), combine(ROCK, STEEL, ELECTRO));
		POISON = new Type(combine(), combine(GRASS, FAIRY), combine(POISON, GROUND, ROCK, GHOST));
		GROUND = new Type(combine(ELECTRO), combine(POISON, ROCK, STEEL, FIRE, ELECTRO), combine(GRASS, BUG));
		ROCK = new Type(combine(), combine(FLYING, BUG, FIRE, ICE), combine(FIGHT, GROUND, STEEL));
		BUG = new Type(combine(), combine(GRASS, PSYCHO, DARK), combine(FIGHT, FLYING, GROUND, GHOST, STEEL, FIRE, FAIRY));
		GHOST = new Type(combine(NORMAL, FIGHT), combine(GHOST, PSYCHO), combine(DARK));
		STEEL = new Type(combine(POISON), combine(ROCK, ICE, FAIRY), combine(STEEL, FIRE, WATER, ELECTRO));
		FIRE = new Type(combine(), combine(BUG, STEEL, GRASS, ICE), combine(ROCK, FIRE, WATER, DRAGON));
		WATER = new Type(combine(), combine(GROUND, ROCK, FIRE), combine(WATER, GRASS, DRAGON));
		GRASS = new Type(combine(), combine(GROUND, ROCK, WATER), combine(FLYING, POISON, BUG, STEEL, FIRE, GRASS, DRAGON));
		ELECTRO = new Type(combine(), combine(FLYING, WATER), combine(GRASS, ELECTRO, DRAGON));
		PSYCHO = new Type(combine(), combine(FIGHT, POISON), combine(STEEL, PSYCHO));
		ICE = new Type(combine(), combine(FLYING, GROUND, GRASS, DRAGON), combine(STEEL, FIRE, WATER, ICE));
		DRAGON = new Type(combine(), combine(DRAGON), combine(STEEL));
		DARK = new Type(combine(PSYCHO), combine(GHOST, PSYCHO), combine(FIGHT, DARK, FAIRY));
		FAIRY = new Type(combine(DRAGON), combine(FIGHT, DRAGON, DARK), combine(POISON, STEEL, FIRE));
	}

	private static Type[] combine(Type... types) {
		return types;
	}

	private Type[] goodAgainst, ressistent, badAgainst;

	public Type(Type[] res, Type[] goodAgainst, Type[] badAgainst) {
		this.goodAgainst = goodAgainst;
		ressistent = res;
		this.badAgainst = badAgainst;
	}

	public boolean isGoodAgainst(Type def) {
		for (Type o : goodAgainst) {
			if (o == def) return true;
		}
		return false;
	}

	public boolean getsDamage(Type attacker) {
		for (Type t : ressistent)
			if (t == attacker) return false;
		return true;
	}

	public boolean isBadAgainst(Type def) {
		for (Type o : badAgainst) {
			if (o == def) return true;
		}
		return false;
	}

	public float getModFor(Type other) {
		if (other.getsDamage(this)) {
			if (isGoodAgainst(other)) return 2;
			if (isBadAgainst(other)) return 0.5F;
		} else return 0;
		return 1;
	}

}

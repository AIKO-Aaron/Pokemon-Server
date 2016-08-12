package ch.aiko.pokemon.attacks;

public class Type {

	public static Type FIRE, GRASS, WATER;

	static {
		FIRE = new Type(GRASS);
		GRASS = new Type(WATER);
		WATER = new Type(FIRE);
	}

	private Type[] goodAgainst;

	public Type(Type... types) {
		goodAgainst = types;
	}

	public boolean isGoodAgainst(Type ot) {
		for (Type o : goodAgainst) {
			if (o == ot) return true;
		}
		return false;
	}

}

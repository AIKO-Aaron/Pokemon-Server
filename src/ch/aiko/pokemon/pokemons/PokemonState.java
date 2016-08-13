package ch.aiko.pokemon.pokemons;

public abstract class PokemonState {

	public static PokemonState NORMAL, DEFEATED;

	public static void init() {
		DEFEATED = new PokemonState("K.O.", false, 1) {
			public void onAttack(TeamPokemon attacking) {}
		};
		NORMAL = new PokemonState("", true, 0) {
			public void onAttack(TeamPokemon attacking) {}
		};
	}

	public String name;
	public boolean canBattle;

	public PokemonState(String name, boolean canBattle, int id) {
		PokeUtil.registerState(this, id);
		this.name = name;
		this.canBattle = canBattle;
	}

	public int getID() {
		return PokeUtil.getIDOf(this);
	}

	public abstract void onAttack(TeamPokemon attacking);

	public static final PokemonState getState(int i) {
		return PokeUtil.getState(i);
	}

}

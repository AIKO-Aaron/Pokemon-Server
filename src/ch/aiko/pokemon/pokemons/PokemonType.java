package ch.aiko.pokemon.pokemons;

public enum PokemonType {

	ENEMY(0),
	WILD(1),
	OWNED(2);
	
	public int in;
	private PokemonType(int in) {
		this.in = in;
	}
}

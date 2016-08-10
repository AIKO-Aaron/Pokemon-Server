package ch.aiko.pokemon.pokemons;

import java.util.ArrayList;

import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.graphics.GIF;
import ch.aiko.pokemon.graphics.TextureLoader;

public class PokeUtil {

	public static final float SCALE = 2F;
	public static final float OWN_MOD = 1.5F;

	public static final ArrayList<Pokemons> pokemons = new ArrayList<Pokemons>();
	public static final ArrayList<PokemonType> types = new ArrayList<PokemonType>();
	public static final ArrayList<GIF> frontAnimations = new ArrayList<GIF>();
	public static final ArrayList<GIF> backAnimations = new ArrayList<GIF>();

	public static int index = 0;
	public static int START_INDEX = 0;
	public static int MAX_POKEMON = 0;

	public static void registerPokemon(Pokemons p) {
		if (p.getPokedexNumber() == 1) START_INDEX = pokemons.size();
		pokemons.add(p);
		frontAnimations.add(null);
		backAnimations.add(null);
		++MAX_POKEMON;
	}

	public static void registerPokemon(Pokemons p, Class<?> loader) {
		pokemons.add(p);
		frontAnimations.add(null);
		backAnimations.add(null);
		++MAX_POKEMON;
	}

	public static void loadEmAll() {
		ModLoader.Status = 3;
		for (int i = 0; i < pokemons.size(); i++) {
			Pokemons p = pokemons.get(i);
			ModLoader.CoreInit = "Loading sprites for: " + p.getName();
			frontAnimations.set(i, TextureLoader.loadGIF(p.getPathToAnimation(PokemonType.ENEMY), SCALE).replaceColor(0xFFFFFFFF, 0));
			backAnimations.set(i, TextureLoader.loadGIF(p.getPathToAnimation(PokemonType.OWNED), SCALE * OWN_MOD).replaceColor(0xFFFFFFFF, 0));
			index++;
			ModLoader.bar3.setValue(100 * index / MAX_POKEMON);
		}
		ModLoader.Status = 7;
	}

	public static void load(int num) {
		load(get(num));
	}

	public static void load(Pokemons p) {
		int indexOf = pokemons.indexOf(p);
		frontAnimations.set(indexOf, TextureLoader.loadGIF(p.getPathToAnimation(PokemonType.ENEMY), SCALE).replaceColor(0xFFFFFFFF, 0));
		backAnimations.set(indexOf, TextureLoader.loadGIF(p.getPathToAnimation(PokemonType.OWNED), SCALE * OWN_MOD).replaceColor(0xFFFFFFFF, 0));
	}

	public static GIF getFrontAnimation(Pokemons p) {
		if (backAnimations.get(pokemons.indexOf(p)) == null) load(p);
		return frontAnimations.get(pokemons.indexOf(p));
	}

	public static GIF getBackAnimation(Pokemons p) {
		if (backAnimations.get(pokemons.indexOf(p)) == null) load(p);
		return backAnimations.get(pokemons.indexOf(p));
	}

	public static GIF getAnimation(Pokemons type, PokemonType holder) {
		return holder == PokemonType.OWNED ? getBackAnimation(type) : getFrontAnimation(type);
	}

	public static Pokemons get(int pokedexNumber) {
		if (pokemons.size() > pokedexNumber + START_INDEX && pokemons.get(pokedexNumber + START_INDEX).getPokedexNumber() == pokedexNumber) return pokemons.get(pokedexNumber + START_INDEX);
		for (Pokemons p : pokemons) {
			if (p.getPokedexNumber() == pokedexNumber) return p;
		}
		return get(1);
	}

	public static PokemonType getType(int readInt) {
		switch(readInt) {
			case 0: return PokemonType.ENEMY;
			case 1: return PokemonType.WILD;
			case 2: return PokemonType.OWNED;
		}
		return PokemonType.ENEMY;
	}

}

package ch.aiko.modloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.aiko.pokemon.language.Language;
import ch.aiko.pokemon.pokemons.PokeUtil;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.pokemon.server.PokemonServer;


public class ModUtils {

	public static InputStream getResourceAsStream(String path) {
		for (LoadedMod mod : ModLoader.loadedMods) {
			InputStream inStream = mod.loader.getResourceAsStream(path);
			if (inStream != null) return inStream;
		}
		PokemonServer.out.err("Error loading " + path + ". Resource cannot be found. EVERYBODY PANIC!!!");
		return null;
	}

	public static String readFile(String path) {
		String ret = "", line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(path)));
		try {
			while ((line = reader.readLine()) != null) {
				ret += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace(PokemonServer.out);
		}
		return ret;
	}
	
	public static void addTranslations(String lang, String pathToFile) {
		Language.appendTranslations(lang, readFile(pathToFile));
	}
	
	public static void createNewPokemon(String name, int num) {
		new Pokemons(name, num);
	}
	
	public static void createNewPokemon(String name, int num, int lvlForEvo, int evolvesTo) {
		new Pokemons(name, num, lvlForEvo, evolvesTo);
	}
	
	public static void createMegaEvolution(String name, int origPokemon) {
		Pokemons orig = PokeUtil.get(origPokemon);
		Pokemons mega = new Pokemons(name, orig.getMegaEvolutions().size() + 1, origPokemon);
		orig.addMegaEvolution(mega);
	}

}

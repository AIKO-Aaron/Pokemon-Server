package ch.aiko.pokemon.basic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import ch.aiko.modloader.GameEvent;
import ch.aiko.modloader.LoadedMod;
import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.attacks.Attack;
import ch.aiko.pokemon.attacks.AttackUtil;
import ch.aiko.pokemon.attacks.Type;
import ch.aiko.pokemon.language.Language;
import ch.aiko.pokemon.pokemons.PokeUtil;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.pokemon.server.PokemonServer;

public class ModUtils {

	public static final boolean isServer = true;

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

	public static void createNewPokemon(String name, HashMap<Integer, Attack> moveSet, int num, Type t1) {
		new Pokemons(name, moveSet, num).setType(t1, null);
	}

	public static void createNewPokemon(String name, HashMap<Integer, Attack> moveSet, int num, int lvlForEvo, int evolvesTo, Type t1) {
		new Pokemons(name, moveSet, num, lvlForEvo, evolvesTo).setType(t1, null);
	}
	
	public static void createNewPokemon(String name, HashMap<Integer, Attack> moveSet, int num, Type t1, Type t2) {
		new Pokemons(name, moveSet, num).setType(t1, t2);
	}

	public static void createNewPokemon(String name, HashMap<Integer, Attack> moveSet, int num, int lvlForEvo, int evolvesTo, Type t1, Type t2) {
		new Pokemons(name, moveSet, num, lvlForEvo, evolvesTo).setType(t1, t2);
	}

	public static HashMap<Integer, Attack> createMoveSet(Attack... attacks) {
		HashMap<Integer, Attack> ms = new HashMap<Integer, Attack>();
		for (int i = 0; i < attacks.length; i++) {
			Attack a = attacks[i];
			if (a != null) ms.put(i, a);
		}
		return ms;
	}

	public static void createMegaEvolution(String name, int origPokemon) {
		Pokemons orig = PokeUtil.get(origPokemon);
		Pokemons mega = new Pokemons(name, orig.getMegaEvolutions().size() + 1, origPokemon);
		orig.addMegaEvolution(mega);
	}
	
	public static Attack[] convertToAttacks(String... strings) {
		Attack[] attacks = new Attack[strings.length];
		for (int i = 0; i < attacks.length; i++)
			attacks[i] = AttackUtil.getAttack(strings[i]);
		return attacks;
	}

	public static void executeOnServerOnly(Runnable r) {
		r.run();
	}

	public static void executeOnClientOnly(Runnable r) {}

	public static void eventPerformed(GameEvent evt) {
		ModLoader.performEvent(evt);
	}

	public static void setInitValue(int value) {
		ModLoader.bar3.setValue(value > 100 ? 100 : value < 0 ? 0 : value);
	}

}

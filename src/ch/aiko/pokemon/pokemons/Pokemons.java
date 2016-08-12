package ch.aiko.pokemon.pokemons;

import static ch.aiko.pokemon.basic.ModUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ch.aiko.pokemon.attacks.Attack;

public class Pokemons {
	
	public static void init() {
		// MEGA EVOLUTIONS

		Pokemons MEGA_VENUSAUR = new Pokemons("Mega Venusaur", 1, 3);
		Pokemons MEGA_CHARIZARD_X = new Pokemons("Mega Charizard X", 1, 6);
		Pokemons MEGA_CHARIZARD_Y = new Pokemons("Mega Charizard Y", 2, 6);
		Pokemons MEGA_BLASTOISE = new Pokemons("Mega Blastoise", 1, 9);

		// NORMAL POKEMON

		new Pokemons("Bulbasaur", createMoveSet(null, null), 1, 16, 2);
		new Pokemons("Ivysaur", createMoveSet(null, null), 2, 32, 3);
		new Pokemons("Venusaur", createMoveSet(null, null), 3, MEGA_VENUSAUR);
		new Pokemons("Charmander", createMoveSet(null, null), 4, 16, 5);
		new Pokemons("Charmeleon", createMoveSet(null, null), 5, 36, 6);
		new Pokemons("Charizard", createMoveSet(null, null), 6, MEGA_CHARIZARD_X, MEGA_CHARIZARD_Y);
		new Pokemons("Squirtle", createMoveSet(null, null), 7, 16, 8);
		new Pokemons("Wartortle", createMoveSet(null, null), 8, 36, 9);
		new Pokemons("Blastoise", createMoveSet(null, null), 9, MEGA_BLASTOISE);
	}

	private String unlocName;
	private int lvlForEvo;
	private int pokedexNumber;
	private boolean isMega = false;
	private ArrayList<Pokemons> megas = new ArrayList<Pokemons>();
	private int index, evolvesTo, evolvesFrom;
	private HashMap<Integer, Attack> moveSet;

	public Pokemons(String unlocName, HashMap<Integer, Attack> moveSet, int pokedexNumber, int lvlForEvo, int evolvesTo) {
		this.lvlForEvo = lvlForEvo;
		this.pokedexNumber = pokedexNumber;
		this.evolvesTo = evolvesTo;
		this.unlocName = unlocName;
		this.moveSet = moveSet;
		PokeUtil.registerPokemon(this);
	}

	public Pokemons(String unlocName, HashMap<Integer, Attack> moveSet, int pokedexNumber, int lvlForEvo, int evolvesTo, Pokemons... megas) {
		this.lvlForEvo = lvlForEvo;
		this.pokedexNumber = pokedexNumber;
		this.evolvesTo = evolvesTo;
		this.megas = new ArrayList<Pokemons>(Arrays.asList(megas));
		this.unlocName = unlocName;
		this.moveSet = moveSet;
		PokeUtil.registerPokemon(this);
	}

	public Pokemons(String unlocName, HashMap<Integer, Attack> moveSet, int pokedexNumber, Pokemons... megas) {
		this.lvlForEvo = Integer.MAX_VALUE;
		this.pokedexNumber = pokedexNumber;
		this.unlocName = unlocName;
		this.moveSet = moveSet;
		this.megas = new ArrayList<Pokemons>(Arrays.asList(megas));
		PokeUtil.registerPokemon(this);
	}

	public Pokemons(String unlocName, HashMap<Integer, Attack> moveSet, int pokedexNumber) {
		this.lvlForEvo = Integer.MAX_VALUE;
		this.unlocName = unlocName;
		this.pokedexNumber = pokedexNumber;
		this.moveSet = moveSet;
		PokeUtil.registerPokemon(this);
	}

	public Pokemons(String unlocName, int index, int evolvesFrom) {
		this.lvlForEvo = Integer.MAX_VALUE;
		this.pokedexNumber = -1;
		this.unlocName = unlocName;
		this.isMega = true;
		this.index = index;
		this.evolvesFrom = evolvesFrom;
		PokeUtil.registerPokemon(this);
	}

	public boolean hasMegaEvolution() {
		return megas.size() != 0;
	}

	public int getEvolvesInto() {
		return evolvesTo;
	}

	public Pokemons getEvolvedPokemon() {
		return PokeUtil.get(evolvesTo);
	}

	public boolean doesLearnAttackAtLevel(int level) {
		return isMega ? PokeUtil.get(evolvesFrom).doesLearnAttackAtLevel(level) : moveSet.containsKey(level);
	}

	public Attack getAttackAtLevel(int level) {
		return isMega ? PokeUtil.get(evolvesFrom).getAttackAtLevel(level) : moveSet.get(level);
	}

	public Pokemons getChild() {
		if (isMega) {
			return PokeUtil.get(evolvesFrom);
		} else {
			for (Pokemons p : PokeUtil.pokemons) {
				if (p.getEvolvesInto() == pokedexNumber) return p;
			}
		}
		return get(1);
	}

	public ArrayList<Pokemons> getMegaEvolutions() {
		return megas;
	}

	public Pokemons getMegaEvolution(int i) {
		if (megas.size() <= 0) return this;
		for (Pokemons p : megas) {
			if (p.getIndex() == i) return p;
		}
		return megas.get(i);
	}

	public void addMegaEvolution(Pokemons p) {
		megas.add(p);
	}

	public boolean canEvolve(int lvl) {
		return lvl >= lvlForEvo;
	}

	public int getPokedexNumber() {
		return pokedexNumber;
	}

	public boolean isMegaEvolution() {
		return isMega;
	}

	public String getPathToAnimation(PokemonType type) {
		String curNum = "" + (isMega ? evolvesFrom : pokedexNumber);
		while (curNum.length() < 3)
			curNum = "0" + curNum;
		return "ch/aiko/pokemon/textures/pokemon/" + (type == PokemonType.OWNED ? "back/" : "front/") + curNum + (isMega ? "m" + index : "") + ".gif";
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return unlocName;
	}

	public static Pokemons get(int i) {
		return PokeUtil.get(i);
	}

}

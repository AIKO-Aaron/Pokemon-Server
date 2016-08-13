package ch.aiko.pokemon.entity.trainer;

import java.util.ArrayList;

import ch.aiko.pokemon.attacks.Attack;
import ch.aiko.pokemon.attacks.AttackUtil;
import ch.aiko.pokemon.pokemons.PokeUtil;
import ch.aiko.pokemon.pokemons.PokemonType;
import ch.aiko.pokemon.pokemons.TeamPokemon;
import ch.aiko.util.BasicLoader;

public class TrainerLoader extends BasicLoader {

	protected Trainer loading;

	public TrainerLoader(String path) {
		super(path);
	}

	public TrainerLoader(String path, int x, int y) {
		super(path);
		loading.setPosition(x, y);
	}

	@Override
	public void init() {
		loading = new Trainer();
	}

	@Override
	public boolean executePreprocessor(String command, String[] args) {
		if (command.equalsIgnoreCase("SET") || command.equalsIgnoreCase("DEFINE")) {
			if (args.length < 1) return throwError(line);
			String toT = args.length > 1 ? args[1] : "0";
			String frT = args[0];
			if (isKeyWord(frT)) throwError(line, "Cannot assign keywords");

			String varT = text.replace("+", " + ").replace("-", " - ").replace("*", " * ").replace("/", " / ").replace("%", " % ").replace("^", " ^ ").replace("√", " √ ").replace("\n", " \n ").replace("\"", " \" ").replace("#", " # ");
			text = varT.replace(" " + frT + " ", " " + toT + " ").replace(" + ", "+").replace(" - ", "-").replace(" * ", "*").replace(" / ", "/").replace(" % ", "%").replace(" ^ ", "^").replace(" √ ", "√").replace(" \n ", "\n").replace(" \" ", "\"").replace(" # ", "#");
		}

		return true;
	}

	private boolean isKeyWord(String frT) {
		boolean b = false;
		if (!b) b |= frT.equalsIgnoreCase("NAME");
		if (!b) b |= frT.equalsIgnoreCase("BATTLETEXT");
		if (!b) b |= frT.equalsIgnoreCase("ONWIN");
		if (!b) b |= frT.equalsIgnoreCase("ONLOSE");
		if (!b) b |= frT.equalsIgnoreCase("INBATTLETEXT");
		if (!b) b |= frT.equalsIgnoreCase("INBATTLE");
		if (!b) b |= frT.equalsIgnoreCase("TEAMSET");
		if (!b) b |= frT.equalsIgnoreCase("ATK");
		if (!b) b |= frT.equalsIgnoreCase("SPECATK");
		if (!b) b |= frT.equalsIgnoreCase("DEF");
		if (!b) b |= frT.equalsIgnoreCase("SPECDEF");
		if (!b) b |= frT.equalsIgnoreCase("HP");
		if (!b) b |= frT.equalsIgnoreCase("MAXHP");
		if (!b) b |= frT.equalsIgnoreCase("XP");
		if (!b) b |= frT.equalsIgnoreCase("NUMBER");
		if (!b) b |= frT.equalsIgnoreCase("NUMBER");
		if (!b) b |= frT.equalsIgnoreCase("NICKNAME");
		if (!b) b |= frT.equalsIgnoreCase("DIR");
		if (!b) b |= frT.equalsIgnoreCase("SPIN");
		if (!b) b |= frT.equalsIgnoreCase("SPINFUNC");
		return b;
	}

	@Override
	public boolean executeCommand(String command, String[] args) {

		if (command.equalsIgnoreCase("NAME")) {
			loading.name = args[0];
		} else if (command.equalsIgnoreCase("SPIN") || command.equalsIgnoreCase("SPINFUNC")) {
			loading.spinFunc = Integer.parseInt(args[0]);
		} else if (command.equalsIgnoreCase("SPINTIME")) {
			loading.spinTime = Integer.parseInt(args[0]);
		} else if (command.equalsIgnoreCase("BATTLETEXT")) {
			loading.battletext = args[0];
		} else if (command.equalsIgnoreCase("DIR")) {
			loading.dir = Integer.parseInt(args[0]);
		} else if (command.equalsIgnoreCase("SIGHT")) {
			loading.sight = Integer.parseInt(args[0]);
		} else if (command.equalsIgnoreCase("ONWIN")) {
			loading.wintext = args[0];
		} else if (command.equalsIgnoreCase("ONLOSE")) {
			loading.losttext = args[0];
		} else if (command.equalsIgnoreCase("INBATTLETEXT")) {
			loading.inbattletext = args[0];
		} else if (command.equalsIgnoreCase("INBATTLE")) {
			boolean b;
			try {
				b = Boolean.parseBoolean(args[0]);
			} catch (Throwable t) {
				b = false;
			}
			loading.inbattle = b;
		} else if (command.equalsIgnoreCase("TEAMSET")) {
			int index = 0;
			int dexNumber = 0, hp = 0, maxhp = 0, atk = 0, satk = 0, def = 0, sdef = 0, speed = 0, xp = 0;
			String nickname = null;
			ArrayList<Attack> atks = new ArrayList<Attack>();
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.equalsIgnoreCase("INDEX")) index = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("NUMBER")) dexNumber = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("HP")) hp = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("MAXHP")) maxhp = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("ATK")) atk = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("SPECATK")) satk = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("DEF")) def = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("SPECDEF")) sdef = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("SPEED")) speed = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("XP")) xp = Integer.parseInt(args[++i]);
				if (arg.equalsIgnoreCase("NICKNAME")) nickname = args[++i];
				if (arg.equalsIgnoreCase("MOVE")) atks.add(AttackUtil.getAttack(args[++i]));
			}
			if (index < 0 || index >= 6) throwError(line, "Out of bounds! Index must be between 0 and 5");
			loading.team[index] = new TeamPokemon(PokeUtil.get(dexNumber), PokemonType.ENEMY, nickname == null ? PokeUtil.get(dexNumber).getName() : nickname, atks.toArray(new Attack[atks.size()]), hp, maxhp, atk, satk, def, sdef, speed, xp);
		}

		// TODO team-loading

		return true;
	}

	public Trainer getTrainer() {
		return loading;
	}
}

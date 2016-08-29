package ch.aiko.pokemon.server;

import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.attacks.Attack;
import ch.aiko.pokemon.basic.PokemonEvents;
import ch.aiko.pokemon.language.Language;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.util.FileUtil;
import ch.aiko.util.Log;

public class PokemonServer {

	public static Log out = new Log(PokemonServer.class);
	public static UpdateHandler handler;
	public static ServerListener listener;
	public static int TeamSize = 6;
	private static String moddir;
	private static int port;

	public static void main(String[] args) {
		boolean isDir = FileUtil.getRunningJar().isDirectory();
		moddir = (isDir ? FileUtil.getRunningJar().getParent() : FileUtil.getRunningJar().getAbsolutePath()) + "/mods/";
		port = 4732;
		for (String arg : args) {
			if (arg.startsWith("-m=") || arg.startsWith("--mods=")) moddir = arg.substring(arg.split("=")[0].length() + 1);
			if (arg.startsWith("-p=") || arg.startsWith("--port")) port = Integer.parseInt(arg.split("=")[1]);
		}
		new PokemonServer();
	}

	public PokemonServer() {
		if (UpdateHandler.GUI) {
			out.setLogMethod((m) -> UpdateHandler.log(m));
		}

		Language.setup();
		Attack.init();

		out.println("Starting Modloader...");
		ModLoader.loadMods(moddir, () -> load());
		out.println("Done loading mods. Starting threads...");

		ModLoader.Status = 7;

		handler = new UpdateHandler();
	}

	private void load() {
		out.println("Core loading has begun");
		listener = new ServerListener(port);
		Pokemons.init();
		out.println("Core done loading");
		ModLoader.performEvent(new PokemonEvents.ServerStartEvent());
	}

}

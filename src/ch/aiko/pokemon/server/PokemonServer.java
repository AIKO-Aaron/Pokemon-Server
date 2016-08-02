package ch.aiko.pokemon.server;

import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.language.Language;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.util.FileUtil;
import ch.aiko.util.Log;

public class PokemonServer {

	public static Log out = new Log(PokemonServer.class);
	public static UpdateHandler handler;
	public static ServerListener listener;

	public static void main(String[] args) {
		new PokemonServer();
	}

	public PokemonServer() {
		Language.setup();

		boolean isDir = FileUtil.getRunningJar().isDirectory();

		out.println("Starting Modloader...");
		ModLoader.loadMods(out, (isDir ? FileUtil.getRunningJar().getParent() : FileUtil.getRunningJar().getAbsolutePath()) + "/mods/", () -> load());
		out.println("Done loading mods. Starting threads...");

		ModLoader.Status = 7;
		
		handler = new UpdateHandler();
	}

	private void load() {
		out.println("Core loading has begun");
		listener = new ServerListener();
		Pokemons.init();
		out.println("Core done loading");
	}

}

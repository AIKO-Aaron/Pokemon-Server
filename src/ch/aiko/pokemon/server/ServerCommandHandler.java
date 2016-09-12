package ch.aiko.pokemon.server;

import ch.aiko.engine.command.BasicCommand;
import ch.aiko.engine.graphics.Screen;

public class ServerCommandHandler {

	public ServerCommandHandler(UpdateHandler instance) {
		new BasicCommand("ups", "ups", 0, (String[] args, Screen sender) -> {
			PokemonServer.out.println("" + sender.lastUPS);
			return true;
		});

		new BasicCommand("save", "save", 1, (String[] args, Screen sender) -> {
			instance.savePlayers();
			return true;
		});

		new BasicCommand("say", "say <message>", 0, (String[] args, Screen sender) -> {
			if (args.length <= 0) return false;
			if (args[0] != null) PokemonServer.listener.sendToAll("/chat/Server: [OWNER] " + args[0]);
			return true;
		});
	}

}

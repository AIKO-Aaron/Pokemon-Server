package ch.aiko.pokemon.client;

import java.net.Socket;
import java.util.ArrayList;

import ch.aiko.pokemon.entity.player.OtherPlayer;
import ch.aiko.pokemon.pokemons.TeamPokemon;
import ch.aiko.pokemon.server.PokemonServer;

public class PokemonClient {

	public static final int PORT = 4732;

	public ArrayList<OtherPlayer> players = new ArrayList<OtherPlayer>();
	public Thread receiver, sender;
	public boolean running, sending;
	public String address;
	public String uuid;
	public String pathToLevel;
	public int x, y, dir;
	public TeamPokemon[] team = new TeamPokemon[PokemonServer.TeamSize];
	public Socket socket;
	public boolean synchrone = false, lvl = false, pos = false;
	@SuppressWarnings("unused")
	private int waitingForMods = 0;
	@SuppressWarnings("unused")
	private ArrayList<String> modNames = new ArrayList<String>();
	@SuppressWarnings("unused")
	private ArrayList<String> texts = new ArrayList<String>();
	@SuppressWarnings("unused")
	private int dataLength = 0;
	@SuppressWarnings("unused")
	private boolean receivingPlayers = false;

	public PokemonClient(String connectTo, String uuid) {}

	public void sendText(String text) {}

	public void send() {}

	public void sendBytes(byte[] bytes) {}

	@SuppressWarnings("unused")
	private void receive() {}

	@SuppressWarnings("unused")
	private void perform(String received, Socket s) {}

	public void addPlayer(OtherPlayer otpl) {}

	public void close() {}

	public String getLevel() {
		return null;
	}

	public void waitFor() {}

	public void ifNotSet(String string) {}
}

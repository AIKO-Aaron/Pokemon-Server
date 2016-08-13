package ch.aiko.pokemon.basic;

import java.net.Socket;

import ch.aiko.modloader.GameEvent;
import ch.aiko.pokemon.entity.player.PlayerMenu;
import ch.aiko.pokemon.server.ServerPlayer;

public class PokemonEvents {
	
	public static final String PLAYER_MENU_OPEN_EVENT = "playeropenmenu";
	public static final String SERVER_START = "serverstart";
	public static final String CLIENT_START = "clientstart";
	public static final String PLAYER_CONNECTED = "playerconnect";
	public static final String STRING_RECEIVED = "receiveddata";
	//TODO more events (for mods)

	public static class PlayerOpenMenuEvent extends GameEvent {
		public String eventName() {
			return PLAYER_MENU_OPEN_EVENT;
		}

		public PlayerMenu menu;

		public PlayerOpenMenuEvent(PlayerMenu m) {
			menu = m;
		}
	}
	
	public static class ServerStartEvent extends GameEvent {
		public String eventName() {
			return SERVER_START;
		}
	}
	
	public static class ClientStartEvent extends GameEvent {
		public String eventName() {
			return CLIENT_START;
		}
	}

	public static class PlayerConnectedEvent extends GameEvent {
		public String eventName() {
			return PLAYER_CONNECTED;
		}
		public ServerPlayer player;
		public PlayerConnectedEvent(ServerPlayer p) {}
	}
	
	public static class StringReceivedEvent extends GameEvent {
		public String eventName() {
			return STRING_RECEIVED;
		}
		public String received;
		public Socket socket;
		public StringReceivedEvent(String re, Socket s) {received = re;socket = s;}
	}
}

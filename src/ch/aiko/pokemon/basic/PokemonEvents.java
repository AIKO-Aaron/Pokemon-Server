package ch.aiko.pokemon.basic;

import java.net.Socket;

import ch.aiko.engine.graphics.Window;
import ch.aiko.modloader.GameEvent;
import ch.aiko.pokemon.entity.player.PlayerMenu;
import ch.aiko.pokemon.graphics.menu.Menu;
import ch.aiko.pokemon.pokemons.TeamPokemon;
import ch.aiko.pokemon.server.ServerPlayer;

public class PokemonEvents {

	public static final String PLAYER_MENU_OPEN_EVENT = "playeropenmenu";
	public static final String SERVER_START = "serverstart";
	public static final String CLIENT_START = "clientstart";
	public static final String PLAYER_CONNECTED = "playerconnect";
	public static final String STRING_RECEIVED = "receiveddata";
	public static final String POKEMON_DEFEATED = "pokemondefeat";
	public static final String WINDOW_CLOSING = "windowclosing";
	public static final String MENU_OPENING = "menuopening";
	public static final String MENU_CLOSING = "menuclosing";

	public static class PlayerOpenMenuEvent extends GameEvent {
		public String eventName() {
			return PLAYER_MENU_OPEN_EVENT;
		}

		public PlayerMenu menu;

		public PlayerOpenMenuEvent(PlayerMenu m) {
			menu = m;
		}

		public PlayerOpenMenuEvent getEvent() {
			return this;
		}
	}

	public static class ServerStartEvent extends GameEvent {
		public String eventName() {
			return SERVER_START;
		}
		public ServerStartEvent getEvent() {
			return this;
		}
	}

	public static class ClientStartEvent extends GameEvent {
		public String eventName() {
			return CLIENT_START;
		}
		public ClientStartEvent getEvent() {
			return this;
		}
	}

	public static class PlayerConnectedEvent extends GameEvent {
		public String eventName() {
			return PLAYER_CONNECTED;
		}

		public ServerPlayer player;

		public PlayerConnectedEvent(ServerPlayer p) {player = p;}
		
		public PlayerConnectedEvent getEvent() {
			return this;
		}
	}

	public static class StringReceivedEvent extends GameEvent {
		public String eventName() {
			return STRING_RECEIVED;
		}

		public String received;
		public Socket socket;

		public StringReceivedEvent(String re, Socket s) {
			received = re;
			socket = s;
		}
		
		public StringReceivedEvent getEvent() {
			return this;
		}
	}

	public static class PokemonDefeatEvent extends GameEvent {
		public String eventName() {
			return POKEMON_DEFEATED;
		}

		public TeamPokemon defeated, other;

		public PokemonDefeatEvent(TeamPokemon pok1, TeamPokemon pok2) {
			defeated = pok1;
			other = pok2;
		}
		
		public PokemonDefeatEvent getEvent() {
			return this;
		}
	}

	public static class WindowClosingEvent extends GameEvent {
		public String eventName() {
			return WINDOW_CLOSING;
		}

		public Window window;

		public WindowClosingEvent(Window window) {
			this.window = window;
		}
		
		public WindowClosingEvent getEvent() {
			return this;
		}
	}

	public static class MenuOpeningEvent extends GameEvent {
		public String eventName() {
			return MENU_OPENING;
		}

		public Menu menu;

		public MenuOpeningEvent(Menu menu) {
			this.menu = menu;
		}
		
		public MenuOpeningEvent getEvent() {
			return this;
		}
	}

	public static class MenuClosingEvent extends GameEvent {
		public String eventName() {
			return MENU_CLOSING;
		}

		public Menu menu;

		public MenuClosingEvent(Menu menu) {
			this.menu = menu;
		}
		
		public MenuClosingEvent getEvent() {
			return this;
		}
	}
}

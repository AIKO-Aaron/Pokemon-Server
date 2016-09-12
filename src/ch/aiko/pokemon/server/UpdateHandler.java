package ch.aiko.pokemon.server;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.aiko.as.ASDataBase;
import ch.aiko.as.ASObject;
import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.graphics.Updatable;
import ch.aiko.engine.graphics.Window;
import ch.aiko.pokemon.basic.MacAdapter;
import ch.aiko.pokemon.graphics.menu.Button;
import ch.aiko.pokemon.graphics.menu.Menu;
import ch.aiko.pokemon.graphics.menu.MenuObject;
import ch.aiko.pokemon.graphics.menu.TextField;
import ch.aiko.pokemon.settings.Settings;
import ch.aiko.util.FileUtil;

public class UpdateHandler extends Menu implements Renderable, Updatable {

	public ArrayList<ServerPlayer> p = new ArrayList<ServerPlayer>();

	public static final boolean GUI = true;
	private static final ArrayList<String> loggedMessages = new ArrayList<String>();
	private static TextField tf;
	private int textsize = 12;

	public void setPlayerLevel(String uuid, String level) {
		for (ServerPlayer player : p)
			if (player.uuid.equals(uuid)) player.currentLevel = level;
	}

	public void setPlayerPos(String uuid, int x, int y, int dir) {
		for (ServerPlayer player : p)
			if (player.uuid.equals(uuid)) {
				player.x = x;
				player.y = y;
				player.dir = dir;
			}
	}

	public ServerPlayer getPlayer(String uuid) {
		for (ServerPlayer player : p)
			if (player.uuid.equals(uuid)) return player;
		return null;
	}

	public void addPlayer(ServerPlayer player) {
		p.add(player);
	}

	private Screen screen = new Screen(960, 540) {
		private static final long serialVersionUID = 1L;
		private ScheduledFuture<?> save;

		public Screen startThreads() {
			ScheduledThreadPoolExecutor exe = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(4);
			update = exe.scheduleAtFixedRate(() -> preUpdate(), 0, 1000000000 / 60, TimeUnit.NANOSECONDS);
			save = exe.scheduleAtFixedRate(() -> savePlayers(), 0, 10, TimeUnit.SECONDS);
			render = exe.scheduleAtFixedRate(() -> preRender(), 1, 1, TimeUnit.NANOSECONDS);
			disp = exe.scheduleAtFixedRate(() -> {
				lastUPS = ups;
				ups = 0;
			} , 0, 1, TimeUnit.SECONDS);
			return this;
		}

		public void stopThreads() {
			PokemonServer.out.println("Shutting down the application...");
			update.cancel(true);
			save.cancel(true);
			disp.cancel(true);
			render.cancel(true);
			quit();
		};
	};

	public void quit() {
		savePlayers();
		PokemonServer.out.println("Done");
	}

	public void savePlayers() {
		ASDataBase base = new ASDataBase("PlayerData");
		for (ServerPlayer player : p) {
			if (player == null) continue;
			player.reload();
			base.addObject(player);
		}
		base.saveToFile(FileUtil.getRunningJar().getParent() + "/players.bin");
	}

	private void registerCommands() {
		new ServerCommandHandler(this);
	}

	private void loadPlayers() {
		// ASDataBase base = new ASDataBase("ServerData");
		ASDataBase base = ASDataBase.createFromFile(FileUtil.getRunningJar().getParent() + "/players.bin");
		if (base == null) base = new ASDataBase("PlayerData");

		for (int i = 0; i < base.objectCount; i++) {
			ASObject obj = base.objects.get(i);
			if (obj != null && obj.getName().equals("Player")) {
				p.add(new ServerPlayer(obj));
				PokemonServer.out.println("Found Player:" + p.get(p.size() - 1).uuid);
			}
		}
	}

	public UpdateHandler() {
		super(false);
		if (System.getProperty("os.name").contains("Mac")) {
			MacAdapter.addCloseFunction(() -> quit());
		}

		registerCommands();
		loadPlayers();
		screen.ps = PokemonServer.out;

		if (GUI) {
			parent = screen;
			screen.addLayer(this);
			Window gui = new Window("Pokemon-server", screen);
			gui.start();
		} else {
			screen.startThreads();
			screen.startCommandLineReader();
		}
	}

	public void comm(MenuObject sender) {
		if(tf.getText().replace(" ", "").equals("")) return;
		screen.executeCommand(tf.getText(), 4);
		tf.setText("");
	}

	public static void log(String m) {
		loggedMessages.add(m);
	}

	public void onOpen() {
		addLayer((tf = new TextField(10, screen.getFrameHeight() - 3 * textsize, screen.getFrameWidth() - 200, 2 * textsize, "", (s) -> comm(s))));
		addButton(new Button(screen.getFrameWidth() - 180, screen.getFrameHeight() - 3 * textsize, 170, 2 * textsize, "Send", (s) -> comm(s)).setType(Button.RECT_BUTTON), 0, 0);
		tf.setOrientation(TextField.LEFT);
	}

	public void onClose() {}

	public void renderMenu(Renderer r) {
		for (int i = 0; i < loggedMessages.size() && i < 50; i++) {
			r.drawText(loggedMessages.get(loggedMessages.size() - 1 - i), Settings.font, textsize, 0, 0, r.getHeight() - i * textsize - 5 * textsize, 0xFFFFFFFF);
		}
	}

	public void updateMenu(Screen s, Layer l) {}

	public String getName() {
		return "Console";
	}

}

package ch.aiko.pokemon.server;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.aiko.as.ASDataBase;
import ch.aiko.as.ASObject;
import ch.aiko.engine.command.BasicCommand;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.util.FileUtil;

public class UpdateHandler {

	public ArrayList<Player> p = new ArrayList<Player>();

	public Player getPlayer(String uuid) {
		for (Player player : p)
			if (player.uuid.equals(uuid)) return player;
		return null;
	}

	public void addPlayer(Player player) {
		p.add(player);
	}

	private Screen screen = new Screen(960, 540) {
		private static final long serialVersionUID = 1L;

		public Screen startThreads() {
			ScheduledThreadPoolExecutor exe = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
			update = exe.scheduleAtFixedRate(() -> preUpdate(), 0, 1000000000 / 60, TimeUnit.NANOSECONDS);
			disp = exe.scheduleAtFixedRate(() -> {
				lastUPS = ups;
				ups = 0;
			} , 0, 1, TimeUnit.SECONDS);
			return this;
		}

		public void stopThreads() {
			savePlayers();
			update.cancel(false);
			disp.cancel(true);
		};
	};

	private void savePlayers() {
		ASDataBase base = new ASDataBase("PlayerData");
		for (Player player : p) {
			base.addObject(player);
		}
		base.saveToFile(FileUtil.getRunningJar().getParent() + "/test.bin");
	}

	private void registerCommands() {
		new BasicCommand("ups", "ups", 0, (String[] args, Screen sender) -> {
			PokemonServer.out.println("" + sender.lastUPS);
			return true;
		});
	}

	private void loadPlayers() {
		// ASDataBase base = new ASDataBase("ServerData");
		ASDataBase base = ASDataBase.createFromFile(FileUtil.getRunningJar().getParent() + "/test.bin");
		if (base == null) base = new ASDataBase("PlayerData");

		for (int i = 0; i < base.objectCount; i++) {
			ASObject obj = base.objects.get(i);
			if (obj != null && obj.getName().equals("Player")) {
				p.add(new Player(obj));
			}
		}
	}

	public UpdateHandler() {
		registerCommands();
		loadPlayers();
		screen.ps = PokemonServer.out;
		screen.startThreads();
		screen.startCommandLineReader();
	}

}

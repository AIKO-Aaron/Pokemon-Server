package ch.aiko.pokemon.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.aiko.engine.graphics.Screen;

public class UpdateHandler {

	private Screen screen = new Screen(960, 540) {
		private static final long serialVersionUID = 1L;
		public Screen startThreads() {
			ScheduledThreadPoolExecutor exe = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
			update = exe.scheduleAtFixedRate(() -> preUpdate(), 0, 1000000000 / 60, TimeUnit.NANOSECONDS);
			disp = exe.scheduleAtFixedRate(() -> {
				lastUPS = ups;
				ups = 0;
			}, 0, 1, TimeUnit.SECONDS);
			new Thread(()->{
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				while(isVisible()) {
					try {
						String line = reader.readLine();
						if(line == null || line.trim().replace(" ", "").equalsIgnoreCase("")) continue;
						executeCommand(line);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			return this;
		}
		
		public void stopThreads() {
			update.cancel(false);
			disp.cancel(true);
		};
	};
	
	private void executeCommand(String line) {
		if(line.equalsIgnoreCase("ups")) {
			System.out.println(screen.lastUPS);
		} else if(line.equalsIgnoreCase("stop")) {
			screen.stopThreads();
			System.exit(0);
		}
	}
	
	public UpdateHandler() {
		screen.ps = PokemonServer.out;
		screen.startThreads();
	}
	
}

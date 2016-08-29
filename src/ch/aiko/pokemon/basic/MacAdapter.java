package ch.aiko.pokemon.basic;

import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.AppEvent.QuitEvent;

public class MacAdapter {

	public static void addCloseFunction(Runnable quit) {
		Application.getApplication().setQuitHandler(new QuitHandler() {
			public void handleQuitRequestWith(QuitEvent arg0, QuitResponse qr) {
				quit.run();
				qr.performQuit();
			}
		});
	}
	
}

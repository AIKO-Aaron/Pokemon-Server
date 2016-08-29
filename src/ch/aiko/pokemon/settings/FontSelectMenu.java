package ch.aiko.pokemon.settings;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.graphics.menu.Menu;

public class FontSelectMenu extends Menu {

	public FontSelectMenu(Screen parent) {
		super(parent);
	}

	public void onOpen() {}

	public void onClose() {}

	public void renderMenu(Renderer r) {
		r.fillRect(0, 0, parent.getWidth(), parent.getHeight(), 0xFFFF00FF);
	}

	public void updateMenu(Screen s, Layer l) {}

	public String getName() {
		return "FontSelector";
	}
	
	public boolean stopsRendering() {
		return true;
	}
	
	public boolean stopsUpdating() {
		return true;
	}

}

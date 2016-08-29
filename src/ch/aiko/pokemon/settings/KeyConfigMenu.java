package ch.aiko.pokemon.settings;

import java.util.ArrayList;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.graphics.menu.Button;
import ch.aiko.pokemon.graphics.menu.Menu;

public class KeyConfigMenu extends Menu {

	private ArrayList<KeyConfig> configs = new ArrayList<KeyConfig>();

	public KeyConfigMenu(Screen parent) {
		super(parent);

		configs.add((KeyConfig) addLayer(new KeyConfig(0, 0, 100, 50, "keyA")));
		configs.add((KeyConfig) addLayer(new KeyConfig(100, 0, 100, 50, "keyMenu")));
		configs.add((KeyConfig) addLayer(new KeyConfig(200, 100, 100, 50, "keyUp")));
		configs.add((KeyConfig) addLayer(new KeyConfig(300, 50, 100, 50, "keyLeft")));
		configs.add((KeyConfig) addLayer(new KeyConfig(300, 150, 100, 50, "keyRight")));
		configs.add((KeyConfig) addLayer(new KeyConfig(400, 100, 100, 50, "keyDown")));
		addButton(new Button(parent.getWidth() / 2 - 125, parent.getHeight() - 100, 250, 50, "Save", (b) -> save()), 0, index);

	}

	private void save() {
		
	}

	public void onOpen() {}

	public void onClose() {}

	public void renderMenu(Renderer r) {
		r.fillRect(0, 0, parent.getWidth(), parent.getHeight(), 0xFF00FFFF);
	}

	public void updateMenu(Screen s, Layer l) {}

	public String getName() {
		return "KeyConfig";
	}

}

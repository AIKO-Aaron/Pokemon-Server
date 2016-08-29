package ch.aiko.pokemon.settings;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.graphics.menu.Button;
import ch.aiko.pokemon.graphics.menu.ButtonGroupMenu;

public class SettingsPanel extends ButtonGroupMenu {

	public final int WIDTH = 350;

	public SettingsPanel(Screen parent) {
		super(parent);

		addButton(new Button(0, 0, WIDTH, -1, "Back", (b) -> closeMe()));
		addButton(new Button(0, 0, WIDTH, -1, "Test", (b) -> closeMe()));
		addButton(new Button(0, 0, WIDTH, -1, "Also back", (b) -> closeMe()));
		addButton(new Button(0, 0, WIDTH, -1, "Fonts", (b) -> level.openMenu(new FontSelectMenu(parent))));
		addButton(new Button(0, 0, WIDTH, -1, "keys", (b) -> level.openMenu(new KeyConfigMenu(parent))));
	}

	public void onOpen() {}

	public void onClose() {}

	public void renderMenu(Renderer r) {}

	public void updateMenu(Screen s, Layer l) {}

	public String getName() {
		return "Settings";
	}

}

package ch.aiko.pokemon.entity.player;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.graphics.menu.Menu;

public class PlayerSetterMenu extends Menu {

	public PlayerSetterMenu(Screen parent) {
		super(parent);
	}

	public void onOpen() {}

	public void onClose() {}

	public void renderMenu(Renderer r) {}

	public void updateMenu(Screen s, Layer l) {}

	public String getName() {
		return "PlayerSetter";
	}

	public void done1() {}

	public void chooseGender(int g) {}

	public void done2() {}

	public void chooseName(String name) {}

	public void done3() {}

}

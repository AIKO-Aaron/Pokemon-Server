package ch.aiko.pokemon.entity.player;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.graphics.menu.Button;
import ch.aiko.pokemon.graphics.menu.ButtonGroupMenu;
import ch.aiko.pokemon.graphics.menu.ButtonMenuTest;
import ch.aiko.pokemon.graphics.menu.RaveMenu;

public class PlayerMenu extends ButtonGroupMenu {

	// private String[] texts = new String[] { "Pokedex", "Pokemon", "Beutel", "what?", "rave!" };

	public final int WIDTH = 350;

	public PlayerMenu(Screen parent) {
		super(parent);

		addButton(new Button(parent.getWidth() - WIDTH, 0, WIDTH, 0, "Pokedex", (b) -> level.openMenu(new ButtonMenuTest(parent))));
		addButton(new Button(parent.getWidth() - WIDTH, 0, WIDTH, 0, "Pokemon", (b) -> {}));
		addButton(new Button(parent.getWidth() - WIDTH, 0, WIDTH, 0, "Beutel", (b) -> {}));
		addButton(new Button(parent.getWidth() - WIDTH, 0, WIDTH, 0, "What?", (b) -> {}));
		addButton(new Button(parent.getWidth() - WIDTH, 0, WIDTH, 0, "Rave", (b) -> level.openMenu(new RaveMenu(parent))));
	}

	@Override
	public String getName() {
		return "MainMenu";
	}

	@Override
	public void renderMenu(Renderer r) {}

	@Override
	public void updateMenu(Screen s, Layer l) {}

	@Override
	public void onOpen() {}

	@Override
	public void onClose() {}

	public Screen getScreen() {
		return parent;
	}

}

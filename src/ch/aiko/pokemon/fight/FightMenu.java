package ch.aiko.pokemon.fight;

import java.awt.event.KeyEvent;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.graphics.menu.Button;
import ch.aiko.pokemon.graphics.menu.Menu;
import ch.aiko.pokemon.pokemons.TeamPokemon;

public class FightMenu extends Menu {

	final int width = 250;

	public FightMenu(Screen parent) {
		super(parent);
		x_for_close = false;
		reopen();
	}
	
	public void reopen() {
		int xc = parent.getFrameWidth() - width;
		int yc = parent.getFrameHeight() - 100;

		addButton(new Button(xc - width, yc - 100, width, 100, "Fight", (b) -> openMoves((Button) b)), 1, 0);
		addButton(new Button(xc, yc - 100, width, 100, "Pokemon", (b) -> buttonPressed((Button) b)), 1, 2);
		addButton(new Button(xc - width, yc, width, 100, "Item", (b) -> buttonPressed((Button) b)), 1, 1);
		addButton(new Button(xc, yc, width, 100, "Run", (b) -> buttonPressed((Button) b)), 1, 3);
	}

	public void openMoves(Button sender) {
		removeAllButtons();
		int xc = parent.getFrameWidth() - width;
		int yc = parent.getFrameHeight() - 100;
		TeamPokemon owned = ((Fight) (parent.getTopLayer("Fight"))).pok1;
		for (int i = 0; i < owned.getMoveSet().length; i++) {
			addButton(new Button(xc - (i % 2) * width, yc - (i / 2) * 100, width, 100, owned.getMoveSet()[i].attackName, (b) -> attack((Button) b)), 1, i);
		}
	}
	
	public void attack(Button b) {
		removeAllButtons();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		reopen();
	}

	public void buttonPressed(Button sender) {
		removeAllButtons();
		addButton(new Button(0, 0, 350, 100, sender.getText(), (b) -> buttonPressed((Button) b)), 1, 0);
	}

	@Override
	public void onOpen() {
		xOffset = 2 * width;
	}

	@Override
	public void onClose() {}

	@Override
	public void renderMenu(Renderer r) {

	}

	@Override
	public void updateMenu(Screen s, Layer l) {
		if (popKeyPressed(KeyEvent.VK_RIGHT) || popKeyPressed(KeyEvent.VK_LEFT)) index = (index + 2) % buttons.size();

		if (xOffset > 0) xOffset -= 10;
	}

	@Override
	public String getName() {
		return "FightMenu";
	}

	@Override
	public boolean stopsUpdating() {
		return false;
	}

}

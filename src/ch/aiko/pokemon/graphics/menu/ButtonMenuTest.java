package ch.aiko.pokemon.graphics.menu;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.server.PokemonServer;

public class ButtonMenuTest extends Menu {

	private int count = 20;

	public ButtonMenuTest(Screen parent) {
		super(parent);
	}

	@Override
	public void onOpen() {		
		float bs = parent.getFrameHeight() / count;
		for (float i = 0; i < count; i++)
			addButton(new Button(parent.getFrameWidth() - 350 - 350, (int) (bs * i), 350, (int) bs, "Index: " + (i + 1), (sender) -> {
				PokemonServer.out.println(((Button) sender).getText());
			}), 1, (int) i);
	}

	@Override
	public void onClose() {}

	@Override
	public void renderMenu(Renderer r) {}

	@Override
	public void updateMenu(Screen s, Layer l) {}

	@Override
	public boolean stopsUpdating() {
		return true;
	}

	@Override
	public String getName() {
		return "TestMenuForButtons";
	}

}

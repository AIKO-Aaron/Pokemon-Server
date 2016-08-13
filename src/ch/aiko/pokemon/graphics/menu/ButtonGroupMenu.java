package ch.aiko.pokemon.graphics.menu;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Screen;

public abstract class ButtonGroupMenu extends Menu {

	protected int index;

	public ButtonGroupMenu(Screen parent) {
		super(parent);
	}

	public void addButton(Button b) {
		addButton(b, 0, index++);
		double bs = (double) parent.getHeight() / (double) buttons.size();
		for (int i = 0; i < buttons.size(); i++) {
			Layer l = buttons.get(i);
			Button button = (Button) l;
			button.setHeight((int) bs);
			button.setY((int) (bs * i));
		}
	}

}

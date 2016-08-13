package ch.aiko.pokemon.graphics.menu;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.LayerContainer;
import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.graphics.Updatable;
import ch.aiko.pokemon.entity.player.Player;
import ch.aiko.pokemon.fight.Fight;
import ch.aiko.pokemon.level.Level;

public abstract class Menu extends LayerContainer implements Renderable, Updatable {

	protected int xOffset, yOffset;
	protected Screen parent;
	protected Player holder;
	protected Level level;
	protected Fight fight;
	protected int index, mouseSel;
	protected boolean x_for_close = true;
	protected boolean manage_buttons = true;

	protected ArrayList<Layer> buttons = new ArrayList<Layer>();

	public Menu(Screen parent) {
		resetOffset = false;
		this.parent = parent;
		this.level = (Level) parent.getTopLayer("Level");
		this.fight = (Fight) parent.getTopLayer("Fight");
		this.holder = (Player) (level).getTopLayer("Player");
	}

	/**
	 * Gets the level of the current layer. The player gets rendered on layer 1 so yeah... probably over 1
	 */
	@Override
	public int getLevel() {
		return Player.PLAYER_RENDERED_LAYER + 1;
	}

	@Override
	public boolean stopsRendering() {
		return false;
	}

	@Override
	public boolean stopsUpdating() {
		return true;
	}

	@Override
	public Renderable getRenderable() {
		return this;
	}

	@Override
	public Updatable getUpdatable() {
		return this;
	}

	public void addButton(Button b, int layer, int index) {
		b.setNeedsUpdates(false);
		while (buttons.size() <= index) {
			buttons.add(null);
		}
		buttons.set(index, addLayer(b.setLayer(layer)));
	}

	public Button getButton(int index) {
		return (Button) buttons.get(index).getRenderable();
	}

	public void removeButton(Button b) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).getRenderable() == b) {
				if (index > i) index--;
				removeLayer(buttons.get(i));
				buttons.remove(i);
				i--;
			}
		}
	}

	public void removeAllButtons() {
		for (Layer b : buttons)
			removeLayer(b);
		buttons.clear();
		index = 0;
	}

	private int x, y;

	@Override
	public final void layerRender(Renderer r) {
		x = r.getXOffset();
		y = r.getYOffset();
		r.setOffset(xOffset, yOffset);
		renderMenu(r);
	}

	@Override
	public final void postRender(Renderer r) {
		r.setOffset(x, y);
	}

	@Override
	public final void layerUpdate(Screen s, Layer l) {
		if (x_for_close && popKeyPressed(KeyEvent.VK_X)) closeMe();

		if (manage_buttons && buttons.size() > 0) {
			Point pos = s.getMousePosition();
			int mx = pos == null ? getMouseXInFrame(s) : pos.x;
			int my = pos == null ? getMouseYInFrame(s) : pos.y;

			if (popMouseKey(MouseEvent.BUTTON1)) if (((Button) buttons.get(index)).isInside(mx, my)) ((Button) buttons.get(index)).buttonPressed();

			if (popKeyPressed(KeyEvent.VK_SPACE)) ((Button) buttons.get(index)).buttonPressed();

			for (int i = 0; i < buttons.size(); i++)
				if (i != mouseSel && ((Button) buttons.get(i)).isInside(mx, my)) mouseSel = index = i;
			
			if (popKeyPressed(KeyEvent.VK_DOWN)) index = (index + 1) % buttons.size();
			if (popKeyPressed(KeyEvent.VK_UP)) index = index > 0 ? (index - 1) : buttons.size() - 1;

			for (int i = 0; i < buttons.size(); i++)
				((Button) buttons.get(i)).setSelected(i == index);
		}

		updateMenu(s, l);
	}

	public void closeMe() {
		if (level != null) level.closeMenu(this);
		if (fight != null) fight.closeMenu(this);
	}

	@Override
	public abstract void onOpen();

	@Override
	public abstract void onClose();

	public abstract void renderMenu(Renderer r);

	public abstract void updateMenu(Screen s, Layer l);

}

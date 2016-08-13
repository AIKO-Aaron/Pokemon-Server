package ch.aiko.pokemon.graphics.menu;

import java.util.ArrayList;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;

public class TextBox extends MenuObject {

	public static final int THICKNESS = 5;

	@SuppressWarnings("unused")
	private String __text;
	protected int x, y, w, h;
	protected ArrayList<String> lines;
	protected int index;
	protected MenuObjectAction action;
	protected boolean stopsUpdates;

	public TextBox(String text) {
		x = 0;
		this.__text = text;
	}

	public TextBox(String text, MenuObjectAction ac) {
		x = 0;
		this.__text = text;
		this.action = ac;
	}

	public TextBox(String text, MenuObjectAction ac, boolean su) {
		x = 0;
		this.__text = text;
		this.action = ac;
		this.stopsUpdates = su;
	}

	public TextBox setOnCloseAction(MenuObjectAction ac) {
		action = ac;
		return this;
	}

	@Override
	public void onOpen(Screen s) {
		super.onOpen(s);
		x = -s.getRenderer().getXOffset();
		w = s.getFrameWidth();
		h = 100;
		y = s.getFrameHeight() - h - s.getRenderer().getYOffset();
	}

	@Override
	public void update(Screen s, Layer l) {}

	@Override
	public void render(Renderer renderer) {}

	@Override
	public String getName() {
		return "TextBox";
	}

	public boolean stopsUpdating() {
		return stopsUpdates;
	}

}

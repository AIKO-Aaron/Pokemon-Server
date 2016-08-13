package ch.aiko.pokemon.graphics.menu;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;

public class Button extends MenuObject {

	public static final int THICKNESS = 3;

	protected int layer;
	protected int x, y, w, h;
	protected String text;
	protected boolean selected = false, nu = true;
	protected MenuObjectAction r = (MenuObject b) -> {};
	protected int lastX, lastY;

	public Button() {
		x = y = w = h = 0;
		text = "";
	}

	public Button(int x, int y, int w, int h, String text) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.text = text;
	}

	public Button(int x, int y, int w, int h, String text, MenuObjectAction r) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.text = text;
		this.r = r;
	}

	public Button setX(int x) {
		this.x = x;
		return this;
	}

	public Button setY(int y) {
		this.y = y;
		return this;
	}

	public Button setWidth(int w) {
		this.w = w;
		return this;
	}

	public Button setHeight(int h) {
		this.h = h;
		return this;
	}

	public Button setText(String text) {
		this.text = text;
		return this;
	}

	public Button setSelected(boolean sel) {
		selected = sel;
		return this;
	}

	public Button setAction(MenuObjectAction r) {
		this.r = r;
		return this;
	}

	public boolean isInside(int mx, int my) {
		return mx >= x && mx <= x + w && my >= y && my <= y + h;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public String getText() {
		return text;
	}

	public boolean isSelected() {
		return selected;
	}

	public MenuObjectAction getAction() {
		return r;
	}

	@Override
	public void update(Screen screen, Layer l) {}

	@Override
	public void render(Renderer renderer) {}

	public void buttonPressed() {
		r.actionPerformed(this);
	}

	public Layer setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	public void setNeedsUpdates(boolean b) {
		nu = b;
	}

}

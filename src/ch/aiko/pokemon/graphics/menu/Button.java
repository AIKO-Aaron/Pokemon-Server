package ch.aiko.pokemon.graphics.menu;

import java.awt.Font;
import java.awt.event.MouseEvent;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.settings.Settings;

public class Button extends MenuObject {

	public static final int THICKNESS = 3;

	public static final int SHRINKING = 2;

	protected int layer;
	protected int x, y, w, h;
	protected String text;
	protected boolean selected = false, nu = true;
	protected MenuObjectAction r = (MenuObject b) -> {};
	protected int lastX, lastY;
	protected int textsize = 0;
	protected int button_type = 0;
	protected boolean pressed = false;

	public static final int ROUND_BUTTON = 0;
	public static final int RECT_BUTTON = 1;

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
	public void update(Screen screen, Layer l) {
		if (!nu) return;
		int xx = getMouseXInFrame(screen);
		int yy = getMouseYInFrame(screen);
		if (xx > x && xx < x + w && yy > y && yy < y + h) {
			if (xx != lastX || yy != lastY) selected = true;
			if (input.isMouseKeyPressed(MouseEvent.BUTTON1)) {
				setPressed();
			} else if (pressed) {
				buttonPressed();
			}
		} else if (input.isMouseKeyPressed(MouseEvent.BUTTON1)) {
			pressed = false;
			selected = false;
		} else selected = false;
		lastX = xx;
		lastY = yy;
	}

	@Override
	public void render(Renderer renderer) {
		if (button_type == RECT_BUTTON) {
			renderer.drawRect(x + (pressed ? SHRINKING : 0), y + (pressed ? SHRINKING : 0), w - (pressed ? 2 * SHRINKING : 0), h - (pressed ? 2 * SHRINKING : 0), selected ? 0xFFFF00FF : 0xFF000000, THICKNESS);
			renderer.fillRect(x + (pressed ? SHRINKING : 0) + THICKNESS, y + (pressed ? SHRINKING : 0) + THICKNESS, w - 2 * THICKNESS - 1 - (pressed ? 2 * SHRINKING : 0), h - 2 * THICKNESS - 1 - (pressed ? 2 * SHRINKING : 0), 0xFFFFFFFF);
		} else if (button_type == ROUND_BUTTON) {
			renderer.fillOval(x + (pressed ? SHRINKING : 0), y + (pressed ? SHRINKING : 0), w - (pressed ? 2 * SHRINKING : 0), h - (pressed ? 2 * SHRINKING : 0), selected ? 0xFFFF00FF : 0xFF000000);
			renderer.drawOval(x + (pressed ? SHRINKING : 0), y + (pressed ? SHRINKING : 0), w - (pressed ? 2 * SHRINKING : 0), h - (pressed ? 2 * SHRINKING : 0), selected ? 0xFFFF00FF : 0xFFFFFFFF, 5);
		}
		int xstart = x + (w - getStringWidth(renderer.getScreen(), new Font(Settings.font, 0, textsize), text)) / 2;
		while (xstart < x + 20 + (pressed ? 2 * SHRINKING : 0)) {
			textsize--;
			xstart = x + (w - getStringWidth(renderer.getScreen(), new Font(Settings.font, 0, textsize), text)) / 2;
		}
		int ystart = y + (h - getStringHeight(renderer.getScreen(), new Font(Settings.font, 0, textsize))) / 3;
		renderer.drawText(text, Settings.font, textsize, 0, xstart, ystart, selected ? 0xFFFF00FF : 0xFFFFFFFF);
	}

	public void buttonPressed() {
		pressed = false;
		r.actionPerformed(this);
	}

	public Layer setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	public Button setNeedsUpdates(boolean b) {
		nu = b;
		return this;
	}

	public Button setType(int typ) {
		button_type = typ;
		return this;
	}

	public void setPressed() {
		pressed = true;
	}

}

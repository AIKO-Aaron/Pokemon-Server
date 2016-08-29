package ch.aiko.pokemon.settings;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.input.Input;
import ch.aiko.pokemon.graphics.menu.MenuObject;

public class KeyConfig extends MenuObject implements KeyListener {

	private static final int THICKNESS = 3;

	public static final ArrayList<Integer> blackList = new ArrayList<Integer>(Arrays.asList(new Integer[] { KeyEvent.VK_ENTER, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_SHIFT, KeyEvent.VK_ALT, KeyEvent.VK_META, KeyEvent.VK_CONTROL, KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_ESCAPE, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP, KeyEvent.VK_END, KeyEvent.VK_BEGIN, KeyEvent.VK_DELETE, KeyEvent.VK_FINAL }));

	protected int x, y, w, h;
	protected boolean selected = false;
	protected String key;
	protected String label;
	protected int keyCode;

	public KeyConfig(int x, int y, int w, int h, String kc) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		label = kc;
		keyCode = Settings.getInteger(kc);
		key = "" + (char) keyCode;
	}

	@Override
	public void update(Screen screen, Layer l) {
		if (input.popMouseKey(MouseEvent.BUTTON1)) {
			int xx = getMouseXInFrame(screen);
			int yy = getMouseYInFrame(screen);
			if (xx > x && xx < x + w && yy > y && yy < y + h) {
				selected = true;
			} else {
				selected = false;
			}
		}
	}

	@Override
	public void setInput(Input input) {
		this.input = input;
		input.screen.addKeyListener(this);
	}

	@Override
	public void onClose() {
		input.screen.removeKeyListener(this);
	}

	@Override
	public void render(Renderer renderer) {
		renderer.drawRect(x, y, w, h, selected ? 0xFFFF00FF : 0xFF000000, THICKNESS);
		renderer.fillRect(x + THICKNESS, y + THICKNESS, w - 2 * THICKNESS - 1, h - 2 * THICKNESS - 1, 0xFFFFFFFF);
		if (key == null || key.equalsIgnoreCase("")) return;
		renderer.drawText(label, Settings.font, h / 3, 0, x + w / 2 - 30, y + h / 2 - 20, 0xFF000000);
		renderer.drawText(keyCode + ":" + key, Settings.font, h / 2, 0, x + w / 2 - 30, y + h / 2 - 10, 0xFF000000);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!blackList.contains(keyCode) && selected) {
			key = "" + e.getKeyChar();
			this.keyCode = e.getKeyCode();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}

package ch.aiko.pokemon.entity;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.level.Level;

public class Teleporter extends Entity {

	@SuppressWarnings("unused")
	private Level level;
	@SuppressWarnings("unused")
	private int dx, dy;

	public Teleporter(int x, int y, Level dest, int destX, int destY) {
		super(x, y);
		level = dest;
		dx = destX;
		dy = destY;
	}

	public Teleporter(int x, int y, Level dest, int destX, int destY, int w, int h) {
		super(x, y);
		this.w = w;
		this.h = h;
		level = dest;
		dx = destX;
		dy = destY;
	}

	public void teleportPlayer(Screen s) {}

	@Override
	public void render(Renderer renderer) {}

	@Override
	public void update(Screen s, Layer layer) {}

}

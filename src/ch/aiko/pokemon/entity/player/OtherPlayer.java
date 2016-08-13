package ch.aiko.pokemon.entity.player;

import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.sprite.SpriteSheet;
import ch.aiko.pokemon.level.Level;

public class OtherPlayer extends Player {

	public String uuid = "0000-0000-0000-0000-0000";
	public int lx, ly;

	public OtherPlayer(String data) {
		String[] da = data.split("/");
		xPos = Integer.parseInt(da[0]);
		yPos = Integer.parseInt(da[1]);
		dir = Integer.parseInt(da[2]);
		uuid = da[3];
	}

	public OtherPlayer(int x, int y, int dir) {
		xPos = x;
		yPos = y;
		this.dir = dir;
	}

	public void add(Level l) {
		sprites = new SpriteSheet("/ch/aiko/pokemon/textures/player/player_boy.png", 32, 32).removeColor(0xFF88B8B0);
		for (int i = 0; i < 4 * 4; i++) {
			walkingAnims[i] = sprites.getSprite(i, false);
		}
		l.addEntity(this);
	}

	public void update(Screen screen) {
		if (walking) {
			anim++;
			if (anim > 15) {
				anim = 0;
				curAnim++;
				curAnim %= 4;
			}
		}
		lx = xPos;
		ly = yPos;
	}

	public void render(Renderer renderer) {
		sprite = walkingAnims[dir * 4 + curAnim];
		if (sprite != null) renderer.drawSprite(sprite, xPos + renderer.getWidth() / 2, yPos + renderer.getHeight() / 2);
	}

	public OtherPlayer setX(int x) {
		this.xPos = x;
		return this;
	}

	public OtherPlayer setY(int y) {
		this.yPos = y;
		return this;
	}

	public OtherPlayer setUUID(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public void setWalking(boolean b) {
		walking = b;
	}
	
	public String getName() {
		return "OtherPlayer";
	}
}

package ch.aiko.pokemon.entity.player;

import ch.aiko.as.ASDataBase;
import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSheet;
import ch.aiko.pokemon.entity.Direction;
import ch.aiko.pokemon.entity.Entity;
import ch.aiko.pokemon.entity.trainer.Trainer;
import ch.aiko.pokemon.level.Level;
import ch.aiko.pokemon.pokemons.TeamPokemon;
import ch.aiko.pokemon.server.PokemonServer;
import ch.aiko.util.FileUtil;

public class Player extends Entity {

	protected int xoff, yoff;
	protected int speed = 6;
	protected SpriteSheet sprites;
	protected int dir = 0; // down, up, left, right
	protected int playerLayer = 0;

	protected Sprite[] walkingAnims = new Sprite[4 * 4];
	protected int anim = 0, curAnim = 0, send = 0;
	protected boolean walking = false;
	public boolean isPaused = false;
	public TeamPokemon[] team = new TeamPokemon[PokemonServer.TeamSize];

	public static final boolean CAN_WALK_SIDEWAYS = true;

	public static final int PLAYER_RENDERED_LAYER = 10;

	public int getWidth() {
		return sprite.getWidth();
	}

	public int getHeight() {
		return sprite.getHeight();
	}

	@Override
	public int getX() {
		return xPos + xoff;
	}

	@Override
	public int getY() {
		return yPos + yoff;
	}

	public int getRealX() {
		return xPos;
	}

	public int getRealY() {
		return yPos;
	}

	@Override
	public void setPosition(int x, int y) {
		xPos = x;
		yPos = y;
	}

	public void setPositionInLevel(int x, int y) {
		xPos = x - xoff;
		yPos = y - yoff;
	}

	protected Player() {}

	public Player(int x, int y) {
		sprites = new SpriteSheet("/ch/aiko/pokemon/textures/player/player_boy.png", 32, 32).removeColor(0xFF88B8B0);
		for (int i = 0; i < 4 * 4; i++) {
			walkingAnims[i] = sprites.getSprite(i, false);
		}
		xPos = x;
		yPos = y;
	}

	public boolean isTeamEmpty() {
		if (team == null) return true;
		for (TeamPokemon pom : team)
			if (pom != null) return false;
		return true;
	}

	public void save() {
		System.out.println("Saving");
		try {
			ASDataBase base = new ASDataBase("Team");
			for (TeamPokemon pok : team) {
				if (pok != null) {
					pok.reload();
					base.addObject(pok);
				}
			}
			base.saveToFile(FileUtil.getRunningJar().getParent() + "/player.bin");
		} catch (Throwable t) {
			t.printStackTrace(PokemonServer.out);
		}
	}

	@Override
	public void render(Renderer renderer) {}

	public boolean collides(Level level, int xoff, int yoff) {
		for (int i = 0; i < sprite.getWidth(); i++) {
			for (int j = 0; j < sprite.getHeight(); j++) {
				if (level.isSolid(xPos + xoff + i, yPos + yoff + j, playerLayer)) { return true; }
			}
		}
		return false;
	}

	public boolean isInside(int xx, int yy) {
		int width = sprite == null ? 32 : sprite.getWidth();
		int height = sprite == null ? 32 : sprite.getHeight();
		return xx > getX() && xx < getX() + width && yy > getY() && yy < getY() + height;
	}

	@Override
	public void update(Screen screen, Layer l) {}

	public void startBattle(Screen screen, Trainer t) {}

	public boolean isMoving() {
		return walking;
	}

	public void setPaused(boolean b) {
		isPaused = b;
	}

	public void setDirection(int dir2) {
		dir = dir2;
	}

	public void setDirection(Direction dir2) {
		dir = dir2.getDir();
	}

	public int getDirection() {
		return dir;
	}

	@Override
	public String getName() {
		return "Player";
	}

	@Override
	public int getLevel() {
		return PLAYER_RENDERED_LAYER;
	}

}

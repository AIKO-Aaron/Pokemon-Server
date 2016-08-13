package ch.aiko.pokemon.entity.trainer;

import java.util.Random;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSheet;
import ch.aiko.pokemon.entity.Direction;
import ch.aiko.pokemon.entity.Entity;
import ch.aiko.pokemon.entity.player.Player;
import ch.aiko.pokemon.level.Level;
import ch.aiko.pokemon.pokemons.TeamPokemon;

public class Trainer extends Entity {

	public TeamPokemon[] team = new TeamPokemon[6];
	public String name, battletext, inbattletext, wintext, losttext;
	public boolean inbattle;
	public int dir, sight;
	public int spinFunc;
	public int spinTime;
	private int spinTimer = 0;
	private int xdistance;
	private int ydistance;
	private boolean walkingToPlayer;

	protected SpriteSheet sprites;
	protected Sprite[] walkingAnims = new Sprite[4 * 4];
	protected int anim, curAnim;

	private static final int speed = 3;

	public Trainer() {
		sprites = new SpriteSheet("/ch/aiko/pokemon/textures/player/player_boy.png", 32, 32).removeColor(0xFF88B8B0);
		for (int i = 0; i < 4 * 4; i++) {
			walkingAnims[i] = sprites.getSprite(i, false);
		}
	}

	@Override
	public void render(Renderer renderer) {
		sprite = walkingAnims[dir * 4 + anim];
		renderer.drawSprite(sprite, xPos, yPos);
		renderer.drawRect(xPos + (dir == 2 ? -xdistance : dir == 3 ? sprite.getWidth() : 0), yPos + (dir == 1 ? -ydistance : dir == 0 ? sprite.getHeight() : 0), dir == 2 || dir == 3 ? xdistance : sprite.getWidth(), dir == 0 || dir == 1 ? ydistance : sprite.getHeight(), 0xFFFF00FF);
	}

	@Override
	public void update(Screen s, Layer l) {
		Level level = (Level) s.getTopLayer("Level");
		Player holder = (Player) (level).getTopLayer("Player");
		if (holder == null) return;
		if (walkingToPlayer) {
			int x = holder.getX() - (dir == 3 ? holder.getWidth() : 0);
			int y = holder.getY() - (dir == 0 ? holder.getHeight() : 0);
			if ((xPos <= x && dir == 2) || (xPos >= x && dir == 3) || (yPos >= y && dir == 0) || (yPos <= y && dir == 1)) {
				holder.setDirection(Direction.getDirection(dir).getOpposite());
				holder.startBattle(s, this);
			}
			xPos += dir == 2 ? (Math.abs(x - xPos) < speed ? x - xPos : -speed) : dir == 3 ? (Math.abs(x - xPos) < speed ? x - xPos : speed) : x - xPos;
			yPos += dir == 0 ? (Math.abs(y - yPos) < speed ? y - yPos : speed) : dir == 1 ? (Math.abs(y - yPos) < speed ? y - yPos : -speed) : y - yPos;
			if (curAnim >= 10) {
				anim++;
				anim %= 4;
				curAnim = 0;
			}
			curAnim++;
			return;
		}

		if (holder != null) {
			int x = holder.getX();
			int y = holder.getY();
			int w = holder.getWidth();
			int h = holder.getHeight();
			xdistance = sight * level.fieldWidth;
			ydistance = sight * level.fieldHeight;
			if (dir == 0) {
				int yOff = 0;
				while (!level.isSolid(xPos + sprite.getWidth() / 2, yPos + yOff + sprite.getHeight(), 0) && yOff < ydistance)
					yOff++;
				ydistance = yOff;
			} else if (dir == 1) {
				int yOff = 0;
				while (!level.isSolid(xPos + sprite.getWidth() / 2, yPos - yOff, 0) && yOff < ydistance)
					yOff++;
				ydistance = yOff;
			} else if (dir == 2) {
				int xOff = 0;
				while (!level.isSolid(xPos - xOff, yPos, 0) && xOff < xdistance)
					xOff++;
				xdistance = xOff;
			} else if (dir == 3) {
				int xOff = 0;
				while (!level.isSolid(xPos + xOff + sprite.getWidth(), yPos, 0) && xOff < xdistance)
					xOff++;
				xdistance = xOff;
			}
			if (x + w + (dir == 2 ? xdistance : 0) > xPos && x < xPos + sprite.getWidth() + (dir == 3 ? xdistance : 0) && y + h + (dir == 1 ? ydistance : 0) > yPos && y < yPos + sprite.getHeight() + (dir == 0 ? ydistance : 0)) {
				holder.isPaused = true;
				walkingToPlayer = true;
			}
		}

		if (spinFunc == 1) { // random spinning
			spinTimer++;
			if (spinTimer > spinTime) {
				dir = new Random().nextInt(4);
				spinTimer = 0;
			}
		} else if (spinFunc == 2) { // timed spinning
			spinTimer++;
			if (spinTimer > spinTime) {
				dir = (dir + 1) % 4;
				spinTimer = 0;
			}
		} else if (spinFunc == 3) { // Follow player
			int xv = xPos - holder.getX();
			int yv = yPos - holder.getY();
			dir = (Math.abs(xv) > Math.abs(yv)) ? (xv > 0 ? 2 : 3) : (yv > 0 ? 1 : 0);
		}
	}

}

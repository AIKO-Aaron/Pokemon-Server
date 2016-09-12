package ch.aiko.pokemon.entity.player;

import java.util.ArrayList;

import ch.aiko.as.ASArray;
import ch.aiko.as.ASDataBase;
import ch.aiko.as.ASField;
import ch.aiko.as.ASObject;
import ch.aiko.as.ASString;
import ch.aiko.as.SerializationReader;
import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSheet;
import ch.aiko.pokemon.basic.ModUtils;
import ch.aiko.pokemon.entity.Direction;
import ch.aiko.pokemon.entity.Entity;
import ch.aiko.pokemon.entity.trainer.Trainer;
import ch.aiko.pokemon.fight.Fight;
import ch.aiko.pokemon.level.Level;
import ch.aiko.pokemon.pokemons.PokemonType;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.pokemon.pokemons.TeamPokemon;
import ch.aiko.pokemon.server.PokemonServer;
import ch.aiko.util.FileUtil;

public class Player extends Entity {

	public static final int BOY = 1;
	public static final int GIRL = 2;

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
	public Fight currentFight;
	public ArrayList<Integer> trainersDefeated = new ArrayList<Integer>();
	public String name;
	public int gender;

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
		try {
			ASDataBase base = new ASDataBase("Player");
			ASObject teamObj = new ASObject("Team");
			for (TeamPokemon pok : team) {
				if (pok != null) {
					pok.reload();
					teamObj.addObject(pok.toObject());
				}
			}

			ASObject tdo = new ASObject("TDO");
			int[] td = new int[trainersDefeated.size()];
			for (int i = 0; i < td.length; i++)
				td[i] = trainersDefeated.get(i);
			tdo.addArray(ASArray.Integer("TD", td));

			ASObject poso = new ASObject("POS");
			poso.addField(ASField.Integer("X", xPos));
			poso.addField(ASField.Integer("Y", yPos));
			poso.addString(ASString.Create("Level", ""));

			ASObject gend = new ASObject("PlayerData");
			gend.addField(ASField.Integer("Gender", gender));
			gend.addString(ASString.Create("Name", name));
			
			base.addObject(teamObj);
			base.addObject(tdo);
			base.addObject(poso);
			base.addObject(gend);
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
	
	public String getPlayerName() {
		return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	}

	@Override
	public String getName() {
		return "Player";
	}

	@Override
	public int getLevel() {
		return PLAYER_RENDERED_LAYER;
	}

	public void winBattle(Trainer ct) {}

	public void lostBattle(Trainer ct) {}

	public Level load() {
		ASDataBase base = ASDataBase.createFromFile(FileUtil.getRunningJar().getParent() + "/player.bin");
		if (base != null) {
			ASObject gen = base.getObject("PlayerData");
			if (gen != null) {
				setGender(SerializationReader.readInt(gen.getField("Gender").data, 0));
				name = gen.getString("Name").toString();
			}

			ASObject teamObj = base.getObject("Team");
			int index = 0;
			if (team != null) {
				for (int i = 0; i < teamObj.objectCount; i++) {
					ASObject obj = teamObj.objects.get(i);
					if (obj != null) team[index++] = new TeamPokemon(obj);
				}
			}
			if (teamObj == null || index == 0) {
				team[0] = new TeamPokemon(Pokemons.get(6), PokemonType.OWNED, "Exterminator", ModUtils.convertToAttacks("Tackle", "Verzweifler"), 5, 10, 10, 10, 10, 10, 10, 10);
			}

			ASObject tdo = base.getObject("TDO");
			if (tdo != null) {
				int[] td = tdo.getArray("TD").getIntData();
				trainersDefeated = new ArrayList<Integer>();
				for (int i : td) {
					if (!trainersDefeated.contains(i)) trainersDefeated.add(i);
				}
			}
			ASObject poso = base.getObject("POS");
			xPos = SerializationReader.readInt(poso.getField("X").data, 0);
			yPos = SerializationReader.readInt(poso.getField("Y").data, 0);
			if (!poso.getString("Level").toString().endsWith(".layout")) return new PlayerSetter(this);
			return new Level(poso.getString("Level").toString());
		} else {
			team[0] = new TeamPokemon(Pokemons.get(6), PokemonType.OWNED, "Exterminator", ModUtils.convertToAttacks("Tackle", "Verzweifler"), 5, 10, 10, 10, 10, 10, 10, 10);
		}

		return new PlayerSetter(this);
	}

	public int getTeamLength() {
		int length = 0;
		for (TeamPokemon pok : team)
			if (pok != null) ++length;
		return length;
	}

	public void setGender(int p) {
		gender = p;
		sprites = new SpriteSheet("/ch/aiko/pokemon/textures/player/player_" + (gender == BOY ? "boy" : "girl") + ".png", 32, 32).removeColor(0xFF88B8B0);
		for (int i = 0; i < 4 * 4; i++) {
			walkingAnims[i] = sprites.getSprite(i, false);
		}
	}
}

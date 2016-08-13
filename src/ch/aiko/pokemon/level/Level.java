package ch.aiko.pokemon.level;

import java.util.ArrayList;
import java.util.Stack;

import ch.aiko.as.ASDataBase;
import ch.aiko.as.ASObject;
import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.LayerBuilder;
import ch.aiko.engine.graphics.LayerContainer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.sprite.SpriteSerialization;
import ch.aiko.engine.sprite.Tile;
import ch.aiko.pokemon.entity.Entity;
import ch.aiko.pokemon.entity.player.Player;
import ch.aiko.pokemon.graphics.menu.Menu;
import ch.aiko.pokemon.server.PokemonServer;

public class Level extends LayerContainer {

	public static final boolean DEBUG = false;

	public LevelSerialization type;
	// public int fieldSize = 64;
	public int fieldWidth, fieldHeight;
	public ArrayList<short[]> tileData = new ArrayList<short[]>();
	public ArrayList<Layer[]> tiles = new ArrayList<Layer[]>();
	public int layerCount = 0;
	public LevelPalette lp;
	public String path;

	private int tileSize = 32;
	public Stack<Layer> openMenus = new Stack<Layer>();

	public Level() {
		SpriteSerialization.TILE_SIZE = tileSize;
		type = new LevelSerialization(this);
		tiles = new ArrayList<Layer[]>();
		resetOffset = false;
		fieldWidth = 64;
		fieldHeight = 64;
	}

	public Level(int tileSize) {
		this.tileSize = tileSize;
		SpriteSerialization.TILE_SIZE = tileSize;
		type = new LevelSerialization(this);
		tiles = new ArrayList<Layer[]>();
		resetOffset = false;
		fieldWidth = 64;
		fieldHeight = 64;
	}

	/**
	 * Doesn't load the level! Use {@link ch.aiko.pokemon.level.Level#reload() reload} to load from path
	 * 
	 * @param path
	 *            The Path to the layout / level file
	 */
	public Level(String path) {
		SpriteSerialization.TILE_SIZE = tileSize;
		type = new LevelSerialization(this);
		tiles = new ArrayList<Layer[]>();
		resetOffset = false;
		this.path = path;
		fieldWidth = 64;
		fieldHeight = 64;
	}

	/**
	 * Doesn't load the level! Use {@link ch.aiko.pokemon.level.Level#reload() reload} to load from path
	 * 
	 * @param path
	 *            The Path to the layout / level file
	 * @param tileSize
	 *            The size of each tile
	 */
	public Level(String path, int tileSize) {
		this.tileSize = tileSize;
		SpriteSerialization.TILE_SIZE = tileSize;
		type = new LevelSerialization(this);
		tiles = new ArrayList<Layer[]>();
		resetOffset = false;
		this.path = path;
		fieldWidth = 64;
		fieldHeight = 64;
	}

	public void setTileSize(int size) {
		SpriteSerialization.TILE_SIZE = tileSize;
		this.tileSize = size;
	}

	public void getData(ASObject obj) {
		type.getData(obj);
	}

	public void decode() {
		int addedTiles = 0;
		for (int layer = 0; layer < tileData.size(); layer++) {
			short[] data = tileData.get(layer);
			Layer[] current = new Layer[fieldWidth * fieldHeight];
			for (int indexed = 0; indexed < data.length; indexed++) {
				boolean b = false;
				for (int i = 0; i < layer; i++) {
					if (tileData.get(i)[indexed] == tileData.get(layer)[indexed]) b = true;
				}
				if (b) continue;
				addedTiles++;
				Tile tile = lp.getCoding(data[indexed], (indexed % fieldWidth) * tileSize, (indexed / fieldWidth) * tileSize);
				if (indexed < current.length) current[indexed] = addLayer(new LayerBuilder().setLayer(layer).setNeedsInput(false).setName("Tile" + indexed).setRenderable(tile).toLayer());
			}
			tiles.add(current);
		}
		PokemonServer.out.println("Loaded " + layerCount + " layers for the level, with a total of " + (fieldWidth * fieldHeight * layerCount) + " tiles, but only " + addedTiles + " tiles were added");
	}

	public Level setPath(String path) {
		this.path = path;
		return this;
	}

	public Level loadLevel(String pathToLevel) {
		path = pathToLevel;

		removeAllLayers();
		tiles.clear();
		tileData.clear();
		layerCount = 0;

		PokemonServer.out.println("Loading Level");
		if (pathToLevel.endsWith(".bin")) {
			ASDataBase db = ASDataBase.createFromResource(pathToLevel);
			if (db == null) {
				System.err.println("DB == null");
				return this;
			}
			SpriteSerialization.deserializeSprites(db);
			ASObject levelData = db.getObject("LevelData");
			type.load(levelData);
		} else if (pathToLevel.endsWith(".layout")) {
			LayoutLoader loader = new LayoutLoader(pathToLevel);
			loader.loadLevel(this);
		}

		return this;
	}

	public int getLevel() {
		return -1;
	}

	public boolean stopsRendering() {
		return true;
	}

	public boolean stopsUpdating() {
		return true;
	}

	@Override
	public void layerRender(Renderer r) {}

	@Override
	public void postRender(Renderer r) {
		if (DEBUG) {
			for (int i = 0; i < tiles.size(); i++) {
				Layer[] ls = tiles.get(i);
				for (int j = 0; j < ls.length; j++) {
					Layer l = ls[j];
					if (l == null || l.getRenderable() == null) continue;
					Tile t = (Tile) l.getRenderable();
					if (t.layer > 0) r.drawRect(t.x, t.y, t.w, t.h, 0xFFFF00FF);
				}
			}
		}
	}

	@Override
	public void layerUpdate(Screen s, Layer l) {}

	public ArrayList<Tile> getTile(int x, int y) {
		if (x + y * fieldWidth >= fieldWidth * fieldHeight) return new ArrayList<Tile>();
		ArrayList<Tile> ret = new ArrayList<Tile>();
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i)[x + y * fieldWidth] != null) ret.add((Tile) tiles.get(i)[x + y * fieldWidth].getRenderable());
		}
		return ret;
	}

	public boolean isSolid(int x, int y, int layer) {
		int xedge = tileSize * fieldWidth;
		int yedge = tileSize * fieldHeight;
		if (x < 0 || x > xedge || y < 0 || y > yedge) return true; // Out of bounds

		int xcol = x / tileSize;
		int ycol = y / tileSize;
		int xof = x % tileSize;
		int yof = y % tileSize;

		for (Tile t : getTile(xcol, ycol)) {
			if (t != null && t.isSolid(xof, yof, layer)) return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "Level";
	}

	public Menu openMenu(Menu menu) {
		menu.onOpen();
		openMenus.add(addLayer(menu));
		return menu;
	}

	public void closeTopMenu() {
		Layer topLayer = openMenus.pop();
		((Menu) topLayer).onClose();
		removeLayer(topLayer);
	}

	public void closeAllMenus() {
		while (openMenus.isEmpty())
			closeTopMenu();
	}

	public void closeMenu(Menu m) {
		openMenus.remove(m);
		m.onClose();
		removeLayer(m);
	}

	public void addEntity(Entity p) {
		addLayer(p);
	}

	public void addPlayer(Player p) {
		addLayer(p);
	}

	/**
	 * Load the level from the path previously set (with {@link #setPath(String path) setPath(path)} or {@link #loadLevel(String path) loadLevel(path)}
	 * 
	 * @return this
	 */
	public Level reload() {
		if (path != null) loadLevel(path);
		return this;
	}

	public void removeEntity(Entity p) {
		removeLayer(p);
	}

}

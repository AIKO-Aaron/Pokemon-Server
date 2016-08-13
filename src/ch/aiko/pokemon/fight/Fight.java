package ch.aiko.pokemon.fight;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Stack;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.LayerBuilder;
import ch.aiko.engine.graphics.LayerContainer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSheet;
import ch.aiko.pokemon.attacks.Attack;
import ch.aiko.pokemon.entity.player.Player;
import ch.aiko.pokemon.entity.trainer.Trainer;
import ch.aiko.pokemon.graphics.menu.Animation;
import ch.aiko.pokemon.graphics.menu.Menu;
import ch.aiko.pokemon.pokemons.PokemonType;
import ch.aiko.pokemon.pokemons.Pokemons;
import ch.aiko.pokemon.pokemons.TeamPokemon;

public class Fight extends LayerContainer {

	public TeamPokemon pok1;
	public TeamPokemon pok2 = new TeamPokemon(Pokemons.get(1), PokemonType.ENEMY, "MyPokemonNickName2", new Attack[] {}, 2, 2, 0, 0, 0, 0, 0, 1);

	public Stack<Layer> openMenus = new Stack<Layer>();
	public Sprite background;
	public Screen s;

	public Font f = new Font("Arial", 0, 25);
	public int color, color2;
	public PixelRenderer renderer;

	public Fight(Screen s, Player p, Trainer t) {
		this.s = s;
		background = new Sprite("/ch/aiko/pokemon/textures/fight_background/grass_day.png").getScaledInstance(s.getFrameWidth(), s.getFrameHeight());
		Sprite ground = new Sprite("/ch/aiko/pokemon/textures/fight_ground/grass_day.png").getScaledInstance(s.getFrameWidth(), s.getFrameHeight());
		background.getImage().getGraphics().drawImage(ground.getImage(), 0, 0, null);
		renderer = new PixelRenderer(background, background.getWidth(), background.getHeight());

		pok1 = p.team[0];
		pok2 = t.team[0];

		addLayer(new LayerBuilder().setLayer(5).setRenderable(pok1).setUpdatable(pok1).toLayer());
		addLayer(new LayerBuilder().setLayer(5).setRenderable(pok2).setUpdatable(pok2).toLayer());
	}

	public int getStringWidth(String s) {
		FontMetrics metrics = background.getImage().getGraphics().getFontMetrics(f);
		return metrics.stringWidth(s);
	}

	public int getStringHeight() {
		FontMetrics metrics = background.getImage().getGraphics().getFontMetrics(f);
		return metrics.getHeight();
	}

	@Override
	public void onOpen() {
		openMenu(new FightMenu(s));
		openMenu(new Animation(s, new SpriteSheet("/ch/aiko/pokemon/textures/player/player_fight_boy.png", 80, 80, 300, 300).removeColor(0xFF88B8B0), false, 7).setPosition(150, s.getFrameHeight() - 300));
		// SoundPlayer.playSound("/ch/aiko/pokemon/sounds/TrainerFight.mp3"); // why is music disabled? Because I'm testing and it's annoying...

		// Set background of the renderer to the background image. It can be modified afterwards
		s.getRenderer().setClearPixels(renderer.pixels);
	}

	@Override
	public void onClose() {
		s.getRenderer().removeClearImage();
	}

	public int getLevel() {
		return 1;
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
	public void layerUpdate(Screen s, Layer l) {}

	@Override
	public String getName() {
		return "Fight";
	}

	public void openMenu(Menu menu) {
		menu.onOpen();
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

}

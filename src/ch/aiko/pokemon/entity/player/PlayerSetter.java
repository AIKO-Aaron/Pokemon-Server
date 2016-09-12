package ch.aiko.pokemon.entity.player;

import java.awt.image.BufferedImage;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.pokemon.level.Level;

public class PlayerSetter extends Level {

	private BufferedImage oak;
	private Player p;
	public PlayerSetterMenu menu;

	public PlayerSetter(Player p) {}

	public void layerRender(Renderer r) {
		r.fillRect(0, 0, r.getWidth(), r.getHeight(), 0xFF00FF00);
	}

	public void postRender(Renderer r) {
		r.drawImage(oak, 100, 100);
	}

	public void layerUpdate(Screen sc, Layer layer) {
		p.setPosition(0, 0);
	}

	public Level loadLevel(String pathToLevel) {
		return this;
	}

	public Level reload() {
		return this;
	}

	public void addPlayer(Player p) {}

}

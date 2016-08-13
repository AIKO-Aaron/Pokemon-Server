package ch.aiko.pokemon.graphics.menu;

import java.awt.Font;
import java.awt.FontMetrics;

import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.LayerContainer;
import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.graphics.Updatable;
import ch.aiko.pokemon.entity.player.Player;

public abstract class MenuObject extends Layer {

	public final int getStringWidth(Screen s, Font f, String text) {
		FontMetrics metrics = s.getGraphics().getFontMetrics(f);
		return metrics.stringWidth(text);
	}

	public final int getStringHeight(Screen s, Font f) {
		FontMetrics metrics = s.getGraphics().getFontMetrics(f);
		return metrics.getHeight();
	}

	@Override
	public void render(Renderer r) {}

	@Override
	public void update(Screen s, Layer l) {}

	@Override
	public Renderable getRenderable() {
		return (Renderer r) -> render(r);
	}

	@Override
	public Updatable getUpdatable() {
		return (Screen s, Layer l) -> update(s, l);
	}

	@Override
	public int getLevel() {
		return Player.PLAYER_RENDERED_LAYER + 1;
	}

	@Override
	public boolean stopsRendering() {
		return false;
	}

	@Override
	public boolean stopsUpdating() {
		return false;
	}

	@Override
	public String getName() {
		return "MenuObject";
	}

	public void closeMe() {
		if (parent != null) {
			try {
				((LayerContainer) parent).removeLayer(this);
			} catch (Throwable t) {
				System.err.println("Cannot convert " + parent + " to a layercontainer. Cannot close " + this);
			}
		} else if (needsInput) {
			input.screen.removeLayer(this);
		} else {
			System.err.println("Couldn't close " + this + " no parent found");
		}
	}

}

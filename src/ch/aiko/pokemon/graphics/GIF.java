package ch.aiko.pokemon.graphics;

import ch.aiko.util.ImageFrame;
import ch.aiko.util.ImageUtil;

public class GIF {

	public ImageFrame[] animation;
	public float scale = 1f;

	public GIF(String path) {
		animation = ImageUtil.readGifInClassPath(path, 1);
	}

	public GIF(String path, float scale) {
		animation = ImageUtil.readGifInClassPath(path, scale);
		this.scale = scale;
	}

	public GIF(String path, float scale, ClassLoader loader) {
		animation = ImageUtil.readGifInClassPath(path, scale, loader);
		this.scale = scale;
	}

	public GIF(ImageFrame[] anim, float scale) {
		animation = anim;
		this.scale = scale;
	}

	public GIF replaceColor(int old, int newc) {
		for (int i = 0; i < animation.length; i++) {
			ImageFrame ol = animation[i];
			animation[i] = new ImageFrame(ImageUtil.replaceColor(ol.getImage(), old, newc), ol.getDelay(), ol.getDisposal(), ol.getWidth(), ol.getHeight());
		}
		return this;
	}

}

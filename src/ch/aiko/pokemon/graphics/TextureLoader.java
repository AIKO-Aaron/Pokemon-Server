package ch.aiko.pokemon.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import ch.aiko.modloader.LoadedMod;
import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.server.PokemonServer;
import ch.aiko.util.ImageUtil;

import javax.imageio.ImageIO;

public class TextureLoader {

	public static BufferedImage loadImage(String path, Class<?> caller) {
		try {
			return ImageIO.read(caller.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static GIF loadGIF(String path, float scale) {
		for (LoadedMod mod : ModLoader.loadedMods) {
			InputStream inStream = mod.loader.getResourceAsStream(path);
			if (inStream != null) {
				try {
					return new GIF(ImageUtil.readGif(inStream, scale), scale);
				} catch (Throwable e) {
					e.printStackTrace(PokemonServer.out);
				}
			}
		}
		PokemonServer.out.err("Error loading " + path + ". It cannot be found. EVERYBODY PANIC!!!");
		return null;
	}

}

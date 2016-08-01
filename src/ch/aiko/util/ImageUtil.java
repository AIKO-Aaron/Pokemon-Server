package ch.aiko.util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import ch.aiko.pokemon.server.PokemonServer;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageUtil {

	/**
	 * Loads an Image from a given File and returns it as BufferedImage
	 * 
	 * @param f
	 *            The File which is a picture
	 * @return The BufferedImage from the File
	 */
	public static BufferedImage loadImage(File f) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (Throwable e) {
			e.printStackTrace(PokemonServer.out);
			System.err.println("File not found" + f.getAbsolutePath());
		}
		return img;
	}

	/**
	 * Loads an Image from a given Path from a File and returns it as BufferedImage
	 * 
	 * @param f
	 *            The File which is a picture
	 * @return The BufferedImage from the File
	 */
	public static BufferedImage loadImage(String path) {
		return loadImage(FileUtil.LoadFile(path));
	}

	/**
	 * Loads an Image from a Path from a File in ClassPath and returns it as BufferedImage
	 * 
	 * @param f
	 *            The File which is a picture
	 * @return The BufferedImage from the File
	 */
	public static BufferedImage LoadImageInClassPath(String path) {
		return loadImage(FileUtil.LoadFileInClassPath(path));
	}

	/**
	 * Second Method to load an Image in your ClassPath. Only use other when you have to!
	 * 
	 * @param path
	 *            The Path to the Image from your classPath ( Don't forget the slash at the beginning)
	 * @return The BufferedImage which was loaded
	 * @see LoadImageInClassPath
	 */
	public static BufferedImage loadImageInClassPath(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(ImageUtil.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace(PokemonServer.out);
			System.err.println("File not found: " + path);
		}
		return img;
	}

	/**
	 * Gets all Pixels from the Picture and returns it as one-dimensional Array
	 * 
	 * @param img
	 *            The Picture which is getting into the pixel array
	 * @return The Array of Pixels
	 */
	public static int[] getPixels(BufferedImage img) {
		int[] ret = new int[img.getWidth() * img.getHeight()];
		ret = img.getRGB(0, 0, img.getWidth(), img.getHeight(), ret, 0, img.getWidth());
		return ret;
	}

	/**
	 * Gets all Pixels from the Picture and returns it as one-dimensional Array
	 * 
	 * @param img
	 *            The Picture which is getting into the pixel array
	 * @return The Array of Pixels
	 */
	public static int[] getPixels(Image i) {
		BufferedImage img = toBufferedImage(i);

		return getPixels(img);
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) return (BufferedImage) img;

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	/**
	 * Creates a BufferedImage from pixels
	 * 
	 * @param pixels
	 *            All pixels
	 * @param width
	 *            The Width of the Image. If this isn't right the picture looks weird
	 * @param height
	 *            The Height of the Image. If this isn't right the picture looks weird
	 * @return The BufferedImage from all pixels
	 */
	public static BufferedImage loadImage(int[] pixels, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
	}

	public static BufferedImage loadImageFromWebsite(URL website) {
		try {
			return ImageIO.read(website);
		} catch (Exception e) {
			e.printStackTrace(PokemonServer.out);
		}

		return null;
	}

	public static BufferedImage loadImageFromWebsite(String website) {
		try {
			return ImageIO.read(new URL(website));
		} catch (Exception e) {
			e.printStackTrace(PokemonServer.out);
		}

		return null;
	}

	/**
	 * Gets the RGB value of a certain Pixel
	 * 
	 * @param img
	 *            The Image which the pixel should be gotten
	 * @param x
	 *            The X Coordinate
	 * @param y
	 *            The Y Coordinate
	 * @return The RGB Value of the Pixel X/Y
	 */
	public static int getRGB(BufferedImage img, int x, int y) {
		return img.getRGB(x, y);
	}

	/**
	 * Sets the RGB Value of a certain Pixel
	 * 
	 * @param img
	 *            The Image which should be returned after the Color of the Pixel has changed
	 * @param x
	 *            The X Coordinate
	 * @param y
	 *            The Y Coordinate
	 * @param pixelColor
	 *            The Color, which the new Pixel should get
	 * @return
	 */
	public static BufferedImage setRGB(BufferedImage img, int x, int y, int pixelColor) {
		img.setRGB(x, y, pixelColor);
		return img;
	}

	/**
	 * gets the Width of the Screen
	 * 
	 * @return The Width of the Screen
	 */
	public static int getScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	/**
	 * gets the Height of the Screen
	 * 
	 * @return The Height of the Screen
	 */
	public static int getScreenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	/**
	 * Takes a ScreenCapture over the entire Screen
	 * 
	 * @return A BufferedImage from the ScreenCapture
	 */
	public static BufferedImage takeScreenCapture() {
		BufferedImage img = null;
		try {
			Robot r = new Robot();
			img = r.createScreenCapture(new Rectangle(0, 0, getScreenWidth(), getScreenHeight()));
		} catch (AWTException e) {
			e.printStackTrace(PokemonServer.out);
		}

		return img;
	}

	/**
	 * Takes a ScreenCapture over the entire Screen
	 * 
	 * @param x
	 *            The X Coordinate where the Capture should start
	 * @param y
	 *            The Y Coordinate where the Capture should start
	 * @param width
	 *            The width of the Capture
	 * @param height
	 *            The Height of the Capture
	 * @return A BufferedImage from the ScreenCapture
	 */
	public static BufferedImage takeScreenCapture(int x, int y, int width, int height) {
		BufferedImage img = null;
		try {
			Robot r = new Robot();
			img = r.createScreenCapture(new Rectangle(x, y, width + x, height + y));
		} catch (AWTException e) {
			e.printStackTrace(PokemonServer.out);
		}

		return img;
	}

	/**
	 * Creates a resized instance of the Image Caution! Does not copy Alpha Channels
	 * 
	 * @param img
	 *            The old Image
	 * @param newWidth
	 *            The new Width of the Image
	 * @param newHeight
	 *            The new Height of the Image
	 * @return The Resized Instance of the Image
	 */
	public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight, int color) {
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImg.createGraphics();

		g.setColor(new Color(color));
		g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(), img.getHeight(), new Color(color), null);

		return newImg;
	}

	public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImg.createGraphics();

		g.drawImage(img, 0, 0, newWidth, newHeight, null);

		return newImg;
	}

	public static BufferedImage resize2(BufferedImage img, float nw, float nh) {
		AffineTransform af = new AffineTransform();
		af.scale(nw, nh);

		AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage bufferedThumb = operation.filter(img, null);
		return bufferedThumb;
	}

	public static BufferedImage takeScreenCaptureAll() {
		Rectangle screenRect = new Rectangle(0, 0, 0, 0);
		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
		}
		try {
			return new Robot().createScreenCapture(screenRect);
		} catch (AWTException e) {
			e.printStackTrace(PokemonServer.out);
		}
		return null;
	}

	public static BufferedImage takeScreenCapture(int monitor) {
		monitor %= getNumberMonitors();
		Rectangle screenRect = new Rectangle(0, 0, 0, 0);
		int i = 0;
		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			if (i == monitor) {
				screenRect = gd.getDefaultConfiguration().getBounds();
			}
			i++;
		}
		try {
			return new Robot().createScreenCapture(screenRect);
		} catch (AWTException e) {
			e.printStackTrace(PokemonServer.out);
		}
		return null;
	}

	public static int getNumberMonitors() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
	}

	public static BufferedImage replaceColor(BufferedImage src, int oldCol, int newCol) {
		int[] pixels = new int[src.getWidth() * src.getHeight()];
		pixels = src.getRGB(0, 0, src.getWidth(), src.getHeight(), pixels, 0, src.getWidth());
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == oldCol) pixels[i] = newCol;
		}
		src.setRGB(0, 0, src.getWidth(), src.getHeight(), pixels, 0, src.getWidth());
		return src;
	}

	public static ImageFrame[] readGifInClassPath(String path, float scale) {
		try {
			return readGif(ImageUtil.class.getResourceAsStream(path), scale);
		} catch (Throwable e) {
			e.printStackTrace(PokemonServer.out);
		}
		return null;
	}

	public static ImageFrame[] readGif(String path, float scale) {
		try {
			return readGif(new FileInputStream(path), scale);
		} catch (Throwable e) {
			e.printStackTrace(PokemonServer.out);
		}
		return null;
	}
	
	public static ImageFrame[] readGifInClassPath(String path, float scale, ClassLoader cl) {
		try {
			return readGif(cl.getResourceAsStream(path), scale);
		} catch (Throwable e) {
			e.printStackTrace(PokemonServer.out);
		}
		return null;
	}

	public static ImageFrame[] readGif(InputStream stream, float scale) throws IOException, Throwable {
		ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);

		ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
		reader.setInput(ImageIO.createImageInputStream(stream));

		int lastx = 0;
		int lasty = 0;

		int width = -1;
		int height = -1;

		IIOMetadata metadata = reader.getStreamMetadata();

		Color backgroundColor = null;

		if (metadata != null) {
			IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

			NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
			NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

			if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0) {
				IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

				if (screenDescriptor != null) {
					width = (int)(Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth")) * scale);
					height = (int)(Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight")) * scale);
				}
			}

			if (globalColorTable != null && globalColorTable.getLength() > 0) {
				IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

				if (colorTable != null) {
					String bgIndex = colorTable.getAttribute("backgroundColorIndex");

					IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
					while (colorEntry != null) {
						if (colorEntry.getAttribute("index").equals(bgIndex)) {
							int red = Integer.parseInt(colorEntry.getAttribute("red"));
							int green = Integer.parseInt(colorEntry.getAttribute("green"));
							int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

							backgroundColor = new Color(red, green, blue);
							break;
						}

						colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
					}
				}
			}
		}

		BufferedImage master = null;
		boolean hasBackround = false;

		for (int frameIndex = 0;; frameIndex++) {
			BufferedImage image;
			try {
				image = reader.read(frameIndex);
			} catch (IndexOutOfBoundsException io) {
				break;
			}

			if (width == -1 || height == -1) {
				width = (int)(image.getWidth() * scale);
				height = (int)(image.getHeight() * scale);
			}

			IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
			IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
			NodeList children = root.getChildNodes();

			int delay = Integer.valueOf(gce.getAttribute("delayTime"));

			String disposal = gce.getAttribute("disposalMethod");

			if (master == null) {
				master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				master.createGraphics().setColor(backgroundColor);
				master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

				hasBackround = image.getWidth() == width && image.getHeight() == height;

				master.createGraphics().drawImage(image, 0, 0, width, height, null);
			} else {
	            master.createGraphics().setColor(backgroundColor);
	            master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());
				
				int x = 0;
				int y = 0;

				for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
					Node nodeItem = children.item(nodeIndex);

					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						NamedNodeMap map = nodeItem.getAttributes();

						x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
						y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
					}
				}

				if (disposal.equals("restoreToPrevious")) {
					BufferedImage from = null;
					for (int i = frameIndex - 1; i >= 0; i--) {
						if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
							from = frames.get(i).getImage();
							break;
						}
					}

					if (from != null) {
						ColorModel model = from.getColorModel();
						boolean alpha = from.isAlphaPremultiplied();
						WritableRaster raster = from.copyData(null);
						master = new BufferedImage(model, raster, alpha, null);
					}
				} else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
					if (!hasBackround || frameIndex > 1) {
						master.createGraphics().fillRect(lastx, lasty, frames.get(frameIndex - 1).getWidth(), frames.get(frameIndex - 1).getHeight());
					}
				}
				master.createGraphics().drawImage(image, x, y, width, height, null);

				lastx = x;
				lasty = y;
			}

			BufferedImage copy;

			ColorModel model = master.getColorModel();
			boolean alpha = master.isAlphaPremultiplied();
			WritableRaster raster = master.copyData(null);
			copy = new BufferedImage(model, raster, alpha, null);
			frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));

			master.flush();
		}
		reader.dispose();

		return frames.toArray(new ImageFrame[frames.size()]);
	}

	public static BufferedImage deleteAlpha(BufferedImage image, int cap) {
		int[] pixels = getPixels(image);
		for (int i = 0; i < pixels.length; i++) {
			System.out.println(pixels[i]);
			if (((i >> 24) & 0xFF) < cap) pixels[i] = 0;
		}
		return loadImage(pixels, image.getWidth(), image.getHeight());
	}
}

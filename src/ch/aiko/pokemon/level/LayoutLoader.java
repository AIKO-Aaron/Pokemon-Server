package ch.aiko.pokemon.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import ch.aiko.as.ASDataBase;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSerialization;
import ch.aiko.engine.sprite.SpriteSheet;
import ch.aiko.pokemon.entity.Entity;
import ch.aiko.pokemon.entity.Teleporter;
import ch.aiko.pokemon.entity.trainer.TrainerLoader;
import ch.aiko.pokemon.server.PokemonServer;
import ch.aiko.util.FileUtil;

public class LayoutLoader {

	public static final boolean DEBUG = false;

	private HashMap<Integer, short[]> tiles;
	private HashMap<String, String> variables;
	private HashMap<Integer, SpriteSheet> sheets;
	private HashMap<Integer, Sprite> sprites;
	private HashMap<Integer, Integer> spriteLayers;
	private ArrayList<Entity> entities;

	private int sheetID = 0;
	private int spriteID = 1;

	private String text, path;
	private int tileSize = 32;
	private int fieldWidth = 64, fieldHeight = 64;

	public LayoutLoader(String path) {
		this.path = path;

		text = FileUtil.ReadFileInClassPath(path);
		tiles = new HashMap<Integer, short[]>();
		variables = new HashMap<String, String>();
		sheets = new HashMap<Integer, SpriteSheet>();
		sprites = new HashMap<Integer, Sprite>();
		spriteLayers = new HashMap<Integer, Integer>();
		entities = new ArrayList<Entity>();

		variables.put("FIELDWIDTH", "" + fieldWidth);
		variables.put("FIELDHEIGHT", "" + fieldHeight);
		variables.put("TILESIZE", "" + tileSize);

		decode(text);
	}

	public void addLayer(int id) {
		if (!tiles.containsKey(id - 1) && id > 0) addLayer(id - 1);
		short[] newTiles = new short[fieldWidth * fieldHeight];
		tiles.put(id, newTiles);
	}

	public void setTiles(int spid, int start, int times, int layer, int step) {
		if (!tiles.containsKey(layer)) addLayer(layer);
		short[] oldTiles = tiles.get(layer);
		for (int i = 0; i < times; i++) {
			oldTiles[i * step + start] = (short) spid;
		}
		tiles.put(layer, oldTiles);
	}

	public void setTiles(int spid, String varName, int times, int layer, int step) {
		if (!tiles.containsKey(layer)) addLayer(layer);
		int start = variables.containsKey(varName) ? Integer.parseInt(variables.get(varName)) : 0;
		short[] oldTiles = tiles.get(layer);
		for (int i = 0; i < times; i++) {
			oldTiles[i * step + start] = (short) spid;
		}
		variables.put(varName, "" + (start + times));
		tiles.put(layer, oldTiles);
	}

	public void fillTiles(int sid, int x, int y, int width, int height, int layer, boolean incr) {
		if (!tiles.containsKey(layer)) addLayer(layer);
		short[] oldTiles = tiles.get(layer);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				oldTiles[(j + x) + (i + y) * fieldWidth] = (short) sid;
				if (incr) sid++;
			}
		}
		tiles.put(layer, oldTiles);
	}

	public boolean decode(String s) {
		int line = 0;
		boolean prepro = false;
		boolean semi;
		try {
			while (!s.replace(" ", "").replace("\n", "").equalsIgnoreCase("")) {
				++line;
				String code = s.split("\n")[0];

				if (!code.startsWith("#")) {
					prepro = false;
					semi = code.contains(";");
					code = code.split(";")[0];
				} else {
					prepro = true;
					semi = false;
					code = code.substring(1);
				}
				s = s.substring(code.length() + 1 + (prepro ? 1 : 0));

				if (code.replace(" ", "").equalsIgnoreCase("") || code.startsWith("//")) continue;
				if (DEBUG) System.out.println("Interpreting: " + code);

				String c = code.split(" ")[0];
				String[] args = null;
				if (code.contains("\"")) {
					code = code.substring(c.length() + 1);
					ArrayList<String> arguments = new ArrayList<String>();
					while (code.contains("\"")) {
						int sp = code.indexOf(" ");
						int fi = code.indexOf("\"");

						if (sp < fi && sp != -1) {
							arguments.add(code.split(" ")[0]);
							code = code.substring(arguments.get(arguments.size() - 1).length() + 1);
							continue;
						}

						int si = code.substring(fi + 1).indexOf("\"") + fi + 1;
						arguments.add(code.substring(fi + 1, si));
						code = code.substring(0, fi) + code.substring(si + 1);
					}
					String[] rest = code.split(" ");
					for (int i = 0; i < rest.length; i++)
						arguments.add(rest[i]);
					args = arguments.toArray(new String[arguments.size()]);
				} else args = code.contains(" ") ? code.substring(c.length() + 1).split(" ") : new String[0];
				if (prepro) {
					if (c.equalsIgnoreCase("SET") || c.equalsIgnoreCase("DEFINE")) {
						if (args.length < 1) return throwError(line);
						String toT = args.length > 1 ? args[1] : "0";
						String frT = args[0];
						if (isKeyWord(frT)) throwError(line, "Cannot assign keywords");

						String varT = s.replace("+", " + ").replace("-", " - ").replace("*", " * ").replace("/", " / ").replace("%", " % ").replace("^", " ^ ").replace("√", " √ ").replace("\n", " \n ").replace("\"", " \" ").replace("#", " # ");
						s = varT.replace(" " + frT + " ", " " + toT + " ").replace(" + ", "+").replace(" - ", "-").replace(" * ", "*").replace(" / ", "/").replace(" % ", "%").replace(" ^ ", "^").replace(" √ ", "√").replace(" \n ", "\n").replace(" \" ", "\"").replace(" # ", "#");
					} else if (c.equalsIgnoreCase("FIELDWIDTH")) {
						if (args.length < 1) return throwError(line);
						fieldWidth = Integer.parseInt(args[0]);
						variables.put("FIELDWIDTH", "" + fieldWidth);
					} else if (c.equalsIgnoreCase("FIELDHEIGHT")) {
						if (args.length < 1) return throwError(line);
						fieldHeight = Integer.parseInt(args[0]);
						variables.put("FIELDHEIGHT", "" + fieldHeight);
					} else if (c.equalsIgnoreCase("TILESIZE")) {
						if (args.length < 1) return throwError(line);
						tileSize = Integer.parseInt(args[0]);
						variables.put("TILESIZE", "" + tileSize);
					}
				} else {
					if (c.equalsIgnoreCase("ADD")) {
						String toAdd = args[0];
						if (toAdd.equalsIgnoreCase("ENTITY")) {
							String type = args[1];

							// Load general stuff about the entity (position)
							int x = 0, y = 0, w = 32, h = 32;
							for (int i = 2; i < args.length; i++) {
								if (args[i].equalsIgnoreCase("X")) x = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("Y")) y = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("WIDTH")) w = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("HEIGHT")) h = (int) resolve(args[++i]);
							}

							// Specify wich entity
							if (type.equalsIgnoreCase("TELEPORTER")) {
								int dx = 0, dy = 0;
								String levelpath = path;
								for (int i = 2; i < args.length; i++) {
									if (args[i].equalsIgnoreCase("DX")) dx = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("DY")) dy = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("PATH")) levelpath = args[++i];
								}
								entities.add(new Teleporter(x, y, new Level(levelpath), dx, dy, w, h));
							} else if (type.equalsIgnoreCase("TRAINER")) {
								String tpath = "";
								for (int i = 2; i < args.length; i++) {
									if (args[i].equalsIgnoreCase("REL")) tpath = (path.contains("/") ? path.substring(0, path.length() - path.split("/")[path.split("/").length - 1].length()) : "/") + args[++i];
									if (args[i].equalsIgnoreCase("ABS")) tpath = args[++i];
								}
								if (!tpath.endsWith(".trainer")) tpath += ".trainer";
								entities.add(new TrainerLoader(tpath, x, y).getTrainer());
							}
						}
					} else if (c.equalsIgnoreCase("PRINT")) {
						PokemonServer.out.print("");
						for (int i = 0; i < args.length; i++) {
							if (args[i].equalsIgnoreCase("TEXT")) System.out.print(args[++i]);
							if (args[i].equalsIgnoreCase("VAR")) System.out.print(variables.get(args[++i]));
							if (args[i].equalsIgnoreCase("NEWLINE")) {
								System.out.println();
								PokemonServer.out.print("");
							}
							if (args[i].equalsIgnoreCase("NUM")) System.out.print(resolve(args[++i]));
							if (args[i].equalsIgnoreCase("LAYER")) {
								int layer = (int) resolve(args[++i]);
								short[] l = tiles.get(layer);
								for (int j = 0; j < fieldHeight; j++) {
									for (int k = 0; k < fieldWidth; k++) {
										System.out.print(l[k + j * fieldWidth] + ((k != fieldWidth - 1) ? "," : "\n"));
									}
								}
							}
						}
					} else if (c.equalsIgnoreCase("WRITE")) {
						int spriteID = 0, times = 1, to = 0, start = 0, step = 1;
						String varName = null;
						try {
							for (int i = 0; i < args.length; i++) {
								if (args[i].equalsIgnoreCase("ID")) spriteID = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("TIMES")) times = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("TO")) to = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("FROM")) start = variables.containsKey(args[i + 1]) ? Integer.parseInt(variables.get(args[++i])) : (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("VAR")) varName = args[++i];
								if (args[i].equalsIgnoreCase("SPRITE")) spriteID = variables.containsKey(args[i + 1]) ? Integer.parseInt(variables.get(args[++i])) : 0;
								if (args[i].equalsIgnoreCase("STEP")) step = (int) resolve(args[++i]);;
								if (args[i].equalsIgnoreCase("DOWN")) step = fieldWidth;
							}
						} catch (Throwable e) {
							return throwError(line, "Error parsing command: " + e.toString());
						}
						if (varName == null) setTiles(spriteID, start, times, to, step);
						else setTiles(spriteID, varName, times, to, step);
					} else if (c.equalsIgnoreCase("VAR")) {
						variables.put(args[0], args[1]);
					} else if (c.equalsIgnoreCase("FILL")) {
						int spriteID = 0, x = 0, y = 0, width = 1, height = 1, to = 0;
						boolean incr = false;
						for (int i = 0; i < args.length; i++) {
							if (args[i].equalsIgnoreCase("TO")) to = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("ID")) spriteID = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("X")) x = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("Y")) y = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("WIDTH")) width = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("HEIGHT")) height = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("SPRITE")) spriteID = variables.containsKey(args[i + 1]) ? Integer.parseInt(variables.get(args[++i])) : 0;
							if (args[i].equalsIgnoreCase("INCR")) incr = true;
						}
						fillTiles(spriteID, x, y, width, height, to, incr);
					} else if (c.equalsIgnoreCase("MAP")) {
						int spriteID = 0, level = 0, length = 1, step = 1;
						for (int i = 0; i < args.length; i++) {
							if (args[i].equalsIgnoreCase("ID")) spriteID = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("SPRITE")) spriteID = variables.containsKey(args[i + 1]) ? Integer.parseInt(variables.get(args[++i])) : 0;
							if (args[i].equalsIgnoreCase("LEVEL")) level = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("TIMES")) length = (int) resolve(args[++i]);
							if (args[i].equalsIgnoreCase("STEP")) step = (int) resolve(args[++i]);
						}
						for (int i = 0; i < length; i++)
							spriteLayers.put(spriteID + i * step, level);
					} else if (c.equalsIgnoreCase("LOAD")) {
						if (args[0].equalsIgnoreCase("SPRITE")) {
							int toID = 0, sx = 0, sy = 0, fromSheet = -1;
							String fromPath = null, idVar = null;
							int layer = 0;
							long color = 0xFFFF00FF;
							for (int i = 0; i < args.length; i++) {
								if (args[i].equalsIgnoreCase("FROM")) fromSheet = variables.containsKey(args[i + 1]) ? fromSheet = Integer.parseInt(variables.get(args[++i])) : (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("TO")) toID = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("ATX")) sx = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("ATY")) sy = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("LEVEL")) layer = (int) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("PATH")) fromPath = args[++i];
								if (args[i].equalsIgnoreCase("AUTO")) idVar = args[++i];
								if (args[i].equalsIgnoreCase("COLOR")) color = (long) resolve(args[++i]);
								if (args[i].equalsIgnoreCase("FLOOR")) toID = 0;
							}

							if (idVar != null) {
								toID = spriteID++;
								variables.put(idVar, "" + toID);
							}
							spriteLayers.put(toID, layer);
							if (sheets.containsKey(fromSheet)) {
								sprites.put(toID, sheets.get(fromSheet).getSprite(sx, sy).getScaledInstance(tileSize, tileSize));
							} else if (fromPath != null) {
								sprites.put(toID, new Sprite(fromPath).getScaledInstance(tileSize, tileSize));
							} else {
								int[] pix = new int[tileSize * tileSize];
								Arrays.fill(pix, (int) color);
								sprites.put(toID, new Sprite(pix, tileSize, tileSize));
							}
						} else if (args[0].equalsIgnoreCase("SPRITESHEET")) {
							int toID = 0, sw = 0, sh = 0;
							String fromPath = null, idVar = null;
							try {
								for (int i = 0; i < args.length; i++) {
									if (args[i].equalsIgnoreCase("TO")) toID = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("SWIDTH")) sw = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("SHEIGHT")) sh = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("PATH")) fromPath = args[++i];
									if (args[i].equalsIgnoreCase("AUTO")) idVar = args[++i];
								}
							} catch (Throwable e) {
								return throwError(line, "Error parsing command: " + e.toString());
							}
							if (idVar != null) {
								toID = sheetID++;
								variables.put(idVar, "" + toID);
							}
							sheets.put(toID, new SpriteSheet(fromPath, sw, sh));
						} else if (args[0].equalsIgnoreCase("AREA")) {
							int sheetID = 0, x = 0, y = 0, w = 1, h = 1, level = 0;
							String varID = null;
							try {
								for (int i = 0; i < args.length; i++) {
									if (args[i].equalsIgnoreCase("VAR")) varID = args[++i];
									if (args[i].equalsIgnoreCase("LEVEL")) level = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("FROM")) sheetID = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("X")) x = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("Y")) y = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("WIDTH")) w = (int) resolve(args[++i]);
									if (args[i].equalsIgnoreCase("HEIGHT")) h = (int) resolve(args[++i]);
								}
							} catch (Throwable e) {
								return throwError(line, "Error parsing command: " + e.toString());
							}
							int toID = 0;
							if (varID != null) {
								toID = spriteID++;
								variables.put(varID, "" + toID);
							}
							for (int i = 0; i < h; i++) {
								for (int j = 0; j < w; j++) {
									spriteLayers.put(toID, level);
									sprites.put(toID++, sheets.get(sheetID).getSprite(x + j, y + i).getScaledInstance(tileSize, tileSize));
								}
							}
						}
					}
					if (semi) line--;
				}
			}
		} catch (Throwable t) {
			throwError(line, "Unsuspected Error: " + t.toString());
			t.printStackTrace();
		}

		return true;
	}

	private boolean isKeyWord(String frT) {
		boolean b = false;

		if (!b) b |= frT.equalsIgnoreCase("LOAD");
		if (!b) b |= frT.equalsIgnoreCase("SPRITE");
		if (!b) b |= frT.equalsIgnoreCase("SPRITESHEET");
		if (!b) b |= frT.equalsIgnoreCase("X");
		if (!b) b |= frT.equalsIgnoreCase("Y");
		if (!b) b |= frT.equalsIgnoreCase("WIDTH");
		if (!b) b |= frT.equalsIgnoreCase("HEIGHT");
		if (!b) b |= frT.equalsIgnoreCase("FIELDWIDTH");
		if (!b) b |= frT.equalsIgnoreCase("FIELDHEIGHT");
		if (!b) b |= frT.equalsIgnoreCase("INCR");
		if (!b) b |= frT.equalsIgnoreCase("ATX");
		if (!b) b |= frT.equalsIgnoreCase("ATY");
		if (!b) b |= frT.equalsIgnoreCase("SET");
		if (!b) b |= frT.equalsIgnoreCase("USING");
		if (!b) b |= frT.equalsIgnoreCase("PATH");
		if (!b) b |= frT.equalsIgnoreCase("SOLID");
		if (!b) b |= frT.equalsIgnoreCase("SWIDTH");
		if (!b) b |= frT.equalsIgnoreCase("SHEIGHT");
		if (!b) b |= frT.equalsIgnoreCase("AUTO");
		if (!b) b |= frT.equalsIgnoreCase("NEWLINE");
		if (!b) b |= frT.equalsIgnoreCase("STEP");
		if (!b) b |= frT.equalsIgnoreCase("DOWN");

		if (frT.equalsIgnoreCase("ID") || frT.equalsIgnoreCase("TO") || frT.equalsIgnoreCase("FROM") || frT.equalsIgnoreCase("TIMES") || frT.equalsIgnoreCase("VAR")) return true;
		return b;
	}

	public double resolve(String equation) {
		equation = equation.replace(" ", "");

		String varT = " " + equation.replace("+", " + ").replace("-", " - ").replace("*", " * ").replace("/", " / ").replace("%", " % ").replace("^", " ^ ").replace("√", " √ ") + " ";
		for (String s : varT.split(" ")) {
			if (variables.containsKey(s)) {
				varT = varT.replace(" " + s + " ", variables.get(s));
			}
		}
		equation = varT.replace(" ", "");

		HashMap<Integer, Double> brackets = new HashMap<>();
		int curID = 0;

		while (equation.split(Pattern.quote("(")).length > 1 && equation.split(Pattern.quote(")")).length > 1) {
			int to;
			String[] te = equation.split(Pattern.quote("("));
			for (int i = 0; i < te.length; i++) {
				String t = te[i];
				if (t.replace(" ", "").equalsIgnoreCase("")) continue;
				if (((to = t.indexOf(")")) < t.indexOf("(")) || t.indexOf("(") == -1) {
					String modified = t.substring(0, to == -1 ? t.length() : to);
					double val = resolve(replace(modified, brackets));
					int id = curID++;
					brackets.put(id, val);
					equation = equation.replace("(" + t.substring(0, to == -1 ? t.length() : to) + ")", "$" + id);
				}
			}
		}

		equation = equation.replace("(", "").replace(")", "");

		while (equation.contains("^") || equation.contains("√")) {
			int firstIndex = equation.length();
			for (int i = 0; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '^' || c == '√') {
					firstIndex = i;
					break;
				}
			}

			int startFrom = 0;
			for (int i = firstIndex; i > 0; i--) {
				char c = equation.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
					startFrom = i + 1;
					break;
				}
			}

			int secondIndex = equation.length();
			for (int i = firstIndex + 1; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
					secondIndex = i;
					break;
				}
			}
			String ss1 = replace(equation.substring(startFrom, firstIndex), brackets);
			String ss2 = replace(equation.substring(firstIndex + 1, secondIndex), brackets);

			boolean hex1 = false, hex2 = false;

			if (ss1.startsWith("0x")) hex1 = true;
			if (ss2.startsWith("0x")) hex2 = true;

			double s1 = hex1 ? Long.parseLong(ss1.substring(2), 16) : Double.parseDouble(ss1);
			double s2 = hex2 ? Long.parseLong(ss2.substring(2), 16) : Double.parseDouble(ss2);

			if (equation.charAt(firstIndex) == '√') {
				equation = equation.replace(equation.substring(startFrom, secondIndex), Math.pow(s2, 1 / s1) + "");
			} else {
				equation = equation.replace(equation.substring(startFrom, secondIndex), Math.pow(s1, s2) + "");
			}
		}

		while (equation.contains("*") || equation.contains("/") || equation.contains("%")) {
			int fi = equation.length();
			for (int i = 0; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '*' || c == '/' || c == '%') {
					fi = i;
					break;
				}
			}
			int startFrom = 0;
			for (int i = fi - 1; i > 0; i--) {
				char c = equation.charAt(i);
				if (c == '*' || c == '/' || c == '%' || c == '+' || c == '-') {
					startFrom = i + 1;
					break;
				}
			}
			int si = equation.length();
			for (int i = fi + 1; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
					si = i;
					break;
				}
			}

			if (startFrom > fi) startFrom = 0;

			String ss1 = replace(equation.substring(startFrom, fi), brackets);
			String ss2 = replace(equation.substring(fi + 1, si), brackets);

			boolean hex1 = false, hex2 = false;

			if (ss1.startsWith("0x")) hex1 = true;
			if (ss2.startsWith("0x")) hex2 = true;

			double s1 = hex1 ? Long.parseLong(ss1.substring(2), 16) : Double.parseDouble(ss1);
			double s2 = hex2 ? Long.parseLong(ss2.substring(2), 16) : Double.parseDouble(ss2);

			double res = 0;

			if (equation.charAt(fi) == '*') res = s1 * s2;
			else if (equation.charAt(fi) == '/') res = s1 / s2;
			else if (equation.charAt(fi) == '%') res = s1 % s2;

			if (res < 0) {
				int id = curID++;
				brackets.put(id, res);
				equation = equation.replace(equation.substring(startFrom, si), "$" + id);
			} else {
				equation = equation.replace(equation.substring(startFrom, si), res + "");
			}
		}

		while (equation.contains("+") || equation.contains("-")) {
			if (equation.startsWith("-")) {
				double s2 = -Double.parseDouble(equation.substring(1).split(Pattern.quote("-"))[0].split(Pattern.quote("+"))[0]);
				int id = curID++;
				equation = "$" + id + equation.substring(equation.substring(1).split(Pattern.quote("-"))[0].split(Pattern.quote("+"))[0].length() + 1);
				brackets.put(id, s2);
			} else {
				int firstIndex = equation.length();
				for (int i = 0; i < equation.length(); i++) {
					char c = equation.charAt(i);
					if (c == '+' || c == '-') {
						firstIndex = i;
						break;
					}
				}

				int secondIndex = equation.length();
				for (int i = firstIndex + 2; i < equation.length(); i++) {
					char c = equation.charAt(i);
					if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
						secondIndex = i;
						break;
					}
				}

				String ss1 = replace(equation.substring(0, firstIndex), brackets);
				String ss2 = replace(equation.substring(firstIndex + 1, secondIndex), brackets);

				boolean hex1 = false, hex2 = false;

				if (ss1.startsWith("0x")) hex1 = true;
				if (ss2.startsWith("0x")) hex2 = true;

				double s1 = hex1 ? Long.parseLong(ss1.substring(2), 16) : Double.parseDouble(ss1);
				double s2 = hex2 ? Long.parseLong(ss2.substring(2), 16) : Double.parseDouble(ss2);

				if (equation.charAt(firstIndex) == '-') {
					equation = equation.replace(equation.substring(0, secondIndex), (s1 - s2) + "");
				} else {
					equation = equation.replace(equation.substring(0, secondIndex), (s1 + s2) + "");
				}
			}
		}
		equation = replace(equation, brackets);
		if (equation.startsWith("0x")) equation = "" + Long.parseLong(equation.substring(2), 16);
		return Double.parseDouble(equation);
	}

	public String replace(String s, HashMap<Integer, Double> map) {
		for (Entry<Integer, Double> e : map.entrySet()) {
			s = s.replace("$" + e.getKey(), e.getValue() + "");
		}
		return s;
	}

	public boolean throwError(int line) {
		return throwError("Error interpreting line: " + line + ": " + text.split("\n")[line]);
	}

	public boolean throwError(int line, String error) {
		return throwError("Error interpreting line: " + line + ": " + text.split("\n")[line] + "\n\t" + error);
	}

	public boolean throwError(String error) {
		System.err.println(error);
		return false;
	}

	public Level loadLevel(Level l) {
		l.fieldWidth = fieldWidth;
		l.fieldHeight = fieldHeight;

		l.lp = new LevelPalette("LevelPalette", l.type);

		for (int i = 0; i < tiles.size(); i++)
			l.tileData.add(new short[fieldWidth * fieldHeight]);
		for (Entry<Integer, short[]> e : tiles.entrySet()) {
			l.tileData.set(e.getKey().intValue(), e.getValue());
		}

		l.setTileSize(tileSize);

		SpriteSerialization.SERIALIZED.clear();
		SpriteSerialization.SPRITE_SHEETS.clear();
		SpriteSerialization.SPRITES.clear();
		SpriteSerialization.SS_SERIALIZED.clear();

		for (Entry<Integer, SpriteSheet> s : sheets.entrySet()) {
			SpriteSerialization.createFromImage(s.getValue().path, s.getValue().getSpriteWidth(), s.getValue().getSpriteHeight(), s.getKey());
		}
		for (Entry<Integer, Sprite> s : sprites.entrySet()) {
			if (s.getValue().getSuper() == null) SpriteSerialization.addSprite(s.getValue(), s.getValue().getPath(), s.getKey());
			else SpriteSerialization.addSprite(s.getValue(), s.getValue().getSuper(), s.getValue().getSuperX(), s.getValue().getSuperY(), false, s.getKey());
		}

		for (Entry<Integer, Sprite> e : sprites.entrySet()) {
			int id = e.getKey().intValue();
			Sprite s = sprites.get(id);
			int layer = spriteLayers.get(id).intValue();
			l.lp.addCoding((short) id, s, layer);
		}

		l.lp.reload();
		l.type.reload();

		for (Entity e : entities) {
			l.addEntity(e);
		}

		return l;
	}

	public LayoutLoader writeToLevel(Level l, ASDataBase db) {
		l.fieldWidth = fieldWidth;
		l.fieldHeight = fieldHeight;

		for (int i = 0; i < tiles.size(); i++)
			l.tileData.add(new short[fieldWidth * fieldHeight]);
		for (Entry<Integer, short[]> e : tiles.entrySet()) {
			l.tileData.set(e.getKey().intValue(), e.getValue());
		}

		SpriteSerialization.SERIALIZED.clear();
		SpriteSerialization.SPRITE_SHEETS.clear();
		SpriteSerialization.SPRITES.clear();
		SpriteSerialization.SS_SERIALIZED.clear();

		for (Entry<Integer, SpriteSheet> s : sheets.entrySet()) {
			SpriteSerialization.createFromImage(s.getValue().path, s.getValue().getSpriteWidth(), s.getValue().getSpriteHeight(), s.getKey());
		}
		for (Entry<Integer, Sprite> s : sprites.entrySet()) {
			if (s.getValue().getSuper() == null) SpriteSerialization.addSprite(s.getValue(), s.getValue().getPath(), s.getKey());
			else SpriteSerialization.addSprite(s.getValue(), s.getValue().getSuper(), s.getValue().getSuperX(), s.getValue().getSuperY(), false, s.getKey());
		}

		db.addObject(SpriteSerialization.serializeSprites());

		for (Entry<Integer, Sprite> e : sprites.entrySet()) {
			int id = e.getKey().intValue();
			Sprite s = sprites.get(id);
			int layer = spriteLayers.get(id).intValue();
			l.lp.addCoding((short) id, s, layer);
		}
		l.lp.reload();
		l.type.reload();
		db.addObject(l.type);
		return this;
	}
}
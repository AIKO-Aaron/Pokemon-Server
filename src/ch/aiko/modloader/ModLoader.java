package ch.aiko.modloader;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ch.aiko.util.FileUtil;
import ch.aiko.util.Log;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ModLoader {

	private static final ArrayList<URL> ALL_MOD_FILES = new ArrayList<>();

	public static ArrayList<LoadedMod> loadedMods = new ArrayList<LoadedMod>();

	public static int Status = 0;
	private static int FileIndex = 0;
	private static int CurrentFile = 0;
	private static int ClassIndex = 0;
	private static int CurrentClass = 0;
	private static int InitIndex = 0;

	public static String CoreInit = "Not known";
	private static String CurrentFileName;
	private static String CurrentClassName;

	public static JProgressBar bar3;

	public static Log log = new Log(ModLoader.class);

	public static final void loadMods(String dir, Runnable initCore) {
		new Thread(() -> displayStatus()).start();
		findMethods(dir);
		preInit();
		initCore.run();
		start();
	}

	private static void findMethods(String dir) {
		ArrayList<Class<?>> classesAnnotated = new ArrayList<>();

		File parent = new File(dir);
		File[] potentialMods = parent.listFiles((File pathname) -> pathname.getAbsolutePath().endsWith(".zip") || pathname.getAbsolutePath().endsWith(".jar"));

		if (!parent.exists()) {
			parent.mkdirs();
			return;
		}

		for (File f : potentialMods) {
			LoadedMod currentMod = new LoadedMod();

			Status = 1;
			++CurrentFile;
			CurrentFileName = f.getAbsolutePath();
			currentMod.pathToJar = f.getAbsolutePath();
			log.println("Loading File: " + CurrentFile + "/" + FileIndex + ": " + CurrentFileName);

			JarFile file = null;
			URLClassLoader cl = null;
			ClassPool pool = new ClassPool();

			try {
				cl = new URLClassLoader(new URL[] { new URL("jar:file:" + f.getAbsolutePath() + "!/") }, ClassLoader.getSystemClassLoader());
				file = new JarFile(f);
			} catch (Throwable t) {
				t.printStackTrace(log);
			}

			currentMod.loader = cl;

			boolean foundInfo = false;
			Enumeration<JarEntry> searchForInfo = file.entries();
			while (searchForInfo.hasMoreElements()) {
				JarEntry nextElement = searchForInfo.nextElement();
				if (nextElement.getName().endsWith(".amf")) {
					if (currentMod.modInfoList == null) {
						currentMod.modInfoList = new ModInfo(cl.getResourceAsStream(nextElement.getName()));
					} else {
						log.err("Each mod requires only one modInfoList (.amf)");
					}
					foundInfo = true;
				}
			}
			if (!foundInfo) continue;

			currentMod.name = currentMod.modInfoList.get("name");
			if (currentMod.name.equals("name")) currentMod.name = "Add a name in the .amf file: name=<insertNameHere>";

			log.println("Loading Mod: " + currentMod);

			try {
				ALL_MOD_FILES.add(new URL("jar:file:" + f.getAbsolutePath() + "!/"));
			} catch (MalformedURLException e) {
				e.printStackTrace(log);
			}

			pool.appendClassPath(new ClassClassPath(Mod.class));
			pool.appendClassPath(new ClassClassPath(GameInit.class));
			pool.appendClassPath(new LoaderClassPath(cl));
			ArrayList<Class<?>> possibleClasses = new ArrayList<>();

			Enumeration<JarEntry> entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry nextElement = entries.nextElement();
				if (!nextElement.isDirectory() && !nextElement.getName().startsWith("META-INF")) {
					if (nextElement.getName().endsWith(".class")) {
						String className = nextElement.getName().substring(0, nextElement.getName().length() - 6);
						className = className.replace("/", ".");
						try {
							possibleClasses.add(Class.forName(className, false, cl));
						} catch (Throwable e) {
							e.printStackTrace(log);
						}
					}
				}
			}

			Status = 2;
			CurrentClass = 0;
			ClassIndex = possibleClasses.size();
			possibleClasses.stream().map((c) -> {
				++CurrentClass;
				CurrentClassName = c.getName();
				log.println("Loading Class: " + CurrentClass + "/" + ClassIndex + ": " + CurrentClassName);
				return c;
			}).forEach((Class<?> c) -> {
				try {
					CtClass clazz = pool.get(c.getName());
					for (CtMethod m : clazz.getMethods()) {
						Event e;
						if ((e = (Event) m.getAnnotation(Event.class)) != null) {
							String[] keys = e.listeningFor();
							for (String key : keys) {
								ArrayList<Method> methods = currentMod.events.get(key);
								if (methods == null) methods = new ArrayList<>();
								if (m.getParameterTypes().length == 0) methods.add(c.getMethod(m.getName(), new Class[] {}));
								else methods.add(c.getMethod(m.getName(), new Class[] { GameEvent.class }));
								currentMod.events.put(key, methods);
							}
						}
					}

					for (CtField field : clazz.getFields()) {
						if (field.getAnnotation(Instance.class) != null) {
							Object inst = null;
							try {
								inst = c.getField(field.getName()).get(null);
							} catch (Throwable e) {
								e.printStackTrace(log);
							}
							if (inst != null) {
								currentMod.instances.put(c.getName(), inst);
							}
						}
					}
					if (clazz.getAnnotation(Mod.class) != null) {
						for (CtMethod m : clazz.getMethods()) {
							Object anno = m.getAnnotation(GameInit.class);
							if (anno != null) {
								if (((GameInit) anno).type() == InitMethod.PRE_INIT) {
									currentMod.preInits.add(c.getMethod(m.getName(), new Class[] {}));
								}
								if (((GameInit) anno).type() == InitMethod.INIT) {
									currentMod.Inits.add(c.getMethod(m.getName(), new Class[] {}));
								}
								if (((GameInit) anno).type() == InitMethod.POST_INIT) {
									currentMod.postInits.add(c.getMethod(m.getName(), new Class[] {}));
								}
							}
						}
					}
				} catch (NotFoundException | ClassNotFoundException | SecurityException | NoSuchMethodException e) {
					e.printStackTrace(log);
				}
			});

			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace(log);
			}

			loadedMods.add(currentMod);
		}

		Status = 3;

		LoadedMod internalFiles = new LoadedMod();
		internalFiles.name = "LocalFiles";
		internalFiles.modInfoList = new ModInfo();
		internalFiles.pathToJar = FileUtil.getRunningJar().getAbsolutePath();
		internalFiles.loader = ModLoader.class.getClassLoader();

		for (Class<?> c : classesAnnotated) {
			for (Method m : c.getMethods()) {
				if (m.isAnnotationPresent(GameInit.class)) {
					if (((GameInit) m.getAnnotation(GameInit.class)).type() == InitMethod.PRE_INIT) {
						internalFiles.preInits.add(m);
					}
					if (((GameInit) m.getAnnotation(GameInit.class)).type() == InitMethod.INIT) {
						internalFiles.Inits.add(m);
					}
					if (((GameInit) m.getAnnotation(GameInit.class)).type() == InitMethod.POST_INIT) {
						internalFiles.postInits.add(m);
					}
				} else if (c.getSuperclass() == CoreLoader.class) {
					if (m.getName().equals("preInit")) {
						internalFiles.preInits.add(m);
					}
					if (m.getName().equals("init")) {
						internalFiles.Inits.add(m);
					}
					if (m.getName().equals("postInit")) {
						internalFiles.postInits.add(m);
					}
				}
			}
		}

		loadedMods.add(0, internalFiles);
	}

	private static void displayStatus() {
		final JFrame frame = new JFrame();

		final JProgressBar bar1 = new JProgressBar(0, 100);
		final JProgressBar bar2 = new JProgressBar(0, 700);
		bar3 = new JProgressBar(0, 100);

		int preInitSize = 0;
		for (LoadedMod lm : loadedMods)
			preInitSize += lm.preInits.size();

		int InitSize = 0;
		for (LoadedMod lm : loadedMods)
			InitSize += lm.Inits.size();

		int postInitSize = 0;
		for (LoadedMod lm : loadedMods)
			postInitSize += lm.postInits.size();

		final JTextPane area = new JTextPane();
		area.setPreferredSize(new Dimension(750, 150));
		area.setFont(new Font("Arial", 0, 40));
		area.setEditable(false);
		area.setText("1\n2\3\n4\n5\n6\n7\n8\n9\n10");

		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
		area.setParagraphAttributes(attribs, true);

		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(area);
		pane.add(bar1);
		pane.add(bar2);
		pane.add(bar3);

		frame.add(pane);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.setVisible(true);

		while (true) {
			frame.requestFocus();

			int v1 = 0;
			switch (Status) {
				case 1:
					v1 = FileIndex == 0 ? 0 : 100 * CurrentFile / FileIndex;
					break;
				case 2:
					v1 = ClassIndex == 0 ? 0 : 100 * CurrentClass / ClassIndex;
					break;
				case 4:
					v1 = preInitSize != 0 ? 100 * InitIndex / preInitSize : 100;
					break;
				case 5:
					v1 = InitSize != 0 ? 100 * InitIndex / InitSize : 100;
					break;
				case 6:
					v1 = postInitSize != 0 ? 100 * InitIndex / postInitSize : 100;
					break;
				default:
					v1 = 0;
			}
			bar1.setValue(v1);
			bar2.setValue(Status * 100 + v1);

			String text = "Status: " + (Status == 0 ? "Loading Internal Files" : Status == 1 ? "Loading Jar" : Status == 2 ? "Loading Classes from Jar" : Status == 3 ? "Setup Core components" : Status == 4 ? "Pre-initialization" : Status == 5 ? "Initializing" : Status == 6 ? "Post-initializing" : Status + "");
			text += "\n\n";
			switch (Status) {
				case 1:
					text += "File " + CurrentFile + " from " + FileIndex + "\n" + CurrentFileName + "\nn";
					break;
				case 2:
					text += "Class " + CurrentClass + " from " + ClassIndex + "\n" + CurrentClassName + "\nfrom File: " + CurrentFile + "/" + FileIndex + ": " + CurrentFileName + "\n";
					break;
				case 3:
					text += "Loading Core: " + CoreInit;
					break;
				case 4:
					text += "Pre-initializing: " + InitIndex + "/" + preInitSize;
					break;
				case 5:
					text += "Initializing: " + InitIndex + "/" + InitSize;
					break;
				case 6:
					text += "Post-initializing: " + InitIndex + "/" + postInitSize;
					break;
				default:
					text += "\n\n\n";
			}
			if (!text.equalsIgnoreCase(area.getText())) {
				area.setText(text);
			}
			if (Status == 7) {
				break;
			}
		}
		frame.setVisible(false);
		frame.dispose();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static Object getInstanceOf(LoadedMod lm, Method m) {
		if (lm.instances.containsKey(m.getDeclaringClass().getName())) { return lm.instances.get(m.getDeclaringClass().getName()); }
		Object instance = null;
		try {
			instance = m.getDeclaringClass().newInstance();
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e1) {
			log.println("Couldn't read @instance in your class nor could it be instanziated. Mod will not be loaded...");
		}
		return instance;
	}

	private static void preInit() {
		for (int i = 0; i < loadedMods.size(); i++) {
			LoadedMod lm = loadedMods.get(i);
			if (lm == null) continue;
			if (lm.preInits.size() > 0) {
				Status = 4;
				InitIndex = 0;
				lm.preInits.stream().forEach((Method m) -> {
					try {
						Object instance = getInstanceOf(lm, m);
						try {
							++InitIndex;
							if(bar3 != null) bar3.setValue(0);
							m.invoke(instance, new Object[] {});
							if(bar3 != null) bar3.setValue(100);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
					} catch (Throwable t) {
						log.err("Error loading mod: " + lm.name + ". Error whilst pre-initialization: " + t);
						t.printStackTrace(log);
						loadedMods.remove(lm);
					}
				});
			}
		}
	}

	private static void start() {
		for (LoadedMod lm : loadedMods) {

			if (lm.Inits.size() > 0) {
				Status = 5;
				InitIndex = 0;
				lm.Inits.stream().forEach((Method m) -> {
					try {
						Object instance = getInstanceOf(lm, m);
						try {
							++InitIndex;
							if(bar3 != null) bar3.setValue(0);
							m.invoke(instance, new Object[] {});
							if(bar3 != null) bar3.setValue(100);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
					} catch (Throwable t) {
						log.err("Error loading mod: " + lm.name + ". Error whilst initialization");
						loadedMods.remove(lm);
					}
				});
			}
		}

		for (LoadedMod lm : loadedMods) {
			if (lm.postInits.size() > 0) {
				Status = 6;
				InitIndex = 0;
				lm.postInits.stream().forEach((Method m) -> {
					try {
						Object instance = getInstanceOf(lm, m);
						try {
							++InitIndex;
							if(bar3 != null) bar3.setValue(0);
							m.invoke(instance, new Object[] {});
							if(bar3 != null) bar3.setValue(100);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
					} catch (Throwable t) {
						log.err("Error loading mod: " + lm.name + ". Error whilst post-initialization");
						loadedMods.remove(lm);
					}
				});
			}
		}
		Status = 7;
	}

	public static void performEvent(GameEvent evt) {
		for (LoadedMod mod : loadedMods) {
			if (mod != null && mod.events != null && mod.events.containsKey(evt.eventName())) {
				for (Method m : mod.events.get(evt.eventName())) {
					executeEvent(m, mod, evt);
				}
			}
		}
	}

	private static void executeEvent(Method m, LoadedMod mod, GameEvent evt) {
		try {
			if (m.getParameterCount() == 0) m.invoke(getInstanceOf(mod, m));
			else if (m.getParameterCount() == 1) m.invoke(getInstanceOf(mod, m), evt);
			else log.err("Couldn't invoke: " + m.getName() + " because the argument count was wrong!");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}

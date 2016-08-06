package ch.aiko.pokemon.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

import ch.aiko.modloader.LoadedMod;
import ch.aiko.modloader.ModLoader;

public class ServerListener {

	public static final int PORT = 4732;

	protected boolean running = false;
	protected Thread acceptor, receiver;
	protected ServerSocket socket;
	protected ArrayList<BufferedReader> reader = new ArrayList<BufferedReader>();
	protected ArrayList<Socket> clients = new ArrayList<Socket>();
	protected ArrayList<String> uuids = new ArrayList<String>();

	public ServerListener() {
		running = true;
		try {
			socket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		acceptor = new Thread(() -> accept());
		receiver = new Thread(() -> receive());
		PokemonServer.out.println("Listening on port:" + PORT);
		acceptor.start();
		receiver.start();
	}

	public boolean existsUUID(String uuid) {
		for (Player p : PokemonServer.handler.p) {
			if (p.uuid.equals(uuid)) return true;
		}
		return false;
	}

	private void accept() {
		while (running) {
			try {
				Socket s = socket.accept();
				clients.add(s);
				reader.add(new BufferedReader(new InputStreamReader(s.getInputStream())));
				uuids.add(null);
				PokemonServer.out.println("Connected on port: " + s.getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receive() {
		while (running) {
			if (reader.size() == 0) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else for (int i = 0; i < reader.size(); i++) {
				Socket c = clients.get(i);
				if (c.isClosed()) {
					disconnect(c);
					continue;
				}
				BufferedReader r = reader.get(i);
				if (c == null || r == null) continue;
				try {
					String received = r.readLine();
					if (received == null) continue;
					perform(received, c);
				} catch (IOException e) {
					if (!(e instanceof SocketException)) e.printStackTrace();
					disconnect(c);
				}
			}
		}
	}

	public void kickUUID(String uuid) {
		int index = uuids.indexOf(uuid);
		clients.remove(index);
		uuids.remove(index);
		reader.remove(index);
	}

	private void perform(String received, Socket s) {
		if (!clients.contains(s)) return;
		if (received.equalsIgnoreCase("/ruuid/")) {
			String uuid = genUUID();
			while (existsUUID(uuid))
				uuid = genUUID();
			System.out.println("Generated uuid: " + uuid);
			send(s, "/guuid/" + uuid);
			connect(s, uuid);
		}
		if (received.equalsIgnoreCase("/rlvl/")) {
			send(s, "/lvl/" + PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s))).currentLevel);
		}
		if (received.equalsIgnoreCase("/rpos/")) {
			Player p = PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s)));
			send(s, "/pos/" + p.x + "/" + p.y + "/" + p.dir);
		}
		if (received.startsWith("/c/")) connect(s, received.substring(3));
		if (received.startsWith("/slvl/")) {
			PokemonServer.handler.setPlayerLevel(getUUID(s), received.substring(6));
			updatePositions();
		}
		if (received.startsWith("/spos/")) {
			int x = Integer.parseInt(received.substring(6).split("/")[0]);
			int y = Integer.parseInt(received.substring(6).split("/")[1]);
			int dir = Integer.parseInt(received.substring(6).split("/")[2]);
			PokemonServer.handler.setPlayerPos(getUUID(s), x, y, dir);
			if (PokemonServer.listener != null) PokemonServer.listener.updatePositions();
		}
		if (received.equalsIgnoreCase("/q/")) {
			disconnect(s);
		}
	}

	public String getUUID(Socket s) {
		return uuids.get(clients.indexOf(s));
	}

	public Player getPlayer(Socket s) {
		return PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s)));
	}

	private void connect(Socket s, String uuid) {
		if (uuids.contains(uuid)) kickUUID(uuid);
		PokemonServer.out.println("Player connected with uuid: " + uuid);
		uuids.set(clients.indexOf(s), uuid);
		Player p = PokemonServer.handler.getPlayer(uuid);
		if (p == null) PokemonServer.handler.addPlayer(new Player(uuid));
		p = PokemonServer.handler.getPlayer(uuid);
		p.online = true;

		send(s, "/pos/" + p.x + "/" + p.y + "/" + p.dir);
		send(s, "/lvl/" + p.currentLevel);

		String mods = "/mods/" + ModLoader.loadedMods.size() + "\n";
		for (LoadedMod lm : ModLoader.loadedMods) {
			mods += lm.modInfoList.get("name") + "=" + lm.modInfoList.get("version") + "\n";
		}
		send(s, mods);

		setPositions(s);
	}

	public void setPositions(Socket s) {
		Player p = getPlayer(s);
		String setter = "/pset/\n";

		for (int i = 0; i < PokemonServer.handler.p.size(); i++) {
			Player p1 = PokemonServer.handler.p.get(i);
			if (p != p1 && p1.online && p1.currentLevel.equalsIgnoreCase(p.currentLevel)) {
				String player = p1.x + "/" + p1.y + "/" + p1.dir + "/" + p1.uuid;
				setter += player + "\n";

				Socket s1 = clients.get(uuids.indexOf(p1.uuid));
				send(s1, "/padd/" + p.x + "/" + p.y + "/" + p.dir + "/" + p.uuid);
			}
		}

		setter += "/pend/";
		send(s, setter);
	}

	public void removePlayer(Socket s) {
		for (int i = 0; i < clients.size(); i++) {
			Socket s1 = clients.get(i);
			if (s1 == s) continue;
			send(s1, "/prem/" + uuids.get(clients.indexOf(s)));
		}
	}

	public void updatePositions() {
		if (PokemonServer.handler == null || PokemonServer.handler.p == null) return;
		for (int i = 0; i < PokemonServer.handler.p.size(); i++) {
			Player p1 = PokemonServer.handler.p.get(i);
			if (!p1.online) continue;
			for (int j = i + 1; j < PokemonServer.handler.p.size(); j++) {
				Player p = PokemonServer.handler.p.get(j);
				if (!p.online) continue;
				if (p1.currentLevel.equalsIgnoreCase(p.currentLevel)) {
					Socket s = clients.get(uuids.indexOf(p.uuid));
					send(s, "/pupd/" + p1.x + "/" + p1.y + "/" + p1.dir + "/" + p1.uuid);

					Socket s1 = clients.get(uuids.indexOf(p1.uuid));
					send(s1, "/pupd/" + p.x + "/" + p.y + "/" + p.dir + "/" + p.uuid);
				}
			}
		}
	}

	public void disconnect(Socket s) {
		Player p = getPlayer(s);
		System.out.println("Player disconnected: " + p.uuid);
		int index = clients.indexOf(s);
		p.online = false;
		try {
			clients.get(index).close();
			reader.get(index).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clients.remove(index);
		reader.remove(index);
		uuids.remove(index);
	}

	private void send(Socket s, String text) {
		new Thread(() -> {
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				for (String t : text.split("\n")) {
					writer.write(t + "\n");
					writer.flush();
				}
			} catch (IOException e) {
				if (!(e instanceof java.net.SocketException)) e.printStackTrace(PokemonServer.out);
			}
		}).start();
	}

	private String genUUID() {
		Random randseed = new Random(); // unneccessary I know
		Random rand = new Random(randseed.nextLong());
		String uuid = "";
		// 48-57 = 0-9
		// 65-90 = A-Z
		// 97-122 = a-z
		for (int i = 0; i < 20; i++) {
			int type = rand.nextInt(2);
			uuid += type == 0 ? (char) (rand.nextInt(9) + 48) : type == 1 ? (char) (rand.nextInt(25) + 65) : (char) (rand.nextInt(25) + 97);
			if (i % 4 == 3 && i != 19) uuid += "-";
		}
		return uuid;
	}
}

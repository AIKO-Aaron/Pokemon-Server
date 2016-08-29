package ch.aiko.pokemon.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ch.aiko.as.ASDataBase;
import ch.aiko.modloader.LoadedMod;
import ch.aiko.modloader.ModLoader;
import ch.aiko.pokemon.basic.PokemonEvents;

public class ServerListener {

	protected boolean running = false, sending;
	protected Thread acceptor, receiver, sender;
	protected ServerSocket socket;
	protected ArrayList<BufferedReader> reader = new ArrayList<BufferedReader>();
	protected ArrayList<Socket> clients = new ArrayList<Socket>();
	protected ArrayList<String> uuids = new ArrayList<String>();
	protected HashMap<Socket, ArrayList<String>> texts = new HashMap<Socket, ArrayList<String>>();

	public ServerListener(int PORT) {
		running = true;
		try {
			socket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		acceptor = new Thread(() -> accept());
		receiver = new Thread(() -> receive());
		sender = new Thread(() -> sendText());
		PokemonServer.out.println("Listening on port:" + PORT);
		acceptor.start();
		receiver.start();
		sender.start();
	}

	public boolean existsUUID(String uuid) {
		for (ServerPlayer p : PokemonServer.handler.p) {
			if (p.uuid.equals(uuid)) return true;
		}
		return false;
	}

	private void accept() {
		while (running) {
			try {
				Socket s = socket.accept();
				texts.put(s, new ArrayList<String>());
				uuids.add(null);
				PokemonServer.out.println("Connected on port: " + s.getPort());
				reader.add(new BufferedReader(new InputStreamReader(s.getInputStream())));
				clients.add(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receive() {
		while (running) {
			if (reader.size() == 0) {
				try {
					Thread.sleep(100);
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
					if (received == null || received.equalsIgnoreCase("")) continue;
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
		ModLoader.performEvent(new PokemonEvents.StringReceivedEvent(received, s));
		if (!clients.contains(s)) return;
		if (received.equalsIgnoreCase("/ruuid/")) {
			String uuid = genUUID();
			while (existsUUID(uuid))
				uuid = genUUID();
			PokemonServer.out.println("Generated uuid: " + uuid);
			send(s, "/guuid/" + uuid);
			connect(s, uuid);
		}
		if (received.equalsIgnoreCase("/rlvl/")) {
			send(s, "/lvl/" + PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s))).currentLevel);
		}
		if (received.equalsIgnoreCase("/rpos/")) {
			ServerPlayer p = PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s)));
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
		if (received.equalsIgnoreCase("/rec/")) {
			finishUp(s);
		}
		if (received.startsWith("/LTT/")) {
			int lostToTrainer = Integer.parseInt(received.substring(5));
			System.out.println("Player: " + getPlayer(s).uuid + " got defeated by trainer: " + lostToTrainer);
		}
		if (received.startsWith("/DFT/")) {
			int defeatedTrainer = Integer.parseInt(received.substring(5));
			getPlayer(s).trainersDefeated.add(defeatedTrainer);
		}
	}

	public String getUUID(Socket s) {
		return uuids.get(clients.indexOf(s));
	}

	public ServerPlayer getPlayer(Socket s) {
		return PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s)));
	}

	private void connect(Socket s, String uuid) {
		if (uuids.contains(uuid)) kickUUID(uuid);
		PokemonServer.out.println("Player connected with uuid: " + uuid);
		uuids.set(clients.indexOf(s), uuid);
		ServerPlayer p = PokemonServer.handler.getPlayer(uuid);
		if (p == null) PokemonServer.handler.addPlayer(new ServerPlayer(uuid));
		p = PokemonServer.handler.getPlayer(uuid);
		p.online = true;

		ASDataBase base = p.toBase();
		byte[] bytes = new byte[base.getSize()];
		base.getBytes(bytes, 0);

		send(s, "/SOPD/" + bytes.length);
	}

	private void finishUp(Socket s) {
		ServerPlayer p = getPlayer(s);
		ModLoader.performEvent(new PokemonEvents.PlayerConnectedEvent(p));
		ASDataBase base = p.toBase();
		byte[] bytes = new byte[base.getSize()];
		base.getBytes(bytes, 0);
		sendBytes(s, bytes);
		send(s, "/EOPD/");

		String mods = "/mods/" + ModLoader.loadedMods.size() + "\n";
		for (LoadedMod lm : ModLoader.loadedMods) {
			mods += lm.modInfoList.get("name") + "=" + lm.modInfoList.get("version") + "\n";
		}
		send(s, mods);

		setPositions(s);
	}

	public void setPositions(Socket s) {
		ServerPlayer p = getPlayer(s);
		String setter = "/pset/\n";

		for (int i = 0; i < PokemonServer.handler.p.size(); i++) {
			ServerPlayer p1 = PokemonServer.handler.p.get(i);
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
			ServerPlayer p1 = PokemonServer.handler.p.get(i);
			if (!p1.online) continue;
			for (int j = i + 1; j < PokemonServer.handler.p.size(); j++) {
				ServerPlayer p = PokemonServer.handler.p.get(j);
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
		ServerPlayer p = getPlayer(s);
		PokemonServer.out.println("Player disconnected: " + p.uuid);
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

	public void send(Socket s, String text) {
		texts.get(s).add(text);
	}

	private void sendText() {
		sending = true;
		PokemonServer.out.println("Started sending");
		while (sending) {
			if (clients.size() == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {
				for (int i = 0; i < clients.size(); i++) {
					Socket s = clients.get(i);
					if (s == null) continue;
					ArrayList<String> textsToSend = texts.get(s);
					if (textsToSend == null || textsToSend.size() <= 0) continue;
					try {
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
						String text = textsToSend.get(0);
						if (text != null) for (String t : text.split("\n")) {
							writer.write(t + "\n");
							writer.flush();
						}
						texts.get(s).remove(0);
					} catch (IOException e) {
						e.printStackTrace(PokemonServer.out);
					}
				}
			}
		}
	}

	public void sendBytes(Socket s, byte[] bytes) {
		if (s == null) return;
		try {
			new DataOutputStream(s.getOutputStream()).write(bytes, 0, bytes.length);
		} catch (Throwable e) {
			if (!(e instanceof SocketException)) e.printStackTrace(PokemonServer.out);
		}
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

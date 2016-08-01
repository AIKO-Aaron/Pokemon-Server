package ch.aiko.pokemon.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

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
		System.out.println("Listening on port:" + PORT);
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
				clients.add(socket.accept());
				reader.add(new BufferedReader(new InputStreamReader(clients.get(clients.size() - 1).getInputStream())));
				uuids.add(null);
				System.out.println("Connected on port: " + clients.get(clients.size() - 1).getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receive() {
		while (running) {
			for (int i = 0; i < reader.size(); i++) {
				Socket c = clients.get(i);
				BufferedReader r = reader.get(i);
				if (c == null || r == null) continue;
				try {
					String received = r.readLine();
					if (received == null) continue;
					perform(received.trim(), c);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void kickUUID(String uuid) {

	}

	private void perform(String received, Socket s) {
		if (received.equalsIgnoreCase("/ruuid/")) {
			String uuid = genUUID();
			while (existsUUID(uuid))
				uuid = genUUID();
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
			getPlayer(s).currentLevel = received.substring(6);
		}
		if(received.startsWith("/spos/")) {
			Player p = getPlayer(s);
			p.x = Integer.parseInt(received.substring(6).split("/")[0]);
			p.y = Integer.parseInt(received.substring(6).split("/")[1]);
			p.dir = Integer.parseInt(received.substring(6).split("/")[2]);
		}
	}
	
	public Player getPlayer(Socket s) {
		return PokemonServer.handler.getPlayer(uuids.get(clients.indexOf(s)));
	}

	private void connect(Socket s, String uuid) {
		uuids.set(clients.indexOf(s), uuid);
		if (PokemonServer.handler.getPlayer(uuid) == null) {
			PokemonServer.handler.addPlayer(new Player(uuid));
		}
	}

	private void send(Socket s, String text) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write(text + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
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

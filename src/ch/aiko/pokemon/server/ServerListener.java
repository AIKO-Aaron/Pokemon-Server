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
	protected ArrayList<Socket> clients = new ArrayList<Socket>();

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

	private void accept() {
		while (running) {
			try {
				clients.add(socket.accept());
				System.out.println("Connected on port: " + clients.get(clients.size() - 1).getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receive() {
		while (running) {
			for (int i = 0; i < clients.size(); i++) {
				Socket c = clients.get(i);
				if (c == null) continue;
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
					String received = reader.readLine();
					if (received == null) continue;
					perform(received.trim(), c);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void perform(String received, Socket s) {
		if (received.equalsIgnoreCase("/ruuid/")) send(s, "/guuid/" + genUUID());
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
		System.out.println("Generated Uuid:" + uuid);
		return uuid;
	}
}

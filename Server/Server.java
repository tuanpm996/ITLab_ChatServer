package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
	private int port;
	private List<User> clients;
	private ServerSocket server;

	public List<User> getClients() {
		return this.clients;
	}

	public static void main(String[] args) throws IOException {
		new Server(12345).run();
		System.out.println("end server");
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<User>();
	}

	public void run() throws IOException {
		try {
			this.server = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Port 12345 is now open");
		while (true) {
			Socket client = server.accept();
			Scanner sc = new Scanner(client.getInputStream());
			String command = sc.nextLine();

			if (command.equals("1")) {
				User newUser = new User(client);
				newUser.getOutStream().println("type # + username to login: ");
				new Thread(new UserHandler(this, newUser)).start();
			} else {
				if (command.equals("2")) {
					User newUser = new User(client);
					this.clients.add(newUser);
					newUser.getOutStream().println("type ^ + username + % + password to register: ");

					new Thread(new UserHandler(this, newUser)).start();
				}
			}

		}
	}

	public void sendMessage(User sender, String msg) {
		boolean blocked = false;
		User receiver = sender.getInteractingUser();
		for (User user : receiver.getBlockedUsers()) {
			if (user.equals(sender)) {
				blocked = true;
			}
		}
		if (!blocked) {
			receiver.getOutStream().println(sender.getNickname() + " messaged you: " + msg);
		} else {
			sender.getOutStream().println("You're blocked by " + receiver.getNickname());
		}
	}

	public void removeUser(User user) {
		this.clients.remove(user);
	}

}

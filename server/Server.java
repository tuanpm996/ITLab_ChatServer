package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
	private int port;
	private List<User> clients;
	private ServerSocket server;
	private ArrayList<Socket> sockets;
	private boolean flag;

	public List<User> getClients() {
		return this.clients;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		new Server(12345).run();
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<User>();
		this.flag = true;
		this.sockets = new ArrayList<Socket>();
	}

	public void run() throws IOException, ClassNotFoundException, SQLException {
		try {
			this.server = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new ServerHandler(this)).start();
		System.out.println("Port 12345 is now open");
		while (this.flag) {
			try {
				Socket client = server.accept();
				this.sockets.add(client);
				Scanner sc = new Scanner(client.getInputStream());
				String command = sc.nextLine();

				if (command.equals("1")) {
					User newUser = new User(client);
					this.clients.add(newUser);
					newUser.getOutStream().println("type # + username to login: ");
					new Thread(new UserHandler(this, newUser)).start();
				} else {
					if (command.equals("2")) {
						User newUser = new User(client);
						this.clients.add(newUser);
						newUser.getOutStream().println("type ^ + username to register: ");
						new Thread(new UserHandler(this, newUser)).start();
					} else {
						System.out.println("else");
						User newUser = new User(client);
						this.clients.add(newUser);
						newUser.getOutStream().println("\nChoose what to do:\n1.Sign in 2.Sign up");
						new Thread(new UserHandler(this, newUser)).start();
					}
				}
			} catch (SocketException e) {
				this.flag = false;
				for (Socket socket : sockets) {
					socket.close();
				}
			}
		}
		System.out.println("Server end!");
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void setClients(List<User> clients) {
		this.clients = clients;
	}

}

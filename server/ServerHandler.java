package server;

import java.io.IOException;
import java.util.Scanner;

public class ServerHandler implements Runnable {
	private Server server;

	public ServerHandler(Server server) {
		this.server = server;
	}

	public void run() {
		Scanner sc = new Scanner(System.in);
		String message;
		while (sc.hasNextLine() && !(message = sc.nextLine()).equals("")) {
			if (message.equalsIgnoreCase("QUIT")) {
				try {
					this.server.getServer().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
}

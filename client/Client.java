package client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private String host;
	private int port;
	private String userChatWith;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void setUserChatWith(String name) {
		this.userChatWith = name;
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Client("127.0.0.1", 12345).run();
	}

	private void run() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Socket client = new Socket(host, port);

		new Thread(new ReceivedMessagesHandler(client.getInputStream(), this)).start();

		System.out.println("Connected to server");
		PrintStream output = new PrintStream(client.getOutputStream());

		Scanner sc = new Scanner(System.in);
		System.out.println("Choose what to do: \n1.Sign in 2.Sign up");
		String nickname = sc.nextLine();

		output.println(nickname);

		String message;
		while (sc.hasNextLine() && !(message = sc.nextLine()).equals("")) {
			if (this.userChatWith != null) {
				System.out.println("You message " + this.userChatWith + ": " + message);
			} else {
				System.out.println("You : " + message);
			}
			output.println(message);
		}
		System.out.println("Client end!");
		output.close();
		sc.close();
		client.close();

	}
}

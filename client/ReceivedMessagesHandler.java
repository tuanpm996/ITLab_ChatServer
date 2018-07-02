package client;

import java.io.InputStream;
import java.util.Scanner;

public class ReceivedMessagesHandler implements Runnable {
	private InputStream serverInputStream;
	private Client client;

	public ReceivedMessagesHandler(InputStream serverInputStream, Client client) {
		this.serverInputStream = serverInputStream;
		this.client = client;
	}

	public void run() {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(serverInputStream);
		String message = "";
		while (sc.hasNextLine()) {
			message = sc.nextLine();
			if (message.length() > 0) {
				switch (message.charAt(0)) {
				case '@':
					String userName = message.substring(1);
					this.client.setUserChatWith(userName);
					System.out.println("You now chat with " + userName);
					break;
				default:
					System.out.println(message);
					break;
				}
			}
		}
		sc.close();

	}

}

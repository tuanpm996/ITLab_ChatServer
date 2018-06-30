package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class User {
	public static int nbUser = 0;
	private int userId;
	private PrintStream streamOut;
	private InputStream streamIn;
	private String nickname;
	private Socket client;
	private User interactingUser;
	private boolean isSignedIn;
	private boolean isActive;
	private ArrayList<User> blockedUsers;

	public User(Socket client, String name) throws IOException {
		this.streamOut = new PrintStream(client.getOutputStream());
		this.streamIn = client.getInputStream();
		this.client = client;
		this.nickname = name;
		this.userId = nbUser;
		this.isSignedIn = true;
		this.blockedUsers = new ArrayList<User>();
		User.nbUser += 1;
	}

	public User(Socket client) throws IOException {
		this.streamOut = new PrintStream(client.getOutputStream());
		this.streamIn = client.getInputStream();
		this.client = client;
		this.isSignedIn = false;
		// this.userId = nbUser;
		// this.blockedUsers = new ArrayList<User>();
		// User.nbUser += 1;
	}

	public PrintStream getOutStream() {
		return this.streamOut;
	}

	public InputStream getInputStream() {
		return this.streamIn;
	}

	public String getNickname() {
		return this.nickname;
	}

	public int getUserId() {
		return this.userId;
	}

	public User getInteractingUser() {
		return interactingUser;
	}

	public void setInteractingUser(User interactingUser) {
		this.interactingUser = interactingUser;
	}

	public ArrayList<User> getBlockedUsers() {
		return blockedUsers;
	}

	public void setBlockedUsers(ArrayList<User> blockedUsers) {
		this.blockedUsers = blockedUsers;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isSignedIn() {
		return isSignedIn;
	}

	public void setSignedIn(boolean isSignedIn) {
		this.isSignedIn = isSignedIn;
	}

	public static int getNbUser() {
		return nbUser;
	}

	public static void setNbUser(int nbUser) {
		User.nbUser = nbUser;
	}

	public PrintStream getStreamOut() {
		return streamOut;
	}

	public void setStreamOut(PrintStream streamOut) {
		this.streamOut = streamOut;
	}

	public InputStream getStreamIn() {
		return streamIn;
	}

	public void setStreamIn(InputStream streamIn) {
		this.streamIn = streamIn;
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}

package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserHandler implements Runnable {

	private Server server;
	private User user;

	public UserHandler(Server server, User user) {
		this.server = server;
		this.user = user;
	}

	public void run() {
		String message;

		Scanner sc = new Scanner(this.user.getInputStream());
		while (sc.hasNextLine() && !(message = sc.nextLine()).equals("")) {
			switch (message.charAt(0)) {
			// choose person to chat with
			case '@':
				try {
					int userId = Integer.parseInt(message.substring(1));
					for (User client : this.server.getClients()) {
						if (client.getUserId() == userId) {
							this.user.setInteractingUser(client);
							this.user.getOutStream().println("@" + client.getNickname());
						}
					}
					if (this.user.getInteractingUser() == null) {
						this.user.getOutStream().println("User doesn't exist: ");
					}
				} catch (NumberFormatException e) {
					this.user.getOutStream().println("It's not a id, enter again: ");
				}
				break;

			// get users list
			case '&':
				StringBuilder sb = new StringBuilder();
				sb.append("List users:\n");
				for (User user : this.server.getClients()) {
					if (user.isSignedIn()) {
						sb.append(user.getNickname());
						sb.append("-");
						sb.append(user.getUserId());
						sb.append("\n");
					}
				}
				sb.append("\nType @ + id of person you want to chat with: \n");
				this.user.getOutStream().println(sb.toString());
				break;

			// block
			case '$':
				try {
					int userId = Integer.parseInt(message.substring(1));
					for (User client : this.server.getClients()) {
						if (client.getUserId() == userId) {
							this.user.getBlockedUsers().add(client);
						}
					}
				} catch (NumberFormatException e) {
					this.user.getOutStream().println("It's not a id, enter again: ");
				}
				break;

			// unblock
			case '*':
				try {
					int userId = Integer.parseInt(message.substring(1));
					for (User client : this.server.getClients()) {
						if (client.getUserId() == userId) {
							this.user.getBlockedUsers().remove(client);
						}
					}
				} catch (NumberFormatException e) {
					this.user.getOutStream().println("It's not a id, enter again: ");
				}
				break;

			// sign up
			case '^':
				if (!this.user.isSignedIn()) {
					try {
						User user = this.getUser(message.substring(1), this.user);
						if (user == null) {
							this.insertUser(message.substring(1), this.user);
							this.user = this.getUser(message.substring(1), this.user);
							this.user.setSignedIn(true);
							this.user.setSignedIn(true);
							this.user.getOutStream().println("you registered and login");
							this.getIntroduction(server, user);
						} else {
							this.user.getOutStream().println("you can not register");
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					this.user.getOutStream().println("You logged in so cant register");
				}
				break;

			// sign in
			case '#':
				if (!this.user.isSignedIn()) {
					try {
						User user = this.getUser(message.substring(1), this.user);
						if (user != null && user.isActive()) {
							this.user = user;
							this.user.setNickname(message.substring(1));
							this.user.setSignedIn(true);
							System.out.println(user.getUserId());
							this.user.getOutStream().println("You logged in");
							this.getIntroduction(server, user);
						} else {
							this.user.getOutStream()
									.println("You can't login\nChoose what to do:\n1.Sign in 2.Sign up");
						}

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					this.user.getOutStream().println("You're logged in");

				}
				break;
			case '1':
				this.user.getOutStream().println("type # + username to login: ");
				break;
			case '2':
				this.user.getOutStream().println("type ^ + username + % + password to register: ");
				break;

			// send normal message
			default:
				if (this.user.isSignedIn()) {
					if (this.user.getInteractingUser() != null) {
						this.server.sendMessage(this.user, message);
					} else {
						this.user.getOutStream().println("You must select people to chat with");
					}
				} else {
					this.user.getOutStream().println("You are not loggin");
				}

				break;
			}
		}
		System.out.println("End user handler thread");
		User.nbUser -= 1;
		server.removeUser(user);
		sc.close();
	}

	public static User getUser(String username, User user) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Chat", "root", "abcd1234");
		java.sql.Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery("select * from User where username='" + username + "'");

		int userId;
		boolean isActive;
		while (rs.next()) {// Di chuyển con trỏ xuống bản ghi kế tiếp.
			userId = rs.getInt("id");
			isActive = rs.getBoolean("isActive");
			user.setActive(isActive);
			user.setUserId(userId);
			return user;
		}
		return null;
	}

	public static int insertUser(String username, User user) throws ClassNotFoundException, SQLException {
		System.out.println("insert");
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Chat", "root", "abcd1234");
		java.sql.Statement stm = conn.createStatement();
		int rs = stm.executeUpdate("insert into User (username) VALUES ('" + username + "')");
		return rs;
	}

	private void getIntroduction(Server server, User user) {
		StringBuilder sb = new StringBuilder();
		sb.append("Hello ");
		sb.append(user.getNickname());
		sb.append("\nList users:\n");
		for (User client : server.getClients()) {
			if (client.isSignedIn()) {
				sb.append(client.getNickname());
				sb.append("-");
				sb.append(client.getUserId());
				sb.append("\n");
			}
		}
		sb.append("Type @ + id of person you want to chat with: \n");
		sb.append("Type & to know users list: \n");
		sb.append("Type $ + id of person you want to block: \n");
		sb.append("Type * + id of person you want to unblock: \n");
		user.getOutStream().println(sb.toString());
	}
}

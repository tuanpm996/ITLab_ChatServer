package Server;

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
					sb.append(user.getNickname());
					sb.append("-");
					sb.append(user.getUserId());
					sb.append("\n");
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

			// block
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
							User userInsert = this.insertUser(message.substring(1), this.user);
							this.user = userInsert;
							this.user.getOutStream().println("you registered");
						} else {
							this.user.getOutStream().println("you can not register");
						}
						this.user.getOutStream().println("you registered");
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
				// System.out.println(message);
				break;

			// sign in
			case '#':
				this.user.setNickname(message.substring(1));
				this.user.setSignedIn(true);
				// System.out.println(message);
				try {
					User user = this.getUser(message.substring(1), this.user);
					System.out.println(message.substring(1));
					System.out.println(user.isActive());
					if (user != null && user.isActive() && !user.isSignedIn()) {
						this.user = user;
						System.out.println(user.getUserId());
						this.user.getOutStream().println("You logged in");
					} else {
						this.user.getOutStream().println("You can't login\nChoose what to do: \\n1.Sign in 2.Sign up");
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			System.out.println(isActive);
			user.setActive(isActive);
			user.setUserId(userId);
		}
		return user;
	}

	public static User insertUser(String username, User user) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Chat", "root", "abcd1234");
		java.sql.Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery("insert into User (username) values ('" + username + "')");

		int userId;
		boolean isActive;
		while (rs.next()) {// Di chuyển con trỏ xuống bản ghi kế tiếp.
			userId = rs.getInt("id");
			isActive = rs.getBoolean("isActive");
			System.out.println(isActive);
			user.setActive(isActive);
			user.setUserId(userId);
		}
		return user;
	}
}

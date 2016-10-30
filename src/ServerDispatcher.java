import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Отправка сообщений всем или конкретным пользователям.
 * 
 */
public class ServerDispatcher extends Thread{
	private ConcurrentHashMap<String, Socket> UserAll;
	private Socket socket;
	private String name;
	public DataInputStream in;
	public DataOutputStream out;

	public ServerDispatcher(Socket sock, ConcurrentHashMap<String, Socket> user) {
		UserAll = user;
		socket = sock;
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (true) {
				String line = in.readUTF();
				name = line;
				UserAll.put(name, socket);
				line = in.readUTF();
				if (line.contains("@quit")) {
					SendAll(name + "ran away");
					UserAll.remove(name);
				} else if (line.contains("@senduser")) {
					int begin = line.indexOf(" ", "@senduser".length() + 1);
					int end = line.indexOf("[");
					String mes = line.substring(end + 1);
					String names = line.substring(begin, end);
					for (String log : UserAll.keySet()) {
						if (names.contains(log)) {
							new DataOutputStream(UserAll.get(log).getOutputStream()).writeUTF(mes);
							;
						}
					}
				}
			}
		} catch (IOException e) {
		}
	}

	private void SendAll(String line) {
		for (Socket socket : UserAll.values())
			if (!socket.equals(socket))
				try {
					new DataOutputStream(socket.getOutputStream()).writeUTF(line);
				} catch (IOException e) {
				}
	}

}

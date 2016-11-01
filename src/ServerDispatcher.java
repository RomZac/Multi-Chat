import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerDispatcher extends Thread {
	private ConcurrentHashMap<String, Socket> UserAll;
	private Socket socket;
	private String nameCl;
	public DataInputStream in;
	public DataOutputStream out;
	DataOutputStream output;

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
			String line = in.readUTF();
			nameCl = line;
			UserAll.put(nameCl, socket);
			System.out.println("User: " + nameCl + " is connected");
			while (true) {
				line = in.readUTF();
				if (line.contains("@quit")) {
					SendAll(nameCl + " ran away");
					System.out.println("User" + nameCl + " disconneted ");
					socket.close();
					UserAll.remove(nameCl);
				} else if (line.contains("@senduser")) {
					int end = line.indexOf(" ", "@senduser".length() + 1);
					String name = line.substring("@senduser".length() + 1, end);
					line = line.substring(end + 1);
					if (UserAll.containsKey(name))
						new DataOutputStream(UserAll.get(name).getOutputStream()).writeUTF(nameCl + ": " + line);
					else
						out.writeUTF(name + " is not online.");
				} else
					SendAll(nameCl + ": " + line);
			}
		} catch (IOException e) {
		}
	}

	private void SendAll(String line) throws IOException {
		for (Socket sock : UserAll.values())
			if (!sock.equals(socket)) {
				new DataOutputStream(sock.getOutputStream()).writeUTF(line);
			}
	}

}

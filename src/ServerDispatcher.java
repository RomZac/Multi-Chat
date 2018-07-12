import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerDispatcher extends Thread {
	private ConcurrentHashMap<String, Socket> UserAll;
	private ConcurrentHashMap<String, int[]> table;
	private Socket socket;
	private String nameCl;
	public DataInputStream in;
	public DataOutputStream out;
	DataOutputStream output;

	public ServerDispatcher(Socket sock, ConcurrentHashMap<String, Socket> user, ConcurrentHashMap<String, int[]> tab) {
		UserAll = user;
		table = tab;
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

				} else if (line.contains("@show")) {
					new DataOutputStream(socket.getOutputStream()).writeUTF("\n");
					for (Map.Entry<String, int[]> entry : table.entrySet()) {
						String str = "Name: " + entry.getKey() + "\tMarks:";
						for (int i = 0; i < 5; i++) {
							str += entry.getValue()[i] + " ";
						}
						new DataOutputStream(socket.getOutputStream()).writeUTF(str);
					}
				} else if (line.contains("@set")) {
					try {
						int end_name = line.indexOf(" ", "@set".length() + 1);
						int end_exam = line.indexOf(" ", end_name + 1);

						String name = line.substring("@set".length() + 1, end_name);
						int exam = Integer.parseInt(line.substring(end_name + 1, end_exam));
						int grade = Integer.parseInt(line.substring(end_exam + 1));

						if (exam > 0 && exam < 6 && grade > -1 && grade < 10 && table.containsKey(name)) {
							for (Map.Entry<String, int[]> entry : table.entrySet()) {
								if (entry.getKey().equals(name)) {
									int _mark[] = entry.getValue();
									_mark[exam - 1] = grade;
									entry.setValue(_mark);
									break;
								}
							}
						}
					} catch (Exception e) {
						new DataOutputStream(socket.getOutputStream()).writeUTF("Error message");
					}

				} else if (line.contains("@add")) {// name exam_marks
					try {
						int end_name = line.indexOf(" ", "@set".length() + 1);
						int[] marks = new int[5];
						String name = line.substring("@set".length() + 1, end_name);
						line += " ";
						try {
							line = line.substring(end_name + 1);
							for (int r = 0; r < 5; r++) {
								int index = line.indexOf(" ");
								String m = line.substring(0, index);
								if (Integer.parseInt(m) > -1 && Integer.parseInt(m) < 10)
									marks[r] = Integer.parseInt(m);
								else {
									new DataOutputStream(socket.getOutputStream()).writeUTF("Error MArks");
									for (int i = 0; i < 5; i++)
										marks[i] = 0;
									break;
								}
								line = line.substring(index + 1);
							}
						} catch (Exception e) {
							for (int i = 0; i < 5; i++)
								marks[i] = 0;
							new DataOutputStream(socket.getOutputStream()).writeUTF("Not MArks");
						}
						table.put(name, marks);
					} catch (Exception e) {
						new DataOutputStream(socket.getOutputStream()).writeUTF("Not MArks");
					}

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

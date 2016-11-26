import java.io.*;
import java.net.*;

public class Client {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private String name;
	private BufferedReader keyboard;
	private Thread listener;

	public Client(String adr, int port) throws IOException {
		InetAddress ipAddress = InetAddress.getByName(adr);
		socket = new Socket(ipAddress, port);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		keyboard = new BufferedReader(new InputStreamReader(System.in));
		listener = new Thread(new FromServer());
		listener.start();
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome!");
		new Client(args[1], Integer.parseInt(args[0])).run();
	}

	public void run() {
		try {
			System.out.print("Login: ");
			name = keyboard.readLine();
			out.writeUTF(name);
			out.flush();
			while (true) {
				String line;
				System.out.print("Message: ");
				line = keyboard.readLine();
				if (socket.isClosed()) {
					break;
				}
				out.writeUTF(line);
				out.flush();
				if (line.equals("@quit")) {
					socket.close();
					break;
				}
				if (line.contains("@name"))
					name = line.substring("@name".length() + 1);
			}
		} catch (IOException x) {
			x.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	private class FromServer implements Runnable {

		public void run() {
			String line;
			try {
				while (true) {
					line = in.readUTF();
					System.out.println(line);
				}
			} catch (IOException e) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
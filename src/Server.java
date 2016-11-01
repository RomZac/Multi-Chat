import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
	private ConcurrentHashMap<String, Socket> user = new ConcurrentHashMap<>();
	private ServerSocket ser_socket;
	private Socket socket;

	public Server(int port) throws IOException {
		ser_socket = new ServerSocket(port);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Server on");
		new Server(Integer.parseInt(args[0])).run();
	}

	public void run() throws IOException {
		while (true) {
			socket = ser_socket.accept();
			//System.out.println("User is connected");
			ServerDispatcher client = new ServerDispatcher(socket, user);
			client.start();
		}
	}
}

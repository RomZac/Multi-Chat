import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
	private ConcurrentHashMap<String, Socket> user = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, int[]> table = new ConcurrentHashMap<>();
	private ServerSocket ser_socket;
	private Socket socket;
	/*
	int backlog,
    InetAddress bindAddr)*/
	public Server(int port) throws IOException {
		ser_socket = new ServerSocket(port);
		Scanner input = new Scanner(new FileReader("Exam.txt"));
		while (input.hasNextLine()) {
			String name = input.nextLine();
			int[] mark = new int[5];
			int end = 0;
			for(int i =0; i <5; i++){
				end = name.indexOf(" ", end);
				mark[i] = Integer.parseInt(name.substring(end + 1, end + 2));
				end++;
			}
			name = name.substring(0, name.indexOf(" ", 0));
			table.put(name, mark);
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Server on");
		System.out.println("Instructions:");
		System.out.println("By default, a message is sent to all participants in the chat");
		System.out.println("Teams:");
		System.out.println("@senduser nameUser - Send message user");
		System.out.println("@show - See marks of exams");
		System.out.println("@set name exam_num grade - Changes evaluation of exam");	
		System.out.println("@add name exam_marks - ");	
		new Server(Integer.parseInt(args[0])).run();
	}

	public void run() throws IOException {
		while (true) {
			socket = ser_socket.accept();
			// System.out.println("User is connected");
			ServerDispatcher client = new ServerDispatcher(socket, user, table);
			client.start();
		}
	}
}

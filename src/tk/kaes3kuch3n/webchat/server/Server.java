package tk.kaes3kuch3n.webchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();
	
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	private final int MAX_ATTEMPTS = 5;
	
	private boolean raw = false;
	
	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		run = new Thread(this, "Server");
		run.start();
	}
	
	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
		Scanner scanner = new Scanner(System.in);
		while(running) {
			String text = scanner.nextLine();
			if(!text.startsWith("/")) {
				sendToAll("/m/Server: " + text + "/e/");
				continue;
			}
			text = text.substring(1);
			if(text.equals(raw)) {
				if(raw) System.out.println("Raw mode off.");
				else System.out.println("Raw mode on.");
				raw = !raw;
			}
			else if(text.equals("who") | text.equals("online") | text.equals("clients")) {
				System.out.println("Connected clients: " + clients.size());
				System.out.println("=========================");
				for(int i = 0; i < clients.size(); i++) {
					ServerClient c = clients.get(i);
					System.out.println(c.user + " (" + c.getID() + ") @ " + c.address + ":" + c.port);
				}
				System.out.println("=========================");
			}
			else if(text.startsWith("kick")) {
				String user = text.split(" ")[1];
				boolean number = true;
				int id = -1;
				try{
					id = Integer.parseInt(user);
				}
				catch (NumberFormatException e) {
					number = false;
				}
				if(number){
					boolean exists = false;
					for(int i = 0; i < clients.size(); i++) {
						if(clients.get(i).getID() == id) {
							exists = true;
							break;
						}
					}
					if(exists) disconnect(id, true);
					else System.out.println("Client " + id + "doesn't exist! Check ID number.");
				}
				else {
					for(int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if(user.equals(c.user)) {
							disconnect(c.getID(), true);
							break;
						}
					}
				}
			}
			else if(text.equals("stop")) stop();
			else if(text.equals("help") | text.equals("?")) printHelp();
			else System.out.println("Unnown command. Type /help or /? for a list of commands.");;
		}
		scanner.close();
	}
	
	private void printHelp() {
		System.out.println("===============Help===============");
		System.out.println("/raw - Enables raw mode");
		System.out.println("/who | /online | /clients - Shows a list of online users");
		System.out.println("/kick [user|ID] - Kicks the specified user from the server");
		System.out.println("/stop - Stops the server");
		System.out.println("/help | /? - Shows the help message");
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while(running) {
					sendToAll("/i/server");
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if(!clientResponse.contains(clients.get(i).getID())) {
							if(c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							}
							else {
								c.attempt++;
							}
						}
						else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}
	
	private void sendStatus() {
		if(clients.size() <= 0) return;
		String users = "/u/";
		for(int i = 0; i < clients.size() - 1; i++) {
			users += clients.get(i).user + " (ID: " + clients.get(i).getID() + ")/n/";
		}
		users += clients.get(clients.size() -1).user + " (ID: " + clients.get(clients.size() - 1).getID() + ")/e/";
		sendToAll(users);
	}
	
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (SocketException e) {
						System.out.println("Stopped server");
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
			
		};
		receive.start();
	}
	
	private void sendToAll(String message) {
		if (message.startsWith("/m/")) {
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(text);
		}
		for(int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message, final InetAddress address, int port) {
		message += "/e/";
		send(message.getBytes(), address, port);
	}
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if (raw) System.out.println(string);
		if(string.startsWith("/c/")) {
			//UUID id = UUID.randomUUID();
			int id = UniqueIdentifiers.getIdentifier();
			String user = string.split("/c/|/e/")[1];
			System.out.println(user + " (" + id + ") connected to the server");
			clients.add(new ServerClient(user, packet.getAddress(), packet.getPort(), id));
			String ID = "/c/" + id;
			send(ID, packet.getAddress(), packet.getPort());
		}
		else if(string.startsWith("/m/")) {
			sendToAll(string);
		}
		else if(string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
		}
		else if(string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
;		}
		else {
			System.out.println(string);
		}
	}
	
	private void stop() {
		for(int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}
		running = false;
		socket.close();
	}
	
	private void disconnect(int id, boolean status) {
		ServerClient c = null;
		boolean existed = false;
		for(int i = 0; i < clients.size(); i++) {
			if(clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				String message = "/d/Disconnected from server";
				send(message, c.address, c.port);
				existed = true;
				break;
			}
		}
		if(existed){
			String message = "";
			if(status) {
				message = "Client " + c.user + " (" + c.getID() + ") @ " + c.address + ":" + c.port + " disconnected!";
			}
			else {
				message = "Client " + c.user + " (" + c.getID() + ") @ " + c.address + ":" + c.port + " timed out!";
			}
			System.out.println(message);
		}
	}
	
}

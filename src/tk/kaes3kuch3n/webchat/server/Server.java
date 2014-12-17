package tk.kaes3kuch3n.webchat.server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {
	
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	
	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		run = new Thread(this, "Server");
	}
	
	public void run() {
		running = true;
		manageClients();
		receive();
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while(running) {
					//Client Management
				}
			}
		};
		manage.start();
	}
	
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				//Receiving packets
			}
		};
		receive.start();
	}
	
}

package tk.kaes3kuch3n.webchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	
	private String user;
	private String address;
	private int port;
	
	private DatagramSocket socket;
	private InetAddress ip;
	
	private Thread send;
	
	public Client(String user, String address, int port) {
		this.user = user;
		this.address = address;
		this.port = port;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getAddress() {
		return address;
	}
	
	//Open a new connection to a server
	public boolean openConnection(String address) {
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//Receive data
	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String msg = new String(packet.getData());
		return msg;
	}
	
	//Send data
	public void send(final byte[] data) {
		send= new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	
	
	
	
}

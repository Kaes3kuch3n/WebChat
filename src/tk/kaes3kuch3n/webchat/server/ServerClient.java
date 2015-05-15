package tk.kaes3kuch3n.webchat.server;

import java.net.InetAddress;

public class ServerClient {
	
	public String user;
	public InetAddress address;
	public int port;
	private final int ID;
	public int attempt = 0;
	
	public ServerClient(String user, InetAddress address, int port, final int ID) {
		this.user = user;
		this.address = address;
		this.port = port;
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}
	
}

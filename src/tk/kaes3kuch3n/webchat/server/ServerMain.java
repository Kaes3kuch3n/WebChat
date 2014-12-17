package tk.kaes3kuch3n.webchat.server;

public class ServerMain {
	
	private int port;
	private Server server;
	
	public ServerMain(int port) {
		this.port = port;
		server = new Server(port);
	}
	
	public static void main(String[] args) {
		int port;
		if(args.length != 1) {
			System.out.println("Too many args!");
			return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
	
}

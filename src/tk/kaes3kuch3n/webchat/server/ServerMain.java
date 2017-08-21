package tk.kaes3kuch3n.webchat.server;

@SuppressWarnings("unused")
public class ServerMain {
	
	private int port;
	private Server server;
	
	public ServerMain(int port) {
		this.port = port;
		server = new Server(port);
	}
	
	public static void main(String[] args) {
		int port;
		if(args.length == 0) {
			port = 9811;
		}
		
		else if(args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		else {
			System.err.println("Usage: java -jar WebChat_Server.jar [port]");
			return;
		}
		new ServerMain(port);
	}
	
}

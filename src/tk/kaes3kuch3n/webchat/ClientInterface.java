package tk.kaes3kuch3n.webchat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientInterface extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	private final int SIZEX = 600;
	private final int SIZEY = 500;
	
	private JPanel contentPane;
	private JTextField txtMsg;
	private JTextArea history;
	private DefaultCaret caret;
	private Thread run, listen;
	private Client client;
	
	private boolean running;
	private JMenuBar menuBar;
	private JMenu mnOptions;
	private JMenuItem mntmOnlineUsers;
	private JMenuItem mntmQuit;
	
	private OnlineUsers users;
	
	public ClientInterface(String user, String address, int port) {
		client = new Client(user, address, port);
		boolean connected = client.openConnection(address);
		if(!connected) {
			System.err.println("Connection failed!");
			console("Connection to " + address + ":" + port + "failed!");
		}
		createWindow();
		console("Attempting a connection to " + address + ":" + port + " as " + user);
		String connection = "/c/" + client.getUser() + "/e/";
		client.send(connection.getBytes());
		users = new OnlineUsers(this);
		run = new Thread(this, "Running");
		running = true;
		run.start();
	}
	
private void createWindow() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setTitle("WebChat - " + client.getUser());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SIZEX, SIZEY);
		setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		menuBar.setBackground(Color.LIGHT_GRAY);
		setJMenuBar(menuBar);
		
		mnOptions = new JMenu("Options");
		mnOptions.setBackground(Color.LIGHT_GRAY);
		menuBar.add(mnOptions);
		
		mntmOnlineUsers = new JMenuItem("Online Users");
		mntmOnlineUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				users.setVisible(true);
			}
		});
		mntmOnlineUsers.setBackground(Color.WHITE);
		mnOptions.add(mntmOnlineUsers);
		
		mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mntmQuit.setBackground(Color.WHITE);
		mnOptions.add(mntmQuit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{30, 515, 50, 5};
		gbl_contentPane.rowHeights = new int[]{30, 380, 90};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		JScrollPane scroll = new JScrollPane(history);
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.weightx = 1;
		scrollConstraints.weighty = 1;
		scrollConstraints.insets = new Insets(8, 5, 5, 5);
		contentPane.add(scroll, scrollConstraints);
		
		txtMsg = new JTextField();
		GridBagConstraints gbc_txtMsg = new GridBagConstraints();
		gbc_txtMsg.insets = new Insets(0, 0, 0, 5);
		gbc_txtMsg.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMsg.gridx = 0;
		gbc_txtMsg.gridy = 2;
		gbc_txtMsg.gridwidth = 2;
		gbc_txtMsg.weightx = 1;
		gbc_txtMsg.weighty = 0;
		contentPane.add(txtMsg, gbc_txtMsg);
		txtMsg.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMsg.getText(), true);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		gbc_btnSend.weightx = 0;
		gbc_btnSend.weighty = 0;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect = "/d/" + client.getID() + "/e/";
				send(disconnect, false);
				client.close();
				running = false;
			}
		});
		
		setVisible(true);
		
		txtMsg.requestFocusInWindow();
		this.getRootPane().setDefaultButton(btnSend);
	}
	
	public int getPosX() {
		return getLocationOnScreen().x;
	}
	
	public int getPosY() {
		return getLocationOnScreen().y;
	}
	
	public int getSizeX() {
		return SIZEX;
	}
	
	public int getSizeY() {
		return SIZEY;
	}
	
	public void run() {
		listen();
	}
	
	private void send(String message, boolean text) {
		if(message.equals("")) return;
		if (text) {
			message = client.getUser() + ": " + message;
			message = "/m/" + message + "/e/";
			txtMsg.setText("");
		}
		client.send(message.getBytes());
	}
	
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while(running) {
					String message = client.receive();
					if(message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Succesfully connected to server as " + client.getUser() + "! ID: " + client.getID());
					}
					else if(message.startsWith("/m/")) {
						String text = message.substring(3);
						text = text.split("/e/")[0];
						console(text);
					}
					else if(message.startsWith("/i/")) {
						String text = "/i/" + client.getID() + "/e/";
						send(text, false);
					}
					else if(message.startsWith("/d/")) {
						String text = message.substring(3);
						text = text.split("/e/")[0];
						console(text);
						client.close();
						running = false;
					}
					else if(message.startsWith("/u/")) {
						String[] u = message.split("/u/|/n/|/e/");
						users.update(Arrays.copyOfRange(u, 1, u.length - 1));
					}
				}
			}
		};
		listen.start();
	}
	
	public void console(String msg) {
		history.append(msg + "\n");
		history.setCaretPosition(history.getDocument().getLength());
	}
}

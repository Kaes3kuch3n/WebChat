package tk.kaes3kuch3n.webchat;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import sun.font.CreatedFontTracker;

import java.awt.GridBagLayout;

import javax.swing.JTextArea;

import java.awt.GridBagConstraints;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Client extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private String user;
	private String ip;
	private int port;
	private JTextField txtMsg;
	private JTextArea history;
	private DefaultCaret caret;
	
	public Client(String user, String ip, int port) {
		this.user = user;
		this.ip = ip;
		this.port = port;
		createWindow();
		console("Successfully connected to " + ip + ":" + port);
		console("Welcome, " + user + "!");
	}
	
	private void createWindow() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setTitle("WebChat - " + user);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{30, 515, 50, 5};
		gbl_contentPane.rowHeights = new int[]{30, 380, 90};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
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
		scrollConstraints.insets = new Insets(8, 5, 5, 5);
		contentPane.add(scroll, scrollConstraints);
		
		txtMsg = new JTextField();
		GridBagConstraints gbc_txtMsg = new GridBagConstraints();
		gbc_txtMsg.insets = new Insets(0, 0, 0, 5);
		gbc_txtMsg.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMsg.gridx = 0;
		gbc_txtMsg.gridy = 2;
		gbc_txtMsg.gridwidth = 2;
		
		contentPane.add(txtMsg, gbc_txtMsg);
		txtMsg.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMsg.getText());
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		setVisible(true);
		
		txtMsg.requestFocusInWindow();
		this.getRootPane().setDefaultButton(btnSend);
		this.history = history;
	}
	
	private void send(String msg) {
		if(msg.equals("")) return;
		console(user + ": " + msg);
		txtMsg.setText("");
	}
	
	public void console(String msg) {
		history.append(msg + "\n");
		history.setCaretPosition(history.getDocument().getLength());
	}
	
}

package tk.kaes3kuch3n.webchat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Login extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtIp;
	private JLabel lblIp;
	private JTextField txtPort;
	private JLabel lblPort;
	private JLabel lblBspIp;
	private JLabel lblBspPort;
	
	public Login() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Chat Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(250, 400);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(25, 45, 200, 28);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(89, 25, 71, 16);
		contentPane.add(lblUsername);
		
		txtIp = new JTextField();
		txtIp.setBounds(25, 120, 200, 28);
		contentPane.add(txtIp);
		txtIp.setColumns(10);
		
		lblIp = new JLabel("IP-Adresse:");
		lblIp.setBounds(84, 100, 81, 16);
		contentPane.add(lblIp);
		
		txtPort = new JTextField();
		txtPort.setBounds(25, 220, 200, 28);
		contentPane.add(txtPort);
		txtPort.setColumns(10);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(106, 200, 38, 16);
		contentPane.add(lblPort);
		
		lblBspIp = new JLabel("(z.B. 127.0.0.1)");
		lblBspIp.setBounds(78, 150, 94, 16);
		contentPane.add(lblBspIp);
		
		lblBspPort = new JLabel("(z.B. 55656)");
		lblBspPort.setBounds(84, 250, 81, 16);
		contentPane.add(lblBspPort);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(66, 325, 117, 29);
		contentPane.add(btnLogin);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

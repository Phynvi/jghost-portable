package org.whired.ghostclient.awt;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.whired.ghostclient.client.GhostUI;

public class ConnectDialog extends JDialog {

	private final Image bgImg;

	private final JPanel contentPanel = new JPanel();
	private JPasswordField txtPass;
	private JTextField txtPort;
	private JTextField txtIp;
	private boolean canceled = true;
	private String ip;
	private char[] password;
	private int port;

	/**
	 * Create the dialog.
	 */
	public ConnectDialog(GhostUI ui) {
		setUndecorated(true);
		setModal(true);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(230, 102);
		bgImg = ui.getBackgroundImage();
		this.setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				if (bgImg != null) {
					g.drawImage(bgImg, 0, 0, this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), this);
				}
			};
		});
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setOpaque(false);
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(new GridLayout(3, 1, 2, 4));
		{
			txtIp = new JTextField();
			txtIp.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					txtIp.selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (txtIp.getText().length() == 0) {
						txtIp.setText("IP");
					}
				}
			});
			txtIp.setText("IP");
			txtIp.setOpaque(false);
			txtIp.setBorder(ui.getBorder());
			txtIp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					validateAndClose();
				}

			});
			contentPanel.add(txtIp);
			txtIp.setColumns(10);
		}
		{
			txtPort = new JTextField();
			txtPort.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					txtPort.selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (txtPort.getText().length() == 0) {
						txtPort.setText("Port");
					}
				}
			});
			txtPort.setText("Port");
			txtPort.setOpaque(false);
			txtPort.setBorder(ui.getBorder());
			txtPort.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					validateAndClose();
				}

			});
			contentPanel.add(txtPort);
			txtPort.setColumns(10);
		}
		{
			txtPass = new JPasswordField();
			txtPass.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					txtPass.selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (txtPass.getPassword().length == 0) {
						txtPass.setText("password");
					}
				}
			});
			txtPass.setText("password");
			txtPass.setOpaque(false);
			txtPass.setBorder(ui.getBorder());
			txtPass.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					validateAndClose();
				}

			});
			contentPanel.add(txtPass);
			txtPass.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.setOpaque(false);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JLabel btnOk = new JLabel("OK");
				btnOk.setBorder(ui.getBorder());
				btnOk.setOpaque(false);
				btnOk.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						validateAndClose();
					}
				});
				buttonPane.add(btnOk);
			}
			{
				JLabel btnCancel = new JLabel("Cancel");
				btnCancel.setBorder(ui.getBorder());
				btnCancel.setOpaque(false);
				btnCancel.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						setVisible(false);
					}
				});
				buttonPane.add(btnCancel);
			}
		}
	}

	public void validateAndClose() {
		try {
			try {
				port = Integer.parseInt(txtPort.getText());
			}
			catch (NumberFormatException n) {
				throw new Throwable("port");
			}
			ip = txtIp.getText();
			if (ip.length() == 0) {
				throw new Throwable("ip");
			}
			password = txtPass.getPassword();
			if (password.length == 0) {
				throw new Throwable("password");
			}
			canceled = false;
			setVisible(false);
		}
		catch (Throwable t) {
			JOptionPane.showMessageDialog(this, "The " + t.getMessage() + " is invalid", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean isCancelled() {
		return canceled;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public char[] getPassword() {
		return password;
	}
}

package org.whired.ghost.client.ui;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.whired.ghost.net.reflection.Accessor;

public class ReflectionPacketBuilder extends JDialog {
	private JList choiceList;
	private DefaultListModel listModel;
	private Accessor selectedAccessor;
	public boolean fromDoneButton = false;

	public ReflectionPacketBuilder(String title, final Frame owner) {
		super(owner, title, true);
		listModel = new DefaultListModel();
		choiceList = new JList(listModel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		choiceList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectedAccessor = (Accessor) choiceList.getSelectedValue();
					setVisible(false);
				}
			}
		});
		JButton btnDone = new JButton("Done");
		btnDone.setSize(80, 22);
		btnDone.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		btnDone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectedAccessor = (Accessor) choiceList.getSelectedValue();
				fromDoneButton = true;
				setVisible(false);
			}
		});

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		add(new JScrollPane(choiceList));
		add(btnDone);
		setSize(400, 550);
		setLocationRelativeTo(owner);
	}

	public void addChoice(Accessor choice) {
		listModel.addElement(choice);
	}

	public void clearChoices() {
		listModel.clear();
	}

	public void setList(java.util.ArrayList<org.whired.ghost.net.reflection.Accessor> list) {
		clearChoices();
		for (Accessor a : list) {
			addChoice(a);
		}
	}

	public Accessor getSelectedAccessor() {
		return this.selectedAccessor;
	}
}

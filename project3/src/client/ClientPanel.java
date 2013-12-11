package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class ClientPanel {
	
	private static JFrame clientPanel;
	private static JPanel buttonPanel;
	private static JButton input;
	private static JButton output;
	private static JButton transfer;
	private static JTextArea showFiles;
	
	public static void main(String[] args) {
		loadGUI();
	}
	
	private static void loadGUI() {
		/*
		 * Set up main panel
		 */
		clientPanel = new JFrame("Client File Transfer Control Panel");
		clientPanel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		clientPanel.setMaximumSize(new Dimension(400, 720));
		setUpButtonPanel();
		
		clientPanel.add(clientPanel, BorderLayout.CENTER);
		
		clientPanel.validate();
		clientPanel.pack();
		clientPanel.setVisible(true);
	}
	
	private static void setUpButtonPanel() {
		buttonPanel.setLayout(new GridLayout(1, 3));
		input = new JButton("Add input files or directory");
		output = new JButton("Choose output directory");
		transfer = new JButton("Transfer");
		buttonPanel.add(input);
		buttonPanel.add(output);
		buttonPanel.add(transfer);
	}
	
	
}

package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class ClientPanel {
	
	private JFrame clientFrame;
	private JPanel buttonPanel;
	private JPanel textPanel;
	private JPanel selectPanel;
	private JPanel controlPanel;
	private JButton input;
	private JButton output;
	private JButton transfer;
	private JTextArea showResults;
	private JComboBox<String> fileToGet;
	private static final String GET_FILE="Please choose a file to retrieve";
	
	public static void main(String args[]) {
		ClientPanel clientPanel = new ClientPanel();
	}
	
	public ClientPanel() {
		loadGUI();
	}
	
	private void loadGUI() {
		/*
		 * Set up main panel
		 */
		clientFrame = new JFrame("Client File Transfer Control Panel");
		clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		clientFrame.setMaximumSize(new Dimension(600, 720));
		buttonPanel = new JPanel();
		textPanel = new JPanel();
		selectPanel = new JPanel();
		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		
		setUpButtonPanel();
		setUpTextPanel();
		setUpSelectBox();
		
		// controlPanel.add(textPanel, BorderLayout.NORTH);
		textPanel.add(selectPanel, BorderLayout.SOUTH);
		controlPanel.add(textPanel, BorderLayout.NORTH);
		controlPanel.add(buttonPanel, BorderLayout.CENTER);
		clientFrame.add(controlPanel, BorderLayout.CENTER);
		
		clientFrame.validate();
		clientFrame.pack();
		clientFrame.setVisible(true);
	}

	private void setUpSelectBox(){
		fileToGet = new JComboBox<String>();
		fileToGet.setEditable(false);
		fileToGet.addItem(GET_FILE);
		selectPanel.add(fileToGet, BorderLayout.NORTH);
	}

	private void setUpTextPanel() {
		showResults = new JTextArea();
		showResults.setLineWrap(true);
		showResults.setEditable(false);
		JScrollPane scrollTextArea = new JScrollPane(showResults,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		textPanel.add(scrollTextArea, BorderLayout.NORTH);
	}
	
	/**
	 * Set up the buttons
	 */
	private void setUpButtonPanel() {
		buttonPanel.setLayout(new GridLayout(3, 1));
		input = new JButton("Add input files or directory to share");
		output = new JButton("Choose output directory");
		transfer = new JButton("Transfer");
		buttonPanel.add(input);
		buttonPanel.add(output);
		buttonPanel.add(transfer);
	}
	
	
}

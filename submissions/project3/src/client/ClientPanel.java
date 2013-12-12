package client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClientPanel implements ClientView {
	
	private static final String GET_FILE = "Please choose a file to retrieve";
	private static final int NAMES_PER_LINE = 3;
	
	private JFrame clientFrame;
	private JPanel buttonPanel;
	private JPanel displayPanel;
	private JPanel selectPanel;
	private JPanel controlPanel;
	private JPanel receiveControlPanel;
	private JPanel receivePanel;
	private JPanel statusPanel;
	private JPanel receiveSubPanel1;
	private JPanel receiveSubPanel2;
	private JButton input;
	private JButton output;
	private JButton receive;
	private JTextArea showResults;
	private JTextArea showStatus;
	private JComboBox<String> fileToGet;
	private JProgressBar progressBar;
	
	private FileReceiverTask fileReceiver;
	
	public ClientPanel() {
		this.fileReceiver = null;
		loadGUI();
	}

	private void loadGUI() {
		clientFrame = new JFrame("Client File Transfer Control Panel");
		clientFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ClientMain.terminate();
			}
		});
		clientFrame.setMaximumSize(new Dimension(1920, 1080));
		buttonPanel = new JPanel();
		selectPanel = new JPanel();
		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		displayPanel = new JPanel();
		displayPanel.setLayout(new BorderLayout());
		receivePanel = new JPanel();
		receivePanel.setLayout(new BorderLayout());
		receiveControlPanel = new JPanel();
		receiveControlPanel.setLayout(new BorderLayout());
		statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());

		setUpButtonPanel();
		setUpSelectBox();
		setUpDisplayPanel();
		setUpStatusPanel();
		setUpReceivePanel();

		receiveControlPanel.add(receivePanel, BorderLayout.WEST);
		receiveControlPanel.add(displayPanel, BorderLayout.CENTER);
		controlPanel.add(buttonPanel, BorderLayout.NORTH);
		controlPanel.add(receiveControlPanel, BorderLayout.CENTER);
		clientFrame.add(controlPanel, BorderLayout.CENTER);

		clientFrame.validate();
		clientFrame.pack();
		clientFrame.setVisible(true);
		
		// add action listener
		input.addActionListener(new InputDirectoryChooser());
		output.addActionListener(new OutputDirectoryChooser());
		receive.addActionListener(new Receive());
		
	}

	private void setUpSelectBox() {
		fileToGet = new JComboBox<String>();
		fileToGet.setEditable(false);
		fileToGet.addItem(GET_FILE);
		selectPanel.setLayout(new BorderLayout());
		selectPanel.add(fileToGet, BorderLayout.CENTER);
	}

	private void setUpDisplayPanel() {
		showResults = new JTextArea();
		showResults.setLineWrap(true);
		showResults.setEditable(false);
		JScrollPane scrollTextArea = new JScrollPane(showResults,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		displayPanel.add(scrollTextArea, BorderLayout.CENTER);
	}
	
	private void setUpStatusPanel() {
		showStatus = new JTextArea();
		showStatus.setLineWrap(true);
		showStatus.setEditable(false);
		JScrollPane scrollTextArea = new JScrollPane(showStatus,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		statusPanel.add(scrollTextArea, BorderLayout.CENTER);
	}

	private void setUpButtonPanel() {
		buttonPanel.setLayout(new GridLayout(1, 2));
		input = new JButton("Set input directory to share");
		output = new JButton("Choose output directory");
		buttonPanel.add(input);
		buttonPanel.add(output);
	}

	private void setUpReceivePanel() {
		receive = new JButton("Receive");
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		receiveSubPanel1 = new JPanel();
		receiveSubPanel1.setLayout(new BorderLayout());
		receiveSubPanel2 = new JPanel();
		receiveSubPanel2.setLayout(new BorderLayout());
		receiveSubPanel1.add(progressBar, BorderLayout.NORTH);
		receiveSubPanel1.add(statusPanel, BorderLayout.CENTER);
		receiveSubPanel2.add(receive, BorderLayout.NORTH);
		receiveSubPanel2.add(receiveSubPanel1, BorderLayout.CENTER);
		receivePanel.add(selectPanel, BorderLayout.NORTH);
		receivePanel.add(receiveSubPanel2, BorderLayout.CENTER);
	}

	@Override
	public void displayAvailableFiles(Set<String> fileNames) {
		showResults.setText("");
		showResults.append("Please choose a file to receive in the drop down menu!\n");
		showResults.append("Available files are shown below: \n");
		fileToGet.removeAllItems();
		fileToGet.addItem(GET_FILE);
		int i = 0;
		for (String fileName: fileNames) {
			showResults.append(fileName);
			if (i != NAMES_PER_LINE - 1) {
				showResults.append("\t");
			} else {
				showResults.append("\n");
				i = -1;
			}
			i++;
			fileToGet.addItem(fileName);
		}
		showResults.append("\n");
		fileToGet.validate();
		fileToGet.repaint();
	}

	@Override
	public String retrieveFilenameRequest() {
		String file = String.valueOf(fileToGet.getSelectedItem());
		System.out.println("The user chose: " + file);
		return file;
	}

	@Override
	public void displayError(String error) {
		showStatus.append(error + "\n");
	}

	@Override
	public void displayMessage(String message) {
		showResults.append(message + "\n");
	}

	@Override
	public void displayWaitingMessage(String message) {
		showResults.setText(message + "\n");
	}
	
	@Override
	public void registerFileReceiver(FileReceiverTask fileReceiver) {
		ClientMain.lock.lock();
		try {
			this.fileReceiver = fileReceiver;
		} finally {
			ClientMain.lock.unlock();
		}
		
	}

	@Override
	public void unregisterFileReceiver() {
		ClientMain.lock.lock();
		try {
			this.fileReceiver = null;
		} finally {
			ClientMain.lock.unlock();
		}
		receive.setEnabled(true);
		input.setEnabled(true);
		output.setEnabled(true);
		clientFrame.setCursor(null);

	}

	@Override
	public void tellWaiting() {		
	}
	
	private class InputDirectoryChooser implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Please choose a directory to share");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				ClientMain.updateInputDirectory(chooser.getSelectedFile().getAbsolutePath());			}
		}
	}
	
	private class OutputDirectoryChooser implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Please choose a output directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				ClientMain.updateOutputDirectory(chooser.getSelectedFile().getAbsolutePath());
			}
		}
		
	}
	
	private class Receive implements ActionListener, PropertyChangeListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			receive.setEnabled(false);
			input.setEnabled(false);
			output.setEnabled(false);
			clientFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			ClientMain.lock.lock();
			try {
				ClientMain.notifyUserInputed();
				while(fileReceiver == null) {
					try {
						ClientMain.fileReceiverNotInitialized.await();
						if(ClientMain.hasErrorOccurred()) {
							unregisterFileReceiver();
							return;
						}
					} catch (InterruptedException e1) {}
				}
				fileReceiver.addPropertyChangeListener(this);
				fileReceiver.execute();
			} finally {
				ClientMain.setErrorOccurred(false);
				ClientMain.lock.unlock();
			}
		
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if("progress".equals(evt.getPropertyName())) {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
				progressBar.validate();
				progressBar.repaint();
			}
		}
	}
}

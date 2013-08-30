package javacalculus.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javacalculus.core.CalculusEngine;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Console extends JFrame implements ActionListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6064704618707571040L;
	
	private JPanel content = new JPanel();
	private CalculusEngine c = new CalculusEngine();
	private JTextField input = new JTextField();
	private JLabel inputLabel = new JLabel("Command:");
	private JTextArea console = new JTextArea("[OUTPUT]\nKeyboard shortcuts: Enter -> Execute, Up/down -> Navigate command history\n");
	//private JTextArea error = new JTextArea("[ERROR]\n");
	private JButton execute = new JButton("EXECUTE");
	
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int commandHistoryIndex = 0;
	
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Console();
			}
		});
	}
	
	public Console() {
		super("Javacalculus Test GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		System.setOut(new PrintStream(new consoleOutputStream()));
		//System.setErr(new PrintStream(new consoleOutputStream(true)));
		inputLabel.setLabelFor(input);
		input.setPreferredSize(new Dimension(300,20));
		input.setBorder(BorderFactory.createLoweredBevelBorder());
		input.addKeyListener(this);
		commandHistory.add("");
		execute.addActionListener(this);
		execute.setBackground(Color.GREEN);
		execute.setForeground(Color.WHITE);
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setEditable(false);
    	console.setFont(new Font("Dialog", Font.BOLD, 14));
		JScrollPane consolePane = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		consolePane.setPreferredSize(new Dimension(600,300));
		consolePane.setBorder(BorderFactory.createLoweredBevelBorder());
//		error.setLineWrap(true);
//		error.setWrapStyleWord(true);
//		error.setEditable(false);
//    	error.setFont(new Font("Dialog", Font.ITALIC, 12));
//		JScrollPane errorPane = new JScrollPane(error, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		errorPane.setBorder(BorderFactory.createLoweredBevelBorder());
//		errorPane.setPreferredSize(new Dimension(600,300));
		//content.setPreferredSize(new Dimension(400,400));
		content.setLayout(new GridBagLayout());
		content.setBorder(BorderFactory.createLoweredBevelBorder());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH;
		content.add(inputLabel, c);
		c.gridx = 1; c.gridy = 0;
		content.add(input, c);
		c.gridx = 2; c.gridy = 0;
		content.add(execute, c);
		c.gridx = 0; c.gridy = 1; c.gridwidth = 3;
		content.add(consolePane, c);
//		c.gridx = 0; c.gridy = 2;
//		content.add(errorPane, c);
		add(content);
		
		pack();
		setVisible(true);
		input.requestFocus();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		print(c.execute(input.getText()));
		commandHistory.remove(commandHistory.size() - 1);
		commandHistory.add(input.getText());
		commandHistory.add("");
		commandHistoryIndex = commandHistory.size() - 1;
		input.setText("");
		input.requestFocus();
	}
	
	private void print(String string) {
		console.append("\n" + string);
		console.setCaretPosition(console.getDocument().getLength());
	}
	
//	private void printError(String string) {
//		error.append(string);
//		error.setCaretPosition(error.getDocument().getLength());
//	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
			case KeyEvent.VK_ENTER:
				actionPerformed(null);
				break;
			case KeyEvent.VK_UP:
				if (commandHistoryIndex > 0) commandHistoryIndex--;
				input.setText(commandHistory.get(commandHistoryIndex));
				break;
			case KeyEvent.VK_DOWN:
				if (commandHistoryIndex < commandHistory.size() - 1) commandHistoryIndex++;
				input.setText(commandHistory.get(commandHistoryIndex));
				break;
			case KeyEvent.VK_BACK_SPACE:
				commandHistoryIndex = commandHistory.size() - 1;
				break;
			default:
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	
	private class consoleOutputStream extends OutputStream {
		//private boolean isError = false;
		
		public consoleOutputStream() {
//			this.isError = isError;
		}
		
		@Override	
		public void write(byte[] b) 
		{
			String out = new String(b);
			if (out.endsWith("\n")) out = out.substring(0, out.length() - 2); //remove endline char
			//if (isError) printError(out);
			print(out);
		}
		
		@Override	
		public void write(byte[] b, int off, int len) 
		{
			String out = new String(b, off, len);
			if (out.endsWith("\n")) out = out.substring(0, out.length() - 2); //remove endline char
			//if (isError) printError(out);
			print(out);
		}
		@Override
		public void write(int b) throws IOException {}	
	}
}

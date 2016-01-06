package com.matthew0x40.calculator.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class JFieldOption {
	//
    // Option types
    //

    /**
     * Type meaning Look and Feel should not supply any options -- only
     * use the options from the <code>JOptionPane</code>.
     */
    public static final int  DEFAULT_OPTION = JOptionPane.DEFAULT_OPTION;
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int  YES_NO_OPTION = JOptionPane.YES_NO_OPTION;
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int  YES_NO_CANCEL_OPTION = JOptionPane.YES_NO_CANCEL_OPTION;
    /** Type used for <code>showConfirmDialog</code>. */
    public static final int  OK_CANCEL_OPTION = JOptionPane.OK_CANCEL_OPTION;

    //
    // Return values.
    //
    /** Return value from class method if YES is chosen. */
    public static final int  YES_OPTION = JOptionPane.YES_OPTION;
    /** Return value from class method if NO is chosen. */
    public static final int  NO_OPTION = JOptionPane.NO_OPTION;
    /** Return value from class method if CANCEL is chosen. */
    public static final int  CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
    /** Return value form class method if OK is chosen. */
    public static final int  OK_OPTION = JOptionPane.OK_OPTION;
    /** Return value from class method if user closes window without selecting
     * anything, more than likely this should be treated as either a
     * <code>CANCEL_OPTION</code> or <code>NO_OPTION</code>. */
    public static final int  CLOSED_OPTION = JOptionPane.CLOSED_OPTION;

    //
    // Message types. Used by the UI to determine what icon to display,
    // and possibly what behavior to give based on the type.
    //
    /** Used for error messages. */
    public static final int  ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE;
    /** Used for information messages. */
    public static final int  INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE;
    /** Used for warning messages. */
    public static final int  WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE;
    /** Used for questions. */
    public static final int  QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE;
    /** No icon is used. */
    public static final int  PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;

	private ArrayList<JTextField> fields = new ArrayList<JTextField>();
	private JPanel panel;
	private int result = -1;
	
	public JFieldOption() {
		setup(1, null);
	}
	
	public JFieldOption(int fields) {
		setup(fields, null);
	}
	
	public JFieldOption(int fields, String text) {
		setup(fields, text);
	}
	
	public static void main(String[] args) {
		JFieldOption.showDialog(null,
				"Enter variable name in first field and value in second field",
				2, "Create variable", JFieldOption.OK_CANCEL_OPTION);
	}
	
	private void setup(int fields, String text) { 
		
	    panel = new JPanel();
	    panel.setLayout(new GridLayout(text != null ? fields+1 : fields, 1));
	    
	    JLabel textLabel = new JLabel(text);
	    textLabel.setOpaque(true);
	    textLabel.setBackground((Color) UIManager.get("Panel.background"));
	    
	    panel.add(textLabel);
	    for (int i = 0; i < fields; i++) {
	    	JTextField jtf = new JTextField();
	    	
    		jtf.setBorder(BorderFactory.createCompoundBorder(
    				BorderFactory.createMatteBorder(0, 0, 15, 0, (Color) UIManager.get("Panel.background")), 
    				jtf.getBorder() ));
    	
	    	this.fields.add(jtf);
	    	panel.add(jtf);
	    	
	    }
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public ArrayList<JTextField> getFields() {
		return fields;
	}
	
	public String[] getInput() {
		ArrayList<String> input = new ArrayList<String>();
		for (JTextField jtf : fields) {
			input.add(jtf.getText());
		}
		return input.toArray(new String[input.size()]);
	}
	
	public int getResult() {
		return result ;
	}
	
	public void showConfirmDialog(Component parentComponent) {
		result = JOptionPane.showConfirmDialog(null, panel);
	}
	
	public void showConfirmDialog(Component parentComponent, String title, int optionType) {
		result = JOptionPane.showConfirmDialog(null, panel, title, optionType);
	}
	
	public void showConfirmDialog(Component parentComponent, String title, int optionType, int messageType) {
		result = JOptionPane.showConfirmDialog(null, panel, title, optionType, messageType);
	}
	
	public void showConfirmDialog(Component parentComponent, String title, int optionType, int messageType, Icon icon) {
		result = JOptionPane.showConfirmDialog(null, panel, title, optionType, messageType, icon);
	}
	
	public static JFieldOption showDialog(Component parentComponent, String message, int fields) {
		JFieldOption pane = new JFieldOption(fields, message);
		pane.showConfirmDialog(parentComponent);
		return pane;
	}
	
	public static JFieldOption showDialog(Component parentComponent, String message, int fields, String title, int optionType) {
		JFieldOption pane = new JFieldOption(fields, message);
		pane.showConfirmDialog(parentComponent, title, optionType);
		return pane;
	}
	
	public static JFieldOption showDialog(Component parentComponent, String message, int fields, String title, int optionType, int messageType) {
		JFieldOption pane = new JFieldOption(fields, message);
		pane.showConfirmDialog(parentComponent, title, optionType, messageType);
		return pane;
	}
	
	public static JFieldOption showDialog(Component parentComponent, String message, int fields, String title, int optionType, int messageType, Icon icon) {
		JFieldOption pane = new JFieldOption(fields, message);
		pane.showConfirmDialog(parentComponent, title, optionType, messageType, icon);
		return pane;
	}
}

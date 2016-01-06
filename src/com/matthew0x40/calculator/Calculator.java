package com.matthew0x40.calculator;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.matthew0x40.calculator.Expression.Variable;
import com.matthew0x40.calculator.util.DateTextField;
import com.matthew0x40.calculator.util.Display;
import com.matthew0x40.calculator.util.JFieldOption;
import com.matthew0x40.calculator.util.JTextFrame;
import com.matthew0x40.calculator.util.Util;

public class Calculator {
	static JFrame frame;
	static JTextPane output;
	static JTextField input;
	static JScrollPane scrollArea;
	
	public static List<String> last = new ArrayList<String>();
	public static double last_ans = 0;
	public static List<String> hist = new ArrayList<String>(); // History, zero index = oldest, max index = newest
	public static int histIdx = -1;
	
	static Expression vars = new Expression() { // Not an expression to be evaluated, just a variable holder
		public boolean delVar(String name) {
			boolean b = super.delVar(name);
			if (b && varFrame.isVisible())
				varFrameCanvas.repaint();
			return b;
		}
		
		public boolean setVar(String name, double value) {
			boolean b = super.setVar(name, value);
			if (b && varFrame.isVisible())
				varFrameCanvas.repaint();
			return b;
		}
	};
	static SimpleAttributeSet alignRight = new SimpleAttributeSet();
	static JMenuBar menuBar;
	static JTextFrame helpFrame;
	static JTextFrame histFrame;
	
	static JMenu viewMenu;
	static JMenu editMenu;
	static JMenu helpMenu;
	
	static JMenuItem histItem;
	static AbstractButton helpItem;
	static JMenuItem clearOutput;
	static JRadioButtonMenuItem unitConversionItem;
	static JRadioButtonMenuItem dateCalcItem;
	static JMenuItem recallItem;
	static Display varFrame;
	static Dimension varFrameDim = new Dimension(500, 600);
	static Canvas varFrameCanvas;
	static int varFrameIdx = 0;
	static final int varFramePer = 15;
	static final HashMap<Integer, Rectangle[]> varFrameB = new HashMap<Integer, Rectangle[]>();
	static double varFrameBIH = -2;
	private static JFrame dateCalcFrame;
	private static DateTextField dateCalcField1;
	private static DateTextField dateCalcField2;
	private static JComboBox<String> dateCalcOption;
	private static JTextField dateCalcResult;
	
	private static PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
		    .printZeroAlways()
		    .minimumPrintedDigits(2)
		    .appendMonths().appendSuffix(" months ")
		    .appendDays().appendSuffix(" days ")
		    .appendYears().appendSuffix(" years ")
		    .appendHours().appendSuffix(" hours ")
		    .appendMinutes().appendSuffix(" minutes ")
		    .appendSeconds().appendSuffix(" seconds")
		    .toFormatter(); // produce thread-safe formatter
	
	static {
		StyleConstants.setAlignment(alignRight, StyleConstants.ALIGN_RIGHT);  
		
		UIManager.put("ToolTip.background", new ColorUIResource(255, 255, 255));
		UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(118,118,118)));
		ToolTipManager.sharedInstance().setInitialDelay(400);
		ToolTipManager.sharedInstance().setDismissDelay(3600000);
	}
	
	public static void initHelpFrame() {
		helpFrame = new JTextFrame("Help");
		helpFrame.getTextPane().setEditable(false);
		helpFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		helpFrame.setPreferredSize(new Dimension(620, 700));
		helpFrame.getTextPane().setContentType("text/html");
		
		try {
			helpFrame.getTextPane().setText(Util.streamToString(Calculator.class.getResourceAsStream("help.html")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		helpFrame.getTextPane().addHyperlinkListener(new HyperlinkListener(){

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					Util.openWebpage(e.getURL());
				} else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
					helpFrame.getTextPane().setToolTipText(e.getURL().toString());
				} else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
					helpFrame.getTextPane().setToolTipText(null);
				}
			}
			
		});
		
		helpFrame.setResizable(true);
		helpFrame.pack();
	}
	
	public static void initDateCalcFrame() {
		dateCalcFrame = new JFrame("Date Calculation");
		dateCalcFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		dateCalcFrame.setPreferredSize(new Dimension(350, 300));
		dateCalcFrame.setResizable(false);
		dateCalcFrame.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.insets = new Insets(5,5,5,5);
		
		gbc.gridx = 5;
		gbc.gridy = 5;
		
		dateCalcField1 = new DateTextField();
		dateCalcFrame.add(dateCalcField1, gbc);
		gbc.gridy = 25;
		dateCalcField2 = new DateTextField();
		dateCalcFrame.add(dateCalcField2, gbc);
		
		gbc.gridy = 45;
		//String[] optionArray = { "Difference between two dates", "Add or subract days to a specific date" };
		String[] optionArray = { "Difference between two dates" };
		dateCalcOption = new JComboBox<String>(optionArray);
		dateCalcOption.setSelectedIndex(0);
		
		dateCalcFrame.add(dateCalcOption, gbc);
		gbc.gridy = 55;
		
		dateCalcResult = new JTextField();
		dateCalcResult.setEditable(false);
		dateCalcFrame.add(dateCalcResult, gbc);
		gbc.gridy = 65;
		JButton calc = new JButton("Calculate");
		calc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIdx = dateCalcOption.getSelectedIndex();
				DateTime resDate1 = dateCalcField1.getDateTime();
				DateTime resDate2 = dateCalcField2.getDateTime();
				
				if (selectedIdx == 0) {
					dateCalcResult.setText(periodFormatter.print(
							new Period(resDate1, resDate2) ));
				} else if (selectedIdx == 1) {
					//dateCalcResult.setText(dateFormat.format(resDate1));
				}
			}
			
		});
		dateCalcFrame.add(calc, gbc);
		
		dateCalcFrame.pack();
	}

	public static void main(String[] args) {
		// Initialize JFrame
		frame = new JFrame("Calculator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(700, 700));
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(100, 125));
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {}
		
		// Initialize extra frames
		initHelpFrame();
		initDateCalcFrame();
		
		varFrame = new Display("Variables");
		varFrame.setResizable(false);
		varFrameCanvas = new Canvas(){
			private static final long serialVersionUID = 73163761809862365L;
			BufferedImage buffer = new BufferedImage((int) varFrameDim.getWidth(), (int) varFrameDim.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) buffer.getGraphics();
			
			@Override
			public void paint(Graphics g0) {
	        	Util.applyQuality(g);
	        	
	        	g.setColor(Color.white);
	        	g.fillRect(0, 0, varFrame.getWidth(), varFrame.getHeight());
	        	
	        	int endI = varFrameIdx;
	        	
	        	while (endI < varFrameIdx+varFramePer && endI < vars.var.size()) {
	        		endI++;
	        	}
	        	
	        	g.setColor(Color.black);
	        	
	        	g.drawLine(0, 30, varFrame.getWidth(), 30);
	        	g.drawString("Name", 5, 20);
	        	g.drawString("Value", 125, 20);
	        	g.drawString("Options", 325, 20);
	        	
	        	FontMetrics fm = g.getFontMetrics();
	        	
	        	List<Variable> sub = vars.var.subList(varFrameIdx, endI);
	        	int nextY = 30;
	        	for (int i = 0; i < sub.size(); i++) {
	        		Variable v = sub.get(i);
	        		g.drawString(v.name, 5, nextY+=30);
	        		g.drawString(v.roundedValue(20), 125, nextY);
	        		
	        		Rectangle2D r0 = fm.getStringBounds("Delete", g);
	        		if (varFrameBIH == i)
	        			g.setColor(Color.blue);
	        		g.drawString("Delete", 325, nextY);
	        		g.setColor(Color.black);
	        		
	        		Rectangle2D r1 = fm.getStringBounds("Change", g);
	        		if (varFrameBIH == i+0.5)
	        			g.setColor(Color.blue);
	        		g.drawString("Change", 325+75, nextY);
	        		g.setColor(Color.black);
	        		
	        		if (!varFrameB.containsKey(i))	{
	        			varFrameB.put(i, new Rectangle[] {
	        				new Rectangle(325-5, nextY-5, (int) r0.getWidth()+10, (int) r0.getHeight()+10),
	        				new Rectangle(325+75-5, nextY-5, (int) r1.getWidth()+10, (int) r1.getHeight()+10)
	        			});
	        		}
	        	}
	        	
	        	g.drawLine(0, varFrame.getHeight()-100, varFrame.getWidth(), varFrame.getHeight()-100);
	        	
	        	g.setColor(new Color(220,220,220));
	        	//g.fillRect(3, varFrame.getHeight()-90, 70, 20);
	        	varFrameB.put(-1, new Rectangle[] { new Rectangle(3, varFrame.getHeight()-90, 70, 20) } );
	        	
	        	if (varFrameBIH == -1)
        			g.setColor(Color.blue);
	        	else
	        		g.setColor(Color.black);
	        	g.drawString("Create new", 5, varFrame.getHeight()-75);
	        	
	        	g0.drawImage(buffer, 0, 0, null);
			}
		};
		varFrameCanvas.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) {}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				Point p = arg0.getPoint();
				for (Map.Entry<Integer, Rectangle[]> e : varFrameB.entrySet()) {
					if (e.getKey() == -1) {
						if (e.getValue()[0].contains(p)) {
							if (varFrameBIH == -1)
								return;
							varFrameBIH = -1;
							varFrameCanvas.repaint();
						}
						return;
					}
					
					Rectangle[] ra = e.getValue();
					if (ra[0].contains(p)) {
						if (varFrameBIH == e.getKey())
							return;
						varFrameBIH = e.getKey();
						varFrameCanvas.repaint();
						return;
					} else if (ra[1].contains(p)) {
						if (varFrameBIH == e.getKey()+0.5)
							return;
						varFrameBIH = e.getKey()+0.5;
						varFrameCanvas.repaint();
						return;
					}
				}
				varFrameBIH = -2;
			}
			
		});
		varFrameCanvas.addMouseListener(new MouseListener() {
			
			@Override public void mouseClicked(MouseEvent arg0) {}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0)  {}
			@Override public void mousePressed(MouseEvent arg0) {}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Point p = arg0.getPoint();
				for (Map.Entry<Integer, Rectangle[]> e : varFrameB.entrySet()) {
					if (e.getKey() == -1) {
						if (e.getValue()[0].contains(p)) {
							JFieldOption opt = JFieldOption.showDialog(null,
									"Enter variable name in first field and value in second field.\nExpressions work. Ex: 5+5-2",
									2, "Create variable", JFieldOption.OK_CANCEL_OPTION);
							if (opt.getResult() == JFieldOption.OK_OPTION) {
								String[] a = opt.getInput();
								
								if (vars.restricted(a[0].trim())) {
									JOptionPane.showMessageDialog(varFrameCanvas, "Invalid name", "Error", JOptionPane.ERROR_MESSAGE);
									return;
								}
								
								Variable v = vars.getVarObj(a[0].trim());
								if (v == null) {
									try {
										Expression ex = new Expression(a[1].trim());
										ex.replaceVars(vars);
										Double er = ex.evaluate();
										if (er == null)
											throw new Exception();
										vars.var.add(new Variable(a[0].trim(), er));
										varFrameCanvas.repaint();
										return;
									} catch(Exception e1) {
										JOptionPane.showMessageDialog(varFrameCanvas, "Invalid expression", "Error", JOptionPane.ERROR_MESSAGE);
									}
								} else {
									JOptionPane.showMessageDialog(varFrameCanvas, "Variable already exists", "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
						return;
					}
					
					Rectangle[] ra = e.getValue();
					if (ra[0].contains(p)) {
						Variable v = vars.var.get(varFrameIdx+e.getKey());
						
						if (vars.restricted(v.name)) {
							JOptionPane.showMessageDialog(varFrameCanvas, "Cannot delete this variable", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						vars.var.remove(vars.var.get(varFrameIdx+e.getKey()));
						varFrameCanvas.repaint();
						return;
					} else if (ra[1].contains(p)) {
						Variable v = vars.var.get(varFrameIdx+e.getKey());
						
						if (vars.restricted(v.name)) {
							JOptionPane.showMessageDialog(varFrameCanvas, "Cannot edit this variable", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						String input = JOptionPane.showInputDialog(varFrameCanvas,
								"Input new value for: " + v.name+"\nExpressions work. Ex: 5+5-2",
								"Change Variable", JOptionPane.OK_CANCEL_OPTION);
						if (input == null || input.trim().equals("")) {
							return;
						}
						
						try {
							Expression ex = new Expression(input.trim());
							ex.replaceVars(vars);
							Double er = ex.evaluate();
							if (er == null)
								throw new Exception();
							v.value = er;
							varFrameCanvas.repaint();
							return;
						} catch(Exception e1) {
							JOptionPane.showMessageDialog(varFrameCanvas, "Invalid expression", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		varFrameCanvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (Math.signum(e.getWheelRotation()) == 1.0) {
					// Scrolled down
					if (varFrameIdx + 1 < vars.var.size()-varFramePer+1) {
						varFrameIdx++;
						varFrameCanvas.repaint();
					}
				} else {
					// Scrolled up
					if (varFrameIdx - 1 > 0) {
						varFrameIdx--;
						varFrameCanvas.repaint();
					}
				}
			}
			
		});
		varFrame.add(varFrameCanvas);
		varFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		varFrame.setPreferredSize(varFrameDim);
		varFrame.setResizable(true);
		varFrame.pack();
		
		histFrame = new JTextFrame("History");
		histFrame.getTextPane().setEditable(false);
		histFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		histFrame.setPreferredSize(new Dimension(500, 600));
		histFrame.setResizable(true);
		histFrame.pack();
		
		// Initialize menu bar
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		// View Menu
		
        viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        
        dateCalcItem = new JRadioButtonMenuItem("Date Calculation"); // TODO
        dateCalcItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (dateCalcFrame.isVisible()) {
            		dateCalcFrame.setVisible(false);
            	} else {
            		dateCalcFrame.setVisible(true);
            	}
            }
        });
        viewMenu.add(dateCalcItem);
        
        // Edit Menu
        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        
        recallItem = new JMenuItem("Recall Variables"); // TODO
        recallItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	varFrame.setVisible(true);
            	varFrameCanvas.repaint();
            }
        });
        editMenu.add(recallItem);
        
        clearOutput = new JMenuItem("Clear output");
        clearOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	output.setText("");
            }
        });
        editMenu.add(clearOutput);
        
        histItem = new JMenuItem("History");
        histItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	histFrame.setVisible(true);
            }
        });
        editMenu.add(histItem);
        
        // Help Menu
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        helpItem = new JMenuItem("View Help");
        helpItem.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		helpFrame.setVisible(true);  
            }
        });
        helpMenu.add(helpItem);
        
		
        // Initialize output text area
		output = new JTextPane();
		output.setEditable(false);
		
		// Initialize input text field
		input = new JTextField();
		input.addKeyListener(new KeyListener() {

			@Override public void keyPressed(KeyEvent ke) {}

			@Override
			public void keyReleased(KeyEvent ke) {
				int k = ke.getKeyCode();
				history: {
					if (hist.isEmpty())
						break history;
					if (k == KeyEvent.VK_UP) {
						if (histIdx == -1) {
							histIdx = hist.size();
						}
						if (histIdx - 1 >= 0) {
							histIdx--;
							input.setText(hist.get(histIdx));
						}	
					} else if (k == KeyEvent.VK_DOWN) {
						if (histIdx == -1) {
							return;
						}
						
						if (histIdx + 1 < hist.size()) {
							histIdx++;
							input.setText(hist.get(histIdx));
						} else {
							histIdx = -1;
							input.setText("");
						}
					}
				}
			}

			@Override public void keyTyped(KeyEvent ke) {}
			
		});
		input.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				handleInput();
			}
			
		});
		
		// Initialize JScrollPane
		scrollArea = new JScrollPane(output);
		scrollArea.setBorder(BorderFactory.createEmptyBorder());
		scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		frame.add(scrollArea, BorderLayout.CENTER);
		frame.add(input, BorderLayout.SOUTH);
		
		// Pack and set visible
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void handleInput() {
		// Get trimmed input text, then clear input
		String text = input.getText().trim();
	    input.setText("");
	    
	    // Append input text (aligned-left)
	 	appendln(text);
	    
	    
		if (text.equals("")) {
			// If empty, set input text equal to last input
			text = hist.get(hist.size()-1);
		} else if (text.startsWith("recall ")) {
			Double vr = vars.getVar(text.replaceFirst("recall ", "").trim());
			appendResult((vr == null) ? "No variable for name" : Util.formatDouble(vr.toString()));
			return;
		} else if (text.contains("->") || text.contains("- >")) {
			String[] a = text.split("->|- >");
			try {
				Expression e = new Expression(a[0].trim());
				e.replaceVars(vars);
				Double er = e.evaluate();
				if (er == null)
					throw new Exception();
				if (vars.restricted(a[1].trim())) {
					appendResult("Cannot set values to given name");
					return;
				}
				vars.setVar(a[1].trim(), er);
				if (a.length > 2 || a.length < 2) {
					throw new Exception();
				}
				appendResult(Util.formatDouble(er.toString()));
				return;
			} catch(Exception e) {
				appendResult("Invalid sto expression");
				return;
			}
		} else if (text.startsWith("delvar ")) {
			String name = text.replaceFirst("delvar ", "").trim();
			if (vars.containsVar(name)) {
				if (vars.restricted(name)) {
					appendResult("Cannot delete given variable name");
					return;
				}
				
				vars.delVar(name);
				appendResult("Variable deleted");
			} else {
				appendResult("No variable for name");
			}
			return;
		}
		
		// Get answer
		Expression e = new Expression(text.replace("ans", Double.toString(last_ans))
				.replace("ANS", Double.toString(last_ans))
				.replace("Ans", Double.toString(last_ans)));
		e.replaceVars(vars);
		Double a = e.evaluate();
		
		// Set last answer to 'a' only if a is valid
		if (a != null)
			last_ans = a;
	    
		// Append answer or "Invalid Expression" if input is invalid (aligned-right)
		String result = (a == null) ? "Invalid expression" : a+"";
		appendResult(Util.formatDouble(result));
		
		// Append input text to history and reset history index
		histIdx = -1;
		if (hist.size() >= 1) {
			if (!hist.get(hist.size()-1).equals(text)) {
				hist.add(text);
			}
		} else
			hist.add(text);
	    histFrame.appendln(text);
	    scrollArea.getVerticalScrollBar().setValue(scrollArea.getVerticalScrollBar().getMaximum());
	}
	
	public static void appendResult(String result) {
		StyledDocument doc = output.getStyledDocument();
		appendln(result);
		doc.setParagraphAttributes(doc.getLength()-result.length(), result.length(), alignRight, true);
	}
	
	public static void appendln(String str) {
		append( str+System.lineSeparator());
	}
	
	public static void append(String str) {
		Document doc = output.getDocument();
		if (doc != null) {
			try {
				doc.insertString(doc.getLength(), str, null);
			} catch (BadLocationException e) {
			}
		}
	}
}

package com.matthew0x40.calculator.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.DateTime;
import org.joda.time.chrono.GregorianChronology;

public class DatePicker {
	static Color normal = new Color(253,253,253);
	static Color hover = new Color(213,225,242);
	static Color active = new Color(194,213,242);
	static String[] monthsArray = "January February March April May June July August September October November December".split(" ");
	static GregorianChronology calendar = GregorianChronology.getInstance();
	
	JLabel leftArrowBtn;
	JLabel rightArrowBtn;
	final JDialog frame;
	final JComboBox<String> monthSelector = new JComboBox<String>(monthsArray);
	
	ArrayList<SelectionListener> selListeners = new ArrayList<SelectionListener>();
	DateTime today = null;
	JPanel topArea = new JPanel();
	JPanel upperArea = new JPanel();
	JPanel dayLabels = new JPanel();
	JPanel dayArea = new JPanel();
	JSpinner yearSelector = new JSpinner();
	JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSelector, "#");
	KeyListener kl = new KeyListener() {
		@Override public void keyReleased(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
				frame.setVisible(false); frame.dispose(); }}
		@Override public void keyPressed(KeyEvent arg0) {}
		@Override public void keyTyped(KeyEvent arg0) {}};
	
	public DatePicker() {
		// Init Frame
		frame = new JDialog();
		topArea.addKeyListener(kl);
		upperArea.addKeyListener(kl);
		dayArea.addKeyListener(kl);
		dayLabels.addKeyListener(kl);
		yearSelector.addKeyListener(kl);
		monthSelector.addKeyListener(kl);
		frame.addWindowListener(new WindowListener() {
			
			@Override public void windowActivated(WindowEvent arg0) {}
			@Override public void windowClosed(WindowEvent arg0) {}
			@Override public void windowClosing(WindowEvent arg0) {}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				frame.setVisible(false);
				frame.dispose();
			}

			@Override public void windowDeiconified(WindowEvent arg0) {}
			@Override public void windowIconified(WindowEvent arg0) {}
			@Override public void windowOpened(WindowEvent arg0) {}
			
		});
		frame.setUndecorated(true);
		frame.setPreferredSize(new Dimension(250,250));
		frame.getRootPane().setBackground(Color.white);
		frame.getRootPane().setBorder(BorderFactory.createLineBorder(active, 1));
		
		// Days Area
		dayArea.setLayout(new GridLayout(6,7));
		
		for (int i = 0; i < (6*7); i++) {
			dayArea.add(new DayButton());
		}
		dayArea.setBackground(Color.white);
		
		// Left Arrow (Previous) Button
		try {
			leftArrowBtn = new JLabel(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("left.png"))));
			leftArrowBtn.setBackground(normal);
			leftArrowBtn.setOpaque(true);
			leftArrowBtn.setPreferredSize(new Dimension(35, 21));
			leftArrowBtn.addMouseListener(new MouseListener() {
				boolean mouseIn = false;
				
				public void mouseClicked(MouseEvent arg0) {}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					mouseIn = true;
					if (leftArrowBtn.isEnabled()) {
						leftArrowBtn.setBackground(hover);
					}
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					mouseIn = false;
					if (leftArrowBtn.isEnabled()) {
						leftArrowBtn.setBackground(normal);
					}
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (leftArrowBtn.isEnabled()) {
						leftArrowBtn.setBackground(active);
					}
				}
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
					if (leftArrowBtn.isEnabled()) {
						if (mouseIn)
							leftArrowBtn.setBackground(hover);
						else
							leftArrowBtn.setBackground(normal);
					}
					
					int month = monthSelector.getSelectedIndex();
					int year = (int) yearSelector.getValue();
					if (month == 0) {
						yearSelector.setValue(year-1);
						monthSelector.setSelectedIndex(11);
					} else {
						monthSelector.setSelectedIndex(month-1);
					}
				}
				
			});
			upperArea.add(leftArrowBtn, BorderLayout.WEST);
		} catch(IOException e) {}
		
		// Month Selector
		
		monthSelector.setRenderer(new DatePickerComboBoxRenderer());
		monthSelector.setPreferredSize(new Dimension(77, 21));
		monthSelector.setBorder(BorderFactory.createLineBorder(new Color(198, 198, 198)));
		monthSelector.setFocusTraversalKeysEnabled(false);
		monthSelector.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update((int) yearSelector.getValue(), monthSelector.getSelectedIndex()+1);
			}
			
		});
		upperArea.add(monthSelector, BorderLayout.WEST);
		
		// Year Selector
		
		yearSelector.setEditor(editor);
		yearSelector.setFocusTraversalKeysEnabled(false);
		yearSelector.setPreferredSize(new Dimension(50, 21));
		yearSelector.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				update((int) yearSelector.getValue(), monthSelector.getSelectedIndex()+1);
			}
			
		});
		((JSpinner.DefaultEditor) yearSelector.getEditor()).getTextField().addKeyListener(new KeyListener() {
			@Override public void keyPressed(KeyEvent evt) {}
			@Override public void keyReleased(KeyEvent arg0) {}

			@Override public void keyTyped(KeyEvent evt) {
				int n = Character.getNumericValue(evt.getKeyChar());
				if (!(n != -1 && n != -2 && n <= 9)) {
					evt.consume();
				}
			}
			
		});
		upperArea.add(yearSelector, BorderLayout.EAST);
		
		// Right Arrow (Next) Button
		
		try {
			rightArrowBtn = new JLabel(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("right.png"))));
			rightArrowBtn.setBackground(normal);
			rightArrowBtn.setOpaque(true);
			rightArrowBtn.setPreferredSize(new Dimension(35, 21));
			rightArrowBtn.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					rightArrowBtn.setBorder(BorderFactory.createLineBorder(new Color(198, 198, 198)));
				}

				@Override
				public void focusLost(FocusEvent e) {
					rightArrowBtn.setBorder(BorderFactory.createEmptyBorder());
				}
			});
			rightArrowBtn.addMouseListener(new MouseListener() {
				boolean mouseIn = false;
				
				public void mouseClicked(MouseEvent arg0) {}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					mouseIn = true;
					if (rightArrowBtn.isEnabled()) {
						rightArrowBtn.setBackground(hover);
					}
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					mouseIn = false;
					if (rightArrowBtn.isEnabled()) {
						rightArrowBtn.setBackground(normal);
					}
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (rightArrowBtn.isEnabled()) {
						rightArrowBtn.setBackground(active);
					}
				}
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
					if (rightArrowBtn.isEnabled()) {
						if (mouseIn)
							rightArrowBtn.setBackground(hover);
						else
							rightArrowBtn.setBackground(normal);
					}
					
					int month = monthSelector.getSelectedIndex();
					int year = (int) yearSelector.getValue();
					if (month == 11) {
						yearSelector.setValue(year+1);
						monthSelector.setSelectedIndex(0);
					} else {
						monthSelector.setSelectedIndex(month+1);
					}
				}
				
			});
			
			upperArea.add(rightArrowBtn, BorderLayout.EAST);
		} catch(IOException e) {}
		
		// Day Labels
		
		dayLabels.setLayout(new GridLayout(1, 7));
		createDayLabels(false, "Sunday","Monday","Tuesday","Thursday","Friday","Saturday");
		
		// Finish stuff
		
		topArea.setLayout(new BorderLayout());

		upperArea.setBackground(Color.white);
		dayLabels.setBackground(Color.white);
		topArea.setBackground(Color.white);
		
		topArea.add(upperArea, BorderLayout.NORTH);
		topArea.add(dayLabels, BorderLayout.SOUTH);

		frame.add(topArea, BorderLayout.NORTH);
		frame.add(dayArea, BorderLayout.CENTER);
		
		frame.pack();
	}
	
	private void createDayLabels(boolean tooltip, String... labels) {
		for (String label : labels) {
			createDayLabel(tooltip, label);
		}
	}
	
	private void createDayLabel(boolean tooltip, String label) {
		JLabel dayLabel1 = new JLabel(label.substring(0, 3), SwingConstants.CENTER);
		if (tooltip)
			dayLabel1.setToolTipText(label);
		dayLabels.add(dayLabel1);
	}
	
	public boolean addSelectionListener(SelectionListener listener) {
		return selListeners.add(listener);
	}
	
	public boolean removeSelectionListener(SelectionListener listener) {
		return selListeners.remove(listener);
	}
	
	public void show(Point p) {
		show(p, DateTime.now());
	}

	public void update(int year, int month) {
		DateTime firstDay = new DateTime(year, month, 1, 1, 1);
		DateTime prevFirstDay = new DateTime(year == 1 ? 12 : year-1,
				month == 1 ? 12 : month-1, 1, 1, 1);

		int d = 0; // Component index in 'days' JPanel
		
		// Previous month
		int daysInPrevMonth = prevFirstDay.dayOfMonth().getMaximumValue();
		for (int x = daysInPrevMonth-firstDay.getDayOfWeek()+1; x <= daysInPrevMonth; x++) {
			DayButton btn = ((DayButton) dayArea.getComponent(d++));
			btn.setEnabled(false);
			btn.setToday(false);
			btn.setText(""+x);
		}
		
		// This month
		for (int x = 1; x <= firstDay.dayOfMonth().getMaximumValue(); x++) {
			DayButton btn = ((DayButton) dayArea.getComponent(d++));
			btn.setEnabled(true);
			btn.setText(""+x);
			if (today.getMonthOfYear() == month && today.getYear() == year && x == today.getDayOfMonth()) {
				btn.setToday(true);
			} else {
				btn.setToday(false);
			}
		}
		
		// Next month
		for (int x = firstDay.getDayOfWeek()+firstDay.dayOfMonth().getMaximumValue(), x2 = 1; x < 42; x++, x2++) {
			DayButton btn = ((DayButton) dayArea.getComponent(d++));
			btn.setEnabled(false);
			btn.setToday(false);
			btn.setText(""+x2);
		}
	}
	
	
	public void show(Point p, DateTime dt) {
		frame.setLocation(p);
		
		today = dt;
		monthSelector.setSelectedIndex(dt.getMonthOfYear()-1);
		yearSelector.setValue(dt.getYear());
		
		frame.setVisible(true);
	}
	
	public class DayButton extends JLabel {
		private static final long serialVersionUID = -3706128958443647568L;
		private boolean today = false;
		
		public void setToday(boolean today) {
			this.today = today;
			if (today)
				this.setBackground(hover);
			else
				this.setBackground(normal);
		}
		
		public DayButton() {
			this(0, false);
		}
		
		public DayButton(int day) {
			this(day, false);
		}
		
		public DayButton(int day, boolean today) {
			super(""+day);
			this.today = today;
			
			this.setOpaque(true);
			if (today)
				this.setBackground(hover);
			else
				this.setBackground(normal);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.addMouseListener(new MouseListener() {
				boolean mouseIn = false;
				
				public void mouseClicked(MouseEvent arg0) {
					DateTime dt = new DateTime(
							(int) yearSelector.getValue(),
							monthSelector.getSelectedIndex() + 1,
							Integer.parseInt(DayButton.this.getText()), 1, 1);
					for (SelectionListener listener : selListeners) {
						listener.dateSelected(dt);
					}
					
					frame.setVisible(false);
					frame.dispose();
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					mouseIn = true;
					if (DayButton.this.isEnabled() && !DayButton.this.today) {
						DayButton.this.setBackground(hover);
					}
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					mouseIn = false;
					if (DayButton.this.isEnabled() && !DayButton.this.today) {
						DayButton.this.setBackground(normal);
					}
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (DayButton.this.isEnabled()) {
						DayButton.this.setBackground(active);
					}
				}
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
					if (DayButton.this.isEnabled()) {
						if (mouseIn || DayButton.this.today)
							DayButton.this.setBackground(hover);
						else {
							DayButton.this.setBackground(normal);
						}
					}
				}
				
			});
			
			this.setBorder(BorderFactory.createLineBorder(Color.white, 2));
		}
	}
	
	public static interface SelectionListener {
		public void dateSelected(DateTime dt);
	}
	
	public static class DatePickerComboBoxRenderer extends DefaultListCellRenderer {
		Color selectColor = new Color(198, 198, 198);
		
		private static final long serialVersionUID = 4114195900557852755L;

		public DatePickerComboBoxRenderer() {
			super();
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object obj, int index, boolean selected, boolean hasFocus){
			Component comp = super.getListCellRendererComponent(list, obj, index, selected, hasFocus);
			if (selected) {
				comp.setBackground(selectColor);
				comp.setForeground(Color.black);
			} else {
				comp.setBackground(Color.white);
			}
			return comp;

		}
	}
}

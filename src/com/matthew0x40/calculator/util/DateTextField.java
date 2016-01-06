package com.matthew0x40.calculator.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.matthew0x40.calculator.util.DatePicker.SelectionListener;

public class DateTextField extends JTextField {
	private static final long serialVersionUID = -1575771323824118084L;

	static Color transparent = new Color(0,0,0,0);
	
	JButton btn;
	DatePicker datePicker = new DatePicker();
	DateTime dt;
	DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
	int selectedPart = -1;
	int preInputVal = 0;
	boolean inputting = false;
	
	public DateTextField() {
		this(DateTime.now());
	}
	
	public DateTextField(DateTime dt) {
		super();
		this.setText(dt.toString(dtf));
		this.dt = dt;
		this.setCaretColor(transparent);
		initDateTextField();
		
		this.setDay(this.getDay());
		this.setMonth(this.getMonth());
		
		setSelectedPart(1);
		inputting = false;
	}
	
	public int getSelectedPart() {
		return selectedPart;
	}
	
	public void setSelectedPart(int part) {
		setSelectedPart(part, false);
	}
	
	public void setSelectedPart(int part, boolean inputting) {
		// The order of lines in this function is important
		this.inputting = inputting;
		String text = this.getText();
		
		if (part == 1) {
			this.select(0, text.indexOf('/'));
		} else if (part == 2) {
			this.select(text.indexOf('/')+1, text.indexOf('/', text.indexOf('/')+1));
		} else if (part == 3) {
			this.select(text.indexOf('/', text.indexOf('/')+1)+1, text.length());
		} else if (part == 4) {
			btn.requestFocusInWindow();
		} else {
			throw new IllegalArgumentException("1, 2, and 3 are the only valid arguments");
		}
		
		if (part != selectedPart) {
			boolean v = validateCurrentPart();
			selectedPart = part;

			try {
				preInputVal = Integer.parseInt(this.getSelectedText().trim());
			} catch(Exception e) {}
			
			if (v) {
				this.setSelectedPart(selectedPart);
				try {
					preInputVal = Integer.parseInt(this.getSelectedText().trim()); // Yes this needs to be here again
				} catch(Exception e) {}
			}
		}
	}
	
	private boolean validateCurrentPart() {
		if (selectedPart == 1) {
			int val = getMonth();
			if (val == -1 || val == 0) {
				this.setMonth(preInputVal);
				return true;
			}
		} else if (selectedPart == 2) {
			int val = getDay();
			if (val == -1 || val == 0) {
				this.setDay(preInputVal);
				return true;
			}
		} else if (selectedPart == 3) {
			int val = getYear();
			if (val == -1 || val == 0) {
				this.setYear(preInputVal);
				return true;
			}
		}
		return false;
	}
	
	protected void initDateTextField() {
		datePicker.addSelectionListener(new SelectionListener() {

			@Override
			public void dateSelected(DateTime dt) {
				setDateTime(dt);
			}
			
		});
		this.setSelectionColor(new Color(198, 198, 198));
		this.setSelectedTextColor(new Color(0,0,0));
		this.setFont(new Font("Consolas", Font.PLAIN, 14));
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		ComponentBorder cb;
		try {
			btn = new JButton(
					new ImageIcon( ImageIO.read(this.getClass().getResourceAsStream("calendar-button.png")) ));
			btn.setMargin(new Insets(0,0,0,0));
			btn.setFocusPainted(false);
			btn.setContentAreaFilled(false);
			btn.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					btn.setBorder(BorderFactory.createLineBorder(new Color(198, 198, 198)));
				}

				@Override
				public void focusLost(FocusEvent e) {
					btn.setBorder(BorderFactory.createEmptyBorder());
				}
				
			});
			btn.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent evt) {
					int kc = evt.getKeyCode();
					
					if (kc == KeyEvent.VK_LEFT || kc == KeyEvent.VK_KP_LEFT) {
						if (selectedPart > 1) {
							DateTextField.this.requestFocusInWindow();
							setSelectedPart(selectedPart - 1);
						}
						evt.consume();
					} else if (kc == KeyEvent.VK_ENTER) {
						datePicker.show(btn.getLocationOnScreen(), dt);
						evt.consume();
					}
				}

				@Override
				public void keyReleased(KeyEvent arg0) {}

				@Override
				public void keyTyped(KeyEvent arg0) {}
				
			});
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					datePicker.show(MouseInfo.getPointerInfo().getLocation(), dt);
				}
			});
			
			cb = new ComponentBorder(btn);
			cb.install(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {
				setSelectedPart(partForCaretPosition(getCaretPosition()));
				arg0.consume();
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				setSelectedPart(selectedPart);
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {}
			
		});
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent evt) {
				int kc = evt.getKeyCode();
				
				if (kc == KeyEvent.VK_LEFT || kc == KeyEvent.VK_KP_LEFT) {
					if (selectedPart > 1)
						setSelectedPart(selectedPart - 1);
					evt.consume();
				} else if (kc == KeyEvent.VK_RIGHT || kc == KeyEvent.VK_KP_RIGHT) {
					if (selectedPart < 4)
						setSelectedPart(selectedPart + 1);
					evt.consume();
				} else if (kc == KeyEvent.VK_BACK_SPACE || kc == KeyEvent.VK_DELETE) {
					if (selectedPart == 1)
						replaceSelection("  ");
					else if (selectedPart == 2)
						replaceSelection("  ");
					else if (selectedPart == 3)
						replaceSelection("    ");
					setSelectedPart(selectedPart, true);
					evt.consume();
				}
			}

			@Override public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent evt) {
				int n = Character.getNumericValue(evt.getKeyChar());
				
				if (n != -1 && n != -2 && n <= 9) {
					int sel = 0;
					try {
						sel = Integer.parseInt(getSelectedText().trim());
					} catch(Exception e) {}
					
					if (selectedPart == 1) {
						if (inputting && sel == 1 && (n == 0 || n == 1 || n == 2) ) {
							replaceSelection(sel+""+n);
						} else {
							replaceSelection(" "+n);
						}
					} else if (selectedPart == 2) {
						if (inputting && (sel == 1 || sel == 2 || sel == 3)) {
							int temp = Integer.parseInt(sel+""+n);
							if (temp > daysInMonth())
								replaceSelection(" "+n);
							else
								replaceSelection(sel+""+n);
						} else {
							replaceSelection(" "+n);
						}
					} else if (selectedPart == 3) {
						if (inputting && sel != 0) {
							String temp = sel+""+n;
							
							if (temp.length() == 2) {
								temp = "  "+temp;
							} else if (temp.length() == 3) {
								temp = " "+temp;
							}
							
							replaceSelection(temp);
						} else {
							replaceSelection("   "+n);
						}
					}
					
					setSelectedPart(selectedPart);
					evt.consume();
					
					dt = getDateTime();
				} else {
					evt.consume();
				}
				
				inputting = true;
			}
			
		});
	}
	
	public DateTime setDateTime(DateTime dt) {
		DateTime prev = getDateTime();
		this.setText(dt.toString(dtf));
		this.dt = dt;

		this.setDay(this.getDay());
		this.setMonth(this.getMonth());
		
		setSelectedPart(selectedPart);
		
		return prev;
	}
	
	public DateTime getDateTime() {
		return new DateTime(getYear(), getMonth(), getDay(), 0, 0);
	}
	
	public int getMonth() {
		String text = this.getText();
		try {
			return Integer.parseInt(text.substring(0, text.indexOf('/')).trim());
		} catch(Exception e) {
			return -1;
		}
	}
	
	public int getDay() {
		String text = this.getText();
		try {
			return Integer.parseInt(text.substring(text.indexOf('/')+1, text.indexOf('/', text.indexOf('/')+1)).trim());
		} catch(Exception e) {
			return -1;
		}
	}
	
	public int getYear() {
		String text = this.getText();
		try {
			return Integer.parseInt(text.substring(text.indexOf('/', text.indexOf('/')+1)+1, text.length()).trim());
		} catch(Exception e) {
			return -1;
		}
	}
	
	public int daysInMonth() {
		return new DateTime(getYear(), getMonth(), 1, 1, 1).dayOfMonth().getMaximumValue();
	}
	
	public int setMonth(int month) {
		if (month > 12 || month < 1)
			return -1;
		
		String monthStr = Integer.toString(month);
		if (monthStr.length() == 1)
			monthStr = " " + monthStr;
		
		String text = this.getText();
		String now = text.substring(0, text.indexOf('/')+1);
		
		
		String[] a = text.split(now);
		this.setText(a[0] + monthStr + "/"+ a[1]);
		
		try {
			return Integer.parseInt(now.trim());
		} catch(Exception e) {
			return -1;
		}
	}
	
	public int setDay(int day) {
		if (day < 1 || day > daysInMonth())
			return -1;
		
		String dayStr = Integer.toString(day);
		if (dayStr.length() == 1)
			dayStr = " " + dayStr;
		
		String text = this.getText();
		String now = text.substring(text.indexOf('/'), text.indexOf('/', text.indexOf('/')+1)+1);
		
		String[] a = text.split(now);
		this.setText(a[0] + "/" + dayStr + "/" + a[1]);
		
		try {
			return Integer.parseInt(now.trim());
		} catch(Exception e) {
			return -1;
		}
	}
	
	public int setYear(int year) {
		if (year < 1)
			return -1;
		
		String yearStr = Integer.toString(year);
		if (yearStr.length() == 2)
			yearStr = "  "+yearStr;
		else if (yearStr.length() == 3)
			yearStr = " "+yearStr;
		
		String text = this.getText();
		String now = text.substring(text.indexOf('/', text.indexOf('/')+1), text.length());
		
		String[] a = text.split(now);
		this.setText(a[0] + "/" + yearStr);
		
		try {
			return Integer.parseInt(now.trim());
		} catch(Exception e) {
			return -1;
		}
	}
	
	protected int partForCaretPosition(int pos) {
		String text = this.getText();
		int dash1 = text.indexOf("/");
		int dash2 = text.indexOf("/", text.indexOf("/")+1);
		
		if (pos <= dash1) {
			return 1;
		} else if (pos <= dash2) {
			return 2;
		} else {
			return 3;
		}
	}
}

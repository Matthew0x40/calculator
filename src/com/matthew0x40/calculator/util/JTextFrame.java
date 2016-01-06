package com.matthew0x40.calculator.util;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JTextFrame extends JFrame {
	private static final long serialVersionUID = -6564715211304349434L;
	private JTextPane textArea;
	private JScrollPane scrollArea;
	static SimpleAttributeSet alignLeft = new SimpleAttributeSet();
	static SimpleAttributeSet alignCenter = new SimpleAttributeSet();
	static SimpleAttributeSet alignRight = new SimpleAttributeSet();
	static SimpleAttributeSet alignJustified = new SimpleAttributeSet();
	public static final int ALIGN_LEFT = 0, ALIGN_CENTER = 1, ALIGNED_RIGHT = 2, ALIGN_JUSTIFIED = 3;
	static SimpleAttributeSet bold = new SimpleAttributeSet();
	static SimpleAttributeSet sans_bold = new SimpleAttributeSet();
	static SimpleAttributeSet italic = new SimpleAttributeSet();
	static SimpleAttributeSet sans_italic = new SimpleAttributeSet();
	static SimpleAttributeSet strike = new SimpleAttributeSet();
	static SimpleAttributeSet sans_strike = new SimpleAttributeSet();
	static SimpleAttributeSet underline = new SimpleAttributeSet();
	static SimpleAttributeSet sans_underline = new SimpleAttributeSet();
	static SimpleAttributeSet subscript = new SimpleAttributeSet();
	static SimpleAttributeSet sans_subscript = new SimpleAttributeSet();
	static SimpleAttributeSet superscript = new SimpleAttributeSet();
	static SimpleAttributeSet sans_superscript = new SimpleAttributeSet();
	static {
		StyleConstants.setAlignment(alignLeft, StyleConstants.ALIGN_LEFT);
		StyleConstants.setAlignment(alignCenter, StyleConstants.ALIGN_CENTER);
		StyleConstants.setAlignment(alignRight, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setAlignment(alignJustified, StyleConstants.ALIGN_JUSTIFIED);
		StyleConstants.setBold(bold, true);
		StyleConstants.setBold(sans_bold, false);
		StyleConstants.setItalic(italic, true);
		StyleConstants.setItalic(sans_italic, false);
		StyleConstants.setStrikeThrough(strike, true);
		StyleConstants.setStrikeThrough(sans_strike, false);
		StyleConstants.setUnderline(underline, true);
		StyleConstants.setUnderline(sans_underline, false);
		
		StyleConstants.setSubscript(subscript, true);
		StyleConstants.setSubscript(sans_subscript, false);
		StyleConstants.setSuperscript(superscript, true);
		StyleConstants.setSuperscript(sans_superscript, false);
	}
	
	
	public JTextFrame() {
		super();
		initTextFrame();
	}

	public JTextFrame(String title) {
		super(title);
		initTextFrame();
	}
	
	private void initTextFrame() {
		textArea = new JTextPane() {
			private static final long serialVersionUID = -1764719687394256438L;

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				Util.applyQuality(g2d);
				super.paintComponent(g);
			}
		};
		scrollArea = new JScrollPane(textArea);
		this.add(scrollArea);
	}
	
	public JTextPane getTextPane() {
		return textArea;
	}
	
	public JScrollPane getScrollArea() {
		return scrollArea;
	}
	
	public void setBold(int offset, int length, boolean state, boolean replacePrevAttrib) {
		textArea.getStyledDocument().setCharacterAttributes(offset, length, state ? bold : sans_bold, replacePrevAttrib);
	}
	public void setItalic(int offset, int length, boolean state, boolean replacePrevAttrib) {
		textArea.getStyledDocument().setCharacterAttributes(offset, length, state ? italic : sans_italic, replacePrevAttrib);
	}
	public void setStrikeThrough(int offset, int length, boolean state, boolean replacePrevAttrib) {
		textArea.getStyledDocument().setCharacterAttributes(offset, length, state ? strike : sans_strike, replacePrevAttrib);
	}
	public void setUnderline(int offset, int length, boolean state, boolean replacePrevAttrib) {
		textArea.getStyledDocument().setCharacterAttributes(offset, length, state ? underline : sans_underline, replacePrevAttrib);
	}
	public void setSubscript(int offset, int length, boolean state, boolean replacePrevAttrib) {
		textArea.getStyledDocument().setCharacterAttributes(offset, length, state ? subscript : sans_subscript, replacePrevAttrib);
	}
	public void setSuperscript(int offset, int length, boolean state, boolean replacePrevAttrib) {
		textArea.getStyledDocument().setCharacterAttributes(offset, length, state ? superscript : sans_superscript, replacePrevAttrib);
	}
	
	public void setFontFamily(int offset, int length, String fam, boolean replacePrevAttrib) {
		SimpleAttributeSet font = new SimpleAttributeSet();
		StyleConstants.setFontFamily(font, fam);
		textArea.getStyledDocument().setCharacterAttributes(offset, length, font, replacePrevAttrib);
	}
	
	public void setFontSize(int offset, int length, int size, boolean replacePrevAttrib) {
		SimpleAttributeSet font_size = new SimpleAttributeSet();
		StyleConstants.setFontSize(font_size, size);
		textArea.getStyledDocument().setCharacterAttributes(offset, length, font_size, replacePrevAttrib);
	}
	
	public StyledDocument getStyledDocument() {
		return textArea.getStyledDocument();
	}
	
	public void align(int offset, int length, int alignType, boolean replacePrevAttrib) {
		StyledDocument doc = textArea.getStyledDocument();
		switch (alignType) {
		case 0:
			doc.setParagraphAttributes(offset, length, alignLeft, replacePrevAttrib);
			break;
		case 1:
			doc.setParagraphAttributes(offset, length, alignCenter, replacePrevAttrib);
			break;
		case 2:
			doc.setParagraphAttributes(offset, length, alignRight, replacePrevAttrib);
			break;
		case 3:
			doc.setParagraphAttributes(offset, length, alignJustified, replacePrevAttrib);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public void appendln(String str) {
		append(str+System.lineSeparator());
	}
	
	public void append(String str) {
		Document doc = textArea.getDocument();
		if (doc != null) {
			try {
				doc.insertString(doc.getLength(), str, null);
			} catch (BadLocationException e) {
			}
		}
	}
}

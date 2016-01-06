package com.matthew0x40.calculator.util;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.Frame;

public class Display extends Frame {
	private static final long serialVersionUID = 4066233670352841687L;
	public static final int DO_NOTHING_ON_CLOSE = 0;
	public static final int HIDE_ON_CLOSE = 1;
	public static final int DISPOSE_ON_CLOSE = 2;
	public static final int EXIT_ON_CLOSE = 3;
	private int defaultCloseOperation = HIDE_ON_CLOSE;
	
    public Display() throws HeadlessException {
        super();
        frameInit();
    }
    
    public Display(GraphicsConfiguration gc) {
        super(gc);
        frameInit();
    }
    
    public Display(String title) throws HeadlessException {
        super(title);
        frameInit();
    }
    
    public Display(String title, GraphicsConfiguration gc) {
        super(title, gc);
        frameInit();
    }
    
    protected void frameInit() {
    	enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
    }
	
	public void beep() {
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	
	private Dimension old_size = new Dimension(0, 0);
	private Dimension new_size = new Dimension(0, 0);
	
	@Override
	public void validate() {
		super.validate();
		new_size.width = getWidth();
		new_size.height = getHeight();
		if (old_size.equals(new_size)) {
			return;
		} else {
			render0();
		}
	}
	
	@Override
	public final void paint(Graphics g) {
		render0();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		createBufferStrategy(2);
	}
	
	public void render(Graphics g) {
	}
	
	private synchronized void render0() {
		BufferStrategy strategy = getBufferStrategy();
		if (strategy == null) {
			return;
		}
		// Render single frame
		do {
			// The following loop ensures that the contents of the
			// drawing buffer are consistent in case the underlying
			// surface was recreated
			
			do {
				Graphics g = strategy.getDrawGraphics();
				
				render(g);
				
				// Repeat the rendering if the drawing buffer contents
				// were restored
			} while (strategy.contentsRestored());

			// Display the buffer
			strategy.show();

			// Repeat the rendering if the drawing buffer was lost
		} while (strategy.contentsLost());
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            switch(defaultCloseOperation) {
              case HIDE_ON_CLOSE:
                 setVisible(false);
                 break;
              case DISPOSE_ON_CLOSE:
                 dispose();
                 break;
              case DO_NOTHING_ON_CLOSE:
                 default:
                 break;
              case EXIT_ON_CLOSE:
                  // This needs to match the checkExit call in
                  // setDefaultCloseOperation
                System.exit(0);
                break;
            }
        }
    }
	
	public void setDefaultCloseOperation(int operation) {
        if (operation != DO_NOTHING_ON_CLOSE &&
            operation != HIDE_ON_CLOSE &&
            operation != DISPOSE_ON_CLOSE &&
            operation != EXIT_ON_CLOSE) {
            throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE");
        }
        if (this.defaultCloseOperation != operation) {
            if (operation == EXIT_ON_CLOSE) {
                SecurityManager security = System.getSecurityManager();
                if (security != null) {
                    security.checkExit(0);
                }
            }
            int oldValue = this.defaultCloseOperation;
            this.defaultCloseOperation = operation;
            firePropertyChange("defaultCloseOperation", oldValue, operation);
        }
    }
	
	public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }
	
	static void test() {
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		System.setProperty("sun.awt.noerasebackground", "true");
		Display test = new Display("Test");
		test.setPreferredSize(new Dimension(200, 200));

		test.setDefaultCloseOperation(EXIT_ON_CLOSE);

		test.pack();
		test.setVisible(true);
	}
}
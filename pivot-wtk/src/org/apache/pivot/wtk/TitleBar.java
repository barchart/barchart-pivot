package org.apache.pivot.wtk;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Titlebar to be used with the {@link DesktopFrame} class which provides
 * template methods for styling the titlebar, inserting the control buttons
 * (see {@link TitleBarControl}) and managing the frame's state.
 * 
 * @author David Ray
 */
@SuppressWarnings("serial")
public abstract class TitleBar extends java.awt.Panel implements java.awt.event.MouseListener {
	public enum Location { LEFT, CENTER, RIGHT };
	
	List<IconizeListener> iconizeListeners = new ArrayList<IconizeListener>();
	
	protected TitleBarControl control;
	protected String title;
	
	public TitleBar() {
		this("");
	}
	
	public TitleBar(String title) {
		this.title = title;
		setLayout(new BorderLayout());
	}
	
	public void addIconizeListener(IconizeListener l) {
		if(l != null) {
			iconizeListeners.add(l);
		}
	}
	
	public void removeIconizeListener(IconizeListener l) {
		iconizeListeners.remove(l);
	}
	
	public void fireFrameWillBecomeIconized() {
		for(IconizeListener l : iconizeListeners) {
			l.frameWillIconize();
		}
	}
	
	public boolean isControlHit(MouseEvent m) {
		if(control != null) {
			return control.isControlHit(m);
		}
		return false;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public void mousePressed(MouseEvent m) {
		if(control == null) return;
		control.mousePressed(m);
	}
	
	@Override
	public void mouseReleased(MouseEvent m) {
		if(control == null) return;
		control.mouseReleased(m);
	}
	
	//NO-OP
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	/**
	 * Adds the component housing the window controls (i.e. minimize/maximize buttons)
	 * to this {@code TitleBar}
	 * 
	 * @param c		Subclass of {@link TitleBarControl}
	 */
	public void addTitleBarControl(TitleBarControl c) {
		this.control = c;
		if(this.control != null) {
			remove(control);
		}
		
		this.control = c;
		switch(control.getControlLocation()) {
			case LEFT: add(c, BorderLayout.WEST); break;
			case CENTER: add(c, BorderLayout.CENTER); break;
			case RIGHT : add(c, BorderLayout.EAST); break;
		}
	}
	
	/**
	 * Adds a corner logo to the frame.
	 * @param c	a java.awt.Component
	 */
	public void addSignature(java.awt.Component c) {
			switch(control.getControlLocation()) {
			//Opposite of the control
			case LEFT: add(c, BorderLayout.EAST); break;
			case RIGHT : add(c, BorderLayout.WEST); break;
		}
	}
	
	/**
	 * Returns this {@code TitleBar}'s {@link TitleBarControl} if
	 * any exists.
	 * 
	 * @return	the installed {@code TitleBarControl}
	 */
	public TitleBarControl getTitleBarControl() {
		return control;
	}
	
	int paintCount = 3;
	@Override
	public void paint(Graphics g) {
		if(paintCount == -1) {
			paintCount = 3;
			return;
		}
		
		--paintCount;
		
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        fillTitleBar(g2);
        
        //if clip is for child(i.e. TitleBarControl) don't paint it now.
        //The clip indicates what's being painted. If the clip is the
        //size of the titlebar then paint the title otherwise if the 
        //clip is the clip representing the control, don't paint the 
        //title into it, since we don't want them to overlap.
        if(g2.getClipBounds().getSize().equals(getSize())) {
        	paintTitle(g2);
        }
        //Paint the child component(TitleBarControl)
        super.paint(g);
        
        //Associated with paintCount above. Don't repaint the Title
        //Bar control bounds because it causes flicker (i.e. x > 0)
        if(g.getClipBounds().x == 0) {
        	repaint();	
        }
    }
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	protected abstract void paintTitle(Graphics2D g2);
	
	protected abstract void fillTitleBar(Graphics2D g2);
	
	/**
	 * Listened to by those classes wishing to be informed
	 * when this {@link TitleBar}'s frame will be iconized.
	 * 
	 * @author David Ray
	 */
	public interface IconizeListener {
		public void frameWillIconize();
	}

}

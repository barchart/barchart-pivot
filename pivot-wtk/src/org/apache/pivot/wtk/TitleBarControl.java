package org.apache.pivot.wtk;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;



/**
 * Classes which are intended to provide titlebar control implementations 
 * should be an extension of this class when used with the {@link TempFrame}
 * 
 * @author David Ray
 */
@SuppressWarnings("serial")
public abstract class TitleBarControl extends java.awt.Component implements MouseListener, MouseMotionListener {
	private TitleBar.Location titleBarLoc = TitleBar.Location.RIGHT;
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	/**
	 * Sets the title bar location, see {@link TitleBar.Location}
	 * 
	 * @param loc
	 */
	public void setControlLocation(TitleBar.Location loc) {
		if(loc == null) throw new NullPointerException("location can not be null");
		this.titleBarLoc = loc;
	}

	/**
	 * Returns the {@link TitleBar.Location}
	 * @return	the {@link TitleBar.Location} of the control
	 */
	public TitleBar.Location getControlLocation() {
		return this.titleBarLoc;
	}
	
	public boolean isControlHit(MouseEvent e) {
		return this.getBounds().contains(e.getPoint());
	}
}

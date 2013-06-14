package org.apache.pivot.wtk;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Used in conjunction with the {@link DesktopFrame} to provide an OSX-like 
 * window title bar.
 *  
 * @author David Ray
 *
 */
@SuppressWarnings("serial")
public class OSXTitleBar extends TitleBar {
	
	
	public OSXTitleBar() {
		addTitleBarControl(createTitleBarControl());
	}
	
	@Override
	protected void paintTitle(Graphics2D g2) {
		FontMetrics fm = g2.getFontMetrics();
        int titleWidth = SwingUtilities.computeStringWidth(fm, title);
        g2.setColor(Color.WHITE);
        Rectangle r = g2.getClipBounds();
        int textX = (r.width - titleWidth) / 2;
        int textY = ((r.height) / 2) - ((fm.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
        g2.drawString(title, textX + 1, textY + 1);
        g2.setColor(Color.BLACK);
        g2.drawString(title, textX, textY);
	}
	
	@Override
	protected void fillTitleBar(Graphics2D g2) {
		Paint oldPaint = g2.getPaint();
        Stroke oldStroke = g2.getStroke();
        
        g2.setColor(new Color(241,241,241));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(0, 0, getWidth(), 0);
        
        LinearGradientPaint p = new LinearGradientPaint(
        	1f, 1f, 1f, getHeight() - 1,
        		new float[] { 0.0f, 0.499f, 0.5f, 1.0f },
        			new Color[] { new Color(230, 230, 230), new Color(202, 202, 202),
        				new Color(202, 202, 202), new Color(178, 178, 178) });
        g2.setPaint(p);
        g2.fillRect(0, 0, getWidth(), this.getHeight() - 1);
        
        
        g2.setPaint(oldPaint);
        g2.setColor(new Color(104, 104, 104));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(getX(), getHeight() - 1, getWidth(), getHeight() - 1);
        
        g2.setStroke(oldStroke);
	}
	
	private TitleBarControl createTitleBarControl() {
		OSXTitleBarControl control = new OSXTitleBarControl();
		control.setPreferredSize(new Dimension(60,20));
		control.setControlLocation(TitleBar.Location.LEFT);
		return control;
	}
	
	class OSXTitleBarControl extends TitleBarControl {
		private static final int SIDE_MARGIN = 8;
    	private Rectangle2D minimizeRect;
    	private Rectangle2D maximizeRect;
    	private Rectangle2D closeRect;
    	
    	private boolean mouseOverControl;
    	private boolean maskMinimized;
    	private boolean maskMaximized;
    	private boolean maskClosed;
    	
    	private java.awt.Frame hostFrame;
    	
    	private boolean shapesInitialized;
    	
    	public OSXTitleBarControl() {
    		getParentFrame();
    	}
    	
    	@Override
    	public void paint(Graphics graphics) {
    		Graphics2D g = (Graphics2D)graphics;
    		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    		
    		if(!shapesInitialized) {
    			createControlShapes();
    			installListeners();
    			shapesInitialized = true;
    		}
    		
    		paintRedButton(g);
    		paintYellowButton(g);
    		paintGreenButton(g);
    	}  
    	
    	private void paintRedButton(Graphics2D g2) {
    		Color white = Color.white;
    		Paint old = g2.getPaint();
    		Stroke oldStroke = g2.getStroke();
    		g2.setStroke(new BasicStroke(2));
    		
    		g2.setColor(Color.WHITE);
    		g2.drawOval((int)closeRect.getX(), (int)closeRect.getY() + 1, (int)closeRect.getWidth() - 1, (int)closeRect.getHeight());
    		g2.setColor(new Color(134, 56, 51));
    		g2.drawOval((int)closeRect.getX(), (int)closeRect.getY(), (int)closeRect.getWidth(), (int)closeRect.getHeight());
    		
    		Paint p = new RadialGradientPaint(new Point2D.Double(closeRect.getCenterX(), closeRect.getCenterY()),
    			(float)closeRect.getWidth() / 2.0f, new float[] { 0.0f, 1.0f }, new Color[] { new Color(255,90,79), new Color(211,74,54) });
    		g2.setPaint(p);
    		g2.fillOval((int)closeRect.getX() + 1, (int)closeRect.getY() + 1, (int)closeRect.getWidth(), (int)closeRect.getHeight() - 1);
    		
    		p = new RadialGradientPaint(new Point2D.Double(closeRect.getCenterX(), closeRect.getY() + (closeRect.getHeight() * 1.5) + 5),
    			(float)closeRect.getWidth() - 2, new Point2D.Double(closeRect.getCenterX(), closeRect.getHeight() * 3),
    				new float[] { 0.0f, 00.8f }, new Color[] { new Color(white.getRed(), white.getGreen(), white.getBlue(), 175), 
    					new Color(white.getRed(), white.getGreen(), white.getBlue(), 0) }, RadialGradientPaint.CycleMethod.NO_CYCLE,
    						RadialGradientPaint.ColorSpaceType.SRGB, AffineTransform.getScaleInstance(1.0, 0.5));
    		g2.setPaint(p);
    		g2.fillOval((int)closeRect.getX() + 1, (int)closeRect.getY() + 1, (int)closeRect.getWidth(), (int)closeRect.getHeight() - 1);
    		
    		p = new RadialGradientPaint(new Point2D.Double(closeRect.getCenterX(), closeRect.getCenterY()), (float)closeRect.getWidth() * 1.5f,
    			new Point2D.Double(closeRect.getCenterX(), 6), new float[] { 0.0f, 0.3f }, new Color[] { new Color(white.getRed(), white.getGreen(), white.getBlue(), 255),
    				new Color(white.getRed(), white.getGreen(), white.getBlue(), 0) }, RadialGradientPaint.CycleMethod.NO_CYCLE);
    		g2.setPaint(p);
    		g2.fillOval((int)closeRect.getX() + 1, (int)closeRect.getY() + 1, (int)closeRect.getWidth(), (int)closeRect.getHeight() - 1);
    		
    		g2.setPaint(old);
    		g2.setStroke(oldStroke);
    		
    		if(mouseOverControl) {
    			g2.setColor(new Color(119,19,17));//90,11,10 -=-  119,19,17
    			g2.setStroke(new BasicStroke(2));
    			g2.drawLine((int)closeRect.getX() + 3, (int)closeRect.getY() + 3, (int)closeRect.getMaxX() - 3, (int)closeRect.getMaxY() - 3);
    			g2.drawLine((int)closeRect.getX() + 3, (int)closeRect.getMaxY() - 3, (int)closeRect.getMaxX() - 3, (int)closeRect.getY() + 3);
    			g2.setStroke(oldStroke);
    		}
    		
    		if(maskClosed) {
    			g2.setColor(new Color(0,0,0,75));
    			g2.fillOval((int)closeRect.getX() + 1, (int)closeRect.getY() + 1, (int)closeRect.getWidth(), (int)closeRect.getHeight() - 1);
    		}
    	}
    	
    	private void paintYellowButton(Graphics2D g2) {
    		Color white = Color.white;
    		Paint old = g2.getPaint();
    		Stroke oldStroke = g2.getStroke();
    		g2.setStroke(new BasicStroke(2));
    		
    		g2.setColor(Color.WHITE);
    		g2.drawOval((int)minimizeRect.getX(), (int)minimizeRect.getY() + 1, (int)minimizeRect.getWidth() - 1, (int)minimizeRect.getHeight());
    		g2.setColor(new Color(115, 84, 47));
    		g2.drawOval((int)minimizeRect.getX(), (int)minimizeRect.getY(), (int)minimizeRect.getWidth(), (int)minimizeRect.getHeight());
    		
    		Paint p = new RadialGradientPaint(new Point2D.Double(minimizeRect.getCenterX(), minimizeRect.getCenterY()),
    			(float)minimizeRect.getWidth() / 2.0f, new float[] { 0.0f, 1.0f }, new Color[] { new Color(238,202,99), new Color(237,166,72) });
    		g2.setPaint(p);
    		g2.fillOval((int)minimizeRect.getX() + 1, (int)minimizeRect.getY() + 1, (int)minimizeRect.getWidth(), (int)minimizeRect.getHeight() - 1);
    		
    		p = new RadialGradientPaint(new Point2D.Double(minimizeRect.getCenterX(), minimizeRect.getY() + (minimizeRect.getHeight() * 1.5) + 5),
    			(float)minimizeRect.getWidth() - 2, new Point2D.Double(minimizeRect.getCenterX(), minimizeRect.getHeight() * 3),
    				new float[] { 0.0f, 00.8f }, new Color[] { new Color(white.getRed(), white.getGreen(), white.getBlue(), 250), 
    					new Color(white.getRed(), white.getGreen(), white.getBlue(), 0) }, RadialGradientPaint.CycleMethod.NO_CYCLE,
    						RadialGradientPaint.ColorSpaceType.SRGB, AffineTransform.getScaleInstance(1.0, 0.5));
    		g2.setPaint(p);
    		g2.fillOval((int)minimizeRect.getX() + 1, (int)minimizeRect.getY() + 1, (int)minimizeRect.getWidth(), (int)minimizeRect.getHeight() - 1);
    		
    		p = new RadialGradientPaint(new Point2D.Double(minimizeRect.getCenterX(), minimizeRect.getCenterY()), (float)minimizeRect.getWidth() * 1.5f,
    			new Point2D.Double(minimizeRect.getCenterX(), 6), new float[] { 0.0f, 0.3f }, new Color[] { new Color(white.getRed(), white.getGreen(), white.getBlue(), 255),
    				new Color(white.getRed(), white.getGreen(), white.getBlue(), 0) }, RadialGradientPaint.CycleMethod.NO_CYCLE);
    		g2.setPaint(p);
    		g2.fillOval((int)minimizeRect.getX() + 1, (int)minimizeRect.getY() + 1, (int)minimizeRect.getWidth(), (int)minimizeRect.getHeight() - 1);
    		
    		g2.setPaint(old);
    		g2.setStroke(oldStroke);
    		
    		if(mouseOverControl) {
    			g2.setColor(new Color(102,36,0));
    			g2.drawLine((int)minimizeRect.getX() + 2, (int)minimizeRect.getCenterY(), (int)minimizeRect.getMaxX() - 2, (int)minimizeRect.getCenterY());
    			g2.setColor(new Color(166,99,1));
    			g2.drawLine((int)minimizeRect.getX() + 2, (int)minimizeRect.getCenterY() + 1, (int)minimizeRect.getMaxX() - 2, (int)minimizeRect.getCenterY() + 1);
    		}
    		
    		if(maskMinimized) {
    			g2.setColor(new Color(0,0,0,75));
    			g2.fillOval((int)minimizeRect.getX() + 1, (int)minimizeRect.getY() + 1, (int)minimizeRect.getWidth(), (int)minimizeRect.getHeight() - 1);
    		}
    	}
    	
    	private void paintGreenButton(Graphics2D g2) {
    		Color white = Color.white;
    		Paint old = g2.getPaint();
    		Stroke oldStroke = g2.getStroke();
    		g2.setStroke(new BasicStroke(2));
    		
    		g2.setColor(Color.WHITE);
    		g2.drawOval((int)maximizeRect.getX(), (int)maximizeRect.getY() + 1, (int)maximizeRect.getWidth() - 1, (int)maximizeRect.getHeight());
    		g2.setColor(new Color(74, 96, 59));
    		g2.drawOval((int)maximizeRect.getX(), (int)maximizeRect.getY(), (int)maximizeRect.getWidth(), (int)maximizeRect.getHeight());
    		
    		Paint p = new RadialGradientPaint(new Point2D.Double(maximizeRect.getCenterX(), maximizeRect.getCenterY()),
    			(float)maximizeRect.getWidth() / 2.0f, new float[] { 0.0f, 1.0f }, new Color[] { new Color(194,242,147), new Color(116,174,86) });
    		g2.setPaint(p);
    		g2.fillOval((int)maximizeRect.getX() + 1, (int)maximizeRect.getY() + 1, (int)maximizeRect.getWidth(), (int)maximizeRect.getHeight() - 1);
    		
    		p = new RadialGradientPaint(new Point2D.Double(maximizeRect.getCenterX(), maximizeRect.getY() + (maximizeRect.getHeight() * 1.5) + 5),
    			(float)maximizeRect.getWidth() - 2, new Point2D.Double(maximizeRect.getCenterX(), maximizeRect.getHeight() * 3),
    				new float[] { 0.0f, 00.8f }, new Color[] { new Color(white.getRed(), white.getGreen(), white.getBlue(), 250), 
    					new Color(white.getRed(), white.getGreen(), white.getBlue(), 0) }, RadialGradientPaint.CycleMethod.NO_CYCLE,
    						RadialGradientPaint.ColorSpaceType.SRGB, AffineTransform.getScaleInstance(1.0, 0.5));
    		g2.setPaint(p);
    		g2.fillOval((int)maximizeRect.getX() + 1, (int)maximizeRect.getY() + 1, (int)maximizeRect.getWidth(), (int)maximizeRect.getHeight() - 1);
    		
    		p = new RadialGradientPaint(new Point2D.Double(maximizeRect.getCenterX(), maximizeRect.getCenterY()), (float)maximizeRect.getWidth() * 1.5f,
    			new Point2D.Double(maximizeRect.getCenterX(), 6), new float[] { 0.0f, 0.3f }, new Color[] { new Color(white.getRed(), white.getGreen(), white.getBlue(), 255),
    				new Color(white.getRed(), white.getGreen(), white.getBlue(), 0) }, RadialGradientPaint.CycleMethod.NO_CYCLE);
    		g2.setPaint(p);
    		g2.fillOval((int)maximizeRect.getX() + 1, (int)maximizeRect.getY() + 1, (int)maximizeRect.getWidth(), (int)maximizeRect.getHeight() - 1);
    		
    		g2.setPaint(old);
    		g2.setStroke(oldStroke);
    		
    		if(mouseOverControl) {
    			g2.setColor(new Color(0,53,-0));
    			g2.setStroke(new BasicStroke(1));
    			g2.drawLine((int)maximizeRect.getX() + 2, (int)maximizeRect.getCenterY(), (int)maximizeRect.getMaxX() - 2, (int)maximizeRect.getCenterY());
    			g2.setColor(new Color(45,118,0));
    			g2.drawLine((int)maximizeRect.getX() + 2, (int)maximizeRect.getCenterY() + 1, (int)maximizeRect.getMaxX() - 2, (int)maximizeRect.getCenterY() + 1);
    			g2.setStroke(new BasicStroke(2));
    			g2.setColor(new Color(0,53,-0));
    			g2.drawLine((int)maximizeRect.getCenterX(), (int)maximizeRect.getY() + 2, (int)maximizeRect.getCenterX(), (int)maximizeRect.getY() + 3);
    			g2.setColor(new Color(45,118,0));
    			g2.drawLine((int)maximizeRect.getCenterX(), (int)maximizeRect.getY() + 3, (int)maximizeRect.getCenterX(), (int)maximizeRect.getMaxY() - 2);
    		}
    		
    		if(maskMaximized) {
    			g2.setColor(new Color(0,0,0,75));
    			g2.fillOval((int)maximizeRect.getX() + 1, (int)maximizeRect.getY() + 1, (int)maximizeRect.getWidth(), (int)maximizeRect.getHeight() - 1);
    		}
    	}
    	
    	private void createControlShapes() {
    		int y = ((getHeight() - 10) / 2) - 1;
    		closeRect = new Rectangle2D.Double(SIDE_MARGIN, y, 10, 10);
    		minimizeRect = new Rectangle2D.Double(SIDE_MARGIN + 20, y, 10, 10);
    		maximizeRect = new Rectangle(SIDE_MARGIN + 40, y, 10, 10);
    	}
    	
    	private void installListeners() {
    		final Rectangle2D localCloseRect = new Rectangle2D.Double(
				closeRect.getX() - 4, closeRect.getY() - 4, 
					closeRect.getWidth() + 8, closeRect.getHeight() + 8);
			final Rectangle2D localMinimizeRect = new Rectangle2D.Double(
				minimizeRect.getX() - 4, minimizeRect.getY() - 4, 
					minimizeRect.getWidth() + 8, minimizeRect.getHeight() + 8);
			final Rectangle2D localMaximizeRect = new Rectangle2D.Double(
				maximizeRect.getX() - 4, maximizeRect.getY() - 4, 
					maximizeRect.getWidth() + 8, maximizeRect.getHeight() + 8);
			
			Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
				 public void eventDispatched(AWTEvent e) {
					 if(e.getID() == MouseEvent.MOUSE_RELEASED && mouseOverControl) {
						 maskMinimized = maskMaximized = maskClosed = mouseOverControl = false;
						 repaint();
					 }
				 }
			}, AWTEvent.MOUSE_EVENT_MASK);
    			
    		addMouseListener(new MouseAdapter() {
    			public void mouseReleased(MouseEvent e) {
    				if(localMinimizeRect.contains(e.getPoint())) {
    					fireFrameWillBecomeIconized();
    					hostFrame.setExtendedState(JFrame.ICONIFIED);
    				}else if(localMaximizeRect.contains(e.getPoint())) {
    					if((hostFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
    						hostFrame.setExtendedState(JFrame.NORMAL);
    					}else{
    						hostFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    					}
    				}else if(localCloseRect.contains(e.getPoint())) {
    					WindowEvent wev = new WindowEvent(hostFrame, WindowEvent.WINDOW_CLOSING);
    	                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    				}
    				maskMinimized = maskMaximized = maskClosed = false;
    				repaint();
    			}
    			public void mousePressed(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if(localCloseRect.contains(p)) {
						maskClosed = true;
						maskMinimized = false;
						maskMaximized = false;
					}else if(localMinimizeRect.contains(p)) {
						maskClosed = false;
						maskMinimized = true;
						maskMaximized = false;
					}else if(localMaximizeRect.contains(p)) {
						maskClosed = false;
						maskMinimized = false;
						maskMaximized = true;
					}
    				repaint();
    			}
    		});
    		OSXTitleBarControl.this.addMouseMotionListener(new MouseAdapter() {
    			public void mouseMoved(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if(localCloseRect.contains(p) || localMinimizeRect.contains(p) || localMaximizeRect.contains(p)) {
    					mouseOverControl = true;
    				}else{
    					mouseOverControl = false;
    					maskClosed = false;
						maskMinimized = false;
						maskMaximized = false;
    				}
    				repaint();
    			}
    			public void mouseDragged(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if(localCloseRect.contains(p) || localMinimizeRect.contains(p) || localMaximizeRect.contains(p)) {
    					mouseOverControl = true;
    				}else{
    					maskClosed = false;
						maskMinimized = false;
						maskMaximized = false;
						repaint();
    				}
    			}
    		});
    		OSXTitleBar.this.addMouseMotionListener(new MouseAdapter() {
    			public void mouseMoved(MouseEvent m) {
    				boolean prev = mouseOverControl;
    				mouseOverControl = false;
    				maskClosed = false;
					maskMinimized = false;
					maskMaximized = false;
    				if(prev) {
    					repaint();
    				}
    			}
    			public void mouseDragged(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if(localCloseRect.contains(p) || localMinimizeRect.contains(p) || localMaximizeRect.contains(p)) {
    					mouseOverControl = true;
    				}else{
    					maskClosed = false;
						maskMinimized = false;
						maskMaximized = false;
						repaint();
    				}
    			}
    		});
    	}
    	
    	private void getParentFrame() {
    		(new Thread() {
    			public void run() {
    				while(hostFrame == null) {
	    				try{ Thread.sleep(100); }catch(Exception e) { e.printStackTrace(); }
	    				SwingUtilities.invokeLater(new Runnable() {
	    					public void run() {
	    						hostFrame = (java.awt.Frame)SwingUtilities.getAncestorOfClass(DesktopFrame.class, OSXTitleBarControl.this);
	    					}
	    				});
    				}
    			}
    		}).start();
    	}
	}
}

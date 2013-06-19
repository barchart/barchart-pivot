package org.apache.pivot.wtk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Component which represents the styling of the top border of
 * a given frame. This {@link TitleBar} has the particular styling
 * for a BarChart custom style. This title bar also defines a
 * {@link TitleBarControl} which handles the minimize, maximize
 * and close functions.
 * 
 * @author David Ray
 *
 */
public class BarChartTitleBar extends TitleBar {

	private static final long serialVersionUID = 8331424255756031127L;
	
	Timer t = null;
	final String s = "A Mikey Mike Production";
	int strLoc = Integer.MIN_VALUE;
	int offset = 0;
	boolean animating = false;
	Timer repaintTimer = null;
	
	int paintCountDown = 3;
	
	private DesktopFrame hostFrame;
	
	public BarChartTitleBar() {
		addTitleBarControl(createTitleBarControl());
		addSignature(createSignatureLogo());
		getParentFrame();
	}

	@Override
	protected void paintTitle(Graphics2D g2) {
		g2.setFont(g2.getFont().deriveFont(Font.BOLD).deriveFont(16f));
		FontMetrics fm = g2.getFontMetrics();
		int titleWidth = SwingUtilities.computeStringWidth(fm, title);
        g2.setColor(Color.WHITE.darker());
        Rectangle r = g2.getClipBounds();
        int textX = (r.width - titleWidth) / 2;
        int textY = ((r.height) / 2) - ((fm.getAscent() + fm.getDescent()) / 2) + fm.getAscent();
        g2.drawString(title, textX, textY);
    }
	
	@Override
	protected void fillTitleBar(Graphics2D g2) {
		Rectangle r = g2.getClipBounds();
		if(r.x > 0) return;
		
		Paint oldPaint = g2.getPaint();
        Stroke oldStroke = g2.getStroke();
        
        g2.setColor(new Color(241,241,241));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(0, 0, getWidth(), 0);
        
        LinearGradientPaint lgp = new LinearGradientPaint(
            new Point2D.Double(0,0), new Point2D.Double(0, r.height),
            	new float[] {0.0f, 1.0f}, new Color[] { new Color(120, 120, 120), Color.BLACK });
        g2.setPaint(lgp);
        g2.fillRect(0, 0, getWidth(), this.getHeight() - 1);
        g2.setPaint(oldPaint);
        
        g2.setPaint(oldPaint);
        g2.setColor(new Color(104, 104, 104));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(getX(), getHeight() - 1, getWidth(), getHeight() - 1);
        
        g2.setStroke(oldStroke);
        
        if(animating) {
        	g2.setClip(getBounds());
        	g2.setColor(Color.WHITE);
        	strLoc = - SwingUtilities.computeStringWidth(getFontMetrics(getFont()), s);
        	g2.drawString(s, strLoc + offset, 20);
        }
	}
	
	private TitleBarControl createTitleBarControl() {
		final WindowsTitleBarControl control = new WindowsTitleBarControl();
		control.setPreferredSize(new Dimension(120,20));
		control.setControlLocation(TitleBar.Location.RIGHT);
		
		repaintTimer = new Timer(250, new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				java.awt.Point p = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(p, control);
				if(!minus.contains(p) && !plus.contains(p) && !close.contains(p)) {
					overPlus = false;
					overMinus = false;
					overClose = false;
					control.repaint();
				}
			}
		});
		repaintTimer.setRepeats(false);
		
		return control;
	}
	
	private SignatureLogo createSignatureLogo() {
		SignatureLogo logo = new SignatureLogo();
		logo.setPreferredSize(new Dimension(60,20));
		return logo;
	}
	
	@SuppressWarnings("serial")
	public class SignatureLogo extends java.awt.Component implements MouseListener, MouseMotionListener {
		private double rotationAngle = 0.0d;
		private BufferedImage logo = createLogo(Color.WHITE, new Color(95, 95, 95));
		
		public SignatureLogo() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
	        
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        int x = 5;
	        int y = 6;
	        int imgCenterX = x + this.logo.getWidth() / 2;
	        int imgCenterY = y + this.logo.getHeight() / 2 - 2;
	        g2.translate(imgCenterX, imgCenterY);
	        double angle = -getRotation();
	        g2.rotate(angle);
	        g2.translate(-imgCenterX, -imgCenterY);
	        g2.drawImage(this.logo, x, y, null); 
	    }
		
		private double getRotation() {
			return rotationAngle;
		}
		
		private BufferedImage createLogo(Color bg, Color fg) {
			BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D)img.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			int lx = 0;
			g2.setColor(bg);
	        Arc2D lLogo = new Arc2D.Double(lx, 0, 16, 16, 90, 180, Arc2D.OPEN);
	        g2.draw(lLogo);
	        g2.drawLine(lx + 8, 0, lx + 8, 4);
	        g2.drawLine(lx + 8, 4, lx + 4, 4);
	        g2.drawLine(lx + 4, 4, lx + 4, 6);
	        g2.drawLine(lx + 4, 6, lx + 8, 6);
	        g2.drawLine(lx + 8, 6, lx + 8, 15);
	                
	        Arc2D rLogo = new Arc2D.Double(lx + 2, 0, 18, 17, 90, -180, Arc2D.OPEN);
	        g2.fill(rLogo);
	        g2.setColor(Color.BLACK);
	        g2.fillRect(lx + 10, 10, 4, 2);
	        
	        g2.dispose();
	        return img;
		}
		
		public void mousePressed(MouseEvent m) {
			if(getBounds().contains(m.getPoint())) {
				rotationAngle = Math.PI;
				repaint();
			}
		}
		
		public void mouseReleased(MouseEvent m) {
			rotationAngle = 0;
			
			if(m.getButton() == MouseEvent.BUTTON2 && (t == null || !t.isRunning())) {
				t = new Timer(0, new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						if(strLoc + offset > 69) {
							t.stop();
							t = null;
							offset = strLoc;
							animating = false;
							BarChartTitleBar.this.repaint();
						}else{
							offset += 3;
							BarChartTitleBar.this.repaint();
						}
					}
				});
				animating = true;
				t.setRepeats(true);
				t.setDelay(50);
				t.start();
			}
			repaint();
		}
	}
	
	java.awt.Point mp = new java.awt.Point();
	Rectangle minus = new Rectangle();
	Rectangle plus = new Rectangle();
	Rectangle close = new Rectangle();
	boolean overClose = false;
	boolean overPlus = false;
	boolean overMinus = false;
	@SuppressWarnings("serial")
	public class WindowsTitleBarControl extends TitleBarControl {
		public WindowsTitleBarControl() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		public void update(Graphics g) {
			paint(g);
		}
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setColor(Color.white);
			Rectangle r = g.getClipBounds();
			r.height -= 1;
			r.width -= 1;
			
			Rectangle xBox = new Rectangle(r.width - 43, 3, 31, 22);
			close = xBox;
			
    		Color topColor = new Color(20, 20, 20);
    		Color bottomColor = new Color(120, 120, 120);
    		if(overClose) {
    			topColor = new Color(120, 120, 120);
    		}
			    		
    		RoundRectangle2D e = new RoundRectangle2D.Double(xBox.x, xBox.y, xBox.width, xBox.height, 10, 10);
    		LinearGradientPaint topHalfX = new LinearGradientPaint(
            new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
            	new float[] {0.0f, 1.0f}, new Color[] { topColor, bottomColor });
    		LinearGradientPaint botHalfX = new LinearGradientPaint(
	            new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
	            	new float[] {0.0f, 1.0f}, new Color[] { bottomColor, topColor });
    		g2.setPaint(topHalfX);
    		g2.draw(e);
   		
    		Stroke wide = new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    		Stroke thin = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    		g2.setStroke(wide);
    		g2.drawLine(xBox.x + 5, xBox.y + 5, xBox.x + 26, xBox.y + xBox.height - 5);
    		g2.drawLine(xBox.x + 5, xBox.y + xBox.height - 5, xBox.x + 26, xBox.y + 5);
    		
    		g2.setStroke(thin);
    		g2.setPaint(botHalfX);
    		g2.drawLine(xBox.x + 5, xBox.y + 5, xBox.x + 26, xBox.y + xBox.height - 5);
    		g2.drawLine(xBox.x + 5, xBox.y + xBox.height - 5, xBox.x + 26, xBox.y + 5);
    		
    		////////////////////
    		
    		g2.setStroke(new BasicStroke(1));
    		topColor = new Color(20, 20, 20);
    		bottomColor = new Color(120, 120, 120);
    		if(overPlus) {
    			topColor = new Color(120, 120, 120);
    		}
    		xBox = new Rectangle(r.width - 81, 3, 31, 22);
    		plus = xBox;
    		e = new RoundRectangle2D.Double(xBox.x, xBox.y, xBox.width, xBox.height, 10, 10);
    		topHalfX = new LinearGradientPaint(
            new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
            	new float[] {0.0f, 1.0f}, new Color[] { topColor, bottomColor });
    		botHalfX = new LinearGradientPaint(
	            new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
	            	new float[] {0.0f, 1.0f}, new Color[] { bottomColor, topColor });
    		g2.setPaint(topHalfX);
    		g2.draw(e);
    		
    		topHalfX = new LinearGradientPaint(
    			new Point2D.Double(xBox.x, xBox.y + 5), new Point2D.Double(xBox.x, xBox.y + xBox.height - 5),
    				new float[] {0.0f, 1.0f}, new Color[] { topColor, bottomColor });
    		botHalfX = new LinearGradientPaint(
	            new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
	            	new float[] {0.0f, 1.0f}, new Color[] { bottomColor, topColor });
    		g2.setPaint(topHalfX);
    		g2.setStroke(wide);
    		g2.drawLine(xBox.x + 5, xBox.y + xBox.height / 2, xBox.x + 26, xBox.y + xBox.height / 2);
    		g2.drawLine(xBox.x + 1 + xBox.width / 2, xBox.y + 5, xBox.x + 1 + xBox.width / 2, xBox.y + xBox.height - 5);
    		
//    		g2.setColor(topColor);
    		g2.setStroke(thin);
    		g2.setPaint(botHalfX);
    		g2.drawLine(xBox.x + 5, xBox.y + xBox.height / 2, xBox.x + 26, xBox.y + xBox.height / 2);
    		g2.drawLine(xBox.x + 1 + xBox.width / 2, xBox.y + 5, xBox.x + 1 + xBox.width / 2, xBox.y + xBox.height - 5);
    		
    		////////////////////
    		
    		g2.setStroke(new BasicStroke(1));
    		topColor = new Color(20, 20, 20);
    		bottomColor = new Color(120, 120, 120);
    		if(overMinus) {
    			topColor = new Color(120, 120, 120);
    		}
    		xBox = new Rectangle(r.width - 119, 3, 31, 22);
    		minus = xBox;
    		e = new RoundRectangle2D.Double(xBox.x, xBox.y, xBox.width, xBox.height, 10, 10);
    		topHalfX = new LinearGradientPaint(
    			new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
        			new float[] {0.0f, 1.0f}, new Color[] { topColor, bottomColor });
        	botHalfX = new LinearGradientPaint(
            new Point2D.Double(xBox.x, xBox.y), new Point2D.Double(xBox.x, xBox.y + xBox.height),
            	new float[] {0.0f, 1.0f}, new Color[] { bottomColor, topColor });
    	    		g2.draw(e);
    	    g2.setPaint(topHalfX);
    		g2.draw(e);
    		
    		topHalfX = new LinearGradientPaint(
    			new Point2D.Double(xBox.x, xBox.y + (xBox.height / 2) - 3), new Point2D.Double(xBox.x, (xBox.y + xBox.height / 2) + 3),
    				new float[] {0.0f, 1.0f}, new Color[] { topColor, bottomColor });
    		botHalfX = new LinearGradientPaint(
    			new Point2D.Double(xBox.x, (xBox.y + xBox.height / 2) - 3), new Point2D.Double(xBox.x, (xBox.y + xBox.height / 2) + 3),
	            	new float[] {0.0f, 1.0f}, new Color[] { bottomColor, topColor });
    		g2.setStroke(wide);
    		g2.drawLine(xBox.x + 5, xBox.y + xBox.height / 2, xBox.x + 26, xBox.y + xBox.height / 2);
    		
    		g2.setStroke(thin);
    		g2.setPaint(botHalfX);
    		g2.drawLine(xBox.x + 5, xBox.y + xBox.height / 2, xBox.x + 26, xBox.y + xBox.height / 2);
    		
    		
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			mp = e.getPoint();
			if(plus.contains(e.getPoint())) {
				overPlus = true;
				overMinus = false;
				overClose = false;
			}else if(close.contains(e.getPoint())) {
				overPlus = false;
				overMinus = false;
				overClose = true;
			}else if(minus.contains(e.getPoint())) {
				overPlus = false;
				overMinus = true;
				overClose = false;
			}else{
				overPlus = false;
				overMinus = false;
				overClose = false;
			}
			repaint();
			
			repaintTimer.restart();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(minus.contains(e.getPoint())) {
				fireFrameWillBecomeIconized();
				hostFrame.setExtendedState(JFrame.ICONIFIED);
			}else if(plus.contains(e.getPoint())) {
				if((hostFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
					hostFrame.setExtendedState(JFrame.NORMAL);
					hostFrame.setBordersVisible(true);
					hostFrame.setBorderSize(5);
					hostFrame.invalidate();
					hostFrame.repaint();
				}else{
					hostFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					hostFrame.setBordersVisible(false);
					hostFrame.setBorderSize(0);
					hostFrame.invalidate();
					hostFrame.repaint();
				}
			}else if(close.contains(e.getPoint())) {
				WindowEvent wev = new WindowEvent(hostFrame, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
			}
			
			repaint();
		}
	}

	private void getParentFrame() {
		(new Thread() {
			public void run() {
				while(hostFrame == null) {
    				try{ Thread.sleep(100); }catch(Exception e) { e.printStackTrace(); }
    				SwingUtilities.invokeLater(new Runnable() {
    					public void run() {
    						hostFrame = (DesktopFrame)SwingUtilities.getAncestorOfClass(DesktopFrame.class, BarChartTitleBar.this);
    						if((hostFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) { 
    							hostFrame.setBorderSize(0);
    						}
    					}
    				});
				}
			}
		}).start();
	}

}

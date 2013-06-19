package org.apache.pivot.wtk;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Mockup of a "Windows 7" titlebar, giving a "windowsy" look and
 * feel when used in conjunction with a custom frame on a Windows 7
 * os.
 * 
 * @author David Ray
 */
@SuppressWarnings("serial")
public class WindowsTitleBarX extends TitleBar {
	
	/**
	 * Constructs a new {@code WindowsTitleBarX}
	 */
	public WindowsTitleBarX() {
		addTitleBarControl(createTitleBarControl());
	}
	
	@Override
	protected void paintTitle(Graphics2D g2) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Called by the {@link TitleBar} base class to paint 
	 * the background of the titlebar.
	 */
	@Override
	protected void fillTitleBar(Graphics2D g) {
		Rectangle r = getBounds();
		Color ltGray = Color.GRAY;
		g.setColor(ltGray);
		g.fillRoundRect(0,0,r.width, r.height + 15, 15, 15);
		g.setColor(Color.BLACK);
		g.drawRoundRect(0,0,r.width - 1, r.height + 15, 15, 15);
		g.setColor(Color.WHITE);
		g.drawRoundRect(1,1,r.width - 3, r.height + 15, 15, 15);
	}
	
	/**
	 * Creates the control containing the frame state
	 * management buttons.
	 * 
	 * @return	{@link TitleBarControl}
	 */
	private TitleBarControl createTitleBarControl() {
		WindowsTitleBarControl control = new WindowsTitleBarControl();
		control.setPreferredSize(new Dimension(103,20));
		control.setControlLocation(TitleBar.Location.RIGHT);
		return control;
	}
	
	class WindowsTitleBarControl extends TitleBarControl {
		private static final int SIDE_MARGIN = 3;
    	private Rectangle2D minimizeRect;
    	private Rectangle2D maximizeRect;
    	private Rectangle2D closeRect;
    	
    	private boolean mouseOverControl;
    	private boolean maskMinimized;
    	private boolean maskMaximized;
    	private boolean maskClosed;
    	private boolean overMin;
    	private boolean overMax;
    	private boolean overClose;
    	
    	private java.awt.Frame hostFrame;
    	
    	private boolean shapesInitialized;
    	
    	public WindowsTitleBarControl() {
    		getParentFrame();
    	}
    	
    	@Override
    	public void paint(Graphics graphics) {
    		super.paint(graphics);
    		Graphics2D g = (Graphics2D)graphics;
    		if(!shapesInitialized) {
    			createTargetShapes();
    			installListeners();
    			shapesInitialized = true;
    		}
    		
    		paintControl(g);
    	}
    	
    	@Override
    	public void update(Graphics g) {
    		paint(g);
    	}
    	
    	protected void paintControl(Graphics2D g) {
    		Rectangle r = g.getClipBounds();
    		Graphics2D g2 = (Graphics2D)g;
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    		
    		//The control box
    		g2.setColor(Color.BLACK);
    		g2.drawRoundRect(r.width - 99, -10, 90, 29, 10, 10);
    		g2.setColor(Color.WHITE);
    		g2.drawRoundRect(r.width - 100, -9, 92, 29, 10, 10);
    		
    		//The controls
    		int x = r.width - 98;
    		GeneralPath path = new GeneralPath();
    		
    		//Button 1
    		path.moveTo(x, 2);
    		path.lineTo(x, 14);
    		path.curveTo(x, 14, x, 16, x + 4, 18);
    		path.lineTo(x + 23, 18);
    		path.lineTo(x + 23, 2);
    		g2.draw(path);
    		
    		//Button 2
    		g2.setColor(Color.BLACK);
    		g2.drawLine(x + 24, 0, x + 24, 18);
    		g2.setColor(Color.WHITE);
    		g2.drawLine(x + 25, 2, x + 25, 18);
    		g2.drawLine(x + 25, 18, x + 47, 18);
    		g2.drawLine(x + 47, 18, x + 47, 2);
    		
    		//Button 3
    		g2.setColor(Color.BLACK);
    		g2.drawLine(x + 48, 18, x + 48, 0);
    		g2.setColor(Color.WHITE);
    		path.moveTo(x + 49, 2);
    		path.lineTo(x + 49, 18);
    		path.lineTo(x + 85, 18);
    		path.curveTo(x + 85, 18, x + 85, 16, x + 88, 14);
    		path.lineTo(x + 88, 2);
    		g2.draw(path);
    		g2.setColor(new Color(186,56,33));
    		g2.fillRect(x + 49, 1, 38, 18);
    		g2.fillRect(x + 49, 1, 40, 15);
    		g2.drawLine(x + 49, 16, x + 88, 16);
    		g2.drawLine(x + 49, 17, x + 87, 17);
    		
//    		image = getGaussianBlurFilter(5, true).filter(image, null);
//    		image = getGaussianBlurFilter(5, false).filter(image, null);
//    		
//    		g.drawImage(image, 0, 0, null);
//    		
//    		g2.dispose();
    		
    		//Reflection on upper half of all 3 buttons
    		Composite oldComp = g2.getComposite();
    		g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
    		
    		Point2D start = new Point2D.Double(x, 2);
    		Point2D end = new Point2D.Double(x, 9);
    		float[] fractions = new float[] { 0.0f, 1.0f };
    		Color[] colors = new Color[] { Color.WHITE, Color.GRAY };
    		java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(start, end, fractions, colors, CycleMethod.NO_CYCLE);
    		
    		g2.setPaint(lgp);//Color.WHITE);
    		g2.fillRect(x, 2, 23, 9); //Button 1
    		g2.fillRect(x + 25, 2, 23, 9); //Button 2
    		g2.fillRect(x + 48, 1, 41, 9); //Button 3
    		
    		g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
    		start = new Point2D.Double(x, 9);
    		end = new Point2D.Double(x, 18);
    		fractions = new float[] { 0.0f, 1.0f };
    		colors = new Color[] { Color.DARK_GRAY, Color.LIGHT_GRAY };
    		lgp = new java.awt.LinearGradientPaint(start, end, fractions, colors, CycleMethod.NO_CYCLE);
    		
//    		g2.setPaint(lgp);
//    		g2.fillRect(x, 10, 23, 16); //Button 1
//    		g2.fillRect(x + 25, 10, 23, 16); //Button 2
//    		g2.fillRect(x + 48, 10, 41, 17); //Button 3
    		
    		g2.setComposite(oldComp); //Reverse alpha composite
    		
    		//Button symbols
    		g2.setColor(Color.WHITE);
    		g2.fillRect(x + 7, 11, 10, 3);
    		g2.setColor(Color.BLACK);
    		g2.drawRect(x + 7, 10, 10, 4);
    		
    		g2.setColor(Color.WHITE);
    		g2.setStroke(new BasicStroke(2));
    		g2.drawRect(x + 31, 6, 10, 7);
    		g2.setColor(Color.BLACK);
    		g2.setStroke(new BasicStroke(1));
    		g2.drawRect(x + 30, 5, 12, 9);
    		g2.drawRect(x + 33, 8, 6, 3);
    		
    		Area xfig = new Area(new Rectangle(x + 62, 5, 14, 8));
    		int[] xpoints = new int[]{ x + 62, x + 66, x + 62 };
    		int[] ypoints = new int[] { 5, 9, 13 };
    		Polygon p = new Polygon(xpoints, ypoints, 3);
    		Area leftTri = new Area(p);
    		xfig.subtract(leftTri);
    		xpoints = new int[]{ x + 66, x + 69, x + 72 };
    		ypoints = new int[] { 5, 8, 5 };
    		p = new Polygon(xpoints, ypoints, 3);
    		Area topTri = new Area(p);
    		xfig.subtract(topTri);
    		xpoints = new int[]{ x + 76, x + 72, x + 76 };
    		ypoints = new int[] { 5, 9, 13 };
    		p = new Polygon(xpoints, ypoints, 3);
    		Area rightTri = new Area(p);
    		xfig.subtract(rightTri);
    		xpoints = new int[]{ x + 66, x + 69, x + 72 };
    		ypoints = new int[] { 13, 10, 13 };
    		p = new Polygon(xpoints, ypoints, 3);
    		Area bottomTri = new Area(p);
    		xfig.subtract(bottomTri);
    		
    		g2.setColor(Color.WHITE);
    		g2.fill(xfig);
    		g2.setColor(Color.BLACK);
    		g2.draw(xfig);
    		
    		if(mouseOverControl) {
    			if(maskMinimized) {
    				g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
    				g2.setColor(Color.DARK_GRAY);
    				g2.fill(minimizeRect);
    			}else if(maskMaximized) {
    				g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
    				g2.setColor(Color.DARK_GRAY);
    				g2.fill(maximizeRect);
    			}else if(maskClosed) {
    				g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
    				g2.setColor(Color.DARK_GRAY);
    				g2.fill(closeRect);
    			}
    			
    			g2.setComposite(oldComp);
    		}
    	}
    	
    	private void createTargetShapes() {
    		int y =  1;
    		minimizeRect = new Rectangle2D.Double(SIDE_MARGIN, y, 25, 20);
    		maximizeRect  = new Rectangle2D.Double(SIDE_MARGIN + 25, y, 25, 20);
    		closeRect = new Rectangle(SIDE_MARGIN + 50, y, 40, 20);
    	}
    	
    	private void installListeners() {
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
    				if(minimizeRect.contains(e.getPoint())) {
    					fireFrameWillBecomeIconized();
    					hostFrame.setExtendedState(JFrame.ICONIFIED);
    				}else if(maximizeRect.contains(e.getPoint())) {
    					if((hostFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
    						hostFrame.setExtendedState(JFrame.NORMAL);
    					}else{
    						hostFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    					}
    				}else if(closeRect.contains(e.getPoint())) {
    					WindowEvent wev = new WindowEvent(hostFrame, WindowEvent.WINDOW_CLOSING);
    	                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    				}
    				maskMinimized = maskMaximized = maskClosed = false;
    				repaint();
    			}
    			public void mousePressed(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if(closeRect.contains(p)) {
						maskClosed = true;
						maskMinimized = false;
						maskMaximized = false;
					}else if(minimizeRect.contains(p)) {
						maskClosed = false;
						maskMinimized = true;
						maskMaximized = false;
					}else if(maximizeRect.contains(p)) {
						maskClosed = false;
						maskMinimized = false;
						maskMaximized = true;
					}
    				repaint();
    			}
    		});
    		WindowsTitleBarControl.this.addMouseMotionListener(new MouseAdapter() {
    			public void mouseMoved(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if((overClose = closeRect.contains(p)) || (overMin = minimizeRect.contains(p)) || (overMax = maximizeRect.contains(p))) {
    					mouseOverControl = true;
    					overClose = !overMin && !overMax;
    					overMin = !overClose && !overMax;
    					overMax = !overMin && !overClose;
    				}else{
    					mouseOverControl = false;
    					maskClosed = false;
						maskMinimized = false;
						maskMaximized = false;
						overMin = false;
						overClose = false;
						overMax = false;
    				}
    				
    				repaint();
    			}
    			public void mouseDragged(MouseEvent m) {
    				java.awt.Point p = m.getPoint();
    				if(closeRect.contains(p) || minimizeRect.contains(p) || maximizeRect.contains(p)) {
    					mouseOverControl = true;
    				}else{
    					maskClosed = false;
						maskMinimized = false;
						maskMaximized = false;
						
						repaint();
    				}
    			}
    		});
    		WindowsTitleBarX.this.addMouseMotionListener(new MouseAdapter() {
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
    				if(closeRect.contains(p) || minimizeRect.contains(p) || maximizeRect.contains(p)) {
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
	    						hostFrame = (java.awt.Frame)SwingUtilities.getAncestorOfClass(DesktopFrame.class, WindowsTitleBarControl.this);
	    						WindowsTitleBarX.this.invalidate();
	    						WindowsTitleBarX.this.validate();
	    						WindowsTitleBarX.this.repaint();
	    					}
	    				});
    				}
    			}
    		}).start();
    	}
	}

	
	
	public ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
	     if (radius < 1) {
	    	 throw new IllegalArgumentException(
	             "Radius must be >= 1");
	     }

	     int size = radius * 2 + 1;
	     float[] data = new float[size];
	     float sigma = radius / 3.0f;
	     float twoSigmaSquare = 2.0f * sigma * sigma;
	     float sigmaRoot = (float)
	         Math.sqrt(twoSigmaSquare * Math.PI);
	     float total = 0.0f;
	     for (int i = -radius; i <= radius; i++) {
	    	 float distance = i * i;
	       	int index = i + radius;
	       	data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
	       	total += data[index];
	     }

	     for (int i = 0; i < data.length; i++) {
	    	 data[i] /= total;
	     }

	     Kernel kernel = null;
	     if(horizontal) {
	    	 kernel = new Kernel(size, 1, data);
	     }else{
	    	 kernel = new Kernel(1, size, data);
	     }
	     return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	 }

}

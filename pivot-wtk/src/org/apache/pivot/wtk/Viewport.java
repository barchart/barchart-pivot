/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.wtk;

import org.apache.pivot.beans.DefaultProperty;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.ListenerList;

/**
 * Abstract base class for viewport components. Viewports provide a windowed
 * view on a component (called the "view") that is too large to fit within a
 * given area. They are generally scrollable.
 * <p> Even though this class is a {@link Container}, no components should be
 * added to it via the {@link #add add()} method.  The component that gets the
 * windowed (or scrollable) view should be added via the {@link #setView setView()}
 * method (or the "view" property).
 */
@DefaultProperty("view")
public abstract class Viewport extends Container {
    /**
     * Viewport skin interface. Viewport skins must implement this.
     */
    public interface Skin {
        /**
         * The bounds of the Viewport within the container, for example, in ScrollPaneSkin, this excludes the scrollbars.
         */
        public Bounds getViewportBounds();
    }

    private static class ViewportListenerList extends WTKListenerList<ViewportListener>
        implements ViewportListener {

        @Override
        public void scrollTopChanged(Viewport viewport, int previousScrollTop) {
            for (ViewportListener listener : this) {
                listener.scrollTopChanged(viewport, previousScrollTop);
            }
        }

        @Override
        public void scrollLeftChanged(Viewport viewport, int previousScrollLeft) {
            for (ViewportListener listener : this) {
                listener.scrollLeftChanged(viewport, previousScrollLeft);
            }
        }

        @Override
        public void viewChanged(Viewport viewport, Component previousView) {
            for (ViewportListener listener : this) {
                listener.viewChanged(viewport, previousView);
            }
        }
    }

    private int scrollTop = 0;
    private int scrollLeft = 0;
    private Component view;

    private boolean consumeRepaint = false;
    private boolean repaintAllViewport = false;

    private ViewportListenerList viewportListeners = new ViewportListenerList();

    @Override
    protected void setSkin(org.apache.pivot.wtk.Skin skin) {
        if (!(skin instanceof Viewport.Skin)) {
            throw new IllegalArgumentException("Skin class must implement "
                + Viewport.Skin.class.getName());
        }

        super.setSkin(skin);
    }

    public int getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(int scrollTop) {
        int previousScrollTop = this.scrollTop;

        if (scrollTop != previousScrollTop) {
            this.scrollTop = scrollTop;
            viewportListeners.scrollTopChanged(this, previousScrollTop);
        }
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(int scrollLeft) {
        int previousScrollLeft = this.scrollLeft;

        if (scrollLeft != previousScrollLeft) {
            this.scrollLeft = scrollLeft;
            viewportListeners.scrollLeftChanged(this, previousScrollLeft);
        }
    }

    /**
     * Returns the (single) component (typically a {@link Container})
     * that we are providing a windowed (or scrollable) view of.
     */
    public Component getView() {
        return view;
    }

    /**
     * Set the single component (typically a {@link Container}) that
     * we will provide a windowed (or scrollable) view of.
     */
    public void setView(Component view) {
       Component previousView = this.view;

        if (view != previousView) {
            // Remove any previous view component
            this.view = null;

            if (previousView != null) {
                remove(previousView);
            }

            // Set the new view component
            if (view != null) {
                insert(view, 0);
            }

            this.view = view;

            viewportListeners.viewChanged(this, previousView);
        }
    }

    /**
     * Returns the <tt>consumeRepaint</tt> flag, which controls whether the
     * viewport will propagate repaints to its parent or consume them.
     * This flag enables skins to optimize viewport scrolling by blitting the
     * display to reduce the required repaint area.
     *
     * @return
     * <tt>true</tt> if this viewport will consume repaints that bubble up
     * through it; <tt>false</tt> if it will propagate them up like normal.
     */
    public boolean isConsumeRepaint() {
        return consumeRepaint;
    }

    /**
     * Sets the <tt>consumeRepaint</tt> flag, which controls whether the
     * viewport will propagate repaints to its parent or consume them.
     * This flag enables skins to optimize viewport scrolling by blitting the
     * display to reduce the required repaint area.
     *
     * @param consumeRepaint
     * <tt>true</tt> to consume repaints that bubble up through this viewport;
     * <tt>false</tt> to propagate them up like normal.
     */
    public void setConsumeRepaint(boolean consumeRepaint) {
        this.consumeRepaint = consumeRepaint;
    }

    /**
     * The bounds of the Viewport within the container, for example, in ScrollPaneSkin, this excludes the scrollbars.
     */
    public Bounds getViewportBounds() {
        Viewport.Skin viewportSkin = (Viewport.Skin)getSkin();
        return viewportSkin.getViewportBounds();
    }

    @Override
    public void repaint(int x, int y, int width, int height, boolean immediate) {
        if (!consumeRepaint) {
            super.repaint(x, y, width, height, immediate);
        }
    }

    /**
     * This method should not be called to remove child components
     * from the Viewport because the viewable child(ren) are set
     * by the {@link #setView} method instead.  Any attempt to
     * remove the "view" component with this method will result
     * in an exception.
     */
    @Override
    public Sequence<Component> remove(int index, int count) {
        for (int i = index, n = index + count; i < n; i++) {
            Component component = get(i);
            if (component == view) {
                throw new UnsupportedOperationException();
            }
        }

        // Call the base method to remove the components
        return super.remove(index, count);
    }

    public ListenerList<ViewportListener> getViewportListeners() {
        return viewportListeners;
    }

    /**
     * Tell if the viewport painting mode is optimized (repaint only needed area, default), or repaint all.
     * <p> This is implemented as a workaround for various painting issues on some platforms.
     * So, if you experience problems with the scrolled-in area not being painted properly
     * by default, consider setting this property <tt>true</tt> using the
     * {@link #setRepaintAllViewport setRepaintAllViewport} method.
     *
     * @return <tt>false</tt> if optimized, otherwise <tt>true</tt> (repaint entire viewport)
     */
    public boolean isRepaintAllViewport() {
        return repaintAllViewport;
    }

    /**
     * Set the viewport painting mode.
     * <p> This is implemented as a workaround for various painting issues on some platforms.
     * So, if you experience problems with the scrolled-in area not being painted properly
     * by default, consider setting this property <tt>true</tt> (default is <tt>false</tt>).
     *
     * @param repaintAllViewport
     * <tt>false</tt> means optimized (repaint only needed area, default), while <tt>true</tt> means repaint all
     */
    public void setRepaintAllViewport(boolean repaintAllViewport) {
        this.repaintAllViewport = repaintAllViewport;
    }

}

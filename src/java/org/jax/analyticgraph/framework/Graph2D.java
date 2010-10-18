/*
 * Copyright (c) 2009 The Jackson Laboratory
 * 
 * This software was developed by Gary Churchill's Lab at The Jackson
 * Laboratory (see http://research.jax.org/faculty/churchill).
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jax.analyticgraph.framework;

import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * Base type to be implemented by 2D graphing classes.
 * 
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public interface Graph2D
{
    /**
     * Getter for this graph's coordinate converter. A coordinate converter
     * allows the graph to go between the Java2D and graph coordinate systems.
     * @return
     *          the coordinate converter that this graph is using
     */
    public GraphCoordinateConverter getGraphCoordinateConverter();

    /**
     * Setter for this graph's coordinate converter. See
     * {@link #getGraphCoordinateConverter()}.
     * 
     * @param graphCoordinateConverter
     *          the graphCoordinateConverter to set
     */
    public void setGraphCoordinateConverter(GraphCoordinateConverter graphCoordinateConverter);
    
    /**
     * Getter for the container component (the component that this
     * graph is rendered in)
     * @return
     *          the containerComponent
     */
    public JComponent getContainerComponent();

    /**
     * Setter for the container component.
     * @see #getContainerComponent()
     * @param containerComponent
     *          the containerComponent to set
     */
    public void setContainerComponent(JComponent containerComponent);

    /**
     * Render this Graph2D to the given Graphics2D context. This method
     * should only be called within the context
     * @param graphics2D
     *          the graphics context to render to
     */
    public void renderGraph(Graphics2D graphics2D);
}

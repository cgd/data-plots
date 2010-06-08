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

import java.beans.PropertyChangeListener;

import org.jax.analyticgraph.graph.AxisDescription;

/**
 * Interface for a 2D graph with axes.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public interface Graph2DWithAxes extends Graph2D
{
    /**
     * Getter for the X-axis description.
     * @return
     *          the X-axis description
     */
    public AxisDescription getXAxisDescription();
    
    /**
     * Getter for the Y-axis description.
     * @return
     *          the Y-axis description
     */
    public AxisDescription getYAxisDescription();
    
    /**
     * Getter for the graph title
     * @return
     *          the graph title
     */
    public String getGraphTitle();
    
    /**
     * Setter for the graph title
     * @param graphTitle
     *          the graph title
     */
    public void setGraphTitle(String graphTitle);
    
    /**
     * Getter for the title's font size
     * @return
     *          the title's font size
     */
    public int getGraphTitleFontSize();
    
    /**
     * Setter for the title's font size
     * @param graphTitleFontSize
     *          the font size
     */
    public void setGraphTitleFontSize(int graphTitleFontSize);
    
    /**
     * Add a property change listener
     * @param listener
     *          the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    /**
     * Remove the given listener
     * @param listener
     *          the listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}

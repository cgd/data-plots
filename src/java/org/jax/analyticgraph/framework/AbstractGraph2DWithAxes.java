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
import java.beans.PropertyChangeSupport;

/**
 * An abstract base class for 2D graphs with axes
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public abstract class AbstractGraph2DWithAxes
extends AbstractGraph2D
implements Graph2DWithAxes
{
    private final PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    
    private volatile String graphTitle;

    private volatile int graphTitleFontSize = 24;
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use
     */
    public AbstractGraph2DWithAxes(
            GraphCoordinateConverter graphCoordinateConverter)
    {
        super(graphCoordinateConverter);
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getGraphTitle()
    {
        return this.graphTitle;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setGraphTitle(String graphTitle)
    {
        this.graphTitle = graphTitle;
        this.propertyChangeSupport.firePropertyChange(
                "graphTitle",
                null,
                graphTitle);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getGraphTitleFontSize()
    {
        return this.graphTitleFontSize;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setGraphTitleFontSize(int graphTitleFontSize)
    {
        this.graphTitleFontSize = graphTitleFontSize;
        this.propertyChangeSupport.firePropertyChange(
                "graphTitleFontSize",
                null,
                graphTitleFontSize);
    }
}

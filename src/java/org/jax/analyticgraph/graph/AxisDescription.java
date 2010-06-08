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

package org.jax.analyticgraph.graph;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.jax.analyticgraph.framework.GraphCoordinateConverter;

/**
 * An interface that allows us to describe an axis
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public abstract class AxisDescription
{
    /**
     * for firing off property change events
     */
    protected final PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    
    /**
     * Enum for indicating axis type
     */
    public enum AxisType
    {
        /**
         * For indicating x-axis
         */
        X_AXIS,
        
        /**
         * For indicating y-axis
         */
        Y_AXIS
    }
    
    private final GraphCoordinateConverter graphCoordinateConverter;
    
    private final AxisType axisType;
    
    private volatile String axisName;
    
    private volatile int axisLabelFontSize = 20;
    
    private volatile int tickLabelFontSize = 10;
    
    private volatile boolean showTickMarks = true;
    
    private volatile boolean showTickLabels = true;
    
    /**
     * Getter for the ticks
     * @return
     *          the ticks
     */
    public abstract List<Tick> getTicks();
    
    /**
     * Determine if the user should be allowed to perform translation
     * operations on the axis
     * @return
     *          true iff the user should be allowed to translate the axis
     */
    public abstract boolean getAllowAxisTranslation();
    
    /**
     * If true then the user should be allowed to perform scaling operations
     * @return
     *          true iff the user should be allowed to scale the axis
     */
    public abstract boolean getAllowAxisScaling();
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter that this axis description is
     *          for
     * @param axisType
     *          the type of axis
     */
    public AxisDescription(
            GraphCoordinateConverter graphCoordinateConverter,
            AxisType axisType)
    {
        this.graphCoordinateConverter = graphCoordinateConverter;
        this.axisType = axisType;
    }
    
    /**
     * Getter for the name of the axis
     * @return
     *          the axis name
     */
    public String getAxisName()
    {
        return this.axisName;
    }
    
    /**
     * Setter for the name of the axis
     * @param axisName
     *          the axis name
     */
    public void setAxisName(String axisName)
    {
        this.axisName = axisName;
        this.propertyChangeSupport.firePropertyChange(
                "axisName",
                null,
                axisName);
    }
    
    /**
     * Getter for the axis origin
     * @param axisOrigin
     *          the axis origin
     */
    public void setAxisOrigin(double axisOrigin)
    {
        if(this.axisType == AxisType.X_AXIS)
        {
            this.graphCoordinateConverter.updateGraphDimensions(
                    axisOrigin,
                    this.graphCoordinateConverter.getGraphOriginY(),
                    this.graphCoordinateConverter.getGraphWidth(),
                    this.graphCoordinateConverter.getGraphHeight());
        }
        else
        {
            // its a y-axis
            this.graphCoordinateConverter.updateGraphDimensions(
                    this.graphCoordinateConverter.getGraphOriginX(),
                    axisOrigin,
                    this.graphCoordinateConverter.getGraphWidth(),
                    this.graphCoordinateConverter.getGraphHeight());
        }
        
        this.propertyChangeSupport.firePropertyChange(
                "axisOrigin",
                null,
                axisOrigin);
    }
    
    /**
     * Setter for the axis origin
     * @return
     *          the axis origin
     */
    public double getAxisOrigin()
    {
        if(this.axisType == AxisType.X_AXIS)
        {
            return this.graphCoordinateConverter.getGraphOriginX();
        }
        else
        {
            return this.graphCoordinateConverter.getGraphOriginY();
        }
    }
    
    /**
     * Setter for the axis extent
     * @param axisExtent
     *          the axis extent
     */
    public void setAxisExtent(double axisExtent)
    {
        if(this.axisType == AxisType.X_AXIS)
        {
            this.graphCoordinateConverter.updateGraphDimensions(
                    this.graphCoordinateConverter.getGraphOriginX(),
                    this.graphCoordinateConverter.getGraphOriginY(),
                    axisExtent,
                    this.graphCoordinateConverter.getGraphHeight());
        }
        else
        {
            // its a y-axis
            this.graphCoordinateConverter.updateGraphDimensions(
                    this.graphCoordinateConverter.getGraphOriginX(),
                    this.graphCoordinateConverter.getGraphOriginY(),
                    this.graphCoordinateConverter.getGraphWidth(),
                    axisExtent);
        }
        
        this.propertyChangeSupport.firePropertyChange(
                "axisExtent",
                null,
                axisExtent);
    }
    
    /**
     * Getter for the axis extent
     * @return
     *          the axis extent
     */
    public double getAxisExtent()
    {
        if(this.axisType == AxisType.X_AXIS)
        {
            return this.graphCoordinateConverter.getGraphWidth();
        }
        else
        {
            // its a y-axis
            return this.graphCoordinateConverter.getGraphHeight();
        }
    }
    
    /**
     * Add the given listener
     * @param listener
     *          the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
     * Remove the property change listener
     * @param listener
     *          the listener to remove
     */
    public void removePropertyChangeSupport(PropertyChangeListener listener)
    {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Getter for the axis font size
     * @return the axis font size
     */
    public int getAxisLabelFontSize()
    {
        return this.axisLabelFontSize;
    }
    
    /**
     * Setter for the axis label font size
     * @param axisLabelFontSize the axisLabelFontSize to set
     */
    public void setAxisLabelFontSize(int axisLabelFontSize)
    {
        this.axisLabelFontSize = axisLabelFontSize;
        this.propertyChangeSupport.firePropertyChange(
                "axisLabelFontSize",
                null,
                axisLabelFontSize);
    }
    
    /**
     * Getter for the tick label size
     * @return the tickLabelFontSize
     */
    public int getTickLabelFontSize()
    {
        return this.tickLabelFontSize;
    }
    
    /**
     * Setter for the tick label size
     * @param tickLabelFontSize the tickLabelFontSize to set
     */
    public void setTickLabelFontSize(int tickLabelFontSize)
    {
        this.tickLabelFontSize = tickLabelFontSize;
        this.propertyChangeSupport.firePropertyChange(
                "tickLabelFontSize",
                null,
                tickLabelFontSize);
    }
    
    /**
     * Getter for determining if we show the tick labels
     * @return the showTickLabels
     */
    public boolean getShowTickLabels()
    {
        return this.showTickLabels;
    }
    
    /**
     * Setter for determining if we show the tick labels
     * @param showTickLabels the showTickLabels to set
     */
    public void setShowTickLabels(boolean showTickLabels)
    {
        this.showTickLabels = showTickLabels;
        this.propertyChangeSupport.firePropertyChange(
                "showTickLabels",
                null,
                showTickLabels);
    }
    
    /**
     * Getter for determining if we show the tick marks
     * @return the showTickMarks
     */
    public boolean getShowTickMarks()
    {
        return this.showTickMarks;
    }
    
    /**
     * Setter for determining if we show the tick mark
     * @param showTickMarks the showTickMarks to set
     */
    public void setShowTickMarks(boolean showTickMarks)
    {
        this.showTickMarks = showTickMarks;
        this.propertyChangeSupport.firePropertyChange(
                "showTickMarks",
                null,
                showTickMarks);
    }
}

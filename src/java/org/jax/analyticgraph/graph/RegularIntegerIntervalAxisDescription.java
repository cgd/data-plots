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

import java.util.ArrayList;
import java.util.List;

import org.jax.analyticgraph.framework.GraphCoordinateConverter;

/**
 * Similar to {@link org.jax.analyticgraph.graph.RegularIntervalAxisDescription}
 * except that the intervals are all integer values.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class RegularIntegerIntervalAxisDescription extends AxisDescription
{
    private volatile int majorTickInterval;
    
    private final boolean allowTickIntervalModifications;
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use
     * @param axisType
     *          the axis type
     * @param allowTickIntervalModifications
     *          determines if the tick intervals are allowed to be modified
     */
    public RegularIntegerIntervalAxisDescription(
            GraphCoordinateConverter graphCoordinateConverter,
            AxisType axisType,
            boolean allowTickIntervalModifications)
    {
        super(graphCoordinateConverter, axisType);
        this.allowTickIntervalModifications = allowTickIntervalModifications;
    }

    /**
     * Getter for determining if you are allowed to modify the tick interval
     * via {@link #setMajorTickInterval(int)} or
     * {@link #updateMajorTickInterval(int)}
     * @return the allowTickIntervalModifications
     */
    public boolean getAllowTickIntervalModifications()
    {
        return this.allowTickIntervalModifications;
    }
    
    /**
     * Update the interval in {@link #getMajorTickInterval()} using a target
     * tick count rather than updating it directly
     * @param tickCount
     *          the tick count we are trying to achieve (plus or minus one tick)
     */
    public void updateMajorTickInterval(int tickCount)
    {
        int newMajorTickInterval = ((int)this.getAxisExtent()) / tickCount;
        if(newMajorTickInterval <= 0)
        {
            newMajorTickInterval = 1;
        }
        this.setMajorTickInterval(newMajorTickInterval);
    }

    /**
     * @return the majorTickInterval
     */
    public int getMajorTickInterval()
    {
        return this.majorTickInterval;
    }
    
    /**
     * @param majorTickInterval the majorTickInterval to set
     */
    public void setMajorTickInterval(int majorTickInterval)
    {
        this.majorTickInterval = majorTickInterval;
        this.propertyChangeSupport.firePropertyChange(
                "majorTickInterval",
                null,
                majorTickInterval);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> getTicks()
    {
        final List<Tick> ticks = new ArrayList<Tick>();
        
        final int axisStart = (int)this.getAxisOrigin();
        final int axisExtent = (int)this.getAxisExtent();
        final int majorTickInterval = this.majorTickInterval;
        
        // make sure that we put the 1st tick in the correct position which
        // is relative to the origin on the axis
        // TODO test all of the if's
        final int tickMod = axisStart % majorTickInterval;
        final int tickStart;
        if(tickMod == 0)
        {
            tickStart = axisStart;
        }
        else if(tickMod > 0)
        {
            tickStart = axisStart + (majorTickInterval - tickMod);
        }
        else
        {
            tickStart = axisStart - tickMod;
        }
        
        for(int currTickPosition = tickStart;
            currTickPosition <= axisStart + axisExtent;
            currTickPosition += majorTickInterval)
        {
            ticks.add(new Tick(
                    currTickPosition,
                    Tick.DEFAULT_MAJOR_TICK_SIZE_PIXELS,
                    Integer.toString(currTickPosition)));
        }
        
        return ticks;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAllowAxisScaling()
    {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAllowAxisTranslation()
    {
        return true;
    }
}

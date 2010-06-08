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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jax.analyticgraph.framework.GraphCoordinateConverter;
import org.jax.util.math.NumericUtilities;

/**
 * An axis description that uses a regular interval to create tick marks
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class RegularIntervalAxisDescription extends AxisDescription
{
    private final boolean allowTickIntervalModifications;
    
    private final int significantDigitsInInterval;
    
    private volatile double majorTickInterval;
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use
     * @param axisType
     *          the axis type
     * @param axisName
     *          see {@link #getAxisName()}
     * @param majorTickInterval
     *          see {@link #getMajorTickInterval()}
     * @param allowTickIntervalModifications
     *          if true we'll allow modifications to the interval
     */
    public RegularIntervalAxisDescription(
            GraphCoordinateConverter graphCoordinateConverter,
            AxisType axisType,
            String axisName,
            double majorTickInterval,
            boolean allowTickIntervalModifications)
    {
        super(graphCoordinateConverter, axisType);
        this.significantDigitsInInterval = -1;
        this.setAxisName(axisName);
        this.setMajorTickInterval(majorTickInterval);
        this.allowTickIntervalModifications = allowTickIntervalModifications;
    }
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use
     * @param axisType
     *          the axis type
     * @param axisName
     *          see {@link #getAxisName()}
     * @param initialTickCount
     *          see {@link #updateMajorTickInterval(int)}
     * @param significantDigitsInInterval
     *          the number of significant digits to use
     * @param allowTickIntervalModifications
     *          if true we'll allow modifications to the interval
     */
    public RegularIntervalAxisDescription(
            GraphCoordinateConverter graphCoordinateConverter,
            AxisType axisType,
            String axisName,
            int initialTickCount,
            int significantDigitsInInterval,
            boolean allowTickIntervalModifications)
    {
        super(graphCoordinateConverter, axisType);
        this.significantDigitsInInterval = significantDigitsInInterval;
        this.setAxisName(axisName);
        this.updateMajorTickInterval(initialTickCount);
        this.allowTickIntervalModifications = allowTickIntervalModifications;
    }
    
    /**
     * Determine if you're allowed to modify the tick intervals
     * @return the allowTickIntervalModifications
     */
    public boolean getAllowTickIntervalModifications()
    {
        return this.allowTickIntervalModifications;
    }

    /**
     * Setter for the major tick interval. This number will get
     * rounded according to the value set in the constructor
     * @param majorTickInterval the majorTickInterval to set
     */
    public void setMajorTickInterval(double majorTickInterval)
    {
        if(this.significantDigitsInInterval >= 1)
        {
            this.majorTickInterval = NumericUtilities.roundToSignificantDigitsDouble(
                    majorTickInterval,
                    this.significantDigitsInInterval);
        }
        else
        {
            this.majorTickInterval = majorTickInterval;
        }
        
        this.propertyChangeSupport.firePropertyChange(
                "majorTickInterval",
                null,
                this.majorTickInterval);
    }

    /**
     * Update the interval in {@link #getMajorTickInterval()} using a target
     * tick count rather than updating it directly
     * @param tickCount
     *          the tick count we are trying to achieve (plus or minus one tick)
     */
    public void updateMajorTickInterval(
            int tickCount)
    {
        if(tickCount <= 0)
        {
            throw new IllegalArgumentException(
                    "tick count cannot be : " + tickCount +
                    ". It must be >= 1");
        }
        else
        {
            double newTickInterval = this.getAxisExtent() / tickCount;
            
            this.setMajorTickInterval(newTickInterval);
        }
    }

    /**
     * Getter for the major tick interval
     * @return the majorTickInterval
     */
    public double getMajorTickInterval()
    {
        return this.majorTickInterval;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> getTicks()
    {
        return RegularIntervalAxisDescription.createRegularlySpacedTickMarkPositions(
                this.getAxisOrigin(),
                this.getAxisExtent(),
                this.getMajorTickInterval());
    }
    
    /**
     * Get the tick mark positions that we should hit given the parameters.
     * @param minAxisRangeValue
     *          this is the left-most (or bottom most for y-axis) visible
     *          position of the graph which may not necessarily be (0,0)
     * @param axisExtent
     *          this is how long the visible part of the axis is
     * @param tickInterval
     *          this is the spacing that we should use between ticks
     * @return
     *          a list of tick marks along the axis (in interior graph units,
     *          not pixel units)
     */
    // TODO rethink name "origin" may be misleading
    private static List<Tick> createRegularlySpacedTickMarkPositions(
            double minAxisRangeValue,
            double axisExtent,
            double tickInterval)
    {
        if(tickInterval <= 0.0 || axisExtent <= 0.0)
        {
            return Collections.emptyList();
        }
        else
        {
            // we're using a lot of BigDecimal's in this function
            // because we can't afford floating point errors, also
            // note that we're using valueOf(...) because
            // the double constructor is "unpredictable" (see javadoc)
            BigDecimal bigOrigin =
                BigDecimal.valueOf(minAxisRangeValue);
            BigDecimal bigAxisLength =
                BigDecimal.valueOf(axisExtent);
            BigDecimal bigTickInterval =
                BigDecimal.valueOf(tickInterval);
            
            BigDecimal tickRemainder = bigOrigin.remainder(bigTickInterval);
            
            BigDecimal currentMajorTick = null;
            
            // find the 1st major tick
            // TODO test this if-else!!
            if(tickRemainder.signum() == 0)
            {
                // if the remainder is zero then the origin is
                // the 1st tick
                currentMajorTick = bigOrigin;
            }
            else
            {
                // since the remainder isn't 0, we need to figure out
                // where the 1st tick is
                BigDecimal differenceFromOrigin;
                if(bigOrigin.signum() >= 0)
                {
                    // the origin is positive or zero
                    differenceFromOrigin =
                        bigTickInterval.subtract(tickRemainder);
                }
                else
                {
                    // the origin is negative
                    differenceFromOrigin =
                        tickRemainder.abs();
                }
                
                currentMajorTick =
                    bigOrigin.add(differenceFromOrigin);
            }
            
            // TODO we're loosing precision info here. probably a better way
            //      to do this
            currentMajorTick = currentMajorTick.stripTrailingZeros();
            
            // while the current tick is <= axis length
            List<Tick> tickList =
                new ArrayList<Tick>();
            BigDecimal bigStopNumber = bigOrigin.add(bigAxisLength);
            while(currentMajorTick.compareTo(bigStopNumber) <= 0)
            {
                // add the current tick to our list
                tickList.add(new Tick(
                        currentMajorTick.doubleValue(),
                        Tick.DEFAULT_MAJOR_TICK_SIZE_PIXELS,
                        currentMajorTick.toString()));
                
                // jump to the next tick mark
                currentMajorTick = currentMajorTick.add(bigTickInterval);
            }
            
            return tickList;
        }
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

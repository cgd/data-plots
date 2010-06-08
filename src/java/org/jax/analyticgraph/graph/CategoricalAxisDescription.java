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
 * An axis description for categorical data.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class CategoricalAxisDescription extends AxisDescription
{
    private final String[] categoryNames;
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use
     * @param axisType
     *          the axis type
     * @param axisName
     *          see {@link #getAxisName()}
     * @param categoryNames
     *          see {@link #getCategoryNames()}
     */
    public CategoricalAxisDescription(
            GraphCoordinateConverter graphCoordinateConverter,
            AxisType axisType,
            String axisName,
            String[] categoryNames)
    {
        super(graphCoordinateConverter, axisType);
        this.setAxisName(axisName);
        this.categoryNames = categoryNames;
    }

    /**
     * Getter for the category names
     * @return the categoryNames
     */
    public String[] getCategoryNames()
    {
        return this.categoryNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tick> getTicks()
    {
        List<Tick> ticks = new ArrayList<Tick>(this.categoryNames.length);
        for(int categorIndex = 0;
            categorIndex < this.categoryNames.length;
            categorIndex++)
        {
            double currTickPosition = this.getCategoryAxisPosition(
                    categorIndex);
            ticks.add(new Tick(
                    currTickPosition,
                    Tick.DEFAULT_MAJOR_TICK_SIZE_PIXELS,
                    this.categoryNames[categorIndex]));
        }
        
        return ticks;
    }
    
    /**
     * Get the position of the given category index given the axis start and
     * extent
     * @param categoryIndex
     *          the category index whose position we're looking for
     * @return
     *          the position
     */
    public double getCategoryAxisPosition(
            int categoryIndex)
    {
        double tickDistance =
            1.0 / this.categoryNames.length;
        double currTickPosition =
            (tickDistance * categoryIndex) + (tickDistance / 2.0);
        return currTickPosition;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAllowAxisScaling()
    {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAllowAxisTranslation()
    {
        return false;
    }
}

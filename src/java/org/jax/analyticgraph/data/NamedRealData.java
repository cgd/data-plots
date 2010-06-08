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

package org.jax.analyticgraph.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A named numerical data type that holds real-valued data.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class NamedRealData
extends AbstractSelectableNamedData<Number>
{
    /**
     * The real number data.
     */
    private final Double[] realNumericalData;
    
    /**
     * the number data
     */
    private final List<Number> numericalData;
    
    /**
     * 
     * @param nameOfData
     * @param realNumericalData
     */
    public NamedRealData(String nameOfData, Double[] realNumericalData)
    {
        super(nameOfData);
        
        this.realNumericalData = realNumericalData;
        
        // just fill in the number array from our double array
        this.numericalData = new ArrayList<Number>(
                this.realNumericalData.length);
        
        for(int i = 0; i < this.realNumericalData.length; i++)
        {
            this.numericalData.add(this.realNumericalData[i]);
        }
    }
    
    /**
     * Get the real valued numerical data.
     * @return
     *          the real valued data
     */
    public Double[] getRealNumericalData()
    {
        return this.realNumericalData;
    }

    /**
     * {@inheritDoc}
     */
    public List<Number> getData()
    {
        return this.numericalData;
    }
}

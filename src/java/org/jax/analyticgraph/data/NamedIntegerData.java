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
 * A named numerical data type that holds int-valued data.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class NamedIntegerData extends AbstractSelectableNamedData<Number>
{
    /**
     * The int number data.
     */
    private final int[] integerData;
    
    /**
     * the number data
     */
    private final List<Number> numericalData;
    
    /**
     * Constructor
     * @param nameOfData
     *          the name of the data
     * @param integerData
     *          the integer data
     */
    public NamedIntegerData(String nameOfData, int[] integerData)
    {
        super(nameOfData);
        
        this.integerData = integerData;
        
        // just fill in the number array from our int array
        this.numericalData = new ArrayList<Number>(
                this.integerData.length);
        
        for(int i = 0; i < this.integerData.length; i++)
        {
            this.numericalData.add(this.integerData[i]);
        }
    }
    
    /**
     * Get the int valued numerical data.
     * @return
     *          the int valued data
     */
    public int[] getIntegerData()
    {
        return this.integerData;
    }

    /**
     * {@inheritDoc}
     */
    public List<Number> getData()
    {
        return this.numericalData;
    }
}

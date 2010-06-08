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

import java.util.List;

import org.jax.util.Condition;

/**
 * Base type for all named data.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 * @param <D> the type of data this interface holds
 */
public interface NamedData<D>
{
    /**
     * subset our data. can return <code>this</code> if there's no subsetting
     * to do
     * @param filterCondition
     *          the filter condition to use
     * @param subsetName
     *          the name to use (null means use superset name)
     * @return
     *          the subset
     */
    public NamedData<D> createDataSubset(
            Condition<D> filterCondition,
            String subsetName);
    
    /**
     * subset our data. can return <code>this</code> if there's no subsetting
     * to do
     * @param filter
     *          the filter to use. true means that data should be filtered out
     * @param subsetName
     *          the name to use (null means use superset name)
     * @return
     *          the subset
     */
    public NamedData<D> createDataSubset(
            boolean[] filter,
            String subsetName);
    
    /**
     * Get the number of datum elements
     * @return
     *          the size
     */
    public int getSize();
    
    /**
     * Get the name for this data.
     * @return  the name
     */
    public String getNameOfData();

    /**
     * Get the actual data.
     * @return  the data
     */
    public abstract List<D> getData();
}

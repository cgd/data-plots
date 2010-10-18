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
 * This type is for grouping named data together. All
 * named data types should be the same length.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 * @param <D> the type of data that this class holds
 */
public interface NamedDataMatrix<D>
{
    /**
     * Getter for the named data list. This is a list of
     * named data types where the length of each list is
     * the same.
     * @return the namedDataList
     *          the list of named data
     */
    public List<NamedData<D>> getNamedDataList();
    
    /**
     * basically works like calling {@link NamedData#createDataSubset(Condition, String)}
     * on all of the data except that we remove a datum index from all data lists if the filterCondition
     * is true for any of them (this ensures that all {@link NamedData} stay the same
     * size in this matrix)
     * @param filterCondition
     *          the condition that we're filtering on (true means filter it out)
     * @return
     *          the filtered result (can be <code>this</code> if there was no
     *          filtering to do)
     */
    public NamedDataMatrix<D> createMatrixSubset(Condition<D> filterCondition);
    
    /**
     * Get the names of the internal {@link NamedData}s
     * @return
     *          the names
     */
    public String[] getDataNames();
    
    /**
     * Get the {@link NamedData} with the given name
     * @param name
     *          the name of the data we're looking for
     * @return
     *          a matching {@link NamedData} or null if we can't find it
     */
    public NamedData<D> getDataWithName(String name);
}

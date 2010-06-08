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

import org.jax.util.datastructure.ImmutableReorderedList;


/**
 * Holds a subset of data.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
class NamedDataSubset<D> extends AbstractSelectableNamedData<D>
{
    /**
     * @see #getSuperset()
     */
    private final NamedData<D> superset;
    
    /**
     * holds the data we return in {@link #getData()}
     */
    private final ImmutableReorderedList<D> subsetData;

    /**
     * Construct a subset of the given superset data.
     * @param filter
     *          the filter to use in subseting (true means filter out)
     * @param subsetName
     *          the name of the subset (null means use parent)
     * @param superset
     *          the superset of this subset
     */
    public NamedDataSubset(
            NamedData<D> superset,
            boolean[] filter,
            String subsetName)
    {
        super(subsetName == null ? superset.getNameOfData() : subsetName);
        this.superset = superset;
        this.subsetData = new ImmutableReorderedList<D>(
                superset.getData(),
                filter);
    }

    /**
     * getter for the superset
     * @return the superset
     */
    public NamedData<D> getSuperset()
    {
        return this.superset;
    }

    /**
     * {@inheritDoc}
     */
    public ImmutableReorderedList<D> getData()
    {
        return this.subsetData;
    }
}
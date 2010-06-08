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

import org.jax.util.Condition;
import org.jax.util.datastructure.ImmutableReorderedList;

/**
 * A reordered matrix. This can also be used to subset the matrix.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 * @param <D> the type of data this class holds
 */
public class ReorderedNamedDataMatrix<D> extends SimpleSelectableNamedDataMatrix<D>
{
    /**
     * Presents a filtered view of the given original matrix. See
     * {@link ImmutableReorderedList#ImmutableReorderedList(java.util.List, Condition)}
     * for details on how this filtering works
     * @param originalMatrix
     *          the original matrix that we're filtering
     * @param removeFilterCondition
     *          when this returns true on one of our {@link NamedData},
     *          we filter it out
     */
    public ReorderedNamedDataMatrix(
            NamedDataMatrix<D> originalMatrix,
            Condition<NamedData<D>> removeFilterCondition)
    {
        super(new ImmutableReorderedList<NamedData<D>>(
                originalMatrix.getNamedDataList(),
                removeFilterCondition));
    }
    
    /**
     * Presents a reordered view of the given {@link NamedDataMatrix}.
     * See {@link ImmutableReorderedList} for details on how the reordering
     * works since we're just delegating to this list.
     * @param originalMatrix
     *          the original matrix
     * @param toOriginalOrderMapping
     *          the mapping that we use on the data from
     *          {@link #getNamedDataList()}
     */
    public ReorderedNamedDataMatrix(
            NamedDataMatrix<D> originalMatrix,
            int[] toOriginalOrderMapping)
    {
        super(new ImmutableReorderedList<NamedData<D>>(
                originalMatrix.getNamedDataList(),
                toOriginalOrderMapping));
    }

    /**
     * This implementation just provides a signature that is more
     * type-specific than our parent's signature.
     * In every other way it is the same.
     * @return
     *          the reordered list
     */
    @Override
    public ImmutableReorderedList<NamedData<D>> getNamedDataList()
    {
        return (ImmutableReorderedList<NamedData<D>>)super.getNamedDataList();
    }
}

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

import java.util.Collection;
import java.util.SortedSet;

/**
 * A named data type that supports selection and eventing
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public interface SelectableData
{
    /**
     * Select the datum at the given index. Has no effect if
     * the index is already selected.
     * @param indexToSelect
     *          the index to select
     */
    public void selectIndex(int indexToSelect);
    
    /**
     * Determine if the given index is currently selected.
     * @param indexToCheck
     *          the index we're checking
     * @return
     *          true iff the given index is selected
     */
    public boolean isIndexSelected(int indexToCheck);
    
    /**
     * Deselect the datum at the given index. Has no effect
     * if the index is not currently selected
     * @param indexToDeselect
     *          the index to deselect
     */
    public void deselectIndex(int indexToDeselect);
    
    /**
     * Clear all selections.
     */
    public void clearSelections();
    
    /**
     * Select all of the given indices. This method adds to the current
     * selection.
     * @param indicesToSelect
     *          the indices for us to select
     */
    public void selectAllIndices(Collection<Integer> indicesToSelect);
    
    /**
     * Deselect all of the given indices.
     * @param indicesToDeselect
     *          the indices for us to deselect
     */
    public void deselectAllIndices(Collection<Integer> indicesToDeselect);
    
    /**
     * Set the selected indices. Do nothing if the set returned by
     * {@link #getSelectedIndices()} matches the given indices
     * perfectly.
     * @param selectedIndicies
     */
    public void setSelectedIndices(SortedSet<Integer> selectedIndicies);
    
    /**
     * Get the selected data indices. Returns an immutable, synchronized
     * view.
     * @return
     *          the indices that are currently selected
     */
    public SortedSet<Integer> getSelectedIndices();
    
    /**
     * Add a new listener.
     * @param listenerToAdd
     *          the new listener
     */
    public void addSelectableDataListener(SelectableDataListener listenerToAdd);
    
    /**
     * Remove the new listener
     * @param listenerToRemove
     *          the listener to remove
     */
    public void removeSelectableDataListener(
            SelectableDataListener listenerToRemove);
    
    /**
     * Adds a slave instance. Whenever our selection state changes, we will
     * force the slave to have the same selection state.
     * @param slaveToAdd
     *          the slave that we're adding
     */
    public void addSelectableDataSlave(SelectableData slaveToAdd);
    
    /**
     * Removes a slave instance.
     * @param slaveToRemove
     *          the slave that we're removing
     * @see #addSelectableDataSlave(SelectableData)
     */
    public void removeSelectableDataSlave(SelectableData slaveToRemove);
}

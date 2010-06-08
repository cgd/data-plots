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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A support class that contains a reusable implementation of the
 * {@link SelectableData} interface.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class SelectableDataSupport implements SelectableData
{
    /**
     * the event source to use
     */
    private final SelectableData sourceSelectableData;
    
    /**
     * listeners
     */
    private final ConcurrentLinkedQueue<SelectableDataListener> listenerList;
    
    /**
     * slaves
     */
    private final ConcurrentLinkedQueue<WeakReference<SelectableData>> slaveList;
    
    /**
     * the set of selected indices
     */
    private final SortedSet<Integer> selectedIndices;
    
    /**
     * so that no one can touch our indices but us
     */
    private final SortedSet<Integer> immutableSelectedIndices;
    
    /**
     * Constructor
     * @param sourceSelectableData
     *          the event source to use when fireing events
     */
    public SelectableDataSupport(SelectableData sourceSelectableData)
    {
        this.sourceSelectableData = sourceSelectableData;
        
        // initialize all of the structures
        this.listenerList = new ConcurrentLinkedQueue<SelectableDataListener>();
        this.slaveList = new ConcurrentLinkedQueue<WeakReference<SelectableData>>();
        this.selectedIndices = Collections.synchronizedSortedSet(
                new TreeSet<Integer>());
        this.immutableSelectedIndices = Collections.unmodifiableSortedSet(
                this.selectedIndices);
    }

    /**
     * {@inheritDoc}
     */
    public void clearSelections()
    {
        if(!this.selectedIndices.isEmpty())
        {
            this.selectedIndices.clear();
            this.fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deselectIndex(int indexToDeselect)
    {
        boolean selectionModified =
            this.selectedIndices.remove(indexToDeselect);
        
        if(selectionModified)
        {
            this.fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIndexSelected(int indexToCheck)
    {
        return this.selectedIndices.contains(indexToCheck);
    }

    /**
     * {@inheritDoc}
     */
    public void selectIndex(int indexToSelect)
    {
        boolean selectionModified =
            this.selectedIndices.add(indexToSelect);
        
        if(selectionModified)
        {
            this.fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedIndices(SortedSet<Integer> selectedIndicies)
    {
        boolean selectionsModified = false;
        synchronized(this.selectedIndices)
        {
            if(!this.selectedIndices.equals(selectedIndicies))
            {
                selectionsModified = true;
                
                // the sets are not 100% equal, so we'll go forward with the
                // operation
                this.selectedIndices.clear();
                this.selectedIndices.addAll(selectedIndicies);
            }
        }
        
        // notify listeners if the set changed
        if(selectionsModified)
        {
            this.fireSelectionChanged();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public SortedSet<Integer> getSelectedIndices()
    {
        return this.immutableSelectedIndices;
    }

    /**
     * {@inheritDoc}
     */
    public void selectAllIndices(Collection<Integer> indicesToSelect)
    {
        boolean selectionModified =
            this.selectedIndices.addAll(indicesToSelect);
        
        if(selectionModified)
        {
            this.fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deselectAllIndices(Collection<Integer> indicesToDeselect)
    {
        boolean selectionModified =
            this.selectedIndices.removeAll(indicesToDeselect);
        
        if(selectionModified)
        {
            this.fireSelectionChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectableDataListener(SelectableDataListener listenerToAdd)
    {
        this.listenerList.add(listenerToAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectableDataListener(
            SelectableDataListener listenerToRemove)
    {
        this.listenerList.remove(listenerToRemove);
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectableDataSlave(SelectableData slaveToAdd)
    {
        this.slaveList.add(new WeakReference<SelectableData>(slaveToAdd));
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectableDataSlave(SelectableData slaveToRemove)
    {
        Iterator<WeakReference<SelectableData>> slaveRefIter =
            this.slaveList.iterator();
        while(slaveRefIter.hasNext())
        {
            WeakReference<SelectableData> currSlaveRef =
                slaveRefIter.next();
            SelectableData currSlave = currSlaveRef.get();
            if(currSlave != null)
            {
                if(slaveToRemove.equals(currSlave))
                {
                    // found it!
                    slaveRefIter.remove();
                    return;
                }
            }
            else
            {
                // we lost this reference... throw it out
                // don't use iter.remove()... it's not supported
                // for CopyOnWriteArrayList
                this.slaveList.remove(currSlaveRef);
            }
        }
    }

    /**
     * tell our listeners that the selection changed
     */
    private void fireSelectionChanged()
    {
        for(SelectableDataListener currListener: this.listenerList)
        {
            currListener.selectionChanged(this.sourceSelectableData);
        }
        
        Iterator<WeakReference<SelectableData>> slaveRefIter =
            this.slaveList.iterator();
        while(slaveRefIter.hasNext())
        {
            WeakReference<SelectableData> currSlaveRef =
                slaveRefIter.next();
            SelectableData currSlave = currSlaveRef.get();
            if(currSlave != null)
            {
                currSlave.setSelectedIndices(this.getSelectedIndices());
            }
            else
            {
                slaveRefIter.remove();
            }
        }
    }
}

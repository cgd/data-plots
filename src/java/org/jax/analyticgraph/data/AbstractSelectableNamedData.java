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
import java.util.List;
import java.util.SortedSet;

import org.jax.util.Condition;
import org.jax.util.ObjectUtil;
import org.jax.util.datastructure.SequenceUtilities;

/**
 * An abstract base class that contains a lot of the functionality needed
 * for {@link NamedData} that are also {@link SelectableData}.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 * @param <D> the type of data this class holds
 */
public abstract class AbstractSelectableNamedData<D>
implements NamedData<D>, SelectableData
{
    /**
     * Variable that holds the name given to this data
     */
    private final String nameOfData;
    
    /**
     * the real implementation for selectable data
     */
    private final SelectableDataSupport selectableDataSupport =
        new SelectableDataSupport(this);

    /**
     * Constructor.
     * @param nameOfData    the name given to this data
     */
    public AbstractSelectableNamedData(final String nameOfData)
    {
        this.nameOfData = nameOfData;
    }
    
    /**
     * {@inheritDoc}
     */
    public NamedData<D> createDataSubset(
            Condition<D> filterCondition,
            String subsetName)
    {
        List<D> data = this.getData();
        boolean[] filter = SequenceUtilities.testInputs(
                filterCondition,
                data);
        return this.createDataSubset(filter, subsetName);
    }
    
    /**
     * {@inheritDoc}
     */
    public NamedData<D> createDataSubset(boolean[] filter, String subsetName)
    {
        // don't create a subset unless we need to
        if(SequenceUtilities.anyTrue(filter))
        {
            return new NamedDataSubset<D>(this, filter, subsetName);
        }
        else
        {
            return this;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSize()
    {
        return this.getData().size();
    }

    /**
     * {@inheritDoc}
     */
    public String getNameOfData()
    {
        return this.nameOfData;
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectableDataListener(SelectableDataListener listenerToAdd)
    {
        this.selectableDataSupport.addSelectableDataListener(
                listenerToAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void clearSelections()
    {
        this.selectableDataSupport.clearSelections();
    }

    /**
     * {@inheritDoc}
     */
    public void deselectIndex(int indexToDeselect)
    {
        this.selectableDataSupport.deselectIndex(indexToDeselect);
    }

    /**
     * {@inheritDoc}
     */
    public SortedSet<Integer> getSelectedIndices()
    {
        return this.selectableDataSupport.getSelectedIndices();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIndexSelected(int indexToCheck)
    {
        return this.selectableDataSupport.isIndexSelected(indexToCheck);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectableDataListener(
            SelectableDataListener listenerToRemove)
    {
        this.selectableDataSupport.removeSelectableDataListener(
                listenerToRemove);
    }

    /**
     * {@inheritDoc}
     */
    public void selectAllIndices(Collection<Integer> indicesToSelect)
    {
        this.selectableDataSupport.selectAllIndices(indicesToSelect);
    }

    /**
     * {@inheritDoc}
     */
    public void deselectAllIndices(Collection<Integer> indicesToDeselect)
    {
        this.selectableDataSupport.deselectAllIndices(indicesToDeselect);
    }

    /**
     * {@inheritDoc}
     */
    public void selectIndex(int indexToSelect)
    {
        this.selectableDataSupport.selectIndex(indexToSelect);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedIndices(SortedSet<Integer> selectedIndicies)
    {
        this.selectableDataSupport.setSelectedIndices(selectedIndicies);
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectableDataSlave(SelectableData slaveToAdd)
    {
        this.selectableDataSupport.addSelectableDataSlave(slaveToAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectableDataSlave(SelectableData slaveToRemove)
    {
        this.selectableDataSupport.removeSelectableDataSlave(slaveToRemove);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object otherNamedDataObject)
    {
        if(otherNamedDataObject == this)
        {
            return true;
        }
        else if(otherNamedDataObject == null)
        {
            return false;
        }
        else if(otherNamedDataObject instanceof NamedData)
        {
            NamedData otherNamedData =
                (NamedData)otherNamedDataObject;
            return
                    ObjectUtil.areEqual(
                            this.getNameOfData(),
                            otherNamedData.getNameOfData()) &&
                    ObjectUtil.areEqual(
                            this.getData(),
                            otherNamedData.getData());
        }
        else
        {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return ObjectUtil.hashObject(this.getNameOfData());
    }
}

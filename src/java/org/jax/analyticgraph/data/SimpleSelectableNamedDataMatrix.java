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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.jax.util.Condition;
import org.jax.util.ObjectUtil;
import org.jax.util.datastructure.SequenceUtilities;

/**
 * An immutable implementation of the {@link NamedDataMatrix}
 * interface, with selectable indices.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 * @param <D> the data type this class holds
 */
public class SimpleSelectableNamedDataMatrix<D>
implements NamedDataMatrix<D>, SelectableData
{
    /**
     * the real implementation for selectable data
     */
    private final SelectableDataSupport selectableDataSupport =
        new SelectableDataSupport(this);

    /**
     * @see #getNamedDataList()
     */
    private final List<NamedData<D>> namedDataList;
    
    /**
     * Constructor.
     * @param namedDataArray
     *          see {@link #getNamedDataList()}
     * @throws IllegalArgumentException
     *          if all of the named data objects don't have the
     *          same number of data elements
     */
    public SimpleSelectableNamedDataMatrix(
            final NamedData<D>[] namedDataArray)
            throws IllegalArgumentException
    {
        this(Arrays.asList(namedDataArray));
    }
    
    /**
     * Constructor.
     * @param namedDataList
     *          see {@link #getNamedDataList()}
     * @throws IllegalArgumentException
     *          if all of the named data objects don't have the
     *          same number of data elements
     */
    public SimpleSelectableNamedDataMatrix(
            final List<NamedData<D>> namedDataList)
            throws IllegalArgumentException
    {
        // throw an exception if the lengths don't match up
        Iterator<NamedData<D>> namedDataIter =
            namedDataList.iterator();
        if(namedDataIter.hasNext())
        {
            NamedData<D> prevData = namedDataIter.next();
            while(namedDataIter.hasNext())
            {
                NamedData<D> currData = namedDataIter.next();
                
                if(currData.getSize() != prevData.getSize())
                {
                    throw new IllegalArgumentException(
                            "Mismatch in named data lengths: length of \"" +
                            currData.getNameOfData() + "\" = " + currData.getSize() +
                            ", length of \"" + prevData.getNameOfData() + "\" = " +
                            prevData.getSize());
                }
                
                prevData = currData;
            }
        }
        
        // set up a mutually slaved situation with all our selectable
        // data lists
        for(NamedData<D> currNamedData: namedDataList)
        {
            if(currNamedData instanceof SelectableData)
            {
                SelectableData currSelectableData =
                    (SelectableData)currNamedData;
                currSelectableData.addSelectableDataSlave(this);
                this.addSelectableDataSlave(currSelectableData);
            }
        }
        
        // data looks ok so we can proceed
        this.namedDataList = namedDataList;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<NamedData<D>> getNamedDataList()
    {
        return this.namedDataList;
    }

    /**
     * {@inheritDoc}
     */
    public NamedDataMatrix<D> createMatrixSubset(
            Condition<D> filterCondition)
    {
        Iterator<NamedData<D>> dataListIter =
            this.getNamedDataList().iterator();
        
        if(dataListIter.hasNext())
        {
            // initialize the cumulative filter
            boolean[] cumulativeFilterValues = SequenceUtilities.testInputs(
                    filterCondition,
                    dataListIter.next().getData());
            
            // iterate through the rest and accumulate their filter values
            while(dataListIter.hasNext())
            {
                boolean[] currFilterValues = SequenceUtilities.testInputs(
                        filterCondition,
                        dataListIter.next().getData());
                
                // collapse the filter values
                for(int i = 0; i < currFilterValues.length; i++)
                {
                    if(currFilterValues[i])
                    {
                        cumulativeFilterValues[i] = true;
                    }
                }
            }
            
            // see if anything was filtered out
            if(SequenceUtilities.anyTrue(cumulativeFilterValues))
            {
                // we can't get away cheap on this... do the actual filtering
                // start up a new iterator
                dataListIter = this.getNamedDataList().iterator();
                NamedData<D>[] filteredData =
                    SequenceUtilities.instantiateGenericArray(
                            this.getNamedDataList().get(0).getClass(),
                            this.getNamedDataList().size());
                
                // iterate through all, subsetting as we go
                for(int i = 0; dataListIter.hasNext(); i++)
                {
                    filteredData[i] = dataListIter.next().createDataSubset(
                            cumulativeFilterValues,
                            null);
                }
                
                return new SimpleSelectableNamedDataMatrix<D>(
                        filteredData);
            }
            else
            {
                // everything passed through our filter
                return this;
            }
        }
        else
        {
            // no filtering to do
            return this;
        }
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
    public void addSelectableDataListener(SelectableDataListener listenerToAdd)
    {
        this.selectableDataSupport.addSelectableDataListener(
                listenerToAdd);
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
        this.selectableDataSupport.removeSelectableDataSlave(
                slaveToRemove);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getDataNames()
    {
        List<NamedData<D>> dataList = this.getNamedDataList();
        String[] dataNames = new String[dataList.size()];
        Iterator<NamedData<D>> dataIter = dataList.iterator();
        for(int i = 0; i < dataNames.length; i++)
        {
            dataNames[i] = dataIter.next().getNameOfData();
        }
        
        return dataNames;
    }

    /**
     * {@inheritDoc}
     */
    public NamedData<D> getDataWithName(String name)
    {
        for(NamedData<D> currData: this.getNamedDataList())
        {
            if(currData.getNameOfData().equals(name))
            {
                return currData;
            }
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object otherMatrixObject)
    {
        if(this == otherMatrixObject)
        {
            return true;
        }
        else if(otherMatrixObject instanceof SimpleSelectableNamedDataMatrix)
        {
            NamedDataMatrix otherMatrix =
                (NamedDataMatrix)otherMatrixObject;
            
            return ObjectUtil.areEqual(
                    this.namedDataList,
                    otherMatrix.getNamedDataList());
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
        return ObjectUtil.hashObject(this.namedDataList);
    }
}

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
import java.util.Arrays;
import java.util.List;

/**
 * Holds named categorical data.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class NamedCategoricalData extends AbstractSelectableNamedData<Number>
{
    /**
     * @see #getCategoricalNumericalData()
     */
    private final Integer[] categoricalNumericalData;
    
    /**
     * @see #getCategoryNames()
     */
    private final String[] categoryNames;

    /**
     * @see #getNullCategoryName()
     */
    private final String nullCategoryName;
    
    /**
     * @see #getData()
     */
    private ArrayList<Number> numericalData;
    
    /**
     * Constructor
     * @param nameOfData
     *          the name
     * @param categoricalNumericalData
     *          the catagory data. these values act as an index into the
     *          given category names
     * @param categoryNames
     *          the category names
     */
    public NamedCategoricalData(
            final String nameOfData,
            final Integer[] categoricalNumericalData,
            final String categoryNames[])
    {
        this(nameOfData,
             categoricalNumericalData,
             categoryNames,
             "Missing");
    }
    
    /**
     * Constructor
     * @param nameOfData
     *          the name
     * @param categoricalNumericalData
     *          the catagory data. these values act as an index into the
     *          given category names
     * @param categoryNames
     *          the category names
     * @param nullCategoryName
     *          the category name to use for missing types
     */
    public NamedCategoricalData(
            final String nameOfData,
            final Integer[] categoricalNumericalData,
            final String categoryNames[],
            final String nullCategoryName)
    {
        super(nameOfData);
        
        this.categoricalNumericalData = categoricalNumericalData;
        this.categoryNames = categoryNames;
        this.nullCategoryName = nullCategoryName;
        
        this.numericalData = new ArrayList<Number>(
                categoricalNumericalData.length);
        for(Integer currCategoricalNumericalDatum: categoricalNumericalData)
        {
            this.numericalData.add(currCategoricalNumericalDatum);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Number> getData()
    {
        return Arrays.asList((Number[])this.categoricalNumericalData);
    }

    /**
     * the catagory data. these values act as an index into the
     * given category names
     * @return the categoricalNumericalData
     */
    public Integer[] getCategoricalNumericalData()
    {
        return this.categoricalNumericalData;
    }
    
    /**
     * This is just a shortcut for passing the result from
     * {@link #getCategoricalNumericalData()} at an index into the
     * {@link #integerToCategoryString(Integer)} function
     * @param index
     *          the index
     * @return
     *          the category value at the index
     */
    public String getCategoryStringAt(int index)
    {
        Integer value = this.getCategoricalNumericalData()[index];
        if(value == null)
        {
            return null;
        }
        else
        {
            return this.integerToCategoryString(
                    this.getCategoricalNumericalData()[index]);
        }
    }
    
    /**
     * Convert the given category value into a category string
     * @param categoryInt
     *          the integer to convert. if this is null we use the value
     *          from {@link #getNameOfData()} otherwise we use this number as
     *          an index into {@link #getCategoryNames()}
     * @return
     *          the string value of the given category integer
     */
    public String integerToCategoryString(Integer categoryInt)
    {
        if(categoryInt == null)
        {
            return this.nullCategoryName;
        }
        else
        {
            return this.getCategoryNames()[categoryInt.intValue()];
        }
    }

    /**
     * the category names
     * @return the categoryNames
     */
    public String[] getCategoryNames()
    {
        return this.categoryNames;
    }

    /**
     * @return the nullCategoryName
     */
    public String getNullCategoryName()
    {
        return this.nullCategoryName;
    }

}

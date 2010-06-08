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

package org.jax.analyticgraph.framework;

/**
 * Interface for graph coordinate converters that can contain
 * other graph coordiante converters.
 * 
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public abstract class GraphCoordinateConverterContainer implements GraphCoordinateConverter
{
    /**
     * Add the coordinate converter.
     * @param coordinateConverterToAdd
     *          the coordinate converter that we're adding
     */
    public abstract void addGraphCoordinateConverter(
            GraphCoordinateConverter coordinateConverterToAdd);
    
    /**
     * Remove the coordinate converter.
     * @param coordinateConverterToRemove
     *          the coordinate converter that we're removing
     */
    public abstract void removeGraphCoordinateConverter(
            GraphCoordinateConverter coordinateConverterToRemove);
    
    /**
     * Update the dimensions of the given nested coordinate converter using
     * the container's absolute dimension values.
     * @see #updateDimensionsOfNestedCoordinateConverter(double, double, double, double, GraphCoordinateConverter)
     * @param containingCoordinateConverter
     *          the container that we're using to make the updates
     * @param nestedCoordinateConverter
     *          the nested coordinate converter that we're updating
     */
    public static void updateDimensionsOfNestedCoordinateConverter(
            final GraphCoordinateConverter containingCoordinateConverter,
            final GraphCoordinateConverter nestedCoordinateConverter)
    {
        updateDimensionsOfNestedCoordinateConverter(
                containingCoordinateConverter.getAbsoluteXOffsetInPixels(),
                containingCoordinateConverter.getAbsoluteYOffsetInPixels(),
                containingCoordinateConverter.getAbsoluteWidthInPixels(),
                containingCoordinateConverter.getAbsoluteHeightInPixels(),
                nestedCoordinateConverter);
    }
    
    /**
     * Update the dimensions of the given nested coordinate converter using
     * the container's absolute dimension values. Note that the dimensions
     * given apply to the nested coordinate converter's container and not
     * the nested coordinate converter itself.
     * 
     * @param containerAbsoluteXOffsetInPixels
     *          see {@link #getAbsoluteXOffsetInPixels()}
     * @param containerAbsoluteYOffsetInPixels
     *          see {@link #getAbsoluteYOffsetInPixels()}
     * @param containerAbsoluteWidthInPixels
     *          see {@link #getAbsoluteWidthInPixels()}
     * @param containerAbsoluteHeightInPixels
     *          see {@link #getAbsoluteHeightInPixels()}
     * @param nestedCoordinateConverter
     *          the nested coordinate converter
     */
    public static void updateDimensionsOfNestedCoordinateConverter(
            final double containerAbsoluteXOffsetInPixels,
            final double containerAbsoluteYOffsetInPixels,
            final double containerAbsoluteWidthInPixels,
            final double containerAbsoluteHeightInPixels,
            final GraphCoordinateConverter nestedCoordinateConverter)
    {
        double currAbsWidth =
            nestedCoordinateConverter.getContainerRelativeWidth() * containerAbsoluteWidthInPixels;
        double currAbsHeight =
            nestedCoordinateConverter.getContainerRelativeHeight() * containerAbsoluteHeightInPixels;
        double currAbsXOffset =
            containerAbsoluteXOffsetInPixels +
            (nestedCoordinateConverter.getContainerRelativeXOffset() * containerAbsoluteWidthInPixels);
        
        // Y axis needs special treatment because of the whole upper vs.
        // lower origin thing
        double currAbsYOffset =
            containerAbsoluteYOffsetInPixels +
            containerAbsoluteHeightInPixels -
            (currAbsHeight + nestedCoordinateConverter.getContainerRelativeYOffset() * containerAbsoluteHeightInPixels);
        
        nestedCoordinateConverter.updateAbsoluteDimensions(
                currAbsXOffset,
                currAbsYOffset,
                currAbsWidth,
                currAbsHeight);
    }
}

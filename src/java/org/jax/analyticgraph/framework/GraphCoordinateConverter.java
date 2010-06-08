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

import java.awt.geom.Point2D;

/**
 * This coordinate converter is able to convert between a
 * Java2D container and a graph coordinate system. The origin in the Java2D
 * coordinate system is at the upper left with x values increasing
 * the further down you go. The graph coordinate system uses
 * the bottom left as the origin and x values increase in the
 * up direction. Also, the Java2D coordinate system should be
 * computed based on the size of the containing component and
 * the "container relative" dimensions that have been set. The
 * graph coordinate system is independent of the size of the
 * container. The graph dimensions can be updated using the
 * {@link #updateGraphDimensions(double, double, double, double)}
 * method while the Java2D dimensions can be updated using
 * the {@link #updateAbsoluteDimensions(double, double, double, double)}
 * method. The role that the "container relative" properties play
 * is that they define the graph systems size and offset relative
 * to their immediate container.
 * 
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public interface GraphCoordinateConverter
{
    /**
     * Convert from the java coordinate system to the graph coordinate system
     * (see class comments). This is the inverse of
     * {@link #convertGraphCoordinateToJava2DCoordinate(Point2D)}
     * @param java2DPointToConvert
     *          the Java2D coordinate to convert
     * @return
     *          the point in graph coordinates
     */
    public Point2D convertJava2DCoordinateToGraphCoordinate(
            final Point2D java2DPointToConvert);
    
    /**
     * Convert from the graph coordinate system to the Java2D coordinate system
     * (see class comments). This is the inverse of
     * {@link #convertJava2DCoordinateToGraphCoordinate(Point2D)}
     * @param graphPointToConvert
     *          the graph coordinate to convert
     * @return
     *          the point in Java2D coordinates
     */
    public Point2D convertGraphCoordinateToJava2DCoordinate(
            final Point2D graphPointToConvert);
    
    /**
     * Convert from a java 2d x coordinate to a graph x coordinate.
     * @param java2DXCoordinateToConvert
     *          the coordinate we're converting
     * @return
     *          the result
     */
    public double convertJava2DXCoordinateToGraphXCoordinate(
            final double java2DXCoordinateToConvert);
    
    /**
     * Convert from a Java2D Y coordinate to a graph X coordinate.
     * @param java2DYCoordinateToConvert
     *          the coordinate we're converting
     * @return
     *          the result
     */
    public double convertJava2DYCoordinateToGraphYCoordinate(
            final double java2DYCoordinateToConvert);
    
    /**
     * Convert from a graph X coordinate to a Java2D X coordinate.
     * @param graphXCoordinate
     *          the coordinate that we're converting
     * @return
     *          the result
     */
    public double convertGraphXCoordinateToJava2DXCoordinate(
            final double graphXCoordinate);
    
    /**
     * Convert from a graph Y coordinate to a Java2D Y coordinate.
     * @param graphYCoordinate
     *          the coordinate that we're converting
     * @return
     *          the result
     */
    public double convertGraphYCoordinateToJava2DYCoordinate(
            final double graphYCoordinate);
    
    /**
     * Convert the given graph height value into a Java2D height
     * value. This is a magnitude, so both the parameter and return
     * values should always be positive.
     * @param graphHeightValue
     *          the height value that we're converting from
     *          the graph coordinate system
     * @return
     *          the height value converted to Java2D space
     */
    public double convertGraphHeightValueToJava2DHeightValue(
            final double graphHeightValue);
    
    /**
     * Convert the given Java2D height value into a graph height
     * value. This is the inverse of
     * {@link #convertGraphHeightValueToJava2DHeightValue(double)}
     * @param java2DHeightValue
     *          the Java2D height value to convert
     * @return
     *          the converted graph height value
     */
    public double convertJava2DHeightValueToGraphHeightValue(
            final double java2DHeightValue);
    
    /**
     * Basically the same as {@link #convertGraphHeightValueToJava2DHeightValue(double)}
     * except that we're talking in terms of width not height.
     * @param graphWidthValue
     *          the width value that we're converting from
     *          the graph coordinate system
     * @return
     *          the width value converted to Java2D space
     */
    public double convertGraphWidthValueToJava2DWidthValue(
            final double graphWidthValue);
    
    /**
     * Convert the given Java2D width value to a graph width value.
     * This function is basically the inverse of
     * {@link #convertGraphWidthValueToJava2DWidthValue(double)}
     * @param java2DWidthValue
     *          the Java2D width value to convert
     * @return
     *          the converted graph value
     */
    public double convertJava2DWidthValueToGraphWidthValue(
            final double java2DWidthValue);
    
    /**
     * Getter for the nested coordinate system's x offset. The units
     * are a factor of the containing coordinate system's width.
     * @return
     *          the nested coordinate system's offset along
     *          the x-axis
     */
    public double getContainerRelativeXOffset();

    /**
     * Getter for the nested coordinate system's y offset. The units
     * are a factor of the containing coordinate system's height, using
     * a lower-left origin coordinate system. Note that this measures the offset
     * of the lower-left corner of this coordinate system from the lower
     * left corner of the containing system which is different from how
     * {@link #getAbsoluteYOffsetInPixels()} works.
     * @return
     *          the nested coordinate system's offset along
     *          the y-axis
     */
    public double getContainerRelativeYOffset();

    /**
     * Getter for the nested coordinate system's width. The units are a factor
     * of the containing coordinate system's width (eg. 0.5 means 50% of the
     * containing system's width)
     * @return
     *          the nested coordinate system's width (x-axis)
     */
    public double getContainerRelativeWidth();

    /**
     * Getter for the nested coordinate system's height. The units are a factor
     * of the containing coordinate system's height (eg. 0.5 means 50% of the
     * containing system's height)
     * @return
     *          the nested coordinate system's height (y-axis)
     */
    public double getContainerRelativeHeight();

    /**
     * Getter for the absolute x offset.
     * @return
     *          the absolute x offset
     */
    public double getAbsoluteXOffsetInPixels();

    /**
     * Getter for the absolute y offset. This is in pixles and uses
     * an upper-left origin. Also since we're using the upper left
     * model, this measures the offset of the upper left coordinate
     * of this coordinate system, which is different from the graph
     * origin of this coordinate system.
     * @return
     *          the absolute y offset
     */
    public double getAbsoluteYOffsetInPixels();

    /**
     * Getter for the absolute width in pixels.
     * @return
     *          the absolute width in pixels
     */
    public double getAbsoluteWidthInPixels();

    /**
     * Getter for the absolute height in pixels.
     * @return
     *          the absolute height in pixels
     */
    public double getAbsoluteHeightInPixels();
    
    /**
     * Getter for the graph origin's x value
     * @return
     *          the graph origin's x value
     */
    public double getGraphOriginX();
    
    /**
     * Getter for the graph origin's y value
     * @return
     *          the graph origin's y value
     */
    public double getGraphOriginY();
    
    /**
     * Getter for the graph's width
     * @return
     *          the graph's width
     */
    public double getGraphWidth();
    
    /**
     * Getter for the graph's height
     * @return
     *          the graph's height
     */
    public double getGraphHeight();
    
    /**
     * Update all of the graph dimensions. For more information on what this means,
     * see the class level documentation.
     * 
     * @param graphOriginX
     *          see {@link #getGraphOriginX()}
     * @param graphOriginY
     *          see {@link #getGraphOriginY()}
     * @param graphWidth
     *          see {@link #getGraphWidth()}
     * @param graphHeight
     *          see {@link #getGraphHeight()}
     */
    public void updateGraphDimensions(
            double graphOriginX,
            double graphOriginY,
            double graphWidth,
            double graphHeight);
    
    /**
     * Update all of the absolute dimensions
     * 
     * @param absoluteXOffsetInPixels
     *          see {@link #getAbsoluteXOffsetInPixels()}
     * @param absoluteYOffsetInPixels
     *          see {@link #getAbsoluteYOffsetInPixels()}
     * @param absoluteWidthInPixels
     *          see {@link #getAbsoluteWidthInPixels()}
     * @param absoluteHeightInPixels
     *          see {@link #getAbsoluteHeightInPixels()}
     */
    public void updateAbsoluteDimensions(
            double absoluteXOffsetInPixels,
            double absoluteYOffsetInPixels,
            double absoluteWidthInPixels,
            double absoluteHeightInPixels);
    
    /**
     * Update all of the container relative dimensions.
     * 
     * WARNING: This is a somewhat dangerous method... realize that it doesn't
     *          in itself change the absolute dimensions used for conversion
     *          to and from Java2D coordinates. If you're calling this function
     *          you should most likely be calling
     *          {@link #updateAbsoluteDimensions(double, double, double, double)}
     *          immediately after
     * 
     * @param containerRelativeXOffset
     *          see {@link #getContainerRelativeXOffset()}
     * @param containerRelativeYOffset
     *          see {@link #getContainerRelativeYOffset()}
     * @param containerRelativeWidth
     *          see {@link #getContainerRelativeWidth()}
     * @param containerRelativeHeight
     *          see {@link #getContainerRelativeHeight()}
     */
    // TODO there's probably a smarter way to do this without the WARNING
    public void updateContainerRelativeDimensions(
            double containerRelativeXOffset,
            double containerRelativeYOffset,
            double containerRelativeWidth,
            double containerRelativeHeight);

    /**
     * Determine if the given point in pixel units (ie absolute units)
     * falls inside of this graph coordinate converter
     * @param point
     *          the point
     * @return
     *          true iff the pixel is inside our bounds
     */
    public boolean isPixelPointInBounds(Point2D point);
    
    /**
     * Determine if the given point in graph units falls inside this
     * graph coordinate converter
     * @param graphPoint
     *          the graph point to test
     * @return
     *          true if we're in the coordinate converter bounds
     */
    public boolean isGraphPointInBounds(Point2D graphPoint);
    
    /**
     * Determine if the given point in graph units falls inside this
     * graph coordinate converter
     * @param graphPointX
     *          the graph point X coordinate to test
     * @param graphPointY
     *          the graph point Y coordinate to test
     * @return
     *          true if we're in the coordinate converter bounds
     */
    public boolean isGraphPointInBounds(double graphPointX, double graphPointY);
}

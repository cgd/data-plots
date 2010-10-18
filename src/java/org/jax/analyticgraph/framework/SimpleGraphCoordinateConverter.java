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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple implementation of the graph coordinate converter container.
 * 
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class SimpleGraphCoordinateConverter extends GraphCoordinateConverterContainer
{
    /**
     * Our logger
     */
    private static final Logger LOG = Logger.getLogger(SimpleGraphCoordinateConverter.class.getName());
    
    /**
     * @see #getContainerRelativeXOffset()
     */
    private double containerRelativeXOffset;
    
    /**
     * @see #getContainerRelativeYOffset()
     */
    private double containerRelativeYOffset;

    /**
     * @see #getContainerRelativeWidth()
     */
    private double containerRelativeWidth;
    
    /**
     * @see #getContainerRelativeHeight()
     */
    private double containerRelativeHeight;
    
    /**
     * @see #getAbsoluteWidthInPixels()
     */
    private double absoluteWidthInPixels;
    
    /**
     * @see #getAbsoluteHeightInPixels()
     */
    private double absoluteHeightInPixels;
    
    /**
     * @see #getAbsoluteXOffsetInPixels()
     */
    private double absoluteXOffsetInPixels;
    
    /**
     * @see #getAbsoluteYOffsetInPixels()
     */
    private double absoluteYOffsetInPixels;
    
    /**
     * the list of nested coordinate converters
     */
    private List<GraphCoordinateConverter> nestedConverterList =
        Collections.synchronizedList(new ArrayList<GraphCoordinateConverter>());

    /**
     * @see #getGraphOriginX()
     */
    private double graphOriginX;

    /**
     * @see #getGraphOriginY()
     */
    private double graphOriginY;

    /**
     * @see #getGraphHeight()
     */
    private double graphHeight;

    /**
     * @see #getGraphWidth()
     */
    private double graphWidth;

    /**
     * Default constructor. Sets all ranges on a 0->1 scale
     */
    public SimpleGraphCoordinateConverter()
    {
        this(0.0, 0.0, 1.0, 1.0);
    }
    
    /**
     * Constructor. Sets default graph origin to (0, 0) and
     * both graph width and height to 1.
     * @param containerRelativeXOffset
     *          see {@link #getContainerRelativeXOffset()}
     * @param containerRelativeYOffset
     *          see {@link #getContainerRelativeYOffset()}
     * @param containerRelativeWidth
     *          see {@link #getContainerRelativeWidth()}
     * @param containerRelativeHeight
     *          see {@link #getContainerRelativeHeight()}
     */
    public SimpleGraphCoordinateConverter(
            final double containerRelativeXOffset,
            final double containerRelativeYOffset,
            final double containerRelativeWidth,
            final double containerRelativeHeight)
    {
        this.containerRelativeXOffset = containerRelativeXOffset;
        this.containerRelativeYOffset = containerRelativeYOffset;
        this.containerRelativeWidth = containerRelativeWidth;
        this.containerRelativeHeight = containerRelativeHeight;
        
        // set default values for graph dimensions
        this.updateGraphDimensions(
                0.0, 0.0,
                1.0, 1.0);
    }
    
    /**
     * {@inheritDoc}
     */
    public double getAbsoluteWidthInPixels()
    {
        return this.absoluteWidthInPixels;
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteHeightInPixels()
    {
        return this.absoluteHeightInPixels;
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteXOffsetInPixels()
    {
        return this.absoluteXOffsetInPixels;
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteYOffsetInPixels()
    {
        return this.absoluteYOffsetInPixels;
    }

    /**
     * {@inheritDoc}
     */
    public double getContainerRelativeWidth()
    {
        return this.containerRelativeWidth;
    }

    /**
     * {@inheritDoc}
     */
    public double getContainerRelativeHeight()
    {
        return this.containerRelativeHeight;
    }

    /**
     * {@inheritDoc}
     */
    public double getContainerRelativeXOffset()
    {
        return this.containerRelativeXOffset;
    }

    /**
     * {@inheritDoc}
     */
    public double getContainerRelativeYOffset()
    {
        return this.containerRelativeYOffset;
    }

    /**
     * {@inheritDoc}
     */
    public Point2D convertGraphCoordinateToJava2DCoordinate(Point2D graphPointToConvert)
    {
        return new Point2D.Double(
                this.convertGraphXCoordinateToJava2DXCoordinate(graphPointToConvert.getX()),
                this.convertGraphYCoordinateToJava2DYCoordinate(graphPointToConvert.getY()));
    }

    /**
     * {@inheritDoc}
     */
    public Point2D convertJava2DCoordinateToGraphCoordinate(Point2D java2DPointToConvert)
    {
        return new Point2D.Double(
                this.convertJava2DXCoordinateToGraphXCoordinate(java2DPointToConvert.getX()),
                this.convertJava2DYCoordinateToGraphYCoordinate(java2DPointToConvert.getY()));
    }

    /**
     * {@inheritDoc}
     */
    public double convertGraphXCoordinateToJava2DXCoordinate(double graphXCoordinate)
    {
        // copy the graph width so that it doesn't change after our divide by zero check
        double copyOfGraphWidth = this.getGraphWidth();
        
        if(copyOfGraphWidth == 0.0)
        {
            LOG.warning("coordinate system has a height of zero");
            return 0;
        }
        else
        {
            // compensate for the offset
            double returnVal = graphXCoordinate - this.getGraphOriginX();
            
            // scale it to a 0 - 1 range
            returnVal /= copyOfGraphWidth;
            
            // scale it to Java2D size
            returnVal *= this.getAbsoluteWidthInPixels();
            
            // now apply the Java2D offset
            returnVal += this.getAbsoluteXOffsetInPixels();
            
            return returnVal;
        }
    }

    /**
     * {@inheritDoc}
     */
    public double convertGraphYCoordinateToJava2DYCoordinate(double graphYCoordinate)
    {
        // copy the graph height so that it doesn't change after our divide by zero check
        double copyOfGraphHeight = this.getGraphHeight();
        
        if(copyOfGraphHeight == 0.0)
        {
            LOG.warning("coordinate system has a height of zero");
            return 0;
        }
        else
        {
            // compensate for the offset
            double returnVal = graphYCoordinate - this.getGraphOriginY();
            
            // scale it to a 0 - 1 range
            returnVal /= copyOfGraphHeight;
            
            // flip the coordinate system to be upper origin instead of lower
            returnVal = 1.0 - returnVal;
            
            // scale it to Java2D size
            returnVal *= this.getAbsoluteHeightInPixels();
            
            // now apply the offset
            returnVal += this.getAbsoluteYOffsetInPixels();
            
            return returnVal;
        }
    }

    /**
     * {@inheritDoc}
     */
    public double convertJava2DXCoordinateToGraphXCoordinate(double java2DXCoordinateToConvert)
    {
        // make a temporary copy so the width doesn't change after the divide by zero check
        double copyOfAbsoluteWidth = this.getAbsoluteWidthInPixels();
        
        // start w/ a divide by zero check
        if(copyOfAbsoluteWidth == 0.0)
        {
            LOG.warning("coordinate system has an absolute width of zero");
            return 0.0;
        }
        else
        {
            // compensate for the offset
            double returnVal = java2DXCoordinateToConvert - this.getAbsoluteXOffsetInPixels();
            
            // scale to a 0 - 1 range
            returnVal /= copyOfAbsoluteWidth;
            
            // scale to our graph's range
            returnVal *= this.getGraphWidth();
            
            // compensate for graph offset
            returnVal += this.getGraphOriginX();
            
            return returnVal;
        }
    }

    /**
     * {@inheritDoc}
     */
    public double convertJava2DYCoordinateToGraphYCoordinate(double java2DYCoordinateToConvert)
    {
        // make a temporary copy so the height doesn't change after the divide by zero check
        double copyOfAbsoluteHeight = this.getAbsoluteHeightInPixels();
        
        // start w/ a divide by zero check
        if(copyOfAbsoluteHeight == 0.0)
        {
            LOG.warning("coordinate system has an absolute height of zero");
            return 0.0;
        }
        else
        {
            // compensate for the offset
            double returnVal = java2DYCoordinateToConvert - this.getAbsoluteYOffsetInPixels();
            
            // scale to a 0 - 1 range
            returnVal /= copyOfAbsoluteHeight;
            
            // flip the coordinate system to be lower origin instead of upper origin
            returnVal = 1.0 - returnVal;
            
            // scale to our graphs range
            returnVal *= this.getGraphHeight();
            
            // compensate for graph offset
            returnVal += this.getGraphOriginY();
            
            // that's it!
            return returnVal;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addGraphCoordinateConverter(GraphCoordinateConverter coordinateConverterToAdd)
    {
        synchronized(this.nestedConverterList)
        {
            this.nestedConverterList.add(coordinateConverterToAdd);
            
            // we need to communicate our absolute dimensions to this new coordinate system
            GraphCoordinateConverterContainer.updateDimensionsOfNestedCoordinateConverter(
                    this.getAbsoluteXOffsetInPixels(),
                    this.getAbsoluteYOffsetInPixels(),
                    this.getAbsoluteWidthInPixels(),
                    this.getAbsoluteHeightInPixels(),
                    coordinateConverterToAdd);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeGraphCoordinateConverter(GraphCoordinateConverter coordinateConverterToRemove)
    {
        this.nestedConverterList.remove(coordinateConverterToRemove);
    }

    /**
     * {@inheritDoc}
     */
    public void updateAbsoluteDimensions(
            double absoluteXOffsetInPixels,
            double absoluteYOffsetInPixels,
            double absoluteWidthInPixels,
            double absoluteHeightInPixels)
    {
        // set our local values
        this.absoluteXOffsetInPixels = absoluteXOffsetInPixels;
        this.absoluteYOffsetInPixels = absoluteYOffsetInPixels;
        this.absoluteWidthInPixels = absoluteWidthInPixels;
        this.absoluteHeightInPixels = absoluteHeightInPixels;
        
        // update all of our nested coordinate systems
        synchronized(this.nestedConverterList)
        {
            for(GraphCoordinateConverter currCoordinateConverter : this.nestedConverterList)
            {
                GraphCoordinateConverterContainer.updateDimensionsOfNestedCoordinateConverter(
                        absoluteXOffsetInPixels,
                        absoluteYOffsetInPixels,
                        absoluteWidthInPixels,
                        absoluteHeightInPixels,
                        currCoordinateConverter);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public double getGraphHeight()
    {
        return this.graphHeight;
    }

    /**
     * {@inheritDoc}
     */
    public double getGraphOriginX()
    {
        return this.graphOriginX;
    }

    /**
     * {@inheritDoc}
     */
    public double getGraphOriginY()
    {
        return this.graphOriginY;
    }

    /**
     * {@inheritDoc}
     */
    public double getGraphWidth()
    {
        return this.graphWidth;
    }

    /**
     * {@inheritDoc}
     */
    public void updateGraphDimensions(
            double graphOriginX,
            double graphOriginY,
            double graphWidth,
            double graphHeight)
    {
        this.graphOriginX = graphOriginX;
        this.graphOriginY = graphOriginY;
        this.graphWidth = graphWidth;
        this.graphHeight = graphHeight;
    }

    /**
     * {@inheritDoc}
     */
    public double convertGraphHeightValueToJava2DHeightValue(double graphHeightValue)
    {
        // make a copy of the graph height so that it doesn't change
        // after we do our divide by zero check
        double copyOfGraphHeight = this.getGraphHeight();
        
        if(copyOfGraphHeight == 0.0)
        {
            LOG.severe(
                    "cannot convert a graph height value to a Java2D height value " +
                    "because the graph height is set to zero");
            return 0.0;
        }
        else
        {
            // ok... no divide by zero, so we can move on
            return (graphHeightValue / copyOfGraphHeight) * this.getAbsoluteHeightInPixels();
        }
    }

    /**
     * {@inheritDoc}
     */
    public double convertGraphWidthValueToJava2DWidthValue(double graphWidthValue)
    {
        // make a copy of the graph width so that it doesn't change
        // after we do our divide by zero check
        double copyOfGraphWidth = this.getGraphWidth();
        
        if(copyOfGraphWidth == 0.0)
        {
            LOG.severe(
                    "cannot convert a graph width value to a Java2D width value " +
                    "because the graph width is set to zero");
            return 0.0;
        }
        else
        {
            // ok... no divide by zero, so we can move on
            return (graphWidthValue / copyOfGraphWidth) * this.getAbsoluteWidthInPixels();
        }
    }

    /**
     * {@inheritDoc}
     */
    public double convertJava2DHeightValueToGraphHeightValue(double java2DHeightValue)
    {
        // make a copy of the Java2D height so that it doesn't change
        // after we do our divide by zero check
        double copyOfAbsoluteHeightInPixels = this.getAbsoluteHeightInPixels();
        
        if(copyOfAbsoluteHeightInPixels == 0.0)
        {
            LOG.severe(
                    "cannot convert a Java2D height value to a graph height value " +
                    "because the Java2D height is set to zero");
            return 0.0;
        }
        else
        {
            // we're ok... no div by zero
            return (java2DHeightValue / copyOfAbsoluteHeightInPixels) * this.getGraphHeight();
        }
    }

    /**
     * {@inheritDoc}
     */
    public double convertJava2DWidthValueToGraphWidthValue(double java2DWidthValue)
    {
        // make a copy of the Java2D width so that it doesn't change
        // after we do our divide by zero check
        double copyOfAbsoluteWidthInPixels = this.getAbsoluteWidthInPixels();
        
        if(copyOfAbsoluteWidthInPixels == 0.0)
        {
            LOG.severe(
                    "cannot convert a Java2D width value to a graph width value " +
                    "because the Java2D width is set to zero");
            return 0.0;
        }
        else
        {
            // we're ok... no div by zero
            return (java2DWidthValue / copyOfAbsoluteWidthInPixels) * this.getGraphWidth();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateContainerRelativeDimensions(
            double containerRelativeXOffset,
            double containerRelativeYOffset,
            double containerRelativeWidth,
            double containerRelativeHeight)
    {
        this.containerRelativeXOffset = containerRelativeXOffset;
        this.containerRelativeYOffset = containerRelativeYOffset;
        this.containerRelativeWidth = containerRelativeWidth;
        this.containerRelativeHeight = containerRelativeHeight;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPixelPointInBounds(Point2D point)
    {
        double xStart = this.getAbsoluteXOffsetInPixels();
        double xStop = xStart + this.getAbsoluteWidthInPixels();
        double yStart = this.getAbsoluteYOffsetInPixels();
        double yStop = yStart + this.getAbsoluteHeightInPixels();
        
        return point.getX() >= xStart &&
               point.getX() <= xStop &&
               point.getY() >= yStart &&
               point.getY() <= yStop;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isGraphPointInBounds(Point2D graphPoint)
    {
        double xStart = this.getGraphOriginX();
        double xStop = xStart + this.getGraphWidth();
        double yStart = this.getGraphOriginY();
        double yStop = yStart + this.getGraphHeight();
        
        return graphPoint.getX() >= xStart &&
               graphPoint.getX() <= xStop &&
               graphPoint.getY() >= yStart &&
               graphPoint.getY() <= yStop;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isGraphPointInBounds(double graphPointX, double graphPointY)
    {
        double xStart = this.getGraphOriginX();
        double xStop = xStart + this.getGraphWidth();
        double yStart = this.getGraphOriginY();
        double yStop = yStart + this.getGraphHeight();
        
        return graphPointX >= xStart &&
               graphPointX <= xStop &&
               graphPointY >= yStart &&
               graphPointY <= yStop;
    }
}

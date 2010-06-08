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

package org.jax.analyticgraph.graph.scatterplot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jax.analyticgraph.data.NamedData;
import org.jax.analyticgraph.data.NamedDataMatrix;
import org.jax.analyticgraph.data.SelectableData;
import org.jax.analyticgraph.data.SelectableDataListener;
import org.jax.analyticgraph.framework.AbstractGraph2DWithAxes;
import org.jax.analyticgraph.framework.GraphCoordinateConverter;
import org.jax.analyticgraph.graph.AxisDescription;
import org.jax.analyticgraph.graph.RegularIntervalAxisDescription;
import org.jax.analyticgraph.graph.AxisDescription.AxisType;
import org.jax.util.datastructure.SequenceUtilities;
import org.jax.util.math.NumberComparator;

/**
 * The interior part of the scatter plot (this is basically everything
 * except for the X/Y axis along with labels).
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class ScatterPlot extends AbstractGraph2DWithAxes
{
    private static final int DEFAULT_TICK_SIGNIFICANT_DIGITS = 2;
    
    private static final int DEFAULT_NUMBER_OF_TICKS = 10;
    
    /**
     * our logger
     */
    private static final Logger LOG =
        Logger.getLogger(ScatterPlot.class.getName());
    
    /**
     * the data for our x axis
     */
    private NamedData<Number> xAxisData;
    
    /**
     * the description of the x axis
     */
    private RegularIntervalAxisDescription xAxisDescription;
    
    /**
     * the data for our y axis
     */
    private NamedData<Number> yAxisData;
    
    /**
     * the description of the y axis
     */
    private RegularIntervalAxisDescription yAxisDescription;
    
    /**
     * the minimum data point that we have on the x axis
     */
    private double xAxisMin;
    
    /**
     * the maximum data point that we have on the x axis
     */
    private double xAxisMax;
    
    /**
     * the minimum data point that we have on the y axis
     */
    private double yAxisMin;
    
    /**
     * the maximum data point that we have on the y axis
     */
    private double yAxisMax;
    
    /**
     * the rectangle representing the overall area that the user is selecting.
     * This selection rectangle uses Java2D color
     */
    private Rectangle selectionRectangle;
    
    /**
     * our mouse motion listener
     */
    private MouseMotionListener containerComponentMotionListener =
        new MouseMotionListener()
        {
            public void mouseDragged(MouseEvent event)
            {
                ScatterPlot.this.containerComponentMouseDragged(event);
            }
    
            public void mouseMoved(MouseEvent event)
            {
                
            }
        };

    /**
     * our mouse listener
     */
    private MouseListener containerComponentMouseListener =
        new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                ScatterPlot.this.containerComponentMouseClicked(event);
            }

            @Override
            public void mousePressed(MouseEvent event)
            {
                ScatterPlot.this.containerComponentMousePressed(event);
            }

            @Override
            public void mouseReleased(MouseEvent event)
            {
                ScatterPlot.this.containerComponentMouseReleased(event);
            }
        };
    
    /**
     * our graph data selection listener
     */
    private final SelectableDataListener graphDataSelectionListener =
        new SelectableDataListener()
        {
            public void selectionChanged(SelectableData selectableData)
            {
                // all we have to do is repaint
                ScatterPlot.this.repaintContainerComponent();
            }
        };
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the coordinate converter for this graph. note that this
     *          scatter plot will change the state of this converter
     */
    public ScatterPlot(GraphCoordinateConverter graphCoordinateConverter)
    {
        super(graphCoordinateConverter);
    }
    
    /**
     * clear out all selections
     */
    private synchronized void clearSelection()
    {
        if(this.xAxisData instanceof SelectableData)
        {
            SelectableData selectableXAxisData =
                (SelectableData)this.xAxisData;
            
            selectableXAxisData.clearSelections();
        }
        
        if(this.yAxisData instanceof SelectableData)
        {
            SelectableData selectableYAxisData =
                (SelectableData)this.yAxisData;
            
            selectableYAxisData.clearSelections();
        }
    }
    
    /**
     * The selection rectangle has been started.
     * @param startPoint
     *          the starting point
     */
    private synchronized void selectionRectangleStarted(Point startPoint)
    {
        this.selectionRectangle = new Rectangle(startPoint);
    }
    
    /**
     * We're finished with the selection rectangle
     * @param completionPoint
     *          the point that the rectangle ended at
     */
    private synchronized void selectionRectangleCompleted(Point completionPoint)
    {
        this.selectionRectangleDragged(completionPoint);
        
        SelectableData selectableData = this.getSelectableData();
        if(selectableData != null)
        {
            // add all the points in the selection rectangle
            selectableData.selectAllIndices(
                    this.getIndicesOfBoundedPoints(
                            this.selectionRectangle));
        }
        
        // kill the rectangle. we're done with it
        this.selectionRectangle = null;
        
        // repaint the component without the selection rectangle
        this.repaintContainerComponent();
    }
    
    /**
     * The selection rectangle has been dragged
     * @param dragPoint
     *          the location that it was dragged to
     */
    private synchronized void selectionRectangleDragged(Point dragPoint)
    {
        this.selectionRectangle.width =
            dragPoint.x - this.selectionRectangle.x;
        this.selectionRectangle.height =
            dragPoint.y - this.selectionRectangle.y;
        
        // repaint the component for the updated selection
        this.repaintContainerComponent();
    }
    
    /**
     * call repaint on the container component
     */
    private void repaintContainerComponent()
    {
        JComponent containerComponent = this.getContainerComponent();
        if(containerComponent != null)
        {
            containerComponent.repaint();
        }
        else
        {
            LOG.warning("failed to paint null container component");
        }
    }
    
    /**
     * Convenience function to get just the x axis values
     * @return
     *          the x axis values
     */
    private List<Number> getXAxisValues()
    {
        return this.xAxisData.getData();
    }
    
    /**
     * Convenience function to get just the y axis values
     * @return
     *          the y axis values
     */
    private List<Number> getYAxisValues()
    {
        return this.yAxisData.getData();
    }
    
    /**
     * Get indices of bounded points
     * @param java2DBoundingRectangle
     *          the bounding rectangle (in Java2D coordinates)
     * @return
     *          the indices that fall within the bounding rectangle
     */
    private synchronized List<Integer> getIndicesOfBoundedPoints(
            Rectangle java2DBoundingRectangle)
    {
        java2DBoundingRectangle = ScatterPlot.toNonNegativeWidthHeightRectangle(
                java2DBoundingRectangle);
        
        GraphCoordinateConverter coordConverter =
            this.getGraphCoordinateConverter();

        // change the bounding rectangle to graph coordinates
        double graphLeftXBound =
            coordConverter.convertJava2DXCoordinateToGraphXCoordinate(
                    java2DBoundingRectangle.x);
        double graphRightXBound =
            coordConverter.convertJava2DXCoordinateToGraphXCoordinate(
                    java2DBoundingRectangle.x + java2DBoundingRectangle.width);
        double graphUpperYBound =
            coordConverter.convertJava2DYCoordinateToGraphYCoordinate(
                    java2DBoundingRectangle.y);
        double graphLowerYBound =
            coordConverter.convertJava2DYCoordinateToGraphYCoordinate(
                    java2DBoundingRectangle.y + java2DBoundingRectangle.height);
        
        // grab the value lists
        List<Number> xAxisValues = this.getXAxisValues();
        List<Number> yAxisValues = this.getYAxisValues();
        int listSizes = xAxisValues.size();
        
        // find all the bound indices
        List<Integer> selectionIndecies =
            new ArrayList<Integer>(listSizes);
        
        for(int i = 0; i < listSizes; i++)
        {
            Number currXValueObj = xAxisValues.get(i);
            Number currYValueObj = yAxisValues.get(i);
            
            if(currXValueObj != null && currYValueObj != null)
            {
                double currXValue = currXValueObj.doubleValue();
                double currYValue = currYValueObj.doubleValue();
                
                // if the x and y value is bound, add it to the index list
                if(currXValue > graphLeftXBound && currXValue < graphRightXBound &&
                   currYValue > graphLowerYBound && currYValue < graphUpperYBound)
                {
                    selectionIndecies.add(i);
                }
            }
        }
        
        return selectionIndecies;
    }
    
    /**
     * deal with mouse click events
     * @param event
     *          the event
     */
    private void containerComponentMouseClicked(MouseEvent event)
    {
        List<Integer> pointIndecies = this.getPointIndicesAtJava2DCoordinate(event.getPoint());
        
        // if shift is down we're adding to the selection, not replacing it
        if(!event.isShiftDown())
        {
            this.clearSelection();
        }
        
        this.selectPointsAtIndecies(pointIndecies);
    }

    /**
     * @param pointIndecies
     */
    private void selectPointsAtIndecies(List<Integer> pointIndecies)
    {
        SelectableData selectableData = this.getSelectableData();
        if(selectableData != null)
        {
            selectableData.selectAllIndices(pointIndecies);
        }
    }

    /**
     * Get the points at the given Java2D coordinate.
     * @param java2DCoordinate 
     *          the java 2d coordinate
     * @return
     *          the points at the given coordinate or empty if there
     *          aren't any
     */
    private List<Integer> getPointIndicesAtJava2DCoordinate(Point java2DCoordinate)
    {
        NamedData<Number> xData = this.getXAxisData();
        NamedData<Number> yData = this.getYAxisData();
        
        Iterator<Number> xAxisIter = xData.getData().iterator();
        Iterator<Number> yAxisIter = yData.getData().iterator();
        
        GraphCoordinateConverter coordConverter =
            this.getGraphCoordinateConverter();
        
        List<Integer> clickedIndices = new ArrayList<Integer>();
        for(int currIndex = 0; xAxisIter.hasNext() && yAxisIter.hasNext(); currIndex++)
        {
            Number currXDatumObj = xAxisIter.next();
            Number currYDatumObj = yAxisIter.next();
            
            if(currXDatumObj != null && currYDatumObj != null)
            {
                double currXDatum = currXDatumObj.doubleValue();
                double currYDatum = currYDatumObj.doubleValue();
                
                double currJava2DXDatum =
                    coordConverter.convertGraphXCoordinateToJava2DXCoordinate(
                            currXDatum);
                double currJava2DYDatum =
                    coordConverter.convertGraphYCoordinateToJava2DYCoordinate(
                            currYDatum);
                
                // test if the click is close enough to the point center
                double distance = java2DCoordinate.distance(
                        currJava2DXDatum,
                        currJava2DYDatum);
                if(distance <= this.getPointWidth())
                {
                    clickedIndices.add(currIndex);
                }
            }
        }
        
        return clickedIndices;
    }

    /**
     * deal with mouse press events
     * @param event
     *          the event
     */
    private void containerComponentMousePressed(MouseEvent event)
    {
        this.selectionRectangleStarted(event.getPoint());
    }

    /**
     * deal with mouse release events
     * @param event
     *          the event
     */
    private void containerComponentMouseReleased(MouseEvent event)
    {
        if(!event.isShiftDown())
        {
            this.clearSelection();
        }
        
        this.selectionRectangleCompleted(event.getPoint());
    }

    /**
     * deal with mouse drag events
     * @param event
     *          the event
     */
    private void containerComponentMouseDragged(MouseEvent event)
    {
        this.selectionRectangleDragged(event.getPoint());
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void renderGraph(Graphics2D graphics2D)
    {
        // "push" the graphics data the we need to restore
        Color pushColor = graphics2D.getColor();
        
        boolean renderIntermediateSelection =
            this.selectionRectangle != null &&
            (this.selectionRectangle.getWidth() != 0.0 ||
            this.selectionRectangle.getHeight() != 0.0);
        
        // build a set of selected indices to render composed of the
        // intermediate selections and the current selections
        HashSet<Integer> combinedSelectedIndices =
            new HashSet<Integer>();
        SelectableData selectableData = this.getSelectableData();
        if(selectableData != null)
        {
            combinedSelectedIndices.addAll(
                    selectableData.getSelectedIndices());
            
            if(renderIntermediateSelection)
            {
                combinedSelectedIndices.addAll(
                        this.getIndicesOfBoundedPoints(this.selectionRectangle));
            }
        }
        
        
        Iterator<Number> xNumbersIter =
            this.xAxisData.getData().iterator();
        Iterator<Number> yNumbersIter =
            this.yAxisData.getData().iterator();
        for(int currDatumIndex = 0; xNumbersIter.hasNext() && yNumbersIter.hasNext(); currDatumIndex++)
        {
            Number nextXNumber = xNumbersIter.next();
            Number nextYNumber = yNumbersIter.next();
            
            if(nextXNumber != null && nextYNumber != null)
            {
                this.renderPoint(
                        graphics2D,
                        combinedSelectedIndices.contains(currDatumIndex),
                        nextXNumber.doubleValue(),
                        nextYNumber.doubleValue());
            }
            else
            {
                if(LOG.isLoggable(Level.FINE))
                {
                    LOG.fine(
                            "Not plotting scatter plot point for X Axis=" +
                            this.xAxisData.getNameOfData() + ", Y Axis=" +
                            this.yAxisData.getNameOfData() +
                            ", index=" + currDatumIndex +
                            " because at least one of the axis values is null: " +
                            "x=" + nextXNumber + ", y=" + nextYNumber);
                }
            }
        }
        
        if(renderIntermediateSelection)
        {
            this.renderSelectionRectangle(
                    graphics2D,
                    this.selectionRectangle);
        }
        
        // "pop" the graphics data that we need to restore
        graphics2D.setColor(pushColor);
    }
    
    /**
     * Plot the given correlated data. The x axis is the first
     * {@link NamedData} and the y axis is the second.
     * @param dataMatrix
     *          the data matrix to plot
     * @throws IndexOutOfBoundsException
     *          if there are less than 2 {@link NamedData}s
     *          in the given matrix
     */
    public synchronized void plotData(
            NamedDataMatrix<Number> dataMatrix)
            throws IndexOutOfBoundsException
    {
        // deregister for selection events on the old data
        SelectableData selectableData = this.getSelectableData();
        if(selectableData != null)
        {
            selectableData.removeSelectableDataListener(
                    this.graphDataSelectionListener);
        }
        
        this.xAxisData = dataMatrix.getNamedDataList().get(0);
        this.yAxisData = dataMatrix.getNamedDataList().get(1);
        
        // initialize min/max values to extreme opposites
        this.xAxisMin = Double.POSITIVE_INFINITY;
        this.xAxisMax = Double.NEGATIVE_INFINITY;
        this.yAxisMin = Double.POSITIVE_INFINITY;
        this.yAxisMax = Double.NEGATIVE_INFINITY;
        
        // find x min/max
        if(this.xAxisData.getData().size() >= 1)
        {
            Number xAxisMinNumber = SequenceUtilities.getMinDatum(
                    NumberComparator.getInstance(),
                    this.xAxisData.getData());
            Number xAxisMaxNumber = SequenceUtilities.getMaxDatum(
                    NumberComparator.getInstance(),
                    this.xAxisData.getData());
            this.xAxisMin = xAxisMinNumber.doubleValue();
            this.xAxisMax = xAxisMaxNumber.doubleValue();
        }
        
        // find y min/max
        if(this.yAxisData.getData().size() >= 1)
        {
            Number yAxisMinNumber = SequenceUtilities.getMinDatum(
                    NumberComparator.getInstance(),
                    this.yAxisData.getData());
            Number yAxisMaxNumber = SequenceUtilities.getMaxDatum(
                    NumberComparator.getInstance(),
                    this.yAxisData.getData());
            this.yAxisMin = yAxisMinNumber.doubleValue();
            this.yAxisMax = yAxisMaxNumber.doubleValue();
        }
        
        // register for selection events on the new data
        selectableData = this.getSelectableData();
        if(selectableData != null)
        {
            selectableData.addSelectableDataListener(
                    this.graphDataSelectionListener);
        }
        
        // use the min/max values to bound the graph's coordinate system
        this.getGraphCoordinateConverter().updateGraphDimensions(
                this.xAxisMin,
                this.yAxisMin,
                this.xAxisMax - this.xAxisMin,
                this.yAxisMax - this.yAxisMin);
        
        // OK, now update the axis descriptions
        this.xAxisDescription = new RegularIntervalAxisDescription(
                this.getGraphCoordinateConverter(),
                AxisType.X_AXIS,
                this.xAxisData.getNameOfData(),
                DEFAULT_NUMBER_OF_TICKS,
                DEFAULT_TICK_SIGNIFICANT_DIGITS,
                true);
        this.yAxisDescription = new RegularIntervalAxisDescription(
                this.getGraphCoordinateConverter(),
                AxisType.Y_AXIS,
                this.yAxisData.getNameOfData(),
                DEFAULT_NUMBER_OF_TICKS,
                DEFAULT_TICK_SIGNIFICANT_DIGITS,
                true);
    }

    /**
     * @return the xAxisData
     */
    public synchronized NamedData<Number> getXAxisData()
    {
        return this.xAxisData;
    }

    /**
     * @return the yAxisData
     */
    public synchronized NamedData<Number> getYAxisData()
    {
        return this.yAxisData;
    }
    
    /**
     * Gets the selectable data shared by x-axis data and y axis data or
     * null if they're not selectable. This is basically a convenience function
     * for casting.
     * @return
     *          the selectable data
     */
    private SelectableData getSelectableData()
    {
        NamedData<Number> xAxisData = this.getXAxisData();
        if(xAxisData instanceof SelectableData)
        {
            return (SelectableData)xAxisData;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized JComponent getContainerComponent()
    {
        return super.getContainerComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setContainerComponent(
            JComponent containerComponent)
    {
        // stop listening to old component
        JComponent currContainerComponent = this.getContainerComponent();
        if(currContainerComponent != null)
        {
            currContainerComponent.removeMouseListener(
                    this.containerComponentMouseListener);
            currContainerComponent.removeMouseMotionListener(
                    this.containerComponentMotionListener);
        }
        
        super.setContainerComponent(containerComponent);
        
        // start listening to new component
        if(containerComponent != null)
        {
            containerComponent.addMouseListener(
                    this.containerComponentMouseListener);
            containerComponent.addMouseMotionListener(
                    this.containerComponentMotionListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public AxisDescription getXAxisDescription()
    {
        return this.xAxisDescription;
    }

    /**
     * {@inheritDoc}
     */
    public AxisDescription getYAxisDescription()
    {
        return this.yAxisDescription;
    }
}

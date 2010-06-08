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

package org.jax.analyticgraph.graph.histogram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.swing.JComponent;

import org.jax.analyticgraph.data.NamedData;
import org.jax.analyticgraph.data.SelectableData;
import org.jax.analyticgraph.data.SelectableDataListener;
import org.jax.analyticgraph.framework.AbstractGraph2DWithAxes;
import org.jax.analyticgraph.framework.GraphCoordinateConverter;
import org.jax.analyticgraph.graph.AxisDescription;
import org.jax.analyticgraph.graph.RegularIntegerIntervalAxisDescription;
import org.jax.analyticgraph.graph.RegularIntervalAxisDescription;
import org.jax.analyticgraph.graph.AxisDescription.AxisType;
import org.jax.util.math.Matlab;
import org.jax.util.math.NumericUtilities;

/**
 * Renders a histogram
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 * @author Lei Wu (optimal bin sizing code)
 */
// TODO implement bar chart vs. histogram (frequency vs count and contiguous
//      bars vs stand alone bars)
// TODO implement int bins for int data
// TODO implement Integer detection in smart bining
// TODO implement categorical binning
public class Histogram extends AbstractGraph2DWithAxes
{
    /**
     * The default y axis name
     */
    // TODO add something for "(Relative) Frequency"
    private static final String DEFAULT_Y_AXIS_NAME = "Absolute Frequency";
    
    /**
     * the default number of ticks that we want to allow on the
     * y axis
     */
    private static final int DEFAULT_NUM_Y_AXIS_MAJOR_TICKS = 10;
    
    /**
     * This is the number of significant digits that we allow the
     * histogram bin size to have.
     */
    private static final int DEFAULT_HISTOGRAM_BIN_SIGNIFICANT_DIG = 2;
    
    /**
     * the numerical data that we're plotting
     */
    private NamedData<Number> graphData;
    
    /**
     * for mapping from the bin's position to the bin
     */
    private final SortedMap<BigDecimal, Bin> binMap =
        new TreeMap<BigDecimal, Bin>();
    
    /**
     * the width of each bin in graph units
     */
    private BigDecimal binWidth;
    
    /**
     * the default outline color
     */
    private static final Color DEFAULT_OUTLINE_COLOR = Color.BLACK;
    
    /**
     * @see #getOutlineColor()
     */
    private Color outlineColor = DEFAULT_OUTLINE_COLOR;
    
    /**
     * the default fill color
     */
    private static final Color DEFAULT_FILL_COLOR = Color.BLUE;
    
    /**
     * @see #getFillColor()
     */
    private Color fillColor = DEFAULT_FILL_COLOR;
    
    /**
     * the default selection color
     */
    private static final Color DEFAULT_SELECTION_COLOR = Color.RED;

    /**
     * @see #getSelectionColor()
     */
    private Color selectionColor = DEFAULT_SELECTION_COLOR;

    /**
     * our mouse listener
     */
    private final MouseListener containerComponentMouseListener =
        new MouseListener()
        {
            public void mouseClicked(MouseEvent event)
            {
                Histogram.this.containerComponentMouseClicked(event);
            }

            public void mouseEntered(MouseEvent e)
            {
            }

            public void mouseExited(MouseEvent e)
            {
            }

            public void mousePressed(MouseEvent e)
            {
            }

            public void mouseReleased(MouseEvent e)
            {
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
                Histogram.this.graphDataSelectionChanged();
            }
        };
    
    private volatile RegularIntervalAxisDescription xAxisDescription;
    
    private volatile RegularIntegerIntervalAxisDescription yAxisDescription;
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use
     */
    public Histogram(GraphCoordinateConverter graphCoordinateConverter)
    {
        super(graphCoordinateConverter);
    }
    
    /**
     * respond to a selection change event for graph data
     */
    protected void graphDataSelectionChanged()
    {
        this.recalculateBinSelections();
    }

    /**
     * Deal with the given click event
     * @param event
     *          the mouse event for this click
     */
    protected void containerComponentMouseClicked(MouseEvent event)
    {
        Bin binClicked = this.getBinAtJava2DCoordinates(event.getPoint());
        
        if(event.isShiftDown())
        {
            if(binClicked != null)
            {
                this.toggleBinSelections(binClicked);
            }
        }
        else
        {
            this.clearSelections();
            if(binClicked != null)
            {
                this.selectBin(binClicked);
            }
        }
    }

    /**
     * Get the bin that lives at the given coordinate.
     * @param java2DPoint
     *          the point that we're looking at
     * @return
     *          the bin at the given java 2D coordinate or null if we
     *          can't find one
     */
    private Bin getBinAtJava2DCoordinates(Point java2DPoint)
    {
        // convert the point
        GraphCoordinateConverter coordConverter =
            this.getGraphCoordinateConverter();
        Point2D graphPoint = coordConverter.convertJava2DCoordinateToGraphCoordinate(
                java2DPoint);
        
        // find the bin at the right x coordinate and check to see if it was
        // tall enough to be clicked
        Bin bin = this.getBinAtGraphXCoordinate(graphPoint.getX());
        if(bin != null && bin.getContainedIndices().size() >= graphPoint.getY())
        {
            return bin;
        }
        else
        {
            return null;
        }
    }

    /**
     * clear all selections
     */
    private void clearSelections()
    {
        SelectableData selectableGraphData = this.getSelectableGraphData();
        if(selectableGraphData != null)
        {
            selectableGraphData.clearSelections();
        }
    }
    
    /**
     * Select all elements of the given bin
     * @param binToSelect
     *          the bin whose data we should select
     */
    private void selectBin(Bin binToSelect)
    {
        SelectableData selectableGraphData =
            this.getSelectableGraphData();
        if(selectableGraphData != null)
        {
            selectableGraphData.selectAllIndices(
                    binToSelect.getContainedIndices());
        }
    }
    
    /**
     * Toggle the bin's selection status. If it's 100% selected deselect it,
     * otherwise select it
     * @param binToToggle
     *          the bin that we're toggling
     */
    private void toggleBinSelections(Bin binToToggle)
    {
        SelectableData selectableGraphData =
            this.getSelectableGraphData();
        if(selectableGraphData != null)
        {
            SortedSet<Integer> selectedIndices =
                selectableGraphData.getSelectedIndices();
            if(selectedIndices.containsAll(binToToggle.getContainedIndices()))
            {
                // it's already 100% selected... deselect
                selectableGraphData.deselectAllIndices(
                        binToToggle.getContainedIndices());
            }
            else
            {
                // it isn't 100% selected... select it
                selectableGraphData.selectAllIndices(
                        binToToggle.getContainedIndices());
            }
        }
    }
    
    /**
     * Get the bin that the given x coordinate falls in
     * @param graphXCoordinate
     *          the x coordinate to get the bin for
     * @return
     *          the bin
     */
    private synchronized Bin getBinAtGraphXCoordinate(double graphXCoordinate)
    {
        BigDecimal minInclusiveBinPosition =
            this.graphXCoordinateToMinInclusiveBinPosition(
                    BigDecimal.valueOf(graphXCoordinate));
        
        return this.binMap.get(minInclusiveBinPosition);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void renderGraph(Graphics2D graphics2D)
    {
        // grab graphics state
        Color color = graphics2D.getColor();
        
        // render one bin at a time
        for(Bin currBin: this.binMap.values())
        {
            this.renderBin(graphics2D, currBin);
        }
        
        // restore graphics state
        graphics2D.setColor(color);
    }

    /**
     * render the given bin
     * @param graphics2D
     *          the graphics context to render to
     * @param binToRender
     *          the bin to render
     */
    private synchronized void renderBin(
            Graphics2D graphics2D,
            Bin binToRender)
    {
        // render the normal bin followed by the selection bin (if needed)
        this.renderBin(
                graphics2D,
                binToRender,
                binToRender.getContainedIndices().size(),
                this.getFillColor());
        if(binToRender.getSelectionCount() > 0)
        {
            this.renderBin(
                    graphics2D,
                    binToRender,
                    binToRender.getSelectionCount(),
                    this.getSelectionColor());
        }
    }

    /**
     * render the given bin
     * @param graphics2D
     *          the graphics context to render to
     * @param binToRender
     *          the bin to render
     * @param binHeight
     *          the bin's height
     * @param binColor
     *          the bin's fill color
     */
    private synchronized void renderBin(
            Graphics2D graphics2D,
            Bin binToRender,
            double binHeight,
            Color binColor)
    {
        // get graph coordinate space dimensions
        double binLeft = binToRender.getMinInclusivePosition().doubleValue();
        double binRight = binLeft + this.binWidth.doubleValue();
        double binBottom = 0.0;
        double binTop = binBottom + binHeight;
        
        GraphCoordinateConverter coordConverter = this.getGraphCoordinateConverter();
        double graphLeft = coordConverter.getGraphOriginX();
        double graphRight = graphLeft + coordConverter.getGraphWidth();
        double graphBottom = coordConverter.getGraphOriginY();
        double graphTop = graphBottom + coordConverter.getGraphHeight();
        
        // bound the bin's shape
        if(binLeft < graphLeft)
        {
            binLeft = graphLeft;
        }
        
        if(binRight > graphRight)
        {
            binRight = graphRight;
        }
        
        if(binBottom < graphBottom)
        {
            binBottom = graphBottom;
        }
        
        if(binTop > graphTop)
        {
            binTop = graphTop;
        }
        
        // don't draw the bin unless the bound bin is in the graph
        if(binLeft < binRight && binBottom < binTop)
        {
            // create a Java2D rectangle for the bin
            double leftXJava2D = coordConverter.convertGraphXCoordinateToJava2DXCoordinate(
                    binLeft);
            double upperYJava2D = coordConverter.convertGraphYCoordinateToJava2DYCoordinate(
                    binTop);
            double widthJava2D = coordConverter.convertGraphWidthValueToJava2DWidthValue(
                    binRight - binLeft);
            double heightJava2D = coordConverter.convertGraphHeightValueToJava2DHeightValue(
                    binTop - binBottom);
            Rectangle2D.Double binRect = new Rectangle2D.Double(
                    leftXJava2D, upperYJava2D,
                    widthJava2D, heightJava2D);
            
            // render the bin
            graphics2D.setColor(binColor);
            graphics2D.fill(binRect);
            graphics2D.setColor(this.getOutlineColor());
            graphics2D.draw(binRect);
        }
    }

    /**
     * getter for the outline color
     * @return the outlineColor
     */
    public synchronized Color getOutlineColor()
    {
        return this.outlineColor;
    }

    /**
     * setter for the outline color
     * @param outlineColor the outlineColor to set
     */
    public synchronized void setOutlineColor(Color outlineColor)
    {
        this.outlineColor = outlineColor;
    }

    /**
     * @return the fillColor
     */
    public synchronized Color getFillColor()
    {
        return this.fillColor;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public synchronized void setFillColor(Color fillColor)
    {
        this.fillColor = fillColor;
    }

    /**
     * @return the selectionColor
     */
    public Color getSelectionColor()
    {
        return this.selectionColor;
    }

    /**
     * @param selectionColor the selectionColor to set
     */
    public void setSelectionColor(Color selectionColor)
    {
        this.selectionColor = selectionColor;
    }
    
    /**
     * Setter for the bin width
     * @param binWidth
     *          the bin width
     */
    public synchronized void setBinWidth(double binWidth)
    {
        this.setBinWidth(BigDecimal.valueOf(binWidth));
    }

    /**
     * setter for the bin width
     * @param binWidth the binWidth to set
     */
    public synchronized void setBinWidth(BigDecimal binWidth)
    {
        this.binWidth = binWidth;
        this.placeDataInBins();
        this.xAxisDescription.setMajorTickInterval(binWidth.doubleValue());
    }

    /**
     * getter for the bin width
     * @return the binWidth
     */
    public synchronized BigDecimal getBinWidth()
    {
        return this.binWidth;
    }

    /**
     * Getter for the graph data
     * @return the graphData
     */
    public synchronized NamedData<Number> getGraphData()
    {
        return this.graphData;
    }
    
    /**
     * This is a convenience function for casting the graph data to
     * {@link SelectableData} if its possible.
     * @return
     *          the cast graph data or null if it isn't a {@link SelectableData}
     * @see #getGraphData()
     * @see SelectableData
     */
    private SelectableData getSelectableGraphData()
    {
        NamedData<Number> graphData = this.getGraphData();
        if(graphData instanceof SelectableData)
        {
            SelectableData selectableGraphData = (SelectableData)graphData;
            return selectableGraphData;
        }
        else
        {
            return null;
        }
    }

    /**
     * Plot the given data using a default histogram
     * bin generation strategy.
     * @param graphData
     *          the data to plot
     */
    public void setGraphData(
            NamedData<Number> graphData)
    {
        this.setGraphData(
            graphData,
            Histogram.calculateOptimalBinWidth(graphData),
            DEFAULT_HISTOGRAM_BIN_SIGNIFICANT_DIG);
    }
    
    /**
     * Plot the given data using the given data binning
     * parameters.
     * @param graphData
     *          the data to plot
     * @param unroundedBinWidth
     *          the bin width (before being rounded)
     * @param binWidthSignificantDigits
     *          the rounding significance to use. see
     *          {@link org.jax.util.math.NumericUtilities#roundToDecimalPositionBigDecimal(double, int)}
     *          for more information on how this works
     */
    public void setGraphData(
            NamedData<Number> graphData,
            double unroundedBinWidth,
            int binWidthSignificantDigits)
    {
        double optimalBinWidth =
            Histogram.calculateOptimalBinWidth(graphData);
        BigDecimal binWidth =
            NumericUtilities.roundToSignificantDigitsBigDecimal(
                    optimalBinWidth,
                    binWidthSignificantDigits);
        
        this.setGraphData(graphData, binWidth);
    }
    
    /**
     * Plot the given data as histogram data
     * @param graphData
     *          the data for us to plot
     * @param binWidth
     *          the (exact) bin width to use
     */
    public synchronized void setGraphData(
            NamedData<Number> graphData,
            BigDecimal binWidth)
    {
        // remove any pre-existing selection listener
        SelectableData selectableGraphData = this.getSelectableGraphData();
        if(selectableGraphData != null)
        {
            selectableGraphData.removeSelectableDataListener(
                    this.graphDataSelectionListener);
        }
        
        this.binWidth = binWidth;
        this.graphData = graphData;
        
        // register with the new graph data if it's selectable
        selectableGraphData = this.getSelectableGraphData();
        if(selectableGraphData != null)
        {
            selectableGraphData.addSelectableDataListener(
                    this.graphDataSelectionListener);
        }
        
        this.placeDataInBins();
        this.updateGraphDimensions();
    }
    
    /**
     * update the selection count for all of the bins
     * @see Bin#getSelectionCount()
     */
    private void recalculateBinSelections()
    {
        SelectableData selectableGraphData = this.getSelectableGraphData();
        if(selectableGraphData == null)
        {
            // there isn't any selectable data. set the counts to 0
            for(Bin currBin: this.binMap.values())
            {
                currBin.setSelectionCount(0);
            }
        }
        else
        {
            // find out how many selected items fall into each bin and
            // set the counts accordingly
            for(Bin currBin: this.binMap.values())
            {
                Set<Integer> selectedBinContents =
                    new HashSet<Integer>(currBin.getContainedIndices());
                selectedBinContents.retainAll(
                        selectableGraphData.getSelectedIndices());
                currBin.setSelectionCount(
                        selectedBinContents.size());
            }
        }
        
        // refresh the display
        JComponent containerComponent = this.getContainerComponent();
        if(containerComponent != null)
        {
            containerComponent.repaint();
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
    public synchronized void setContainerComponent(JComponent containerComponent)
    {
        // stop listening to old component
        JComponent currContainerComponent = this.getContainerComponent();
        if(currContainerComponent != null)
        {
            currContainerComponent.removeMouseListener(
                    this.containerComponentMouseListener);
        }
        
        super.setContainerComponent(containerComponent);
        
        // start listening to new component
        if(containerComponent != null)
        {
            containerComponent.addMouseListener(
                    this.containerComponentMouseListener);
        }
    }

    /**
     * update the height and width of our coordinate system to
     * accommodate the new data
     */
    private synchronized void updateGraphDimensions()
    {
        // the height is determined by our
        // tallest bar
        int newHeight = this.getMaximumBinContentCount();
        
        // the width is determined by the amount
        // of coordinate space that the bars span
        int numBins = this.getNumberOfBins();
        BigDecimal newWidth = this.binWidth.multiply(BigDecimal.valueOf(numBins));
        
        // the "x origin" is determined by the position of the 1st bar
        BigDecimal xOrigin = this.binMap.firstKey();
        
        // make the update 
        this.getGraphCoordinateConverter().updateGraphDimensions(
                xOrigin.doubleValue(),
                0.0,
                newWidth.doubleValue(),
                newHeight);
        
        // update the axes
        this.xAxisDescription = new RegularIntervalAxisDescription(
                this.getGraphCoordinateConverter(),
                AxisType.X_AXIS,
                this.graphData.getNameOfData(),
                this.binWidth.doubleValue(),
                false);
        this.yAxisDescription = new RegularIntegerIntervalAxisDescription(
                this.getGraphCoordinateConverter(),
                AxisType.Y_AXIS,
                true);
        this.yAxisDescription.setAxisName(DEFAULT_Y_AXIS_NAME);
        this.yAxisDescription.setAxisOrigin(0);
        this.yAxisDescription.setAxisExtent(newHeight);
        this.yAxisDescription.updateMajorTickInterval(
                DEFAULT_NUM_Y_AXIS_MAJOR_TICKS);
    }

    /**
     * Getter for the number of bins we're currently using.
     * @return
     *          the bin count
     */
    private synchronized int getNumberOfBins()
    {
        return this.binMap.size();
    }

    /**
     * Place all of the data in the correct bins.
     */
    private synchronized void placeDataInBins()
    {
        int currIndex = 0;
        this.binMap.clear();
        for(Number currDatum: this.graphData.getData())
        {
            if(currDatum != null)
            {
                // find the bin position that the current datum falls into
                BigDecimal big_binValue =
                    this.graphXCoordinateToMinInclusiveBinPosition(
                            BigDecimal.valueOf(currDatum.doubleValue()));
                
                Bin bin = this.binMap.get(big_binValue);
                if(bin == null)
                {
                    bin = new Bin(big_binValue);
                    this.binMap.put(big_binValue, bin);
                }
                bin.getContainedIndices().add(currIndex);
            }
            
            currIndex++;
        }
        
        // empty bins are currently nulls in our mapping... fill them in with
        // non-null, then recalculate selections
        this.fillInEmptyBins();
        this.recalculateBinSelections();
    }
    
    /**
     * Finds the minimum inclusive position of the bin that the given
     * X coordinate falls in.
     * @param graphXCoordinate
     *          the graph x coordinate
     * @return
     *          the min inclusive bin position that the given coordinate
     *          falls into (this should always be <= the given position)
     */
    private BigDecimal graphXCoordinateToMinInclusiveBinPosition(
            BigDecimal graphXCoordinate)
    {
        // get the bin value
        BigDecimal big_binMultiple;
        if(graphXCoordinate.signum() < 0)
        {
            // it's negative, which requires a little more massaging
            // to keep the properties that we want (eg the min
            // inclusive bin border)
            BigDecimal[] divAndRemainder = graphXCoordinate.divideAndRemainder(
                    this.binWidth);
            if(divAndRemainder[1].signum() == 0)
            {
                big_binMultiple = divAndRemainder[0];
            }
            else
            {
                big_binMultiple = divAndRemainder[0].subtract(BigDecimal.ONE);
            }
        }
        else
        {
            // it's positive
            big_binMultiple = graphXCoordinate.divideToIntegralValue(
                    this.binWidth);
        }
        BigDecimal big_binValue = big_binMultiple.multiply(this.binWidth);
        
        return big_binValue;
    }
    
    /**
     * Fill in empty bins so that they are non-null
     */
    private synchronized void fillInEmptyBins()
    {
        BigDecimal[] existingBinValues = this.binMap.keySet().toArray(
                new BigDecimal[this.binMap.size()]);
        for(int i = 0; i < existingBinValues.length - 1; i++)
        {
            BigDecimal currBinValue = existingBinValues[i];
            BigDecimal nextBinValue = existingBinValues[i + 1];
            
            // while there are still more holes to fill
            BigDecimal currBinHoleValue = currBinValue.add(this.binWidth);
            while(currBinHoleValue.compareTo(nextBinValue) < 0)
            {
                // fill in the hole with an empty set
                this.binMap.put(
                        currBinHoleValue,
                        new Bin(currBinHoleValue));
                
                // "increment" the bin hole
                currBinHoleValue = currBinHoleValue.add(this.binWidth);
            }
        }
    }
    
    /**
     * Gets the maximum count of elements that fall into a
     * single bin.
     * @return
     *          the count
     */
    public synchronized int getMaximumBinContentCount()
    {
        int maxCount = 0;
        
        // search through all of the bins for the max
        for(Bin currBin: this.binMap.values())
        {
            if(currBin.getContainedIndices().size() > maxCount)
            {
                maxCount = currBin.getContainedIndices().size();
            }
        }
        
        return maxCount;
    }
    
    /**
     * @see #calculateOptimalBinWidth(double[])
     * @param dataToBin
     *          the data to bin
     * @return
     *          the optimal bin width
     */
    public static double calculateOptimalBinWidth(NamedData<Number> dataToBin)
    {
        double[] doubleDataToBin = new double[dataToBin.getSize()];
        
        Iterator<Number> dataToBinIter = dataToBin.getData().iterator();
        for(int i = 0; dataToBinIter.hasNext(); i++)
        {
            Number currNumber = dataToBinIter.next();
            if(currNumber != null)
            {
                doubleDataToBin[i] = currNumber.doubleValue();
            }
        }
        
        return calculateOptimalBinWidth(doubleDataToBin);
    }

    /**
     * A formula for finding optimal histogram bin widths
     * as defined by:
     * <pre>
     * Scott, 1979
     *      Scott, D. 1979.
     *      On optimal and data-based histograms.
     *      Biometrika, 66:605-610.
     * </pre>
     * @param dataToBin
     *          the data that we're finding an optimal bin for
     * @return
     *          the optimal bin width
     */
    public static double calculateOptimalBinWidth(double[] dataToBin)
    {
        return 3.49 * Matlab.std(dataToBin) * Math.pow(dataToBin.length, -1.0/3.0);
    }
    
    /**
     * A type for representing histogram bins
     * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
     */
    private static class Bin implements Comparable<Bin>
    {
        /**
         * @see #getMinInclusivePosition()
         */
        private final BigDecimal minInclusivePosition;
        
        /**
         * @see #getContainedIndices()
         */
        private final Set<Integer> containedIndices;

        private int selectionCount;
        
        /**
         * Constructor
         * @param minInclusivePosition
         *          see {@link #getMinInclusivePosition()}
         */
        public Bin(BigDecimal minInclusivePosition)
        {
            this(minInclusivePosition, new HashSet<Integer>());
        }

        /**
         * Constructor
         * @param minInclusivePosition
         *          see {@link #getMinInclusivePosition()}
         * @param containedIndices
         *          see {@link #getContainedIndices()}
         */
        public Bin(BigDecimal minInclusivePosition, Set<Integer> containedIndices)
        {
            this.minInclusivePosition = minInclusivePosition;
            
            this.containedIndices = containedIndices;
        }

        /**
         * Setter for the selection count for this bin.
         * @param selectionCount
         *          the new selection count for this bin
         */
        public void setSelectionCount(int selectionCount)
        {
            this.selectionCount = selectionCount;
        }

        /**
         * Getter for the selection count.
         * @return the selectionCount
         */
        public int getSelectionCount()
        {
            return this.selectionCount;
        }

        /**
         * The minimum (ie left border) position of this
         * bin which is inclusive (the right border is
         * exclusive).
         * @return the minInclusivePosition
         */
        public BigDecimal getMinInclusivePosition()
        {
            return this.minInclusivePosition;
        }

        /**
         * The set of contained indexes
         * @return the containedIndices (mutable)
         */
        public Set<Integer> getContainedIndices()
        {
            return this.containedIndices;
        }

        /**
         * Compare only on the basis of
         * {@link #getMinInclusivePosition()}
         * @param otherBin
         *          the {@link Bin} we're comparing ourselves to
         * @return
         *          see {@link java.lang.Comparable} for the rules
         *          on this
         */
        public int compareTo(Bin otherBin)
        {
            return this.minInclusivePosition.compareTo(otherBin.minInclusivePosition);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object otherBinObj)
        {
            // we can't use BigDecimals equals function... see their
            // javadoc for why
            Bin otherBin = (Bin)otherBinObj;
            return this.compareTo(otherBin) == 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            // we can't just use BigDecimal's hashCode... see their
            // javadoc for why
            return this.minInclusivePosition.unscaledValue().hashCode();
        }
    }

    /**
     * {@inheritDoc}
     */
    public RegularIntervalAxisDescription getXAxisDescription()
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

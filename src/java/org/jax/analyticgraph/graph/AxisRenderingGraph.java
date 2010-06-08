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

package org.jax.analyticgraph.graph;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;

import org.jax.analyticgraph.framework.AbstractGraph2D;
import org.jax.analyticgraph.framework.Graph2D;
import org.jax.analyticgraph.framework.Graph2DWithAxes;
import org.jax.analyticgraph.framework.GraphCoordinateConverter;
import org.jax.analyticgraph.framework.GraphCoordinateConverterContainer;
import org.jax.util.Condition;
import org.jax.util.datastructure.ImmutableReorderedList;

/**
 * This class can be used as a foundation for building most
 * classes that need to render a graph along two axes. It renders
 * the X and Y axes along with decorations such as tick marks,
 * labels etc...
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class AxisRenderingGraph extends AbstractGraph2D
{
    /**
     * This flag is read by the render method to see if we need to update
     * our interior graph's dimensions.
     */
    private boolean interiorDimensionsUpdateNeededFlag = false;
    
    private static final int MINIMUM_AXIS_INSET_PIXELS = 5;
    
    /**
     * this font name comes with {@link java.awt.Font} as of java 6.0,
     * but we need to be java 5.0 compatible
     */
    // TODO for now this is OK, but get rid of this when we move to 6.0
    private static final String SANS_SERIF_FONT_NAME = "SansSerif";
    
    private final PropertyChangeListener interiorGraphPropertyListener = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            AxisRenderingGraph.this.updateInteriorGraphsDimensions();
        }
    };
    
    /**
     * the component listener that we use to detect when the panel
     * has been resized
     */
    private final ComponentListener ownComponentListener = new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                AxisRenderingGraph.this.updateInteriorGraphsDimensions();
            }
        };
    
    /**
     * @see #getInteriorGraph()
     */
    private Graph2DWithAxes interiorGraph;
    
    /**
     * the default buffer to use for the axis label
     */
    private static final double DEFAULT_AXIS_LABEL_BUFFER_PIXELS = 2.0; 
    
    /**
     * acts as a spacer between the axis label and the tick labels
     */
    private double axisLabelBufferPixels = DEFAULT_AXIS_LABEL_BUFFER_PIXELS;
    
    /**
     * the default value to use for the tick label buffer
     */
    private static final double DEFAULT_TICK_LABEL_BUFFER_PIXELS = 3.0;
    
    /**
     * acts as a spacer between the tick labels and tick marks
     */
    private double tickLabelBufferPixels = DEFAULT_TICK_LABEL_BUFFER_PIXELS;
    
    /**
     * Constructor. This graph sets its coordinate system up as a 0-1
     * "unit" coordinate system and should not be changed. Some of the spacing
     * logic depends on this.
     * @param graphCoordinateConverter
     *          the coordinate converter for this graph
     */
    // TODO this dependency on the unit coordinate system should be either
    //     removed, or it should have more robust/fail-fast support in the
    //     API
    public AxisRenderingGraph(GraphCoordinateConverterContainer graphCoordinateConverter)
    {
        super(graphCoordinateConverter);
        
        graphCoordinateConverter.updateGraphDimensions(
                0.0, 0.0, 1.0, 1.0);
    }
    
    private Font getAxisLabelFont(AxisDescription axisDescription)
    {
        return new Font(
                SANS_SERIF_FONT_NAME,
                Font.PLAIN,
                axisDescription.getAxisLabelFontSize());
    }
    
    private Font getTickLabelFont(AxisDescription axisDescription)
    {
        return new Font(
                SANS_SERIF_FONT_NAME,
                Font.PLAIN,
                axisDescription.getTickLabelFontSize());
    }
    
    private Font getGraphTitleLabelFont(Graph2DWithAxes graph)
    {
        return new Font(
                SANS_SERIF_FONT_NAME,
                Font.PLAIN,
                graph.getGraphTitleFontSize());
    }
    
    /**
     * Get the amount of total space taken up by the x axis (labels ticks and all).
     * @param graphics2D
     *          the graphics context that will be used in rendering. we need
     *          this to translate fonts into dimensions
     * @return
     *          the total height in pixel units
     */
    private synchronized double getTotalAxisSpacePixels(
            Graphics2D graphics2D,
            AxisDescription axisDescription,
            List<Tick> ticks)
    {
        // account for the tick marks, tick labels and axis label
        return this.getAxisTickSpacePixels(
                       graphics2D,
                       ticks,
                       axisDescription) +
               this.getAxisLabelSpacePixels(
                       graphics2D,
                       axisDescription);
    }
    
    /**
     * Get the amount of buffer space we need to allocate for the given tick
     * marks
     * @param graphics2D
     *          the graphics context
     * @param ticks
     *          the ticks
     * @param axisDescription
     *          the axis description to use
     * @return
     *          the space needed
     */
    private double getAxisTickSpacePixels(
            Graphics2D graphics2D,
            List<Tick> ticks,
            AxisDescription axisDescription)
    {
        Font font = this.getTickLabelFont(axisDescription);
        Rectangle2D maxCharBounds =
            font.getMaxCharBounds(graphics2D.getFontRenderContext());
        double maxSize = 0.0;
        for(Tick tick: ticks)
        {
            double currMaxSize =
                tick.getSizeInPixles();
            String tickLabel = tick.getLabel();
            if(tickLabel != null && tickLabel.trim().length() > 0)
            {
                currMaxSize +=
                    maxCharBounds.getHeight() + this.tickLabelBufferPixels;
            }
            
            if(currMaxSize > maxSize)
            {
                maxSize = currMaxSize;
            }
        }
        
        return maxSize;
    }

    /**
     * Get the width taken up by the y axis label.
     * @param graphics2D
     *          the graphics context that will be used in rendering. we need
     *          this to translate fonts into dimensions
     * @param axisDescription
     *          the axis description to use
     * @return
     *          the total label width in pixels
     */
    private synchronized double getAxisLabelSpacePixels(
            Graphics2D graphics2D,
            AxisDescription axisDescription)
    {
        final String labelString = axisDescription.getAxisName();
        
        // 1st check to see if we have a y axis label
        if(labelString != null && labelString.trim().length() > 0)
        {
            final Font font = this.getAxisLabelFont(axisDescription);
            Rectangle2D maxCharBounds =
                font.getMaxCharBounds(graphics2D.getFontRenderContext());
            return maxCharBounds.getHeight() + this.axisLabelBufferPixels;
        }
        else
        {
            return 0.0;
        }
    }
    
    /**
     * Determine if the contained graph has a title
     * @return
     *          true if the contained graph has a title
     */
    private synchronized boolean getHaveGraphTitleLabel()
    {
        // see if we have a string that's more than just white space
        if(this.interiorGraph != null)
        {
            String graphTitle = this.interiorGraph.getGraphTitle();
            
            return graphTitle != null && graphTitle.trim().length() > 0;
        }
        else
        {
            return false;
        }
    }

    /**
     * Determines if we should render a label for the y axis.
     * @return
     *          true if we should render a y axis label
     */
    private synchronized boolean getHaveYAxisLabel()
    {
        // see if we have a string that's more than just white space
        if(this.interiorGraph != null)
        {
            AxisDescription yAxisDescription =
                this.interiorGraph.getYAxisDescription();
            
            if(yAxisDescription != null)
            {
                return yAxisDescription.getAxisName() != null &&
                       yAxisDescription.getAxisName().trim().length() > 0;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Converts a string into a shape so that it's easy to transform and render
     * @param labelString
     *          the string to get a shape for
     * @param graphics2D
     *          the graphics context that we will eventually be rendering to
     * @param font
     *          the font to use
     * @return
     *          the shape of the string given the graphics context
     */
    private synchronized Shape stringToShape(
            String labelString,
            Graphics2D graphics2D,
            Font font)
    {
        // get a handle on the font rendering context
        FontRenderContext frc = graphics2D.getFontRenderContext();
        
        // convert the string into a shape
        GlyphVector gv =
            font.createGlyphVector(frc, labelString);
        return gv.getOutline();
    }
    
    /**
     * Determine if we have an X axis label to render.
     * @return
     *          true if there's a label to 
     */
    private synchronized boolean getHaveXAxisLabel()
    {
        // see if we have a string that's more than just white space
        if(this.interiorGraph != null)
        {
            AxisDescription xAxisDescription =
                this.interiorGraph.getXAxisDescription();
            
            if(xAxisDescription != null)
            {
                return xAxisDescription.getAxisName() != null &&
                       xAxisDescription.getAxisName().trim().length() > 0;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Render's this graph and invokes {@link Graph2D#renderGraph(Graphics2D)}
     * on the interior graph ({@link #getInteriorGraph()}).
     * @param graphics2D
     *          the graphics context to render to
     */
    public void renderGraph(Graphics2D graphics2D)
    {
        GraphCoordinateConverter innerGraphCoordinateConverter =
            this.interiorGraph.getGraphCoordinateConverter();
        AxisDescription xAxisDescription =
            this.interiorGraph.getXAxisDescription();
        Font xAxisLabelFont = this.getAxisLabelFont(xAxisDescription);
        Font xAxisTickFont = this.getTickLabelFont(xAxisDescription);
        AxisDescription yAxisDescription =
            this.interiorGraph.getYAxisDescription();
        Font yAxisLabelFont = this.getAxisLabelFont(yAxisDescription);
        Font yAxisTickFont = this.getTickLabelFont(yAxisDescription);
        
        if(xAxisDescription != null && yAxisDescription != null)
        {
            List<Tick> xTicks = xAxisDescription.getTicks();
            List<Tick> yTicks = yAxisDescription.getTicks();
            
            // check to see if we should update our dimensions before rendering
            if(this.interiorDimensionsUpdateNeededFlag)
            {
                this.updateInteriorGraphsDimensions(
                        graphics2D,
                        xTicks,
                        yTicks);
                this.interiorDimensionsUpdateNeededFlag = false;
            }
            
            // render the inner graph
            if(this.interiorGraph != null)
            {
                this.interiorGraph.renderGraph(graphics2D);
            }
            
            // now take care of the axes
            double totalInnerXAxisHeightPixels = this.getTotalAxisSpacePixels(
                    graphics2D,
                    xAxisDescription,
                    xTicks);
            double totalInnerYAxisWidthPixels = this.getTotalAxisSpacePixels(
                    graphics2D,
                    yAxisDescription,
                    yTicks);
            
            GraphCoordinateConverter ourCoordianteConverter =
                this.getGraphCoordinateConverter();
            
            // draw the axis lines
            Rectangle2D innerGraphPixelRectangle = new Rectangle2D.Double(
                    totalInnerYAxisWidthPixels,
                    totalInnerXAxisHeightPixels,
                    ourCoordianteConverter.getAbsoluteWidthInPixels() - (2 * totalInnerYAxisWidthPixels),
                    ourCoordianteConverter.getAbsoluteHeightInPixels() - (2 * totalInnerXAxisHeightPixels));
            graphics2D.draw(innerGraphPixelRectangle);
            
            // get the y pixel location and height of all of our x axis ticks...
            // for that we need our coordinate converter
            double allXTicksStartPixel =
                ourCoordianteConverter.getAbsoluteHeightInPixels() - totalInnerXAxisHeightPixels;
            
            // iterate throught all of the X ticks
            for(Tick currTick: xTicks)
            {
                // ok, get the x pixel location of this tick, for that we
                // need the inner coordinate converter
                final double currTickXPixel =
                    innerGraphCoordinateConverter.convertGraphXCoordinateToJava2DXCoordinate(
                            currTick.getPositionInGraphUnits());
                
                final double currXTickStopPixel;
                
                // reverse the direction of the tick if it is an inside tick
                if(currTick.isOutsideGraphBorder())
                {
                    currXTickStopPixel =
                        allXTicksStartPixel + currTick.getSizeInPixles();
                }
                else
                {
                    currXTickStopPixel =
                        allXTicksStartPixel - currTick.getSizeInPixles();
                }
                
                if(xAxisDescription.getShowTickMarks())
                {
                    // render the tick line
                    Line2D tickLine = new Line2D.Double(
                            currTickXPixel, allXTicksStartPixel,
                            currTickXPixel, currXTickStopPixel);
                    graphics2D.draw(tickLine);
                }
                
                if(xAxisDescription.getShowTickLabels())
                {
                    // render the tick label
                    String currTickLabel = currTick.getLabel();
                    if(currTickLabel != null && currTickLabel.trim().length() > 0)
                    {
                        currTickLabel = currTickLabel.trim();
                        
                        Shape tickLabelShape = this.stringToShape(
                                currTickLabel,
                                graphics2D,
                                xAxisTickFont);
                        Rectangle2D tickLabelBounds = tickLabelShape.getBounds2D();
                        AffineTransform at = new AffineTransform();
                        at.translate(
                                currTickXPixel - (tickLabelBounds.getWidth() / 2.0),
                                currXTickStopPixel + this.tickLabelBufferPixels + tickLabelBounds.getHeight());
                        tickLabelShape = at.createTransformedShape(tickLabelShape);
                        graphics2D.fill(tickLabelShape);
                    }
                }
            }
            
            // get the x pixel location and width of all of our y axis ticks...
            // for that we need our coordinate converter
            double allYTicksStartPixel =
                totalInnerYAxisWidthPixels;
            
            // iterate through all of the y ticks
            for(Tick currTick: yTicks)
            {
                // ok, get the x pixel location of this tick, for that we
                // need the inner coordinate converter
                final double currTickYPixel =
                    innerGraphCoordinateConverter.convertGraphYCoordinateToJava2DYCoordinate(
                            currTick.getPositionInGraphUnits());
                final double currYTickStopPixel;
                
                // reverse the direction of the tick if it is an inside tick
                if(currTick.isOutsideGraphBorder())
                {
                    currYTickStopPixel =
                        allYTicksStartPixel - currTick.getSizeInPixles();
                }
                else
                {
                    currYTickStopPixel =
                        allYTicksStartPixel + currTick.getSizeInPixles();
                }
                
                if(yAxisDescription.getShowTickMarks())
                {
                    // render the tick line
                    Line2D tickLine = new Line2D.Double(
                            allYTicksStartPixel, currTickYPixel,
                            currYTickStopPixel, currTickYPixel);
                    graphics2D.draw(tickLine);
                }
                
                if(yAxisDescription.getShowTickLabels())
                {
                    // render the tick label
                    String currTickLabel = currTick.getLabel();
                    if(currTickLabel != null && currTickLabel.trim().length() > 0)
                    {
                        currTickLabel = currTickLabel.trim();
                        
                        Shape tickLabelShape = this.stringToShape(
                                currTickLabel,
                                graphics2D,
                                yAxisTickFont);
                        Rectangle2D tickLabelBounds = tickLabelShape.getBounds2D();
                        AffineTransform at = new AffineTransform();
                        at.translate(
                                currYTickStopPixel - this.tickLabelBufferPixels,
                                currTickYPixel + (tickLabelBounds.getWidth() / 2.0));
                        at.rotate(Math.PI * 3.0/2.0);
                        tickLabelShape = at.createTransformedShape(tickLabelShape);
                        graphics2D.fill(tickLabelShape);
                    }
                }
            }
            
            // now render the axis labels
            if(this.getHaveXAxisLabel())
            {
                Shape xAxisLabelShape = this.stringToShape(
                        xAxisDescription.getAxisName(),
                        graphics2D,
                        xAxisLabelFont);
                FontMetrics fontMetrics = graphics2D.getFontMetrics(xAxisLabelFont);
                Rectangle2D axisLabelBounds = xAxisLabelShape.getBounds2D();
                AffineTransform at = new AffineTransform();
                at.translate(
                        ourCoordianteConverter.getAbsoluteXOffsetInPixels() -
                        (axisLabelBounds.getWidth() / 2.0) +
                        (ourCoordianteConverter.getAbsoluteWidthInPixels() / 2.0),
                        ourCoordianteConverter.getAbsoluteHeightInPixels() -
                        (fontMetrics.getDescent() + 1));
                xAxisLabelShape = at.createTransformedShape(xAxisLabelShape);
                graphics2D.fill(xAxisLabelShape);
            }
            
            if(this.getHaveYAxisLabel())
            {
                Shape yAxisLabelShape =
                    this.stringToShape(
                            yAxisDescription.getAxisName(),
                            graphics2D,
                            yAxisLabelFont);
                Rectangle2D axisLabelBounds = yAxisLabelShape.getBounds2D();
                AffineTransform at = new AffineTransform();
                at.translate(
                        ourCoordianteConverter.getAbsoluteXOffsetInPixels() +
                        axisLabelBounds.getHeight(),
                        ourCoordianteConverter.getAbsoluteYOffsetInPixels() +
                        (axisLabelBounds.getWidth() / 2.0) +
                        (ourCoordianteConverter.getAbsoluteHeightInPixels() / 2.0));
                at.rotate(Math.PI * 3.0/2.0);
                yAxisLabelShape = at.createTransformedShape(yAxisLabelShape);
                graphics2D.fill(yAxisLabelShape);
            }
            
            if(this.getHaveGraphTitleLabel())
            {
                Shape graphHeaderShape =
                    this.stringToShape(
                            this.interiorGraph.getGraphTitle(),
                            graphics2D,
                            this.getGraphTitleLabelFont(this.interiorGraph));
                Rectangle2D labelBounds = graphHeaderShape.getBounds2D();
                AffineTransform at = new AffineTransform();
                at.translate(
                        ourCoordianteConverter.getAbsoluteXOffsetInPixels() -
                        (labelBounds.getWidth() / 2.0) +
                        (ourCoordianteConverter.getAbsoluteWidthInPixels() / 2.0),
                        innerGraphCoordinateConverter.getAbsoluteYOffsetInPixels() -
                        labelBounds.getHeight());
                graphHeaderShape = at.createTransformedShape(graphHeaderShape);
                graphics2D.fill(graphHeaderShape);
            }
        }
    }
    
    /**
     * Getter for the interior graph (graph inside of the axes).
     * @return the interiorGraph
     */
    public synchronized Graph2DWithAxes getInteriorGraph()
    {
        return this.interiorGraph;
    }

    /**
     * Setter for the interior graph
     * @see #getInteriorGraph()
     * @param interiorGraph the interiorGraph to set
     */
    public synchronized void setInteriorGraph(Graph2DWithAxes interiorGraph)
    {
        GraphCoordinateConverterContainer converterContainer =
            (GraphCoordinateConverterContainer)this.getGraphCoordinateConverter();
        
        if(this.interiorGraph != null)
        {
            converterContainer.removeGraphCoordinateConverter(
                    this.interiorGraph.getGraphCoordinateConverter());
            this.interiorGraph.removePropertyChangeListener(this.interiorGraphPropertyListener);
            this.interiorGraph.getXAxisDescription().removePropertyChangeSupport(
                    this.interiorGraphPropertyListener);
            this.interiorGraph.getYAxisDescription().removePropertyChangeSupport(
                    this.interiorGraphPropertyListener);
        }
        
        this.interiorGraph = interiorGraph;
        this.updateInteriorGraphsDimensions();
        
        if(this.interiorGraph != null)
        {
            converterContainer.addGraphCoordinateConverter(
                    this.interiorGraph.getGraphCoordinateConverter());
            this.interiorGraph.addPropertyChangeListener(this.interiorGraphPropertyListener);
            this.interiorGraph.getXAxisDescription().addPropertyChangeListener(
                    this.interiorGraphPropertyListener);
            this.interiorGraph.getYAxisDescription().addPropertyChangeListener(
                    this.interiorGraphPropertyListener);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setContainerComponent(JComponent containerComponent)
    {
        if(this.getContainerComponent() != null)
        {
            this.getContainerComponent().removeComponentListener(this.ownComponentListener);
        }
        
        super.setContainerComponent(containerComponent);
        
        if(this.getContainerComponent() != null)
        {
            this.getContainerComponent().addComponentListener(this.ownComponentListener);
            this.getInteriorGraph().setContainerComponent(
                    this.getContainerComponent());
        }
        
        this.updateInteriorGraphsDimensions();
    }

    /**
     * Set's {@link #interiorDimensionsUpdateNeededFlag} to true
     * and forces a repaint if we have a container component.
     */
    private synchronized void updateInteriorGraphsDimensions()
    {
        this.interiorDimensionsUpdateNeededFlag = true;
        
        JComponent containComponent = this.getContainerComponent();
        if(containComponent != null)
        {
            containComponent.repaint();
        }
    }
    
    private static final Condition<Tick> outsideTickCondition =
        new Condition<Tick>()
        {
            public boolean test(Tick input)
            {
                return input.isOutsideGraphBorder();
            }
        };
    
    private static final Condition<Tick> insideTickCondition =
        new Condition<Tick>()
        {
            public boolean test(Tick input)
            {
                return !input.isOutsideGraphBorder();
            }
        };
    
    /**
     * This function updates the container-relative and absolute dimensions
     * of the inner graph for this two axis graph.
     * @param graphics2D
     *          the graphics context to use while updating the sizes
     */
    private synchronized void updateInteriorGraphsDimensions(
            Graphics2D graphics2D,
            List<Tick> xAxisTicks,
            List<Tick> yAxisTicks)
    {
        if(this.interiorGraph != null)
        {
            GraphCoordinateConverter coordConverterToUpdate =
                this.interiorGraph.getGraphCoordinateConverter();
            
            // update the container relative and absolute dimensions
            double absHeight = this.getGraphCoordinateConverter().getAbsoluteHeightInPixels();
            double absWidth = this.getGraphCoordinateConverter().getAbsoluteWidthInPixels();
            if(absHeight > 0.0 && absWidth > 0.0)
            {
                List<Tick> insideXTickList = new ImmutableReorderedList<Tick>(
                        xAxisTicks,
                        AxisRenderingGraph.outsideTickCondition);
                List<Tick> outsideXTickList = new ImmutableReorderedList<Tick>(
                        xAxisTicks,
                        AxisRenderingGraph.insideTickCondition);
                List<Tick> insideYTickList = new ImmutableReorderedList<Tick>(
                        yAxisTicks,
                        AxisRenderingGraph.outsideTickCondition);
                List<Tick> outsideYTickList = new ImmutableReorderedList<Tick>(
                        yAxisTicks,
                        AxisRenderingGraph.insideTickCondition);
                
                AxisDescription yAxisDescription =
                    this.interiorGraph.getYAxisDescription();
                AxisDescription xAxisDescription =
                    this.interiorGraph.getXAxisDescription();
                
                double innerContainerRelativeX =
                    (this.getTotalAxisSpacePixels(graphics2D, yAxisDescription, outsideYTickList) +
                    this.getAxisInsetPixels(graphics2D, insideYTickList, yAxisDescription)) /
                    absWidth;
                double innerContainerRelativeY =
                    (this.getTotalAxisSpacePixels(graphics2D, xAxisDescription, outsideXTickList) +
                    this.getAxisInsetPixels(graphics2D, insideXTickList, xAxisDescription)) /
                    absHeight;
                double innerContainerRelativeWidth =
                    1.0 - (innerContainerRelativeX * 2.0);
                double innerContainerRelativeHeight =
                    1.0 - (innerContainerRelativeY * 2.0);
                coordConverterToUpdate.updateContainerRelativeDimensions(
                        innerContainerRelativeX,
                        innerContainerRelativeY,
                        innerContainerRelativeWidth,
                        innerContainerRelativeHeight);
                
                GraphCoordinateConverterContainer.updateDimensionsOfNestedCoordinateConverter(
                        this.getGraphCoordinateConverter(),
                        coordConverterToUpdate);
            }
        }
    }
    
    /**
     * Get the axis inset to use given the parameters
     * @param graphics2D
     *          the graphics context we're rendering to
     * @param innerTicks
     *          the inner tick marks
     * @param axisDescription
     *          the axis description
     * @return
     *          the inset to use in pixels
     */
    private double getAxisInsetPixels(
            Graphics2D graphics2D,
            List<Tick> innerTicks,
            AxisDescription axisDescription)
    {
        double tickBasedInset = 1 + this.getAxisTickSpacePixels(
                graphics2D,
                innerTicks,
                axisDescription);
        
        return Math.max(MINIMUM_AXIS_INSET_PIXELS, tickBasedInset);
    }
}

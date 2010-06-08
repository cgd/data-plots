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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

/**
 * A base class with some implementation for
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public abstract class AbstractGraph2D implements Graph2D
{
    /**
     * @see #getGraphCoordinateConverter()
     */
    private GraphCoordinateConverter graphCoordinateConverter;
    
    /**
     * @see #getContainerComponent()
     */
    private JComponent containerComponent;
    
    private static final Color DEFAULT_SELECTION_RECTANGLE_COLOR =
        Color.RED;
    
    /**
     * @see #getSelectionRectangleColor()
     */
    private Color selectionRectangleColor =
        DEFAULT_SELECTION_RECTANGLE_COLOR;
    
    /**
     * Same as the border color except translucent
     */
    private static final Color DEFAULT_SELECTION_RECTANGLE_FILL_COLOR = new Color(
            DEFAULT_SELECTION_RECTANGLE_COLOR.getRed(),
            DEFAULT_SELECTION_RECTANGLE_COLOR.getGreen(),
            DEFAULT_SELECTION_RECTANGLE_COLOR.getBlue(),
            DEFAULT_SELECTION_RECTANGLE_COLOR.getAlpha() / 16);

    /**
     * @see #getSelectionRectangleFillColor()
     */
    private Color selectionRectangleFillColor =
        DEFAULT_SELECTION_RECTANGLE_FILL_COLOR;
    
    private static final Color DEFAULT_POINT_COLOR = Color.BLUE;
    
    private Color pointColor = DEFAULT_POINT_COLOR;
    
    private static final Color DEFAULT_SELECTED_POINT_COLOR = Color.RED;
    
    private Color selectedPointColor = DEFAULT_SELECTED_POINT_COLOR;
    
    /**
     * The default point width.
     */
    private static final int DEFAULT_POINT_WIDTH = 5;
    
    /**
     * @see #getPointWidth()
     */
    private int pointWidth = DEFAULT_POINT_WIDTH;
    
    /**
     * Constructor
     * @param graphCoordinateConverter
     *          the graph coordinate converter to use for this graph
     */
    public AbstractGraph2D(GraphCoordinateConverter graphCoordinateConverter)
    {
        this.graphCoordinateConverter = graphCoordinateConverter;
    }

    /**
     * {@inheritDoc}
     */
    public GraphCoordinateConverter getGraphCoordinateConverter()
    {
        return this.graphCoordinateConverter;
    }

    /**
     * {@inheritDoc}
     */
    public void setGraphCoordinateConverter(GraphCoordinateConverter graphCoordinateConverter)
    {
        this.graphCoordinateConverter = graphCoordinateConverter;
    }
    
    /**
     * {@inheritDoc}
     */
    public JComponent getContainerComponent()
    {
        return this.containerComponent;
    }

    /**
     * {@inheritDoc}
     */
    public void setContainerComponent(JComponent containerComponent)
    {
        this.containerComponent = containerComponent;
    }
    
    /**
     * Render the selection rectangle
     * @param graphics2D
     *          the graphics context to render to
     * @param selectionRectangle
     *          the selection rectangle to render
     */
    protected synchronized void renderSelectionRectangle(
            Graphics2D graphics2D,
            Rectangle selectionRectangle)
    {
        Rectangle nonNegativeSelectionRectangle =
            AbstractGraph2D.toNonNegativeWidthHeightRectangle(
                    selectionRectangle);
        graphics2D.setColor(this.getSelectionRectangleFillColor());
        graphics2D.fill(nonNegativeSelectionRectangle);
        graphics2D.setColor(this.getSelectionRectangleColor());
        graphics2D.draw(nonNegativeSelectionRectangle);
    }

    /**
     * Convert a rectangle with negative width or height to one with
     * positive width and height
     * @param rectangle
     *          the rectangle that might have negatives
     * @return
     *          the positive version, or the same instance that was passed
     *          in if it's already positive
     */
    protected static Rectangle toNonNegativeWidthHeightRectangle(
            Rectangle rectangle)
    {
        if(rectangle.width < 0 || rectangle.height < 0)
        {
            Rectangle nonNegativeRect = new Rectangle(rectangle);
            if(rectangle.width < 0)
            {
                nonNegativeRect.width = -rectangle.width;
                nonNegativeRect.x = rectangle.x + rectangle.width;
            }
            
            if(rectangle.height < 0)
            {
                nonNegativeRect.height = -rectangle.height;
                nonNegativeRect.y = rectangle.y + rectangle.height;
            }
            
            return nonNegativeRect;
        }
        else
        {
            // the rectangle that we have is OK
            return rectangle;
        }
    }

    /**
     * Getter for the selection rectangle color
     * @return
     *          the selection rectangle color
     */
    public Color getSelectionRectangleColor()
    {
        return this.selectionRectangleColor;
    }

    /**
     * Setter for the selection rectangle color
     * @param selectionRectangleColor
     *          the color to set
     */
    public void setSelectionRectangleColor(Color selectionRectangleColor)
    {
        this.selectionRectangleColor = selectionRectangleColor;
    }

    /**
     * Getter for the selection rectagle's fill color
     * @return
     *          the selection rectangle's fill color
     */
    public Color getSelectionRectangleFillColor()
    {
        return this.selectionRectangleFillColor;
    }

    /**
     * Setter for the selection rectangle's fill color
     * @param selectionRectangleFillColor
     *          the color to set
     */
    public void setSelectionRectangleFillColor(
            Color selectionRectangleFillColor)
    {
        this.selectionRectangleFillColor = selectionRectangleFillColor;
    }

    /**
     * Render a single point in the scatter plot.
     * @param graphics2D
     *          the graphics context to render to
     * @param selected 
     *          indicates whether or not the point should be rendered as
     *          selected
     * @param x
     *          the point's x position
     * @param y
     *          the point's y position
     */
    protected synchronized void renderPoint(
            Graphics2D graphics2D,
            boolean selected,
            double x,
            double y)
    {
        // set the color (selected or not)
        if(selected)
        {
            graphics2D.setColor(this.getSelectedPointColor());
        }
        else
        {
            graphics2D.setColor(this.getPointColor());
        }
        
        this.renderPoint(graphics2D, x, y);
    }
    
    /**
     * Render a single point
     * @param graphics2D
     *          the graphics context to use
     * @param graphX
     *          the graph x location of the point
     * @param graphY
     *          the graph y location of the point
     */
    protected void renderPoint(
            Graphics2D graphics2D,
            double graphX,
            double graphY)
    {
        // don't do anything unless we're in bounds
        if(this.getGraphCoordinateConverter().isGraphPointInBounds(graphX, graphY))
        {
            int pointPixelWidth = this.getPointWidth();
            double halfPointPixelWidth = pointPixelWidth / 2.0;
            
            // convert to pixels
            GraphCoordinateConverter coordConverter =
                this.getGraphCoordinateConverter();
            double pixelX =
                coordConverter.convertGraphXCoordinateToJava2DXCoordinate(graphX) -
                halfPointPixelWidth;
            double pixelY =
                coordConverter.convertGraphYCoordinateToJava2DYCoordinate(graphY) -
                halfPointPixelWidth;
            
            // render!
            graphics2D.fill(new Ellipse2D.Double(
                    pixelX,
                    pixelY,
                    pointPixelWidth,
                    pointPixelWidth));
        }
    }

    /**
     * Getter for the point color
     * @return
     *          the point color to get
     */
    public Color getPointColor()
    {
        return this.pointColor;
    }

    /**
     * Setter for the point color
     * @param pointColor
     *          the color to set
     */
    public void setPointColor(Color pointColor)
    {
        this.pointColor = pointColor;
    }

    /**
     * Getter for the point color
     * @return
     *          the color
     */
    public Color getSelectedPointColor()
    {
        return this.selectedPointColor;
    }

    /**
     * Setter for the color we use for selected points
     * @param selectedPointColor
     *          the color to set
     */
    public void setSelectedPointColor(Color selectedPointColor)
    {
        this.selectedPointColor = selectedPointColor;
    }

    /**
     * Getter for the point width. Point width determines how large the points
     * are in the scatter plot.
     * @return
     *          the point width
     */
    public synchronized int getPointWidth()
    {
        return this.pointWidth;
    }

    /**
     * Setter for the point width.
     * @see #getPointWidth()
     * @param pointWidth
     *          the pointWidth to set
     */
    public synchronized void setPointWidth(int pointWidth)
    {
        this.pointWidth = pointWidth;
    }
}

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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * This is a simple coordinate converter that attaches directly to a
 * GUI component. For better documentation on how the coordinate systems
 * relate to each other, see {@link GraphCoordinateConverter}.
 * 
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class TopLevelGraphCoordinateConverter extends SimpleGraphCoordinateConverter
{
    // TODO when you have javadoc handy make sure you should be
    // doing "get name"
    /**
     * Our logger
     */
    private static final Logger LOG = Logger.getLogger(
            TopLevelGraphCoordinateConverter.class.getName());
    
    /**
     * The component that we're currently attached to (can be null).
     */
    private JComponent component;

    /**
     * Constructor.
     * @param containerRelativeXOffset
     *          see {@link #getContainerRelativeXOffset()}
     * @param containerRelativeYOffset
     *          see {@link #getContainerRelativeYOffset()}
     * @param containerRelativeWidth
     *          see {@link #getContainerRelativeWidth()}
     * @param containerRelativeHeight
     *          see {@link #getContainerRelativeHeight()}
     */
    public TopLevelGraphCoordinateConverter(
            final double containerRelativeXOffset,
            final double containerRelativeYOffset,
            final double containerRelativeWidth,
            final double containerRelativeHeight)
    {
        super(
                containerRelativeXOffset,
                containerRelativeYOffset,
                containerRelativeWidth,
                containerRelativeHeight);
    }
    
    /**
     * Attach this coordinate converter to the given component.
     * @param component
     *          the component that we're attaching this coordinate system
     *          to. When the dimensions of this component change, that
     *          will be reflected in how we convert between coordinate
     *          systems.
     */
    public void attachToComponent(JComponent component)
    {
        this.component = component;
        
        // register with the component so that we receive any
        // update events
        component.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent componentEvent)
            {
                TopLevelGraphCoordinateConverter.this.componentDimensionsUpdated(
                        componentEvent.getComponent().getWidth(),
                        componentEvent.getComponent().getHeight());
            }
        });
        
        // update any nested coordinate converters that we do have
        this.componentDimensionsUpdated();
    }
    
    /**
     * The component dimensions have been updated, so we should change
     * how we convert components.
     */
    private void componentDimensionsUpdated()
    {
        // lot's of stuff here just for swing thread safety...
        // all we really want to do is update the coordinates
        try
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    TopLevelGraphCoordinateConverter.this.componentDimensionsUpdated(
                            TopLevelGraphCoordinateConverter.this.component.getWidth(),
                            TopLevelGraphCoordinateConverter.this.component.getHeight());
                }
            });
        }
        catch(Exception ex)
        {
            LOG.log(Level.SEVERE,
                    "Failed to resize graph coordinates due to unexpected exception:",
                    ex);
        }
    }

    /**
     * The component dimensions have been updated, so we should change
     * how we convert components.
     * @param updatedComponentWidth
     *          the new value for the component's width
     * @param updatedComponentHeight
     *          the new value for the component's height
     */
    private void componentDimensionsUpdated(int updatedComponentWidth, int updatedComponentHeight)
    {
        // we need special treatment for the y axis because of the difference
        // between the upper and lower origin
        double absoluteHeight = this.getContainerRelativeHeight() * updatedComponentHeight;
        double absoluteYOffset =
            updatedComponentHeight -
            (absoluteHeight + this.getContainerRelativeYOffset() * updatedComponentHeight); 
        
        this.updateAbsoluteDimensions(
                this.getContainerRelativeXOffset() * updatedComponentWidth,
                absoluteYOffset,
                this.getContainerRelativeWidth() * updatedComponentWidth,
                absoluteHeight);
    }
}

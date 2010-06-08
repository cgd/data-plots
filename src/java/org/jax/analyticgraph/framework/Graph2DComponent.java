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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 * A component for displaying one or more graphs.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class Graph2DComponent extends JPanel implements Graph2DContainer
{
    /**
	 * every {@link java.io.Serializable} is supposed to have one
	 * of these
	 */
	private static final long serialVersionUID = -6221792370827750576L;

	/**
     * A list of all of the graphs that this component contains
     */
    private final List<Graph2D> allGraphs = 
        Collections.synchronizedList(new ArrayList<Graph2D>());
    
    /**
     * The coordinate converter that connects the windowing system to our
     * graphing coordinates.
     */
    private TopLevelGraphCoordinateConverter topLevelCoordinateConverter;

    /**
     * Our logger
     */
    private static final Logger LOG = Logger.getLogger(
            Graph2DComponent.class.getName());
    
    /**
     * Constructor
     */
    public Graph2DComponent()
    {
        this.setLayout(null);
        this.setPreferredSize(new Dimension(500, 500));
        this.setBackground(Color.WHITE);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addGraph2D(
            final Graph2D graph2D)
    {
        // initialize the top level coordinate converter if it hasn't yet
        // been initialized. we're doing this here because
        // it's bad practice to pass a "this" reference out of an object
        // during construction
        synchronized(this)
        {
            if(this.topLevelCoordinateConverter == null)
            {
                this.topLevelCoordinateConverter = new TopLevelGraphCoordinateConverter(
                        0.0, 0.0,
                        1.0, 1.0);
                this.topLevelCoordinateConverter.attachToComponent(this);
            }
        }
        
        // add the graph's coordinate converter to the top level coordinate
        // converter
        this.topLevelCoordinateConverter.addGraphCoordinateConverter(
                graph2D.getGraphCoordinateConverter());
        graph2D.setContainerComponent(this);
        
        this.allGraphs.add(graph2D);
    }

    /**
     * {@inheritDoc}
     */
    public void removeGraph2D(final Graph2D graph2D)
    {
        synchronized(this.allGraphs)
        {
            int indexOfGraphToRemove = this.allGraphs.indexOf(graph2D);
            
            if(indexOfGraphToRemove >= 0)
            {
                Graph2D removedGraph2D = this.allGraphs.remove(
                        indexOfGraphToRemove);
                removedGraph2D.setContainerComponent(null);
            }
        }
    }
    
    /**
     * Paint this component.
     * @param graphics
     *          the graphics context to paint to. we expect this to
     *          be of type {@link java.awt.Graphics2D}.
     */
    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        
        if(graphics instanceof Graphics2D)
        {
            Graphics2D graphics2D = (Graphics2D)graphics;
            
            // TODO we probably need to handle the hints at an application
            //      configuration level
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            
            // create a local copy so that we don't have to
            // synchronize through the real processing
            Graph2D[] allGraphsCopy;
            synchronized(this.allGraphs)
            {
                allGraphsCopy = new Graph2D[this.allGraphs.size()];
                allGraphsCopy = this.allGraphs.toArray(allGraphsCopy);
            }
            
            // now paint any sub-graphs
            for(Graph2D currGraph: allGraphsCopy)
            {
                currGraph.renderGraph(graphics2D);
            }
        }
        else
        {
            LOG.severe(
                    "graphics object was expected to be of type [" + Graphics2D.class.getName() +
                    "], but the actual type was [" + graphics.getClass().getName() + "]");
        }
    }
}

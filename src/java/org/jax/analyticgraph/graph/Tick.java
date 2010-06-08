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

/**
 * A tick on an graph axis
 * @see org.jax.analyticgraph.graph.AxisDescription
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class Tick
{
    /**
     * the default size of the major tick marks
     */
    public static final int DEFAULT_MAJOR_TICK_SIZE_PIXELS = 10;
    
    /**
     * @see #getPositionInGraphUnits()
     */
    private final double positionInGraphUnits;
    
    /**
     * @see #getLabel()
     */
    private final String label;
    
    /**
     * @see #isOutsideGraphBorder()
     */
    private final boolean outsideGraphBorder;
    
    /**
     * @see #isRenderedOnOppositeBorder()
     */
    private final boolean renderedOnOppositeBorder;
    
    /**
     * @see #getSizeInPixles()
     */
    private final int sizeInPixles;
    
    /**
     * This constructor invokes
     * {@link #Tick(double, int, String, boolean, boolean)} with
     * outsideGraphBorder set to true and renderedOnOppositeBorder set to false.
     * @param positionInGraphUnits
     *          see {@link #getPositionInGraphUnits()}
     * @param sizeInPixles
     *          see {@link #getSizeInPixles()}
     * @param label
     *          see {@link #getLabel()}
     */
    public Tick(double positionInGraphUnits, int sizeInPixles, String label)
    {
        this(positionInGraphUnits, sizeInPixles, label, true, false);
    }
    
    
    
    /**
     * Constructor
     * @param positionInGraphUnits
     *          see {@link #getPositionInGraphUnits()}
     * @param sizeInPixles
     *          see {@link #getSizeInPixles()}
     * @param label
     *          see {@link #getLabel()}
     * @param outsideGraphBorder
     *          see {@link #isOutsideGraphBorder()}
     * @param renderedOnOppositeBorder
     *          see {@link #isRenderedOnOppositeBorder()}
     */
    public Tick(
            double positionInGraphUnits,
            int sizeInPixles,
            String label,
            boolean outsideGraphBorder,
            boolean renderedOnOppositeBorder)
    {
        this.positionInGraphUnits = positionInGraphUnits;
        this.sizeInPixles = sizeInPixles;
        this.label = label;
        this.outsideGraphBorder = outsideGraphBorder;
        this.renderedOnOppositeBorder = renderedOnOppositeBorder;
    }

    /**
     * Getter for the size of this tick in pixles
     * @return
     *          the size in pixles
     */
    public int getSizeInPixles()
    {
        return this.sizeInPixles;
    }

    /**
     * Getter for this tick's positionInGraphUnits in graph space
     * @return the positionInGraphUnits
     */
    public double getPositionInGraphUnits()
    {
        return this.positionInGraphUnits;
    }
    
    /**
     * Getter for this tick's label (null means no label)
     * @return the label
     */
    public String getLabel()
    {
        return this.label;
    }
    
    /**
     * Getter for determining if this tick should be rendered inside or
     * outside of the graphs border
     * @return the outsideGraphBorder
     */
    public boolean isOutsideGraphBorder()
    {
        return this.outsideGraphBorder;
    }
    
    /**
     * Getter for determining if this tick should be rendered on the
     * opposite border or not. For the X axis, the opposite graph border is the
     * one on top and for the Y axis it's the one on the right side.
     * @return
     *          true iff we should render on the border that is opposite the
     *          normal border
     */
    public boolean isRenderedOnOppositeBorder()
    {
        return this.renderedOnOppositeBorder;
    }
}

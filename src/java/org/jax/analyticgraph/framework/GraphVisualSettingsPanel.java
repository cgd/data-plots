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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jax.analyticgraph.graph.AxisDescription;
import org.jax.analyticgraph.graph.RegularIntegerIntervalAxisDescription;
import org.jax.analyticgraph.graph.RegularIntervalAxisDescription;
import org.jax.analyticgraph.graph.histogram.Histogram;

/**
 * A panel for editing the visual settings of a graph.
 * @author <A HREF="mailto:keith.sheppard@jax.org">Keith Sheppard</A>
 */
public class GraphVisualSettingsPanel extends javax.swing.JPanel
{
    /**
     * every {@link java.io.Serializable} should have one of these
     */
    private static final long serialVersionUID = 1457262190704139854L;
    
    private final Graph2DWithAxes graph2DWithAxes;
    
    private final SpinnerNumberModel graphTitleFontSizeSpinnerNumberModel =
        new SpinnerNumberModel(
                6,
                6,
                40,
                1);
    
    private final SpinnerNumberModel xAxisFontSizeSpinnerNumberModel =
        new SpinnerNumberModel(
                6,
                6,
                40,
                1);
    
    private final SpinnerNumberModel xTickFontSizeSpinnerNumberModel =
        new SpinnerNumberModel(
                6,
                6,
                40,
                1);
    
    private final SpinnerNumberModel yAxisFontSizeSpinnerNumberModel =
        new SpinnerNumberModel(
                6,
                6,
                40,
                1);
    
    private final SpinnerNumberModel yTickFontSizeSpinnerNumberModel =
        new SpinnerNumberModel(
                6,
                6,
                40,
                1);
    
    private final SpinnerNumberModel xAxisOriginSpinnerNumberModel =
        new SpinnerNumberModel(
                0.0,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                1.0);
    
    private final SpinnerNumberModel xAxisExtentSpinnerNumberModel =
        new SpinnerNumberModel(
                1.0,
                0.0,
                Double.POSITIVE_INFINITY,
                1.0);
    
    private final SpinnerNumberModel yAxisOriginSpinnerNumberModel =
        new SpinnerNumberModel(
                0.0,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                1.0);
    
    private final SpinnerNumberModel yAxisExtentSpinnerNumberModel =
        new SpinnerNumberModel(
                1.0,
                0.0,
                Double.POSITIVE_INFINITY,
                1.0);
    
    private final SpinnerNumberModel histogramBinSizeSpinnerNumberModel =
        new SpinnerNumberModel(
                1.0,
                0.0,
                Double.POSITIVE_INFINITY,
                1.0);
    
    /**
     * a little helper class for responding to an item change for showing
     * tick marks
     */
    private class ShowTickMarksChangeListener implements ItemListener
    {
        private final AxisDescription axisDescription;
        
        /**
         * Create a listener for the given axis
         * @param axisDescription
         *          the axis
         */
        public ShowTickMarksChangeListener(AxisDescription axisDescription)
        {
            this.axisDescription = axisDescription;
        }
        
        /**
         * {@inheritDoc}
         */
        public void itemStateChanged(ItemEvent e)
        {
            this.axisDescription.setShowTickMarks(
                    e.getStateChange() == ItemEvent.SELECTED);
        }
    }
    
    /**
     * a little helper class for responding to an item change for showing
     * tick labels
     */
    private class ShowTickLabelChangeListener implements ItemListener
    {
        private final AxisDescription axisDescription;
        
        /**
         * Create a listener for the given axis
         * @param axisDescription
         *          the axis
         */
        public ShowTickLabelChangeListener(AxisDescription axisDescription)
        {
            this.axisDescription = axisDescription;
        }
        
        /**
         * {@inheritDoc}
         */
        public void itemStateChanged(ItemEvent e)
        {
            this.axisDescription.setShowTickLabels(
                    e.getStateChange() == ItemEvent.SELECTED);
        }
    }
    
    private class AxisFontSizeChangeListener implements ChangeListener
    {
        private final AxisDescription axisDescription;
        
        /**
         * Constructor
         * @param axisDescription
         *          the axis
         */
        public AxisFontSizeChangeListener(AxisDescription axisDescription)
        {
            this.axisDescription = axisDescription;
        }

        /**
         * {@inheritDoc}
         */
        public void stateChanged(ChangeEvent e)
        {
            SpinnerNumberModel model = (SpinnerNumberModel)e.getSource();
            this.axisDescription.setAxisLabelFontSize(
                    model.getNumber().intValue());
        }
    }
    
    private class AxisTickFontSizeChangeListener implements ChangeListener
    {
        private final AxisDescription axisDescription;
        
        /**
         * Constructor
         * @param axisDescription
         *          the axis
         */
        public AxisTickFontSizeChangeListener(AxisDescription axisDescription)
        {
            this.axisDescription = axisDescription;
        }

        /**
         * {@inheritDoc}
         */
        public void stateChanged(ChangeEvent e)
        {
            SpinnerNumberModel model = (SpinnerNumberModel)e.getSource();
            this.axisDescription.setTickLabelFontSize(
                    model.getNumber().intValue());
        }
    }
    
    private class AxisTickRealIntervalChangeListener implements ChangeListener
    {
        private final RegularIntervalAxisDescription axisDescription;
        
        /**
         * Constructor
         * @param axisDescription
         *          the axis
         */
        public AxisTickRealIntervalChangeListener(RegularIntervalAxisDescription axisDescription)
        {
            this.axisDescription = axisDescription;
        }

        /**
         * {@inheritDoc}
         */
        public void stateChanged(ChangeEvent e)
        {
            SpinnerNumberModel model = (SpinnerNumberModel)e.getSource();
            this.axisDescription.setMajorTickInterval(
                    model.getNumber().doubleValue());
        }
    }
    
    private class AxisTickIntegerIntervalChangeListener implements ChangeListener
    {
        private final RegularIntegerIntervalAxisDescription axisDescription;
        
        /**
         * Constructor
         * @param axisDescription
         *          the axis
         */
        public AxisTickIntegerIntervalChangeListener(
                RegularIntegerIntervalAxisDescription axisDescription)
        {
            this.axisDescription = axisDescription;
        }
        
        /**
         * {@inheritDoc}
         */
        public void stateChanged(ChangeEvent e)
        {
            SpinnerNumberModel model = (SpinnerNumberModel)e.getSource();
            this.axisDescription.setMajorTickInterval(
                    model.getNumber().intValue());
        }
    }
    
    /**
     * Constructor
     * @param graph2DWithAxes
     *          the graph w/ axes that we're editing
     */
    public GraphVisualSettingsPanel(Graph2DWithAxes graph2DWithAxes)
    {
        this.graph2DWithAxes = graph2DWithAxes;
        
        this.initComponents();
        
        final AxisDescription xAxisDescription =
            this.graph2DWithAxes.getXAxisDescription();
        final AxisDescription yAxisDescription =
            this.graph2DWithAxes.getYAxisDescription();
        
        String graphTitle = this.graph2DWithAxes.getGraphTitle();
        String graphXLabel = this.graph2DWithAxes.getXAxisDescription().getAxisName();
        String graphYLabel = this.graph2DWithAxes.getYAxisDescription().getAxisName();
        
        if(graphTitle != null)
        {
            this.graphTitleTextField.setText(graphTitle);
        }
        if(graphXLabel != null)
        {
            this.xAxisTextField.setText(graphXLabel);
        }
        if(graphYLabel != null)
        {
            this.yAxisTextField.setText(graphYLabel);
        }
        
        this.showXTickLabelsCheckBox.setSelected(
                xAxisDescription.getShowTickLabels());
        this.showXTickLabelsCheckBox.addItemListener(
                new ShowTickLabelChangeListener(xAxisDescription));
        this.showXTicksCheckBox.setSelected(
                xAxisDescription.getShowTickMarks());
        this.showXTicksCheckBox.addItemListener(
                new ShowTickMarksChangeListener(xAxisDescription));
        this.showYTickLabelsCheckBox.setSelected(
                yAxisDescription.getShowTickLabels());
        this.showYTickLabelsCheckBox.addItemListener(
                new ShowTickLabelChangeListener(yAxisDescription));
        this.showYTicksCheckBox.setSelected(
                yAxisDescription.getShowTickMarks());
        this.showYTicksCheckBox.addItemListener(
                new ShowTickMarksChangeListener(yAxisDescription));
        
        this.graphTitleFontSizeSpinnerNumberModel.setValue(
                this.graph2DWithAxes.getGraphTitleFontSize());
        this.xAxisOriginSpinnerNumberModel.setValue(
                this.graph2DWithAxes.getXAxisDescription().getAxisOrigin());
        this.xAxisExtentSpinnerNumberModel.setValue(
                this.graph2DWithAxes.getXAxisDescription().getAxisExtent());
        this.xAxisFontSizeSpinnerNumberModel.setValue(
                xAxisDescription.getAxisLabelFontSize());
        this.xTickFontSizeSpinnerNumberModel.setValue(
                xAxisDescription.getTickLabelFontSize());
        this.yAxisOriginSpinnerNumberModel.setValue(
                this.graph2DWithAxes.getYAxisDescription().getAxisOrigin());
        this.yAxisExtentSpinnerNumberModel.setValue(
                this.graph2DWithAxes.getYAxisDescription().getAxisExtent());
        this.yAxisFontSizeSpinnerNumberModel.setValue(
                yAxisDescription.getAxisLabelFontSize());
        this.yTickFontSizeSpinnerNumberModel.setValue(
                yAxisDescription.getTickLabelFontSize());
        
        this.graphTitleFontSpinner.setModel(
                this.graphTitleFontSizeSpinnerNumberModel);
        this.xAxisOriginSpinner.setModel(
                this.xAxisOriginSpinnerNumberModel);
        this.xAxisExtentSpinner.setModel(
                this.xAxisExtentSpinnerNumberModel);
        this.xAxisFontSpinner.setModel(
                this.xAxisFontSizeSpinnerNumberModel);
        this.xTickFontSizeSpinner.setModel(
                this.xTickFontSizeSpinnerNumberModel);
        this.yAxisOriginSpinner.setModel(
                this.yAxisOriginSpinnerNumberModel);
        this.yAxisExtentSpinner.setModel(
                this.yAxisExtentSpinnerNumberModel);
        this.yAxisExtentSpinner.setModel(
                this.yAxisExtentSpinnerNumberModel);
        this.yAxisFontSpinner.setModel(
                this.yAxisFontSizeSpinnerNumberModel);
        this.yTickFontSizeSpinner.setModel(
                this.yTickFontSizeSpinnerNumberModel);
        this.histogramBinSizeSpinner.setModel(
                this.histogramBinSizeSpinnerNumberModel);
        
        if(graph2DWithAxes instanceof Histogram)
        {
            this.histogramBinSizeSpinnerNumberModel.setValue(
                    ((Histogram)graph2DWithAxes).getBinWidth().doubleValue());
            
            this.xAxisTickIntervalLabel.setVisible(false);
            this.xAxisTickIntervalSpinner.setVisible(false);
        }
        else
        {
            this.histogramBinSizeLabel.setVisible(false);
            this.histogramBinSizeSpinner.setVisible(false);
            
            if(!this.initializeTickIntervalEditing(
                    xAxisDescription,
                    this.xAxisTickIntervalSpinner))
            {
                this.xAxisTickIntervalLabel.setVisible(false);
                this.xAxisTickIntervalSpinner.setVisible(false);
            }
        }
        
        if(!this.initializeTickIntervalEditing(
                yAxisDescription,
                this.yAxisTickIntervalSpinner))
        {
            this.yAxisTickIntervalLabel.setVisible(false);
            this.yAxisTickIntervalSpinner.setVisible(false);
        }
        
        final boolean allowXAxisTranslation =
            this.graph2DWithAxes.getXAxisDescription().getAllowAxisTranslation();
        final boolean allowXAxisScaling =
            this.graph2DWithAxes.getXAxisDescription().getAllowAxisScaling();
        final boolean allowYAxisTranslation =
            this.graph2DWithAxes.getYAxisDescription().getAllowAxisTranslation();
        final boolean allowYAxisScaling =
            this.graph2DWithAxes.getYAxisDescription().getAllowAxisScaling();
        
        if(!allowXAxisTranslation)
        {
            this.xAxisOriginLabel.setVisible(false);
            this.xAxisOriginSpinner.setVisible(false);
        }
        
        if(!allowXAxisScaling)
        {
            this.xAxisExtentLabel.setVisible(false);
            this.xAxisExtentSpinner.setVisible(false);
        }
        
        if(!allowYAxisTranslation)
        {
            this.yAxisOriginLabel.setVisible(false);
            this.yAxisOriginSpinner.setVisible(false);
        }
        
        if(!allowYAxisScaling)
        {
            this.yAxisExtentLabel.setVisible(false);
            this.yAxisExtentSpinner.setVisible(false);
        }
        
        this.xAxisFontSizeSpinnerNumberModel.addChangeListener(
                new AxisFontSizeChangeListener(xAxisDescription));
        this.xTickFontSizeSpinnerNumberModel.addChangeListener(
                new AxisTickFontSizeChangeListener(xAxisDescription));
        this.yAxisFontSizeSpinnerNumberModel.addChangeListener(
                new AxisFontSizeChangeListener(yAxisDescription));
        this.yTickFontSizeSpinnerNumberModel.addChangeListener(
                new AxisTickFontSizeChangeListener(yAxisDescription));
        
        this.graphTitleFontSizeSpinnerNumberModel.addChangeListener(new ChangeListener()
        {
            /**
             * {@inheritDoc}
             */
            public void stateChanged(ChangeEvent e)
            {
                GraphVisualSettingsPanel.this.graphTitleFontSizeChanged();
            }
        });
        
        this.xAxisOriginSpinnerNumberModel.addChangeListener(new ChangeListener()
        {
            /**
             * {@inheritDoc}
             */
            public void stateChanged(ChangeEvent e)
            {
                GraphVisualSettingsPanel.this.xAxisOriginChanged();
            }
        });
        
        this.xAxisExtentSpinnerNumberModel.addChangeListener(new ChangeListener()
        {
            /**
             * {@inheritDoc}
             */
            public void stateChanged(ChangeEvent e)
            {
                GraphVisualSettingsPanel.this.xAxisExtentChanged();
            }
        });
        
        this.yAxisOriginSpinnerNumberModel.addChangeListener(new ChangeListener()
        {
            /**
             * {@inheritDoc}
             */
            public void stateChanged(ChangeEvent e)
            {
                GraphVisualSettingsPanel.this.yAxisOriginChanged();
            }
        });
        
        this.yAxisExtentSpinnerNumberModel.addChangeListener(new ChangeListener()
        {
            /**
             * {@inheritDoc}
             */
            public void stateChanged(ChangeEvent e)
            {
                GraphVisualSettingsPanel.this.yAxisExtentChanged();
            }
        });
        
        this.histogramBinSizeSpinnerNumberModel.addChangeListener(new ChangeListener()
        {
            /**
             * {@inheritDoc}
             */
            public void stateChanged(ChangeEvent e)
            {
                GraphVisualSettingsPanel.this.histogramBinSizeChanged();
            }
        });
        
        this.graphTitleTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.graphTitleChanged();
            }

            public void insertUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.graphTitleChanged();
            }

            public void removeUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.graphTitleChanged();
            }
        });
        
        this.xAxisTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.xAxisLabelChanged();
            }

            public void insertUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.xAxisLabelChanged();
            }

            public void removeUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.xAxisLabelChanged();
            }
        });
        
        this.yAxisTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.yAxisLabelChanged();
            }

            public void insertUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.yAxisLabelChanged();
            }

            public void removeUpdate(DocumentEvent e)
            {
                GraphVisualSettingsPanel.this.yAxisLabelChanged();
            }
        });
    }
    
    private boolean initializeTickIntervalEditing(
            AxisDescription axisDescription,
            JSpinner axisTickIntervalSpinner)
    {
        if(axisDescription instanceof RegularIntervalAxisDescription)
        {
            RegularIntervalAxisDescription realTickIntervalAxis =
                (RegularIntervalAxisDescription)axisDescription;
            if(realTickIntervalAxis.getAllowTickIntervalModifications())
            {
                SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(
                        realTickIntervalAxis.getMajorTickInterval(),
                        0.0,
                        Double.POSITIVE_INFINITY,
                        1.0);
                axisTickIntervalSpinner.setModel(spinnerNumberModel);
                spinnerNumberModel.addChangeListener(
                        new AxisTickRealIntervalChangeListener(
                                realTickIntervalAxis));
                return true;
            }
            else
            {
                return false;
            }
        }
        else if(axisDescription instanceof RegularIntegerIntervalAxisDescription)
        {
            RegularIntegerIntervalAxisDescription intTickIntervalAxis =
                (RegularIntegerIntervalAxisDescription)axisDescription;
            if(intTickIntervalAxis.getAllowTickIntervalModifications())
            {
                SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(
                        intTickIntervalAxis.getMajorTickInterval(),
                        1,
                        Integer.MAX_VALUE,
                        1);
                axisTickIntervalSpinner.setModel(spinnerNumberModel);
                spinnerNumberModel.addChangeListener(
                        new AxisTickIntegerIntervalChangeListener(
                                intTickIntervalAxis));
                return true;
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
     * 
     */
    private void graphTitleFontSizeChanged()
    {
        this.graph2DWithAxes.setGraphTitleFontSize(
                this.graphTitleFontSizeSpinnerNumberModel.getNumber().intValue());
    }

    /**
     * 
     */
    private void histogramBinSizeChanged()
    {
        ((Histogram)this.graph2DWithAxes).setBinWidth(
                this.histogramBinSizeSpinnerNumberModel.getNumber().doubleValue());
    }

    /**
     * 
     */
    private void yAxisExtentChanged()
    {
        this.graph2DWithAxes.getYAxisDescription().setAxisExtent(
                this.yAxisExtentSpinnerNumberModel.getNumber().doubleValue());
    }

    /**
     * 
     */
    private void yAxisOriginChanged()
    {
        this.graph2DWithAxes.getYAxisDescription().setAxisOrigin(
                this.yAxisOriginSpinnerNumberModel.getNumber().doubleValue());
    }

    /**
     * 
     */
    private void xAxisExtentChanged()
    {
        this.graph2DWithAxes.getXAxisDescription().setAxisExtent(
                this.xAxisExtentSpinnerNumberModel.getNumber().doubleValue());
    }

    /**
     * 
     */
    private void xAxisOriginChanged()
    {
        this.graph2DWithAxes.getXAxisDescription().setAxisOrigin(
                this.xAxisOriginSpinnerNumberModel.getNumber().doubleValue());
    }

    /**
     * notification that the y axis label changed
     */
    private void yAxisLabelChanged()
    {
        this.graph2DWithAxes.getYAxisDescription().setAxisName(
                this.yAxisTextField.getText());
    }

    /**
     * notification that the x axis label changed
     */
    private void xAxisLabelChanged()
    {
        this.graph2DWithAxes.getXAxisDescription().setAxisName(
                this.xAxisTextField.getText());
    }

    /**
     * notification that the graph title changed
     */
    private void graphTitleChanged()
    {
        this.graph2DWithAxes.setGraphTitle(
                this.graphTitleTextField.getText());
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("all")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        graphTitleLabel = new javax.swing.JLabel();
        graphTitleTextField = new javax.swing.JTextField();
        graphTitleFontLabel = new javax.swing.JLabel();
        graphTitleFontSpinner = new javax.swing.JSpinner();
        xAxisLabel = new javax.swing.JLabel();
        xAxisTextField = new javax.swing.JTextField();
        xAxisFontLabel = new javax.swing.JLabel();
        xAxisFontSpinner = new javax.swing.JSpinner();
        xAxisOriginLabel = new javax.swing.JLabel();
        xAxisOriginSpinner = new javax.swing.JSpinner();
        xAxisExtentLabel = new javax.swing.JLabel();
        xAxisExtentSpinner = new javax.swing.JSpinner();
        xAxisTickIntervalLabel = new javax.swing.JLabel();
        xAxisTickIntervalSpinner = new javax.swing.JSpinner();
        showXTicksCheckBox = new javax.swing.JCheckBox();
        showXTickLabelsCheckBox = new javax.swing.JCheckBox();
        xTickFontSizeLabel = new javax.swing.JLabel();
        xTickFontSizeSpinner = new javax.swing.JSpinner();
        yAxisLabel = new javax.swing.JLabel();
        yAxisTextField = new javax.swing.JTextField();
        yAxisFontLabel = new javax.swing.JLabel();
        yAxisFontSpinner = new javax.swing.JSpinner();
        yAxisOriginLabel = new javax.swing.JLabel();
        yAxisOriginSpinner = new javax.swing.JSpinner();
        yAxisExtentLabel = new javax.swing.JLabel();
        yAxisExtentSpinner = new javax.swing.JSpinner();
        yAxisTickIntervalLabel = new javax.swing.JLabel();
        yAxisTickIntervalSpinner = new javax.swing.JSpinner();
        showYTicksCheckBox = new javax.swing.JCheckBox();
        showYTickLabelsCheckBox = new javax.swing.JCheckBox();
        yTickFontSizeLabel = new javax.swing.JLabel();
        yTickFontSizeSpinner = new javax.swing.JSpinner();
        histogramBinSizeLabel = new javax.swing.JLabel();
        histogramBinSizeSpinner = new javax.swing.JSpinner();

        setPreferredSize(new java.awt.Dimension(600, 400));
        setLayout(new java.awt.GridBagLayout());

        graphTitleLabel.setText("Graph Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(graphTitleLabel, gridBagConstraints);

        graphTitleTextField.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(graphTitleTextField, gridBagConstraints);

        graphTitleFontLabel.setText("Font Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(graphTitleFontLabel, gridBagConstraints);

        graphTitleFontSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(graphTitleFontSpinner, gridBagConstraints);

        xAxisLabel.setText("X Axis Label:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisLabel, gridBagConstraints);

        xAxisTextField.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisTextField, gridBagConstraints);

        xAxisFontLabel.setText("Font Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisFontLabel, gridBagConstraints);

        xAxisFontSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisFontSpinner, gridBagConstraints);

        xAxisOriginLabel.setText("X Axis Origin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisOriginLabel, gridBagConstraints);

        xAxisOriginSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisOriginSpinner, gridBagConstraints);

        xAxisExtentLabel.setText("X Axis Extent:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisExtentLabel, gridBagConstraints);

        xAxisExtentSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisExtentSpinner, gridBagConstraints);

        xAxisTickIntervalLabel.setText("X Tick Interval:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisTickIntervalLabel, gridBagConstraints);

        xAxisTickIntervalSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xAxisTickIntervalSpinner, gridBagConstraints);

        showXTicksCheckBox.setText("Show X Tick Marks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(showXTicksCheckBox, gridBagConstraints);

        showXTickLabelsCheckBox.setText("Show X Tick Labels");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(showXTickLabelsCheckBox, gridBagConstraints);

        xTickFontSizeLabel.setText("Font Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xTickFontSizeLabel, gridBagConstraints);

        xTickFontSizeSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(xTickFontSizeSpinner, gridBagConstraints);

        yAxisLabel.setText("Y Axis Label:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisLabel, gridBagConstraints);

        yAxisTextField.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisTextField, gridBagConstraints);

        yAxisFontLabel.setText("Font Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisFontLabel, gridBagConstraints);

        yAxisFontSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisFontSpinner, gridBagConstraints);

        yAxisOriginLabel.setText("Y Axis Origin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisOriginLabel, gridBagConstraints);

        yAxisOriginSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisOriginSpinner, gridBagConstraints);

        yAxisExtentLabel.setText("Y Axis Extent:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisExtentLabel, gridBagConstraints);

        yAxisExtentSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisExtentSpinner, gridBagConstraints);

        yAxisTickIntervalLabel.setText("Y Tick Interval:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisTickIntervalLabel, gridBagConstraints);

        yAxisTickIntervalSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yAxisTickIntervalSpinner, gridBagConstraints);

        showYTicksCheckBox.setText("Show Y Tick Marks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(showYTicksCheckBox, gridBagConstraints);

        showYTickLabelsCheckBox.setText("Show Y Tick Labels");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(showYTickLabelsCheckBox, gridBagConstraints);

        yTickFontSizeLabel.setText("Font Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yTickFontSizeLabel, gridBagConstraints);

        yTickFontSizeSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(yTickFontSizeSpinner, gridBagConstraints);

        histogramBinSizeLabel.setText("Histogram Bin Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(histogramBinSizeLabel, gridBagConstraints);

        histogramBinSizeSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(histogramBinSizeSpinner, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel graphTitleFontLabel;
    private javax.swing.JSpinner graphTitleFontSpinner;
    private javax.swing.JLabel graphTitleLabel;
    private javax.swing.JTextField graphTitleTextField;
    private javax.swing.JLabel histogramBinSizeLabel;
    private javax.swing.JSpinner histogramBinSizeSpinner;
    private javax.swing.JCheckBox showXTickLabelsCheckBox;
    private javax.swing.JCheckBox showXTicksCheckBox;
    private javax.swing.JCheckBox showYTickLabelsCheckBox;
    private javax.swing.JCheckBox showYTicksCheckBox;
    private javax.swing.JLabel xAxisExtentLabel;
    private javax.swing.JSpinner xAxisExtentSpinner;
    private javax.swing.JLabel xAxisFontLabel;
    private javax.swing.JSpinner xAxisFontSpinner;
    private javax.swing.JLabel xAxisLabel;
    private javax.swing.JLabel xAxisOriginLabel;
    private javax.swing.JSpinner xAxisOriginSpinner;
    private javax.swing.JTextField xAxisTextField;
    private javax.swing.JLabel xAxisTickIntervalLabel;
    private javax.swing.JSpinner xAxisTickIntervalSpinner;
    private javax.swing.JLabel xTickFontSizeLabel;
    private javax.swing.JSpinner xTickFontSizeSpinner;
    private javax.swing.JLabel yAxisExtentLabel;
    private javax.swing.JSpinner yAxisExtentSpinner;
    private javax.swing.JLabel yAxisFontLabel;
    private javax.swing.JSpinner yAxisFontSpinner;
    private javax.swing.JLabel yAxisLabel;
    private javax.swing.JLabel yAxisOriginLabel;
    private javax.swing.JSpinner yAxisOriginSpinner;
    private javax.swing.JTextField yAxisTextField;
    private javax.swing.JLabel yAxisTickIntervalLabel;
    private javax.swing.JSpinner yAxisTickIntervalSpinner;
    private javax.swing.JLabel yTickFontSizeLabel;
    private javax.swing.JSpinner yTickFontSizeSpinner;
    // End of variables declaration//GEN-END:variables
    
}

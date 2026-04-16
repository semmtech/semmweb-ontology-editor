/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.plugin.semmweb.core.widgets;


import java.util.Map;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.google.common.collect.Maps;
import com.semmtech.ui.plugin.widgets.CustomFormImages;


/**
 * 
 * @author Sander Stolk
 */
public class OntologyEditorFormHeading extends Composite {
    public static final int HEIGHT = 30; // including the separator (if it is
                                         // displayed)
    public static final int SEPARATOR_HEIGHT = 2;
    public static final String HEADER_FONT = "com.semmtech.plugin.semmweb.core.headerFont";

    private static final String COLOR_BASE_BG = "baseBg"; //$NON-NLS-1$

    protected String text;
    protected Image image;
    protected Map<String, Color> colors = Maps.newHashMap();

    protected final Composite titleComposite;
    protected final Label imageLabel;
    protected final Label titleLabel;

    protected final ToolBarManager toolBarManager;

    protected final Composite headClientComposite;

    protected final Composite separatorComposite;

    protected GradientInfo gradientInfo;
    protected Image gradientImage;
    protected boolean separatorVisible;

    private class GradientInfo {
        Color[] gradientColors;
        int[] percents;
        boolean vertical;
    }

    public OntologyEditorFormHeading(Composite parent, int style) {
        super(parent, style);

        separatorVisible = false;

        // Before creating the controls, set the font correctly
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        ITheme currentTheme = themeManager.getCurrentTheme();
        FontRegistry fontRegistry = currentTheme.getFontRegistry();
        Font font = fontRegistry.get(HEADER_FONT);
        setFont(font);

        // Set the layout. Three columns.
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0, 0).numColumns(3)
                .applyTo(this);

        // First composite and controls: titleComposite
        titleComposite = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(SWT.DEFAULT, HEIGHT)
                .applyTo(titleComposite);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(6, 0).applyTo(titleComposite);
        imageLabel = new Label(titleComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, true)
                .applyTo(imageLabel);
        titleLabel = new Label(titleComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, true)
                .applyTo(titleLabel);
        titleLabel.setFont(getFont());

        // Second composite and controls: toolbar manager
        toolBarManager = new ToolBarManager();
        ToolBar toolBar = toolBarManager.createControl(this);
        toolBar.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false)
                .applyTo(toolBar);

        // Third composite and controls: miscellaneous
        headClientComposite = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL).grab(false, true)
                .applyTo(headClientComposite);
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).applyTo(headClientComposite);

        // Separator composite on the next line
        separatorComposite = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().span(3, 1).grab(true, false).hint(SWT.DEFAULT, 0)
                .applyTo(separatorComposite);
        separatorComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        separatorComposite.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                if (separatorVisible && (e.height >= SEPARATOR_HEIGHT)) {
                    e.gc.setLineWidth(1);

                    // Line 1
                    if (hasColor(IFormColors.H_BOTTOM_KEYLINE1)) {
                        e.gc.setForeground(getColor(IFormColors.H_BOTTOM_KEYLINE1));
                    }
                    else {
                        e.gc.setForeground(getBackground());
                    }
                    e.gc.drawLine(e.x, e.y + e.height - 2, e.x + e.width - 1, e.y + e.height - 2);

                    // Line 2
                    if (hasColor(IFormColors.H_BOTTOM_KEYLINE2)) {
                        e.gc.setForeground(getColor(IFormColors.H_BOTTOM_KEYLINE2));
                    }
                    else {
                        e.gc.setForeground(getForeground());
                    }
                    e.gc.drawLine(e.x, e.y + e.height - 1, e.x + e.width - 1, e.y + e.height - 1);
                }
            }
        });

        setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        setBackgroundMode(SWT.INHERIT_FORCE);

        addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event e) {
                if (gradientInfo != null) {
                    updateGradientImage();
                }
            }
        });
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        titleLabel.setText(text);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        imageLabel.setImage(image);
    }

    public ToolBarManager getToolBarManager() {
        return toolBarManager;
    }

    /**
     * Sets the vertical alignment of the toolbar. The value should be either
     * SWT.TOP, SWT.CENTER or SWT.BOTTOM.
     */
    public void setToolBarAlignment(int value) {
        if (value == SWT.TOP || value == SWT.CENTER || value == SWT.BOTTOM) {
            Object layoutData = toolBarManager.getControl().getLayoutData();
            if (layoutData instanceof GridData) {
                GridData gridData = (GridData) layoutData;
                gridData.verticalAlignment = value;
            }
        }
    }

    public void setSeparatorVisible(boolean addSeparator) {
        separatorVisible = addSeparator;
        Object layoutData = separatorComposite.getLayoutData();
        if (layoutData instanceof GridData) {
            GridData gridData = (GridData) layoutData;
            gridData.heightHint = (addSeparator) ? SEPARATOR_HEIGHT : 0;
        }
        layoutData = titleComposite.getLayoutData();
        if (layoutData instanceof GridData) {
            GridData gridData = (GridData) layoutData;
            gridData.heightHint = (addSeparator) ? (HEIGHT - SEPARATOR_HEIGHT) : HEIGHT;
        }
    }

    public Composite getHeadClientComposite() {
        return headClientComposite;
    }

    public void putColor(String key, Color color) {
        if (color == null)
            colors.remove(key);
        else
            colors.put(key, color);
    }

    public Color getColor(String key) {
        return colors.get(key);
    }

    public boolean hasColor(String key) {
        return colors.containsKey(key);
    }

    public void setTextBackground(Color[] gradientColors, int[] percents, boolean vertical) {
        if (gradientColors != null) {
            gradientInfo = new GradientInfo();
            gradientInfo.gradientColors = gradientColors;
            gradientInfo.percents = percents;
            gradientInfo.vertical = vertical;
            setBackground(null);
            updateGradientImage();
        }
        else {
            // reset
            gradientInfo = null;
            if (gradientImage != null) {
                CustomFormImages.getInstance().markFinished(gradientImage, getDisplay());
                gradientImage = null;
                setBackgroundImage(null);
            }
        }
    }

    private void updateGradientImage() {
        Rectangle rect = getBounds();
        if (gradientImage != null) {
            CustomFormImages.getInstance().markFinished(gradientImage, getDisplay());
            gradientImage = null;
        }
        if (gradientInfo != null) {
            gradientImage = CustomFormImages.getInstance().getGradient(gradientInfo.gradientColors,
                    gradientInfo.percents, gradientInfo.vertical ? rect.height : rect.width,
                    gradientInfo.vertical, getColor(COLOR_BASE_BG), getDisplay());
        }
        setBackgroundImage(gradientImage);
    }

    public void setBackgroundImage(Image image) {
        super.setBackgroundImage(image);
        if (image != null) {
            internalSetBackground(null);
        }
    }

    /**
     * Sets the background color of the header.
     */
    public void setBackground(Color bg) {
        super.setBackground(bg);
        internalSetBackground(bg);
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        titleLabel.setForeground(color);
    }

    private void internalSetBackground(Color bg) {
        titleComposite.setBackground(bg);
        imageLabel.setBackground(bg);
        titleLabel.setBackground(bg);
        toolBarManager.getControl().setBackground(bg);
        putColor(COLOR_BASE_BG, bg);
    }

    public void setColors(FormColors colors) {
        putColor(IFormColors.H_GRADIENT_END, colors.getColor(IFormColors.H_GRADIENT_END));
        putColor(IFormColors.H_GRADIENT_START, colors.getColor(IFormColors.H_GRADIENT_START));
        putColor(IFormColors.H_BOTTOM_KEYLINE1, colors.getColor(IFormColors.H_BOTTOM_KEYLINE1));
        putColor(IFormColors.H_BOTTOM_KEYLINE2, colors.getColor(IFormColors.H_BOTTOM_KEYLINE2));
        putColor(IFormColors.H_HOVER_LIGHT, colors.getColor(IFormColors.H_HOVER_LIGHT));
        putColor(IFormColors.H_HOVER_FULL, colors.getColor(IFormColors.H_HOVER_FULL));
        putColor(IFormColors.TB_TOGGLE, colors.getColor(IFormColors.TB_TOGGLE));
        putColor(IFormColors.TB_TOGGLE_HOVER, colors.getColor(IFormColors.TB_TOGGLE_HOVER));
        putColor(IFormColors.TITLE, colors.getColor(IFormColors.TITLE));
    }

    public void decorate() {
        if (hasColor(IFormColors.H_GRADIENT_END) && hasColor(IFormColors.H_GRADIENT_START)) {
            setTextBackground(new Color[] { getColor(IFormColors.H_GRADIENT_END),
                    getColor(IFormColors.H_GRADIENT_START) }, new int[] { 100 }, true);
        }
        if (hasColor(IFormColors.TITLE)) {
            setForeground(getColor(IFormColors.TITLE));
        }
        setSeparatorVisible(true);
    }

    public Label getImageLabel() {
        return imageLabel;
    }

}

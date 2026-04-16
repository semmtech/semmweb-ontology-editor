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

package com.semmtech.ui.plugin.widgets;


import java.util.Iterator;

import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * 
 * @author Sander Stolk
 */
public class ExtendedSourceViewer extends org.eclipse.jface.text.source.SourceViewer {

    protected Canvas borderComposite;
    protected boolean fIsVerticalRulerVisible;

    public ExtendedSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        super(parent, ruler, null, false, styles);
        fIsVerticalRulerVisible = (ruler != null);
    }

    public ExtendedSourceViewer(Composite parent, IVerticalRuler verticalRuler,
            IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
        fIsVerticalRulerVisible = (verticalRuler != null);
    }

    @Override
    public void showAnnotations(boolean show) {
        fIsVerticalRulerVisible = (getVerticalRuler() != null && (show || !isVerticalRulerOnlyShowingAnnotations()));
        super.showAnnotations(show);
    }

    protected boolean isVerticalRulerOnlyShowingAnnotations() {
        IVerticalRuler fVerticalRuler = getVerticalRuler();
        if (fVerticalRuler instanceof VerticalRuler)
            return true;

        if (fVerticalRuler instanceof CompositeRuler) {
            Iterator<?> iter = ((CompositeRuler) fVerticalRuler).getDecoratorIterator();
            return iter.hasNext() && iter.next() instanceof AnnotationRulerColumn
                    && !iter.hasNext();
        }
        return false;
    }

    @Override
    protected void createControl(Composite parent, int styles) {
        if (getVerticalRuler() != null) {
            if ((styles & SWT.BORDER) != 0) {
                borderComposite = new Canvas(parent, SWT.BORDER);
                TableWrapLayout layout = new TableWrapLayout();
                layout.topMargin = 0;
                layout.bottomMargin = 0;
                layout.leftMargin = 0;
                layout.rightMargin = 0;
                borderComposite.setLayout(layout);
                parent = borderComposite;
            }
        }

        super.createControl(parent, styles);

        if (borderComposite != null) {
            Control fComposite = super.getControl();
            fComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                    TableWrapData.FILL_GRAB));
        }
    }

    @Override
    public Control getControl() {
        if (borderComposite != null) {
            return borderComposite;
        }
        return super.getControl();
    }

    @Override
    protected Layout createLayout() {
        return new ExtendedRulerLayout(GAP_SIZE_1);
    }

    protected class ExtendedRulerLayout extends RulerLayout {
        public ExtendedRulerLayout(int gap) {
            super(gap);
        }

        @Override
        protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
            Point s = super.computeSize(composite, wHint, hHint, flushCache);
            Control[] children = composite.getChildren();
            if (children.length > 1) {
                Point textSize = children[0].computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
                if (textSize.y > s.y) {
                    s.y = textSize.y;
                }
            }
            return s;
        }
    }
}

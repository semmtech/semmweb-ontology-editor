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

package com.semmtech.ui.plugin.viewers;


import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.CommonViewerSorter;
import org.eclipse.ui.navigator.INavigatorFilterService;


/**
 * 
 * @author Sander Stolk
 */
public class CommonFilterViewer extends CommonViewer {
    public final static String DEFAULT_ID = "com.semmtech.ui.plugin.viewers.commonFilterViewer";

    public CommonFilterViewer(Composite aParent) {
        this(aParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    }

    public CommonFilterViewer(Composite aParent, int aStyle) {
        this(DEFAULT_ID, aParent, aStyle);
    }

    public CommonFilterViewer(String aViewerId, Composite aParent, int aStyle) {
        super(aViewerId, aParent, aStyle);
        initFilters();
        initSorter();
    }

    private void initFilters() {
        INavigatorFilterService filterService = getNavigatorContentService().getFilterService();
        ViewerFilter[] visibleFilters = filterService.getVisibleFilters(true);
        for (int i = 0; i < visibleFilters.length; i++) {
            addFilter(visibleFilters[i]);
        }
    }

    private void initSorter() {
        setSorter(new CommonViewerSorter());
    }

}

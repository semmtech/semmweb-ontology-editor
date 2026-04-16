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


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.ui.plugin.DelayedRunnableExecution;
import com.semmtech.ui.plugin.widgets.Widgets;


public class SearchComposite extends Composite implements ModifyListener, KeyListener,
        FocusListener {

    private boolean dirty;
    private boolean hasFocus = false;
    private Text filterText;
    private Label commandLabel;
    private String filter = "";
    private final DelayedRunnableExecution execution;
    private List<SearchFilterChangedListener> changeListeners;

    public void addSearchFilterChangedListener(SearchFilterChangedListener listener) {
        changeListeners.add(listener);
    }

    public void removeSearchFilterChangedListener(SearchFilterChangedListener listener) {
        changeListeners.remove(listener);
    }

    public SearchComposite(Composite parent, int style) {
        super(parent, style);
        this.execution = new DelayedRunnableExecution(new Runnable() {
            @Override
            public void run() {
                fireChange();
            }
        });
        this.changeListeners = Lists.newArrayList();
        createControls();
    }

    @SuppressWarnings("unused")
    private void createControls() {
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);

        Composite inputComposite = new Composite(this, SWT.BORDER);
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        inputComposite.setLayoutData(layoutData);
        layout = new GridLayout(3, false);
        layout.marginBottom = 1;
        layout.marginTop = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        inputComposite.setLayout(layout);
        inputComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        new Label(inputComposite, SWT.NONE);

        filterText = new Text(inputComposite, SWT.NONE);
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        filterText.addModifyListener(this);
        filterText.addFocusListener(this);
        filterText.addKeyListener(this);

        commandLabel = new Label(inputComposite, SWT.NONE);
        commandLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        commandLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (!Strings.isNullOrEmpty(filter)) {
                    clearFilter();
                }
            }
        });
        refreshControls();
    }

    /**
     * Sets the text of the textbox, change forecolor and changes image based on
     * the value of the filter.
     */
    private void refreshControls() {
        if (!Strings.isNullOrEmpty(filter) || hasFocus) {
            filterText.setText(filter);
            if (hasFocus) {
                filterText.setSelection(filter.length(), filter.length());
            }

            filterText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
            commandLabel
                    .setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_FIND_CLEAR));
        }
        else {
            filterText.setText("Find");
            filterText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
            commandLabel.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_FIND));
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = Strings.nullToEmpty(filter);
        refreshControls();
    }

    @Override
    public boolean setFocus() {
        if (!Widgets.isNullOrDisposed(filterText)) {
            filterText.selectAll();
            return true;
        }
        return super.setFocus();
    }

    @Override
    public void focusGained(FocusEvent e) {
        hasFocus = true;
        refreshControls();
    }

    @Override
    public void focusLost(FocusEvent e) {
        hasFocus = false;
        execution.abort();
        fireChange();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.CR) {
            execution.abort();
            fireChange();
        }
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (hasFocus) {
            String oldFilter = filter;
            if (filter == null || !filter.equals(filterText.getText())) {
                filter = filterText.getText();
                execution.start();
                execution.poke();

                if (!(isEmptyFilter(oldFilter) && isEmptyFilter(filter))) {
                    dirty = true;
                }
            }
        }
    }

    private boolean isEmptyFilter(String filterText) {
        boolean empty = Strings.isNullOrEmpty(filterText) || filterText.equals("*");
        return empty;
    }

    protected void fireChange() {
        if (Strings.isNullOrEmpty(filter) && !hasFocus) {
            refreshControls();
        }
        if (dirty) {
            for (SearchFilterChangedListener listener : changeListeners) {
                listener.filterChanged(filter);
            }
            dirty = false;
        }
    }

    private void clearFilter() {
        filterText.setText(new String());
        filterText.setFocus();
        refreshControls();
    }

}

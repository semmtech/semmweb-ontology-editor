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


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.google.common.collect.Lists;
import com.semmtech.ui.plugin.ControlProvider;
import com.semmtech.ui.plugin.layouts.SwitchLayout;


/**
 * 
 * @author Sander Stolk
 */
public class ExtendedCTabFolder extends CTabFolder {
    private final int EMPTY_CONTAINER_HEIGHT = 69;
    private final String ATTRIBUTE_ORIGINAL_INDEX = "originalPosition";

    private class HiddenItem {
        private final Object data;
        private final String text;
        private final String tooltip;
        private final Image image;
        private final Object originalIndex;

        public HiddenItem(CTabItem item) {
            this.data = item.getData();
            this.text = item.getText();
            this.tooltip = item.getToolTipText();
            this.image = item.getImage();
            this.originalIndex = item.getData(ATTRIBUTE_ORIGINAL_INDEX);
        }

        public boolean contains(Object object) {
            return ((object != null) && data.equals(object));
        }

        public int getOriginalIndex() {
            if (originalIndex instanceof Integer) {
                return ((Integer) originalIndex).intValue();
            }
            return -1;
        }

        public void createItem(int index) {
            CTabItem item = new CTabItem(ExtendedCTabFolder.this, SWT.NONE, index);
            item.setData(data);
            item.setText((setUnselectedTextInvisibleListener != null) ? "" : text);
            item.setToolTipText(tooltip);
            item.setImage(image);
            item.setData(ATTRIBUTE_ORIGINAL_INDEX, originalIndex);
        }
    }

    private boolean showContent;
    private Composite externalContentComposite;
    private SelectionListener setUnselectedTextInvisibleListener;
    private final List<String> tabNames;
    private final List<HiddenItem> hiddenItems;

    private Label errorLabel;

    public ExtendedCTabFolder(Composite parent, int style) {
        super(parent, style);
        this.showContent = true;
        tabNames = Lists.newArrayList();
        hiddenItems = Lists.newArrayList();
    }

    public void showContent(boolean visible) {
        this.showContent = visible;
    }

    public void setContentToShowIn(Composite composite) {
        this.externalContentComposite = composite;
        composite.setLayout(new SwitchLayout());
    }

    public void setUnselectedTextVisible(boolean visible) {
        if (!visible) {
            if (setUnselectedTextInvisibleListener == null) {
                setUnselectedTextInvisibleListener = new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        setOriginalIndexes();

                        for (int i = 0; i < getItemCount(); i++) {
                            CTabItem item = getItem(i);
                            int originalIndex = getOriginalIndex(item);
                            if (originalIndex == tabNames.size()) {
                                tabNames.add(item.getText());
                            }
                        }

                        if (e.item instanceof CTabItem) {
                            CTabItem itemSelected = (CTabItem) e.item;
                            int indexSelected = indexOf(itemSelected);
                            int originalIndex = getOriginalIndex(itemSelected);

                            // set name again of the selected item
                            itemSelected.setText(tabNames.get(originalIndex));

                            // delete name of the unselected items
                            for (int i = 0; i < getItemCount(); i++) {
                                if (i != indexSelected) {
                                    getItem(i).setText("");
                                }
                            }
                        }
                    }
                };
            }
            addSelectionListener(setUnselectedTextInvisibleListener);

            CTabItem selectedItem = getSelection();
            if (selectedItem != null) {
                selectionChanged(selectedItem);
            }
        }
        else {
            if (setUnselectedTextInvisibleListener != null) {
                removeSelectionListener(setUnselectedTextInvisibleListener);
            }
        }
    }

    @Override
    public void setSelection(int index) {
        super.setSelection(index);
        selectionChanged(getItem(index));
    }

    @Override
    public void setSelection(CTabItem item) {
        super.setSelection(item);
        selectionChanged(item);
    }

    private void selectionChanged(CTabItem item) {
        updateTabTextsOnSelectionChange(item);
        updateDisplayedContentOnSelectionChange(item);
    }

    private void updateTabTextsOnSelectionChange(CTabItem item) {
        if (setUnselectedTextInvisibleListener != null) {
            Event e = new Event();
            e.widget = item;
            e.item = item;
            SelectionEvent se = new SelectionEvent(e);
            setUnselectedTextInvisibleListener.widgetSelected(se);
        }
    }

    private void updateDisplayedContentOnSelectionChange(CTabItem item) {
        if (!showContent || Widgets.isNullOrDisposed(externalContentComposite)) {
            return;
        }
        if (!(externalContentComposite.getLayout() instanceof StackLayout)) {
            return;
        }
        StackLayout stackLayout = (StackLayout) externalContentComposite.getLayout();

        Object data = item.getData();
        Control control = null;
        try {
            if (data instanceof Control) {
                control = (Control) data;
            }
            else if (data instanceof ControlProvider) {
                control = ((ControlProvider) data).getControl();
            }
        }
        catch (Throwable t) {
            control = createErrorControl("An exception occurred:\n" + t.getMessage());
        }

        if (control == null) {
            control = createErrorControl(new String());
        }

        stackLayout.topControl = control;
        control.setFocus();
    }

    protected Control createErrorControl(String message) {
        if (externalContentComposite != null) {
            if (errorLabel == null) {
                errorLabel = new Label(externalContentComposite, SWT.NONE);
            }
            errorLabel.setText(message);
            errorLabel.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                    TableWrapData.FILL_GRAB));
            return errorLabel;
        }
        return null;
    }

    @Override
    public Point getSize() {
        Point result = super.getSize();
        result.x = getWidth(result.x);
        result.y = getHeight(result.y);
        return result;
    }

    @Override
    public Rectangle getBounds() {
        Rectangle result = super.getBounds();
        result.width = getWidth(result.width);
        result.height = getHeight(result.height);
        return result;
    }

    @Override
    public Point computeSize(int wHint, int hHint) {
        Point result = super.computeSize(wHint, hHint);
        result.x = getWidth(result.x);
        result.y = getHeight(result.y);
        return result;
    }

    @Override
    public Rectangle computeTrim(int x, int y, int width, int height) {
        Rectangle result = super.computeTrim(x, y, width, height);
        result.width = getWidth(result.width);
        result.height = getHeight(result.height);
        return result;
    }

    private int getWidth(int proposedWidth) {
        if ((setUnselectedTextInvisibleListener != null) && (getItemCount() > 0)) {
            int maxTextLength = 0;

            if (getSelectionIndex() < 0) {
                return proposedWidth;
            }

            for (int i = 0; i < getItemCount(); i++) {
                if (getItem(i).getText().length() >= maxTextLength) {
                    // maxTextLengthIndex = i;
                    maxTextLength = getItem(i).getText().length();
                }
                if ((tabNames != null) && (i < tabNames.size())
                        && (tabNames.get(i).length() >= maxTextLength)) {
                    // maxTextLengthIndex = i;
                    maxTextLength = tabNames.get(i).length();
                }
            }

            // Roughly calculating the selected tab's length plus that of the
            // others
            int result = (41 + maxTextLength * (5 + 1)) + ((getItemCount() - 1) * 28);
            return result;

            /*
             * NOTE: If a more precise calculation would be required, making it
             * possible to hide the round corner on the right of the
             * ExtendedCTabFolder, the exact difference in width between the
             * currently shown text and the longest text would be required. This
             * difference should then be added to the original proposedWidth.
             * For calculating the width of strings exactly, see function
             * FigureUtilities.getStringExtents(string, font) in package
             * org.eclipse.draw2d.
             */
        }
        return proposedWidth;
    }

    private int getHeight(int proposedHeight) {
        if ((getItemCount() > 0 && proposedHeight > EMPTY_CONTAINER_HEIGHT)
                && (!showContent || !Widgets.isNullOrDisposed(externalContentComposite))) {
            return proposedHeight - EMPTY_CONTAINER_HEIGHT;
        }
        return proposedHeight;
    }

    @Override
    public boolean isFocusControl() {
        // Avoids having titles of tabs underlined
        return false;
    }

    private void setOriginalIndexes() {
        int hiddenItemCount = hiddenItems.size();
        for (int i = 0; i < getItemCount(); i++) {
            CTabItem item = getItem(i);
            if (item.getData(ATTRIBUTE_ORIGINAL_INDEX) == null) {
                item.setData(ATTRIBUTE_ORIGINAL_INDEX, new Integer(i) + hiddenItemCount);
            }
        }
    }

    private int getOriginalIndex(CTabItem item) {
        if (item != null) {
            Object originalIndexValue = item.getData(ATTRIBUTE_ORIGINAL_INDEX);
            if (originalIndexValue instanceof Integer) {
                return ((Integer) originalIndexValue).intValue();
            }
        }
        return -1;
    }

    public void addItem(Control control, String title, Image image) {
        addItem((Object) control, title, image);
    }

    public void addItem(ControlProvider controlProvider, String title, Image image) {
        addItem((Object) controlProvider, title, image);
    }

    private void addItem(Object object, String title, Image image) {
        addItem(object, title, image, getItemCount());
    }

    private void addItem(Object object, String title, Image image, int index) {
        if (title == null) {
            title = "";
        }

        CTabItem item = new CTabItem(this, SWT.NONE, index);
        item.setData(object);
        item.setText(title);
        item.setToolTipText(title);
        item.setImage(image);
    }

    public void hideItem(Control control) {
        hideItem((Object) control);
    }

    public void hideItem(ControlProvider controlProvider) {
        hideItem((Object) controlProvider);
    }

    private void hideItem(Object object) {
        if (object == null) {
            return;
        }

        setOriginalIndexes();

        CTabItem item = null;
        for (int i = 0; i < getItemCount(); i++) {
            CTabItem curItem = getItem(i);
            // Find the item that needs to be hidden.
            if ((curItem.getData() != null) && object.equals(curItem.getData())) {
                item = curItem;
            }
        }

        if (item != null) {
            // Backup data of the item
            hiddenItems.add(new HiddenItem(item));

            // Calculate whether focus needs to move to another tab.
            int shiftFocusToIndex = -1;
            CTabItem selectedItem = getSelection();
            if (item.equals(selectedItem)) {
                shiftFocusToIndex = getSelectionIndex() - 1;
                if (shiftFocusToIndex < 0) {
                    shiftFocusToIndex = 0;
                }
            }

            // Dispose item.
            item.dispose();

            // Shift focus to another tab if necessary.
            if ((shiftFocusToIndex >= 0) && (shiftFocusToIndex < getItemCount())) {
                setSelection(shiftFocusToIndex);
            }
        }

        Widgets.layoutControlUpToScrollableParent(this);
    }

    public void showItem(Control control) {
        showItem((Object) control);
    }

    public void showItem(ControlProvider controlProvider) {
        showItem((Object) controlProvider);
    }

    private void showItem(Object object) {
        HiddenItem itemToShow = null;
        for (HiddenItem hiddenItem : hiddenItems) {
            if (hiddenItem.contains(object)) {
                itemToShow = hiddenItem;
                break;
            }
        }

        if (itemToShow == null) {
            return;
        }

        // Create a new item at the desired index
        int originalIndex = itemToShow.getOriginalIndex();

        int index = 0;
        for (; index < getItemCount(); index++) {
            if (getOriginalIndex(getItem(index)) > originalIndex) {
                break;
            }
        }

        itemToShow.createItem(index);

        // Remove the hidden item from the list
        hiddenItems.remove(itemToShow);

        // Set selection if the added item is the only one shown
        if (getItemCount() == 1) {
            setSelection(0);
        }

        Widgets.layoutControlUpToScrollableParent(this);
    }
}

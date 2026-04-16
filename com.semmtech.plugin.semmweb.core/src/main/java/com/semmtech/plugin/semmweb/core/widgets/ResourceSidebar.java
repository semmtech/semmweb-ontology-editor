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


import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractOntologyEditorFormPage;
import com.semmtech.plugin.semmweb.core.forms.editor.ModelOverviewFormPage;
import com.semmtech.plugin.semmweb.core.forms.editor.ModelResourceFormPage;
import com.semmtech.plugin.semmweb.core.handlers.CopyResourceToClipboardHandler;
import com.semmtech.plugin.semmweb.core.handlers.RelabelSelectedResourceHandler;
import com.semmtech.plugin.semmweb.core.handlers.RenameSelectedResourceHandler;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTip;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class ResourceSidebar extends Composite {
    public static final int WIDTH = 28;
    public static final int BORDER_WIDTH = 1; // the border is not included in
                                              // the value of WIDTH.

    private static final int HOME_HEIGHT = 29;
    private static final int ARROW_HEIGHT = 12;
    private static final int NONSLOTS_HEIGHT_TOP = HOME_HEIGHT + ARROW_HEIGHT;
    private static final int NONSLOTS_HEIGHT_BOTTOM = ARROW_HEIGHT;
    private static final int NONSLOTS_HEIGHT = NONSLOTS_HEIGHT_TOP + NONSLOTS_HEIGHT_BOTTOM;

    private static final int RESOURCE_SLOT_HEIGHT = 24;
    private static final int MINIMUM_SLOTS_SHOWN = 1;

    private final AbstractOntologyEditorFormPage parentPage;
    private final ResourceSidebarSettings settings;
    private int availableHeight;
    private PaintListener paintListener;

    // private MouseWheelListener mouseWheelListener;

    public static class ResourceSidebarSettings {
        protected int firstIndexShown;

        public ResourceSidebarSettings() {
            firstIndexShown = 0;
        }
    }

    public ResourceSidebar(Composite parent, AbstractOntologyEditorFormPage parentPage) {
        this(parent, parentPage, SWT.DEFAULT);
    }

    public ResourceSidebar(Composite parent, AbstractOntologyEditorFormPage parentPage,
            int availableHeight) {
        super(parent, SWT.NONE);
        this.parentPage = parentPage;
        this.settings = parentPage.getOpenResourceSidebarSettings();
        this.availableHeight = availableHeight;
        createContent();
    }

    protected void createContent() {
        FormToolkit toolkit = parentPage.getToolkit();
        Display display = getDisplay();

        final Color backgroundColor = display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        final Color borderColor = display.getSystemColor(SWT.COLOR_DARK_GRAY);

        GridLayoutFactory.fillDefaults().margins(0, 0).extendedMargins(0, BORDER_WIDTH, 0, 0)
                .spacing(0, 0).applyTo(this);
        setBackground(backgroundColor);

        final OntResource currentResource;
        if (parentPage instanceof ModelResourceFormPage) {
            ModelResourceFormPage resourcePage = (ModelResourceFormPage) parentPage;
            currentResource = resourcePage.getResource();
        }
        else {
            currentResource = null;
        }

        Composite startLinkComposite = toolkit.createComposite(this);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).hint(WIDTH, HOME_HEIGHT)
                .applyTo(startLinkComposite);
        startLinkComposite.setBackground(backgroundColor);
        startLinkComposite.setLayout(new GridLayout());

        // Create link to initial page
        ImageHyperlink startLink = toolkit.createImageHyperlink(startLinkComposite, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true)
                .applyTo(startLink);
        startLink.setBackground(backgroundColor);
        startLink.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_ONTOLOGY_FILE));
        startLink.setToolTipText("Open the model overview");
        if (!(parentPage instanceof ModelOverviewFormPage)) {
            startLink.addHyperlinkListener(new HyperlinkAdapter() {
                @Override
                public void linkActivated(HyperlinkEvent arg0) {
                    parentPage.openStartPage();
                }
            });
        }

        final List<OntResource> openResources = parentPage.getOpenResources();
        final int maxResourceSlots = calculateMaxResourceSlots();

        if (settings.firstIndexShown + maxResourceSlots > openResources.size()) {
            settings.firstIndexShown = Math.max(0, openResources.size() - maxResourceSlots);
        }

        int resourceSlotsToShow = Math.min(maxResourceSlots, openResources.size());

        if (!openResources.isEmpty()) {
            boolean enabled = (calculateResourcesHiddenAbove() > 0);
            SquareButton arrowUp = new SquareButton(this, SWT.NONE);
            GridDataFactory.fillDefaults().hint(WIDTH, SWT.DEFAULT).applyTo(arrowUp);
            arrowUp.setBackground(backgroundColor);
            arrowUp.setInnerMarginHeight(0);
            arrowUp.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_ARROW_UP));
            arrowUp.setRoundedCorners(false);
            arrowUp.setEnabled(enabled);
            arrowUp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    if (e.button == 1) {
                        setFirstIndexShown(settings.firstIndexShown - 1);
                    }
                }
            });
            createScrollResourcesContextMenu(arrowUp, resourceSlotsToShow, SWT.UP);
        }

        int slotContainingCurrentResource = -1;
        for (int i = 0; i < resourceSlotsToShow; i++) {
            final OntResource resource = openResources.get(settings.firstIndexShown + i);

            Color resourceBackgroundColor = backgroundColor;
            if ((currentResource != null) && resource.equals(currentResource)) {
                slotContainingCurrentResource = i;
                resourceBackgroundColor = display.getSystemColor(SWT.COLOR_WHITE);
            }

            Composite resourceComposite = toolkit.createComposite(this);
            resourceComposite.setBackground(resourceBackgroundColor);
            GridDataFactory.fillDefaults().hint(WIDTH, SWT.DEFAULT).applyTo(resourceComposite);
            GridLayout layout = new GridLayout();
            layout.marginHeight = 3;
            resourceComposite.setLayout(layout);
            if (i == slotContainingCurrentResource) {
                resourceComposite.addPaintListener(new PaintListener() {
                    @Override
                    public void paintControl(PaintEvent e) {
                        e.gc.setLineWidth(1);
                        e.gc.setForeground(borderColor);
                        e.gc.drawLine(e.x, e.y, e.x + e.width - 1, e.y);
                        e.gc.drawLine(e.x, e.y + e.height - 1, e.x + e.width - 1, e.y + e.height
                                - 1);
                    }
                });
            }

            final ImageHyperlink resourceLink = toolkit.createImageHyperlink(resourceComposite,
                    SWT.NONE);
            resourceLink.setBackground(resourceBackgroundColor);
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(resourceLink);
            resourceLink.setImage(parentPage.getLabelProvider().getImage(resource));

            final ILabelProviderListener resourceLabelListener = new ILabelProviderListener() {
                @Override
                public void labelProviderChanged(LabelProviderChangedEvent event) {
                    if (parentPage.isActive()) {
                        Object[] changedElements = event.getElements();
                        if (changedElements == null
                                || Arrays.asList(changedElements).contains(resource)) {
                            Display.getDefault().syncExec(new Runnable() {
                                public void run() {
                                    // set image
                                    resourceLink.setImage(parentPage.getLabelProvider().getImage(
                                            resource));
                                    // force a refresh of the link
                                    resourceLink.setEnabled(false);
                                    resourceLink.setEnabled(true);
                                }
                            });
                        }
                    }
                }
            };
            parentPage.getLabelProvider().addListener(resourceLabelListener);

            resourceLink.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    ModelNodeLabelProvider labelProvider = parentPage.getLabelProvider();
                    if (labelProvider != null) {
                        labelProvider.removeListener(resourceLabelListener);
                    }
                }
            });

            // Create tooltip
            ResourceToolTip toolTip = new ResourceToolTip(resourceLink);
            toolTip.setModelProvider(parentPage);
            toolTip.setResource(resource);

            // Create single click action
            resourceLink.addHyperlinkListener(new HyperlinkAdapter() {
                @Override
                public void linkActivated(HyperlinkEvent arg0) {
                    parentPage.openResource(resource);
                }
            });

            // Create context menu
            createContextMenu(resourceLink, new IMenuListener() {
                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    if ((currentResource != null) && !resource.equals(currentResource)) {
                        // add action "Open/View"
                        manager.add(new Action("View "
                                + parentPage.getLabelProvider().getText(resource)) {
                            public void run() {
                                parentPage.openResource(resource);
                            }
                        });
                        manager.add(new Separator());
                    }

                    if (resource.isURIResource()) {
                        // add action "Copy URI"
                        ImageDescriptor copyImage = CorePlugin.getDefault().getImageDescriptor(
                                CorePluginImages.IMG_COPY);
                        IAction copyAction = CopyResourceToClipboardHandler.createAction(
                                "Copy URI", resource);
                        copyAction.setImageDescriptor(copyImage);
                        manager.add(copyAction);
                    }

                    // add action "Relabel"
                    ImageDescriptor relabelImage = CorePlugin.getDefault().getImageDescriptor(
                            CorePluginImages.IMG_RENAME);
                    IAction relabelAction = RelabelSelectedResourceHandler.createAction(
                            "Relabel...", resource);
                    relabelAction.setImageDescriptor(relabelImage);
                    manager.add(relabelAction);

                    if (RenameSelectedResourceHandler.isEnabledFor(resource)) {
                        // add action "Set URI"
                        IAction newUriAction = new Action("Set URI...", null) {
                            @Override
                            public void run() {
                                Resource renamedResource = RenameSelectedResourceHandler
                                        .renameResource(resource);
                                if (renamedResource != null) {
                                    parentPage.openResource(renamedResource);
                                    parentPage.closeResource(resource);
                                }
                            }
                        };
                        manager.add(newUriAction);
                    }

                    manager.add(new Separator());

                    // add action "Close"
                    manager.add(new Action("Close") {
                        @Override
                        public void run() {
                            parentPage.closeResource(resource);
                        }
                    });

                    if (parentPage.getOpenResources().size() > 1) {
                        // add action "Close Others"
                        manager.add(new Action("Close Others") {
                            @Override
                            public void run() {
                                parentPage.closeAllResourcesBut(resource);
                            }
                        });

                        // add action "Close All"
                        manager.add(new Action("Close All") {
                            @Override
                            public void run() {
                                parentPage.closeAllResources();
                            }
                        });

                        manager.add(new Separator());

                        // add action "Open/View" for each open resource
                        for (final OntResource resource : parentPage.getOpenResources()) {
                            manager.add(new Action("View "
                                    + parentPage.getLabelProvider().getText(resource)) {
                                public void run() {
                                    parentPage.openResource(resource);
                                }
                            });
                        }
                    }
                }
            });

            DndUtils.addDragSupport(resourceLink, resource);
        }

        if (!openResources.isEmpty()) {
            boolean enabled = (calculateResourcesHiddenBelow(openResources.size(), maxResourceSlots) > 0);

            SquareButton arrowDown = new SquareButton(this, SWT.NONE);
            GridDataFactory.fillDefaults().hint(WIDTH, SWT.DEFAULT).grab(false, true)
                    .align(SWT.LEFT, SWT.END).applyTo(arrowDown);
            arrowDown.setBackground(backgroundColor);
            arrowDown.setInnerMarginHeight(0);
            arrowDown.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_ARROW_DOWN));
            arrowDown.setRoundedCorners(false);
            arrowDown.setEnabled(enabled);
            arrowDown.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    if (e.button == 1) {
                        setFirstIndexShown(settings.firstIndexShown + 1);
                    }
                }
            });
            createScrollResourcesContextMenu(arrowDown, resourceSlotsToShow, SWT.DOWN);
        }

        /*
         * if (mouseWheelListener == null) { mouseWheelListener = new
         * MouseWheelListener() {
         * 
         * @Override public void mouseScrolled(MouseEvent e) { // FIXME:
         * Apparently the mouse wheel can 'fall back' a bit // after scrolling,
         * meaning the direction is involuntarily // reversed for a moment. This
         * should be prevented by // ignoring such last-minute scroll actions or
         * by requiring // multiple mouse scroll events and acquiring the modal
         * // direction. int count = e.count; if (count > 0) {
         * setFirstIndexShown(settings.firstIndexShown - 2); } else {
         * setFirstIndexShown(settings.firstIndexShown + 2); } } };
         * addMouseWheelListener(mouseWheelListener); }
         */

        // Remove old paintListener, if existent
        if (paintListener != null) {
            removePaintListener(paintListener);
            paintListener = null;
        }
        // Add new paintListener to draw borders
        final int whiteResourceSlot = slotContainingCurrentResource;
        paintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setLineWidth(1);
                e.gc.setForeground(borderColor);
                e.gc.drawRectangle(e.x + e.width - 1, 0, BORDER_WIDTH, availableHeight);
                if (whiteResourceSlot >= 0) {
                    int start = NONSLOTS_HEIGHT_TOP + whiteResourceSlot * RESOURCE_SLOT_HEIGHT;
                    e.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                    e.gc.drawRectangle(e.x + e.width - 1, start, BORDER_WIDTH,
                            RESOURCE_SLOT_HEIGHT - 1);
                }
            }
        };
        addPaintListener(paintListener);

        if (DndUtils.getDropTarget(this) == null) {
            DropTarget dropTarget = new DropTarget(this, DND.DROP_MOVE | DND.DROP_COPY);
            dropTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
            dropTarget.addDropListener(new DropTargetAdapter() {
                @Override
                public void drop(DropTargetEvent event) {
                    if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                        Resource resource = (Resource) event.data;
                        parentPage.openResource(resource);
                    }
                }
            });
        }
    }

    private int calculateResourcesHiddenAbove() {
        return settings.firstIndexShown;
    }

    private int calculateResourcesHiddenBelow(int resourcesCount, int maxResourceSlots) {
        return Math.max(0, resourcesCount - (settings.firstIndexShown + maxResourceSlots));
    }

    private int calculateMaxResourceSlots() {
        if (availableHeight == SWT.DEFAULT) {
            return parentPage.getOpenResources().size();
        }
        int availableSlots = (availableHeight - NONSLOTS_HEIGHT) / RESOURCE_SLOT_HEIGHT;
        return Math.max(MINIMUM_SLOTS_SHOWN, availableSlots);
    }

    protected Menu createContextMenu(Control control, IMenuListener menuListener) {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(control);
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(menuListener);
        control.setMenu(menu);
        return menu;
    }

    protected Menu createScrollResourcesContextMenu(final Control control,
            final int itemCountPerPage, final int direction) {
        final int resourcesCount = parentPage.getOpenResources().size();
        final int maxResourceSlots = calculateMaxResourceSlots();

        IMenuListener listener = new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                String directionName = "up";
                String ultimateName = "top";
                if (direction == SWT.UP) {
                    if (calculateResourcesHiddenAbove() == 0) {
                        return;
                    }
                }
                else {
                    directionName = "down";
                    ultimateName = "bottom";
                    if (calculateResourcesHiddenBelow(resourcesCount, maxResourceSlots) == 0) {
                        return;
                    }
                }

                // Add option: Scroll a page
                String actionName = String.format("Scroll a page %s", directionName);
                manager.add(new Action(actionName) {
                    public void run() {
                        if (direction == SWT.UP) {
                            setFirstIndexShown(settings.firstIndexShown - itemCountPerPage);
                        }
                        else {
                            setFirstIndexShown(settings.firstIndexShown + itemCountPerPage);
                        }
                    }
                });

                // Add option: Scroll to ultimate item
                actionName = String.format("Scroll to %s resource", ultimateName);
                manager.add(new Action(actionName) {
                    public void run() {
                        setFirstIndexShown((direction == SWT.UP) ? 0 : resourcesCount);
                    }
                });

                /*
                 * if (SCROLL_STEPS_IN_CONTEXT_MENU.length == 0) { return; }
                 * 
                 * manager.add(new Separator());
                 * 
                 * for (int i = 0; i < SCROLL_STEPS_IN_CONTEXT_MENU.length; i++)
                 * { final int currentSteps = SCROLL_STEPS_IN_CONTEXT_MENU[i];
                 * 
                 * if ((direction == SWT.UP) && (calculateResourcesHiddenAbove()
                 * < currentSteps)) { return; } else if ((direction == SWT.DOWN)
                 * && (calculateResourcesHiddenBelow(resourcesCount,
                 * maxResourceSlots) < currentSteps)) { return; }
                 * 
                 * // Add option: Scroll up/down one item
                 * 
                 * String actionTitle = String.format("Scroll %s %d resources",
                 * directionName, currentSteps); if (currentSteps == 1) {
                 * actionTitle = String.format("Scroll %s", directionName); }
                 * manager.add(new Action(actionTitle) { public void run() { if
                 * (direction == SWT.UP) {
                 * setFirstIndexShown(settings.firstIndexShown - currentSteps);
                 * } else { setFirstIndexShown(settings.firstIndexShown +
                 * currentSteps); }
                 * 
                 * } }); }
                 */
            }
        };
        return createContextMenu(control, listener);
    }

    /**
     * Sets the first index shown to the given index. If that index is smaller
     * than 0, the index is set to 0. If it is larger than the available
     * indices, it is set to the largest available index. If the given index is
     * different from the already set index, a refresh is triggered and the
     * function returns true.
     */
    private boolean setFirstIndexShown(int index) {
        List<OntResource> openResources = parentPage.getOpenResources();
        int maxResourceSlots = calculateMaxResourceSlots();
        if (index + maxResourceSlots >= openResources.size()) {
            index = openResources.size() - maxResourceSlots;
        }

        if (index < 0) {
            index = 0;
        }

        if (index != settings.firstIndexShown) {
            settings.firstIndexShown = index;
            refresh();
            return true;
        }
        return false;
    }

    public void refresh() {
        for (Control child : getChildren()) {
            child.dispose();
        }
        createContent();
        Widgets.layoutControlUpToScrollableParent(this);
    }

    public void setResourceInDisplayedArea(OntResource resource) {
        List<OntResource> openResources = parentPage.getOpenResources();
        int index = openResources.indexOf(resource);
        if (index >= 0) {
            if (index < settings.firstIndexShown) {
                setFirstIndexShown(index);
            }
            else {
                int maxResourceSlots = calculateMaxResourceSlots();
                if (index >= settings.firstIndexShown + maxResourceSlots) {
                    setFirstIndexShown(index);
                }
            }
        }
    }

    public void setAvailableHeight(int height) {
        availableHeight = height;
        refresh();
    }

    @Override
    public void dispose() {
        if (!isDisposed()) {
            removePaintListener(paintListener);
            paintListener = null;
        }
        super.dispose();
    }
}

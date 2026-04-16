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

package com.semmtech.plugin.semmweb.core.intro;


import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.EditorHistory;
import org.eclipse.ui.internal.EditorHistoryItem;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.intro.config.IIntroContentProvider;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.Colors;


@SuppressWarnings("restriction")
public class RecentlyOpenedFilesContentProvider implements IIntroContentProvider {

    private final String EDITORS_PREFERENCE_PAGE_ID = "org.eclipse.ui.preferencePages.Editors";

    private EditorHistoryItem[] editorHistoryItems;
    private IFileEditorMapping[] fileEditorMappings;

    @Override
    public void init(IIntroContentProviderSite site) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench instanceof Workbench) {
            EditorHistory history = ((Workbench) workbench).getEditorHistory();
            editorHistoryItems = history.getItems();
        }

        IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
        fileEditorMappings = editorReg.getFileEditorMappings();
    }

    private IFileEditorMapping getFileEditorMapping(String fileName) {
        if (fileName != null) {
            for (IFileEditorMapping mapping : fileEditorMappings) {
                if (mapping.getExtension().length() < fileName.length()) {
                    int beginIndex = fileName.length() - mapping.getExtension().length();
                    if (fileName.substring(beginIndex).equals(mapping.getExtension())
                            && (fileName.charAt(beginIndex - 1) == '.')) {
                        return mapping;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void createContent(String id, PrintWriter out) {
    }

    @Override
    public void createContent(String id, Composite parent, FormToolkit toolkit) {
        String title = "Recently opened files";
        String description = "This section lists recently opened files. "
                + "The number of files to be remembered can be set in the editor preferences.";

        // Entire section
        Section outerSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
                | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        outerSection.descriptionVerticalSpacing = 10;
        outerSection.setText(title);
        outerSection.setDescription(description);
        outerSection.setLayoutData(new ColumnLayoutData());
        GridLayoutFactory.fillDefaults().applyTo(outerSection);

        // Content within the section, with vertical spacing of 5px
        Composite contentComposite = toolkit.createComposite(outerSection, SWT.NONE);
        contentComposite.setLayoutData(new ColumnLayoutData());
        outerSection.setClient(contentComposite);
        GridLayoutFactory.fillDefaults().spacing(0, 5).applyTo(contentComposite);

        // List within the section, with no vertical spacing
        Composite listComposite = toolkit.createComposite(contentComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(3, 5).spacing(0, 0).applyTo(listComposite);

        int maxItemsToDisplay = WorkbenchPlugin.getDefault().getPreferenceStore()
                .getInt(IPreferenceConstants.RECENT_FILES);
        int itemCount = 0;
        for (final EditorHistoryItem file : editorHistoryItems) {
            if (itemCount == maxItemsToDisplay) {
                break;
            }

            final IFileEditorMapping editorMapping = getFileEditorMapping(file.getName());
            if ((editorMapping == null) || (editorMapping.getDefaultEditor() == null)
                    || !exists(file) || !isWorkspaceResource(file) || isHidden(file)) {
                continue;
            }

            itemCount++;

            Composite itemComposite = toolkit.createComposite(listComposite, SWT.NONE);
            GridLayoutFactory.fillDefaults().margins(0, 0).spacing(5, 0).numColumns(2)
                    .applyTo(itemComposite);

            final String editorId = editorMapping.getDefaultEditor().getId();

            Color linkColor = Colors.getColor(Colors.SWT_LINK_BLUE);
            ImageHyperlink imageHyperlink = toolkit.createImageHyperlink(itemComposite, SWT.NONE);
            imageHyperlink.setImage(editorMapping.getImageDescriptor().createImage());
            imageHyperlink.setText(file.getName());
            imageHyperlink.setForeground(linkColor);

            imageHyperlink.addHyperlinkListener(new IHyperlinkListener() {
                private void openEditor(final IEditorInput input, final String id) {
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                .openEditor(input, id);
                    }
                    catch (PartInitException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void linkActivated(HyperlinkEvent e) {
                    if (!file.isRestored()) {
                        file.restoreState();
                    }
                    openEditor(file.getInput(), editorId);
                }

                @Override
                public void linkEntered(HyperlinkEvent e) {
                }

                @Override
                public void linkExited(HyperlinkEvent e) {
                }
            });

            String locationText = "";
            String location = getLocation(file);
            if (!Strings.isNullOrEmpty(location)) {
                locationText = String.format("(%s)", location);
            }
            Label locationLabel = toolkit.createLabel(itemComposite, locationText);
            locationLabel.setForeground(Colors.getColor(Colors.DARK_GRAY));
        }

        Link link = new Link(contentComposite, SWT.WRAP);
        link.setText("Click <a>here</a> to configure the editor settings.");
        GridDataFactory.fillDefaults().applyTo(link);
        link.setBackground(toolkit.getColors().getBackground());
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        EDITORS_PREFERENCE_PAGE_ID, null, null);
                dialog.open();
            }
        });

        outerSection.setExpanded(true);
    }

    private boolean exists(EditorHistoryItem item) {
        boolean exists = (getFile(item) != null);
        return exists;
    }

    /** Returns the file. If the file does not exist, null is returned. */
    private File getFile(EditorHistoryItem item) {
        String path = getPath(item);

        // Is the path absolute?
        if (path.contains(":")) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }

        // The path is probably relative to the workspace
        IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getRawLocation();
        path = rootPath.toString() + "/" + path;
        File file = new File(path);
        if (file.exists()) {
            return file;
        }

        return null;
    }

    private boolean isWorkspaceResource(EditorHistoryItem item) {
        boolean result = false;

        try {
            File file = getFile(item);
            if (file != null) {
                IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                IFile[] files = workspaceRoot.findFilesForLocationURI(file.toURI());
                result = (files.length > 0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean isHidden(EditorHistoryItem item) {
        File file = getFile(item);
        if (file == null || file.isHidden()) {
            return true;
        }

        try {
            Iterator<Path> iter = file.toPath().iterator();
            while (iter.hasNext()) {
                Path pathSegment = iter.next();
                if (pathSegment.toString().startsWith(".")) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getLocation(EditorHistoryItem item) {
        String name = item.getName();
        String path = getPath(item);

        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(path)) {
            return null;
        }
        if (!path.endsWith(name) || (path.length() <= name.length() + 1)) {
            return null;
        }

        int endIndexLocation = path.length() - (name.length() + 1);
        char separator = path.charAt(endIndexLocation);
        if ((separator != '\\') && (separator != '/')) {
            return null;
        }

        return path.substring(0, endIndexLocation);
    }

    private String getPath(EditorHistoryItem item) {
        return item.getToolTipText();
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }
}

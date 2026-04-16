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

package com.semmtech.plugin.semmweb.laces.ldp.views;


import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.part.PluginDropAdapter;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.ui.dialogs.PreferencesUtil;

import com.semmtech.plugin.semmweb.core.dnd.PublicationTransfer;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.PublicationInfo;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.wizards.DownloadOntModelWizard;
import com.semmtech.plugin.semmweb.laces.ldp.LDPPlugin;
import com.semmtech.plugin.semmweb.laces.ldp.handlers.PublishOntologyHandler;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPGroup;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPItem;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPPublication;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPRepository;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPServer;
import com.semmtech.plugin.semmweb.laces.ldp.preferences.LDPPreference;
import com.semmtech.plugin.semmweb.laces.ldp.preferences.LDPPreferencePage;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;
import com.semmtech.ui.plugin.viewers.PendingElement;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class ServerView extends ViewPart implements IPropertyChangeListener {
    public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.views.servers";

    private static ServerView singleton;

    private Tree tree;
    private TreeViewer viewer;
    private ServersTreeLabelProvider labelProvider;

    private final class FindChildrenJob extends Job {
        private final Object parent;
        private final TreeViewer viewer;
        private List<Object> children;
        private boolean hasGrandChildren;

        public FindChildrenJob(String name, TreeViewer viewer, Object parent) {
            super(name);
            this.parent = parent;
            this.viewer = viewer;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            children = Lists.newArrayList();
            hasGrandChildren = true;
            try {
                if (parent instanceof LDPServer) {
                    monitor.beginTask("Retrieving Groups", 2);
                    monitor.subTask("Retrieving LDP groups...");

                    LDPServer server = (LDPServer) parent;
                    for (LDPGroup group : server.listGroups()) {
                        children.add(group);
                    }
                }
                else if (parent instanceof LDPGroup) {
                    monitor.beginTask("Retrieving Groups and Repositories", 2);
                    monitor.subTask("Retrieving LDP subgroups and repositories...");

                    LDPGroup group = (LDPGroup) parent;
                    LDPServer server = group.getServer();
                    for (LDPGroup subGroup : server.listGroups(group)) {
                        children.add(subGroup);
                    }
                    for (LDPRepository repo : server.listRepositories(group)) {
                        children.add(repo);
                    }
                }
                else if (parent instanceof LDPRepository) {
                    monitor.beginTask("Retrieving Publications", 2);
                    monitor.subTask("Retrieving LDP publications...");

                    LDPRepository repo = (LDPRepository) parent;
                    LDPServer server = repo.getServer();
                    for (LDPPublication publication : server.listPublications(repo)) {
                        children.add(publication);
                    }
                    hasGrandChildren = false;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            monitor.worked(1);

            monitor.subTask("Updating viewer...");
            final int childCount = children.size();
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    viewer.setChildCount(parent, childCount);
                    for (int i = 0; i < childCount; i++) {
                        Object child = children.get(i);
                        viewer.replace(parent, i, child);
                        viewer.setChildCount(child, (hasGrandChildren ? 1 : 0));
                    }
                }
            });
            monitor.worked(1);
            monitor.done();
            return Status.OK_STATUS;
        }
    }

    private final class ServersTreeContentProvider extends LazyTreeContentProvider {
        @Override
        public void updateElement(Object parent, int index) {
            if (parent instanceof List<?>) {
                List<?> collection = (List<?>) parent;
                Object object = collection.get(index);
                if (object instanceof LDPServer) {
                    LDPServer server = (LDPServer) object;
                    viewer.replace(parent, index, server);
                    viewer.setChildCount(server, 2);
                }
            }
            else if (parent instanceof LDPServer) {
                if (index == 0) {
                    LDPServer server = (LDPServer) parent;
                    final PendingElement pending = new PendingElement(server);
                    viewer.replace(parent, 0, pending);
                    viewer.setChildCount(pending, 0);
                    Job job = new FindChildrenJob("Retrieving groups from Laces LDP", viewer,
                            server);
                    job.schedule();
                }
            }
            else if (parent instanceof LDPGroup) {
                if (index == 0) {
                    LDPGroup group = (LDPGroup) parent;
                    final PendingElement pending = new PendingElement(group);
                    viewer.replace(parent, 0, pending);
                    viewer.setChildCount(pending, 0);
                    Job job = new FindChildrenJob(
                            "Retrieving subgroups and repositories from Laces LDP", viewer, group);
                    job.schedule();
                }
            }
            else if (parent instanceof LDPRepository) {
                if (index == 0) {
                    LDPRepository repo = (LDPRepository) parent;
                    final PendingElement pending = new PendingElement(repo);
                    viewer.replace(parent, 0, pending);
                    viewer.setChildCount(pending, 0);
                    Job job = new FindChildrenJob("Retrieving publications from Laces LDP", viewer,
                            repo);
                    job.schedule();
                }
            }
        }
    }

    private final class ServersTreeLabelProvider extends StyledCellLabelProvider {
        private final Map<ImageDescriptor, Image> createdImages = Maps.newHashMap();

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
                    .getAdapter(element, IWorkbenchAdapter.class);
            String text = null;
            Image image = null;

            if (adapter != null) {
                text = adapter.getLabel(element);
                ImageDescriptor descriptor = adapter.getImageDescriptor(element);
                if (!createdImages.containsKey(descriptor) && descriptor != null) {
                    createdImages.put(descriptor, descriptor.createImage());
                }
                image = createdImages.get(descriptor);
            }
            else if (element != null && element instanceof String) {
                text = element.toString();
            }

            StyledString styledText = new StyledString();
            if (text != null && text.length() > 0) {
                styledText.append(text);
            }
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            cell.setImage(image);

            super.update(cell);
        }

        @Override
        public void dispose() {
            for (Image image : createdImages.values()) {
                image.dispose();
            }
            createdImages.clear();
            super.dispose();
        }
    }

    public ServerView() {
        super();
        singleton = this;
    }

    private class ServerDragSourceListener extends DragSourceAdapter {
        private TreeViewer viewer;

        public ServerDragSourceListener(TreeViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void dragSetData(DragSourceEvent event) {
            if (PublicationTransfer.getInstance().isSupportedType(event.dataType)
                    || PluginTransfer.getInstance().isSupportedType(event.dataType)) {
                ISelection selection = viewer.getSelection();
                List<LDPPublication> publications = Selections.retrieveAllAsType(selection,
                        LDPPublication.class);
                if (!publications.isEmpty()) {
                    LDPPublication publication = publications.get(0);
                    PublicationInfo pubInfo = new PublicationInfo();
                    pubInfo.id = publication.getID();
                    pubInfo.name = publication.getName();
                    pubInfo.uri = publication.getURI();
                    pubInfo.versioningModeId = (publication.fullInfo != null) ? publication.fullInfo.versioningMode
                            : null;
                    if (PublicationTransfer.getInstance().isSupportedType(event.dataType)) {
                        event.data = pubInfo;
                    }
                    if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
                        event.data = new PluginTransferData(
                                "com.semmtech.plugin.semmweb.core.dnd.publicationDrop",
                                PublicationTransfer.getInstance().javaToByteArray(pubInfo));
                    }
                }
            }
        }
    }

    private class ServerDropAdapter extends PluginDropAdapter {

        public ServerDropAdapter(StructuredViewer viewer) {
            super(viewer);
        }

        @Override
        public boolean validateDrop(Object target, int operation, TransferData transferType) {
            // if (target != null && target instanceof LDPRepository) {
            if (PluginTransfer.getInstance().isSupportedType(transferType)) {
                // TODO: not all files are OK; only models
                return true;
            }
            // }
            return false;
        }

        @Override
        public void drop(DropTargetEvent event) {
            // identify dragged item
            IPartService ips = (IPartService) getSite().getService(IPartService.class);
            ISelection selection = ips.getActivePart().getSite().getSelectionProvider()
                    .getSelection();
            IModel droppedModel = Selections.retrieveFirstAsType(selection, IModel.class);
            IFile droppedFile = Selections.retrieveFirstAsType(selection, IFile.class);
            if (droppedModel != null) {
                droppedFile = (IFile) droppedModel.getResource();
            }
            if (droppedFile != null) {
                Object target = getCurrentTarget();
                performFileDrop(droppedFile, target);
            }
        }

        protected void performFileDrop(IFile file, Object target) {
            // ignoring target; publish to server via wizard instead
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            PublishOntologyHandler handler = new PublishOntologyHandler();
            try {
                handler.execute(file, shell);
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        labelProvider = new ServersTreeLabelProvider();
        viewer = new TreeViewer(parent, SWT.VIRTUAL);
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(new ServersTreeContentProvider());
        viewer.setLabelProvider(labelProvider);
        tree = viewer.getTree();

        final DragSource dndSource = new DragSource(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        dndSource.addDragListener(new ServerDragSourceListener(viewer));
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (Selections.hasAllOfType(event.getSelection(), LDPPublication.class)) {
                    Transfer[] transferTypes = new Transfer[] { PublicationTransfer.getInstance(),
                            PluginTransfer.getInstance() };
                    dndSource.setTransfer(transferTypes);
                }
                else {
                    dndSource.setTransfer(new Transfer[] {});
                }
            }
        });

        Transfer[] transfers = new Transfer[] { PluginTransfer.getInstance() };
        viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, transfers, new ServerDropAdapter(
                viewer));

        createContextMenu();
        refreshViewer();
    }

    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, viewer);
        getSite().setSelectionProvider(viewer);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        LDPPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        LDPPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        labelProvider.dispose();
        super.dispose();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(LDPPreference.PREFERENCE_LDP_SERVERS)) {
            refreshViewer();
        }
    }

    private void refreshViewer() {
        List<LDPServer> servers = LDPPreference.getServers();
        viewer.setInput(servers);
        tree.setItemCount(servers.size());
    }

    @Override
    public void setFocus() {
        tree.setFocus();
    }

    public static class RefreshViewHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.refreshServer";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.refreshViewer();
            }
            return null;
        }
    }

    public static class RefreshSelectionHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.refreshSelection";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton == null) {
                return null;
            }

            ISelection selection = HandlerUtil.getCurrentSelection(event);
            LDPItem ldpItem = Selections.retrieveFirstAsType(selection, LDPItem.class);
            if (ldpItem == null) {
                singleton.refreshViewer();
            }
            else if (!(ldpItem instanceof LDPPublication)) {
                ServersTreeContentProvider stcp = (ServersTreeContentProvider) singleton.viewer
                        .getContentProvider();
                singleton.viewer.setChildCount(ldpItem, 1);
                stcp.updateElement(ldpItem, 0);
            }

            return null;
        }
    }

    public static class CopyURI extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.copyURI";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            LDPPublication pub = Selections.retrieveFirstAsType(selection, LDPPublication.class);
            String uri = pub.getURI();

            Clipboard clipboard = new Clipboard(Display.getCurrent());
            clipboard.setContents(new Object[] { uri },
                    new Transfer[] { TextTransfer.getInstance() });
            clipboard.dispose();
            return null;
        }
    }

    public static class ViewOnline extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.viewOnline";
        protected static final String browserId = "com.semmtech.plugin.semmweb";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            String url = null;
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            LDPItem ldpItem = Selections.retrieveFirstAsType(selection, LDPItem.class);
            if (ldpItem != null) {
                url = ldpItem.getURL();
            }
            else {
                LDPServer server = Selections.retrieveFirstAsType(selection, LDPServer.class);
                if (server != null) {
                    url = server.getServerUrl();
                }
            }

            if (url != null) {
                try {
                    IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
                            .getBrowserSupport();
                    IWebBrowser browser = browserSupport.getExternalBrowser();
                    browser.openURL(new URL(url));
                }
                catch (Exception e) {
                    // Failed to open url
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static class ImportPublication extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.importPublication";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            ISelection selection = HandlerUtil.getCurrentSelection(event);
            LDPPublication pub = Selections.retrieveFirstAsType(selection, LDPPublication.class);

            if (pub == null || pub.getURI() == null) {
                return null;
            }

            // obtain filename suggestion, based on uri
            String uriVersionless = pub.getURLwithoutVersionSegment();
            String filename = uriVersionless.substring(uriVersionless.lastIndexOf("/") + 1)
                    + ".ttl";
            // TODO: obtain folder suggestion (i.e., which Semantic Project)
            IFolder folder = null;

            String uri = pub.getURI();
            DownloadOntModelWizard wizard = new DownloadOntModelWizard(uri, folder, filename);

            // Create wizard dialog.
            Shell parentShell = HandlerUtil.getActiveShell(event);
            WizardDialog dialog = new WizardDialog(parentShell, wizard);
            dialog.create();

            // Open wizard.
            dialog.open();
            return null;
        }
    }

    public static class EnterCredentials extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.laces.ldp.commands.enterCredentials";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(shell,
                    LDPPreferencePage.ID, null, null);
            if (pref != null) {
                pref.open();
            }
            return null;
        }
    }
}

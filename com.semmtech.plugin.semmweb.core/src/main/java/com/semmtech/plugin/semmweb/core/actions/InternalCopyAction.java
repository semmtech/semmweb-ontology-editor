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

package com.semmtech.plugin.semmweb.core.actions;


import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ResourceTransfer;


/**
 * Copied from
 * {@link org.eclipse.ui.internal.navigator.resources.actions.CopyAction}
 */
public class InternalCopyAction extends SelectionListenerAction {

    /**
     * The id of this action.
     */
    public static final String ID = PlatformUI.PLUGIN_ID + ".CopyAction"; //$NON-NLS-1$

    /**
     * The shell in which to show any dialogs.
     */
    private Shell shell;

    /**
     * System clipboard
     */
    private Clipboard clipboard;

    /**
     * Creates a new action.
     * 
     * @param shell
     *            the shell for any dialogs
     * @param clipboard
     *            a platform clipboard
     */
    public InternalCopyAction(Shell shell, Clipboard clipboard) {
        super("Copy");
        Assert.isNotNull(shell);
        Assert.isNotNull(clipboard);
        this.shell = shell;
        this.clipboard = clipboard;
        setToolTipText("Copy selected resource(s)");
        setId(InternalCopyAction.ID);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, "CopyHelpId"); //$NON-NLS-1$
        // TODO INavigatorHelpContextIds.COPY_ACTION);
    }

    /**
     * The <code>CopyAction</code> implementation of this method defined on
     * <code>IAction</code> copies the selected resources to the clipboard.
     */
    public void run() {
        List<?> selectedResources = getSelectedResources();
        IResource[] resources = selectedResources.toArray(new IResource[selectedResources.size()]);

        // Get the file names and a string representation
        final int length = resources.length;
        int actualLength = 0;
        String[] fileNames = new String[length];
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            IPath location = resources[i].getLocation();
            // location may be null. See bug 29491.
            if (location != null) {
                fileNames[actualLength++] = location.toOSString();
            }
            if (i > 0) {
                buf.append("\n"); //$NON-NLS-1$
            }
            buf.append(resources[i].getName());
        }
        // was one or more of the locations null?
        if (actualLength < length) {
            String[] tempFileNames = fileNames;
            fileNames = new String[actualLength];
            for (int i = 0; i < actualLength; i++) {
                fileNames[i] = tempFileNames[i];
            }
        }
        setClipboard(resources, fileNames, buf.toString());
    }

    /**
     * Set the clipboard contents. Prompt to retry if clipboard is busy.
     * 
     * @param resources
     *            the resources to copy to the clipboard
     * @param fileNames
     *            file names of the resources to copy to the clipboard
     * @param names
     *            string representation of all names
     */
    private void setClipboard(IResource[] resources, String[] fileNames, String names) {
        try {
            // set the clipboard contents
            if (fileNames.length > 0) {
                clipboard.setContents(new Object[] { resources, fileNames, names },
                        new Transfer[] { ResourceTransfer.getInstance(),
                                FileTransfer.getInstance(), TextTransfer.getInstance() });
            }
            else if (resources.length > 0) {
                clipboard.setContents(new Object[] { resources, names }, new Transfer[] {
                        ResourceTransfer.getInstance(), TextTransfer.getInstance() });
            }
        }
        catch (SWTError e) {
            if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
                throw e;
            }
            if (MessageDialog.openQuestion(shell, "Problem with copy title", // TODO ResourceNavigatorMessages.CopyToClipboardProblemDialog_title,  //$NON-NLS-1$
                    "Problem with copy.")) { //$NON-NLS-1$
                setClipboard(resources, fileNames, names);
            }
        }
    }

    /**
     * The <code>CopyAction</code> implementation of this
     * <code>SelectionListenerAction</code> method enables this action if one or
     * more resources of compatible types are selected.
     */
    protected boolean updateSelection(IStructuredSelection selection) {
        if (!super.updateSelection(selection)) {
            return false;
        }

        if (getSelectedNonResources().size() > 0) {
            return false;
        }

        List<?> selectedResources = getSelectedResources();
        if (selectedResources.size() == 0) {
            return false;
        }

        boolean projSelected = selectionIsOfType(IResource.PROJECT);
        boolean fileFoldersSelected = selectionIsOfType(IResource.FILE | IResource.FOLDER);
        if (!projSelected && !fileFoldersSelected) {
            return false;
        }

        // selection must be homogeneous
        if (projSelected && fileFoldersSelected) {
            return false;
        }

        // must have a common parent
        IContainer firstParent = ((IResource) selectedResources.get(0)).getParent();
        if (firstParent == null) {
            return false;
        }

        Iterator<?> resourcesEnum = selectedResources.iterator();
        while (resourcesEnum.hasNext()) {
            IResource currentResource = (IResource) resourcesEnum.next();
            if (!currentResource.getParent().equals(firstParent)) {
                return false;
            }
            // resource location must exist
            if (currentResource.getLocationURI() == null) {
                return false;
            }
        }

        return true;
    }

}

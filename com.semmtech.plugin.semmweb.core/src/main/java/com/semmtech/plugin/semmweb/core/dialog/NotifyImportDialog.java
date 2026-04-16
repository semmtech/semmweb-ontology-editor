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

package com.semmtech.plugin.semmweb.core.dialog;


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.cache.CacheModelJob;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog.ChangeEvent;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProject;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Simone Rondelli
 * 
 */
public class NotifyImportDialog extends MessageComboDialog {

    private static Logger logger = Logger.getLogger(NotifyImportDialog.class);

    private static final String TITLE = "Import Model";
    private static final String MESSAGE = "One of the ontologies within this model requires the following model to be imported in order to be correctly interpreted. The time to process this import may vary and may take some time.";
    private static final String COMBO_MESSAGE = "Select action taken upon notifications for this model:";

    private static final String[] NOTIFY_OPTIONS = new String[] {
            "Always notify on import of this model",
            "Ignore notify on import of this model until exit",
            "Always ignore notification on import of this model" };

    private static final int OPTION_NOTIFY_ALWAYS = 0;

    /*
     * These two values are relatives to the position of the button's labels. I
     * used different values from the default one to make possible to have the
     * OK button as last button.
     */
    private static final int BUTTON_CANCEL = 0; // Window.CANCEL = 1
    private static final int BUTTON_OK = 1; // Window.OK = 0

    private final IProject project;
    private final String publicURI;

    private boolean applyNotifyActionToAll;
    private boolean cacheModel;
    private boolean importChanged;

    private Composite parent;
    private Composite detailsComposite;
    private Button allCheckbox;
    private Button cacheCheckbox;

    private OntDocumentManager documentManager;

    /**
     * This variable is initialized in the constructor with the value contained
     * in the semanticProject. After that if the alternateUrl changes this
     * variable is set with the value provided by the
     * {@link WorkspaceOntologySpecDialog} . In this way we evict to wait the
     * full rebuild of the project to have the importType.
     */
    private ImportType importType;

    public NotifyImportDialog(Shell parentShell, IProject project, String publicUri,
            boolean applyNotifyActionToAll) {
        super(parentShell, TITLE, null, MESSAGE, SWT.ICON_INFORMATION, new String[] { "Cancel",
                "OK" }, BUTTON_OK, COMBO_MESSAGE,
                (project == null) ? new String[] { NOTIFY_OPTIONS[0] } : NOTIFY_OPTIONS,
                OPTION_NOTIFY_ALWAYS);
        this.project = project;
        this.publicURI = publicUri;
        this.applyNotifyActionToAll = applyNotifyActionToAll;
        cacheModel = false;
        importChanged = false;
        documentManager = null;

        SemanticProject semanticProject = SemanticProjectManager.getSemanticProjectManager(project)
                .obtainProject();

        importType = (semanticProject != null) ? semanticProject.getImportType(publicURI)
                : ImportType.WEB_REFERENCE;
    }

    private Control createDetails() {
        Widgets.disposeIfExists(detailsComposite);

        Composite fixedComposite = new Composite(parent, SWT.NONE);
        fixedComposite.setLayout(new FillLayout());

        detailsComposite = new Composite(fixedComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(4, 8).extendedMargins(0, 8, 0, 0)
                .applyTo(detailsComposite);

        Label iconLabel = new Label(detailsComposite, SWT.NONE);
        iconLabel.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_OWL_ONTOLOGY));
        GridDataFactory.fillDefaults().indent(8, 0).applyTo(iconLabel);

        CLabel clabel = new CLabel(detailsComposite, SWT.NONE);
        clabel.setText(publicURI);
        GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(420, SWT.DEFAULT)
                .applyTo(clabel);

        if (project == null) {
            return parent;
        }

        Link link = new Link(detailsComposite, SWT.NONE);
        link.setText("Location used (change mapping <a>here</a>):");
        link.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                OntologyFormEditor.updateDocumentManager(project, publicURI, false, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        if (event instanceof ChangeEvent) {
                            ChangeEvent ontSpecEvt = (ChangeEvent) event;
                            importType = ontSpecEvt.getImportType();
                            if (importType == null) {
                                importType = ImportType.WEB_REFERENCE;
                            }
                        }
                        // TODO: check if something is really changed
                        importChanged = true;
                        documentManager = (OntDocumentManager) event.data;
                        createDetails();
                        resizeShell();
                    }
                });
            }
        });
        GridDataFactory.fillDefaults().span(2, 1).applyTo(link);

        iconLabel = new Label(detailsComposite, SWT.NONE);
        iconLabel.setImage(ImportURLUtils.getAltUrlIcon(importType));

        GridDataFactory.fillDefaults().indent(8, 0).applyTo(iconLabel);

        String altUrl = DocumentManagerPreference.fromProject(project).getAltURL(publicURI, true);
        String prettyAltUrl = ImportURLUtils.getAltUrlText(importType, altUrl);

        clabel = new CLabel(detailsComposite, SWT.NONE);
        clabel.setText(prettyAltUrl);
        GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).hint(420, SWT.DEFAULT)
                .applyTo(clabel);

        if (importType == ImportType.HD_REFERENCE || importType == ImportType.WEB_REFERENCE) {

            cacheCheckbox = new Button(detailsComposite, SWT.CHECK);
            cacheCheckbox.setText("Cache model in project");
            GridDataFactory.fillDefaults().span(2, 1).applyTo(cacheCheckbox);
        }
        return parent;
    }

    @Override
    protected Control createPostMessageArea(Composite parent) {
        this.parent = parent;
        createDetails();
        return parent;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == BUTTON_OK) {
            if (!Widgets.isNullOrDisposed(cacheCheckbox) && cacheCheckbox.getSelection()) {
                cacheModel = true;
            }

            // pass the real value of the OK button
            super.buttonPressed(Window.OK);
        }
        else if (buttonId == BUTTON_CANCEL) {
            // pass the real value of the CANCEL button
            super.buttonPressed(Window.CANCEL);
        }
    }

    /**
     * Cache the model only if it has been explicitly asked by the user. The
     * reason why this job isn't inside the buttonPressed function is that it
     * hangs the interface
     * 
     * @return true if the model has been cached false otherwise
     */
    public boolean runCacheJob(Shell shell) {
        if (cacheModel) {
            try {
                CacheModelJob cacheJob = new CacheModelJob(project, publicURI, false, shell);
                cacheJob.setUser(true);
                // even though the INTERACTIVE priority should be used for other
                // kind of job in this case the job hangs the interface so it
                // must finish ASAP
                cacheJob.setPriority(Job.INTERACTIVE);
                cacheJob.schedule();
                cacheJob.join();
                return true;
            }
            catch (Exception e) {
                String message = "An error occurred while caching the model " + publicURI;
                logger.error(message, e);
                MessageDialog.openError(shell, "Cache Error", message);
            }
        }
        return false;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        if (project != null) {
            allCheckbox = new Button(container, SWT.CHECK);
            // Apply to all within this single read operation
            allCheckbox.setText("Apply action to all model imports");
            allCheckbox.setSelection(applyNotifyActionToAll);
            GridDataFactory.swtDefaults().hint(SWT.DEFAULT, SWT.DEFAULT).applyTo(allCheckbox);
            allCheckbox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    applyNotifyActionToAll = allCheckbox.getSelection();
                }

            });
        }
        return container;
    }

    public boolean isApplyNotifyActionToAll() {
        return applyNotifyActionToAll;
    }

    /**
     * The returned documentManager won't be null only if the altUrl has been
     * changed. In that case the created documentManger should be used by the
     * caller of this class
     */
    public OntDocumentManager getDocumentManager() {
        return documentManager;
    }

    public boolean isImportChanged() {
        return importChanged;
    }

}

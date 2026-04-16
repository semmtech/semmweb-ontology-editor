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

package com.semmtech.plugin.semmweb.core.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.net.URIUtils;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.RepositoryInfo;
import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.VersioningMode;
import com.semmtech.plugin.semmweb.core.extensionpoint.PublicationExtensions;
import com.semmtech.ui.plugin.widgets.Widgets;
import com.semmtech.ui.plugin.wizard.BaseWizardPage;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class PublicationLocationWizardPage extends BaseWizardPage {

    private static final String PAGE_NAME = "publicationTargetPage";
    private static final String PAGE_TITLE = "Publication Location";
    private static final String PAGE_DESCRIPTION = "This page will help you select the location for the publication.";

    private static final int LABEL_WIDTH = 85;
    private static final int BUTTON_WIDTH = 75;

    private List<IPublisher> publishers = PublicationExtensions.findPublishers();
    private Combo serverCombo;
    private Combo repositoryCombo;
    private Combo versioningCombo;
    private Label versioningLabel;
    private Text versioningCustomText;
    private IPublisher publisher;

    private Label ontologyUriValue;
    private Optional<Boolean> ontologyExists;

    /**
     * The URL stored in Resource metadata. It contains server, repository and
     * path. Used in case of relocating a model to fill automatically the
     * fields.
     */
    private String ontologySourceUri;
    private String ontologyUri;
    private String publicationServer;
    private RepositoryInfo repository;
    private String path;
    private VersioningMode versioning;
    private String customVersionLabel;

    private Label ontologyUriLabel;

    private boolean checkingExistence;
    private boolean checkingRepositories;
    private Text pathText;
    private ModifyListener pathModifyListener;
    private Button checkButton;
    private List<RepositoryInfo> repositories;

    public PublicationLocationWizardPage() {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        ontologyExists = Optional.absent();
    }

    public PublicationLocationWizardPage(String url) {
        this();
        ontologySourceUri = url;
        ontologyUri = url;
    }

    public PublicationLocationWizardPage(String url, VersioningMode vm) {
        this();
        ontologySourceUri = url;
        ontologyUri = url;
        versioning = vm;
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 12;
        container.setLayout(layout);

        Label label = new Label(container, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 500;
        label.setLayoutData(layoutData);
        if (Strings.isNullOrEmpty(ontologySourceUri)) {
            label.setText("In order to publish this ontology you must specify the publication server onto which this ontology will be published, along with the desired repository, and the publication name (or rather, a URL segment that will be associated with your publication).\r\n\r\n");
        }
        else {
            label.setText("Please confirm the location of publication.");
        }

        label = new Label(container, SWT.NONE);
        label.setText("Server:");
        GridDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(label);

        serverCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
        serverCombo.setLayoutData(layoutData);

        serverCombo.setItems(createPublisherItems(publishers));
        if (Strings.isNullOrEmpty(ontologySourceUri)) {
            serverCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    publisher = null;
                    int index = serverCombo.getSelectionIndex();
                    if (index > 0) {
                        publisher = publishers.get(index - 1);
                    }
                    publisherChanged();
                }
            });
        }
        else {
            for (int i = 0; i < publishers.size(); i++) {
                if (publishers.get(i).serves(ontologySourceUri)) {
                    serverCombo.select(i + 1);
                    publisher = publishers.get(i);
                }
            }
            serverCombo.setEnabled(false);
        }

        if (Strings.isNullOrEmpty(ontologySourceUri)) {
            label = new Label(container, SWT.NONE);
            label.setText("Repository:");
            GridDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(label);

            repositoryCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
            repositoryCombo.setLayoutData(layoutData);

            repositoryCombo.setItems(new String[] {});
            repositoryCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    int index = repositoryCombo.getSelectionIndex();
                    repository = (RepositoryInfo) repositoryCombo.getData(repositoryCombo
                            .getItem(index));
                    updateOntologyURI();
                    validatePage();
                }
            });

            label = new Label(container, SWT.NONE);
            label.setText("Publication:");
            GridDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(label);

            Composite pathComposite = new Composite(container, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(pathComposite);
            GridDataFactory.fillDefaults().applyTo(pathComposite);

            pathText = new Text(pathComposite, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(pathText);
            pathModifyListener = (new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    path = pathText.getText();
                    int position = pathText.getCaretPosition();
                    int size = path.length();

                    path = sanitizePath(path);

                    pathText.removeModifyListener(pathModifyListener);
                    pathText.setText(path);
                    pathText.setSelection(position - (size - path.length()));
                    pathText.addModifyListener(pathModifyListener);

                    updateOntologyURI();
                    ontologyExists = Optional.absent();
                    validatePage();
                }
            });
            pathText.addModifyListener(pathModifyListener);

            checkButton = new Button(pathComposite, SWT.PUSH);
            checkButton.setText("Check...");
            checkButton.setEnabled(!Strings.isNullOrEmpty(publicationServer) && repository != null
                    && !Strings.isNullOrEmpty(path));
            GridDataFactory.swtDefaults().hint(BUTTON_WIDTH, SWT.DEFAULT).applyTo(checkButton);
            checkButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    validatePage(); // performs checkOntologyExistence as well
                }
            });
        }

        label = new Label(container, SWT.NONE);

        ontologyUriLabel = new Label(container, SWT.NONE);
        ontologyUriLabel.setText("The ontology should be published at the following URL:");
        ontologyUriLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false,
                false, 1, 1));

        label = new Label(container, SWT.NONE);

        ontologyUriValue = new Label(container, SWT.NONE);
        ontologyUriValue.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,
                1, 1));
        if (!Strings.isNullOrEmpty(ontologySourceUri)) {
            ontologyUriValue.setText(ontologySourceUri);
        }

        versioningLabel = new Label(container, SWT.NONE);
        versioningLabel.setText("Versioning:");
        versioningLabel.setVisible(false);
        GridDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(label);

        versioningCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
        versioningCombo.setLayoutData(layoutData);
        versioningCombo.setVisible(false);

        if (versioning != null) {
            versioningCombo.setItems(new String[] { versioning.name });
            versioningCombo.setData(versioning.name, versioning);
            versioningCombo.select(0);
            versioningCombo.setEnabled(false);
        }
        else {
            versioningCombo.setItems(new String[] {});
            versioningCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    int index = versioningCombo.getSelectionIndex();
                    versioning = (VersioningMode) versioningCombo.getData(versioningCombo
                            .getItem(index));
                    validatePage();
                }
            });
        }

        label = new Label(container, SWT.NONE);
        GridDataFactory.swtDefaults().hint(LABEL_WIDTH, SWT.DEFAULT).applyTo(label);

        versioningCustomText = new Text(container, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(versioningCustomText);
        versioningCustomText.setVisible(false);
        versioningCustomText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                customVersionLabel = versioningCustomText.getText();
                validatePage();
            }
        });

        validatePage();
        setControl(container);

        if (Strings.isNullOrEmpty(ontologySourceUri)) {
            pathText.setText(Strings.nullToEmpty(path));
        }

        // if we are modifying an existent server
        if (publicationServer != null) {
            applyPublicationServer(publicationServer);
        }
    }

    protected String sanitizePath(String path) {
        path = path.replaceAll(" ", "-").toLowerCase();

        List<String> illegalChars = Lists.newArrayList();
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '-') {
                illegalChars.add("" + c);
            }
        }

        if (!illegalChars.isEmpty()) {
            for (String illegal : illegalChars) {
                path = path.replace(illegal, "");
            }
        }
        return path;
    }

    protected void publisherChanged() {
        publicationServer = (publisher != null) ? publisher.getServerURL() : null;
        retrieveVersioningOptions();
        retrieveRepositoryList();
        updateOntologyURI();
        validatePage();
    }

    private void retrieveRepositoryList() {
        repositories = Lists.newArrayList();
        if (publisher == null) {
            updateRepositoryList();
        }
        else {
            IRunnableWithProgress operation = new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException {
                    monitor.beginTask("Retrieving Repositories from Publication Server", 1);
                    repositories = publisher.listWritableRepositories();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (repositories.size() == 0) {
                                MessageDialog
                                        .openInformation(
                                                getShell(),
                                                "Repositories",
                                                "No writable repositories have been found on the selected publication server. Please select another server and contact the server's administrator.");
                            }
                            updateRepositoryList();
                            checkingRepositories = false;
                        }
                    });
                    monitor.worked(1);
                    monitor.done();
                }
            };
            try {
                IWizardContainer container = getContainer();
                if (container != null && !checkingRepositories) {
                    // This boolean is used to prevent multiple executions of
                    // the operation!
                    checkingRepositories = true;
                    container.run(false, false, operation);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
                Throwable realException = e.getTargetException();
                Shell shell = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                        .getShell();
                MessageDialog.openError(shell, "Error", realException.getMessage());
            }
        }
    }

    private void updateRepositoryList() {
        String[] items = new String[repositories.size()];
        RepositoryInfo[] repoInfo = new RepositoryInfo[repositories.size()];
        repositoryCombo.setItems(new String[] {});
        for (ListIterator<RepositoryInfo> iter = repositories.listIterator(); iter.hasNext();) {
            int index = iter.nextIndex();
            RepositoryInfo repo = iter.next();
            String repoUriText = Strings.nullToEmpty(repo.uri);
            if (repoUriText.startsWith(publisher.getServerURL())) {
                repoUriText = repoUriText.substring(publisher.getServerURL().length());
            }
            if (repoUriText.startsWith(publisher.getBaseURI())) {
                repoUriText = repoUriText.substring(publisher.getBaseURI().length());
            }
            if (repoUriText.startsWith("/")) {
                repoUriText = repoUriText.substring(1);
            }
            items[index] = repoUriText + " " + "[" + repo.name + "]";
            repoInfo[index] = repo;
        }
        repositoryCombo.setItems(items);
        for (int i = 0; i < items.length; i++) {
            repositoryCombo.setData(items[i], repoInfo[i]);
        }

        if (items.length > 0) {
            repositoryCombo.select(0);
            repository = (RepositoryInfo) repositoryCombo.getData(repositoryCombo.getItem(0));
        }
        else {
            repository = null;
        }
    }

    private void retrieveVersioningOptions() {
        List<VersioningMode> options = Lists.newArrayList();
        if (publisher != null) {
            options = publisher.getVersioningOptions();
        }

        String[] items = new String[options.size()];
        versioningCombo.setItems(new String[] {});
        for (ListIterator<VersioningMode> iter = options.listIterator(); iter.hasNext();) {
            int index = iter.nextIndex();
            VersioningMode option = iter.next();
            items[index] = option.name;
        }
        versioningCombo.setItems(items);
        for (int i = 0; i < items.length; i++) {
            versioningCombo.setData(items[i], options.get(i));
        }

        if (items.length > 0) {
            versioningCombo.select(0);
            versioning = (VersioningMode) versioningCombo.getData(versioningCombo.getItem(0));
        }
        else {
            versioning = null;
        }
    }

    protected void updateOntologyURI() {
        if (!Strings.isNullOrEmpty(ontologySourceUri)) {
            return;
        }

        ontologyUriLabel.setVisible(publisher != null);
        ontologyUriValue.setVisible(publisher != null);

        if (publisher != null && repository != null) {
            ontologyUri = URIUtils.combineSegments(false, (repository.uri.startsWith("http") ? ""
                    : publisher.getServerURL()), repository.uri, path);
            ontologyUri = URIUtils.normalizeUrl(ontologyUri);
            ontologyUriValue.setText(ontologyUri);
        }
        else {
            ontologyUri = null;
        }
        ontologyExists = Optional.absent();
    }

    private String[] createPublisherItems(List<IPublisher> publishers) {
        String[] items = new String[publishers.size() + 1];
        items[0] = "";
        int i = 1;
        for (IPublisher publisher : publishers) {
            items[i++] = publisher.getName();
        }
        return items;
    }

    private void checkOntologyExistence() {
        ontologyExists = Optional.absent();
        if (publisher == null || repository == null) {
            return;
        }
        IRunnableWithProgress operation = new IRunnableWithProgress() {
            private boolean exists;

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                exists = false;
                monitor.beginTask("Checking ontology URI", 1);
                monitor.subTask(String.format("Contacting \"%s\"...", ontologyUri));

                try {
                    publisher.listPublications(repository.id);

                    exists = publisher.containsOntology(ontologyUri);

                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            ontologyExists = Optional.of(Boolean.valueOf(exists));
                            validatePage();
                        }
                    });
                }
                catch (Throwable t) {
                    // Could not check ontology existence
                }
                monitor.worked(1);
                monitor.done();
                checkingExistence = false;
            }
        };
        try {
            IWizardContainer container = getContainer();
            if (container != null && !checkingExistence) {
                // This boolean is used to prevent multiple executions of the
                // operation!
                checkingExistence = true;
                container.run(false, false, operation);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable realException = e.getTargetException();
            Shell shell = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                    .getShell();
            MessageDialog.openError(shell, "Error", realException.getMessage());
        }
    }

    private void validatePage() {
        boolean incompleteCustomVersioning = false;

        setMessage(null);
        setErrorMessage(null);
        setPageComplete(false);

        if (publisher == null && Strings.isNullOrEmpty(ontologySourceUri)) {
            setMessage("Please select the publication server to use");
            setPageComplete(false);
        }
        else if (repository == null && Strings.isNullOrEmpty(ontologySourceUri)) {
            setMessage("Please select an available repository with write access");
            setPageComplete(false);
        }
        else if ((Strings.isNullOrEmpty(path) || path.equals("/"))
                && Strings.isNullOrEmpty(ontologySourceUri)) {
            setMessage("Please provide a publication name");
            setPageComplete(false);
        }
        else if (!Strings.isNullOrEmpty(ontologySourceUri) || ontologyExists.isPresent()) {
            boolean exists = !Strings.isNullOrEmpty(ontologySourceUri)
                    || ontologyExists.get().booleanValue();
            if (exists && Strings.isNullOrEmpty(ontologySourceUri)) {
                setErrorMessage("An ontology already exists at this URL");
                setPageComplete(false);
            }
            else if (getVersioningMode() != null && getVersioningMode().isCustomVersioning
                    && Strings.isNullOrEmpty(customVersionLabel)) {
                setMessage("Please provide a custom versioning label (e.g., v2_0)");
                incompleteCustomVersioning = true;
                setPageComplete(false);
            }
            else if (getVersioningMode() != null && getVersioningMode().isCustomVersioning
                    && !validCustomVersion(customVersionLabel)) {
                setErrorMessage("The custom version contains characters that are not allowed");
                incompleteCustomVersioning = true;
                setPageComplete(false);
            }
            else {
                setMessage("The ontology can be safely created at the specified URL");
                setPageComplete(true);
            }
        }
        else if (!pathText.isFocusControl()) {
            setPageComplete(true);
            checkOntologyExistence();
        }

        boolean visible = (isPageComplete() || incompleteCustomVersioning)
                && (Strings.isNullOrEmpty(ontologySourceUri) || versioning != null);
        versioningLabel.setVisible(visible);
        versioningCombo.setVisible(visible);
        versioningCustomText.setVisible(visible && versioning != null
                && versioning.isCustomVersioning);

        if (checkButton != null) {
            checkButton.setEnabled(!ontologyExists.isPresent() && publisher != null
                    && repository != null && path != null);
        }
    }

    private boolean validCustomVersion(String label) {
        VersioningMode vm = getVersioningMode();
        if (!vm.isCustomVersioning) {
            return false;
        }
        String regexPattern = vm.regexCustomVersioning;
        if (Strings.isNullOrEmpty(regexPattern)) {
            return true;
        }
        try {
            return Pattern.compile(regexPattern).matcher(Strings.nullToEmpty(label)).matches();
        }
        catch (Exception e) {
            return false;
        }
    }

    public IPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(IPublisher publisher) {
        this.publisher = publisher;
    }

    public void setRepository(RepositoryInfo repository) {
        this.repository = repository;
    }

    public void setDefaultPath(String path) {
        this.path = sanitizePath(Strings.nullToEmpty(path));
        if (!Widgets.isNullOrDisposed(pathText)) {
            pathText.setText(Strings.nullToEmpty(path));
        }
    }

    public void autoSelectOnlyPublisher() {
        if (publishers.size() == 1) {
            publisher = publishers.get(0);
            serverCombo.select(1);
            publisherChanged();
        }
    }

    public String getPath() {
        return path;
    }

    public String getOntologyURI() {
        return ontologyUri;
    }

    public VersioningMode getVersioningMode() {
        return versioning;
    }

    public Object getCustomVersionLabel() {
        if (versioning == null || !versioning.isCustomVersioning) {
            return null;
        }
        return Strings.nullToEmpty(customVersionLabel);
    }

    private void applyPublicationServer(String serverUrl) {
        for (int i = 0; i < serverCombo.getItemCount(); i++) {
            String txt = serverCombo.getItem(i);
            if (!Strings.isNullOrEmpty(txt) && serverUrl.startsWith(serverCombo.getText())) {
                serverCombo.select(i);
                serverCombo.notifyListeners(SWT.Selection, null);
                serverUrl = serverUrl.replaceFirst(txt, "");
                break;
            }
        }

        for (int i = 0; i < repositoryCombo.getItemCount(); i++) {
            String txt = repositoryCombo.getItem(i);
            if (!Strings.isNullOrEmpty(txt) && serverUrl.startsWith(repositoryCombo.getText())) {
                repositoryCombo.select(i);
                repository = (RepositoryInfo) repositoryCombo.getData(repositoryCombo.getItem(i));
                serverUrl = serverUrl.replaceFirst(txt, "");
                break;
            }
        }
    }

}

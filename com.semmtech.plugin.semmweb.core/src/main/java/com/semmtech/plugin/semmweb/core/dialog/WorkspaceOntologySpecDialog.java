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


import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.OntologySelector;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.model.ModelObtainer;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IResourceElement;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.semantics.util.FileUtils;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class WorkspaceOntologySpecDialog extends AbstractMessageInputDialog {

    private Composite container;

    private Text uriText;
    private Text prefixText;
    private Button altCheckbox;
    private Label externalUrlLabel;
    private Label workspaceUrlLabel;
    private Text externalUrlText;
    private Text workspaceUrlText;
    private Button workspaceButton;
    private Button fileButton;
    private Button webButton;

    private String publicUri;
    private String prefix;
    private String externalUrl;
    private String workspaceUrl;

    private boolean hasAltUrl;
    private boolean hideOntologyOptions;
    private boolean hidePrefix;
    private boolean hideAltUrl;
    private boolean forceAltUrl;
    private boolean enableUri;

    private String uriFieldName = "URI";
    private String externalUrlFieldName = "Alternative URL";
    private String workspaceUrlFieldName = "Workspace model";

    private final List<WorkspaceOntologySpec> options;
    private IModel model;
    private boolean optionsAreExclusive;
    private WorkspaceOntologySpec selectedOption;
    private int selectedOptionIndex;

    private final static String OPTION_EMPTY = new String();
    private final static String OPTION_CREATE = "[Specify an unlisted ontology...]";
    private final static String OPTION_LOCAL = "[Specify an ontology from a local file...]";

    private IWorkspaceOntologySpecValidator validator;

    /**
     * Keeps track of the type of the alternate url (if exist). Can assume only
     * the values:
     * <ul>
     * <li>WEB_REFERENCE
     * <li>HD_REFERENCE
     * <li>WORKSPACE
     * </ul>
     */
    private ImportType importType;

    /**
     * Sets the used validator to a new default instance of
     * WorkspaceOntologySpecValidator.
     */
    public WorkspaceOntologySpecDialog(Shell parentShell, String title, String message) {
        this(parentShell, title, message, new WorkspaceOntologySpecValidator());
    }

    public WorkspaceOntologySpecDialog(Shell parentShell, String title, String message,
            IWorkspaceOntologySpecValidator validator) {
        this(parentShell, title, message, validator, null);
    }

    public WorkspaceOntologySpecDialog(Shell parentShell, String title, String message,
            IWorkspaceOntologySpecValidator validator, String[] fieldNames) {
        super(parentShell, title, message, true);
        this.validator = validator;
        this.enableUri = true;
        this.options = Lists.newArrayList();
        this.selectedOptionIndex = -1;
        if (fieldNames != null && fieldNames.length == 3) {
            this.uriFieldName = fieldNames[0];
            this.externalUrlFieldName = fieldNames[1];
            this.workspaceUrlFieldName = fieldNames[2];
        }
    }

    @Override
    protected Control createInputArea(final Composite parent) {
        if (container == null) {
            container = (Composite) super.createInputArea(parent);
        }
        return createControls(container);
    }

    @SuppressWarnings("unused")
    protected Control createControls(final Composite container) {
        for (Control childControl : container.getChildren()) {
            childControl.dispose();
        }

        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 8;
        container.setLayout(layout);

        if (!hideOntologyOptions) {
            Label label = new Label(container, SWT.NONE);
            label.setText("Ontology");
            GridData layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
            layoutData.widthHint = 105;
            label.setLayoutData(layoutData);

            final Combo optionsCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
            optionsCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2,
                    1));

            // Only fill options the first time (check because refresh()
            // recreates all controls):
            if (comboOptions.isEmpty()) {
                comboOptions.add("");
                offset = 1;

                if (!optionsAreExclusive) {
                    if (model != null) {
                        comboOptions.add(OPTION_LOCAL);
                        offset++;
                    }

                    comboOptions.add(OPTION_CREATE);
                    offset++;
                }

                for (int i = 0; i < options.size(); i++) {
                    comboOptions.add(getText(options.get(i)));
                }
            }

            optionsCombo.setItems(comboOptions.toArray(new String[0]));
            if (selectedOptionIndex != -1) {
                optionsCombo.select(selectedOptionIndex);
            }

            optionsCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    int selected = optionsCombo.getSelectionIndex();
                    String selectedTxt = optionsCombo.getItem(selected);
                    selectedOptionIndex = selected;

                    if (OPTION_LOCAL.equals(selectedTxt)) {
                        selectImportFromLocalModel();
                        return;
                    }

                    if (OPTION_CREATE.equals(selectedTxt) || OPTION_EMPTY.equals(selectedTxt)) {
                        // Selected no spec or will specify a new spec
                        selectedOption = null;
                        publicUri = null;
                        prefix = null;
                        hasAltUrl = false;
                        externalUrl = null;
                        workspaceUrl = null;
                        importType = null;
                        refresh();
                    }
                    else {
                        // An existing spec has been selected
                        selectedOption = options.get(selected - offset);
                        setOntologySpec(selectedOption);

                        if (!Widgets.isNullOrDisposed(uriText)) {
                            uriText.setText(Strings.nullToEmpty(getPublicURI()));
                        }
                        if (!Widgets.isNullOrDisposed(prefixText)) {
                            prefixText.setText(Strings.nullToEmpty(getPrefix()));
                        }

                        // guess importType
                        importType = null;
                        if (!Strings.isNullOrEmpty(workspaceUrl)) {
                            importType = ImportType.WORKSPACE;
                            if (workspaceUrl.contains(CacheManager.CACHE_DIR)) {
                                importType = ImportType.CACHE;
                            }
                        }
                        else if (!Strings.isNullOrEmpty(externalUrl)) {
                            importType = ImportURLUtils.guessImportTypeExternalUrl(externalUrl);
                        }

                        if (!Widgets.isNullOrDisposed(externalUrlText)) {
                            externalUrlText.setText(Strings.nullToEmpty(getExternalAltURL()));
                        }
                        if (!Widgets.isNullOrDisposed(workspaceUrlText)) {
                            String displayText = Strings.nullToEmpty(getWorkspaceAltURL());
                            if (importType != null) {
                                String altText = ImportURLUtils.getAltUrlText(importType,
                                        getWorkspaceAltURL());
                                if (!Strings.isNullOrEmpty(altText)) {
                                    displayText = altText;
                                }
                            }
                            workspaceUrlText.setText(displayText);
                        }

                        hasAltUrl = !Strings.isNullOrEmpty(getAltURL());
                        refresh();
                    }
                }
            });
        }

        Label label = new Label(container, SWT.NONE);
        label.setText(uriFieldName);
        GridData layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        layoutData.widthHint = 50;
        label.setLayoutData(layoutData);

        uriText = new Text(container, SWT.BORDER);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
        uriText.setLayoutData(layoutData);
        uriText.setText(Strings.nullToEmpty(publicUri));
        uriText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                publicUri = uriText.getText();
                validateInput();
            }
        });
        uriText.setEditable(enableUri);

        if (!hidePrefix) {
            label = new Label(container, SWT.NONE);
            label.setText("Prefix");
            layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
            layoutData.widthHint = 50;
            label.setLayoutData(layoutData);

            prefixText = new Text(container, SWT.BORDER);
            layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1);
            layoutData.widthHint = 50;
            prefixText.setLayoutData(layoutData);
            prefixText.setText(Strings.nullToEmpty(prefix));
            prefixText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    prefix = removeTrailingColon(prefixText.getText());
                    if (prefix.length() == 0) {
                        prefix = null;
                    }
                    validateInput();
                }
            });
        }

        if (!hideAltUrl) {
            new Label(container, SWT.NONE);

            altCheckbox = new Button(container, SWT.CHECK);
            altCheckbox.setText("Use alternate location");
            layoutData = new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1);
            layoutData.verticalIndent = 3;
            altCheckbox.setLayoutData(layoutData);
            altCheckbox.setSelection(hasAltUrl);
            altCheckbox.setEnabled(!forceAltUrl);
            altCheckbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    hasAltUrl = altCheckbox.getSelection();
                    if (!hasAltUrl) {
                        externalUrl = null;
                        workspaceUrl = null;
                        importType = null;
                    }
                    refresh();
                }
            });

            externalUrlLabel = new Label(container, SWT.NONE);
            externalUrlLabel.setText(externalUrlFieldName);
            layoutData = new GridData(GridData.BEGINNING, SWT.CENTER, false, false);
            layoutData.widthHint = 105;
            externalUrlLabel.setLayoutData(layoutData);
            externalUrlLabel.setEnabled(hasAltUrl);

            externalUrlText = new Text(container, SWT.BORDER);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false);
            externalUrlText.setLayoutData(layoutData);
            externalUrlText.setText(Strings.nullToEmpty(externalUrl));
            externalUrlText.setEnabled(hasAltUrl);
            externalUrlText.setEditable(false);

            Button externalUrlClearButton = new Button(container, SWT.NONE);
            layoutData = new GridData(GridData.END, GridData.CENTER, false, false);
            externalUrlClearButton.setLayoutData(layoutData);
            externalUrlClearButton.setImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_REMOVE));
            externalUrlClearButton.setEnabled(hasAltUrl);
            externalUrlClearButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    externalUrl = null;
                    externalUrlText.setText(new String());
                    if (Strings.isNullOrEmpty(workspaceUrl)) {
                        importType = null;
                    }
                }
            });

            workspaceUrlLabel = new Label(container, SWT.NONE);
            workspaceUrlLabel.setText(workspaceUrlFieldName);
            layoutData = new GridData(GridData.BEGINNING, SWT.CENTER, false, false);
            layoutData.widthHint = 105;
            workspaceUrlLabel.setLayoutData(layoutData);
            workspaceUrlLabel.setEnabled(hasAltUrl);

            workspaceUrlText = new Text(container, SWT.BORDER);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false);
            workspaceUrlText.setLayoutData(layoutData);
            String displayText = Strings.nullToEmpty(getWorkspaceAltURL());
            if (importType != null) {
                String altText = ImportURLUtils.getAltUrlText(importType, getWorkspaceAltURL());
                if (!Strings.isNullOrEmpty(altText)) {
                    displayText = altText;
                }
            }
            workspaceUrlText.setText(displayText);
            workspaceUrlText.setEnabled(hasAltUrl);
            workspaceUrlText.setEditable(false);

            Button workspaceUrlClearButton = new Button(container, SWT.NONE);
            layoutData = new GridData(GridData.END, GridData.CENTER, false, false);
            workspaceUrlClearButton.setLayoutData(layoutData);
            workspaceUrlClearButton.setImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_REMOVE));
            workspaceUrlClearButton.setEnabled(hasAltUrl);
            workspaceUrlClearButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    workspaceUrl = null;
                    workspaceUrlText.setText(new String());
                    if (Strings.isNullOrEmpty(externalUrl)) {
                        importType = null;
                    }
                    else {
                        importType = ImportURLUtils.guessImportTypeExternalUrl(externalUrl);
                    }
                }
            });

            label = new Label(container, SWT.NONE);

            Composite altUrlComposite = new Composite(container, SWT.NONE);

            GridLayout externalUrlLayout = new GridLayout(4, false);
            externalUrlLayout.verticalSpacing = 5;
            externalUrlLayout.marginHeight = 0;
            externalUrlLayout.marginWidth = 0;
            layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
            altUrlComposite.setLayout(externalUrlLayout);
            altUrlComposite.setLayoutData(layoutData);

            label = new Label(altUrlComposite, SWT.NONE);
            layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false);
            label.setLayoutData(layoutData);

            workspaceButton = new Button(altUrlComposite, SWT.PUSH);
            workspaceButton.setText("Workspace...");
            layoutData = new GridData(SWT.RIGHT, GridData.CENTER, false, false);
            layoutData.widthHint = 103;
            workspaceButton.setLayoutData(layoutData);
            workspaceButton.setEnabled(hasAltUrl);
            workspaceButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleWorkspaceResource();
                }
            });

            fileButton = new Button(altUrlComposite, SWT.PUSH);
            fileButton.setText("File System...");
            layoutData = new GridData(SWT.RIGHT, GridData.CENTER, false, false);
            layoutData.widthHint = 103;
            fileButton.setLayoutData(layoutData);
            fileButton.setEnabled(hasAltUrl);
            fileButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleExternalFile();
                }
            });

            webButton = new Button(altUrlComposite, SWT.PUSH);
            webButton.setText("Web...");
            layoutData = new GridData(SWT.RIGHT, GridData.CENTER, false, false, 1, 1);
            layoutData.widthHint = 103;
            webButton.setLayoutData(layoutData);
            webButton.setEnabled(hasAltUrl);
            webButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleWeb();
                }
            });

            label = new Label(container, SWT.NONE);

        }

        if (!enableUri) {
            if (!hidePrefix) {
                prefixText.setFocus();
            }
            else if (!hideAltUrl) {
                externalUrlText.setFocus();
            }
        }

        container.layout(true, true);
        validateInput();
        return container;
    }

    /**
     * Try to retrieve the import from a file in the same project
     */
    private void selectImportFromLocalModel() {

        ModelSelectionDialog modelSelectionDialog = new ModelSelectionDialog(getShell(),
                model.getProject(), model);

        if (modelSelectionDialog.open() == Window.OK) {
            IModel model = modelSelectionDialog.getSelectedModel();

            if (model != null) {
                ModelObtainer modelObtainer = new ModelObtainer((IFile) model.getResource(), false);
                modelObtainer.run();

                OntologySelector sourceOntologySelector = new OntologySelector(
                        modelObtainer.getModel());
                sourceOntologySelector.selectOntology();

                Resource ontology = sourceOntologySelector.getOntology();

                if (ontology != null) {
                    publicUri = ontology.getURI();
                    prefix = sourceOntologySelector.getOntologyPrefix();
                    hasAltUrl = true;
                    externalUrl = null;
                    workspaceUrl = "file:///" + model.getResource().getLocation().toOSString();
                    importType = ImportType.WORKSPACE;
                }
                else {
                    publicUri = null;
                    prefix = null;
                    hasAltUrl = false;
                    externalUrl = null;
                    workspaceUrl = null;
                    importType = null;
                }

                refresh();
            }
        }

    }

    private static String getText(WorkspaceOntologySpec spec) {
        if (spec == null) {
            return null;
        }

        String prefix = spec.getPrefix();
        String uri = spec.getPublicURI();

        String result = uri;

        if (!Strings.isNullOrEmpty(prefix)) {
            result = prefix + ":  <" + result + ">";
        }

        return result;
    }

    private String removeTrailingColon(String input) {
        if ((input == null) || (input.length() == 0) || (input.charAt(input.length() - 1) != ':')) {
            return input;
        }
        return (input.length() == 1) ? "" : input.substring(0, input.length() - 1);
    }

    @Override
    protected void validateInput() {
        String errorMessage = null;

        if (validator != null) {
            if (enableUri) {
                errorMessage = validator.isValidPublicUri(publicUri);
            }
            if ((errorMessage == null) && !hidePrefix) {
                errorMessage = validator.isValidPrefix(prefix);
            }
            if ((errorMessage == null) && !hideAltUrl) {
                errorMessage = validator.isValidExternalAltUrl(externalUrl);
            }
            if ((errorMessage == null) && !hideAltUrl) {
                errorMessage = validator.isValidWorkspaceAltUrl(workspaceUrl);
            }
        }
        setErrorMessage(errorMessage);
    }

    private int offset;

    private static final String[][] EXTENSIONS = FileUtils.getFileDialogFormats(true, true);

    private List<String> comboOptions = Lists.newArrayList();

    protected void handleWorkspaceResource() {
        WorkspaceResourceSelectionDialog dialog = new WorkspaceResourceSelectionDialog(getShell(),
                getText(), "Select an alternate URL from the workspace below");

        dialog.addFilter(new WorkspaceResourcesFilter(EXTENSIONS[0], false));
        dialog.setValidator(new ISelectionStatusValidator() {
            @Override
            public IStatus validate(Object[] selection) {
                if (selection.length == 0) {
                    return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, ""); //$NON-NLS-1$
                }
                for (int i = 0; i < selection.length; i++) {
                    if (!(selection[i] instanceof IFile)
                            && !(selection[i] instanceof IResourceElement)) {
                        return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, ""); //$NON-NLS-1$
                    }
                }
                return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, ""); //$NON-NLS-1$
            }
        });
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
        dialog.setAllowMultiple(false);

        if (dialog.open() == Window.OK) {
            IFile file = (IFile) dialog.getSelectedResource();
            String url = "file:///" + file.getLocation().toFile().getAbsolutePath();
            setWorkspaceAltURL(url);
            importType = ImportType.WORKSPACE;
            workspaceUrlText
                    .setText(ImportURLUtils.getAltUrlText(importType, getWorkspaceAltURL()));
        }
    }

    protected void handleExternalFile() {
        FileDialog dialog = new FileDialog(getShell());
        if (!Strings.isNullOrEmpty(externalUrl)) {
            File file = new File(externalUrl);
            if (file.exists()) {
                dialog.setFilterPath(file.getParent());
            }
        }

        dialog.setFilterExtensions(EXTENSIONS[0]);
        dialog.setFilterNames(EXTENSIONS[1]);

        String path = dialog.open();

        if (path != null) {
            String url = "file:///" + path;
            // Could be a workspace file or one external to workspace.
            if (WorkspaceUtils.isWorkspaceFile(url)) {
                setWorkspaceAltURL(url);
                importType = ImportType.WORKSPACE;
                workspaceUrlText.setText(ImportURLUtils.getAltUrlText(importType,
                        getWorkspaceAltURL()));
            }
            else {
                setExternalAltURL(url);
                externalUrlText.setText(getExternalAltURL());
                if (Strings.isNullOrEmpty(getWorkspaceAltURL())) {
                    importType = ImportURLUtils.guessImportTypeExternalUrl(url);
                }
            }
        }
    }

    protected void handleWeb() {
        String title = "Web Resource";
        String message = "Insert a valid web URI, which has to start with http:// or https://";
        String initialValue;

        if (externalUrl != null && externalUrl.startsWith("http")) {
            initialValue = externalUrl;
        }
        else {
            initialValue = "http://";
        }

        final String ALT_URL_REGEX = "(https?://)[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?";
        final Pattern ALT_URL_PATTERN = Pattern.compile(ALT_URL_REGEX);
        IInputValidator validator = new IInputValidator() {

            @Override
            public String isValid(String url) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    return "The URL has to start with http:// or https://";
                }

                try {
                    @SuppressWarnings("unused")
                    URI uri = new URI(url);

                    if (!ALT_URL_PATTERN.matcher(url).matches()) {
                        return "The inserted URL does not appear to be valid.";
                    }
                }
                catch (URISyntaxException e) {
                    String error = url.length() > e.getIndex() ? ":  character "
                            + url.charAt(e.getIndex()) : "";
                    return "The inserted URL does not appear to be valid " + error;
                }
                return null;
            }
        };

        InputDialog webInput = new InputDialog(getShell(), title, message, initialValue, validator);

        if (webInput.open() == Window.OK) {
            String url = webInput.getValue();
            setExternalAltURL(url);
            externalUrlText.setText(getExternalAltURL());
            if (Strings.isNullOrEmpty(getWorkspaceAltURL())) {
                importType = ImportType.WEB_REFERENCE;
            }
        }
    }

    protected void refresh() {
        createControls(container);
    }

    /**
     * Set a list of possible options to select from. If <code>exclusive</code>
     * is false, the user can opt to enter a new spec.
     */
    public void setPossibleOptions(List<WorkspaceOntologySpec> options, IModel model,
            boolean exclusive) {
        this.options.clear();
        this.options.addAll(options);
        this.optionsAreExclusive = exclusive;
        this.model = model;

        Collections.sort(this.options, new Comparator<WorkspaceOntologySpec>() {
            @Override
            public int compare(WorkspaceOntologySpec lh, WorkspaceOntologySpec rh) {
                if (lh == null && rh == null) {
                    return 0;
                }
                if (lh == null) {
                    return 1;
                }
                if (rh == null) {
                    return -1;
                }
                int result = getText(lh).compareTo(getText(rh));
                if (result != 0) {
                    return result;
                }

                return lh.getPublicURI().compareTo(rh.getPublicURI());
            }
        });
    }

    /** Default value: true */
    public void setEnableURI(boolean enable) {
        this.enableUri = enable;
    }

    public void setHidePrefix(boolean hide) {
        this.hidePrefix = hide;
    }

    public void setForceAltURL(boolean force) {
        this.forceAltUrl = force;
        if (force) {
            hasAltUrl = true;
        }
    }

    public void setHideOntologyOptions(boolean hide) {
        this.hideOntologyOptions = hide;
    }

    public void setHideAltURL(boolean hide) {
        this.hideAltUrl = hide;
    }

    public String getPublicURI() {
        return publicUri;
    }

    public void setPublicURI(String publicUri) {
        this.publicUri = publicUri;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean hasAltURL() {
        return hasAltUrl;
    }

    public void setOntologySpec(WorkspaceOntologySpec spec) {
        if (spec != null) {
            this.publicUri = spec.getPublicURI();
            this.prefix = spec.getPrefix();
            this.externalUrl = spec.getExternalAltURL();
            this.workspaceUrl = spec.getWorkspaceAltURL();
            this.hasAltUrl = (spec.getAltURL() != null);
        }
    }

    public String getExternalAltURL() {
        externalUrl = correctAltURL(externalUrl);
        return externalUrl;
    }

    public void setExternalAltURL(String url) {
        externalUrl = correctAltURL(url);
    }

    public String getWorkspaceAltURL() {
        workspaceUrl = correctAltURL(workspaceUrl);
        return workspaceUrl;
    }

    public void setWorkspaceAltURL(String url) {
        workspaceUrl = correctAltURL(url);
        hasAltUrl = !Strings.isNullOrEmpty(getAltURL());
    }

    public String getAltURL() {
        if (!Strings.isNullOrEmpty(workspaceUrl)) {
            return workspaceUrl;
        }
        if (!Strings.isNullOrEmpty(externalUrl)) {
            return externalUrl;
        }
        return null;
    }

    private String correctAltURL(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }

        url = url.replace("\\", "/");
        if (url.startsWith("http:")) {
            url = url.replace(" ", "%20");
        }
        return url;
    }

    /**
     * Returns a WorkspaceOntologySpec object that contains the new
     * configuration.
     * <p>
     * NB: http:// or file:/// at the beginning of the Alternate URL if they
     * aren't provided by the user
     */
    public WorkspaceOntologySpec getOntologySpec() {
        return new WorkspaceOntologySpec(publicUri, prefix, externalUrl, workspaceUrl);
    }

    /**
     * Set the import type
     */
    public void setImportType(ImportType type) {
        importType = type;
    }

    /**
     * If the Alt URL has been set, then return its type (if known)
     */
    public ImportType getImportType() {
        if (hasAltURL()) {
            return importType;
        }
        return null;
    }

    /**
     * Useful method to generate the event that contains the information
     * selected in this dialog. The generated event would be passed to an
     * eventHandler.
     */
    public ChangeEvent getChangeEvent() {
        if (hasAltURL()) {
            return new ChangeEvent(getOntologySpec(), getImportType());
        }
        return new ChangeEvent(getOntologySpec());
    }

    /**
     * Remember that in this context the ImportType is strictly related to the
     * alternateUrl so if the alternateUrl is null then the importType will be
     * null. Outside of this context a null importType could be considered as a
     * {@link ImportType#WEB_REFERENCE}
     * 
     * @author Simone
     */
    public class ChangeEvent extends Event {

        private final WorkspaceOntologySpec spec;
        private final ImportType importType;

        public ChangeEvent(WorkspaceOntologySpec spec) {
            this(spec, null);
        }

        public ChangeEvent(WorkspaceOntologySpec spec, ImportType importType) {
            this.spec = spec;
            this.importType = importType;
        }

        public WorkspaceOntologySpec getOntologySpec() {
            return spec;
        }

        public ImportType getImportType() {
            return importType;
        }
    }

    /**
     * Add namespace options to select from.
     * 
     * @param projNamespaces
     */
    public void setNamespaces(List<String> projNamespaces) {

        this.comboOptions.addAll(projNamespaces);
    }
}

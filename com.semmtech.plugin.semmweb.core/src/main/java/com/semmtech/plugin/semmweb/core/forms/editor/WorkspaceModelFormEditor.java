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

package com.semmtech.plugin.semmweb.core.forms.editor;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.LinearUndoEnforcer;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.FileEditorInput;
import org.xml.sax.SAXParseException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntDocumentManager.ReadFailureHandler;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.NotImplementedException;
import com.semmtech.jena.readers.JenaReadersUtil;
import com.semmtech.jena.skolem.Skolemizer;
import com.semmtech.jena.vocabulary.Skolem;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.ModelProviderRegistry;
import com.semmtech.plugin.semmweb.core.cache.CacheManager;
import com.semmtech.plugin.semmweb.core.decorators.PublishedResourceFileDecorator;
import com.semmtech.plugin.semmweb.core.dialog.IDisableImportDialog;
import com.semmtech.plugin.semmweb.core.dialog.ImportExternalFileDialog;
import com.semmtech.plugin.semmweb.core.dialog.NotifyImportDialog;
import com.semmtech.plugin.semmweb.core.dialog.SaveOntologyAsDialog;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog.ChangeEvent;
import com.semmtech.plugin.semmweb.core.extensionpoint.CoreExtensions;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelProcessor;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelValidationListener;
import com.semmtech.plugin.semmweb.core.extensionpoint.IModelValidator;
import com.semmtech.plugin.semmweb.core.extensionpoint.ModelValidationAdapter;
import com.semmtech.plugin.semmweb.core.io.ModelIOUtils;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jobs.LoadModelJob;
import com.semmtech.plugin.semmweb.core.jobs.SemanticProjectBuildJob;
import com.semmtech.plugin.semmweb.core.model.FileModelMakerManager;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelChangedAdapter;
import com.semmtech.plugin.semmweb.core.model.ModelEventListener;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.TransactionManager;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ImportNotificationsPreference;
import com.semmtech.plugin.semmweb.core.preferences.SkolemizationPreference;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.plugin.semmweb.core.undo.ModelTransactionOperation;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.viewers.DynamicNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.semantics.model.ExtendedModelFactory;
import com.semmtech.semantics.model.PredicateSelector;
import com.semmtech.semantics.util.RDFParserUtil;
import com.semmtech.ui.plugin.PartListener;
import com.semmtech.ui.plugin.dialog.ExtendedMessageDialog;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;


/**
 * This (multi-page) form editor offers an editor for modifying a semantic
 * model.
 * 
 * @author Mike Henrichs
 * 
 */
public abstract class WorkspaceModelFormEditor extends FormEditor implements IModelProvider,
        ModelChangedListener, IPropertyChangeListener {

    private static Logger logger = Logger.getLogger(WorkspaceModelFormEditor.class);

    public static final String SKOLEMIZED_MODEL_IRI = "skolem-support";

    private static final int FAILURE_RETRY = 0;
    private static final int FAILURE_SKIP = 1;
    private static final int FAILURE_ABORT_ALL = 2;

    private static final int OPTION_IGNORE_URI_SESSION = 1;
    private static final int OPTION_IGNORE_URI_ALWAYS = 2;

    private static final Set<String> IGNORED_CURRENT_SESSION_URIS = Sets.newHashSet();

    private DynamicNodeLabelProvider labelProvider;

    private Set<ModelEventListener> modelEventListeners;

    private boolean readingModel;
    private String filename;
    private String serializationLang;
    private String baseUri;
    private String modelUri;
    private IResource file;
    private IProject project;

    private boolean skipAllImports;
    private Set<String> skipImportUris;
    private Set<String> disableReimportNotification;
    private boolean dirty;

    /**
     * The URI that maps to the language profile
     */
    private String languageURI = ProfileRegistry.RDFS_LANG;
    private OntModel ontModel;
    private InfModel infModel;
    private boolean infModelOutdated;
    private String readFailureMessage;
    private boolean modelContentChanged;
    private boolean importsNeedRefreshing;

    /**
     * This value is set to false when the model starts to be readen. And is set
     * to true when the model is fully loaded with all his dependencies.
     */
    private volatile boolean modelLoaded;

    private long fileSystemTimeStamp;
    private final Map<String, Model> subModels;
    private long lastUpdatedLocalSubModels;
    private final Set<String> validationModels;

    private IOperationHistory operationHistory;
    private IUndoContext undoContext;
    private UndoRedoActionGroup modelHistoryActionGroup;

    private IPartListener activeEditorListener;
    private IPartListener2 activeViewListener;

    /**
     * Keep track of the imported uris. Is filled during save action
     */
    private final List<String> importedModelUris;

    private boolean showModelChangedDialog;

    /**
     * Workaround to solve: IP0005-431
     * 
     * This variable is used if an user open a model from outside the editor
     * and, after that, decides to cancel the operation. The default behavior of
     * eclipse would be to open anyway a blank page that shows an error. We
     * don't want that this page is showed. The only workaround to do that is to
     * set this variable and check it inside
     * CorePlugin.trackTopEditor()#partBroughtToTop() method, just after the
     * opening of the error page, and perform the close operation if this
     * variable is true.
     */
    private boolean forceClose;

    /**
     * If true the save and saveAs action are disabled
     */
    private boolean readOnly;

    /**
     * Keep track of the currently activated part. Useful to choose the priority
     * of the event notification
     */
    private IWorkbenchPartReference activePart;

    private boolean validLicense;

    private IEditorInput localEditorInput;

    private final TransactionManager transactionManager;

    public WorkspaceModelFormEditor() {
        super();

        transactionManager = new TransactionManager(this);

        undoContext = new ObjectUndoContext(this);
        modelEventListeners = Sets.newLinkedHashSet();
        subModels = Maps.newHashMap();
        validationModels = Sets.newHashSet();
        ontModel = ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        operationHistory = getDefaultWorkbenchOperationHistory();
        labelProvider = new DynamicNodeLabelProvider(this);
        skipImportUris = Sets.newHashSet();
        disableReimportNotification = Sets.newHashSet();
        importedModelUris = Lists.newArrayList();

        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

        activeEditorListener = new PartListener() {
            @Override
            public void partActivated(IWorkbenchPart part) {
                if (isModelLoaded()
                        && (part == WorkspaceModelFormEditor.this || part instanceof AbstractModelListenerView)) {
                    boolean refreshed = refreshWithFileSystemChange();
                    if (!refreshed) {
                        reimportOutdatedLocalSubModels();
                    }
                }
            }
        };

        activeViewListener = new PartListener() {
            @Override
            public void partActivated(IWorkbenchPartReference partRef) {
                if (partRef.getPart(false) instanceof AbstractModelListenerView) {
                    activePart = partRef;
                }
            }
        };

        showModelChangedDialog = false;
    }

    @Override
    public void dispose() {
        ModelProviderRegistry.unregister(getModelURI());
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);

        if (getSite() != null) {
            IPartService partService = getSite().getWorkbenchWindow().getPartService();
            if (activeEditorListener != null) {
                partService.removePartListener(activeEditorListener);
            }
            if (activeViewListener != null) {
                partService.removePartListener(activeViewListener);
            }
        }

        // Ensure the resource is marked as modified again to switch back to the
        // persisted model in the project builder.
        markResourceModified(null);

        if (operationHistory != null) {
            operationHistory.dispose(undoContext, true, true, true);
            operationHistory = null;
        }
        if (modelHistoryActionGroup != null) {
            modelHistoryActionGroup.dispose();
            modelHistoryActionGroup = null;
        }
        undoContext = null;

        if (labelProvider != null) {
            labelProvider.dispose();
            labelProvider = null;
        }
        if (ontModel != null) {
            ontModel.unregister(this);
            ontModel.close();
            ontModel = null;
        }
        if (infModel != null) {
            infModel.close();
            infModel = null;
        }

        super.dispose();
    }

    /**
     * Create a toolkit that shares colors between editors.
     */
    @Override
    protected FormToolkit createToolkit(Display display) {
        return new FormToolkit(display);
    }

    protected abstract boolean validateLicense();

    /**
     * Will only be called during the initialization of this editor.
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);

        validLicense = validateLicense();

        // if there are problems with license close the editor
        if (!validLicense) {
            forceClose = true;
        }
        localEditorInput = input;
        if (localEditorInput instanceof FileEditorInput) {
            filename = ((FileEditorInput) localEditorInput).getPath().lastSegment();
        }
        else if (localEditorInput instanceof FileStoreEditorInput) {
            // Create a linked resource in an existing project, which points
            // to a file elsewhere in the file system.
            Shell shell = site.getShell();
            FileStoreEditorInput storeInput = (FileStoreEditorInput) input;
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceRoot root = workspace.getRoot();

            boolean existsSemanticProject = false;

            // check if there are opened semantic project
            for (IProject project : root.getProjects()) {
                if (SemanticProjectUtils.isSemanticProject(project)) {
                    existsSemanticProject = true;
                    break;
                }
            }

            // perform the control only if the license is OK, otherwise the
            // "Project Required" dialog could confuse the user
            if (!existsSemanticProject && validLicense) {
                MessageDialog
                        .openWarning(
                                shell,
                                "Project Required",
                                "It seems that no Semantic Projects exist yet in your workspace.\n"
                                        + " Please create a Semantic Project before importing Semantic Files.");
                forceClose = true;
            }

            // these operation are needed to open and close the editor
            // without errors.
            if (forceClose) {
                setInput(storeInput);
                filename = storeInput.getName();
                setModelURI(input);
                setSerializationLang(filename);
                getSite().getWorkbenchWindow().getPartService()
                        .addPartListener(activeEditorListener);
                return;
            }

            URI uri = storeInput.getURI();
            File file = null;
            try {
                IFileStore location = EFS.getLocalFileSystem().getStore(uri);
                file = location.toLocalFile(EFS.NONE, null);
            }
            catch (CoreException e) {
                e.printStackTrace();
            }

            if (file == null) {
                throw new PartInitException("Cannot find the file specified");
            }

            // Check if file is not a file in the workspace
            IPath path = new Path(file.getPath());

            // the method IWorkspaceRoot.exists() works only with relative path
            // for the absolute path the method
            // IWorkspaceRoot.getFileForLocation() is needed
            boolean inWorkspace = root.exists(path);
            IFile resource = null;

            if (inWorkspace) {
                resource = root.getFile(path);
            }
            else {
                // NOTE: It is possible to have a link within a closed project
                // to a file which is also a direct file within another project;
                // for now only the project with the direct file in it will be
                // opened
                for (final IProject project : root.getProjects()) {
                    if (!project.isOpen()) {
                        IPath projectPath = project.getLocation();
                        if (projectPath.isPrefixOf(path)) {
                            try {
                                project.open(IResource.NONE, new NullProgressMonitor());
                                resource = project.getFile(path);
                            }
                            catch (CoreException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }

            if (resource != null) {
                localEditorInput = new FileEditorInput(resource);
                filename = ((FileEditorInput) localEditorInput).getPath().lastSegment();
            }
            else {
                String message = "The file does not appear to be part of your workspace, and as a result may not benefit from settings on projects.\n\nDo you wish to import the file into one of your projects?";

                final int YES = 0;
                final int CANCEL = 2;

                MessageDialog dialog = new MessageDialog(shell, "External File", null, message,
                        SWT.ICON_INFORMATION, new String[] { "Yes", "No", "Cancel" }, YES);
                int result = dialog.open();

                // Default :
                setInput(storeInput);

                // set the forceClose flag to true without exit the method
                // because the successive operation are needed to close the
                // part without errors
                if (result == CANCEL) {
                    forceClose = true;
                }

                filename = file.getAbsolutePath();

                if (result == YES) {
                    ImportExternalFileDialog importDialog = new ImportExternalFileDialog(shell,
                            "Import File");
                    importDialog.setFilename(path.lastSegment());

                    if (importDialog.open() == Window.OK) {
                        int action = importDialog.getAction();
                        IContainer container = importDialog.getContainer();
                        String name = importDialog.getFilename();

                        IFile workspaceFile = null;
                        if (container instanceof IProject) {
                            workspaceFile = ((IProject) container).getFile(name);
                        }
                        else if (container instanceof IFolder) {
                            workspaceFile = ((IFolder) container).getFile(name);
                        }
                        if (workspaceFile != null) {
                            try {
                                if (action == ImportExternalFileDialog.ACTION_LINK) {
                                    workspaceFile.createLink(path, IResource.NONE, null);
                                }
                                else {
                                    try (FileInputStream fis = new FileInputStream(file)) {
                                        workspaceFile.create(fis, IResource.NONE, null);
                                    }
                                }
                            }
                            catch (CoreException | IOException e) {
                                logger.error(e.getMessage(), e);
                            }
                            localEditorInput = new FileEditorInput(workspaceFile);
                            filename = ((FileEditorInput) localEditorInput).getPath().lastSegment();
                        }
                    }
                }
            }
        }

        if (validLicense) {
            setModelURI(input);
            setSerializationLang(filename);
        }

        getSite().getWorkbenchWindow().getPartService().addPartListener(activeEditorListener);
        getSite().getWorkbenchWindow().getPartService().addPartListener(activeViewListener);
    }

    @Override
    public IEditorInput getEditorInput() {
        // Since this class will be extended and we do not want this FormEditor
        // to provide the EditorInput an exception is thrown
        throw new NotImplementedException(
                "This method should have been implemented by the subclass");
    }

    @Override
    protected void setInput(IEditorInput input) {
        // Since this class will be extended and we do not want this FormEditor
        // to keep track of an EditorInput an exception is thrown
        throw new NotImplementedException(
                "This method should have been implemented by the subclass");
    }

    public boolean forceClose() {
        return forceClose;
    }

    private void setModelURI(IEditorInput input) {
        // Initialize modelURI and register to ModelProviderRegistry that this
        // is the provider
        modelUri = ((IURIEditorInput) input).getURI().toString();
        ModelProviderRegistry.register(modelUri, this);
    }

    private static IOperationHistory getDefaultWorkbenchOperationHistory() {
        IWorkbench workbench = CorePlugin.getDefault().getWorkbench();
        IOperationHistory result = workbench.getOperationSupport().getOperationHistory();
        result.addOperationApprover(new LinearUndoEnforcer());
        return result;
    }

    /**
     * Attempts to return the current file as an IResource object; otherwise
     * null
     * 
     * @return
     */
    public IResource getResource() {
        if (file == null) {
            if (localEditorInput instanceof FileEditorInput) {
                FileEditorInput fileEditorInput = (FileEditorInput) localEditorInput;
                if (fileEditorInput != null) {
                    IPath path = fileEditorInput.getFile().getFullPath();
                    IWorkspace workspace = ResourcesPlugin.getWorkspace();
                    IWorkspaceRoot root = workspace.getRoot();
                    IPath base = root.getLocation();
                    path = path.makeRelativeTo(base);
                    if (root.exists(path)) {
                        file = root.findMember(path);
                    }
                }
            }
        }
        return file;
    }

    /**
     * Attempts to return the project for the current resource; if the Editor
     * does not contain a file null will be returned.
     * 
     * @return
     */
    public IProject getProject() {
        if (project == null) {
            IResource resource = getResource();
            if (resource != null) {
                project = resource.getProject();
            }
        }
        return project;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Sets the serialization method, using the guessLang method defined by
     * Jena's FileUtils class:
     * 
     * "Guess the language/type of model data If the URI ends '.rdf', it is
     * assumed to be RDF/XML If the URI ends '.nt', it is assumed to be
     * N-Triples If the URI ends '.ttl', it is assumed to be Turtle If the URI
     * ends '.owl', it is assumed to be RDF/XML" If filename ends with '.owl'
     * the serialization RDF/XML-ABBREV is chosen.
     * 
     * @param filename
     */
    private void setSerializationLang(String filename) {
        serializationLang = FileUtils.guessLang(filename);
        if (filename.endsWith(".owl")) {
            serializationLang = FileUtils.langXMLAbbrev;
        }
    }

    /**
     * Returns the serialization language used by the current TextEditor.
     * 
     * @return
     */
    public String getSerializationLang() {
        return serializationLang;
    }

    @Override
    public List<String> getSubModelURIs() {
        return Lists.newArrayList(subModels.keySet());
    }

    @Override
    public Model getSubModel(String uri) {
        return subModels.get(uri);
    }

    @Override
    public String getModelURI() {
        return modelUri;
    }

    @Override
    public String getBaseURI() {

        return baseUri;
    }

    @Override
    public void setBaseURI(String baseUri) {
        performSetBaseUriOperation(baseUri);
    }

    private void performSetBaseUriOperation(String newBaseUri) {
        String oldBaseUri = this.baseUri;

        if (Strings.isNullOrEmpty(newBaseUri)) {
            if (oldBaseUri == null) {
                return; // nothing changed
            }
            newBaseUri = null;
        }
        else {
            if ((oldBaseUri != null) && newBaseUri.equals(oldBaseUri)) {
                return; // nothing changed
            }
        }
        ChangeBaseUriOperation operation = new ChangeBaseUriOperation(oldBaseUri, newBaseUri);

        performUndoRedoOperation(operation);
    }

    @Override
    public void performUndoRedoOperation(AbstractOperation operation) {
        operation.addContext(undoContext);
        try {
            operationHistory.execute(operation, null, null);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * This operation cannot be moved into a separate type file. The reason for
     * this is the that the operation is local to the enclosing file, and needs
     * direct access to the uri of the OntologyEditorPart.
     * 
     * @author Mike Henrichs
     * 
     */
    private final class ChangeBaseUriOperation extends AbstractOperation {
        private final String oldBaseUri;
        private final String newBaseUri;

        public ChangeBaseUriOperation(String oldBaseUri, String newBaseUri) {
            super("Base URI Change");
            this.oldBaseUri = oldBaseUri;
            this.newBaseUri = newBaseUri;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            WorkspaceModelFormEditor.this.baseUri = newBaseUri;
            updateDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return execute(monitor, info);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            WorkspaceModelFormEditor.this.baseUri = oldBaseUri;
            updateDirty(true);
            return Status.OK_STATUS;
        }
    }

    @Override
    public String getModelTitle() {
        return getTitle();
    }

    @Override
    public DynamicNodeLabelProvider getLabelProvider() {
        return labelProvider;
    }

    @Override
    public Model getBaseModel() {
        return ontModel.getBaseModel();
    }

    @Override
    public OntModel getOntModel() {
        return ontModel;
    }

    public void setInferredModel(final InfModel model) {
        clearInferredModel();

        if (model == null) {
            return;
        }

        /*
         * Get deductions model by subtracting the raw model from the inferred
         * model. Simply getting the deductions model by getDeductionsModel()
         * will give a model that still contains functors (that is to say, Jena
         * reasoning creates literals in that model that will only be converted
         * to proper triples through the InfModel).
         */
        Model deductionsModel = model.difference(model.getRawModel());

        if (ontModel == null || deductionsModel == null || deductionsModel.isEmpty()) {
            return;
        }

        infModel = model;
        ontModel.addSubModel(deductionsModel);
        infModelOutdated = false;
        ontModel.notifyEvent(new SubModelAddedEvent(ontModel, deductionsModel,
                IModelProvider.INFERRED_SUBMODEL_URI, "Sub model added due to inference"));
    }

    public void clearInferredModel() {
        infModel = null;
        if (ontModel == null) {
            return;
        }

        Model deductionsModel = getSubModel(IModelProvider.INFERRED_SUBMODEL_URI);

        if (deductionsModel != null) {
            subModels.remove(IModelProvider.INFERRED_SUBMODEL_URI);
            ontModel.removeSubModel(deductionsModel);
            ontModel.notifyEvent(new SubModelRemovedEvent(ontModel,
                    IModelProvider.INFERRED_SUBMODEL_URI,
                    "Sub model removed due to clearing of inference"));
        }
    }

    /**
     * Returns true if no inferred model has been set or if it might be outdated
     * due to model changes.
     */
    public boolean isInferredModelOutdated() {
        return (infModel == null
                || !getSubModelURIs().contains(IModelProvider.INFERRED_SUBMODEL_URI) || infModelOutdated);
    }

    /**
     * Returns an iterator on derivations if available for the given statement.
     * Returns null otherwise.
     */
    public Iterator<Derivation> getDerivation(Statement statement) {
        if (infModel != null && statement != null) {
            Iterator<Derivation> result = infModel.getDerivation(statement);
            if (result.hasNext()) {
                return result;
            }
        }
        return null;
    }

    private class RefreshReadFailureHandler implements ReadFailureHandler {

        @Override
        public void handleFailedRead(String url, Model model, Exception e) {
            String message = e.getMessage();
            if (message == null) {
                message = "";
            }
            else {
                message = "\n\n" + message;
            }
            if (e instanceof DoesNotExistException) {
                readFailureMessage = String.format("Provided URL \"%s\" does not exist.", url);
            }
            else if (e instanceof NotFoundException) {
                readFailureMessage = String.format("Provided URL \"%s\" could not be found.", url);
            }
            else if (e instanceof SAXParseException) {
                readFailureMessage = String.format("Content found at \"%s\" could not be parsed.",
                        url);
            }
            else if (e instanceof IOException) {
                readFailureMessage = String.format("The location \"%s\" could not be accessed.%s",
                        url, message);
            }
            else {
                readFailureMessage = String.format(
                        "An error occured trying to read from URL \"%s\".%s", url, message);
            }
        }

    }

    private void executeValidation(OntModel model) {
        for (String validatedUri : validationModels) {
            Model subModel = subModels.get(validatedUri);
            model.removeSubModel(subModel);
            subModels.remove(validatedUri);
        }
        validationModels.clear();

        // Added listener to be notified if there is a sub model added
        // to the model.
        IModelValidationListener validationListener = new ModelValidationAdapter() {
            @Override
            public void submodelAdded(Model subModel, String modelUri) {
                subModels.put(modelUri, subModel);
                validationModels.add(modelUri);
            }
        };
        for (IModelValidator validator : CoreExtensions.findValidators()) {
            if (validator.isEnabled()) {
                validator.addValidationListener(validationListener);
                validator.validateModel(model);
            }
        }
    }

    private void executeProcessors(OntModel model) {
        Model baseModel = model.getBaseModel();
        for (IModelProcessor processor : CoreExtensions.findProcessors()) {
            if (processor.isEnabled()) {
                processor.processModel(baseModel);
            }
        }
    }

    /**
     * This combination of ExtendedMessageDialog and the IDisabledMessageDialog
     * can be used to disable imports when they fail.
     * 
     * @author Mike Henrichs
     * 
     */
    private abstract static class AbstractImportErrorDialog extends ExtendedMessageDialog implements
            IDisableImportDialog {

        public AbstractImportErrorDialog(Shell parentShell, String dialogTitle,
                Image dialogTitleImage, String dialogMessage, int dialogImageType,
                String[] dialogButtonLabels, int defaultIndex) {
            super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType,
                    dialogButtonLabels, defaultIndex);
        }
    }

    private class RefreshModelJob extends JobWithMonitor {
        private OntDocumentManager manager;
        protected boolean processImports;
        private boolean abort;
        private final List<String> ignoreUris;
        private int statementsAdded;

        private int previousNotifyAction = -1;
        private boolean applyNotifyActionToAll = false;
        private ImportNotificationsPreference notifyPreference;

        private ModelChangedAdapter modelChangeListener = new ModelChangedAdapter() {
            @Override
            public void addedStatement(Statement statement) {
                statementsAdded++;
            }
        };

        public RefreshModelJob(String name) {
            super(name);
            final IProject project = getProject();
            if (project == null) {
                notifyPreference = ImportNotificationsPreference
                        .fromPreferenceStore(new PreferenceStore());
            }
            else {
                notifyPreference = ImportNotificationsPreference.fromProject(project);
            }

            this.ignoreUris = Lists.newArrayList();
            this.queuedImportUris = Lists.newLinkedList();
            this.abort = false;

            addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    WorkspaceModelFormEditor.this.notify(new ModelEvent("finishedReadingModel",
                            null));
                    markResourceModified(null);
                }
            });
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            startMonitorUpdate(monitor, "Reading Model", 6);

            try {
                WorkspaceModelFormEditor.this.notify(new ModelEvent("startReadingModel", null));
                refreshModel(monitor);
            }
            finally {
                monitor.done();
                stopMonitorUpdate();
            }

            if (readFailureMessage != null || abort) {
                close(false);
                return Status.CANCEL_STATUS;
            }

            return Status.OK_STATUS;
        }

        private void refreshModel(final IProgressMonitor monitor) {
            initValues();

            // Try and read the model from the input
            updateSubTask("Resetting readers...");
            JenaReadersUtil.reset();
            monitor.worked(1);

            updateSubTask("Parsing base model from file...");
            ModelMaker maker = FileModelMakerManager.getInstance().getModelMaker(getProject());
            Model parsedModel = createBaseModel(maker);
            monitor.worked(1);

            // Error during reading model from text editor (only the empty
            // model, without any imports etc.)
            if (readFailureMessage != null) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        Shell shell = getSite().getShell();
                        MessageDialog.openError(shell, "Read Failure", readFailureMessage);
                    }
                });
                if (ontModel == null) {
                    Model emptyModel = maker.createFreshModel();
                    ontModel = ExtendedModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
                            emptyModel);
                    modelContentChanged = true;
                }
            }
            else {
                getRequiredImportInfo(parsedModel);
                lastUpdatedLocalSubModels = System.currentTimeMillis();

                List<String> namespaceUris = Lists.newArrayList(parsedModel.getNsPrefixMap()
                        .values());
                handlePreLoading(namespaceUris);

                monitor.worked(1);

                // Manually process imports (instead of using Jena's
                // build-in mechanism)
                updateSubTask("Importing additional models...");
                if (processImports) {
                    boolean done = false;
                    queuedImportUris.clear();
                    while (!done) {
                        done = !handleImports();

                        if (abort) {
                            updateSubTask("Aborting the operation model...");
                            return;
                        }
                    }
                }
                getDocumentManager().setReadFailureHandler(null);
                modelContentChanged = true;
                monitor.worked(1);
            }

            updateSubTask("Validating model...");
            executeValidation(ontModel);
            monitor.worked(1);

            ontModel.register(WorkspaceModelFormEditor.this);
            parsedModel.unregister(modelChangeListener);

            updateSubTask(String
                    .format("Updating interface with %s statements...", statementsAdded));
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (modelContentChanged) {
                        executeProcessors(ontModel);
                        WorkspaceModelFormEditor.this.notify(new ModelActivatedEvent(
                                ontModel,
                                String.format(
                                        "Finished read and import for file \"%s\" containing %s statements",
                                        filename, statementsAdded)));

                    }
                }
            });

            // if the model is closed before the end of the loading the
            // operationHistory will be null
            if (operationHistory != null) {
                operationHistory.dispose(undoContext, true, true, false);
            }
            monitor.worked(1);
        }

        private void initValues() {
            modelContentChanged = false;
            statementsAdded = 0;
            subModels.clear();
            validationModels.clear();

            ignoreUris.clear();
        }

        private Model createBaseModel(ModelMaker maker) {
            Model parsedModel = maker.createFreshModel();
            parsedModel.register(modelChangeListener);

            // TODO: Do not like the stream1 and stream2 solutions, give better
            // names!
            // Read failure message is set if not null
            readFailureMessage = null;
            try (InputStream stream1 = getEditorUtf8Stream();
                    InputStream stream2 = getEditorUtf8Stream()) {

                IResource resource = getResource();
                if (resource != null) {
                    fileSystemTimeStamp = resource.getLocalTimeStamp();
                }

                // Note: Base URI is not given as parameter; as this is only
                // used by the read method to set a default base URI - read
                // still uses baseURI defined in file if present
                baseUri = null;
                serializationLang = FileUtils.guessLang(filename);
                if (filename.endsWith(".owl")) {
                    serializationLang = FileUtils.langXMLAbbrev;
                }
                baseUri = RDFParserUtil.retrieveBaseURI(stream1, serializationLang);

                parsedModel.read(stream2, null, serializationLang);
            }
            catch (Throwable t) {
                String message = "Error while parsing base model from file "
                        + (file != null ? file.getFullPath() : null) + ":" + t.getMessage();
                logger.error(message, t);
                logger.error("Error parsing base model from file", t);
                if (readFailureMessage == null) {
                    readFailureMessage = "" + t.getMessage();
                }
            }
            return parsedModel;
        }

        private void getRequiredImportInfo(Model model) {
            languageURI = ProfileRegistry.RDFS_LANG;
            List<String> namespaceUris = new ArrayList<>(model.getNsPrefixMap().values());
            if (namespaceUris.contains(OWL.getURI())) {
                languageURI = ProfileRegistry.OWL_LANG;
            }

            ModelMaker baseMaker = FileModelMakerManager.getInstance().getModelMaker(getProject());
            ModelMaker importsMaker = FileModelMakerManager.getInstance().getModelMaker(
                    getProject(), ".imports");
            OntModelSpec spec = new OntModelSpec(baseMaker, importsMaker, getDocumentManager(),
                    null, languageURI);

            // Check if model contains skolemization data
            boolean isSkolemized = Skolemizer.isSkolemized(model);
            // If the model contains skolemized data then be sure to
            // 1. deskolemize this data to make resource anonymous again
            // 2. store the skolemization data in a sub model for future
            if (SkolemizationPreference.isSkolemizationEnabled() && isSkolemized) {
                ModelMaker maker = FileModelMakerManager.getInstance().getModelMaker(getProject());
                Skolemizer skolemizer = new Skolemizer();
                skolemizer.setKeepAnonIds(false);
                Model deskolemizedModel = skolemizer.deskolemize(model);
                Model skolemData = ModelFactory.createDefaultModel();
                Model anonymousModel = maker.createFreshModel();

                Skolemizer.extractSkolemData(deskolemizedModel, anonymousModel, skolemData);
                ontModel = ExtendedModelFactory.createOntologyModel(spec, anonymousModel);
                ontModel.setNsPrefixes(model.getNsPrefixMap());
                if (!skolemData.isEmpty()) {
                    ontModel.addSubModel(skolemData);
                    subModels.put(SKOLEMIZED_MODEL_IRI, skolemData);
                    ontModel.notifyEvent(new SubModelAddedEvent(ontModel, skolemData,
                            SKOLEMIZED_MODEL_IRI, "Skolem support sub model added"));
                }
            }
            else {
                ontModel = ExtendedModelFactory.createOntologyModel(spec, model);
            }
        }

        protected OntDocumentManager getDocumentManager() {
            if (manager == null) {
                DocumentManagerPreference preferences = DocumentManagerPreference
                        .fromProject(getProject());
                manager = preferences.getDocumentManagerConfig().createManager();
                // Store the setting for processImport; and force the manager to
                // false
                processImports = manager.getProcessImports();
                manager.setProcessImports(false);
                manager.setReadFailureHandler(new RefreshReadFailureHandler());
            }
            return manager;
        }

        /**
         * This method checks if any pre-loading needs to be performed, based on
         * the settings in preferences.
         * 
         * @param namespaceUris
         */
        private void handlePreLoading(Iterable<String> namespaceUris) {
            DocumentManagerPreference preferences = DocumentManagerPreference
                    .fromProject(getProject());
            boolean preload = preferences.isPreLoadingRDFOWL() || preferences.isPreLoadingAllways();

            if (preload) {
                updateSubTask("Pre-loading models...");
                for (String uri : namespaceUris) {
                    if (preferences.isPreLoadingAllways()
                            || (uri.equals(RDF.getURI()) || uri.equals(RDFS.getURI()) || uri
                                    .equals(OWL.getURI()))) {

                        if (!ontModel.hasLoadedImport(uri)) {
                            updateSubTask(String.format("Pre-loading <%s>...", uri));

                            try {
                                String url = getDocumentManager().doAltURLMapping(uri);
                                Model preloadModel = LoadModelJob.loadSubModel(uri, url, ontModel);

                                if (readFailureMessage != null) {
                                    ignoreUris.add(uri);
                                }
                                else if (preloadModel != null) {
                                    // prevent the rdf-schema import to be in
                                    // the model (and than shown in the
                                    // ProjectNavigator)
                                    preloadModel.removeAll(null, OWL.imports, (RDFNode) null);
                                    subModels.put(uri, preloadModel);
                                }
                            }
                            catch (Throwable ex) {
                                logger.error(String.format("Unable to pre-load <%s>", uri), ex);
                                ignoreUris.add(uri);
                                readFailureMessage = String
                                        .format("Unable to pre-load <%s> due to the following error:\n\n%s",
                                                uri, ex.getMessage());
                            }
                        }

                        if (readFailureMessage != null) {
                            final String message = readFailureMessage;
                            Display.getDefault().asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    MessageDialog.openError(getSite().getShell(),
                                            "Pre-loading Model", message);
                                }
                            });
                            readFailureMessage = null;
                        }
                    }
                }
            }
        }

        protected boolean handleImports() {
            if (skipAllImports) {
                return false;
            }

            int subModelsSizeBeforeImports = subModels.size();
            // Reset Default readers, these may be reset by ARQ (due to
            // execution of SPARQL query)
            Selector importsSelector = new PredicateSelector(OWL.imports);
            final Set<String> importUris = Sets.newHashSet();

            for (ExtendedIterator<Statement> iter = ontModel.listStatements(importsSelector); iter
                    .hasNext();) {
                Statement stmt = iter.next();
                if (!stmt.getObject().isResource() || stmt.getObject().isAnon()) {
                    continue;
                }
                String uri = stmt.getObject().asResource().getURI();
                if (!ontModel.hasLoadedImport(uri) && !ignoreUris.contains(uri)) {
                    importUris.add(uri);
                }
            }

            importedModelUris.addAll(importUris);
            queuedImportUris.clear();
            queuedImportUris.addAll(importUris);

            String previousUri = new String();
            while (!queuedImportUris.isEmpty()) {
                String uri = queuedImportUris.remove(0);
                if (!handleImport(uri, !uri.equals(previousUri))) {
                    return false;
                }
                previousUri = uri;
            }
            return (subModels.size() != subModelsSizeBeforeImports);
        }

        private boolean isDisabledImport(String uri) {
            IProject project = getProject();
            return DocumentManagerPreference.fromProject(project).isDisabledImport(uri);
        }

        /**
         * Returns true when further imports should be done. Returns false if
         * importing models should be aborted.
         */
        protected boolean handleImport(String uri, boolean notifyUser) {

            if (skipImportUris.contains(uri)) {
                return true;
            }
            if (isDisabledImport(uri)) {
                return true;
            }
            else if (skipAllImports) {
                return false;
            }

            updateSubTask(String.format("Importing <%s> and adding to model...", uri));
            if (notifyUser) {
                notifyBeforeImport(uri);
                if (abort) {
                    return false;
                }
            }

            String url = getDocumentManager().doAltURLMapping(uri);
            try {
                if (subModels.containsKey(uri)) {
                    // Remove old version of the imported submodel
                    Model subModel = subModels.get(uri);
                    ontModel.removeSubModel(subModel);
                    subModels.remove(uri);
                }

                Model importModel = LoadModelJob.loadSubModel(uri, url, ontModel);

                if (readFailureMessage != null) {
                    ignoreUris.add(uri);
                }
                else if (importModel != null) {
                    subModels.put(uri, importModel);
                }
            }
            catch (final Throwable ex) {
                String errorMessage = null;
                if (ex instanceof DoesNotExistException) {
                    errorMessage = String
                            .format("Unable to either retrieve or parse the imported model from <%s>, the location \"%s\" does not appear to exist!",
                                    uri, url);
                    readFailureMessage = "Unable to either retrieve or parse imported model from the given ontology, the location does not appear to exist!";
                }
                else if (ex.getCause() instanceof UnknownHostException) {
                    errorMessage = String
                            .format("Unable to either retrieve or parse the imported model from <%s>, the location \"%s\" has an unknown host!",
                                    uri, url);
                    readFailureMessage = "Unable to either retrieve or parse the imported model from the given ontology, because the specified location appears to be an invalid URL!";
                }
                else {
                    errorMessage = String
                            .format("Unable to either retrieve or parse imported model <%s> from location \"%s\", see inner exception.",
                                    uri, url);
                    readFailureMessage = "Unable to either retrieve or parse imported model from the given ontology due to an unexpected error!";
                }
                logger.error(errorMessage, ex);
                ignoreUris.add(uri);
            }

            if (readFailureMessage != null) {
                final String message = readFailureMessage;
                final String internalUri = uri;
                final String internalUrl = url;
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        handleErrorOnImport(internalUri, internalUrl, message);
                    }
                });
                readFailureMessage = null;
                if (importsAborted) {
                    return false;
                }
            }
            return true;
        }

        private boolean importsAborted = false;

        protected final LinkedList<String> queuedImportUris;

        private void pushOntoQueue(String uri) {
            queuedImportUris.add(0, uri);
        }

        private void abortImports() {
            importsAborted = true;
        }

        /**
         * Notifies the user about an import being executed; if settings prevent
         * the notifications to be given, this method does nothing.
         * 
         * @param publicURI
         */
        private void notifyBeforeImport(final String publicURI) {
            boolean notify = true;

            // Check if apply to all checkbox was checked
            if (applyNotifyActionToAll) {
                applyNotifyAction(previousNotifyAction, publicURI);
            }

            if (IGNORED_CURRENT_SESSION_URIS.contains(publicURI)) {
                notify = false;
            }
            synchronized (notifyPreference) {
                if (notifyPreference.ignoreNotificationForURI(publicURI)) {
                    notify = false;
                }
            }

            if (notify) {
                Display.getDefault().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        Shell shell = Display.getDefault().getActiveShell();
                        NotifyImportDialog dialog = new NotifyImportDialog(shell, project,
                                publicURI, applyNotifyActionToAll);

                        if (dialog.open() == Window.OK) {
                            applyNotifyActionToAll = dialog.isApplyNotifyActionToAll();
                            dialog.runCacheJob(shell);
                            if (dialog.getDocumentManager() != null) {
                                manager = dialog.getDocumentManager();
                            }

                            int action = dialog.getSelectedIndex();
                            applyNotifyAction(action, publicURI);
                            if (applyNotifyActionToAll) {
                                previousNotifyAction = action;
                            }
                        }
                        else {
                            abort = true;
                        }
                    }
                });
            }
        }

        private void applyNotifyAction(final int action, final String uri) {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (action == OPTION_IGNORE_URI_SESSION) {
                        IGNORED_CURRENT_SESSION_URIS.add(uri);
                    }
                    else if (action == OPTION_IGNORE_URI_ALWAYS) {
                        try {

                            synchronized (notifyPreference) {
                                notifyPreference.addIgnoreNotificationURI(uri);
                                notifyPreference.save();
                            }
                        }
                        catch (IOException ex) {
                            logger.error(
                                    "Error saving the import notification preferences, see inner exception",
                                    ex);
                        }
                    }
                }
            });

        }

        private void handleErrorOnImport(final String publicURI, final String altURL, String message) {
            if (skipAllImports) {
                return;
            }
            if (skipImportUris.contains(publicURI)) {
                return;
            }

            Shell shell = getSite().getShell();
            final String[] options = new String[] { "Retry", "Skip", "Abort All" };
            AbstractImportErrorDialog dialog = new AbstractImportErrorDialog(shell,
                    "Importing Model", null, message, SWT.ICON_ERROR, options, FAILURE_RETRY) {

                private boolean disabled = false;
                private Button disabledCheckbox;

                @Override
                protected Control createPostMessageArea(Composite parent) {
                    Composite container = (Composite) super.createPostMessageArea(parent);

                    Composite inner = new Composite(container, SWT.NONE);
                    GridLayoutFactory.fillDefaults().numColumns(2).applyTo(inner);

                    Label iconLabel = new Label(inner, SWT.NONE);
                    iconLabel.setImage(CorePlugin.getDefault().getImage(
                            CorePluginImages.IMG_ONTOLOGY_ERROR));
                    GridDataFactory.defaultsFor(iconLabel).indent(8, 0).span(1, 1)
                            .applyTo(iconLabel);

                    CLabel clabel = new CLabel(inner, SWT.NONE);
                    clabel.setText(publicURI);
                    GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER)
                            .hint(390, SWT.DEFAULT).applyTo(clabel);

                    if (project != null) {
                        Link link = new Link(inner, SWT.NONE);
                        link.setText("Location used (change mapping <a>here</a>):");
                        link.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                updateDocumentManager(project, publicURI, false, new Listener() {

                                    @Override
                                    public void handleEvent(Event event) {
                                        if (event.data instanceof OntDocumentManager) {
                                            manager = (OntDocumentManager) event.data;
                                        }
                                        setReturnCode(FAILURE_RETRY);
                                        close();
                                    }
                                });
                            }
                        });
                        GridDataFactory.fillDefaults().span(2, 1).applyTo(link);

                        iconLabel = new Label(inner, SWT.NONE);
                        iconLabel.setImage(ImportURLUtils.getAltUrlIcon(project, publicURI));

                        GridDataFactory.defaultsFor(iconLabel).indent(8, 0).span(1, 1)
                                .applyTo(iconLabel);

                        String prettyUrl = ImportURLUtils.getAltUrlText(project, publicURI, true);
                        clabel = new CLabel(inner, SWT.NONE);
                        clabel.setText(prettyUrl);
                        GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER)
                                .hint(390, SWT.DEFAULT).applyTo(clabel);

                    }

                    Label label = new Label(inner, SWT.WRAP);
                    label.setText("Do you wish to retry or skip this import, or do you wish to abort this import and all subsequent imports? The skip or abort all setting will be stored until the next time this file is saved.");
                    GridDataFactory.fillDefaults().span(2, 1).hint(420, SWT.DEFAULT).applyTo(label);

                    disabledCheckbox = new Button(inner, SWT.CHECK);
                    disabledCheckbox.setText("Disable import");
                    disabledCheckbox.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            disabled = disabledCheckbox.getSelection();
                        }
                    });
                    GridDataFactory.swtDefaults().span(2, 1).applyTo(disabledCheckbox);

                    return container;
                }

                @Override
                public boolean isDisabled() {
                    return disabled;
                }
            };

            int response = dialog.open();
            if (response == FAILURE_ABORT_ALL) {
                skipAllImports = true;
                abort = true;
                abortImports();
            }
            else if (response == FAILURE_RETRY) {
                pushOntoQueue(publicURI);
            }
            else if (response == FAILURE_SKIP) {
                skipImportUris.add(publicURI);
            }

            DocumentManagerPreference.fromProject(project).setDisabledImport(publicURI,
                    dialog.isDisabled());

        }
    }

    /**
     * Reads the model from the text within the current TextEditor.
     */
    public void readModelFromEditor() {
        clearInferredModel();
        if (validLicense) {
            // TEMP: Boolean to force not to check if project exists
            boolean ignoreNullProject = true;
            if (getProject() == null && !ignoreNullProject) {
                Shell shell = getEditorSite().getShell();
                MessageDialog
                        .open(MessageDialog.ERROR,
                                shell,
                                "Failure to read ontology",
                                "The attempt to read in the model failed. The project it belongs to could not be located and may be closed or may have been deleted.",
                                SWT.NONE);
                close(false);
            }
            else {
                transactionManager.clear();
                RefreshModelJob job = new RefreshModelJob("Reading Model \"" + getPartName() + "\"");
                job.setUser(true);
                job.setRule(OntologyFormEditor.MUTEX_RULE);
                job.schedule();
            }
        }
    }

    private class RefreshSubModelJob extends RefreshModelJob {
        private final String subModelURI;
        private final boolean notifyUser;

        public RefreshSubModelJob(String subModelURI, String name) {
            this(subModelURI, name, false);
        }

        public RefreshSubModelJob(String subModelURI, String name, boolean notifyUser) {
            super(name);
            this.subModelURI = subModelURI;
            this.notifyUser = notifyUser;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            handleImport(subModelURI, notifyUser);
            return Status.OK_STATUS;
        }
    }

    /**
     * Called when save action is performed
     */
    private class RefreshImportsJob extends RefreshModelJob {

        public RefreshImportsJob() {
            super("Refreshing Imports");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask("Refreshing Imports", 4);

            monitor.subTask("Removing imports...");
            for (String uri : importedModelUris) {
                Model removedModel = subModels.remove(uri);
                if (removedModel != null) {
                    ontModel.removeSubModel(removedModel);
                    ontModel.removeLoadedImport(uri);
                }
            }
            monitor.worked(1);

            importedModelUris.clear();

            getDocumentManager();

            if (processImports) {
                // Manually process imports (instead of using Jena's
                // build-in mechanism)
                updateSubTask("Importing additional models...");
                boolean done = false;
                queuedImportUris.clear();

                while (!done) {
                    done = !handleImports();
                }

                getDocumentManager().setReadFailureHandler(null);
                modelContentChanged = true;
                monitor.worked(1);
            }

            updateSubTask("Validating model...");
            executeValidation(ontModel);
            monitor.worked(1);

            updateSubTask(String.format("Updating interface with statements..."));
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (modelContentChanged) {
                        executeProcessors(ontModel);
                        WorkspaceModelFormEditor.this
                                .notify(new ModelActivatedEvent(ontModel, String.format(
                                        "Finished read and import for file \"%s\"", filename)));

                    }
                }
            });

            operationHistory.dispose(undoContext, true, true, false);
            monitor.worked(1);

            return Status.OK_STATUS;
        }
    }

    private boolean refreshWithFileSystemChange() {
        IResource resource = getResource();
        if ((resource != null) && (fileSystemTimeStamp != resource.getLocalTimeStamp())) {
            String title = "Changes in file system detected";
            String message = "This model has been modified directly on the file system. The opened version in this editor does not yet reflect those changes. Do you want to refresh and lose changes?";

            Shell shell = Display.getCurrent().getActiveShell();
            boolean refresh = MessageDialog.openQuestion(shell, title, message);
            if (refresh) {
                // refresh entire model and reopen open resources
                // closeAllResources();
                readModelFromEditor();
                updateDirty(false);
                return true;
            }
            // don't refresh model, but mark as dirty to allow overwriting file
            // system change.
            fileSystemTimeStamp = resource.getLocalTimeStamp();
            updateDirty(true);
        }
        return false;
    }

    private void reimportOutdatedLocalSubModels() {
        if (skipAllImports) {
            return;
        }

        List<String> outdated = getOutdatedSubModelURIs();
        lastUpdatedLocalSubModels = System.currentTimeMillis();

        for (String subModelURI : outdated) {
            if (skipImportUris.contains(subModelURI)) {
                continue;
            }

            if (!disableReimportNotification.contains(subModelURI)) {
                String title = "Import has changed";
                String message = "The following imported model has changed:\n" + subModelURI
                        + "\n\n"
                        + "As such, the triples contained in this import will now be refreshed.";
                String toggleMessage = "Don't show this message again for this import.";
                boolean toggleState = false;
                MessageDialogWithToggle dialog = MessageDialogWithToggle.openInformation(getSite()
                        .getShell(), title, message, toggleMessage, toggleState, null, null);
                toggleState = dialog.getToggleState();
                if (toggleState) {
                    disableReimportNotification.add(subModelURI);
                }
            }

            RefreshSubModelJob job = new RefreshSubModelJob(subModelURI, "Reading import \""
                    + subModelURI + "\"");
            job.setUser(true);
            job.setRule(OntologyFormEditor.MUTEX_RULE);
            job.schedule();
            try {
                job.join();
                ontModel.notifyEvent(new SubModelAddedEvent(ontModel, subModels.get(subModelURI),
                        subModelURI, String.format("Refreshed sub model %s.", subModelURI)));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getOutdatedSubModelURIs() {
        List<String> outdated = Lists.newArrayList();

        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(getProject());
        OntDocumentManager manager = preferences.getDocumentManagerConfig().createManager();
        for (String subModelURI : getSubModelURIs()) {
            String url = manager.doAltURLMapping(subModelURI);
            String localFilePrefix = "file://";
            if (url.startsWith(localFilePrefix)) {
                // the submodel was loaded in from a local file
                File file = new File(url.substring(localFilePrefix.length()));
                if (file.lastModified() > lastUpdatedLocalSubModels) {
                    outdated.add(subModelURI);
                }
            }
        }

        return outdated;
    }

    /**
     * Notify all registered ModelEventListener listeners (to this provider, not
     * the model).
     * 
     * @param event
     */
    private void notify(IModelEvent event) {
        if ("finishedReadingModel".equals(event.getTitle())) {
            modelLoaded = true;
            return;
        }
        else if ("startReadingModel".equals(event.getTitle())) {
            modelLoaded = false;
            return;
        }

        // Secondly, let the label provider be notified; since other listeners
        // (or views) may need the label provider to be updated first!
        if (labelProvider != null) {
            performNotify(labelProvider, event);
        }

        // Create a copy to prevent a concurrency problem, due to the fact that
        // listeners may create new listeners
        LinkedHashSet<ModelEventListener> toNotify = Sets.newLinkedHashSet(modelEventListeners);

        // make sure that the currently active part is being updated first
        if (activePart != null) {
            IWorkbenchPart page = activePart.getPart(false);

            if (toNotify.remove(page)) {
                performNotify((ModelEventListener) page, event);
            }
        }

        for (ModelEventListener listener : toNotify) {
            performNotify(listener, event);
        }
    }

    private void performNotify(ModelEventListener listener, IModelEvent event) {
        if (event instanceof ModelActivatedEvent) {
            listener.modelActivated((ModelActivatedEvent) event);
        }
        else if (event instanceof ModelChangedEvent) {
            listener.modelChanged((ModelChangedEvent) event);
        }
        else if (event instanceof SubModelAddedEvent) {
            listener.subModelAdded((SubModelAddedEvent) event);
        }
        else if (event instanceof SubModelRemovedEvent) {
            listener.subModelRemoved((SubModelRemovedEvent) event);
        }
        else if (event instanceof NamespacePrefixChangedEvent) {
            listener.namespacePrefixChanged((NamespacePrefixChangedEvent) event);
        }
        else if (event instanceof ModelSavedEvent) {
            listener.modelSaved((ModelSavedEvent) event);
        }
        listener.notifyEvent(event);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(DocumentManagerPreference.PREFERENCE_DOCUMENT_MANAGER_CONFIG)
                || property.equals(DocumentManagerPreference.PREFERENCE_PRE_LOADING)) {
            readModelFromEditor();
        }

    }

    /**
     * Returns the content of the underlying file (either in- or outside of
     * Eclipse workspace) as an UTF-8 encoded InputStream.
     * 
     * @return
     * @throws CoreException
     * @throws UnsupportedEncodingException
     */
    private InputStream getEditorUtf8Stream() throws CoreException, UnsupportedEncodingException {
        String encoding = "UTF-8";
        byte[] bytes = null;
        try {
            if (localEditorInput instanceof FileEditorInput) {
                // Input is part of the workspace
                FileEditorInput fileEditorInput = (FileEditorInput) localEditorInput;
                IFile file = fileEditorInput.getFile();
                encoding = file.getCharset();
                try (InputStream stream = file.getContents()) {
                    bytes = IOUtils.toByteArray(stream);
                }
            }
            else if (localEditorInput instanceof FileStoreEditorInput) {
                // Input is an external file
                FileStoreEditorInput storeInput = (FileStoreEditorInput) localEditorInput;
                URI uri = storeInput.getURI();
                IFileStore location = EFS.getLocalFileSystem().getStore(uri);
                File file = location.toLocalFile(EFS.NONE, null);
                if (file != null && file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        bytes = IOUtils.toByteArray(fis);
                    }
                }
            }
            if (bytes != null) {
                String content = new String(bytes, encoding);
                return IOUtils.toInputStream(content, "UTF-8");
            }
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected void updateDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;
            editorDirtyStateChanged();

            // If a change has been made, mark the resource as modified. This
            // will ensure the project builder refreshes if auto-build is
            // enabled.
            if (dirty) {
                markResourceModified(null);
            }
        }
    }

    private void markResourceModified(IProgressMonitor monitor) {
        WorkspaceJob wj = new WorkspaceJob("Make resource dirty") {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                IResource resource = getResource();
                if (resource != null && resource.exists()) {
                    resource.touch(monitor);
                }
                return Status.OK_STATUS;
            }
        };
        wj.schedule();
    }

    /**
     * Saves the editor text to the underlying file used to fill the TextEditor.
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        if (!isDirty()) {
            return;
        }

        if (readOnly) {
            MessageDialog.openInformation(getSite().getShell(), "Read Only",
                    "The file is opened in read only mode, you cannot save it.");
            return;
        }

        Model model = getBaseModel();
        transactionManager.clear();
        try {
            Model writableModel = model;
            if (SkolemizationPreference.isSkolemizationEnabled()) {
                boolean isSkolemizable = Skolemizer.isSkolemizable(writableModel);
                if (isSkolemizable || subModels.containsKey(SKOLEMIZED_MODEL_IRI)) {
                    Model skolemizableModel = ModelFactory.createDefaultModel();
                    Model skolemModel = null;
                    if (subModels.containsKey(SKOLEMIZED_MODEL_IRI)) {
                        skolemModel = subModels.get(SKOLEMIZED_MODEL_IRI);
                    }
                    else {
                        skolemModel = ModelFactory.createDefaultModel();
                        getOntModel().setNsPrefix("skolem", Skolem.NS);
                        subModels.put(SKOLEMIZED_MODEL_IRI, skolemModel);
                    }

                    if (isSkolemizable) {
                        skolemizableModel.setNsPrefixes(model.getNsPrefixMap());
                        skolemizableModel.add(model);
                        skolemizableModel.add(skolemModel);

                        Skolemizer skolemizer = new Skolemizer();
                        skolemizer.setKeepAnonIds(true);
                        writableModel = skolemizer.skolemize(skolemizableModel, true);
                        Model deskolemized = skolemizer.deskolemize(writableModel);

                        skolemModel.removeAll();
                        Skolemizer.extractSkolemData(deskolemized, null, skolemModel);
                        skolemizer.setKeepAnonIds(false);
                        writableModel = skolemizer.skolemize(skolemizableModel, true);
                        ontModel.notifyEvent(new SubModelAddedEvent(ontModel, skolemModel,
                                SKOLEMIZED_MODEL_IRI, "Skolem support sub model added"));
                    }
                }
            }

            if (localEditorInput instanceof FileEditorInput) {
                IFile file = ((FileEditorInput) localEditorInput).getFile();
                String encoding = file.getCharset();
                ModelIOUtils.writeModel(writableModel, file, serializationLang, baseUri, encoding,
                        monitor);
                file.getParent().refreshLocal(1, null);
            }
            else if (localEditorInput instanceof FileStoreEditorInput) {
                IFileStore file = EFS.getStore(((FileStoreEditorInput) localEditorInput).getURI());
                String encoding = ResourcesPlugin.getEncoding();
                ModelIOUtils.writeModel(writableModel, file, serializationLang, baseUri, encoding,
                        monitor);
            }
            else {
                throw new IllegalStateException("The input " + localEditorInput.getClass()
                        + " is not supported by the editor");
            }
            fileSystemTimeStamp = file.getLocalTimeStamp();

            if (importsNeedRefreshing) {
                RefreshImportsJob job = new RefreshImportsJob();
                job.setUser(true);
                job.setRule(OntologyFormEditor.MUTEX_RULE);
                job.schedule();
                try {
                    job.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            updateDirty(false);
            updateModifiedFlag();
            notifyEvent(model, new ModelSavedEvent(model, "Model saved via doSave."));
        }
        catch (CoreException e) {
            logger.error("Error during save of model: " + e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error("I/O Error during save of model: " + e.getMessage(), e);
        }

        skipAllImports = false;
        skipImportUris.clear();
    }

    private void updateModifiedFlag() {
        if (localEditorInput instanceof FileEditorInput) {
            IFile file = ((FileEditorInput) localEditorInput).getFile();
            if (file != null && CoreResourcePropertiesManager.hasSourceLocation(file)) {
                CoreResourcePropertiesManager.setModified(file, true);
                PublishedResourceFileDecorator.refreshAll();
            }
        }
    }

    /**
     * Performs a Save As.. command
     */
    @Override
    public void doSaveAs() {
        if (readOnly) {
            MessageDialog.openInformation(getSite().getShell(), "Read Only",
                    "The file is opened in read only mode, you cannot save it.");
            return;
        }

        SaveOntologyAsDialog dialog = new SaveOntologyAsDialog(getSite().getShell());
        dialog.setShowOnlyProjects(true);
        dialog.setSelectedProject(project);
        dialog.setOriginalName(filename);

        if (dialog.open() == Window.OK) {
            IPath path = dialog.getResult();
            String extension = dialog.getExtension();

            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            if (provider != null) {
                Model model = provider.getBaseModel();
                transactionManager.clear();

                try {
                    IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
                    IFile resource = workspace.getFile(path);
                    String lang = FileUtils.guessLang(String.format("*.%s", extension));
                    String encoding = ResourcesPlugin.getEncoding();
                    ModelIOUtils.writeModel(model, resource, lang, baseUri, encoding, null);

                    close(false);
                    CorePlugin.getDefault().openModelEditor(resource);
                }

                catch (FileNotFoundException ex) {
                    logger.error("FileNotFoundException: " + ex.getMessage(), ex);
                }
                catch (IOException ex) {
                    logger.error("IOException: " + ex.getMessage(), ex);
                }
                catch (CoreException ex) {
                    logger.error("CoreException: " + ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Returns true if this editor allows the underlying model may be saved
     * within a different file.
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void addedStatement(Statement statement) {
        Property predicate = statement.getPredicate();
        if (!importsNeedRefreshing && predicate.equals(OWL.imports)) {
            logger.debug("Import \"" + statement.getObject().asResource().getURI()
                    + "\" has been added!");
            importsNeedRefreshing = true;
            showModelChangedDialog = true;
        }
    }

    @Override
    public void addedStatements(Statement[] statements) {
    }

    @Override
    public void addedStatements(List<Statement> statements) {
    }

    @Override
    public void addedStatements(StmtIterator statements) {
    }

    @Override
    public void addedStatements(Model model) {
    }

    /**
     * Called by the actual model when a notifyEvent has been triggered.
     */
    @Override
    public void notifyEvent(Model model, Object e) {
        if (e instanceof GraphEvents) {
            GraphEvents graphEvents = (GraphEvents) e;

            if (logger.isTraceEnabled()) {
                logger.trace("Part \"" + getPartName() + "\" notifyEvent on model with GraphEvent "
                        + graphEvents.getTitle());
            }

            if (graphEvents.getTitle().equals("remove")) {

            }
            else if (graphEvents.getTitle().equals("removeAll")) {

            }
            else if (graphEvents.getTitle().equals("startRead")) {
                readingModel = true;
            }
            else if (graphEvents.getTitle().equals("finishRead")) {
                readingModel = false;
                notify(new ModelActivatedEvent(model, "Finished reading in of model."));
            }
        }
        else if (e instanceof ModelSavedEvent) {
            ModelSavedEvent event = (ModelSavedEvent) e;

            if (logger.isTraceEnabled()) {
                logger.trace("(" + getPartName() + ") notifyEvent was called due to ModelSaved: "
                        + event.getTitle());
            }

            notify(new ModelSavedEvent(model, "Propagating the ModelSavedEvent to listeners."));
        }

        if (readingModel) {
            return;
        }

        if (e instanceof ModelActivatedEvent) {
            ModelActivatedEvent event = (ModelActivatedEvent) e;

            if (logger.isTraceEnabled()) {
                logger.trace("(" + getPartName()
                        + ") notifyEvent was called due to ModelActivated: " + event.getTitle());
            }

            /*
             * It is highly likely this event was not issued out by this
             * FormEditor (which would issue it out in the event of a GraphEvent
             * with the title "finishRead", see code above). As such, we should
             * consider this event to indicate a substantial change in the
             * contents of this model which cannot simply be represented by a
             * ModelChangedEvent (by, for instance, the Rewrite Namespaces
             * wizard). As such, close all resources, set dirty, and refresh all
             * views.
             */
            // closeAllResources();
            clearInferredModel();
            updateDirty(true);

            notify(new ModelActivatedEvent(model,
                    "Propagating the ModelActivatedEvent to listeners."));
        }
        else if (e instanceof SubModelAddedEvent) {
            SubModelAddedEvent event = (SubModelAddedEvent) e;
            Model subModel = event.getSubModel();
            String subModelUri = event.getSubModelURI();
            subModels.put(subModelUri, subModel);

            if (logger.isTraceEnabled()) {
                logger.trace("(" + getPartName()
                        + ") notifyEvent was called due to SubModelAdded: " + event.getTitle());
            }

            if (!subModelUri.equals(IModelProvider.INFERRED_SUBMODEL_URI)) {
                infModelOutdated = true;
            }
            notify(new SubModelAddedEvent(model, subModel, subModelUri,
                    "Propagating the SubModelAddedEvent to listeners."));
        }
        else if (e instanceof SubModelRemovedEvent) {
            SubModelRemovedEvent event = (SubModelRemovedEvent) e;
            String subModelUri = event.getSubModelURI();
            subModels.remove(subModelUri);

            if (logger.isTraceEnabled()) {
                logger.trace("(" + getPartName()
                        + ") notifyEvent was called due to SubModelRemoved: " + event.getTitle());
            }

            if (!subModelUri.equals(IModelProvider.INFERRED_SUBMODEL_URI)) {
                infModelOutdated = true;
            }

            notify(new SubModelRemovedEvent(model, subModelUri,
                    "Propagating the SubModelRemovedEvent to listeners."));
        }
        else if (e instanceof ModelChangedEvent) {
            ModelChangedEvent event = (ModelChangedEvent) e;

            if (logger.isTraceEnabled()) {
                logger.trace("(" + getPartName() + ") notifyEvent was called due to ModelChanged: "
                        + event.getTitle());
            }

            infModelOutdated = true;
            updateDirty(true);
            notify(new ModelChangedEvent(model, event.getModelChanges(),
                    "Propagating the ModelChangedEvent to listeners."));
        }
        else if (e instanceof NamespacePrefixChangedEvent) {
            NamespacePrefixChangedEvent event = (NamespacePrefixChangedEvent) e;

            if (logger.isTraceEnabled()) {
                logger.trace("(" + getPartName()
                        + ") notifyEvent was called due to NamespacePrefixChanged: "
                        + event.getTitle());
            }

            updateDirty(true);
            notify(new NamespacePrefixChangedEvent(model, event.getChangedPrefixes(),
                    "Propagating the NamespacePrefixChangedEvent to listeners."));
        }

        if (showModelChangedDialog) {
            MessageDialog
                    .openInformation(
                            getSite().getShell(),
                            "Model Changed",
                            "Model has been changed, including changes to imports. Please save the file to propagate the import changes into this model!");
            showModelChangedDialog = false;
        }
    }

    @Override
    public void removedStatement(Statement statement) {
        if (!importsNeedRefreshing && statement.getPredicate().equals(OWL.imports)) {
            logger.debug("Import \"" + statement.getObject().asResource().getURI()
                    + "\" has been removed!");
            importsNeedRefreshing = true;
            showModelChangedDialog = true;
        }
    }

    @Override
    public void removedStatements(Statement[] statements) {
    }

    @Override
    public void removedStatements(List<Statement> statements) {
    }

    @Override
    public void removedStatements(StmtIterator statements) {
    }

    @Override
    public void removedStatements(Model model) {
    }

    @Override
    public void addModelEventListener(ModelEventListener listener) {
        if (!modelEventListeners.contains(listener)) {
            modelEventListeners.add(listener);
        }
    }

    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        modelEventListeners.remove(listener);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        // TODO: Works; but not sure if this is according to the "Eclipse"-way
        // Commented out due to error in core.help plugin
        // if (adapter.equals(IContextProvider.class)) {
        // return new ContextProvider() {
        // @Override
        // public IContext getContext(Object target) {
        // return HelpSystem.getContext(ContextIds.SEMANTIC_MODEL_FORM_EDITOR);
        // }
        // };
        // }
        return super.getAdapter(adapter);
    }

    public static void clearIgnoredSessionURIs() {
        if (IGNORED_CURRENT_SESSION_URIS != null) {
            IGNORED_CURRENT_SESSION_URIS.clear();
        }
    }

    @Override
    public IUndoContext getUndoContext() {
        return undoContext;
    }

    @Override
    public ModelTransaction createTransaction(String description) {
        return transactionManager.createTransaction(description);
    }

    @Override
    public void abortTransaction(ModelTransaction transaction) {
        transactionManager.abortTransaction(transaction);
    }

    @Override
    public boolean commitTransaction(ModelTransaction transaction) {
        transactionManager.commitTransaction(transaction);

        ModelTransactionOperation operation = new ModelTransactionOperation(this, transaction,
                transaction.getDescription());
        operation.addContext(undoContext);

        if (operation.isEmpty()) {
            return true;
        }

        // If there are pending transaction it means that we are inside a nested
        // transaction. The transaction can be considered terminated only when
        // the outer most transaction is committed (maki9ng empty the list of
        // pending transactions)
        if (transactionManager.getPendingTransactions().isEmpty()) {

            try {
                operationHistory.execute(operation, null, null);
            }
            catch (ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }

        return true;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        // if (readOnly) {
        // setPartName("[read-only]");
        // }
        this.readOnly = readOnly;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        // the save on close is performed only if the editor isn't in read-only
        // mode
        return !readOnly;
    }

    /**
     * Being the information related to the import type stored along with the
     * project settings a project rebuild is needed after this operation is
     * performed to make the editor knows about the new type of the import
     * 
     * @param project
     * @param uri
     * @param rebuild
     * @param onChangedListener
     *            called in the end of operation with {@link ChangeEvent} as
     *            parameter. Can be null.
     */
    public static void updateDocumentManager(final IProject project, String uri, boolean rebuild,
            Listener onChangedListener) {
        Shell shell = Display.getDefault().getActiveShell();
        WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(shell, "Mapping",
                "Specify the ontology details below.");
        dialog.setPublicURI(uri);
        dialog.setEnableURI(false);
        dialog.setHideOntologyOptions(true);
        dialog.setHidePrefix(true);

        DocumentManagerPreference preferences = DocumentManagerPreference.fromProject(project);
        WorkspaceDocumentManagerConfiguration configuration = preferences
                .getDocumentManagerConfig();

        if (configuration.listPublicURIs().contains(uri)) {
            dialog.setOntologySpec(configuration.getOntologySpec(uri));
            dialog.setImportType(ImportURLUtils.guessImportType(uri, project));
        }
        if (dialog.open() == Window.OK) {
            configuration.addOntologySpec(dialog.getOntologySpec());
            preferences.setDocumentManagerConfig(configuration);
            try {
                // remove the cache mapping if exists
                CacheManager.fromProject(project).removeFile(uri);
                preferences.save();

                if (rebuild) {
                    SemanticProjectBuildJob rebuildJob = new SemanticProjectBuildJob(project);
                    rebuildJob.setUser(false);
                    rebuildJob.schedule();
                }

                if (onChangedListener != null) {
                    Event event = dialog.getChangeEvent();
                    event.data = configuration.createManager();
                    onChangedListener.handleEvent(event);
                }
            }
            catch (IOException ex) {
                logger.error("Error saving the document manager preferences", ex);
            }
        }
    }

    @Override
    public boolean isModelLoaded() {
        return modelLoaded;
    }
}

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

package com.semmtech.plugin.semmweb.dictionary.views;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.State;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.handlers.EditSelectedResourceHandler;
import com.semmtech.plugin.semmweb.core.jobs.MutexRule;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.OntDocumentManagerRegistry;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.core.widgets.trees.TreeData;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPlugin;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPluginEvent;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPluginEventListener;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPluginImages;
import com.semmtech.plugin.semmweb.dictionary.dnd.ConceptTreeDropListener;
import com.semmtech.plugin.semmweb.dictionary.handlers.SKOSPresentationHandler;
import com.semmtech.plugin.semmweb.dictionary.handlers.SKOSTopLevelElementsHandler;
import com.semmtech.plugin.semmweb.dictionary.preferences.DictionaryPreference;
import com.semmtech.plugin.semmweb.dictionary.preferences.DictionaryPreferenceConstants;
import com.semmtech.plugin.semmweb.dictionary.undo.AddImportsOperation;
import com.semmtech.plugin.semmweb.dictionary.widgets.CollectionTreeData;
import com.semmtech.plugin.semmweb.dictionary.widgets.ConceptSchemeTreeData;
import com.semmtech.plugin.semmweb.dictionary.widgets.ConceptTreeData;
import com.semmtech.semantics.model.ExtendedModelFactory;
import com.semmtech.semantics.skos.Collection;
import com.semmtech.semantics.skos.Concept;
import com.semmtech.semantics.skos.ConceptScheme;
import com.semmtech.semantics.skos.DictionaryModel;
import com.semmtech.semantics.vocabulary.SKOS;
import com.semmtech.ui.plugin.viewers.LazyTreeContentProvider;


public class SKOSConceptView extends AbstractModelListenerView implements
        DictionaryPluginEventListener, ITreeViewerListener, IPropertyChangeListener {
    private static Logger logger = Logger.getLogger(SKOSConceptView.class);
    public static final String ID = "com.semmtech.plugin.semmweb.dictionary.views.concepts";
    public static final MutexRule MUTEX_RULE = new MutexRule();

    private final class ConceptTreeLabelProvider extends CellLabelProvider {
        private final Map<String, Image> cachedImages = Maps.newHashMap();

        @Override
        public void dispose() {
            for (String key : cachedImages.keySet()) {
                Image image = cachedImages.get(key);
                image.dispose();
                image = null;
            }
            super.dispose();
        }

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            String text = getText(element);
            Image image = getImage(element);

            cell.setText(text);
            cell.setImage(image);
        }

        public String getText(Object element) {
            if (element instanceof Concept) {
                String text = "";
                Concept concept = (Concept) element;
                if (concept.hasProperty(SKOS.notation)) {
                    Literal firstNotation = concept.listNotations().next();
                    text = String.format("%s - ", firstNotation.getString());
                }
                for (DisplayLanguage language : LanguagesPreference.getDisplayLanguages()) {
                    if (concept.getPrefLabel(language.getCode()) != null) {
                        return text + concept.getPrefLabel(language.getCode());
                    }
                }
            }
            else if (element instanceof Collection) {
                String text = "";
                Collection collection = (Collection) element;
                if (collection.hasProperty(SKOS.notation)) {
                    Literal firstNotation = collection.listNotations().next();
                    text = String.format("%s - ", firstNotation.getString());
                }
                for (DisplayLanguage language : LanguagesPreference.getDisplayLanguages()) {
                    if (collection.getPrefLabel(language.getCode()) != null) {
                        return text + collection.getPrefLabel(language.getCode());
                    }
                }
            }
            else if (element instanceof ConceptScheme) {
                String text = "";
                ConceptScheme scheme = (ConceptScheme) element;
                if (scheme.hasProperty(SKOS.notation)) {
                    Literal firstNotation = scheme.listNotations().next();
                    text = String.format("%s - ", firstNotation.getString());
                }
                for (DisplayLanguage language : LanguagesPreference.getDisplayLanguages()) {
                    List<Literal> titles = scheme.listTitles(language.getCode()).toList();
                    if (titles.size() > 0) {
                        return text + titles.get(0).getString();
                    }
                }
            }
            else if ((element instanceof Literal) && (hasModelProvider())) {
                return getLabelProvider().getText(element);
            }
            if (element instanceof Resource) {
                return ((Resource) element).getURI();
            }
            return null;
        }

        public Image getImage(Object element) {
            String imageKey = null;

            if (element instanceof ConceptScheme) {
                imageKey = DictionaryPluginImages.IMG_CONCEPT_SCHEME;
            }
            else if (element instanceof Concept) {
                imageKey = DictionaryPluginImages.IMG_CONCEPT;
                if (((Concept) element).isTopConcept()) {
                    imageKey = DictionaryPluginImages.IMG_TOP_CONCEPT;
                }
            }
            else if (element instanceof Collection) {
                imageKey = DictionaryPluginImages.IMG_COLLECTION;
            }
            else if ((element instanceof Literal) && (hasModelProvider())) {
                return getLabelProvider().getImage(element);
            }
            if (imageKey != null) {
                if (!cachedImages.containsKey(imageKey)) {
                    cachedImages.put(
                            imageKey,
                            AbstractUIPlugin.imageDescriptorFromPlugin(DictionaryPlugin.PLUGIN_ID,
                                    imageKey).createImage());
                }
                return cachedImages.get(imageKey);
            }
            return null;
        }

        @Override
        public int getToolTipDisplayDelayTime(Object object) {
            return 80;
        }

        @Override
        public int getToolTipTimeDisplayed(Object object) {
            return 10000;
        }

        @Override
        public Point getToolTipShift(Object object) {
            return new Point(8, 10);
        }

        @Override
        public String getToolTipText(Object element) {
            if (element instanceof Resource) {
                return "resource-tooltip";
            }
            return null;
        }
    }

    private String providingModelUri;
    private static Map<String, Object[]> expandedElements = Maps.newHashMap();
    private boolean restoringExpandedElements;
    private boolean settingViewerInput;

    private Set<Collection> collections;
    private Set<ConceptScheme> conceptSchemes;
    private Set<Concept> conceptsWithoutCollection;
    private Set<Concept> concepts;

    private DictionaryModel dictionary;
    private RefreshConceptsJob refreshJob;
    private IJobChangeListener refreshFinished;
    private TreeViewer treeViewer;

    public SKOSConceptView() {
        initialize();
    }

    private void initialize() {
        conceptSchemes = Sets.newHashSet();
        collections = Sets.newHashSet();
        conceptsWithoutCollection = Sets.newHashSet();
        concepts = Sets.newHashSet();
    }

    protected State getSKOSPresentationState() {
        ICommandService service = (ICommandService) getSite().getService(ICommandService.class);
        Command command = service.getCommand(SKOSPresentationHandler.ID);
        return command.getState(RadioState.STATE_ID);
    }

    protected State getSKOSTopLevelElementsState() {
        ICommandService service = (ICommandService) getSite().getService(ICommandService.class);
        Command command = service.getCommand(SKOSTopLevelElementsHandler.ID);
        return command.getState(RadioState.STATE_ID);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        treeViewer = new TreeViewer(parent, SWT.VIRTUAL | SWT.WRAP);
        treeViewer.setUseHashlookup(true);
        treeViewer.setContentProvider(new LazyTreeContentProvider() {

            @SuppressWarnings("unused")
            @Override
            public void updateElement(Object parent, int index) {
                State presentationState = getSKOSPresentationState();
                State topLevelElementsState = getSKOSTopLevelElementsState();

                // / TODO: Include a default or initializer option
                // String[] languageOrder =
                // LanguagesPreference.getPreferredLanguageOrder();

                List<DisplayLanguage> displayLanguages = LanguagesPreference.getDisplayLanguages();
                if (parent instanceof SKOSConceptView) {
                    if (SKOSTopLevelElementsHandler.STATE_CONCEPT_SCHEMES
                            .equals(topLevelElementsState.getValue())) {
                        synchronized (conceptSchemes) {
                            List<ConceptScheme> ordered = Lists.newArrayList(conceptSchemes);
                            ConceptSchemeTreeData element = new ConceptSchemeTreeData(ordered
                                    .get(index));
                            int topCount = element.listTopConcepts().toList().size();

                            treeViewer.replace(parent, index, element);
                            treeViewer.setChildCount(element, topCount);
                        }
                    }
                    else if (SKOSTopLevelElementsHandler.STATE_CONCEPTS
                            .equals(topLevelElementsState.getValue())) {
                        synchronized (concepts) {
                            List<Concept> ordered = Lists.newArrayList(concepts);
                            ConceptTreeData element = new ConceptTreeData(ordered.get(index));
                            int narrowerCount = element.listNarrowerConcepts().toList().size();

                            treeViewer.replace(parent, index, element);
                            treeViewer.setChildCount(element, narrowerCount);
                        }
                    }
                    else if (SKOSTopLevelElementsHandler.STATE_COLLECTIONS
                            .equals(topLevelElementsState.getValue())) {
                        // List<Collection> collections =
                        // ((DictionaryModel)parent).listCollections().toList();
                        //
                        // if (index < collections.size()) {
                        // CollectionTreeData element = new
                        // CollectionTreeData(collections.get(index));
                        // int childCount =
                        // element.listMembers().toList().size();
                        //
                        // treeViewer.replace(parent, index, element);
                        // treeViewer.setChildCount(element, childCount);
                        // }
                        // else {
                        // int conceptIndex = index - collections.size();
                        // ConceptTreeData element = new
                        // ConceptTreeData(conceptsWithoutCollection.get(conceptIndex));
                        // treeViewer.replace(parent, index, element);
                        // treeViewer.setChildCount(element, 0);
                        // }
                    }
                }

                if (parent instanceof ConceptSchemeTreeData) {
                    ConceptSchemeTreeData schemeData = (ConceptSchemeTreeData) parent;
                    List<Concept> concepts = schemeData.listTopConcepts().toList();
                    ConceptTreeData topConceptData = new ConceptTreeData(concepts.get(index));
                    topConceptData.setParent(schemeData);

                    int childCount = topConceptData.listNarrowerConcepts().toList().size();
                    // childCount +=
                    // topConcept.listPrefLabels().toList().size();
                    // childCount += topConcept.listAltLabels().toList().size();

                    treeViewer.replace(parent, index, topConceptData);
                    treeViewer.setChildCount(topConceptData, childCount);
                }
                else if (parent instanceof CollectionTreeData) {
                    CollectionTreeData collectionData = (CollectionTreeData) parent;
                    List<Concept> members = collectionData.listMembers().toList();
                    ConceptTreeData memberData = new ConceptTreeData(members.get(index));
                    memberData.setParent(collectionData);

                    treeViewer.replace(parent, index, memberData);
                    treeViewer.setChildCount(memberData, memberData.listNarrowerConcepts().toList()
                            .size());
                }
                else if (parent instanceof ConceptTreeData) {
                    ConceptTreeData conceptData = (ConceptTreeData) parent;
                    List<Concept> concepts = conceptData.listNarrowerConcepts().toList();
                    List<Literal> prefLabels = conceptData.listPrefLabels().toList();
                    List<Literal> altLabels = conceptData.listAltLabels().toList();
                    // / TODO: Related and Broader (?in tree) - see toggle
                    // actions
                    int childCount = 0;
                    if (index < concepts.size()) {
                        ConceptTreeData narrowerConceptData = new ConceptTreeData(concepts
                                .get(index));
                        narrowerConceptData.setParent(conceptData);

                        childCount = narrowerConceptData.listNarrowerConcepts().toList().size();
                        // childCount +=
                        // narrowerConcept.listPrefLabels().toList().size();
                        // childCount +=
                        // narrowerConcept.listAltLabels().toList().size();

                        treeViewer.replace(parent, index, narrowerConceptData);
                        treeViewer.setChildCount(narrowerConceptData, childCount);
                    }
                    else if (index >= concepts.size()
                            && index < (concepts.size() + prefLabels.size())) {

                        Literal prefLabel = prefLabels.get(index - concepts.size());
                        treeViewer.replace(parent, index, prefLabel);
                        treeViewer.setChildCount(prefLabel, 0);
                    }
                    else if (index >= (concepts.size() + prefLabels.size())
                            && index < (concepts.size() + prefLabels.size() + altLabels.size())) {

                        Literal altLabel = altLabels.get(index
                                - (concepts.size() + prefLabels.size()));
                        treeViewer.replace(parent, index, altLabel);
                        treeViewer.setChildCount(altLabel, 0);
                    }
                }
            }

            @Override
            public Object getParent(Object element) {
                if (element instanceof TreeData) {
                    return ((TreeData) element).getParent();
                }
                return null;
            }
        });
        treeViewer.setLabelProvider(new ConceptTreeLabelProvider());
        treeViewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { PropertyTransfer
                .getInstance() }, new ConceptTreeDropListener(treeViewer));
        DndUtils.addDragSupport(treeViewer, getOntModel());
        treeViewer.setInput(null);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                // Retrieve the corresponding Services
                IHandlerService handlerService = (IHandlerService) getSite().getService(
                        IHandlerService.class);
                ICommandService commandService = (ICommandService) getSite().getService(
                        ICommandService.class);

                // Retrieve the command
                Command editCommand = commandService.getCommand(EditSelectedResourceHandler.ID);

                // Explicitly specify a selection when activating the button
                // getSite().getSelectionProvider().setSelection(event.getSelection());

                // Create an ExecutionEvent
                ExecutionEvent executionEvent = handlerService.createExecutionEvent(editCommand,
                        new Event());

                // Launch the command
                try {
                    editCommand.executeWithChecks(executionEvent);
                }
                catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (NotDefinedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (NotEnabledException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (NotHandledException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        treeViewer.addTreeListener(this);
        ResourceViewerToolTipSupport.enableFor(treeViewer);

        refreshFinished = new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        refreshViewer();
                    }
                });
            }
        };

        getSite().setSelectionProvider(treeViewer);

        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
        DictionaryPlugin.getDefault().addPluginListener(this);
        DictionaryPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

        createContextMenu();
        setInitialized(true);
    }

    @Override
    public void dispose() {
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        DictionaryPlugin.getDefault().removePluginListener(this);
        DictionaryPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        super.dispose();
    }

    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        getSite().registerContextMenu(menuManager, treeViewer);

        Control control = treeViewer.getControl();
        Menu menu = menuManager.createContextMenu(control);
        control.setMenu(menu);
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    private void importDublinCoreTermsAndSKOS(IModelProvider provider) {
        Shell shell = getSite().getShell();
        boolean importNow = MessageDialog
                .openQuestion(
                        getSite().getShell(),
                        "SKOS and Dublin Core",
                        "The ontologies SKOS and/or Dublin Core do not appear to be imported into this one, do you wish to import these ontologies now?");
        if (importNow) {
            // / This code below is also used in the AddImportHandler (should be
            // refactored in such a way that this method can be reused)
            OntModel model = provider.getOntModel();
            OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
            OntDocumentManager manager = new OntDocumentManager();
            manager.setProcessImports(false);
            spec.setDocumentManager(manager);

            OntModel empty = ModelFactory.createOntologyModel(spec, model.getBaseModel());
            List<Ontology> ontologies = empty.listOntologies().toList();

            String baseOntologyURI = null;
            Ontology baseOntology = null;

            if (ontologies.size() > 1) {
                logger.warn("The model has more than one ontology defined!");
                String message = "The model has more than one ontology defined, future release will allow you to select one of the following (TODO)\n\n";
                for (Ontology ontology : ontologies) {
                    logger.warn(ontology.getURI());
                    message += "\t" + ontology.getURI() + "\n";
                }
                MessageDialog dialog = new MessageDialog(shell, "Add Import", null, message,
                        MessageDialog.INFORMATION, new String[] { "OK" }, 0);
                dialog.open();
            }
            else {
                if (ontologies.size() == 1) {
                    baseOntologyURI = ontologies.get(0).getURI();
                }

                if (baseOntologyURI != null) {
                    baseOntology = model.getOntology(baseOntologyURI);
                }
                else if (ontologies.size() == 1) {
                    for (Ontology ontology : model.listOntologies().toList()) {
                        if (ontology.isAnon() || ontology.getURI() == null) {
                            baseOntology = ontology;
                            break;
                        }
                    }
                }
                else if (model.getNsPrefixURI("") != null) {
                    baseOntology = model.createOntology(model.getNsPrefixURI(""));
                }
                else {
                    baseOntology = model.createOntology(null);
                }

                Map<String, String> ontologyUriWithPrefix = Maps.newHashMap();
                ontologyUriWithPrefix.put(SKOS.NS, "skos");
                ontologyUriWithPrefix.put(DCTerms.NS, "dcterms");
                performAddImportsOperation(provider, baseOntology, ontologyUriWithPrefix);
            }
        }
    }

    private void performAddImportsOperation(IModelProvider provider, Ontology baseOntology,
            Map<String, String> ontologyUriWithPrefix) {
        AddImportsOperation operation = new AddImportsOperation(provider, baseOntology,
                ontologyUriWithPrefix);
        performUndoRedoOperation(operation);
    }

    private void startRefreshJob() {
        if (refreshJob != null) {
            refreshJob.cancel();
        }
        refreshJob = new RefreshConceptsJob();
        refreshJob.addJobChangeListener(refreshFinished);
        refreshJob.setRule(MUTEX_RULE);
        refreshJob.schedule();
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refreshWithChangedModelInformation();
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        refreshWithChangedModelInformation();
    }

    private void refreshWithChangedModelInformation() {
        // Clear collections
        synchronized (conceptSchemes) {
            conceptSchemes = Sets.newHashSet();
        }
        synchronized (collections) {
            collections = Sets.newHashSet();
        }
        synchronized (concepts) {
            concepts = Sets.newHashSet();
        }
        synchronized (conceptsWithoutCollection) {
            conceptsWithoutCollection = Sets.newHashSet();
        }

        providingModelUri = getModelURI();

        if (hasModelProvider()) {
            // Check for SKOS and Dublic Core Terms import
            OntModel model = getOntModel();
            if (model != null) {
                ModelMaker maker = ModelFactory.createMemModelMaker();

                // TODO: Correct and check the current project
                // FIXME: See todo!
                OntDocumentManager manager = OntDocumentManagerRegistry.getInstance()
                        .getDocumentManager();
                manager.setProcessImports(false);
                OntModelSpec spec = new OntModelSpec(maker, maker, manager, null,
                        ProfileRegistry.OWL_LANG);

                dictionary = ExtendedModelFactory.createDictionaryModel(spec, model);

                boolean autoImport = DictionaryPreference.autoImport();
                boolean hasSkos = model.getNsPrefixMap().values().contains(SKOS.NS)
                        && model.listResourcesWithProperty(OWL.imports, SKOS.NAMESPACE).hasNext();
                boolean hasDublinCoreTerms = model.getNsPrefixMap().values().contains(DCTerms.NS)
                        && model.listResourcesWithProperty(OWL.imports, DCTerms.NAMESPACE)
                                .hasNext();

                if (autoImport && (!hasSkos || !hasDublinCoreTerms)) {
                    importDublinCoreTermsAndSKOS(getModelProvider());
                }
            }

            // Start a refresh job
            startRefreshJob();
        }
        refreshViewer();
    }

    private void refreshViewer() {
        DndUtils.addDragSupport(treeViewer, getOntModel());
        treeViewer.setInput(this);
        State topLevelElementsState = getSKOSTopLevelElementsState();
        if (SKOSTopLevelElementsHandler.STATE_CONCEPT_SCHEMES.equals(topLevelElementsState
                .getValue())) {
            synchronized (conceptSchemes) {
                treeViewer.getTree().setItemCount(conceptSchemes.size());
            }
        }
        else if (SKOSTopLevelElementsHandler.STATE_CONCEPTS
                .equals(topLevelElementsState.getValue())) {
            synchronized (concepts) {
                treeViewer.getTree().setItemCount(concepts.size());
            }
        }
        else if (SKOSTopLevelElementsHandler.STATE_COLLECTIONS.equals(topLevelElementsState
                .getValue())) {
            int count = 0;
            synchronized (collections) {
                count += collections.size();
            }
            synchronized (conceptsWithoutCollection) {
                count += conceptsWithoutCollection.size();
            }
            treeViewer.getTree().setItemCount(count);
        }

        if (providingModelUri != null) {
            restoringExpandedElements = true;
            treeViewer.expandToLevel(2);
            if (expandedElements.containsKey(providingModelUri)) {
                treeViewer.setExpandedElements(expandedElements.get(providingModelUri));
            }
            restoringExpandedElements = false;
        }
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        super.partClosed(part);
        if (part instanceof IModelProvider) {
            if (expandedElements.containsKey(part)) {
                expandedElements.remove(part);
            }
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        if (!restoringExpandedElements && !settingViewerInput) {
            List<Object> elements = new ArrayList<>(Arrays.asList(treeViewer.getExpandedElements()));
            elements.remove(event.getElement());
            expandedElements.put(providingModelUri, elements.toArray());
        }
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        if (!restoringExpandedElements && !settingViewerInput) {
            List<Object> elements = new ArrayList<>(Arrays.asList(treeViewer.getExpandedElements()));
            elements.add(event.getElement());
            expandedElements.put(providingModelUri, elements.toArray());
        }
    }

    @Override
    public void notifyEvent(DictionaryPluginEvent event) {
        if (event.getID().equals(DictionaryPluginEvent.SKOS_TOP_ELEMENT_CHANGED)
                || event.getID().equals(DictionaryPluginEvent.SKOS_PRESENTATION_CHANGED)) {
            expandedElements.clear();
            refreshViewer();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(DictionaryPreferenceConstants.PREFERENCE_AUTO_IMPORT)) {
            refreshWithChangedModelInformation();
        }
        if (property.equals(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING)
                || property.equals(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES)) {
            refreshViewer();
        }
    }

    /**
     * Job for refreshing SKOS Concepts
     * 
     * @author Mike Henrichs
     * 
     */
    private class RefreshConceptsJob extends Job {
        public RefreshConceptsJob() {
            super("Refreshing SKOS Concepts");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask(getName(), 1);
            monitor.subTask("Creating inference model...");

            if (dictionary != null) {
                monitor.subTask("Finding concept schemes...");
                synchronized (conceptSchemes) {
                    for (ExtendedIterator<ConceptScheme> iter = dictionary.listConceptSchemes(); iter
                            .hasNext();) {
                        ConceptScheme scheme = iter.next();
                        conceptSchemes.add(scheme);
                    }
                }
                monitor.subTask("Finding concept hierarchy...");
                List<Concept> withoutCollection = Lists.newArrayList();
                synchronized (concepts) {
                    for (ExtendedIterator<Concept> iter = dictionary.listConcepts(); iter.hasNext();) {
                        Concept concept = iter.next();
                        concepts.add(concept);
                        if (!concept.listCollections().hasNext()) {
                            withoutCollection.add(concept);
                        }
                    }
                }
                synchronized (conceptsWithoutCollection) {
                    conceptsWithoutCollection = Sets.newHashSet(withoutCollection);
                }
                monitor.subTask("Finding collections...");
                synchronized (collections) {
                    for (ExtendedIterator<Collection> iter = dictionary.listCollections(); iter
                            .hasNext();) {
                        Collection collection = iter.next();
                        collections.add(collection);
                    }
                }
            }
            monitor.worked(1);
            monitor.done();
            return Status.OK_STATUS;
        }
    }

    @Override
    protected void cleanup() {

    }
}

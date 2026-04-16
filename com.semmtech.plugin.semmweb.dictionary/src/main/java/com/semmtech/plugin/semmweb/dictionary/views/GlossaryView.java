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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.views.AbstractModelListenerView;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPlugin;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPluginImages;
import com.semmtech.plugin.semmweb.dictionary.model.DictionaryEntry;
import com.semmtech.plugin.semmweb.dictionary.model.DictionaryLabelKey;
import com.semmtech.plugin.semmweb.dictionary.model.DictionaryModel;


public class GlossaryView extends AbstractModelListenerView {
    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.triples";
    private TreeViewer treeViewer;

    private List<String> shownLanguages = new ArrayList<>(Arrays.asList("en"));

    private IAction languageNLAction;
    private IAction languageENAction;
    private IAction languageDEAction;
    private IAction languageFRAction;
    private IAction languageUnknownAction;

    private IAction allLanguagesAction;
    private IAction noLanguagesAction;

    public GlossaryView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        treeViewer = new TreeViewer(parent, SWT.VIRTUAL | SWT.WRAP);
        treeViewer.setUseHashlookup(true);
        treeViewer.setContentProvider(new ILazyTreeContentProvider() {
            private DictionaryModel dictionaryModel = null;

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                if (newInput instanceof DictionaryModel) {
                    dictionaryModel = ((DictionaryModel) newInput);
                }
                else {
                    dictionaryModel = new DictionaryModel();
                }

                // if (newInput == null) {
                // treeViewer.setChildCount(TreePath.EMPTY, 1);
                // treeViewer.replace(TreePath.EMPTY, 0, new
                // DictionaryLabelKey("", "Empty", ""));
                // }
            }

            @Override
            public void dispose() {
            }

            @Override
            public void updateElement(Object parent, int index) {
                if (parent instanceof DictionaryModel) {
                    DictionaryLabelKey key = dictionaryModel.listLabelKeys().get(index);
                    DictionaryEntry entry = dictionaryModel.getEntry(key);
                    int count = entry.getLabels().size() + entry.getComments().size();

                    treeViewer.replace(parent, index, key);
                    treeViewer.setChildCount(key, count);
                }
                else if (parent instanceof DictionaryLabelKey) {
                    DictionaryLabelKey key = (DictionaryLabelKey) parent;
                    DictionaryEntry entry = dictionaryModel.getEntry(key);

                    if (index == 0) {
                        treeViewer.replace(parent, 0, entry.getResource());
                    }
                    else if (index < entry.getLabels().size()) {
                        // // Label
                        List<Literal> otherLabels = entry.getOtherLabels(key.getLabel(),
                                key.getLanguage());
                        treeViewer.replace(parent, index, otherLabels.get(index - 1));
                    }
                    else {
                        // / Comment
                        int labelCount = entry.getLabels().size();
                        treeViewer.replace(parent, index,
                                entry.getComments().get(index - labelCount));
                    }
                }
            }

            @Override
            public void updateChildCount(Object element, int currentChildCount) {
            }

            @Override
            public Object getParent(Object element) {
                return null;
            }
        });

        final LabelProvider labelProvider = getLabelProvider();

        treeViewer.setLabelProvider(new LabelProvider() {

            Image keyImage = AbstractUIPlugin.imageDescriptorFromPlugin(DictionaryPlugin.PLUGIN_ID,
                    DictionaryPluginImages.IMG_TAG_BLUE).createImage();

            // ImageDescriptor descriptor =
            // ImageDescriptor.createFromURL(FileLocator.find(SEMMWebDictionaryPlugin.getDefault().getBundle(),
            // new Path("icons/tag_blue.png"), null));
            // Image keyImage = descriptor.createImage();

            @Override
            public String getText(Object element) {
                if (element instanceof Resource) {
                    return labelProvider.getText(element);
                }
                else if (element instanceof Literal) {
                    return labelProvider.getText(element);
                }
                else if (element instanceof DictionaryLabelKey) {
                    DictionaryLabelKey key = (DictionaryLabelKey) element;
                    return key.getLabel();
                }
                return null;
            }

            @Override
            public Image getImage(Object element) {
                if (element instanceof Resource) {
                    return labelProvider.getImage(element);
                }
                else if (element instanceof Literal) {
                    return labelProvider.getImage(element);
                }
                else if (element instanceof DictionaryLabelKey) {
                    return keyImage;
                }
                return null;
            }

        });

        treeViewer.setInput(null);

        getSite().setSelectionProvider(treeViewer);

        makeActions();
        contributeToActionBars();
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager toolBarManager) {
    }

    private void fillLocalPullDown(IMenuManager menuManager) {
        MenuManager languageMenu = new MenuManager("Languages");
        languageMenu.add(languageDEAction);
        languageMenu.add(languageENAction);
        languageMenu.add(languageFRAction);
        languageMenu.add(languageNLAction);
        languageMenu.add(languageUnknownAction);
        languageMenu.add(new Separator());
        languageMenu.add(allLanguagesAction);
        languageMenu.add(noLanguagesAction);

        menuManager.add(languageMenu);

        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void makeActions() {
        languageDEAction = new Action() {
            @Override
            public void run() {
                toggelShownLanguage("de");
                updateLanguageCheckStates();
                refreshLabels();
            }
        };
        languageDEAction.setText("Deutsch");

        languageENAction = new Action() {
            @Override
            public void run() {
                toggelShownLanguage("en");
                refreshLabels();
            }
        };
        languageENAction.setText("English");

        languageFRAction = new Action() {
            @Override
            public void run() {
                toggelShownLanguage("fr");
                refreshLabels();
            }
        };
        languageFRAction.setText("Francais");

        languageNLAction = new Action() {
            @Override
            public void run() {
                toggelShownLanguage("nl");
                refreshLabels();
            }
        };
        languageNLAction.setText("Nederlands");

        languageUnknownAction = new Action() {
            @Override
            public void run() {
                toggelShownLanguage("");
                refreshLabels();
            }
        };
        languageUnknownAction.setText("Unknown");

        allLanguagesAction = new Action() {
            @Override
            public void run() {
                shownLanguages = new ArrayList<>(Arrays.asList("de", "en", "fr", "nl", ""));
                updateLanguageCheckStates();
                refreshLabels();
            }
        };
        allLanguagesAction.setText("All");

        noLanguagesAction = new Action() {
            @Override
            public void run() {
                shownLanguages.clear();
                updateLanguageCheckStates();
                refreshLabels();
            }
        };
        noLanguagesAction.setText("None");

        updateLanguageCheckStates();
    }

    protected void updateLanguageCheckStates() {
        languageDEAction.setChecked(shownLanguages.contains("de"));
        languageENAction.setChecked(shownLanguages.contains("en"));
        languageFRAction.setChecked(shownLanguages.contains("fr"));
        languageNLAction.setChecked(shownLanguages.contains("nl"));
        languageUnknownAction.setChecked(shownLanguages.contains(""));
    }

    private void toggelShownLanguage(String language) {
        if (shownLanguages.contains(language)) {
            shownLanguages.remove(language);
        }
        else {
            shownLanguages.add(language);
        }
        updateLanguageCheckStates();
    }

    @Override
    public void setFocus() {
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
        refreshLabels();
    }

    private void refreshLabels() {
        DictionaryModel dictionaryModel = new DictionaryModel(getOntModel(), shownLanguages);
        treeViewer.setInput(dictionaryModel);
        int count = dictionaryModel.listLabelKeys().size();
        treeViewer.getTree().setItemCount(count);

    }

    @Override
    protected void cleanup() {

    }
}

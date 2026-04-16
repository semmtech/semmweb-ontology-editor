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


import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * 
 * @author Sander Stolk
 */
@SuppressWarnings("unused")
public class ModelResourceDiagramContent extends AbstractModelResourceContent {
    private Map<Resource, GraphNode> nodes = Maps.newHashMap();
    private Graph graph;
    // private Composite composite;
    private int layout = 1;

    private MenuManager menuManager;
    private Property selectedProperty = null;
    private Resource selectedResource = null;
    private GraphNode selectedNode = null;

    private LabelProvider labelProvider;

    public ModelResourceDiagramContent(ModelResourceFormPage page) {
        super(page);
        labelProvider = getModelProvider().getLabelProvider();
    }

    @Override
    public String getTitle() {
        return "Diagram";
    }

    @Override
    public Image getImage() {
        // FIXME: This needs a better icon
        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_GRAPH);
    }

    @Override
    public boolean isViewable() {
        return true;
    }

    @Override
    protected Control createContent(Composite parent) {
        FormToolkit toolkit = getToolkit();

        Composite outerComposite = toolkit.createComposite(parent, SWT.NONE);
        outerComposite.setLayout(new FillLayout(SWT.VERTICAL));

        Composite composite = toolkit.createComposite(outerComposite);
        composite.setLayout(new FillLayout());

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        labelProvider = provider.getLabelProvider();

        // Graph will hold all other objects
        graph = new Graph(composite, SWT.NONE);
        graph.setLayout(new FillLayout(SWT.HORIZONTAL));
        graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
                true);
        // Selection listener on graphConnect or GraphNode is not supported
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
        graph.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                selectedProperty = null;
                selectedResource = null;
                selectedNode = null;
                if (graph.getSelection().size() == 1) {
                    Object selected = graph.getSelection().get(0);
                    if (selected instanceof GraphNode) {
                        selectedNode = (GraphNode) selected;
                        selectedResource = (Resource) selectedNode.getData();
                    }
                    else if (selected instanceof GraphConnection) {
                        selectedProperty = (Property) ((GraphConnection) selected).getData();
                    }
                }
            }
        });

        createResourceDiagram();
        createContextMenu();

        return outerComposite;
    }

    private void createContextMenu() {
        menuManager = new MenuManager();

        Action expandAction = new Action() {
            @Override
            public void run() {
                if (selectedResource != null) {
                    createResourceNode(selectedResource);
                    setLayoutManager();
                }
            }
        };
        expandAction.setText("Expand");

        Action removeAction = new Action() {
            @Override
            public void run() {
                if (selectedNode != null && selectedResource != null) {
                    nodes.remove(selectedResource);
                    selectedNode.dispose();
                    setLayoutManager();
                }
            }
        };
        removeAction.setText("Remove");
        removeAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_DELETE));

        Action refreshAction = new Action() {
            @Override
            public void run() {
                for (GraphNode node : nodes.values()) {
                    node.dispose();
                }
                nodes.clear();
                createResourceDiagram();
                setLayoutManager();
            }
        };
        refreshAction.setText("Refresh");
        refreshAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_REFRESH));

        Action exportImageAction = new Action() {
            @Override
            public void run() {
                exportImage();
            }
        };

        MenuManager exportMenu = new MenuManager("Export Diagram");

        exportImageAction.setText("Save as PNG...");
        exportImageAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                CorePluginImages.IMG_SAVE_IMAGE_AS));

        Action copyImageAction = new Action() {
            @Override
            public void run() {
                copyImage();
            }
        };
        copyImageAction.setText("Copy to Clipboard");

        exportMenu.add(copyImageAction);

        exportMenu.add(exportImageAction);

        menuManager.add(expandAction);
        menuManager.add(new Separator());
        menuManager.add(removeAction);
        menuManager.add(new Separator());
        menuManager.add(refreshAction);
        menuManager.add(new Separator());
        menuManager.add(exportMenu);

        Menu menu = menuManager.createContextMenu(graph);
        graph.setMenu(menu);
    }

    private Image createImage() {
        IFigure figure = graph.getContents();
        Rectangle bounds = figure.getBounds();
        Point size = new Point(figure.getSize().width + 20, figure.getSize().height + 20);
        Point viewLocation = graph.getViewport().getViewLocation();
        final Image image = new Image(null, size.x, size.y);
        GC gc = new GC(image);
        SWTGraphics swtGraphics = new SWTGraphics(gc);

        swtGraphics.translate(-1 * bounds.x + viewLocation.x, -1 * bounds.y + viewLocation.y);
        graph.getViewport().paint(swtGraphics);
        gc.copyArea(image, 0, 0);
        gc.dispose();

        return image;
    }

    protected void copyImage() {
        Clipboard clipboard = new Clipboard(Display.getDefault());
        final Image image = createImage();
        ImageTransfer imageTransfer = ImageTransfer.getInstance();
        clipboard.setContents(new Object[] { image.getImageData() },
                new Transfer[] { imageTransfer });
    }

    protected void exportImage() {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
        dialog.setFilterExtensions(new String[] { "*.png" });
        String filename = dialog.open();
        if (!Strings.isNullOrEmpty(filename)) {
            final Image image = createImage();
            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData() };
            loader.save(filename, SWT.IMAGE_PNG);
        }

    }

    private void createResourceDiagram() {
        Resource resource = getResource();
        if (resource != null) {
            createResourceNode(resource);
        }
    }

    private boolean showLiteralNodes = false;
    private Color highlightBorderColor = new Color(Display.getCurrent(), 25, 105, 187);
    private Color highlightColor = new Color(Display.getCurrent(), 151, 184, 225);

    private GraphNode getResourceGraphNode(Resource resource) {
        GraphNode node = null;
        if (nodes.containsKey(resource)) {
            node = nodes.get(resource);
        }
        else {
            node = new GraphNode(graph, SWT.NONE, labelProvider.getText(resource),
                    labelProvider.getImage(resource));
            node.setData(resource);
            nodes.put(resource, node);
        }
        node.setBorderHighlightColor(highlightBorderColor);
        node.setHighlightColor(highlightColor);
        node.setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        node.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        node.setTooltip(createTooltip(resource));
        return node;
    }

    private IFigure createTooltip(Resource resource) {

        return null;
    }

    private GraphNode getLiteralGraphNode(Literal literal) {
        GraphNode node = new GraphNode(graph, SWT.WRAP, labelProvider.getText(literal),
                labelProvider.getImage(literal));
        node.setData(null);
        return node;
    }

    private GraphNode createResourceNode(Resource resource) {
        Model model = getModelProvider().getOntModel();
        GraphNode rootNode = getResourceGraphNode(resource);
        for (Statement statement : model.listStatements(
                new SimpleSelector(resource, null, (RDFNode) null)).toList()) {
            RDFNode object = statement.getObject();
            GraphNode node = null;
            if (object.isResource()) {
                node = createRelatedResourceNodes(object);
            }
            else if (showLiteralNodes) {
                node = getLiteralGraphNode(object.asLiteral());
            }

            // / TODO: connections with current node is disabled!
            if (node != null && !node.equals(rootNode)) {
                createConnection(statement.getPredicate(), rootNode, node);
            }
        }
        for (Statement statement : model.listStatements(new SimpleSelector(null, null, resource))
                .toList()) {
            RDFNode subject = statement.getSubject();
            GraphNode node = createRelatedResourceNodes(subject);

            // / TODO: connections with current node is disabled!
            if (node != null && !node.equals(rootNode)) {
                createConnection(statement.getPredicate(), node, rootNode);
            }
        }
        return rootNode;
    }

    private GraphNode createRelatedResourceNodes(RDFNode object) {
        Model model = getModelProvider().getOntModel();
        Resource related = object.asResource();
        GraphNode node = getResourceGraphNode(related);

        for (Statement statement : model.listStatements(
                new SimpleSelector(related, null, (RDFNode) null)).toList()) {
            if (statement.getObject().isResource()) {
                Resource other = statement.getObject().asResource();
                if (!related.equals(other) && nodes.containsKey(other)) {
                    createConnection(statement.getPredicate(), node, nodes.get(other));
                }
            }
        }
        for (Statement statement : model.listStatements(new SimpleSelector(null, null, related))
                .toList()) {
            Resource other = statement.getSubject();
            if (!related.equals(other) && nodes.containsKey(other)) {
                createConnection(statement.getPredicate(), nodes.get(other), node);
            }
        }
        return node;
    }

    private void createConnection(Property predicate, GraphNode from, GraphNode to) {
        GraphConnection connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
                from, to);
        connection.setData(predicate);
        connection.setText(labelProvider.getText(predicate));
    }

    public void setLayoutManager() {
        // graph.setLayoutAlgorithm(new
        // TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
        // graph.setLayoutAlgorithm(new
        // RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
        graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
                true);

    }

}

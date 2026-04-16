package com.semmtech.plugin.semmweb.editor;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.dnd.OntClassTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;


public class Snippet026TreeViewerDynamicDndSource {

    final Model model;
    Resource classResource;
    Resource instanceResource;

    public Snippet026TreeViewerDynamicDndSource(Shell shell) {
        final TreeViewer viewer = new TreeViewer(shell);

        // Apply a DragSource on the viewer. That DragSource will be used to
        // dynamically adjust the transfer types offered. For now, we only state
        // how it should offer all the possible different transfer types.
        final DragSource dndSource = new DragSource(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        dndSource.addDragListener(new DragSourceAdapter() {

            @Override
            public void dragSetData(DragSourceEvent event) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                ResourceTreeData draggedResourceData = (ResourceTreeData) selection
                        .getFirstElement();
                Resource resource = (Resource) draggedResourceData.getData();

                if (OntClassTransfer.getInstance().isSupportedType(event.dataType)
                        && (resource.hasProperty(RDF.type, RDFS.Class) || resource.hasProperty(
                                RDF.type, OWL.Class))) {
                    event.data = resource.as(OntClass.class);
                }
                else if (ResourceTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = resource;
                }
                else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = resource.getURI();
                }
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                // Here we dynamically set the transfer types that will be
                // offered for the viewer node that is about to be dragged.
                // Dragging starts by a click of the mouse, selecting the node
                // to be dragged, which we can use to our advantage in
                // determining the transfer types that should be supported for
                // that specific node.
                boolean isClass = false;
                boolean isIndividual = false;

                ISelection selection = event.getSelection();
                if (selection instanceof IStructuredSelection) {
                    IStructuredSelection structuredSelection = (IStructuredSelection) selection;

                    if (structuredSelection.size() == 1) {
                        if (structuredSelection.getFirstElement() instanceof Resource) {
                            Resource resource = (Resource) structuredSelection.getFirstElement();
                            if (resource.hasProperty(RDF.type, RDFS.Class)
                                    || resource.hasProperty(RDF.type, OWL.Class)) {
                                isClass = true;
                            }
                            else {
                                isIndividual = true;
                            }
                        }
                    }
                }

                if (isClass) {
                    dndSource.setTransfer(new Transfer[] { OntClassTransfer.getInstance(),
                            ResourceTransfer.getInstance(), TextTransfer.getInstance() });
                    System.out.println("Set TreeViewer's transfer type to include class transfer");
                }
                else if (isIndividual) {
                    dndSource.setTransfer(new Transfer[] { ResourceTransfer.getInstance(),
                            TextTransfer.getInstance() });
                    System.out.println("Set TreeViewer's transfer type to exclude class transfer");
                }
                else {
                    dndSource.setTransfer(new Transfer[] {});
                }
            }
        });

        model = createExampleModel();
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                String result = "";
                if (element instanceof Resource) {
                    Resource resource = (Resource) element;
                    result = (resource.hasProperty(RDF.type) && resource.getPropertyResourceValue(
                            RDF.type).equals(RDFS.Class)) ? "Class: " : "Instance: ";
                    if (resource.hasProperty(RDFS.label)) {
                        result += resource.getProperty(RDFS.label).getObject().toString();
                    }
                }
                return result;
            }
        });
        class ViewContentProvider implements ITreeContentProvider {
            @Override
            public void dispose() {
            }

            @Override
            public Object[] getElements(Object inputElement) {
                // first-level elements
                return new Resource[] { classResource };
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                Resource resource = (Resource) parentElement;
                if (resource.equals(RDFS.Class)) {
                    return new Resource[] { classResource };
                }
                if (resource.equals(classResource)) {
                    return new Resource[] { instanceResource };
                }
                return new Resource[0];
            }

            @Override
            public Object getParent(Object element) {
                Resource resource = (Resource) element;
                if (resource.equals(classResource)) {
                    return RDFS.Class;
                }
                if (resource.equals(instanceResource)) {
                    return classResource;
                }
                return null;
            }

            @Override
            public boolean hasChildren(Object element) {
                Resource resource = (Resource) element;
                return resource.equals(RDFS.Class) || resource.equals(classResource);
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        }
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setInput(RDFS.Class);
    }

    private Model createExampleModel() {
        Model model = ModelFactory.createDefaultModel();
        classResource = model.createResource();
        classResource.addProperty(RDFS.label, "class resource");
        classResource.addProperty(RDF.type, RDFS.Class);

        instanceResource = model.createResource();
        instanceResource.addProperty(RDFS.label, "instance resource");
        instanceResource.addProperty(RDF.type, classResource);

        return model;
    }

    /**
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        new Snippet026TreeViewerDynamicDndSource(shell);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        display.dispose();
    }

}

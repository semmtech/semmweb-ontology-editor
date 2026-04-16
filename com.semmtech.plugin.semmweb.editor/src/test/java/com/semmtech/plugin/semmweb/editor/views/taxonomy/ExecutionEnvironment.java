package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.TimeTester;


public class ExecutionEnvironment {

    private OntModel model;
    private Resource taxonomyResource;
    private boolean includeChildlessRoots;
    private boolean countInstances;
    private Dataset dataset;
    private boolean tdb;

    private Query query;
    private TimeTester time;

    private TaxonomyViewModel viewModel;

    private long totQueryExecutionTime;
    private long totTestResultTime;

    public ExecutionEnvironment(OntModel model, Resource taxonomyResource,
            boolean includeChildlessRoots, boolean countInstances, Dataset dataset, boolean tdb) {
        this.dataset = dataset;
        this.tdb = tdb;
        this.taxonomyResource = taxonomyResource;
        this.countInstances = countInstances;
        this.includeChildlessRoots = includeChildlessRoots;
        this.model = model;

        time = new TimeTester();
        // query = TaxonomyViewModel.buildViewModelQuery(taxonomyResource,
        // includeChildlessRoots,
        // countInstances);

        // Op op =

        totQueryExecutionTime = 0;
        totTestResultTime = 0;
    }

    public void runQuery() {
        time.start();
        // if (tdb) {
        // dataset.begin(ReadWrite.READ);
        // }

        // long start = System.currentTimeMillis();
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        // AlgebraGenerator gen = new AlgebraGenerator(exec.getContext());
        // if (!optimized) {
        // Op op = Algebra.compile(query);
        // op = Algebra.optimize(op, exec.getContext());
        // query = OpAsQuery.asQuery(op);
        // optimized = true;
        // exec = QueryExecutionFactory.create(query, dataset);
        // System.out.println("Query opt: " + (System.currentTimeMillis() -
        // start));
        // }

        try {
            viewModel = new TaxonomyViewModel(exec.execConstruct(), model);
            totQueryExecutionTime += time.step();
        }
        catch (Exception ex) {
            System.err.println(toString());
            System.err.println("  " + ex.getMessage());
            ex.printStackTrace();
        }
        exec.close();

        // if (tdb) {
        // dataset.end();
        // }

    }

    public long getTotQueryExecutionTime() {
        return totQueryExecutionTime;
    }

    public void testResult(boolean childrenRequired, boolean print) {
        time.start();
        if (viewModel != null) {
            testResult(viewModel, childrenRequired, print);
            totTestResultTime += time.step();
        }
    }

    public void testResult(TaxonomyViewModel model, boolean childrenRequired, boolean print) {
        int rootClassCount = model.getRootClassCount(childrenRequired);

        for (int i = 1; i <= rootClassCount; i++) {
            OntClass clazz = model.getRootClass(childrenRequired, i);
            if (print && clazz != null) {
                System.out.println(model.getText(clazz));
            }
            traverseTree(model, clazz, 1, print);
        }
    }

    public void traverseTree(TaxonomyViewModel model, OntClass clazz, int indent, boolean print) {
        int childCount = model.getChildClassCount(clazz);

        for (int i = 1; i <= childCount; i++) {
            OntClass childClazz = model.getChildClass(clazz, i);
            if (print && childClazz != null) {
                System.out.println(indent(indent) + model.getText(childClazz));
            }
            traverseTree(model, childClazz, indent + 1, print);
        }
    }

    private static String indent(int num) {
        String ind = "";
        for (int i = 0; i < num; i++) {
            ind += "    ";
        }
        return ind;
    }

    @Override
    public String toString() {
        return String.format("res:%s includeChildless:%b countInstance:%b tdb:%b",
                taxonomyResource, includeChildlessRoots, countInstances, tdb);
    }

    public String toString(int times) {
        return String.format(
                "res:%s includeChildless:%b countInstance:%b tdb:%b queryTime:%d exeTime:%d",
                taxonomyResource, includeChildlessRoots, countInstances, tdb, totQueryExecutionTime
                        / times, totTestResultTime / times);
    }
}

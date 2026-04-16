package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.TimeTester;
import com.semmtech.jena.performance.sparql.DatasetProvider;
import com.semmtech.jena.performance.sparql.QueryBenchmark;
import com.semmtech.jena.performance.sparql.QueryExecutionTester;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.sparql.NamespaceLabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.sparql.OntologyLabelProviderPropertyFunction;


// -client -Djava.compiler=NONE
public class TaxonomyViewModelTest {

    private static final int NUMBER_OF_TRY = 5;
    private static final String FILE = "src\\test\\resources\\models\\model.ttl";
    private static TimeTester time = new TimeTester();
    private static List<DatasetProvider> providers;
    private static List<Model> imports;
    private Location location = new Location("src\\test\\resources\\TDB");

    private static final String MODELS_FOLDER = "src\\test\\resources\\models\\";
    private static final String IMPORTS_FOLDER = "src\\test\\resources\\imports\\";

    @BeforeClass
    public static void init() throws FileNotFoundException, IOException {
        registerPropertyFunctions();

        File modelsFolder = new File(MODELS_FOLDER);
        File importsFolder = new File(IMPORTS_FOLDER);
        providers = Lists.newArrayList();
        imports = Lists.newArrayList();

        for (File f : importsFolder.listFiles()) {
            try (InputStream is = new FileInputStream(f)) {
                Model m = ModelFactory.createDefaultModel();
                m.read(is, null, FileUtils.guessLang(f.getName()));
                imports.add(m);
            }
        }

        for (File f : modelsFolder.listFiles()) {
            providers.add(new LazyAndForgetfulDatasetProvider(f, imports));
        }
    }

    @Test
    public void testCountInstances() throws IOException, InterruptedException {
        TimeTester time = new TimeTester();

        File modelFile = new File(MODELS_FOLDER + File.separator + "book-store.ttl");
        Query query1 = TaxonomyViewModel.buildViewModelQuery(RDFS.Class, true);

        LazyAndForgetfulDatasetProvider provider = new LazyAndForgetfulDatasetProvider(modelFile,
                imports);

        Dataset dataset = provider.getDataset();
        QueryExecution exe = QueryExecutionFactory.create(query1, dataset);

        time.start();
        Model viewModel = exe.execConstruct();
        time.step("Create");

        Query countQuery = TaxonomyViewModel.buildCountInstancesQuery();

        dataset.addNamedModel("urn:taxonomyViewModel", viewModel);

        QueryExecution exec = QueryExecutionFactory.create(countQuery, dataset);

        time.start();
        Model m = exec.execConstruct();
        time.step("Count");

        Thread.sleep(2000);

        System.out.println(countQuery);

        m.write(System.out, FileUtils.langTurtle);
    }

    @Test
    public void testTaxonomyViewModelQuery1() throws FileNotFoundException, IOException {
        Query query1 = TaxonomyViewModel.buildViewModelQuery(RDFS.Class, true);
        QueryBenchmark bench1 = new QueryBenchmark(String.format(
                "res:%s includeChildless:%b countInstance:%b", RDFS.Class, true, true), query1);

        runBenchmark(bench1);
    }

    @Test
    public void testTaxonomyViewModelQuery2() {
        Query query2 = TaxonomyViewModel.buildViewModelQuery(OWL.Class, false);
        QueryBenchmark bench2 = new QueryBenchmark(String.format(
                "res:%s includeChildless:%b countInstance:%b", OWL.Class, false, true), query2);

        runBenchmark(bench2);
    }

    @Test
    public void testTaxonomyViewModelQuery3() {
        Query query3 = TaxonomyViewModel.buildViewModelQuery(OWL.Class, true);
        QueryBenchmark bench3 = new QueryBenchmark(String.format(
                "res:%s includeChildless:%b countInstance:%b", OWL.Class, true, false), query3);

        runBenchmark(bench3);
    }

    @Test
    public void testTaxonomyViewModelQuery4() {
        Query query4 = TaxonomyViewModel.buildViewModelQuery(OWL.Class, false);
        QueryBenchmark bench4 = new QueryBenchmark(String.format(
                "res:%s includeChildless:%b countInstance:%b", OWL.Class, false, false), query4);

        runBenchmark(bench4);
    }

    public void runBenchmark(QueryBenchmark bench) {
        System.out.println(bench.getName() + "\n");
        bench.setInput(providers);
        bench.excec();

        System.out.println(bench.formatResult() + "\n\n");
    }

    @Ignore
    @Test()
    public void testOrdered() throws Exception {
        int[] order = new int[] { 1, 2, 3, 4 };

        for (int i : order) {
            switch (i) {
            case 1:
                testTaxonomyViewModelQuery1();
                break;
            case 2:
                testTaxonomyViewModelQuery2();
                break;
            case 3:
                testTaxonomyViewModelQuery3();
                break;
            case 4:
                testTaxonomyViewModelQuery4();
                break;
            }
        }
    }

    private static void registerPropertyFunctions() {
        PropertyFunctionRegistry.get().put(LabelProviderPropertyFunction.getURI(),
                LabelProviderPropertyFunction.class);
        PropertyFunctionRegistry.get().put(NamespaceLabelProviderPropertyFunction.getURI(),
                NamespaceLabelProviderPropertyFunction.class);
        PropertyFunctionRegistry.get().put(OntologyLabelProviderPropertyFunction.getURI(),
                OntologyLabelProviderPropertyFunction.class);
    }

    @Deprecated
    public void testCreate() {
        time.start();

        OntDocumentManager ontDocumentManager = new OntDocumentManager();
        ontDocumentManager.setProcessImports(false);
        ModelMaker modelMaker = ModelFactory.createMemModelMaker();
        OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                OWL.FULL_LANG.getURI());

        OntModel model = ModelFactory.createOntologyModel(spec);
        String lang = FileUtils.guessLang(FILE);

        try (InputStream is = new FileInputStream(FILE)) {
            model.read(is, null, lang);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Cannot read model");
        }

        time.step("Load model");

        OntModel unionModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        unionModel.addSubModel(model.getBaseModel(), false);
        Dataset dataset = DatasetFactory.createMem();
        dataset.setDefaultModel(model);
        dataset.addNamedModel("urn:filterModel", unionModel);

        initTDB();

        Dataset tdbDataset = TDBFactory.createDataset(location);
        tdbDataset.begin(ReadWrite.READ);

        Model tdbModel = tdbDataset.getDefaultModel();

        // assume we want the default model, or we could get a named model
        // here
        OntModel tdbOntModel = ModelFactory.createOntologyModel(spec, tdbModel);

        // read the input file - only needs to be done once
        // ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
        // dataset.getDefaultModel());
        // tdbUnionModel.addSubModel(tdbModel.getBaseModel(), false);
        // tdbDataset.addNamedModel("madol", tdbModel);

        ExecutionEnvironment exe1 = new ExecutionEnvironment(model, OWL.Class, true, true, dataset,
                false);
        ExecutionEnvironment exe2 = new ExecutionEnvironment(tdbOntModel, OWL.Class, true, true,
                tdbDataset, true);
        ExecutionEnvironment exe3 = new ExecutionEnvironment(model, OWL.Class, false, true,
                dataset, false);
        ExecutionEnvironment exe4 = new ExecutionEnvironment(tdbOntModel, OWL.Class, false, true,
                tdbDataset, true);
        ExecutionEnvironment exe5 = new ExecutionEnvironment(model, OWL.Class, true, false,
                dataset, false);
        ExecutionEnvironment exe6 = new ExecutionEnvironment(tdbOntModel, OWL.Class, true, false,
                tdbDataset, true);
        ExecutionEnvironment exe7 = new ExecutionEnvironment(model, OWL.Class, false, false,
                dataset, false);
        ExecutionEnvironment exe8 = new ExecutionEnvironment(tdbOntModel, OWL.Class, false, false,
                tdbDataset, true);

        for (int i = 0; i < NUMBER_OF_TRY; i++) {
            exe1.runQuery();
            exe1.testResult(false, false);
            exe2.runQuery();
            exe2.testResult(false, false);
            exe3.runQuery();
            exe3.testResult(false, false);
            exe4.runQuery();
            exe4.testResult(false, false);
            exe5.runQuery();
            exe5.testResult(false, false);
            exe6.runQuery();
            exe6.testResult(false, false);
            exe7.runQuery();
            exe7.testResult(false, false);
            exe8.runQuery();
            exe8.testResult(false, false);
        }

        // exe2.testResult(false, true);

        System.out.println(exe1.toString(NUMBER_OF_TRY));
        System.out.println(exe2.toString(NUMBER_OF_TRY));
        System.out.println(exe3.toString(NUMBER_OF_TRY));
        System.out.println(exe4.toString(NUMBER_OF_TRY));
        System.out.println(exe5.toString(NUMBER_OF_TRY));
        System.out.println(exe6.toString(NUMBER_OF_TRY));
        System.out.println(exe7.toString(NUMBER_OF_TRY));
        System.out.println(exe8.toString(NUMBER_OF_TRY));

        tdbDataset.commit();

    }

    @Deprecated
    public void initTDB() {
        time.start();
        TDB.getContext().set(ARQ.symLogExec, true);
        // TDB.getContext().set(TDB.symUnionDefaultGraph, true);

        Dataset tdbDataset = TDBFactory.createDataset(location);

        String sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }";

        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, tdbDataset);
        ResultSet results = qexec.execSelect();

        long count = 0;
        while (results.hasNext()) {
            count = results.next().get("count").asLiteral().getLong();
        }

        System.out.println("TDB contains " + count + " triples");

        if (count == 0) {

            tdbDataset.begin(ReadWrite.WRITE);

            Model tdbModel = tdbDataset.getDefaultModel();

            try (InputStream is = new FileInputStream(FILE)) {
                tdbModel.read(is, null, FileUtils.guessLang(FILE));
            }
            catch (Exception e) {
                e.printStackTrace();
                fail("Cannot read model");
            }

            tdbDataset.addNamedModel("urn:filterModel", tdbModel);
            tdbDataset.commit();
        }
        time.step("TDB model");
    }

    public static class LazyAndForgetfulDatasetProvider implements DatasetProvider {

        private final TimeTester time = new TimeTester();
        private long lastLoadTime = -1;
        private File f;
        private List<Model> imports;
        private boolean forgetful;
        private Dataset dataset;
        private OntModel model;

        public LazyAndForgetfulDatasetProvider(File f, List<Model> imports) {
            this.f = f;
            this.imports = imports;
            forgetful = true;
        }

        public void setForgetful(boolean forgetful) {
            this.forgetful = forgetful;
        }

        @Override
        public String getName() {
            return f.getName();
        }

        public OntModel getModel() {
            return model;
        }

        @Override
        public long getLastLoadTime() {
            return lastLoadTime;
        }

        @Override
        public Dataset getDataset() {
            if (!forgetful && dataset != null) {
                lastLoadTime = -1;
                return dataset;
            }

            time.start();
            OntDocumentManager ontDocumentManager = new OntDocumentManager();
            ontDocumentManager.setProcessImports(false);
            ModelMaker modelMaker = ModelFactory.createMemModelMaker();
            final OntModelSpec spec = new OntModelSpec(modelMaker, ontDocumentManager, null,
                    OWL.FULL_LANG.getURI());
            OntModel model = ModelFactory.createOntologyModel(spec);

            try (InputStream is = new FileInputStream(f)) {
                model.read(is, null, FileUtils.guessLang(f.getName()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            this.model = model;

            OntModel unionModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            unionModel.addSubModel(model.getBaseModel(), false);

            for (Model imp : imports) {
                model.addSubModel(imp);
                unionModel.addSubModel(imp, false);
            }

            Dataset dataset = DatasetFactory.createMem();
            dataset.setDefaultModel(model);
            dataset.addNamedModel("urn:filterModel", unionModel);
            lastLoadTime = time.step();
            return dataset;
        }

        @Override
        public QueryExecutionTester getTester() {
            return new TaxonomyViewModeltest(model);
        }
    }

    public static class TaxonomyViewModeltest implements QueryExecutionTester {

        private OntModel model;
        private int classes;
        private int instances;

        public TaxonomyViewModeltest(OntModel model) {
            this.model = model;
            classes = 0;
            instances = -1;
        }

        @Override
        public void testResult(Object res) {
            Model viewModel = (Model) res;
            testResult(new TaxonomyViewModel(viewModel, model), false, true);
        }

        @Override
        public String printResult() {
            return classes + " - " + instances;
        }

        @Override
        public boolean equals(QueryExecutionTester obj) {
            return super.equals(obj);
        }

        public void testResult(TaxonomyViewModel model, boolean childrenRequired, boolean print) {
            int rootClassCount = model.getRootClassCount(childrenRequired);

            for (int i = 1; i <= rootClassCount; i++) {
                OntClass clazz = model.getRootClass(childrenRequired, i);
                classes++;

                int directInstances = -1;
                int indirectInstances = -1;

                if (clazz != null) {
                    directInstances = model.getDirectInstanceCount(clazz);
                    indirectInstances = model.getIndirectInstanceCount(clazz);

                    instances += directInstances;
                }

                if (print && clazz != null) {
                    String count = "";

                    if (directInstances > 0 || indirectInstances > 0) {
                        count = String.format("(%d + %d)", directInstances, indirectInstances);
                    }

                    System.out.println(model.getText(clazz) + " " + count);
                }
                traverseTree(model, clazz, 1, print);
            }
        }

        public void traverseTree(TaxonomyViewModel model, OntClass clazz, int indent, boolean print) {
            int childCount = model.getChildClassCount(clazz);

            for (int i = 1; i <= childCount; i++) {
                OntClass childClazz = model.getChildClass(clazz, i);
                classes++;

                int directInstances = -1;
                int indirectInstances = -1;

                if (clazz != null) {
                    directInstances = model.getDirectInstanceCount(clazz);
                    indirectInstances = model.getIndirectInstanceCount(clazz);

                    instances += directInstances;
                }

                if (print && childClazz != null) {
                    String count = "";

                    if (directInstances > 0 || indirectInstances > 0) {
                        count = String.format("(%d + %d)", directInstances, indirectInstances);
                    }

                    System.out.println(indent(indent) + model.getText(childClazz) + " " + count);
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

    }
}

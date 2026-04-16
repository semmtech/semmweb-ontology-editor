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

package com.semmtech.semantics.model;


import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelFactoryBase;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.InfGraph;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.semmtech.semantics.ontology.ExtendedOntModel;
import com.semmtech.semantics.semm.SEMMModel;
import com.semmtech.semantics.semm.impl.SEMMModelImpl;
import com.semmtech.semantics.skos.DictionaryModel;
import com.semmtech.semantics.skos.impl.DictionaryModelImpl;


public class ExtendedModelFactory extends ModelFactoryBase {
    private ExtendedModelFactory() {
    }

    public static DictionaryModel createDictionaryModel(OntModelSpec spec, Model base) {
        return new DictionaryModelImpl(spec, base);
    }

    public static DictionaryModel createDictionaryModel(OntModelSpec spec) {
        return new DictionaryModelImpl(spec);
    }

    public static SEMMModel createSEMMModel(OntModelSpec spec, Model base) {
        return new SEMMModelImpl(spec, base);
    }

    /**
     * Answer a new SEMM ontology model constructed according to the
     * specification, which includes a ModelMaker which will create the
     * necessary base model.
     * 
     * @param spec
     * @return
     */
    public static SEMMModel createSEMMModel(OntModelSpec spec) {
        return new SEMMModelImpl(spec);
    }

    /**
     * <p>
     * Answer a new ontology model, constructed according to the given ontology
     * model specification, and starting with the ontology data in the given
     * model.
     * </p>
     * 
     * @param spec
     *            An ontology model specification object, that will be used to
     *            construct the ontology model with different options of
     *            ontology language, reasoner, document manager and storage
     *            model
     * @param base
     *            An existing model to treat as an ontology model, or null.
     * @return A new ontology model
     * @see OntModelSpec
     */
    public static OntModel createOntologyModel(OntModelSpec spec, Model base) {
        return new ExtendedOntModel(spec, base);
    }

    /**
     * Answer a new ontology model constructed according to the specification,
     * which includes a ModelMaker which will create the necessary base model.
     */
    public static OntModel createOntologyModel(OntModelSpec spec) {
        return new ExtendedOntModel(spec);
    }

    // Functions copied as is from ModelFactory
    // /////////////////////////////////////////

    /**
     * Each Model created by ModelFactory has a default set of prefix mappings.
     * These mappings are copied from a (static) default PrefixMapping which is
     * set by setDefaultModelPrefixes. It is the reference to a PrefixMapping
     * that is retained, not a copy of it, so a user may set the defaults with
     * this method and continue to modify it; the modifications will appear in
     * the next model to be created.
     * <p>
     * When a Model is created from an existing Graph, the prefixes of that
     * Graph are not disturbed; only ones not present in the Graph are added.
     * 
     * @param pm
     *            the default prefixes to use
     * @return the previous default prefix mapping
     */
    public static PrefixMapping setDefaultModelPrefixes(PrefixMapping pm) {
        return ModelFactory.setDefaultModelPrefixes(pm);
    }

    /**
     * Answer the current default model prefixes PrefixMapping object.
     */
    public static PrefixMapping getDefaultModelPrefixes() {
        return ModelFactory.getDefaultModelPrefixes();
    }

    /**
     * Answer a Model constructed from the single resource in
     * <code>singleRoot</code> of type <code>ja:Model</code>. See the Assembler
     * howto (doc/assembler/assembler-howto.html) for documentation of Assembler
     * descriptions. See also <code>findAssemblerRoots</code> to find the set of
     * possible roots in a description, and
     * <code>assemblerModelFrom(Resource)</code> for assembling a model from its
     * single description.
     */
    public static Model assembleModelFrom(Model singleRoot) {
        return ModelFactory.assembleModelFrom(singleRoot);
    }

    /**
     * Answer a Set of resources present in <code>m</code> that are explicitly
     * or implicitly of type ja:Object, ie, suitable as roots for
     * <code>assemblerModelFrom</code>. Note that the resource objects returned
     * need <i>not</i> have <code>m</code> as their <code>getModel()</code> -
     * they may be members of an extended constructed model.
     */
    public static Set<Resource> findAssemblerRoots(Model m) {
        return ModelFactory.findAssemblerRoots(m);
    }

    /**
     * Answer a Model as described the the Assembler specification rooted at the
     * Resource <code>root</code> in its Model. <code>Resource</code> must be of
     * rdf:type <code>ja:Object</code>, where <code>ja</code> is the prefix of
     * Jena Assembler objects.
     */
    public static Model assembleModelFrom(Resource root) {
        return ModelFactory.assembleModelFrom(root);
    }

    /**
     * Answer a fresh Model with the default specification.
     */
    public static Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }

    /**
     * Answer a model that encapsulates the given graph. Existing prefixes are
     * undisturbed.
     * 
     * @param g
     *            A graph structure
     * @return A model presenting an API view of graph g
     */
    public static Model createModelForGraph(Graph g) {
        return ModelFactory.createModelForGraph(g);
    }

    /**
     * Answer a ModelMaker that constructs memory-based Models that are backed
     * by files in the root directory. The Model is loaded from the file when it
     * is opened, and when the Model is closed it is written back.
     * 
     * @param root
     *            the name of the directory in which the backing files are held
     * @return a ModelMaker linked to the files in the root
     */
    public static ModelMaker createFileModelMaker(String root) {
        return ModelFactory.createFileModelMaker(root);
    }

    /**
     * Answer a ModelMaker that constructs memory-based Models that do not
     * persist past JVM termination.
     * 
     * @return a ModelMaker that constructs memory-based models
     */
    public static ModelMaker createMemModelMaker() {
        return ModelFactory.createMemModelMaker();
    }

    /**
     * Return a Model through which all the RDFS entailments derivable from the
     * given model are accessible. Some work is done when the inferenced model
     * is created but each query will also trigger some additional inference
     * work.
     * 
     * @param model
     *            the Model containing both instance data and schema assertions
     *            to be inferenced over
     */
    public static InfModel createRDFSModel(Model model) {
        return ModelFactory.createRDFSModel(model);
    }

    /**
     * Return a Model through which all the RDFS entailments derivable from the
     * given data and schema models are accessible. There is no strict
     * requirement to separate schema and instance data between the two
     * arguments.
     * 
     * @param model
     *            a Model containing instance data assertions
     * @param schema
     *            a Model containing RDFS schema data
     */
    public static InfModel createRDFSModel(Model schema, Model model) {
        return ModelFactory.createRDFSModel(schema, model);
    }

    /**
     * Build an inferred model by attaching the given RDF model to the given
     * reasoner.
     * 
     * @param reasoner
     *            the reasoner to use to process the data
     * @param model
     *            the Model containing both instance data and schema assertions
     *            to be inferenced over, any statements added to the InfModel
     *            will be added to this underlying data model.
     */
    public static InfModel createInfModel(Reasoner reasoner, Model model) {
        return ModelFactory.createInfModel(reasoner, model);
    }

    /**
     * Build an inferred model by attaching the given RDF model to the given
     * reasoner. This form of the call allows two data sets to be merged and
     * reasoned over - conventionally one contains schema data and one instance
     * data but this is not a formal requirement.
     * 
     * @param reasoner
     *            the reasoner to use to process the data
     * @param schema
     *            a Model containing RDFS schema data
     * @param model
     *            a Model containing instance data assertions, any statements
     *            added to the InfModel will be added to this underlying data
     *            model.
     */
    public static InfModel createInfModel(Reasoner reasoner, Model schema, Model model) {
        return ModelFactory.createInfModel(reasoner, schema, model);
    }

    /**
     * Build an inference model from an InfGraph. Graphs and InfGraphs are
     * internal implementation level objects rather than normal user objects so
     * this method should only be used if you are sure this is what you need.
     * 
     * @param g
     *            and inference graph
     * @return the same graph wrapped up as an InfModel
     */
    public static InfModel createInfModel(InfGraph g) {
        return ModelFactory.createInfModel(g);
    }

    /**
     * <p>
     * Answer a new ontology model which will process in-memory models of
     * ontologies expressed the default ontology language (OWL). The default
     * document manager will be used to load the ontology's included documents.
     * </p>
     * <p>
     * <strong>Note:</strong>The default model chosen for OWL and RDFS includes
     * a weak reasoner that includes some entailments (such as transitive
     * closure on the sub-class and sub-property hierarchies). Users who want
     * either no inference at all, or alternatively more complete reasoning,
     * should use one of the other <code>createOntologyModel</code> methods that
     * allow the preferred OntModel specification to be stated.
     * </p>
     * 
     * @return A new ontology model
     * @see OntModelSpec#getDefaultSpec
     * @see #createOntologyModel(OntModelSpec, Model)
     */
    public static OntModel createOntologyModel() {
        return createOntologyModel(ProfileRegistry.OWL_LANG);
    }

    /**
     * <p>
     * Answer a new ontology model which will process in-memory models of
     * ontologies in the given language. The default document manager will be
     * used to load the ontology's included documents.
     * </p>
     * 
     * @param languageURI
     *            The URI specifying the ontology language we want to process
     * @return A new ontology model
     * @see OntModelSpec#getDefaultSpec
     */
    public static OntModel createOntologyModel(String languageURI) {
        return createOntologyModel(OntModelSpec.getDefaultSpec(languageURI), null);
    }

    /**
     * <p>
     * Answer a new ontology model which will process in-memory models of
     * ontologies expressed the default ontology language (OWL). The default
     * document manager will be used to load the ontology's included documents.
     * </p>
     * 
     * @param spec
     *            An ontology model specification that defines the language and
     *            reasoner to use
     * @param maker
     *            A model maker that is used to get the initial store for the
     *            ontology (unless the base model is given), and create
     *            addtional stores for the models in the imports closure
     * @param base
     *            The base model, which contains the contents of the ontology to
     *            be processed
     * @return A new ontology model
     * @see OntModelSpec
     */
    public static OntModel createOntologyModel(OntModelSpec spec, ModelMaker maker, Model base) {
        OntModelSpec _spec = new OntModelSpec(spec);
        _spec.setImportModelMaker(maker);

        return createOntologyModel(_spec, base);
    }

    /**
     * Answer a new model that is the dynamic union of two other models. By
     * <i>dynamic union</i>, we mean that changes to either <code>m1</code> or
     * <code>m2</code> will be reflected in the result model, and <i>vice
     * versa</i>: specifically, additions to and removals from the union will be
     * implemented as operations on <code>m1</code> <strong>only</strong>. See
     * also the behaviour of OntModel and the MultiUnion class.
     * <p>
     * <code>createUnion</code> only creates two-element unions.
     */
    public static Model createUnion(Model m1, Model m2) {
        return ModelFactory.createUnion(m1, m2);
    }
}

package com.semmtech.jena.reasoner.rulesys;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.reasoner.InfGraph;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerException;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.FBRuleInfGraph;
import com.hp.hpl.jena.reasoner.rulesys.FBRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.impl.OWLRuleTranslationHook;
import com.hp.hpl.jena.shared.impl.JenaParameters;


/**
 * A hybrid forward/backward implementation of the OWL closure rules.
 * 
 * <p>
 * Note: This class has two constructors, one of which can load a custom set of
 * rules rather than the default one.
 * </p>
 */
public class OWLFBRuleReasoner extends FBRuleReasoner {

    /** The location of the OWL rule definitions on the class path */
    protected static final String RULE_FILE = "src/main/rules/owl-fb.rules";

    /** The precomputed axiom closure and compiled rule set */
    protected static FBRuleInfGraph staticPreload;

    protected static Logger logger = LoggerFactory.getLogger(OWLFBRuleReasoner.class);

    /**
     * Constructor
     */
    public OWLFBRuleReasoner(ReasonerFactory factory) {
        this(factory, null);
    }

    /**
     * Constructor
     */
    public OWLFBRuleReasoner(ReasonerFactory factory, List<Rule> rules) {
        super((rules != null) ? rules : loadRules(RULE_FILE), factory);
    }

    /**
     * Internal constructor, used to generated a partial binding of a schema to
     * a rule reasoner instance.
     */
    private OWLFBRuleReasoner(OWLFBRuleReasoner parent, InfGraph schemaGraph) {
        super(parent.rules, schemaGraph, parent.factory);
    }

    /**
     * Precompute the implications of a schema graph. The statements in the
     * graph will be combined with the data when the final InfGraph is created.
     */
    @Override
    public Reasoner bindSchema(Graph tbox) throws ReasonerException {
        checkArgGraph(tbox);
        if (schemaGraph != null) {
            throw new ReasonerException("Can only bind one schema at a time to an OWLRuleReasoner");
        }
        FBRuleInfGraph graph = new FBRuleInfGraph(this, rules, getPreload(), tbox);
        graph.addPreprocessingHook(new OWLRuleTranslationHook());
        graph.prepare();
        return new OWLFBRuleReasoner(this, graph);
    }

    /**
     * Attach the reasoner to a set of RDF data to process. The reasoner may
     * already have been bound to specific rules or ontology axioms (encoded in
     * RDF) through earlier bindRuleset calls.
     * 
     * @param data
     *            the RDF data to be processed, some reasoners may restrict the
     *            range of RDF which is legal here (e.g. syntactic restrictions
     *            in OWL).
     * @return an inference graph through which the data+reasoner can be
     *         queried.
     * @throws ReasonerException
     *             if the data is ill-formed according to the constraints
     *             imposed by this reasoner.
     */
    @Override
    public InfGraph bind(Graph data) throws ReasonerException {
        checkArgGraph(data);
        FBRuleInfGraph graph = null;
        InfGraph schemaArg = schemaGraph == null ? getPreload() : (FBRuleInfGraph) schemaGraph;
        List<Rule> baseRules = ((FBRuleInfGraph) schemaArg).getRules();
        graph = new FBRuleInfGraph(this, baseRules, schemaArg);
        graph.addPreprocessingHook(new OWLRuleTranslationHook());
        graph.setDerivationLogging(recordDerivations);
        graph.setTraceOn(isTraceOn());
        graph.rebind(data);
        graph.setDatatypeRangeValidation(true);

        return graph;
    }

    /**
     * Get the single static precomputed rule closure.
     */
    @Override
    public synchronized InfGraph getPreload() {
        // Method synchronized - synchronize with other FBRulereasoer sync
        // methods
        // synchronized block - sync on static
        synchronized (OWLFBRuleReasoner.class) {
            if (staticPreload == null) {
                boolean prior = JenaParameters.enableFilteringOfHiddenInfNodes;
                try {
                    JenaParameters.enableFilteringOfHiddenInfNodes = true;
                    staticPreload = new FBRuleInfGraph(this, rules, null);
                    staticPreload.prepare();
                }
                finally {
                    JenaParameters.enableFilteringOfHiddenInfNodes = prior;
                }
            }
            return staticPreload;
        }
    }

    /**
     * Check an argument graph to make sure it is not an OWL rule graph already
     * and if so log a warning message.
     */
    private void checkArgGraph(Graph g) {
        if (JenaParameters.enableOWLRuleOverOWLRuleWarnings) {
            if (g instanceof InfGraph) {
                if (((InfGraph) g).getReasoner() instanceof OWLFBRuleReasoner) {
                    logger.warn("Creating OWL rule reasoner working over another OWL rule reasoner");
                }
            }
        }
    }
}

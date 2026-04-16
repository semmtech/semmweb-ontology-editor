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

package com.semmtech.semantics.sparql;


import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apache.jena.atlas.io.IndentedLineBuffer;
import org.apache.jena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.QueryVisitor;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.DatasetDescription;
import com.hp.hpl.jena.sparql.core.Prologue;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.E_IsLiteral;
import com.hp.hpl.jena.sparql.expr.E_IsURI;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_NotExists;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVarDistinct;
import com.hp.hpl.jena.sparql.expr.aggregate.Aggregator;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.Template;


/**
 * 
 * @author Sander Stolk
 */
public class QueryBuilder {
    private Query query;
    private ElementGroup queryPatterns;
    private Var countVar;

    private boolean createNewElement;

    public QueryBuilder() {
        this(null);
    }

    public QueryBuilder(Query query) {
        this.query = (query == null) ? (new Query()) : (query);
        this.queryPatterns = new ElementGroup();
        createNewElement = true;
    }

    public static QueryBuilder createSelect(boolean distinct) {
        QueryBuilder result = new QueryBuilder();
        result.setQuerySelectType();
        result.setDistinct(distinct);
        return result;
    }

    public static QueryBuilder createAsk() {
        QueryBuilder result = new QueryBuilder();
        result.setQueryAskType();
        return result;
    }

    public static QueryBuilder createConstruct() {
        QueryBuilder result = new QueryBuilder();
        result.setQueryConstructType();
        return result;
    }

    public Query getQuery() {
        return query;
    }

    public QueryBuilder addPattern(Element elt) {
        queryPatterns.addElement(elt);
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addPatterns(Element... elts) {
        for (Element elt : elts) {
            queryPatterns.addElement(elt);
        }
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addFilterPattern(Triple t, boolean notExists) {
        if (!notExists) {
            return addTriplePattern(t);
        }

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(t);
        return addFilterPattern(new ElementFilter(new E_NotExists(eg)));
    }

    public QueryBuilder addFilterPattern(ElementFilter elf) {
        queryPatterns.addElementFilter(elf);
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addFilterIsURI(Var var) {
        return addFilterIsURI(var, true);
    }

    public QueryBuilder addFilterIsURI(Var var, boolean value) {
        E_IsURI eIsURI = new E_IsURI(new ExprVar(var.getName()));
        Expr expr = (value) ? eIsURI : new E_LogicalNot(eIsURI);
        return addFilterPattern(new ElementFilter(expr));
    }

    public QueryBuilder addFilterIsLiteral(Var var) {
        return addFilterIsLiteral(var, true);
    }

    public QueryBuilder addFilterIsLiteral(Var var, boolean value) {
        E_IsLiteral eIsLiteral = new E_IsLiteral(new ExprVar(var.getName()));
        Expr expr = (value) ? eIsLiteral : new E_LogicalNot(eIsLiteral);
        return addFilterPattern(new ElementFilter(expr));
    }

    public QueryBuilder addFilterPatterns(ElementFilter... elfs) {
        for (ElementFilter elf : elfs) {
            queryPatterns.addElementFilter(elf);
        }
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePathPattern(Node s, Path path, Node o) {
        ElementPathBlock block = new ElementPathBlock();
        block.addTriplePath(new TriplePath(s, path, o));
        queryPatterns.addElement(block);
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePathPattern(Resource s, Path path, Node o) {
        return addTriplePathPattern(s.asNode(), path, o);
    }

    public QueryBuilder addTriplePathPattern(Node s, Path path, Resource o) {
        return addTriplePathPattern(s, path, o.asNode());
    }

    public QueryBuilder addTriplePathPattern(Resource s, Path path, Resource o) {
        return addTriplePathPattern(s.asNode(), path, o.asNode());
    }

    public QueryBuilder addTriplePattern(Node s, Node p, Node o) {
        queryPatterns.addTriplePattern(new Triple(s, p, o));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Node s, Property p, Node o) {
        queryPatterns.addTriplePattern(new Triple(s, p.asNode(), o));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Node s, Node p, RDFNode o) {
        queryPatterns.addTriplePattern(new Triple(s, p, o.asNode()));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Resource s, Node p, Node o) {
        queryPatterns.addTriplePattern(new Triple(s.asNode(), p, o));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Resource s, Property p, RDFNode o) {
        queryPatterns.addTriplePattern(new Triple(s.asNode(), p.asNode(), o.asNode()));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Node s, Property p, RDFNode o) {
        queryPatterns.addTriplePattern(new Triple(s, p.asNode(), o.asNode()));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Resource s, Node p, RDFNode o) {
        queryPatterns.addTriplePattern(new Triple(s.asNode(), p, o.asNode()));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Resource s, Property p, Node o) {
        queryPatterns.addTriplePattern(new Triple(s.asNode(), p.asNode(), o));
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePattern(Triple t) {
        queryPatterns.addTriplePattern(t);
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePatterns(List<Triple> ts) {
        for (Triple t : ts) {
            queryPatterns.addTriplePattern(t);
        }
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addTriplePatterns(Triple... ts) {
        for (Triple t : ts) {
            queryPatterns.addTriplePattern(t);
        }
        return setQueryPattern(queryPatterns);
    }

    public QueryBuilder addResultVars(Node... nodes) {
        for (Node n : nodes) {
            addResultVar(n);
        }
        return this;
    }

    public QueryBuilder addResultVars(String... varNames) {
        for (String v : varNames) {
            addResultVar(v);
        }
        return this;
    }

    public Var getResultCountVar() {
        return countVar;
    }

    /**
     * Sets the count variable. Note that setting distinct for this query does
     * not entail distinct values for the variable are counted. Setting a query
     * to be distinct will result in
     * <code>SELECT DISTINCT count(?var) AS ?count</code> and not in
     * <code>SELECT count(DISTINCT ?var) AS ?count</code>. To achieve the
     * latter, use the setResultCountVar functions that also take a boolean as
     * argument.
     * 
     * @param instanceVar
     *            The instance variable to count.
     */
    public QueryBuilder setResultCountVar(Var instanceVar) {
        return setResultCountVar(instanceVar, false);
    }

    /**
     * Sets the count variable.
     * 
     * @param instanceVar
     *            The instance variable to count.
     * @param countVar
     *            The variable which will be set to the result from the count
     *            action.
     */
    public QueryBuilder setResultCountVar(Var instanceVar, Var countVar) {
        return setResultCountVar(instanceVar, countVar, false);
    }

    public QueryBuilder setResultCountVar(Var instanceVar, boolean distinct) {
        return setResultCountVar(instanceVar, Var.alloc("count"), distinct);
    }

    public QueryBuilder setResultCountVar(Var instanceVar, Var countVar, boolean distinct) {
        this.countVar = countVar;

        ExprVar expressionVar = new ExprVar(instanceVar);
        Aggregator aggr = (distinct) ? new AggCountVarDistinct(expressionVar) : new AggCountVar(
                expressionVar);
        getProject().add(countVar, allocAggregate(aggr));
        return this;
    }

    public ResultSet execSelect(Model model) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return exec.execSelect();
    }

    public ResultSet execSelect(Model model, QueryBinding[] queryBindings) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return applyBindings(exec, queryBindings).execSelect();
    }

    public ResultSet execSelect(Dataset dataset) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return exec.execSelect();
    }

    public ResultSet execSelect(Dataset dataset, QueryBinding[] queryBindings) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return applyBindings(exec, queryBindings).execSelect();
    }

    public int execCountSelect(Model model) {
        if (countVar == null) {
            return 0;
        }

        QueryExecution exec = QueryExecutionFactory.create(query, model);
        ResultSet queryResult = exec.execSelect();
        if (!queryResult.hasNext()) {
            return 0;
        }
        Literal literal = queryResult.next().getLiteral(countVar.getName());
        return literal.getInt();
    }

    public int execCountSelect(Dataset dataset) {
        if (countVar == null) {
            return 0;
        }

        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        ResultSet queryResult = exec.execSelect();
        if (!queryResult.hasNext()) {
            return 0;
        }
        Literal literal = queryResult.next().getLiteral(countVar.getName());
        return literal.getInt();
    }

    public boolean execAsk(Model model) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return exec.execAsk();
    }

    public boolean execAsk(Model model, QueryBinding[] queryBindings) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return applyBindings(exec, queryBindings).execAsk();
    }

    public boolean execAsk(Dataset dataset) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return exec.execAsk();
    }

    public boolean execAsk(Dataset dataset, QueryBinding[] queryBindings) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return applyBindings(exec, queryBindings).execAsk();
    }

    public Model execConstruct(Model model) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return exec.execConstruct();
    }

    public Model execConstruct(Model model, QueryBinding[] queryBindings) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return applyBindings(exec, queryBindings).execConstruct();
    }

    public Model execConstruct(Dataset dataset) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return exec.execConstruct();
    }

    public Model execConstruct(Dataset dataset, QueryBinding[] queryBindings) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return applyBindings(exec, queryBindings).execConstruct();
    }

    public Model execConstruct(Model model, Model modelToConstructIn) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return exec.execConstruct(modelToConstructIn);
    }

    public Model execConstruct(Model model, QueryBinding[] queryBindings, Model modelToConstructIn) {
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        return applyBindings(exec, queryBindings).execConstruct(modelToConstructIn);
    }

    public Model execConstruct(Dataset dataset, Model modelToConstructIn) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return exec.execConstruct(modelToConstructIn);
    }

    public Model execConstruct(Dataset dataset, QueryBinding[] queryBindings,
            Model modelToConstructIn) {
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        return applyBindings(exec, queryBindings).execConstruct(modelToConstructIn);
    }

    private QueryExecution applyBindings(QueryExecution exec, QueryBinding[] queryBindings) {
        if ((exec != null) && (queryBindings != null)) {
            QuerySolutionMap qsm = new QuerySolutionMap();
            for (QueryBinding binding : queryBindings) {
                if ((binding.getName() != null) && (binding.getNode() != null)) {
                    qsm.add(binding.getName(), binding.getNode());
                }
            }
            exec.setInitialBinding(qsm);
        }
        return exec;
    }

    /*
     * From here on out, all the functions from com.hp.pl.jena.query.Query are
     * listed, with the difference that its methods have been replaced by
     * functions returning this object
     */

    public QueryBuilder setQuerySelectType() {
        query.setQuerySelectType();
        return this;
    }

    public QueryBuilder setQueryConstructType() {
        query.setQueryConstructType();
        return this;
    }

    public QueryBuilder setQueryDescribeType() {
        query.setQueryDescribeType();
        return this;
    }

    public QueryBuilder setQueryAskType() {
        query.setQueryAskType();
        return this;
    }

    public int getQueryType() {
        return query.getQueryType();
    }

    public boolean isSelectType() {
        return query.isSelectType();
    }

    public boolean isConstructType() {
        return query.isConstructType();
    }

    public boolean isDescribeType() {
        return query.isDescribeType();
    }

    public boolean isAskType() {
        return query.isAskType();
    }

    public boolean isUnknownType() {
        return query.isUnknownType();
    }

    public Prologue getPrologue() {
        return query.getPrologue();
    }

    public QueryBuilder setStrict(boolean isStrict) {
        query.setStrict(isStrict);
        return this;
    }

    public boolean isStrict() {
        return query.isStrict();
    }

    public QueryBuilder setDistinct(boolean b) {
        query.setDistinct(b);
        return this;
    }

    public boolean isDistinct() {
        return query.isDistinct();
    }

    public QueryBuilder setReduced(boolean b) {
        query.setReduced(b);
        return this;
    }

    public boolean isReduced() {
        return query.isReduced();
    }

    public Syntax getSyntax() {
        return query.getSyntax();
    }

    public QueryBuilder setSyntax(Syntax syntax) {
        query.setSyntax(syntax);
        return this;
    }

    public long getLimit() {
        return query.getLimit();
    }

    public QueryBuilder setLimit(long limit) {
        query.setLimit(limit);
        return this;
    }

    public boolean hasLimit() {
        return query.hasLimit();
    }

    public long getOffset() {
        return query.getOffset();
    }

    public QueryBuilder setOffset(long offset) {
        query.setOffset(offset);
        return this;
    }

    public boolean hasOffset() {
        return query.hasOffset();
    }

    public boolean hasOrderBy() {
        return query.hasOrderBy();
    }

    public boolean isOrdered() {
        return query.isOrdered();
    }

    public QueryBuilder addOrderBy(SortCondition condition) {
        query.addOrderBy(condition);
        return this;
    }

    public QueryBuilder addOrderBy(Expr expr, int direction) {
        query.addOrderBy(expr, direction);
        return this;
    }

    public QueryBuilder addOrderBy(Node var, int direction) {
        query.addOrderBy(var, direction);
        return this;
    }

    public QueryBuilder addOrderBy(String varName, int direction) {
        query.addOrderBy(varName, direction);
        return this;
    }

    public List<SortCondition> getOrderBy() {
        return query.getOrderBy();
    }

    public boolean isQueryResultStar() {
        return query.isQueryResultStar();
    }

    public QueryBuilder setQueryResultStar(boolean isQueryStar) {
        query.setQueryResultStar(isQueryStar);
        return this;
    }

    public QueryBuilder setQueryPattern(Element elt) {
        if (createNewElement) {
            queryPatterns = new ElementGroup();
            queryPatterns.addElement(elt);
        }
        query.setQueryPattern(queryPatterns);
        return this;
    }

    public Element getQueryPattern() {
        return query.getQueryPattern();
    }

    public QueryBuilder addGraphURI(String s) {
        query.addGraphURI(s);
        return this;
    }

    public QueryBuilder addNamedGraphURI(String uri) {
        query.addNamedGraphURI(uri);
        return this;
    }

    public List<String> getGraphURIs() {
        return query.getGraphURIs();
    }

    public boolean usesGraphURI(String uri) {
        return query.usesGraphURI(uri);
    }

    public List<String> getNamedGraphURIs() {
        return query.getNamedGraphURIs();
    }

    public boolean usesNamedGraphURI(String uri) {
        return query.usesNamedGraphURI(uri);
    }

    public boolean hasDatasetDescription() {
        return query.hasDatasetDescription();
    }

    public DatasetDescription getDatasetDescription() {
        return query.getDatasetDescription();
    }

    public List<String> getResultVars() {
        return query.getResultVars();
    }

    public List<Var> getProjectVars() {
        return query.getProjectVars();
    }

    public VarExprList getProject() {
        return query.getProject();
    }

    public QueryBuilder addProjectVars(Collection<?> vars) {
        query.addProjectVars(vars);
        return this;
    }

    public QueryBuilder addResultVar(String varName) {
        query.addResultVar(varName);
        return this;
    }

    public QueryBuilder addResultVar(Node v) {
        query.addResultVar(v);
        return this;
    }

    public QueryBuilder addResultVar(Node v, Expr expr) {
        query.addResultVar(v, expr);
        return this;
    }

    public QueryBuilder addResultVar(Expr expr) {
        query.addResultVar(expr);
        return this;
    }

    public QueryBuilder addResultVar(String varName, Expr expr) {
        query.addResultVar(varName, expr);
        return this;
    }

    public boolean hasGroupBy() {
        return query.hasGroupBy();
    }

    public boolean hasHaving() {
        return query.hasHaving();
    }

    public VarExprList getGroupBy() {
        return query.getGroupBy();
    }

    public List<Expr> getHavingExprs() {
        return query.getHavingExprs();
    }

    public QueryBuilder addGroupBy(String varName) {
        query.addGroupBy(varName);
        return this;
    }

    public QueryBuilder addGroupBy(Node v) {
        query.addGroupBy(v);
        return this;
    }

    public QueryBuilder addGroupBy(Expr expr) {
        query.addGroupBy(expr);
        return this;
    }

    public QueryBuilder addGroupBy(Var v, Expr expr) {
        query.addGroupBy(v, expr);
        return this;
    }

    public QueryBuilder addHavingCondition(Expr expr) {
        query.addHavingCondition(expr);
        return this;
    }

    public boolean hasAggregators() {
        return query.hasAggregators();
    }

    public List<ExprAggregator> getAggregators() {
        return query.getAggregators();
    }

    public Expr allocAggregate(Aggregator agg) {
        return query.allocAggregate(agg);
    }

    public boolean hasValues() {
        return query.hasValues();
    }

    public List<Var> getValuesVariables() {
        return query.getValuesVariables();
    }

    public List<Binding> getValuesData() {
        return query.getValuesData();
    }

    public QueryBuilder setValuesDataBlock(List<Var> variables, List<Binding> values) {
        query.setValuesDataBlock(variables, values);
        return this;
    }

    public Template getConstructTemplate() {
        return query.getConstructTemplate();
    }

    public QueryBuilder setConstructTemplate(Template templ) {
        query.setConstructTemplate(templ);
        return this;
    }

    public QueryBuilder addDescribeNode(Node node) {
        query.addDescribeNode(node);
        return this;
    }

    public List<Node> getResultURIs() {
        return query.getResultURIs();
    }

    public QueryBuilder setResultVars() {
        query.setResultVars();
        return this;
    }

    public QueryBuilder visit(QueryVisitor visitor) {
        query.visit(visitor);
        return this;
    }

    @Override
    public Object clone() {
        return cloneQuery();
    }

    public QueryBuilder cloneQuery() {
        return new QueryBuilder(query.cloneQuery());
    }

    @Override
    public String toString() {
        return query.toString();
    }

    public String toString(Syntax syntax) {
        return query.toString(syntax);
    }

    @Override
    public int hashCode() {
        return query.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return query.equals(other);
    }

    public QueryBuilder output(IndentedWriter out) {
        query.output(out);
        return this;
    }

    public String serialize() {
        return query.serialize();
    }

    public String serialize(Syntax syntax) {
        return query.serialize(syntax);
    }

    public QueryBuilder serialize(OutputStream out) {
        query.serialize(out);
        return this;
    }

    public QueryBuilder serialize(OutputStream out, Syntax syntax) {
        query.serialize(out, syntax);
        return this;
    }

    public QueryBuilder serialize(IndentedLineBuffer buff) {
        query.serialize(buff);
        return this;
    }

    public QueryBuilder serialize(IndentedLineBuffer buff, Syntax outSyntax) {
        query.serialize(buff, outSyntax);
        return this;
    }

    public QueryBuilder serialize(IndentedWriter writer) {
        query.serialize(writer);
        return this;
    }

    public QueryBuilder serialize(IndentedWriter writer, Syntax outSyntax) {
        query.serialize(writer, outSyntax);
        return this;
    }

    public QueryBuilder setCreateNewElement(boolean createNewElement) {
        this.createNewElement = createNewElement;
        return this;
    }

}

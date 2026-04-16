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

package com.semmtech.plugin.semmweb.sparql.debug.ui;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.google.common.base.Strings;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.sparql.engine.http.Service;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.grammars.antlr.sparql.SparqlLexer;
import com.semmtech.grammars.antlr.sparql.SparqlParser;
import com.semmtech.io.StringOutputStream;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.plugin.semmweb.core.util.WorkspaceUtils;
import com.semmtech.plugin.semmweb.sparql.SparqlPlugin;
import com.semmtech.plugin.semmweb.sparql.SparqlQueryType;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * http://www.eclipse.org/articles/Article-Launch-Framework/launch.html
 * 
 * @author Mike Henrichs
 * 
 */
public class SparqlQueryLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
    private static Logger logger = Logger.getLogger(SparqlQueryLaunchConfigurationDelegate.class);

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
            IProgressMonitor monitor) throws CoreException {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask("Running SPARQL Query", 4);
        // Check for cancellation
        if (monitor.isCanceled()) {
            return;
        }
        try {
            monitor.subTask("Initializing execution...");
            boolean openOnCompletion = getOpenOnCompletion(configuration);
            boolean useExternal = getUseExternalModel(configuration);
            boolean useEmptyModel = getUseEmptyModel(configuration);
            boolean isSparqlEndPoint = getIsSparqlEndPoint(configuration);

            boolean customContext = getUseCustomContext(configuration);

            String inputModelUrl = getInputModelURL(configuration);
            String inputModelFile = getInputModelFile(configuration);
            String queryFile = getQueryFile(configuration);
            String outputFile = getOutputFilename(configuration);
            String outputFolder = getOutputFolder(configuration);
            final String outputSytax = getOutputSyntax(configuration);

            monitor.worked(1);
            if (monitor.isCanceled()) {
                return;
            }

            monitor.subTask(String.format("Reading input model from \"%s\"...", inputModelUrl));
            Model model = null;

            if (useExternal) {
                ModelMaker maker = ModelFactory.createMemModelMaker();

                model = maker.getModel(inputModelUrl, new ModelReader() {
                    @Override
                    public Model readModel(Model toRead, String url) {
                        String lang = FileUtils.guessLang(url);
                        if (url.endsWith(".owl")) {
                            lang = FileUtils.langXMLAbbrev;
                        }
                        toRead.read(url, lang);
                        return toRead;
                    }
                });
            }
            else if (useEmptyModel) {
                model = ModelFactory.createDefaultModel();
            }
            else {
                String lang = FileUtils.guessLang(inputModelFile);
                model = ModelFactory.createDefaultModel();

                try (InputStream fis = ResourcesUtil.getFileUtf8Stream(WorkspaceUtils
                        .getFileFromAbsolutePath(inputModelFile))) {
                    model.read(fis, null, lang);
                }
            }

            monitor.worked(1);
            if (monitor.isCanceled()) {
                return;
            }

            monitor.subTask("Parsing query...");
            String sparqlQuery = org.apache.commons.io.FileUtils.readFileToString(new File(
                    queryFile));
            SparqlQueryType queryType = getQueryType(sparqlQuery);
            Query query = QueryFactory.create(sparqlQuery);

            QueryExecution execution = null;
            if (!useExternal || !isSparqlEndPoint) {
                execution = QueryExecutionFactory.create(query, model);
            }
            else {
                execution = QueryExecutionFactory.createServiceRequest(inputModelUrl, query);
            }

            // The next statements set the context
            // See also:
            // http://jena.apache.org/documentation/query/service.html#controlling-service-requests
            if (customContext) {
                String authUser = getAuthUser(configuration);
                String authPassword = getAuthPassword(configuration);
                execution.getContext().set(Service.queryAuthUser, authUser);
                execution.getContext().set(Service.queryAuthPwd, authPassword);
            }
            monitor.worked(1);

            if (monitor.isCanceled()) {
                return;
            }

            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IContainer[] containers = root.findContainersForLocationURI(new File(outputFolder)
                    .toURI());
            if (containers.length > 0) {
                IContainer container = containers[0];
                final IFile resource = container.getFile(new Path(String.format("%s%s", outputFile,
                        outputSytax)));

                ResultsFormat format = ResultsFormat.guessSyntax(outputSytax,
                        ResultsFormat.FMT_TEXT);
                switch (queryType) {
                case ASK:
                    monitor.subTask("Executing ASK query...");
                    final boolean answer = execution.execAsk();
                    monitor.worked(1);

                    EclipseUIPlugin.getStandardDisplay().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
                            MessageDialog.openInformation(shell, "ASK Query", (answer ? "YES"
                                    : "NO"));
                        }
                    });

                    break;
                case SELECT:
                    monitor.subTask("Executing SELECT query...");
                    ResultSet selectResult = execution.execSelect();
                    monitor.worked(1);

                    monitor.subTask("Saving results...");
                    StringOutputStream stream = new StringOutputStream();
                    outputResult(stream, selectResult, format);
                    String content = stream.toString();
                    if (!resource.exists()) {
                        resource.create(new ByteArrayInputStream(content.getBytes()),
                                IResource.FORCE, monitor);
                    }
                    else {
                        resource.setContents(new ByteArrayInputStream(content.getBytes()),
                                IResource.FORCE, monitor);
                    }
                    stream.close();
                    monitor.worked(1);

                    if (openOnCompletion) {
                        EclipseUIPlugin.getStandardDisplay().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                IWorkbenchPage page = SparqlPlugin.getActivePage();
                                try {
                                    resource.getParent().refreshLocal(IResource.DEPTH_ONE,
                                            new NullProgressMonitor());
                                    if (outputSytax
                                            .equals(SparqlLaunchConfigurationConstants.CSV_EXTENSION)
                                            || outputSytax
                                                    .equals(SparqlLaunchConfigurationConstants.TXT_EXTENSION)
                                            || outputSytax
                                                    .equals(SparqlLaunchConfigurationConstants.TSV_EXTENSION)) {
                                        IDE.openEditor(page, resource,
                                                "org.eclipse.ui.DefaultTextEditor");
                                    }
                                    else {
                                        IDE.openEditor(page, resource, true);
                                    }
                                }
                                catch (PartInitException e) {
                                    e.printStackTrace();
                                }
                                catch (CoreException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    break;
                case CONSTRUCT:
                    monitor.subTask("Executing CONSTRUCT query...");
                    long start = Calendar.getInstance().getTimeInMillis();
                    Model constructResult = execution.execConstruct();
                    logger.info(String.format("execConstruct() took %s ms.", Calendar.getInstance()
                            .getTimeInMillis() - start));
                    monitor.worked(1);

                    monitor.subTask("Saving results...");
                    ByteArrayOutputStream constructStream = new ByteArrayOutputStream();
                    start = Calendar.getInstance().getTimeInMillis();
                    constructResult.write(constructStream,
                            FileUtils.guessLang(outputSytax, FileUtils.langTurtle));
                    constructStream.flush();
                    logger.info(String.format("Creating stream took %s ms.", Calendar.getInstance()
                            .getTimeInMillis() - start));

                    if (!resource.exists()) {
                        resource.create(new ByteArrayInputStream(constructStream.toByteArray()),
                                IResource.FORCE, monitor);
                    }
                    else {
                        resource.setContents(
                                new ByteArrayInputStream(constructStream.toByteArray()),
                                IResource.FORCE, monitor);
                    }
                    constructStream.close();
                    monitor.worked(1);

                    if (openOnCompletion) {
                        EclipseUIPlugin.getStandardDisplay().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                IWorkbenchPage page = SparqlPlugin.getActivePage();
                                try {
                                    resource.getParent().refreshLocal(IResource.DEPTH_ONE,
                                            new NullProgressMonitor());
                                    IDE.openEditor(page, resource, true);
                                }
                                catch (PartInitException e) {
                                    e.printStackTrace();
                                }
                                catch (CoreException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
                case DESCRIBE:
                    monitor.subTask("Executing DESCRIBE query...");
                    Model describeResult = execution.execDescribe();
                    monitor.worked(1);

                    monitor.subTask("Saving results...");
                    ByteArrayOutputStream describeStream = new ByteArrayOutputStream();
                    describeResult.write(describeStream,
                            FileUtils.guessLang(outputSytax, FileUtils.langTurtle));
                    describeStream.flush();
                    if (!resource.exists()) {
                        resource.create(new ByteArrayInputStream(describeStream.toByteArray()),
                                IResource.FORCE, monitor);
                    }
                    else {
                        resource.setContents(
                                new ByteArrayInputStream(describeStream.toByteArray()),
                                IResource.FORCE, monitor);
                    }
                    describeStream.close();
                    monitor.worked(1);

                    if (openOnCompletion) {
                        EclipseUIPlugin.getStandardDisplay().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                IWorkbenchPage page = SparqlPlugin.getActivePage();
                                try {
                                    resource.getParent().refreshLocal(IResource.DEPTH_ONE,
                                            new NullProgressMonitor());
                                    IDE.openEditor(page, resource, true);
                                }
                                catch (PartInitException e) {
                                    e.printStackTrace();
                                }
                                catch (CoreException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
                case NONE:
                default:
                    break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            monitor.done();
        }
    }

    private boolean getUseEmptyModel(ILaunchConfiguration configuration) throws CoreException {
        return configuration.getAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_EMPTY_MODEL,
                false);
    }

    protected void outputResult(OutputStream stream, ResultSet result, ResultsFormat format) {
        if (format.equals(ResultsFormat.FMT_RS_CSV)) {
            ResultSetFormatter.outputAsCSV(stream, result);
        }
        else if (format.equals(ResultsFormat.FMT_RS_JSON)) {
            ResultSetFormatter.outputAsJSON(stream, result);
        }
        else if (format.equals(ResultsFormat.FMT_RDF_N3)) {
            ResultSetFormatter.outputAsRDF(stream, FileUtils.langN3, result);
        }
        else if (format.equals(ResultsFormat.FMT_RDF_NT)) {
            ResultSetFormatter.outputAsRDF(stream, FileUtils.langNTriple, result);
        }
        else if (format.equals(ResultsFormat.FMT_RDF_TURTLE)) {
            ResultSetFormatter.outputAsRDF(stream, FileUtils.langTurtle, result);
        }
        else if (format.equals(ResultsFormat.FMT_RDF_XML)) {
            ResultSetFormatter.outputAsRDF(stream, FileUtils.langXML, result);
        }
        else if (format.equals(ResultsFormat.FMT_RS_SSE)) {
            ResultSetFormatter.outputAsSSE(stream, result);
        }
        else if (format.equals(ResultsFormat.FMT_TEXT)) {
            ResultSetFormatter.out(stream, result);
        }
        else if (format.equals(ResultsFormat.FMT_RS_TSV)) {
            ResultSetFormatter.outputAsTSV(stream, result);
        }
        else if (format.equals(ResultsFormat.FMT_RS_XML)) {
            ResultSetFormatter.outputAsXML(stream, result);
        }
        else {
            ResultSetFormatter.out(stream, result);
        }
    }

    protected SparqlQueryType getQueryType(String sparql) {
        ANTLRStringStream input = new ANTLRStringStream(sparql);
        SparqlLexer lex = new SparqlLexer(input);
        Token token = lex.nextToken();

        while (token != Token.EOF_TOKEN) {
            if (token.getType() == SparqlParser.ASK) {
                return SparqlQueryType.ASK;
            }
            if (token.getType() == SparqlParser.SELECT) {
                return SparqlQueryType.SELECT;
            }
            if (token.getType() == SparqlParser.CONSTRUCT) {
                return SparqlQueryType.CONSTRUCT;
            }
            if (token.getType() == SparqlParser.DESCRIBE) {
                return SparqlQueryType.DESCRIBE;
            }
            token = lex.nextToken();
        }
        return SparqlQueryType.NONE;
    }

    protected boolean getUseExternalModel(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_USE_EXTERNAL_MODEL,
                false);
    }

    protected boolean getIsSparqlEndPoint(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_SPARQL_END_POINT, false);
    }

    protected String getQueryFile(ILaunchConfiguration config) throws CoreException {
        String attribute = config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE,
                (String) null);
        if (attribute != null) {
            return VariablesPlugin.getDefault().getStringVariableManager()
                    .performStringSubstitution(attribute);
        }
        return null;
    }

    protected String getInputModelFile(ILaunchConfiguration config) throws CoreException {
        String attribute = config.getAttribute(
                SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_FILE, (String) null);
        if (attribute != null) {
            return VariablesPlugin.getDefault().getStringVariableManager()
                    .performStringSubstitution(attribute);
        }
        return null;
    }

    protected String getInputModelURL(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_INPUT_MODEL_URL,
                (String) null);
    }

    protected String getOutputSyntax(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_OUTPUT_SYNTAX,
                (String) null);
    }

    protected String getOutputFilename(ILaunchConfiguration config) throws CoreException {
        String outputFilename = config.getAttribute(
                SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME, (String) null);
        if (Strings.isNullOrEmpty(outputFilename)) {
            // return default filename, based on main execution attributes;
            // note: extension (e.g., ".csv") gets added elsewhere
            String queryFilename = getQueryFile(config).replaceAll(".*[\\\\/]", "");
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

            String inputFile = getInputModelFile(config);
            if (!Strings.isNullOrEmpty(inputFile)) {
                inputFile = inputFile.replaceAll(".*[\\\\/]", "");
                outputFilename = "query-results_[input]" + inputFile.replaceAll(".*[\\\\/]", "")
                        + "_[query]" + queryFilename + "_[time]" + timestamp;
            }
            else {
                String inputURL = getInputModelURL(config);
                outputFilename = "query-results_[input]" + inputURL.replaceAll("\\W+", "")
                        + "_[query]" + queryFilename + "_[time]" + timestamp;
            }
        }
        return outputFilename;
    }

    protected boolean getUseCustomContext(ILaunchConfiguration config) throws CoreException {
        return Boolean.parseBoolean(config.getAttribute(
                SparqlLaunchConfigurationConstants.ATTR_CUSTOM_CONTEXT, "false"));
    }

    protected String getAuthUser(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_USER, "");
    }

    protected String getAuthPassword(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_PASSWORD, "");
    }

    protected String getOutputFolder(ILaunchConfiguration config) throws CoreException {
        String attribute = config.getAttribute(
                SparqlLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER, (String) null);
        if (!Strings.isNullOrEmpty(attribute)) {
            return VariablesPlugin.getDefault().getStringVariableManager()
                    .performStringSubstitution(attribute);
        }

        // switch to output folder based on location of input model
        String inputFile = getInputModelFile(config);
        if (!Strings.isNullOrEmpty(inputFile)) {
            // retain only the folder, removing the filename;
            // if it's the "models" folder, use its parent folder instead
            String folder = inputFile.replaceAll("[^\\\\]*$", "");
            return folder.replaceAll("models\\\\$", "");
        }

        // switch to output folder based on location of query
        String queryFile = getQueryFile(config);
        if (!Strings.isNullOrEmpty(queryFile)) {
            // retain only the folder, removing the filename
            return queryFile.replaceAll("[^\\\\]*$", "");
        }

        return null;
    }

    protected boolean getOpenOnCompletion(ILaunchConfiguration config) throws CoreException {
        return config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_OPEN_FILE, false);
    }
}

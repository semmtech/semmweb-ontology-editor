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

package com.semmtech.plugin.semmweb.core.search;


import java.io.InputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PlatformUI;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;


public class OntologySearchQuery implements ISearchQuery {

    private static Logger logger = Logger.getLogger(OntologySearchQuery.class);

    private final OntologySearchResult searchResult;

    private final OntologySearchTarget target;

    private final String searchText;
    @SuppressWarnings("unused")
    private final boolean isRegex;
    private final boolean isCaseSensitive;
    private final FileTextSearchScope scope;

    private IProgressMonitor progressMonitor;

    private int numberOfFileScanned;

    private int numberOfFilesToScan;

    private int numberOfMatches;

    private IFile currentFile;

    public OntologySearchQuery(String searchText, boolean isRegex, boolean isCaseSensitive,
            OntologySearchTarget target, FileTextSearchScope scope) {
        this.searchResult = new OntologySearchResult(this);

        this.searchText = searchText;
        this.isRegex = isRegex;
        this.isCaseSensitive = isCaseSensitive;
        this.target = target;
        this.scope = scope;
    }

    public FileTextSearchScope getSearchScope() {
        return scope;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {

        aboutToStart();

        if (monitor != null && monitor.isCanceled()) {
            throw new OperationCanceledException("Search cancelled");
        }

        // TODO: This is the base for search; requires rewrite and refactor!
        MultiStatus status = new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK,
                "Problems encountered during search.", null);
        IFile[] files = scope.evaluateFilesInScope(status);

        progressMonitor = (monitor == null) ? new NullProgressMonitor() : monitor;
        numberOfFileScanned = 0;
        numberOfMatches = 0;
        numberOfFilesToScan = files.length;
        currentFile = null;

        Job monitorUpdateJob = new Job("Search progress polling") {
            private int lastNumberOfScannedFiles = 0;

            @Override
            public IStatus run(IProgressMonitor inner) {
                while (!inner.isCanceled()) {
                    IFile file = currentFile;
                    if (file != null) {
                        String filename = file.getName();

                        progressMonitor.subTask(String.format("Scanning file %s of %s: %s",
                                numberOfFileScanned, numberOfFilesToScan, filename));
                        int steps = numberOfFileScanned - lastNumberOfScannedFiles;
                        progressMonitor.worked(steps);
                        lastNumberOfScannedFiles += steps;
                    }
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        return Status.OK_STATUS;
                    }
                }
                return Status.OK_STATUS;
            }
        };

        try {
            progressMonitor.beginTask("Searching files...", numberOfFilesToScan);
            monitorUpdateJob.setSystem(true);
            monitorUpdateJob.schedule();
            try {
                // fCollector.beginReporting();
                processFiles(files);
                return Status.OK_STATUS;
            }
            finally {
                monitorUpdateJob.cancel();
            }
        }
        finally {
            progressMonitor.done();
            // fCollector.endReporting();
        }
    }

    private void processFiles(IFile[] files) {
        String sparql = null;

        switch (target) {
        case LITERAL:
            sparql = "" + "SELECT ?resource ?p ?literal " + "WHERE { "
                    + " 	?resource ?p ?literal . "
                    + "	FILTER (isLiteral(?literal) && regex(?literal, \"" + searchText + "\", \""
                    + ((isCaseSensitive) ? "" : "i") + "\")) " + "}";
            break;
        case URI:
        default:
            sparql = "" + "SELECT DISTINCT ?resource " + "WHERE { " + "	?resource ?p ?o . "
                    + " 	FILTER(regex(str(?resource), \"" + searchText + "\", \""
                    + ((isCaseSensitive) ? "" : "i") + "\")) " + "}";
            break;
        }
        Query query = QueryFactory.create(sparql);

        for (int i = 0; i < files.length; i++) {
            currentFile = files[i];
            String filename = currentFile.getName();
            String lang = FileUtils.guessLang(filename, null);

            if (filename.endsWith(".owl")) {
                lang = FileUtils.langXMLAbbrev;
            }

            try (InputStream stream = ResourcesUtil.getFileUtf8Stream(currentFile)) {

                // ModelMaker maker = ModelFactory.createMemModelMaker();
                // OntDocumentManager manager = new OntDocumentManager();
                // manager.setProcessImports(false);
                // OntModelSpec spec = new OntModelSpec(maker, maker, manager,
                // null, ProfileRegistry.RDFS_LANG);
                Model model = ModelFactory.createDefaultModel();
                model.read(stream, null, lang);
                QueryExecution execution = QueryExecutionFactory.create(query, model);
                ResultSet result = execution.execSelect();

                while (result.hasNext()) {
                    QuerySolution solution = result.nextSolution();
                    Resource resource = solution.getResource("resource");
                    numberOfMatches++;
                    OntologyResourceMatch match = null;
                    if (target == OntologySearchTarget.LITERAL) {
                        Property property = solution.getResource("p").as(Property.class);
                        Literal literal = solution.getLiteral("literal");
                        match = new OntologyStatementMatch(currentFile, resource, property, literal);
                        ((OntologyStatementMatch) match).setPropertyID(model.qnameFor(property
                                .getURI()));
                    }
                    else {
                        match = new OntologyResourceMatch(currentFile, resource);
                    }

                    if (!resource.isAnon()) {
                        match.setResourceID(model.qnameFor(resource.getURI()));
                    }
                    searchResult.addMatch(new Match(match, 1, 1));
                }
            }
            catch (Throwable ex) {
                logger.error("An error occurred while searching on file: " + filename, ex);
            }
            numberOfFileScanned++;

            if (progressMonitor.isCanceled()) {
                throw new OperationCanceledException("Operation cancelled");
            }
        }
    }

    private void aboutToStart() {
        searchResult.removeAll();
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                NewSearchUI.activateSearchResultView();
            }
        });
    }

    @Override
    public String getLabel() {
        return "Searching ontologies...";
    }

    @Override
    public boolean canRerun() {
        return true;
    }

    @Override
    public boolean canRunInBackground() {
        return true;
    }

    @Override
    public ISearchResult getSearchResult() {
        return searchResult;
    }

    public String getResultLabel() {
        // if (searchText.length() > 0) {
        // // text search
        // if (isScopeAllFileTypes()) {
        // // search all file extensions
        // if (nMatches == 1) {
        // Object[] args= { searchString, fScope.getDescription() };
        // return Messages.format(SearchMessages.FileSearchQuery_singularLabel,
        // args);
        // }
        // Object[] args= { searchString, new Integer(nMatches),
        // fScope.getDescription() };
        // return Messages.format(SearchMessages.FileSearchQuery_pluralPattern,
        // args);
        // }
        // // search selected file extensions
        // if (nMatches == 1) {
        // Object[] args= { searchString, fScope.getDescription(),
        // fScope.getFilterDescription() };
        // return
        // Messages.format(SearchMessages.FileSearchQuery_singularPatternWithFileExt,
        // args);
        // }
        // Object[] args= { searchString, new Integer(nMatches),
        // fScope.getDescription(), fScope.getFilterDescription() };
        // return
        // Messages.format(SearchMessages.FileSearchQuery_pluralPatternWithFileExt,
        // args);
        // }
        // // file search
        // if (nMatches == 1) {
        // Object[] args= { fScope.getFilterDescription(),
        // fScope.getDescription() };
        // return
        // Messages.format(SearchMessages.FileSearchQuery_singularLabel_fileNameSearch,
        // args);
        // }
        // Object[] args= { fScope.getFilterDescription(), new
        // Integer(nMatches), fScope.getDescription() };
        // return
        // Messages.format(SearchMessages.FileSearchQuery_pluralPattern_fileNameSearch,
        // args);
        if (searchText.length() > 0) {
            return String.format("'%s' - Found %s matches", searchText, numberOfMatches);
        }
        return String.format("<result label>");
    }

}

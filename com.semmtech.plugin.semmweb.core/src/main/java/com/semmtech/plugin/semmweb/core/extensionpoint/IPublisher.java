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

package com.semmtech.plugin.semmweb.core.extensionpoint;


import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public interface IPublisher {
    public class RepositoryInfo {
        public String id;
        public String name;
        public String uri;
    }

    public class PublicationInfo {
        public String id;
        public String name;
        public String uri;
        public String versioningModeId;
    }

    public class VersioningMode {
        public String id;
        public String name;
        public boolean isSeries;
        public boolean isCustomVersioning;
        public String regexCustomVersioning;
    }

    String getName();

    String getBaseURI();

    String getServerURL();

    List<VersioningMode> getVersioningOptions();

    List<RepositoryInfo> listWritableRepositories();

    List<PublicationInfo> listPublications(String repoId);

    /**
     * Returns the URL of the published ontology
     * 
     * @param model
     * @param uri
     * @return
     */
    String publishModel(Model model, String uri, Model metadata, VersioningMode versioningMode,
            Object versioningModeSettings, IPublishErrorHandler errorHandler);

    Model downloadModel(String uri, IDownloadModelHandler downloadHandler);

    boolean serves(String url);

    boolean containsOntology(String ontologyUri);
}

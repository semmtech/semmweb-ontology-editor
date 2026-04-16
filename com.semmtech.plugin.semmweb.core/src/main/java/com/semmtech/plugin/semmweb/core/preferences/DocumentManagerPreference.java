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

package com.semmtech.plugin.semmweb.core.preferences;


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.jena.ontology.OntologySpec;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.ui.plugin.preference.BasePreference;


public final class DocumentManagerPreference extends BasePreference {

    private static Logger logger = Logger.getLogger(DocumentManagerPreference.class);

    public static final String PREFERENCE_QUALIFIER = "documentManager.project";

    public static final String PREFERENCE_PRE_LOADING = "com.semmtech.plugin.semmweb.core.preferences.documentManager.preLoading";
    public static final String PREFERENCE_DISABLED_IMPORTS = "com.semmtech.plugin.semmweb.core.preferences.documentManager.disabledImports";
    public static final String PREFERENCE_DOCUMENT_MANAGER_CONFIG = "com.semmtech.plugin.semmweb.core.preferences.documentManager.documentManagerConfig";

    public static final String VALUE_PRE_LOADING_NEVER = "valuePreLoadingNever";
    public static final String VALUE_PRE_LOADING_RDFOWL = "valuePreLoadingRdfOwl";
    public static final String VALUE_PRE_LOADING_ALLWAYS = "valuePreLoadingAllways";

    protected static final String FILE_DEVICE = "file:";

    protected final IPath relativeBase;

    protected DocumentManagerPreference(IPreferenceStore store, IProject project) {
        super(store);
        relativeBase = (project == null) ? null : project.getLocation();
        setDefaults();
    }

    public void setDefaults() {
        getPreferenceStore().setDefault(PREFERENCE_PRE_LOADING, VALUE_PRE_LOADING_RDFOWL);
        String json = JsonUtil.getJSONFromConfig(getDefaultConfiguration(), relativeBase);
        getPreferenceStore().setDefault(PREFERENCE_DOCUMENT_MANAGER_CONFIG, json);
    }

    public boolean isPreLoadingAllways() {
        String preference = getPreferenceStore().getString(PREFERENCE_PRE_LOADING);
        return preference.equals(VALUE_PRE_LOADING_ALLWAYS);
    }

    public boolean isDisabledImport(String uri) {
        List<String> uris = getDisabledImports();
        return uris.contains(uri);
    }

    public void setDisabledImport(String uri, boolean disabled) {
        List<String> uris = getDisabledImports();
        if (disabled && !uris.contains(uri)) {
            uris.add(uri);
            setDisabledImports(uris);
        }
        else if (!disabled && uris.contains(uri)) {
            uris.remove(uri);
            setDisabledImports(uris);
        }
    }

    public List<String> getDisabledImports() {
        return getValueUsingJSON(PREFERENCE_DISABLED_IMPORTS, new TypeReference<List<String>>() {
        }, new ArrayList<String>());
    }

    public void setDisabledImports(List<String> uris) {
        storeValueUsingJSON(uris, PREFERENCE_DISABLED_IMPORTS);
    }

    public boolean isPreLoadingNever() {
        String preference = getPreferenceStore().getString(PREFERENCE_PRE_LOADING);
        return preference.equals(VALUE_PRE_LOADING_NEVER);
    }

    public boolean isPreLoadingRDFOWL() {
        String preference = getPreferenceStore().getString(PREFERENCE_PRE_LOADING);
        return preference.equals(VALUE_PRE_LOADING_RDFOWL);
    }

    public String getPreLoadingValue() {
        return getPreferenceStore().getString(PREFERENCE_PRE_LOADING);
    }

    public void setPreLoading(String value) {
        getPreferenceStore().setValue(PREFERENCE_PRE_LOADING, value);
    }

    public void setDocumentManagerConfig(WorkspaceDocumentManagerConfiguration config) {
        String json = JsonUtil.getJSONFromConfig(config, relativeBase);
        getPreferenceStore().setValue(PREFERENCE_DOCUMENT_MANAGER_CONFIG, json);
    }

    public WorkspaceDocumentManagerConfiguration getDocumentManagerConfig() {
        String json = getPreferenceStore().getString(PREFERENCE_DOCUMENT_MANAGER_CONFIG);
        return JsonUtil.getConfigFromJSON(json, relativeBase);
    }

    public boolean hasOntologySpec(String publicUri) {
        return getDocumentManagerConfig().listPublicURIs().contains(publicUri);
    }

    /**
     * Returns public URIs for which the currently used alt url location is set
     * to <code>altUrl</code>. That means that if an Alternative Workspace URL
     * is available, only that url is checked and any Alternative External URL
     * isn't.
     */
    public List<String> listReferringSpecs(String altUrl) {
        List<String> uris = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(altUrl)) {
            for (OntologySpec spec : getDocumentManagerConfig().listOntologySpecs()) {
                if (altUrl.equals(spec.getAltURL())) {
                    uris.add(spec.getPublicURI());
                }
            }
        }
        return uris;
    }

    /**
     * Returns the alternative URL for a given public URI based on the document
     * manager. If no alternative could be found and the boolean
     * <code>uriIfAbsent</code> equals <code>true</code>, the public URI will be
     * returned, otherwise <code>null</code>.
     * 
     * @param publicUri
     * @param uriIfAbsent
     * @return
     */
    public String getAltURL(String publicUri, boolean uriIfAbsent) {
        String altUrl = (uriIfAbsent ? publicUri : null);
        WorkspaceOntologySpec ontologySpec = getOntologySpec(publicUri);
        if (ontologySpec != null && !Strings.isNullOrEmpty(ontologySpec.getAltURL())) {
            altUrl = ontologySpec.getAltURL();
        }
        return altUrl;
    }

    public WorkspaceOntologySpec getOntologySpec(String publicUri) {
        return getDocumentManagerConfig().getOntologySpec(publicUri);
    }

    public static WorkspaceDocumentManagerConfiguration getDefaultConfiguration() {
        WorkspaceDocumentManagerConfiguration configuration = new WorkspaceDocumentManagerConfiguration();
        configuration.setProcessImports(true);
        configuration.addOntologySpec(OntologySpec.RDF);
        configuration.addOntologySpec(OntologySpec.RDFS);
        configuration.addOntologySpec(OntologySpec.OWL);
        configuration.addOntologySpec(OntologySpec.SKOS);
        configuration.addOntologySpec(OntologySpec.SKOSXL);
        configuration.addOntologySpec(OntologySpec.DC);
        configuration.addOntologySpec(OntologySpec.DCTERMS);
        configuration.addOntologySpec(OntologySpec.FOAF);
        configuration.addOntologySpec(OntologySpec.VANN);
        configuration.addOntologySpec(OntologySpec.CC);
        configuration.addOntologySpec(OntologySpec.XSD);
        configuration.addOntologySpec(OntologySpec.SH);
        return configuration;
    }

    /**
     * Returns the DocumentManagerPreference for the given project; or if the
     * project is null based on the CorePlugin preference store.
     * 
     * @param project
     * @return
     */
    public static DocumentManagerPreference fromProject(IProject project) {
        IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
        if (project != null) {
            ProjectScope scope = new ProjectScope(project);
            store = new ScopedPreferenceStore(scope, PREFERENCE_QUALIFIER);
        }
        return new DocumentManagerPreference(store, project);
    }

    public static DocumentManagerPreference fromPlugin() {
        return fromProject(null);
    }

    /**
     * Inner class used to convert from- and to JSON syntax.
     * 
     * @author Mike Henrichs
     * 
     */
    private static class JsonUtil {

        private static final String JSON_FIELD_PROCESS_IMPORTS = "processImports";
        private static final String JSON_FIELD_ONTOLOGY_SPECS = "ontologySpecs";
        private static final String JSON_FIELD_PUBLIC_URI = "publicUri";
        private static final String JSON_FIELD_ALT_URL = "altUrl"; // legacy!
        private static final String JSON_FIELD_ALT_URL_IS_RELATIVE = "altUrlIsRelative"; // legacy!
        private static final String JSON_FIELD_EXTERNAL_ALT_URL = "externalAltUrl";
        private static final String JSON_FIELD_WORKSPACE_ALT_URL = "workspaceAltUrl";
        private static final String JSON_FIELD_PREFIX = "prefix";

        /**
         * Helper method for converting a WorkspaceDocumentManagerConfiguration
         * object into JSON.
         * 
         * @param config
         * @return
         */
        public static String getJSONFromConfig(WorkspaceDocumentManagerConfiguration config,
                IPath relativeBase) {
            StringWriter writer = new StringWriter();
            JsonFactory factory = new JsonFactory();
            try (JsonGenerator generator = factory.createJsonGenerator(writer)) {
                generator.writeStartObject();
                generator.writeBooleanField(JSON_FIELD_PROCESS_IMPORTS, config.getProcessImports());
                generator.writeArrayFieldStart(JSON_FIELD_ONTOLOGY_SPECS);
                for (WorkspaceOntologySpec spec : config.listWorkspaceOntologySpecs()) {
                    generator.writeStartObject();
                    generator.writeStringField(JSON_FIELD_PUBLIC_URI, spec.getPublicURI());

                    if (spec.getExternalAltURL() == null) {
                        generator.writeNullField(JSON_FIELD_EXTERNAL_ALT_URL);
                    }
                    else {
                        generator.writeStringField(JSON_FIELD_EXTERNAL_ALT_URL,
                                spec.getExternalAltURL());
                    }

                    if (spec.getWorkspaceAltURL() == null) {
                        generator.writeNullField(JSON_FIELD_WORKSPACE_ALT_URL);
                    }
                    else {
                        if (relativeBase == null) {
                            generator.writeStringField(JSON_FIELD_WORKSPACE_ALT_URL,
                                    spec.getWorkspaceAltURL());
                        }
                        else {
                            // make relative to project
                            String altUrl = spec.getWorkspaceAltURL();
                            IPath altUrlPath = new Path(altUrl);
                            if (altUrlPath.getDevice() != null
                                    && altUrlPath.getDevice().equals(FILE_DEVICE)) {
                                altUrlPath = new Path(StringUtils.stripStart(
                                        altUrl.substring(FILE_DEVICE.length()),
                                        Character.toString(IPath.SEPARATOR)));
                            }
                            altUrl = altUrlPath.makeRelativeTo(relativeBase).toString();
                            generator.writeStringField(JSON_FIELD_WORKSPACE_ALT_URL, altUrl);
                        }
                    }
                    if (spec.getPrefix() == null) {
                        generator.writeNullField(JSON_FIELD_PREFIX);
                    }
                    else {
                        generator.writeStringField(JSON_FIELD_PREFIX, spec.getPrefix());
                    }
                    generator.writeEndObject();
                }
                generator.writeEndArray();
                generator.writeEndObject();
            }
            catch (IOException ex) {
                logger.error("Error occured trying to encode the document manager config!", ex);
            }
            String json = writer.toString();
            return json;
        }

        /**
         * Returns a WorkspaceDocumentManagerConfiguration from the given JSON.
         * 
         * @param json
         * @return
         */
        public static WorkspaceDocumentManagerConfiguration getConfigFromJSON(String json,
                IPath relativeBase) {
            WorkspaceDocumentManagerConfiguration configuration = new WorkspaceDocumentManagerConfiguration();
            if (!Strings.isNullOrEmpty(json)) {
                JsonFactory factory = new JsonFactory();
                try (JsonParser parser = factory.createJsonParser(json)) {
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = parser.getCurrentName();
                        if (JSON_FIELD_PROCESS_IMPORTS.equals(fieldName)) {
                            parser.nextToken();
                            configuration.setProcessImports(parser.getBooleanValue());
                        }
                        else if (JSON_FIELD_ONTOLOGY_SPECS.equals(fieldName)) {
                            parser.nextToken();
                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                String publicUri = null;
                                String prefix = null;
                                String externalAltUrl = null;
                                String workspaceAltUrl = null;
                                String altUrl = null; // legacy!
                                boolean altUrlIsRelative = false; // legacy!
                                while (parser.nextToken() != JsonToken.END_OBJECT) {
                                    String specFieldName = parser.getCurrentName();
                                    if (JSON_FIELD_PUBLIC_URI.equals(specFieldName)) {
                                        parser.nextToken();
                                        publicUri = parser.getText();
                                    }
                                    else if (JSON_FIELD_PREFIX.equals(specFieldName)) {
                                        parser.nextToken();
                                        if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                            prefix = parser.getText();
                                        }
                                    }
                                    else if (JSON_FIELD_EXTERNAL_ALT_URL.equals(specFieldName)) {
                                        parser.nextToken();
                                        if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                            externalAltUrl = parser.getText();
                                        }
                                    }
                                    else if (JSON_FIELD_WORKSPACE_ALT_URL.equals(specFieldName)) {
                                        parser.nextToken();
                                        if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                            workspaceAltUrl = parser.getText();
                                            if (relativeBase != null) {
                                                // workspaceAltUrl is always
                                                // relative -> make absolute
                                                workspaceAltUrl = makeAbsolute(workspaceAltUrl,
                                                        relativeBase);
                                            }
                                        }
                                    }
                                    // FIXME: Legacy code. AltURL isn't used
                                    // anymore. Can be removed after release of
                                    // Castillo.
                                    else if (JSON_FIELD_ALT_URL.equals(specFieldName)) {
                                        parser.nextToken();
                                        if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                            altUrl = parser.getText();
                                        }
                                    }
                                    else if (JSON_FIELD_ALT_URL_IS_RELATIVE.equals(specFieldName)) {
                                        parser.nextToken();
                                        if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
                                            altUrlIsRelative = parser.getBooleanValue();
                                        }
                                    }
                                }
                                WorkspaceOntologySpec spec = new WorkspaceOntologySpec(publicUri);
                                spec.setPrefix(prefix);
                                if (altUrl != null) {
                                    // FIXME: Legacy code in this section.
                                    // AltURL isn't used anymore. Can be removed
                                    // after release of Castillo.
                                    if (altUrlIsRelative && relativeBase != null) {
                                        altUrl = makeAbsolute(altUrl, relativeBase);
                                    }
                                    spec.setAltURL(altUrl);
                                }
                                else {
                                    spec.setExternalAltURL(externalAltUrl);
                                    spec.setWorkspaceAltURL(workspaceAltUrl);
                                }
                                configuration.addOntologySpec(spec);
                            }
                        }
                    }
                }
                catch (JsonParseException ex) {
                    logger.error(
                            "Error occured trying to parse the document manager config settings!",
                            ex);
                }
                catch (IOException ex) {
                    logger.error(
                            "Error occured trying to parse the document manager config settings!",
                            ex);
                }
            }
            return configuration;
        }
    }

    protected static String makeAbsolute(String relativeUrl, IPath relativeBase) {
        IPath altUrlPath = (IPath) relativeBase.clone();
        altUrlPath = altUrlPath.append(relativeUrl).makeAbsolute();
        return FILE_DEVICE + IPath.SEPARATOR + IPath.SEPARATOR + IPath.SEPARATOR
                + altUrlPath.toString();
    }
}

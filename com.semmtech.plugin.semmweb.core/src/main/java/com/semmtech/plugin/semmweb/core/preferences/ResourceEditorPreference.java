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


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreference;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreferences;
import com.semmtech.semantics.vocabulary.SEMM;
import com.semmtech.semantics.vocabulary.SKOS;
import com.semmtech.ui.plugin.preference.BasePreference;


public class ResourceEditorPreference extends BasePreference {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ResourceEditorPreference.class);

    private static final String PREFERENCE_QUALIFIER = "resourceEditor.project";

    public static final String PREFERENCE_RESOURCE_EDITOR_PREFERENCES = "com.semmtech.plugin.semmweb.core.preferences.editor.resourceEditorPreferences";
    public static final String PREFERENCE_HIDE_RESTRICTION_STATEMENTS = "com.semmtech.plugin.semmweb.core.preferences.editor.hideRestrictionStatements";
    public static final String PREFERENCE_CHECK_DOMAIN_RESTRICTIONS = "com.semmtech.plugin.semmweb.core.preferences.editor.checkDomainRestrictions";

    public static final ResourceEditorClassPreferences DEFAULT_RESOURCE_EDITOR_PREFERENCES = createDefaults();

    protected ResourceEditorPreference(IPreferenceStore store) {
        super(store);
        if (getResourceEditorPreferences() == null) {
            setDefaults();
        }
    }

    /**
     * Returns the ResourceEditorPreference for the given project; or if the
     * project is null based on the CorePlugin preference store.
     * 
     * @param project
     * @return
     */
    public static ResourceEditorPreference fromProject(IProject project) {
        IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
        if (project != null) {
            ProjectScope scope = new ProjectScope(project);
            store = new ScopedPreferenceStore(scope, PREFERENCE_QUALIFIER);
        }
        return new ResourceEditorPreference(store);
    }

    public static ResourceEditorPreference fromPlugin() {
        return fromProject(null);
    }

    private static ResourceEditorClassPreferences createDefaults() {
        ResourceEditorClassPreferences preferences = new ResourceEditorClassPreferences();

        // TODO: Default is now not setting any preferences for default
        // properties -> causes bugs (see IP0005-90)
        preferences.addPropertySetting(RDFS.Resource, RDF.type,
                ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
        preferences.addPropertySetting(RDFS.Resource, RDFS.label,
                ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
        // preferences.addPropertySetting(OWL.Thing,
        // SEMM.hasCorrespondingConcept,
        // ResourceEditorClassPreference.SETTING_SHOW_ALWAYS);
        preferences.addPropertySetting(RDF.Property, RDFS.domain,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDF.Property, RDFS.range,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(OWL.ObjectProperty, OWL.inverseOf,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDFS.Class, RDFS.subClassOf,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(OWL.Class, OWL.disjointWith,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(OWL.Restriction, OWL.onProperty,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(OWL.Ontology, OWL.imports,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(OWL.Ontology, OWL.priorVersion,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(OWL.Ontology, OWL.backwardCompatibleWith,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDF.Statement, RDF.subject,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDF.Statement, RDF.predicate,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDF.Statement, RDF.subject,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDF.List, RDF.first,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(RDF.List, RDF.rest,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.prefLabel,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.altLabel,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.definition,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.scopeNote,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.related,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.narrower,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.addPropertySetting(SKOS.Concept, SKOS.broader,
                ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        // preferences.addPropertySetting(SKOS.Concept,
        // SEMM.isCorrespodingConceptFor,
        // ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES);
        preferences.setProposedAspectsSetting(SEMM.PhysicalObject,
                ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES);
        preferences.addQCRPropertySetting(SEMM.PhysicalObject, SEMM.isPartOf,
                ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES);

        return preferences;
    }

    public ResourceEditorClassPreferences getResourceEditorPreferences() {
        return getValueUsingJSON(PREFERENCE_RESOURCE_EDITOR_PREFERENCES,
                new TypeReference<ResourceEditorClassPreferences>() {
                }, null);
    }

    public void setResourceEditorPreferences(ResourceEditorClassPreferences preferences) {
        storeValueUsingJSON(preferences, PREFERENCE_RESOURCE_EDITOR_PREFERENCES);
    }

    public boolean hideRestrictionStatements() {
        return getPreferenceStore().getBoolean(PREFERENCE_HIDE_RESTRICTION_STATEMENTS);
    }

    public void setHideRestrictionStatements(boolean hide) {
        getPreferenceStore().setValue(PREFERENCE_HIDE_RESTRICTION_STATEMENTS, hide);
    }

    public boolean checkDomainRestrictions() {
        return getPreferenceStore().getBoolean(PREFERENCE_CHECK_DOMAIN_RESTRICTIONS);
    }

    public void setCheckDomainRestrictions(boolean check) {
        getPreferenceStore().setValue(PREFERENCE_CHECK_DOMAIN_RESTRICTIONS, check);
    }

    public void setDefaults() {
        setDefaultUsingJSON(PREFERENCE_RESOURCE_EDITOR_PREFERENCES,
                DEFAULT_RESOURCE_EDITOR_PREFERENCES);
        getPreferenceStore().setDefault(
                ResourceEditorPreference.PREFERENCE_HIDE_RESTRICTION_STATEMENTS, true);
        getPreferenceStore().setDefault(
                ResourceEditorPreference.PREFERENCE_CHECK_DOMAIN_RESTRICTIONS, true);
    }

}

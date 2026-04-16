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

package com.semmtech.plugin.semmweb.core;


import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;


/**
 * Constant class standardizing the location of various icons used throughout
 * the interface.
 * 
 * @author Mike Henrichs
 * 
 */
public class CorePluginImages {
    /**
     * Relative location of folder containing icons.
     */
    public static final String ICON_PATH = "icons/";
    public static final String FLAG_PATH = "icons/flags/";
    public static final String OVERLAYS_PATH = "icons/overlays/";
    public static final String IMAGE_PATH = "src/main/resources/images/";

    /**
     * Returns the correct path to the given icon filename. This allows for
     * different folders of icons to be switched.
     * 
     * @param filename
     *            Filename of the icon for which the path should be retrieved
     * @return Returns the path of the given icon filename, relative to the
     *         plug-in.
     */
    private static String icon(String filename) {
        return ICON_PATH + filename;
    }

    private static String overlays(String filename) {
        return OVERLAYS_PATH + filename;
    }

    private static String image(String filename) {
        return IMAGE_PATH + filename;
    }

    /**
     * Returns the correct path to the given flag icon filename.
     * 
     * @param filename
     *            Filename of the flag icon for which the path should be
     *            retrieved
     * @return Returns the path of the given flag icon filename, relative to the
     *         plug-in.
     */
    private static String flag(String filename) {
        return FLAG_PATH + filename;
    }

    public static final String IMG_SEMMTECH_ICON = icon("cloud-closed-shadow-16.png");

    public static final String IMG_ADD_PLUS = icon("Add.gif");

    public static final String IMG_ADD_CLASS = icon("class.png");
    public static final String IMG_CREATE_SUB_CLASS = icon("sub_class_add.png");
    public static final String IMG_CREATE_SIBLING_CLASS = icon("sibling_class_add.png");
    public static final String IMG_CREATE_INSTANCE = icon("individual_add.png");
    public static final String IMG_SHOW_INSTANCES = icon("show_instances.png");

    public static final String IMG_MODEL_FOLDER = icon("ontology_explorer_2.png");

    public static final String IMG_RDF_PROPERTY = icon("property.png");
    public static final String IMG_RDF_PROPERTY_ERROR = icon("property_error.png");
    public static final String IMG_RDF_RESOURCE = icon("web.png");
    public static final String IMG_RDFS_CLASS = icon("class_of_classes.png");
    public static final String IMG_RDF_LIST = icon("list.png");
    public static final String IMG_RDF_LIST_ADD = icon("list_add.png");
    public static final String IMG_RDF_NIL = icon("list_nil.png");
    public static final String IMG_OWL_ANNOTATION_PROPERTY = icon("property.png");
    public static final String IMG_OWL_DATATYPE_PROPERTY = icon("property.png");
    public static final String IMG_OWL_OBJECT_PROPERTY = icon("property.png");
    public static final String IMG_OWL_INDIVIDUAL = icon("individual.png");
    public static final String IMG_OWL_INDIVIDUAL_ADD = icon("individual_add.png");
    public static final String IMG_OWL_ONTOLOGY = icon("ontology.png");

    public static final String IMG_ADD_OWL_ONTOLOGY = icon("ontology_add.png");
    public static final String IMG_OWL_CLASS = icon("class.png");
    public static final String IMG_OWL_CLASS_ADD = icon("class_add.png");
    public static final String IMG_PROPERTY_RESTRICTION = icon("property_restriction.png");
    public static final String IMG_OWL_RESTRICTION = icon("class_restriction.png");
    public static final String IMG_OWL_RESTRICTIONS = icon("class_restrictions.png");
    public static final String IMG_OWL_INTERSECTION_OF = icon("class_intersection.png");
    public static final String IMG_OWL_UNION_OF = icon("class_union.png");
    public static final String IMG_OWL_COMPLEMENT_OF = icon("class_complement.png");
    public static final String IMG_OWL_ALL_VALUES_FROM = icon("class_all_values.png");
    public static final String IMG_OWL_HAS_VALUE = icon("class_has_value.png");
    public static final String IMG_OWL_SOME_VALUES_FROM = icon("class_some_values.png");
    public static final String IMG_OWL_CARDINALITY = icon("class_cardinality.png");
    public static final String IMG_OWL_MAX_CARDINALITY = icon("class_max_cardinality.png");
    public static final String IMG_OWL_MIN_MAX_CARDINALITY = icon("class_min_max_cardinality.png");
    public static final String IMG_OWL_MIN_CARDINALITY = icon("class_min_cardinality.png");
    public static final String IMG_OWL_EQUIVALENT_CLASS = icon("class_equivalent.png");

    public static final String IMG_SEMM_ROLE = icon("role.png");

    public static final String IMG_TRIPLES = icon("triple.png");
    public static final String IMG_RDF_STATEMENT = icon("triple.png");
    public static final String IMG_REASSERT_TRIPLE = icon("reassert_triple.png");
    public static final String IMG_REIFY_TRIPLE = icon("reify_triple.png");

    public static final String IMG_STATEMENT_ADD = icon("statement_add.png");
    public static final String IMG_STATEMENT_DELETE = icon("statement_delete.png");
    public static final String IMG_LITERAL_STATEMENT = icon("literal_statement.png");
    public static final String IMG_ADD_LITERAL_STATEMENT = icon("literal_statement_add.png");
    public static final String IMG_RESOURCE_STATEMENT = icon("resource_statement.png");
    public static final String IMG_ADD_RESOURCE_STATEMENT = icon("resource_statement_add.png");
    public static final String IMG_MULTIPLE_STATEMENTS_ADD = icon("multiple_statements_add.png");
    public static final String IMG_MULTIPLE_STATEMENTS_DELETE = icon("multiple_statements_delete.png");

    public static final String IMG_XSD = icon("xsd.png");
    public static final String IMG_XSD_ADD = icon("xsd_add.png");
    public static final String IMG_XSD_LITERAL = icon("xsd.png");
    public static final String IMG_XSD_STRING = icon("xsd_string.png");
    public static final String IMG_XSD_INTEGER = icon("xsd_int.png");
    public static final String IMG_XSD_DOUBLE = icon("xsd_double.png");
    public static final String IMG_XSD_FLOAT = icon("xsd_float.png");
    public static final String IMG_XSD_DATE = icon("xsd_date.png");
    public static final String IMG_XSD_BOOLEAN = icon("xsd_boolean.png");
    public static final String IMG_XSD_DATETIME = icon("xsd_datetime.png");
    public static final String IMG_XSD_ANYURI = icon("xsd_any_uri.png");
    // added empty overlay
    public static final String IMG_OVERLAY_EMPTY = overlays("empty_set_ovr.png");

    public static final String IMG_ADD = icon("add.png");
    public static final String IMG_COPY = icon("copy.png");
    public static final String IMG_PASTE = icon("paste.gif");
    public static final String IMG_CROSS = icon("cross.png");
    public static final String IMG_WARNING = icon("warning.gif");
    public static final String IMG_WARNING_BIG = icon("message_warning.gif");
    public static final String IMG_PENCIL = icon("pencil.png");
    public static final String IMG_RENAME = icon("rename.png");
    public static final String IMG_REFRESH = icon("refresh.gif");
    public static final String IMG_REFRESH_NAV_DIS = icon("refresh_nav_dis.gif");
    public static final String IMG_REFRESH_NAV_EN = icon("refresh_nav_en.gif");
    public static final String IMG_REMOVE = icon("remove_co.gif");
    public static final String IMG_REMOVE_ALL = icon("removeall.gif");
    public static final String IMG_FILTER = icon("filter.gif");
    public static final String IMG_DELETE = icon("delete.gif");
    public static final String IMG_DELETE_ALL = icon("removeall.gif");
    public static final String IMG_UNDO = icon("undo_edit.gif");
    public static final String IMG_DISK = icon("disk.png");
    public static final String IMG_FORM = icon("application_form.png");
    public static final String IMG_ARROW_UP = icon("arrow_up_9x10.png");
    public static final String IMG_ARROW_DOWN = icon("arrow_down_9x10.png");

    public static final String IMG_DROP_INTO = icon("staging.png");

    public static final String IMG_UNLOCKED_GREY = icon("unlocked-grey.png");
    public static final String IMG_UNLOCKED = icon("unlocked.png");
    public static final String IMG_LOCKED = icon("locked.png");

    public static final String IMG_DATETIME = icon("date_obj.gif");

    public static final String IMG_PROPERTY_INHERITED = icon("property_inherited.png");

    public static final String IMG_APPLICATION = icon("application.png");
    public static final String IMG_SECTION_INHERITED = icon("sections_inherited.png");
    public static final String IMG_SECTION_ALL_PROPERTIES = icon("section_all_properties.png");
    public static final String IMG_SECTION_RESTRICTIONS = icon("section_restrictions.png");
    public static final String IMG_SECTION_RESTRICTIONS_INHERITED = icon("section_restrictions_inherited.png");
    public static final String IMG_SECTION_QCR = icon("generic_qcr_widget.png");

    public static final String IMG_NAMESPACE = icon("link.png");
    public static final String IMG_GRAPH = icon("graph.png");

    public static final String IMG_OVERLAY_IMPORTED = icon("imported.png");
    public static final String IMG_OVERLAY_INHERITED = overlays("inherited_overlay.png");
    public static final String IMG_MODEL = icon("ontology.png");
    public static final String IMG_ONTOLOGY = icon("ontology.png");
    public static final String IMG_ONTOLOGY_ADD = icon("ontology_add.png");
    public static final String IMG_ONTOLOGY_WEB = icon("ontology_web.png");
    public static final String IMG_ONTOLOGY_WEB_DISABLED = icon("ontology_web_disabled.png");
    public static final String IMG_ONTOLOGY_FILE = icon("ontology_file.png");
    public static final String IMG_ONTOLOGY_FILE_DISABLED = icon("ontology_file_disabled.png");
    public static final String IMG_ONTOLOGY_WARNING = icon("ontology_warning.png");
    public static final String IMG_ONTOLOGY_ERROR = icon("ontology_error.png");
    public static final String IMG_ONTOLOGY_FILE_ADD = icon("ontology_file_add.png");
    public static final String IMG_ONTOLOGY_CACHE = icon("ontology_pinned.png");
    public static final String IMG_ONTOLOGY_CACHE_DISABLED = icon("ontology_pinned_disabled.png");

    public static final String IMG_MODELS_FOLDER_ADD = icon("add_model_folder.png");
    public static final String IMG_MODELS_FOLDER_REMOVE = icon("remove_model_folder.png");

    public static final String IMG_IMPORTS_FOLDER = icon("imports_folder_2.png");
    public static final String IMG_IMPORT_ONTOLOGY = icon("ontology_import.png");
    public static final String IMG_IMPORT_ONTOLOGY_WARNING = icon("ontology_import_warning.png");
    public static final String IMG_IMPORT_ONTOLOGY_ADD = icon("ontology_import_add.png");
    public static final String IMG_IMPORT_ONTOLOGY_DELETE = icon("ontology_import_delete.png");

    public static final String IMG_MODEL_WEB = icon("model_web.png");
    public static final String IMG_MODEL_LOCAL = icon("ontology_file.png");

    // TODO: Use overlay images!
    public static final String IMG_SEMANTIC_FILE = icon("ontology_file.png");
    public static final String IMG_SEMANTIC_FILE_SYNC = icon("ontology_file_sync.png");
    public static final String IMG_SEMANTIC_FILE_ERROR = icon("ontology_file_error.png");
    public static final String IMG_SEMANTIC_FILE_UNKNOWN = icon("ontology_file_unknown.png");
    public static final String IMG_SEMANTIC_FILE_WARNING = icon("ontology_file_warning.png");
    public static final String IMG_SEMANTIC_FILE_SUCCESS = icon("ontology_file_success.png");

    public static final String IMG_SEMM_PROJECT = icon("semmweb_project_white.png");
    public static final String IMG_SEMM_PROJECT_ADD = icon("semmweb_project_add.png");
    public static final String IMG_FOLDER = icon("folder.png");

    public static final String IMG_WEB = icon("web.png");
    public static final String IMG_WEB_UNKNOWN = icon("web_unknown.png");
    public static final String IMG_WEB_WARNING = icon("web_warning.png");
    public static final String IMG_WEB_SYNC = icon("web_sync.png");
    public static final String IMG_WEB_ERROR = icon("web_error.png");
    public static final String IMG_WEB_SUCCESS = icon("web_success.png");

    public static final String IMG_FLAG_NL = flag("nl.png");
    public static final String IMG_FLAG_US = flag("us.png");
    public static final String IMG_FLAG_DE = flag("de.png");
    public static final String IMG_FLAG_FR = flag("fr.png");
    public static final String IMG_FLAG_GB = flag("gb.png");
    public static final String IMG_FLAG_CN = flag("cn.png");
    public static final String IMG_FLAG_ES = flag("es.png");
    public static final String IMG_FLAG_EG = flag("eg.png");
    public static final String IMG_FLAG_PT = flag("pt.png");

    public static final String IMG_FLAT = icon("flat.gif");
    public static final String IMG_HIERARCHICAL = icon("hierarchical.gif");
    public static final String IMG_CHECK_ALL = icon("check_all.png");
    public static final String IMG_CHECK_NONE = icon("check_none.png");

    public static final String IMG_TAXONOMY = icon("taxonomy.png");
    public static final String IMG_SUPER_CLASS = icon("super_class.png");

    public static final String IMG_IMPORT = icon("import.gif");
    public static final String IMG_OVERLAY_ADD = icon("add_overlay.png");
    public static final String IMG_OVERLAY_EDIT = overlays("edited_ov.gif");
    public static final String IMG_OVERLAY_WARNING = overlays("warning_ovr.gif");
    public static final String IMG_OVERLAY_ERROR = overlays("error_ovr.gif");

    // public static final ImageDescriptor importedDescriptor =
    // AbstractUIPlugin.imageDescriptorFromPlugin(CorePlugin.PLUGIN_ID,
    // IMG_OVERLAY_IMPORTED);
    // public static final ImageDescriptor addDescriptor =
    // AbstractUIPlugin.imageDescriptorFromPlugin(CorePlugin.PLUGIN_ID,
    // IMG_OVERLAY_ADD);

    public static final String IMG_BANNER_WIZARD_SEMMTECH = icon("wizard_banners_semmtech.png");
    public static final String IMG_BANNER_WIZARD_PROPERTY = icon("wizard_banners_property.png");
    public static final String IMG_BANNER_WIZARD_RDFS_CLASS = icon("wizard_banners_rdfs_class.png");
    public static final String IMG_BANNER_WIZARD_OWL_CLASS = icon("wizard_banners_class.png");
    public static final String IMG_BANNER_WIZARD_INDIVIDUAL = icon("wizard_banners_individual.png");
    public static final String IMG_BANNER_WIZARD_RESTRICTION = icon("wizard_banners_restriction.png");
    public static final String IMG_BANNER_WIZARD_TRIPLE = icon("wizard_banners_triple.png");
    public static final String IMG_BANNER_WIZARD_REIFICATION = icon("wizard_banners_reification.png");
    public static final String IMG_BANNER_WIZARD_ONTOLOGY = icon("wizard_banners_ontology.png");
    public static final String IMG_BANNER_WIZARD_SEMANTIC_PROJECT = icon("wizard_semantic_project.png");

    public static final String IMG_QUALIFIED_CARDINALITY = icon("qualified_cardinality.png");
    public static final String IMG_QUALIFIED_CARDINALITY_ADD = icon("qualified_cardinality_add.png");

    public static final String IMG_CONSTRAINT_VALUE_ADD = icon("class_constraint_add.png");
    public static final String IMG_CONSTRAINT_CARDINALITY_ADD = icon("cardinality_constraint_add.png");

    public static final String IMG_CARDINALITY_ADD = icon("0_n_add.png");

    public static final String IMG_CARDINALITY_NONE = icon("0_0.png");
    public static final String IMG_CARDINALITY_OPTIONAL = icon("0_1.png");
    public static final String IMG_CARDINALITY_ONE = icon("1_1.png");
    public static final String IMG_CARDINALITY_UNBOUNDED = icon("0_n.png");
    public static final String IMG_CARDINALITY_SOME = icon("1_n.png");

    public static final String IMG_CARDINALITY_FROM_ZERO = icon("zero.png");
    public static final String IMG_CARDINALITY_TO_UNBOUNDED = icon("unbounded.png");

    public static final String IMG_POSSESSED_ASPECTS = icon("possessed_aspect_widget.png");
    public static final String IMG_POSSESSED_ASPECTS_ADD = icon("possessed_aspect_widget_add.png");

    public static final String IMG_SPINNER_UP = icon("spinner_up.png");
    public static final String IMG_SPINNER_DOWN = icon("spinner_down.png");

    public static final String IMG_SPARQL = icon("sparql.gif");

    public static final String IMG_RULE_IMPORT = icon("rule_import.png");
    public static final String IMG_RULE_FILE = icon("rule_file.png");

    public static final String IMG_GOOGLE_TRANSLATE = icon("google_translate.png");
    public static final String IMG_GOOGLE_LOGO = icon("google.png");
    public static final String IMG_WIKIPEDIA_SEARCH = icon("wikipedia.png");
    public static final String IMG_SINDICE_LOGO = icon("sindice.png");

    public static final String IMG_OVERLAY_REPOSITORY = overlays("overlay-repository.gif");
    public static final String IMG_OVERLAY_REPOSITORY_MODIFIED = overlays("overlay-repository-modified.png");

    public static final String IMG_SORT_ALPHABETICALLY = icon("alphab_sort_co.gif");
    public static final String IMG_SHOW_IN_EXPLORER = icon("output_folder_attrib.gif");

    public static final String IMG_FIND = icon("find.gif");
    public static final String IMG_FIND_CLEAR = icon("find-clear.gif");
    public static final String IMG_SAVE_IMAGE_AS = icon("save_image_as_obj.gif");

    public static final String IMG_DROPDOWN_MENU = icon("arrow_down_16x16.png");

    public static final String IMG_HOME_NAV = icon("home_nav.gif");
    public static final String IMG_CLOSE = icon("close.gif");

    public static final String IMG_DOCUMENT_MANAGER = icon("configure_document_manager.png");

    public static final String IMG_SEMMWEB_EDITOR_TITLE = image("semmweb-editor-title-new.png");
    public static final String IMG_SEMMTECH_LOGO = image("semmtech-logo.png");

    public static final String IMG_GROUPING = icon("grouping.gif");
    public static final String IMG_INSTANCES = icon("individuals.png");

    /**
     * Returns a list of all flag image filenames.
     * 
     * @return
     */
    public static List<String> getAllFlagKeys() {
        List<String> keys = Lists.newArrayList();
        Bundle bundle = Platform.getBundle("com.semmtech.plugin.semmweb.core");
        Enumeration<URL> urls = bundle.findEntries(FLAG_PATH, "*.png", true);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String key = url.getFile().substring(1);
            keys.add(key);
        }
        return keys;
    }
}

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

package com.semmtech.plugin.semmweb.editor;


import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.BundleContext;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.preferences.UserPreference;
import com.semmtech.plugin.semmweb.editor.perspectives.OntologyPerspectiveFactory;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * The activator class controls the plug-in life cycle
 */
public class EditorPlugin extends EclipseUIPlugin implements IPropertyChangeListener {
    public static final String PLUGIN_ID = "com.semmtech.plugin.semmweb.editor";

    private static EditorPlugin plugin;

    private final Logger logger = Logger.getLogger(EditorPlugin.class);

    public EditorPlugin() {
        super(PLUGIN_ID);
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        configureLoggers();
        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

        logger.debug("Editor plugin started");
    }

    /**
     * Initializes the properties for the various Loggers used throughoutr the
     * plug-in. TODO: use a custom Log formatter to output default Log4j
     * messages to Error View TODO: add a properties file, instead of hardcoding
     * the settings for the loggers in this method
     */
    private void configureLoggers() {
        Properties log4jProperties = new Properties();
        log4jProperties.put("log4j.rootLogger", "TRACE, CONSOLE");

        log4jProperties.put("log4j.logger.com.semmtech.plugin.semmweb.editor.views.PropertiesView",
                "WARN, CONSOLE");
        log4jProperties.put("log4j.logger.com.semmtech.plugin.semmweb.editor.views.TaxonomyView",
                "WARN, CONSOLE");
        log4jProperties.put("log4j.logger.com.semmtech.plugin.semmweb.editor.views.TriplesView",
                "WARN, CONSOLE");

        log4jProperties.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
        log4jProperties.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
        log4jProperties.put("log4j.appender.CONSOLE.layout.ConversionPattern",
                "%-4r [%t] %-5p %c %x - %m%n");

        PropertyConfigurator.configure(log4jProperties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        plugin = null;
        super.stop(context);
    }

    public static EditorPlugin getDefault() {
        return plugin;
    }

    public IEditorPart getActiveEditor() {
        IWorkbench workbench = getDefault().getWorkbench();
        return workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(UserPreference.PREFERENCE_KNOWLEDGE_LEVEL)) {
            // Shell shell =
            // getWorkbench().getActiveWorkbenchWindow().getShell();
            // if (MessageDialog
            // .openQuestion(shell, "Perspective",
            // "Do you wish to change the Ontology perspective to match your knowledge level."))
            // {

            // TODO: Mike (2015-05-19): Disabled this for now
            IPerspectiveDescriptor perspective = getWorkbench().getPerspectiveRegistry()
                    .findPerspectiveWithId(OntologyPerspectiveFactory.ID_PERSPECTIVE);
            getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(perspective);
            getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
            getWorkbench().getPerspectiveRegistry().setDefaultPerspective(
                    OntologyPerspectiveFactory.ID_PERSPECTIVE);
            // }
        }
    }
}

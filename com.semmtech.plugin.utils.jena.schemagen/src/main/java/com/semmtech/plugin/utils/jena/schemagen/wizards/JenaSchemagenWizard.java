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

package com.semmtech.plugin.utils.jena.schemagen.wizards;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.semmtech.jena.Schemagen;


public class JenaSchemagenWizard extends Wizard implements IExportWizard {

    private SchemagenConfigWizardPage configPage;

    public JenaSchemagenWizard() {
        super();
        setWindowTitle("Export an RDF/OWL model as a Java Vocabulary source file");
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        configPage = new SchemagenConfigWizardPage();

        addPage(configPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public boolean performFinish() {
        IRunnableWithProgress operation = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    monitor.beginTask("Calling schemagen", 2);
                    monitor.subTask("Generating Java");
                    String input = configPage.getInput().getLocation().toOSString();
                    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                    String rootPath = root.getLocation().toOSString();
                    String output = rootPath + configPage.getOutput().toOSString();
                    String classname = configPage.getClassName();
                    String packagename = configPage.getPackage();

                    Schemagen generator = new Schemagen();
                    generator.setInput(input);
                    generator.setOutput(output);
                    generator.setClassname(classname);
                    generator.setPackagename(packagename);

                    generator.generate();
                    monitor.worked(1);

                    monitor.subTask("Refreshing workspace");
                    root.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                    monitor.worked(1);

                }
                catch (CoreException e) {
                    e.printStackTrace();
                    throw new InvocationTargetException(e);
                }
                finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, operation);
        }
        catch (InterruptedException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

}

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

package com.semmtech.plugin.semmweb.core.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.semmtech.semantics.util.NamespaceUtil;


/**
 * This {@link IRunnableWithProgress} can be used by wizards to check if a URI
 * is used in any of the given models sub models (ie. imported models).
 * 
 * @author Mike Henrichs
 * 
 */
public final class CheckNamespaceUsageOperation implements IRunnableWithProgress {

    private final OntModel model;
    private final List<String> uris;
    private final List<ICheckNamespaceUsageListener> listeners;

    public CheckNamespaceUsageOperation(OntModel model, Collection<String> uris) {
        this.uris = Lists.newArrayList(uris);
        this.listeners = Lists.newArrayList();
        this.model = model;
    }

    public void addCheckNamespaceUsageListener(ICheckNamespaceUsageListener listener) {
        listeners.add(listener);
    }

    public void removeCheckNamespaceUsageListener(ICheckNamespaceUsageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException {
        boolean resume = true;
        try {
            int workload = uris.size();
            monitor.beginTask("Checking", workload);
            for (String uri : uris) {
                boolean error = false;
                monitor.subTask(String.format("Namespace <%s> usage... ", uri));

                for (ExtendedIterator<OntModel> iter = model.listSubModels(); iter.hasNext();) {
                    OntModel submodel = iter.next();
                    if (NamespaceUtil.usesNamespace(submodel, uri)) {
                        error = true;
                        break;
                    }
                }
                monitor.worked(1);
                if (error) {
                    for (ICheckNamespaceUsageListener listener : listeners) {
                        resume = listener.resumeOnError(uri);
                        if (!resume) {
                            break;
                        }
                    }
                    if (!resume) {
                        break;
                    }
                }
            }
        }
        finally {
            monitor.done();
        }
        if (listeners.size() == 0) {
            return;
        }
        if (resume) {
            for (ICheckNamespaceUsageListener listener : listeners) {
                listener.checkCompleted();
            }
        }
    }
}

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

package com.semmtech.plugin.semmweb.core.internal.navigator;


import org.eclipse.core.resources.IProject;

import com.google.common.base.Objects;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;
import com.semmtech.plugin.semmweb.core.navigator.ImportType;
import com.semmtech.plugin.semmweb.core.util.ImportURLUtils;


public class SemanticProject extends SemanticElement implements ISemanticProject {

    protected IProject project;

    public SemanticProject(SemanticElement parent, IProject project) {
        super(parent);
        this.project = project;
    }

    public ImportType getImportType(String publicUri) {
        ImportType type = null;
        for (ISemanticElement modelColl : getChildrenByType(ISemanticElement.MODEL_COLLECTION)) {
            for (ISemanticElement model : ((SemanticElement) modelColl)
                    .getChildrenByType(ISemanticElement.MODEL)) {
                for (ISemanticElement importColl : ((SemanticElement) model)
                        .getChildrenByType(ISemanticElement.IMPORT_COLLECTION)) {
                    for (ISemanticElement immport : ((SemanticElement) importColl)
                            .getChildrenByType(ISemanticElement.IMPORT)) {
                        IImport curr = (IImport) immport;

                        if (Objects.equal(curr.getURI(), publicUri)) {
                            type = curr.getImportType();
                            break;
                        }
                    }
                }
            }
        }

        if (type == null) {
            type = ImportURLUtils.guessImportType(publicUri, project);
        }
        return type;
    }

    @Override
    public int getElementType() {
        return ISemanticElement.SEMANTIC_PROJECT;
    }

    @Override
    public String getId() {
        return project.getFullPath().toString();
    }

    @Override
    public String getElementName() {
        return "Semantic Project";
    }

    @Override
    public IProject getProject() {
        return project;
    }
}

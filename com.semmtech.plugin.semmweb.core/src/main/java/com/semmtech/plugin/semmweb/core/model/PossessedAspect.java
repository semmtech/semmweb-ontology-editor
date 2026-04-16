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

package com.semmtech.plugin.semmweb.core.model;


import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Resource;


public class PossessedAspect {
    private Resource possessor;
    private Resource aspect;
    private Resource specializedRole;
    private Resource value;
    private Resource scale;
    private boolean optional = true;

    // private String proposedValue;
    // private String proposedScale;

    private Resource possessorRestriction;
    private Resource aspectRestriction;
    private List<Resource> valueRestrictions = Lists.newArrayList();

    public PossessedAspect(Resource possessor, Resource aspect) {
        this.possessor = possessor;
        this.aspect = aspect;
    }

    public Resource getPossessor() {
        return possessor;
    }

    public Resource getAspect() {
        return aspect;
    }

    public Resource getSpecializedRole() {
        return specializedRole;
    }

    public void setSpecializedRole(Resource specializedRole) {
        this.specializedRole = specializedRole;
    }

    public Resource getPossessorRestriction() {
        return possessorRestriction;
    }

    public void setPossessorRestriction(Resource possessorResource) {
        this.possessorRestriction = possessorResource;
    }

    public Resource getAspectRestriction() {
        return aspectRestriction;
    }

    public void setAspectRestriction(Resource aspectRestriction) {
        this.aspectRestriction = aspectRestriction;
    }

    public List<Resource> getValueRestrictions() {
        return valueRestrictions;
    }

    public void addValueRestriction(Resource valueRestriction) {
        valueRestrictions.add(valueRestriction);
    }

    public void addValueRestrictions(Collection<Resource> valueRestrictions) {
        this.valueRestrictions.addAll(valueRestrictions);
    }

    public Resource getValue() {
        return value;
    }

    public void setValue(Resource value) {
        this.value = value;
        // this.proposedValue = null;
    }

    // public String getProposedValue() {
    // return proposedValue;
    // }

    // public void setProposedValue(String proposedValue) {
    // this.proposedValue = proposedValue;
    // }

    public Resource getScale() {
        return scale;
    }

    public void setScale(Resource scale) {
        this.scale = scale;
        // this.proposedScale = null;
    }

    // public String getProposedScale() {
    // return proposedScale;
    // }

    // public void setProposedScale(String proposedScale) {
    // this.proposedScale = proposedScale;
    // }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}

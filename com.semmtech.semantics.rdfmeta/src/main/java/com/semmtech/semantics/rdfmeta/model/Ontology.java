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

package com.semmtech.semantics.rdfmeta.model;


import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;


/**
 * An Ontology. An implementation of a conceptual model.
 * 
 * @author Mike Henrichs
 * @since 1.0
 */
public interface Ontology extends Resource {

    /**
     * Gets the creators.
     * 
     * @return the creators
     */
    List<Agent> getCreators();

    /**
     * Adds the creator.
     * 
     * @param creator
     *            the creator
     */
    void addCreator(Agent creator);

    /**
     * Gets the contributors.
     * 
     * @return the contributors
     */
    List<Agent> getContributors();

    /**
     * Adds the contributor.
     * 
     * @param contributer
     *            the contributer
     */
    void addContributor(Agent contributer);

    /**
     * Gets the natural languaes.
     * 
     * @return the natural languaes
     */
    List<String> getNaturalLanguaes();

    /**
     * Adds the natural language.
     * 
     * @param language
     *            the language
     */
    void addNaturalLanguage(String language);

    /**
     * Gets the keywords.
     * 
     * @return the keywords
     */
    List<String> getKeywords();

    /**
     * Adds the keyword.
     * 
     * @param keyword
     *            the keyword
     */
    void addKeyword(String keyword);

    /**
     * Gets the key classes.
     * 
     * @return the key classes
     */
    List<String> getKeyClasses();

    /**
     * Adds the key class.
     * 
     * @param keyClass
     *            the key class
     */
    void addKeyClass(String keyClass);

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    void setName(String name);

    /**
     * Gets the acronym.
     * 
     * @return the acronym
     */
    String getAcronym();

    /**
     * Sets the acronym.
     * 
     * @param acronym
     *            the new acronym
     */
    void setAcronym(String acronym);

    /**
     * Gets the description.
     * 
     * @return the description
     */
    String getDescription();

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    void setDescription(String description);

    /**
     * Gets the creation date.
     * 
     * @return the creation date
     */
    Date getCreationDate();

    /**
     * Sets the creation date.
     * 
     * @param creationDate
     *            the new creation date
     */
    void setCreationDate(Date creationDate);

    /**
     * Gets the documentation.
     * 
     * @return the documentation
     */
    String getDocumentation();

    /**
     * Sets the documentation.
     * 
     * @param documentation
     *            the new documentation
     */
    void setDocumentation(String documentation);

    /**
     * Gets the reference.
     * 
     * @return the reference
     */
    String getReference();

    /**
     * Sets the reference.
     * 
     * @param reference
     *            the new reference
     */
    void setReference(String reference);

    /**
     * Gets the notes.
     * 
     * @return the notes
     */
    String getNotes();

    /**
     * Sets the notes.
     * 
     * @param notes
     *            the new notes
     */
    void setNotes(String notes);

    /**
     * Gets the status.
     * 
     * @return the status
     */
    String getStatus();

    /**
     * Sets the status.
     * 
     * @param status
     *            the new status
     */
    void setStatus(String status);

    /**
     * Gets the modification date.
     * 
     * @return the modification date
     */
    Date getModificationDate();

    /**
     * Sets the modification date.
     * 
     * @param modificationDate
     *            the new modification date
     */
    void setModificationDate(Date modificationDate);

    /**
     * Sets the version.
     * 
     * @param version
     *            the new version
     */
    void setVersion(String version);

    /**
     * Gets the version.
     * 
     * @return the version
     */
    String getVersion();

    /**
     * Sets the resource locator.
     * 
     * @param locator
     *            the new resource locator
     */
    void setResourceLocator(String locator);

    /**
     * Gets the resource locator.
     * 
     * @return the resource locator
     */
    String getResourceLocator();

    void setMetaURI(String uri);

    String getMetaURI();
}

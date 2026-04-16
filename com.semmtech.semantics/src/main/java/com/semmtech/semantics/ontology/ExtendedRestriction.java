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

package com.semmtech.semantics.ontology;


// Imports
///////////////
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.ProfileException;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.NotImplementedException;


/**
 * <p>
 * Implementation of the ontology abstraction representing restrictions.
 * </p>
 */
public class ExtendedRestriction extends ExtendedOntClass implements Restriction {
    // Constants
    // ////////////////////////////////

    // Static variables
    // ////////////////////////////////

    /**
     * A factory for generating Restriction facets from nodes in enhanced
     * graphs. Note: should not be invoked directly by user code: use
     * {@link com.hp.hpl.jena.rdf.model.RDFNode#as as()} instead.
     */
    public static Implementation factory = new Implementation() {
        @Override
        public EnhNode wrap(Node n, EnhGraph eg) {
            if (canWrap(n, eg)) {
                return new ExtendedRestriction(n, eg);
            }
            throw new ConversionException("Cannot convert node " + n + " to Restriction");
        }

        @Override
        public boolean canWrap(Node node, EnhGraph eg) {
            return (eg instanceof OntModel);
        }
    };

    // Instance variables
    // ////////////////////////////////

    // Constructors
    // ////////////////////////////////

    /**
     * <p>
     * Construct a restriction node represented by the given node in the given
     * graph.
     * </p>
     * 
     * @param n
     *            The node that represents the resource
     * @param g
     *            The enh graph that contains n
     */
    public ExtendedRestriction(Node n, EnhGraph g) {
        super(n, g);
    }

    // External signature methods
    // ////////////////////////////////

    // onProperty

    /**
     * <p>
     * Assert that the property that this restriction applies to is the given
     * property. Any existing statements for <code>onProperty</code> will be
     * removed.
     * </p>
     * 
     * @param prop
     *            The property that this restriction applies to
     * @exception OntProfileException
     *                If the {@link Profile#ON_PROPERTY()} property is not
     *                supported in the current language profile.
     */
    @Override
    public void setOnProperty(Property prop) {
        setPropertyValue(getProfile().ON_PROPERTY(), "ON_PROPERTY", prop);
    }

    /**
     * <p>
     * Answer the property that this property restriction applies to. If there
     * is more than one such resource, an arbitrary selection is made (though
     * well-defined property restrictions should not have more than one
     * <code>onProperty</code> statement.
     * </p>
     * 
     * @return The property that this property restriction applies to
     * @exception OntProfileException
     *                If the {@link Profile#ON_PROPERTY()} property is not
     *                supported in the current language profile.
     */
    @Override
    public OntProperty getOnProperty() {
        return objectAs(getProfile().ON_PROPERTY(), "ON_PROPERTY", OntProperty.class);
    }

    /**
     * <p>
     * Answer true if this restriction is a property restriction on the given
     * property.
     * </p>
     * 
     * @param prop
     *            A property to test against
     * @return True if this restriction is a restriction on <code>prop</code>
     * @exception OntProfileException
     *                If the {@link Profile#ON_PROPERTY()} property is not
     *                supported in the current language profile.
     */
    @Override
    public boolean onProperty(Property prop) {
        return hasPropertyValue(getProfile().ON_PROPERTY(), "ON_PROPERTY", prop);
    }

    /**
     * <p>
     * Remove the given property as the property that this restriction applies
     * to. If this statement is not true of the current model, nothing happens.
     * </p>
     * 
     * @param prop
     *            The property to be removed as a the property that this
     *            restriction applies to
     */
    @Override
    public void removeOnProperty(Property prop) {
        removePropertyValue(getProfile().ON_PROPERTY(), "ON_PROPERTY", prop);
    }

    /**
     * <p>
     * Answer a view of this restriction as an all values from expression
     * </p>
     * 
     * @return This class, but viewed as an AllValuesFromRestriction node
     * @exception ConversionException
     *                if the class cannot be converted to an all values from
     *                restriction given the lanuage profile and the current
     *                state of the underlying model.
     */
    @Override
    public AllValuesFromRestriction asAllValuesFromRestriction() {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // return as(AllValuesFromRestriction.class);
    }

    /**
     * <p>
     * Answer a view of this restriction as a some values from expression
     * </p>
     * 
     * @return This class, but viewed as a SomeValuesFromRestriction node
     * @exception ConversionException
     *                if the class cannot be converted to an all values from
     *                restriction given the lanuage profile and the current
     *                state of the underlying model.
     */
    @Override
    public SomeValuesFromRestriction asSomeValuesFromRestriction() {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // return as(SomeValuesFromRestriction.class);
    }

    /**
     * <p>
     * Answer a view of this restriction as a has value expression
     * </p>
     * 
     * @return This class, but viewed as a HasValueRestriction node
     * @exception ConversionException
     *                if the class cannot be converted to a has value
     *                restriction given the lanuage profile and the current
     *                state of the underlying model.
     */
    @Override
    public HasValueRestriction asHasValueRestriction() {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // return as(HasValueRestriction.class);
    }

    /**
     * <p>
     * Answer a view of this restriction as a cardinality restriction class
     * expression
     * </p>
     * 
     * @return This class, but viewed as a CardinalityRestriction node
     * @exception ConversionException
     *                if the class cannot be converted to a cardinality
     *                restriction given the lanuage profile and the current
     *                state of the underlying model.
     */
    @Override
    public CardinalityRestriction asCardinalityRestriction() {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // return as(CardinalityRestriction.class);
    }

    /**
     * <p>
     * Answer a view of this restriction as a min cardinality restriction class
     * expression
     * </p>
     * 
     * @return This class, but viewed as a MinCardinalityRestriction node
     * @exception ConversionException
     *                if the class cannot be converted to a min cardinality
     *                restriction given the lanuage profile and the current
     *                state of the underlying model.
     */
    @Override
    public MinCardinalityRestriction asMinCardinalityRestriction() {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // return as(MinCardinalityRestriction.class);
    }

    /**
     * <p>
     * Answer a view of this restriction as a max cardinality restriction class
     * expression
     * </p>
     * 
     * @return This class, but viewed as a MaxCardinalityRestriction node
     * @exception ConversionException
     *                if the class cannot be converted to a max cardinality
     *                restriction given the lanuage profile and the current
     *                state of the underlying model.
     */
    @Override
    public MaxCardinalityRestriction asMaxCardinalityRestriction() {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // return as(MaxCardinalityRestriction.class);
    }

    // type tests

    /**
     * <p>
     * Answer true if this restriction is an all values from restriction
     * </p>
     * 
     * @return True if this is an allValuesFrom property restriction
     * @exception ProfileException
     *                if {@link Profile#ALL_VALUES_FROM()} is not supported in
     *                the current profile
     */
    @Override
    public boolean isAllValuesFromRestriction() {
        checkProfile(getProfile().ALL_VALUES_FROM(), "ALL_VALUES_FROM");
        return hasProperty(getProfile().ALL_VALUES_FROM());
    }

    /**
     * <p>
     * Answer true if this restriction is a some values from restriction
     * </p>
     * 
     * @return True if this is a someValuesFrom property restriction
     * @exception ProfileException
     *                if {@link Profile#SOME_VALUES_FROM()} is not supported in
     *                the current profile
     */
    @Override
    public boolean isSomeValuesFromRestriction() {
        checkProfile(getProfile().SOME_VALUES_FROM(), "SOME_VALUES_FROM");
        return hasProperty(getProfile().SOME_VALUES_FROM());
    }

    /**
     * <p>
     * Answer true if this restriction is a has value restriction
     * </p>
     * 
     * @return True if this is a hasValue property restriction
     * @exception ProfileException
     *                if {@link Profile#HAS_VALUE()} is not supported in the
     *                current profile
     */
    @Override
    public boolean isHasValueRestriction() {
        checkProfile(getProfile().HAS_VALUE(), "HAS_VALUE");
        return hasProperty(getProfile().HAS_VALUE());
    }

    /**
     * <p>
     * Answer true if this restriction is a cardinality restriction (ie is a
     * property restriction constructed with an <code>owl:cardinality</code>
     * operator, or similar). This is not a test for a restriction that tests
     * cardinalities in general.
     * </p>
     * 
     * @return True if this is a cardinality property restriction
     * @exception ProfileException
     *                if {@link Profile#CARDINALITY()} is not supported in the
     *                current profile
     */
    @Override
    public boolean isCardinalityRestriction() {
        checkProfile(getProfile().CARDINALITY(), "CARDINALITY");
        return hasProperty(getProfile().CARDINALITY());
    }

    /**
     * <p>
     * Answer true if this restriction is a min cardinality restriction (ie is a
     * property restriction constructed with an <code>owl:minCardinality</code>
     * operator, or similar). This is not a test for a restriction that tests
     * cardinalities in general.
     * </p>
     * 
     * @return True if this is a minCardinality property restriction
     * @exception ProfileException
     *                if {@link Profile#MIN_CARDINALITY()} is not supported in
     *                the current profile
     */
    @Override
    public boolean isMinCardinalityRestriction() {
        checkProfile(getProfile().MIN_CARDINALITY(), "MIN_CARDINALITY");
        return hasProperty(getProfile().MIN_CARDINALITY());
    }

    /**
     * <p>
     * Answer true if this restriction is a max cardinality restriction (ie is a
     * property restriction constructed with an <code>owl:maxCardinality</code>
     * operator, or similar). This is not a test for a restriction that tests
     * cardinalities in general.
     * </p>
     * 
     * @return True if this is a maxCardinality property restriction
     * @exception ProfileException
     *                if {@link Profile#MAX_CARDINALITY()} is not supported in
     *                the current profile
     */
    @Override
    public boolean isMaxCardinalityRestriction() {
        checkProfile(getProfile().MAX_CARDINALITY(), "MAX_CARDINALITY");
        return hasProperty(getProfile().MAX_CARDINALITY());
    }

    // conversions

    /**
     * <p>
     * Convert this restriction to an all values from class expression.
     * </p>
     * 
     * @param cls
     *            The class to which all values of the restricted property must
     *            belong, to be in the extension of this restriction
     * @return This class, but converted to a AllValuesFromRestriction class
     *         expression
     * @exception ProfileException
     *                if {@link Profile#ALL_VALUES_FROM()} is not supported in
     *                the current profile
     */
    @Override
    public AllValuesFromRestriction convertToAllValuesFromRestriction(Resource cls) {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // setPropertyValue(getProfile().ALL_VALUES_FROM(), "ALL_VALUES_FROM",
        // cls);
        // return as(AllValuesFromRestriction.class);
    }

    /**
     * <p>
     * Convert this restriction to a some values from class expression
     * </p>
     * 
     * @param cls
     *            The class to which at least one value of the restricted
     *            property must belong, to be in the extension of this
     *            restriction
     * @return This class, but converted to a SomeValuesFromRestriction node
     * @exception ProfileException
     *                if {@link Profile#SOME_VALUES_FROM()} is not supported in
     *                the current profile
     */
    @Override
    public SomeValuesFromRestriction convertToSomeValuesFromRestriction(Resource cls) {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // setPropertyValue(getProfile().SOME_VALUES_FROM(), "SOME_VALUES_FROM",
        // cls);
        // return as(SomeValuesFromRestriction.class);
    }

    /**
     * <p>
     * Convert this restriction to a has value class expression
     * </p>
     * 
     * @param value
     *            The value which the restricted property must have, for
     *            resource to be in the extension of this restriction
     * @return This class, but converted to a HasValueRestriction
     * @exception ProfileException
     *                if {@link Profile#HAS_VALUE()} is not supported in the
     *                current profile
     */
    @Override
    public HasValueRestriction convertToHasValueRestriction(RDFNode value) {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // setPropertyValue(getProfile().HAS_VALUE(), "HAS_VALUE", value);
        // return as(HasValueRestriction.class);
    }

    /**
     * <p>
     * Convert this restriction to a cardinality restriction class expression
     * </p>
     * 
     * @param cardinality
     *            The exact cardinality for the restricted property
     * @return This class, but converted to a CardinalityRestriction node
     * @exception ProfileException
     *                if {@link Profile#CARDINALITY()} is not supported in the
     *                current profile
     */
    @Override
    public CardinalityRestriction convertToCardinalityRestriction(int cardinality) {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // setPropertyValue(getProfile().CARDINALITY(), "CARDINALITY",
        // getModel().createTypedLiteral(cardinality));
        // return as(CardinalityRestriction.class);
    }

    /**
     * <p>
     * Convert this restriction to a min cardinality restriction class
     * expression
     * </p>
     * 
     * @param cardinality
     *            The minimum cardinality for the restricted property
     * @return This class, but converted to a MinCardinalityRestriction node
     * @exception ProfileException
     *                if {@link Profile#MIN_CARDINALITY()} is not supported in
     *                the current profile
     */
    @Override
    public MinCardinalityRestriction convertToMinCardinalityRestriction(int cardinality) {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // setPropertyValue(getProfile().MIN_CARDINALITY(), "MIN_CARDINALITY",
        // getModel()
        // .createTypedLiteral(cardinality));
        // return as(MinCardinalityRestriction.class);
    }

    /**
     * <p>
     * Convert this restriction to a max cardinality restriction class
     * expression
     * </p>
     * 
     * @param cardinality
     *            The maximum cardinality for the restricted property
     * @return This class, but converted to a MaxCardinalityRestriction node
     * @exception ProfileException
     *                if {@link Profile#MAX_CARDINALITY()} is not supported in
     *                the current profile
     */
    @Override
    public MaxCardinalityRestriction convertToMaxCardinalityRestriction(int cardinality) {
        throw new NotImplementedException("No implementation of our own has been created yet.");
        // setPropertyValue(getProfile().MAX_CARDINALITY(), "MAX_CARDINALITY",
        // getModel()
        // .createTypedLiteral(cardinality));
        // return as(MaxCardinalityRestriction.class);
    }

    // Internal implementation methods
    // ////////////////////////////////

    // ==============================================================================
    // Inner class definitions
    // ==============================================================================

}

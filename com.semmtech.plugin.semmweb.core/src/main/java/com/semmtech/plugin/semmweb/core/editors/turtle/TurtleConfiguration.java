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

package com.semmtech.plugin.semmweb.core.editors.turtle;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


@SuppressWarnings("unused")
public class TurtleConfiguration extends SourceViewerConfiguration {
    // private XMLDoubleClickStrategy doubleClickStrategy;
    // private XMLTagScanner tagScanner;
    // private XMLScanner scanner;
    private ColorManager colorManager;

    public TurtleConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE };
    }

    // public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer
    // sourceViewer, String contentType) {
    // if (doubleClickStrategy == null)
    // doubleClickStrategy = new XMLDoubleClickStrategy();
    // return doubleClickStrategy;
    // }

    // protected XMLScanner getXMLScanner() {
    // if (scanner == null) {
    // scanner = new XMLScanner(colorManager);
    // scanner.setDefaultReturnToken(
    // new Token(
    // new TextAttribute(
    // colorManager.getColor(IXMLColorConstants.DEFAULT))));
    // }
    // return scanner;
    // }

    // protected XMLTagScanner getXMLTagScanner() {
    // if (tagScanner == null) {
    // tagScanner = new XMLTagScanner(colorManager);
    // tagScanner.setDefaultReturnToken(
    // new Token(
    // new TextAttribute(
    // colorManager.getColor(IXMLColorConstants.TAG))));
    // }
    // return tagScanner;
    // }

    // public IPresentationReconciler getPresentationReconciler(ISourceViewer
    // sourceViewer) {
    // PresentationReconciler reconciler = new PresentationReconciler();
    //
    // DefaultDamagerRepairer dr = new
    // DefaultDamagerRepairer(getXMLTagScanner());
    // reconciler.setDamager(dr, XMLPartitionScanner.XML_TAG);
    // reconciler.setRepairer(dr, XMLPartitionScanner.XML_TAG);
    //
    // dr = new DefaultDamagerRepairer(getXMLScanner());
    // reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
    // reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    //
    // NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer( new
    // TextAttribute(
    // colorManager.getColor(IXMLColorConstants.XML_COMMENT)));
    // reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
    // reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);
    //
    // return reconciler;
    // }

}
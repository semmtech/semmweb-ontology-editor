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

package com.semmtech.plugin.semmweb.sparql.editors;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Point;


public class SparqlViewerConfiguration extends SourceViewerConfiguration {
    private ColorManager manager;

    public SparqlViewerConfiguration(ColorManager manager) {
        this.manager = manager;
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
                SparqlPartitionScanner.SPARQL_PROLOGUE, SparqlPartitionScanner.SPARQL_QUERY };
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new IAnnotationHover() {

            @Override
            public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
                IDocument document = sourceViewer.getDocument();

                try {
                    IRegion info = document.getLineInformation(lineNumber);
                    return document.get(info.getOffset(), info.getLength());
                }
                catch (BadLocationException x) {
                }
                return null;
            }
        };
    }

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        System.out.println("getTextHover() contentType = " + contentType);
        return new ITextHover() {

            @Override
            public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
                Point selection = textViewer.getSelectedRange();
                if (selection.x <= offset && offset < selection.x + selection.y) {
                    return new Region(selection.x, selection.y);
                }
                return new Region(offset, 0);
            }

            @Override
            public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
                if (hoverRegion != null) {
                    try {
                        if (hoverRegion.getLength() > -1) {
                            return textViewer.getDocument().get(hoverRegion.getOffset(),
                                    hoverRegion.getLength());
                        }
                    }
                    catch (BadLocationException x) {
                    }
                }
                return "Empty!";

            }
        };
    }

    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();
        assistant.setContentAssistProcessor(new IContentAssistProcessor() {

            @Override
            public ICompletionProposal[] computeCompletionProposals(final ITextViewer viewer,
                    int offset) {

                ICompletionProposal[] proposals = new ICompletionProposal[2];

                proposals[0] = new CompletionProposal("PREFIX", offset, 0, 6);

                proposals[1] = new CompletionProposal("BASE", offset, 0, 4);

                return proposals;
            }

            @Override
            public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public char[] getCompletionProposalAutoActivationCharacters() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public char[] getContextInformationAutoActivationCharacters() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getErrorMessage() {
                return "Error Message!";
            }

            @Override
            public IContextInformationValidator getContextInformationValidator() {
                // TODO Auto-generated method stub
                return null;
            }

        }, SparqlPartitionScanner.SPARQL_PROLOGUE);

        return assistant;
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new SparqlCodeScanner(manager));
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(new SparqlCodeScanner(manager));
        reconciler.setDamager(dr, SparqlPartitionScanner.SPARQL_PROLOGUE);
        reconciler.setRepairer(dr, SparqlPartitionScanner.SPARQL_PROLOGUE);

        dr = new DefaultDamagerRepairer(new SparqlCodeScanner(manager));
        reconciler.setDamager(dr, SparqlPartitionScanner.SPARQL_QUERY);
        reconciler.setRepairer(dr, SparqlPartitionScanner.SPARQL_QUERY);

        return reconciler;
    }

}

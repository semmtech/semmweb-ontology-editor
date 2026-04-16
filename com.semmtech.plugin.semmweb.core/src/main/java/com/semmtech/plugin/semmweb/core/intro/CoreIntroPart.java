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

package com.semmtech.plugin.semmweb.core.intro;


import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.ViewIntroAdapterPart;
import org.eclipse.ui.internal.intro.IIntroConstants;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroContentProvider;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;
import org.eclipse.ui.part.IntroPart;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


/**
 * See http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.platform.doc.
 * isv%2Fguide%2Fua_intro_define_content.htm&cp=2_0_19_0_1_1
 * 
 * @author Sander Stolk
 * 
 */
@SuppressWarnings("restriction")
public class CoreIntroPart extends IntroPart implements IIntroContentProviderSite, IPartListener {
    private static final String CLASS_PROPERTY = "class";

    private List<IIntroContentProvider> introContentProviders;
    private ScrolledForm outerForm;
    private WelcomeContentProvider welcomeProvider;

    @Override
    public void createPartControl(Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());

        outerForm = toolkit.createScrolledForm(parent);
        Composite outerComposite = outerForm.getBody();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(outerComposite);
        GridLayoutFactory.fillDefaults().margins(5, 10).applyTo(outerComposite);

        // TODO: This should be a separate provider, due to the fact that the
        // branding plugin will provide the actual logo of the application
        Composite imageComposite = toolkit.createComposite(outerComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 10, 0, 0).spacing(35, 0)
                .applyTo(imageComposite);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(imageComposite);

        Label label = toolkit.createLabel(imageComposite, null);
        label.setImage(CorePlugin.getDefault()
                .getImageDescriptor(CorePluginImages.IMG_SEMMWEB_EDITOR_TITLE).createImage());
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.BEGINNING)
                .applyTo(label);

        label = toolkit.createLabel(imageComposite, null);
        label.setImage(CorePlugin.getDefault()
                .getImageDescriptor(CorePluginImages.IMG_SEMMTECH_LOGO).createImage());
        GridDataFactory.swtDefaults().hint(86, 75).align(SWT.BEGINNING, SWT.BEGINNING)
                .applyTo(label);

        // Label label = toolkit.createLabel(outerComposite, null);
        // label.setImage(CorePlugin.getDefault()
        // .getImageDescriptor(CorePluginImages.IMG_SEMMWEB_EDITOR_TITLE).createImage());
        // GridDataFactory.fillDefaults().applyTo(label);

        // Control welcomeControl = createWelcome(outerComposite, toolkit);
        // GridDataFactory.fillDefaults().applyTo(welcomeControl);

        Composite contentComposite = toolkit.createComposite(outerComposite);
        GridDataFactory.fillDefaults().align(GridData.FILL, GridData.CENTER).grab(true, false)
                .applyTo(contentComposite);

        ColumnLayout layout = new ColumnLayout();
        layout.minNumColumns = 1;
        layout.maxNumColumns = 2;
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 10;
        layout.leftMargin = 5;
        layout.rightMargin = 5;
        layout.topMargin = 5;
        layout.bottomMargin = 5;
        contentComposite.setLayout(layout);

        welcomeProvider.createContent("welcome", contentComposite, toolkit);

        for (IIntroContentProvider provider : introContentProviders) {
            provider.createContent("", contentComposite, toolkit);
        }

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(this);
    }

    @Override
    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        super.setInitializationData(cfig, propertyName, data);
        setTitle("Start Page");
        setTitleImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_HOME_NAV));

        introContentProviders = Lists.newArrayList();
        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(
                        "com.semmtech.plugin.semmweb.core.startPageContentProviders");
        for (IConfigurationElement element : configurationElements) {
            try {
                Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IIntroContentProvider) {
                    introContentProviders.add((IIntroContentProvider) object);
                }
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(IIntroSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        welcomeProvider = new WelcomeContentProvider();
        welcomeProvider.init(this);
        for (IIntroContentProvider provider : introContentProviders) {
            provider.init(this);
        }
    }

    @Override
    public void reflow(IIntroContentProvider provider, boolean incremental) {
        outerForm.reflow(true);
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
        /*
         * Normally, a good way to check whether it is this part that has been
         * deactivated would be to use instanceof. Here, though, this part is
         * not an instanceof CoreIntroPart, IntroPart, or IIntroPart. Instead,
         * it is an instanceof ViewIntroAdapterPart, which is an internal class
         * we should not access. As such, the title of the part is checked
         * instead.
         */
        if (part.getTitle().equals(getTitle())) {
            PlatformUI.getWorkbench().getIntroManager().closeIntro(this);
        }
    }

    @Override
    public void standbyStateChanged(boolean standby) {
        if (standby) {
            // Ensure this window can't be put in standby mode.
            maximizeIntroPart();
        }
    }

    public static void maximizeIntroPart() {
        // By first maximizing without an async runnable, the part is maximized
        // quickly. However, it won't maximize completely, still showing the
        // toolbar. By then maximizing using the default display, the part will
        // maximize fully.
        maximizePart();
        maximizePartByAsyncDisplayExec();
    }

    private static void maximizePartByAsyncDisplayExec() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                maximizePart();
            }
        });
    }

    private static void maximizePart() {
        try {
            for (final IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
                boolean standby = false;
                IViewPart part = window.getActivePage().findView(IIntroConstants.INTRO_VIEW_ID);
                if (part instanceof ViewIntroAdapterPart) {
                    ViewIntroAdapterPart introPart = (ViewIntroAdapterPart) part;
                    // TODO: This causes error in the Kepler version of Eclipse,
                    // since these methods are no longer available (and
                    // internal!).
                    PartPane pane = ((PartSite) introPart.getSite()).getPane();
                    if (standby == !pane.isZoomed()) {
                        // the zoom state is already correct.
                        // just update the part's state.
                        introPart.setStandby(standby);
                    }
                    else {
                        introPart.getSite().getPage().toggleZoom(pane.getPartReference());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
        if (part.getTitle().equals(getTitle())) {
            maximizeIntroPart();
        }
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
    }

    @Override
    public void setFocus() {
    }
}

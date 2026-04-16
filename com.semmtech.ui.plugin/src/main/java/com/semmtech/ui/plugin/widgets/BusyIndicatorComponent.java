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

package com.semmtech.ui.plugin.widgets;


import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

import com.semmtech.ui.plugin.BasePlugin;
import com.semmtech.ui.plugin.BasePluginImages;


/**
 * This component show a loading icon (animated gif) with a text on the right.
 * After the component creation the {@link #startAnimation()} method have to be
 * called to start the updating of the loading icon.
 * <p>
 * The loading thread will be automatically killed when the {@link #dispose()}
 * method is called.
 * <p>
 * <b>NB:</b>Remember to call layout(true,true) on the container to make this
 * component visible.
 * 
 * @author Simone Rondelli
 */
public class BusyIndicatorComponent extends Composite {

    private ImageLoader imgLoader;
    private BusyIndicatorUpdater gifUpdater;
    private long delay;
    private Shell shell;

    private AtomicBoolean showRefresh;
    private Object lock = new Object();

    public BusyIndicatorComponent(Composite parent, String text) {
        super(parent, SWT.NONE);
        RowLayoutFactory.swtDefaults().type(SWT.HORIZONTAL).spacing(7).applyTo(this);

        Label imgLabel = new Label(this, SWT.NONE);
        RowDataFactory.swtDefaults().hint(16, 16).applyTo(imgLabel);

        Label textLabel = new Label(this, SWT.NONE);
        textLabel.setText(text);
        RowDataFactory.swtDefaults().applyTo(textLabel);

        shell = parent.getShell();
        gifUpdater = new BusyIndicatorUpdater(shell, getImageLoader(), new GC(imgLabel));
        gifUpdater.setDaemon(true);

        delay = 500;
        showRefresh = new AtomicBoolean(true);
        setVisible(false);
    }

    /**
     * Sets the delay in millisecods before the indicator is shown. If, after
     * that the delay is passed, the component is still not disposed then the
     * indicator is shown.
     * <p>
     * This method have to be called before {@link #startAnimation()}
     * 
     * @param delay
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void startAnimation() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (lock) {
                    if (showRefresh.get()) {
                        setVisible();
                        gifUpdater.start();
                    }
                }
            }

            private final void setVisible() {
                shell.getDisplay().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        if (showRefresh.get()) {
                            BusyIndicatorComponent.this.setVisible(true);
                        }
                    }
                });
            }
        }, delay);
    }

    @Override
    public void dispose() {
        synchronized (lock) {
            showRefresh.set(false);
            gifUpdater.kill();
            super.dispose();
        }
    }

    public ImageLoader getImageLoader() {
        if (imgLoader == null) {
            imgLoader = new ImageLoader();
            try {
                Bundle bundle = Platform.getBundle(BasePlugin.PLUGIN_ID);
                Enumeration<URL> urls = bundle.getResources(BasePluginImages.IMG_REFRESHING);
                while (urls.hasMoreElements()) {
                    imgLoader.load(urls.nextElement().openStream());
                    break;
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return imgLoader;
    }

}

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

package com.semmtech.ui.plugin;


import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.semmtech.ui.plugin.decorators.IImageDataProvider;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


public abstract class EclipseUIPlugin extends AbstractUIPlugin implements IImageDataProvider {
    private static final Logger logger = Logger.getLogger(EclipseUIPlugin.class);

    private final String pluginId;

    /** The plug-in install URL */
    private URL installURL;

    /** The plug-in install path */
    private String installPath;

    public EclipseUIPlugin(String pluginId) {
        super();
        this.pluginId = pluginId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        // Get the install path of this plug-in.
        installURL = getBundle().getEntry("/"); //$NON-NLS-1$

        try {
            installPath = FileLocator.resolve(installURL).getPath();
        }
        catch (IOException e) {
            installPath = Platform.getInstallLocation().getURL().getPath();
        }
    }

    public final String getPluginID() {
        return pluginId;
    }

    public String getInstallPath() {
        return installPath;
    }

    public URL getInstallURL() {
        return installURL;
    }

    /**
     * Retrieves the specified image from the plug-in's image registry. NOTE: If
     * the image is not present in image registry; the image is created and
     * added as well
     * 
     * @param symbolicName
     * @return
     */
    public Image getImage(String symbolicName) {
        return getImage(symbolicName, true);
    }

    /**
     * Retrieves the specified image from the plug-in's image registry.
     * 
     * @param symbolicName
     * @param createIfAbsent
     * @return
     */
    public Image getImage(String symbolicName, boolean createIfAbsent) {
        ImageRegistry registry = getImageRegistry();
        if (registry.get(symbolicName) == null && createIfAbsent) {
            try {
                registry.put(symbolicName, imageDescriptorFromPlugin(getPluginID(), symbolicName)
                        .createImage());
            }
            catch (IllegalArgumentException ex) {
                logger.error(String.format(
                        "IllegalArgumentException was thrown with symbolicName = \"%s\": %s",
                        symbolicName, ex.getMessage()));
                ex.printStackTrace();
            }
        }
        return registry.get(symbolicName);
    }

    /**
     * Retrieves the specified image descriptor from the plug-in's image
     * registry. NOTE: If the image is not present in image registry; the image
     * is created and added as well
     * 
     * @param symbolicName
     * @return
     */
    public ImageDescriptor getImageDescriptor(String symbolicName) {
        return getImageDescriptor(symbolicName, true);
    }

    public Image getDecoratedImage(String imageKey, String overlayKey, int location,
            boolean createIfAbsent) {
        @SuppressWarnings("unused")
        ImageRegistry registry = getImageRegistry();

        Image baseImage = getImage(imageKey);
        OverlayImageIcon icon = new OverlayImageIcon(baseImage, this);
        icon.addImageDecoration(overlayKey, location);
        Image decoratedImage = icon.createImage();

        // TODO: Should this be stored in the image registry; or is this
        // redundant (and even double) since
        // both the image and overlay are retrieved from the registry
        // themselves.
        return decoratedImage;
    }

    public Image getDecoratedImage(String imageKey, String overlayKey, int location) {
        return getDecoratedImage(imageKey, overlayKey, location, true);
    }

    /**
     * Retrieves the specified image descriptor from the plug-in's image
     * registry.
     * 
     * @param symbolicName
     * @param createIfAbsent
     * @return
     */
    public ImageDescriptor getImageDescriptor(String symbolicName, boolean createIfAbsent) {
        if (symbolicName == null) {
            return null;
        }
        ImageRegistry registry = getImageRegistry();
        if (registry.get(symbolicName) == null && createIfAbsent) {
            try {
                registry.put(symbolicName, imageDescriptorFromPlugin(getPluginID(), symbolicName));
            }
            catch (IllegalArgumentException ex) {
                logger.error(String.format(
                        "IllegalArgumentException was thrown with symbolicName = \"%s\": %s",
                        symbolicName, ex.getMessage()));
                ex.printStackTrace();
            }
        }
        return registry.getDescriptor(symbolicName);
    }

    @Override
    public ImageData getImageData(String symbolicName) {
        return getImageDescriptor(symbolicName).getImageData();
    }

    /**
     * Get the current Display if possible, or else the default Display.
     * 
     * @return the current or default Display
     */
    public static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }
}

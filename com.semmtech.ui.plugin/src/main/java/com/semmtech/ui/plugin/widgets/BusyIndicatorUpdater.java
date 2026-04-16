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


/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/*
 * Image example snippet: display an animated GIF
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * The aim of this thread is to draw the gif contained in the ImageLoader inside
 * the given container. The gif will be drown in the 0,0 position. Typically a
 * Label is teh best component to use as container:
 * 
 * <pre>
 * Label gifLabel = new Label(parent, SWT.NONE);
 * GC gc = new GC(gifLabel);
 * </pre>
 * 
 * Once the update is not needed anymore the {@link #kill()} method have to be
 * called. In this way the thread will finish his execution disposing all the
 * resources.
 * <p>
 * At the moment the scope of this class is limited to this package .
 * 
 * @author Simone Rondelli
 */
class BusyIndicatorUpdater extends Thread {

    private static Logger logger = Logger.getLogger(BusyIndicatorUpdater.class);

    private Shell shell;
    private ImageLoader loader;
    private boolean alive;
    private GC container;

    BusyIndicatorUpdater(Shell shell, ImageLoader loader, GC container) {
        this.loader = loader;
        this.container = container;
        this.alive = true;
        this.shell = shell;
    }

    @Override
    public void run() {
        ImageData[] imageDataArray = loader.data;

        if (imageDataArray.length <= 1) {
            return;
        }

        Color shellBackground = container.getBackground();
        Display display = shell.getDisplay();
        Image image = null;

        /*
         * Create an off-screen image to draw on, and fill it with the shell
         * background.
         */
        Image offScreenImage = new Image(display, loader.logicalScreenWidth,
                loader.logicalScreenHeight);
        GC offScreenImageGC = new GC(offScreenImage);
        offScreenImageGC.setBackground(shellBackground);
        offScreenImageGC.fillRectangle(0, 0, loader.logicalScreenWidth, loader.logicalScreenHeight);

        try {
            /*
             * Create the first image and draw it on the off-screen image.
             */
            int imageDataIndex = 0;
            ImageData imageData = imageDataArray[imageDataIndex];

            image = new Image(display, imageData);
            offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height, imageData.x,
                    imageData.y, imageData.width, imageData.height);

            /*
             * Now loop through the images, creating and drawing each one on the
             * off-screen image before drawing it on the shell.
             */
            int repeatCount = loader.repeatCount;
            while (alive && (loader.repeatCount == 0 || repeatCount > 0)) {
                switch (imageData.disposalMethod) {
                case SWT.DM_FILL_BACKGROUND:
                    /*
                     * Fill with the background color before drawing.
                     */
                    offScreenImageGC.setBackground(shellBackground);
                    offScreenImageGC.fillRectangle(imageData.x, imageData.y, imageData.width,
                            imageData.height);
                    break;
                case SWT.DM_FILL_PREVIOUS:
                    /*
                     * Restore the previous image before drawing.
                     */
                    offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height,
                            imageData.x, imageData.y, imageData.width, imageData.height);
                    break;
                }

                imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
                imageData = imageDataArray[imageDataIndex];
                image.dispose();
                image = new Image(display, imageData);
                offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height,
                        imageData.x, imageData.y, imageData.width, imageData.height);

                /* Draw the off-screen image to the shell. */
                container.drawImage(offScreenImage, 0, 0);

                /*
                 * Sleep for the specified delay time (adding commonly-used
                 * slow-down fudge factors).
                 */
                try {
                    int ms = imageData.delayTime * 10;
                    if (ms < 20)
                        ms += 30;
                    if (ms < 30)
                        ms += 10;
                    Thread.sleep(ms);
                }
                catch (InterruptedException e) {
                    logger.error("BusyIndicatorUpdater unexpectedly interrupted!", e);
                }

                /*
                 * If we have just drawn the last image, decrement the repeat
                 * count and start again.
                 */
                if (imageDataIndex == imageDataArray.length - 1) {
                    repeatCount--;
                }
            }
        }
        catch (SWTException ex) {
            logger.error("Error while refreshing Busy Icon", ex);
        }
        finally {
            if (!offScreenImage.isDisposed()) {
                offScreenImage.dispose();
            }
            if (!offScreenImageGC.isDisposed()) {
                offScreenImageGC.dispose();
            }
            if (image != null && !image.isDisposed()) {
                image.dispose();
            }
        }
    }

    public void kill() {
        alive = false;
    }

}

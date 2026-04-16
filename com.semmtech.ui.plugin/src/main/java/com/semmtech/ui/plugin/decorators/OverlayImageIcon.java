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

package com.semmtech.ui.plugin.decorators;


import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;


public class OverlayImageIcon extends CompositeImageDescriptor {

    /**
     * Base image of the object
     */
    private Image baseImage;

    /**
     * Size of the base image
     */
    private Point sizeOfImage;

    /**
     * Vector of image keys
     */
    private Map<String, Integer> decorations;
    private IImageDataProvider provider;

    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    public static final int BOTTOM_LEFT = 2;
    public static final int BOTTOM_RIGHT = 4;

    public OverlayImageIcon(Image baseImage, IImageDataProvider provider) {
        this(baseImage, provider, new LinkedHashMap<String, Integer>());
    }

    public OverlayImageIcon(Image baseImage, IImageDataProvider provider,
            Map<String, Integer> decorations) {
        this.baseImage = baseImage;
        this.provider = provider;
        this.decorations = decorations;
        this.sizeOfImage = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
    }

    public void addImageDecoration(String key, int location) {
        decorations.put(key, new Integer(location));
    }

    @Override
    protected void drawCompositeImage(int width, int height) {
        // Draw the base image
        drawImage(baseImage.getImageData(), 0, 0);
        for (String imageKey : decorations.keySet()) {
            int location = decorations.get(imageKey).intValue();
            ImageData imageData = provider.getImageData(imageKey);
            switch (location) {
            // Draw on the top left corner
            case TOP_LEFT:
                drawImage(imageData, 0, 0);
                break;
            // Draw on top right corner
            case TOP_RIGHT:
                drawImage(imageData, sizeOfImage.x - imageData.width, 0);
                break;
            // Draw on bottom left
            case BOTTOM_LEFT:
                drawImage(imageData, 0, sizeOfImage.y - imageData.height);
                break;
            // Draw on bottom right corner
            case BOTTOM_RIGHT:
                drawImage(imageData, sizeOfImage.x - imageData.width, sizeOfImage.y
                        - imageData.height);
                break;
            }
        }

    }

    @Override
    protected Point getSize() {
        return sizeOfImage;
    }

    public Image getImage() {
        return createImage();
    }
}

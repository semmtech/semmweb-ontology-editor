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

package com.semmtech.plugin.semmweb.core.internal.navigator;


import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;


public class AdaptabilityUtility {
    /**
     * <p>
     * Returns an adapter of the requested type (anAdapterType)
     * 
     * @param anElement
     *            The element to adapt, which may or may not implement
     *            {@link IAdaptable}, or null
     * @param anAdapterType
     *            The class type to return
     * @return An adapter of the requested type or null
     */
    public static Object getAdapter(Object anElement, Class<?> anAdapterType) {
        Assert.isNotNull(anAdapterType);
        if (anElement == null) {
            return null;
        }
        if (anAdapterType.isInstance(anElement)) {
            return anElement;
        }

        if (anElement instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) anElement;

            Object result = adaptable.getAdapter(anAdapterType);
            if (result != null) {
                // Sanity-check
                Assert.isTrue(anAdapterType.isInstance(result));
                return result;
            }
        }

        if (!(anElement instanceof PlatformObject)) {
            Object result = Platform.getAdapterManager().getAdapter(anElement, anAdapterType);
            if (result != null) {
                return result;
            }
        }

        return null;
    }
}

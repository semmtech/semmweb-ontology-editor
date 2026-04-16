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

package com.semmtech.ui.plugin.util;


import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionFilter;

import com.google.common.collect.Lists;


public final class Selections {
    private Selections() {

    }

    /**
     * Tries to cast the selection to a <code>IStructuredSelection</code> and if
     * this succeeds returns the result; otherwise <code>null</code> is
     * returned.
     * 
     */
    public static IStructuredSelection toStructured(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            return (IStructuredSelection) selection;
        }
        return null;
    }

    /**
     * Tries to cast the selection to a <code>IStructuredSelection</code> and if
     * this succeeds and the structured selection is non-empty checks if the
     * first element in the selection is an instances of the specified class; if
     * not <code>false</code> is returned.
     * 
     */
    public static <T> boolean hasFirstOfType(ISelection selection, Class<T> type) {
        Object first = retrieveFirst(selection);
        return type.isInstance(first);
    }

    /**
     * Tries to cast the selection to a <code>IStructuredSelection</code> and if
     * this succeeds and the structured selection is non-empty checks if all
     * elements within the selection are instances of the specified class; if
     * not <code>false</code> is returned.
     * 
     */
    public static <T> boolean hasAllOfType(ISelection selection, Class<T> type) {
        IStructuredSelection structured = toStructured(selection);
        if (structured == null) {
            return false;
        }
        if (structured.size() == 0) {
            return false;
        }
        for (Iterator<?> iter = structured.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (!type.isInstance(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries to cast the selection to a <code>IStructuredSelection</code> and if
     * this succeeds and the structured selection is non-empty checks if all
     * elements within the selection are instances of the specified classes; if
     * not <code>false</code> is returned.
     * 
     */
    public static boolean hasAllOfTypes(ISelection selection, Class<?>... types) {
        IStructuredSelection structured = toStructured(selection);
        if (structured == null) {
            return false;
        }
        if (structured.size() == 0) {
            return false;
        }
        for (Iterator<?> iter = structured.iterator(); iter.hasNext();) {
            Object element = iter.next();
            boolean rightType = false;
            for (Class<?> type : types) {
                if (type.isInstance(element)) {
                    rightType = true;
                }
            }

            if (!rightType) {
                return false;
            }

        }
        return true;
    }

    /**
     * Tries to cast the selection to a <code>IStructuredSelection</code> and if
     * this succeeds and the structured selection is non-empty tries to cast the
     * first object to the given type; in all other cases null is returned.
     * 
     * @param selection
     * @param type
     * @return
     */
    public static <T> T retrieveFirstAsType(ISelection selection, Class<T> type) {
        Object first = retrieveFirst(selection);
        if (type.isInstance(first)) {
            return type.cast(first);
        }
        return null;
    }

    /**
     * Attempts to returns all items within the structured selection as a
     * collection. Only the items with the correct type are cast and included in
     * the collection. If the selection is not a structured selection an empty
     * collection is returned.
     * 
     * @param selection
     * @param type
     * @return
     */
    public static <T> List<T> retrieveAllAsType(ISelection selection, Class<T> type) {
        List<T> result = Lists.newArrayList();
        IStructuredSelection structured = toStructured(selection);
        if (structured != null && structured.size() > 0) {
            for (Object element : structured.toArray()) {
                if (type.isInstance(element)) {
                    result.add(type.cast(element));
                }
            }
        }
        return result;
    }

    /**
     * Tries to cast the selection to a <code>IStructuredSelection</code> and if
     * this succeeds and the structured selection is non-empty return the first
     * element within this selection.
     * 
     * @param selection
     * @return
     */
    public static Object retrieveFirst(ISelection selection) {
        IStructuredSelection structured = toStructured(selection);
        if (structured == null) {
            return null;
        }
        if (structured.size() == 0) {
            return null;
        }
        return structured.getFirstElement();
    }

    public static IStructuredSelection getAdaptedSelection(ISelection selection,
            Class<?> adaptedClass) {
        return getAdaptedSelection(selection, new Class<?>[] { adaptedClass });
    }

    public static IStructuredSelection getAdaptedSelection(ISelection selection,
            Class<?> adaptedClasses[]) {
        List<Object> adaptedElements = Lists.newArrayList();

        IStructuredSelection oldSelection = Selections.toStructured(selection);
        if (oldSelection != null) {
            for (Object selectedElement : oldSelection.toList()) {
                if (selectedElement instanceof IAdaptable) {
                    IAdaptable selectedAdaptable = (IAdaptable) selectedElement;
                    Object adaptedElement = null;
                    for (Class<?> recognisedClass : adaptedClasses) {
                        adaptedElement = selectedAdaptable.getAdapter(recognisedClass);
                        if (adaptedElement != null) {
                            break;
                        }
                    }
                    if (adaptedElement != null) {
                        selectedElement = adaptedElement;
                    }
                }
                adaptedElements.add(selectedElement);
            }
        }

        return new StructuredSelection(adaptedElements);
    }

    public static boolean hasObjectState(ISelection selection, String name, String value) {
        IStructuredSelection structured = Selections.getAdaptedSelection(selection,
                new Class<?>[] { IActionFilter.class });
        if (structured == null) {
            return false;
        }

        for (Object selectedObject : structured.toList()) {
            if (!(selectedObject instanceof IActionFilter)) {
                return false;
            }
            IActionFilter selectedActionFilter = (IActionFilter) selectedObject;
            if (!selectedActionFilter.testAttribute(selectedObject, name, value)) {
                return false;
            }
        }
        return true;
    }
}

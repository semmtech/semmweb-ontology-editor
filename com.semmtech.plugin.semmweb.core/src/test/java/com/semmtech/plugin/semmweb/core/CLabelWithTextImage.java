package com.semmtech.plugin.semmweb.core;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * 
 * @author Mike Henrichs
 */
public class CLabelWithTextImage {

    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display, SWT.SHELL_TRIM);
        shell.setLayout(new FillLayout());

        CLabel label = new CLabel(shell, SWT.NONE);
        label.setText("text osdfsdfsdfsdfsdfsdfsdfsdn the label");

        // label.setImage(new Image(display, "yourFile.gif"));
        shell.open();
        // Set up the event loop.
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                // If no more entries in event queue
                display.sleep();
            }
        }
        display.dispose();
    }

}

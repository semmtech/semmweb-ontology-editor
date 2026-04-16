package com.semmtech.plugin.semmweb.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ResizableComposite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setText("SWT SashForm Example");
 
		shell.setLayout(new FillLayout());
 
	    // Create the SashForm with HORIZONTAL
	    SashForm sashForm = new SashForm(shell, SWT.HORIZONTAL);
	    new Button(sashForm, SWT.PUSH).setText("Left");
	    new Button(sashForm, SWT.PUSH).setText("Right");
 
	    // Create the SashForm with VERTICAL
	    SashForm sashForm2 = new SashForm(shell, SWT.VERTICAL);
	    new Button(sashForm2, SWT.PUSH).setText("Up");
	    new Button(sashForm2, SWT.PUSH).setText("Down");
 
		shell.open();
 
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}

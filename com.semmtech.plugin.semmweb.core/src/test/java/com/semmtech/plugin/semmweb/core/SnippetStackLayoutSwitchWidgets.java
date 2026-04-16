package com.semmtech.plugin.semmweb.core;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * 
 * @author Sander Stolk
 */
public class SnippetStackLayoutSwitchWidgets {

    public static void main(String[] args) {
        Display display = new Display();
        final Shell shell = new Shell(display);
        GridLayout gridLayout = new GridLayout(2, false);
        shell.setLayout(gridLayout);

        Label selectControlLabel = new Label(shell, SWT.NONE);
        selectControlLabel.setText("Select control:");
        final Combo combo = new Combo(shell, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        combo.setItems(new String[] { "Text", "Combo", "Radio" });

        final Composite composite = new Composite(shell, SWT.NONE);
        final StackLayout stackLayout = new StackLayout();
        composite.setLayout(stackLayout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true, 2, 1));

        final Text text = new Text(composite, SWT.BORDER);

        final Combo combo1 = new Combo(composite, SWT.NONE);

        final Button button = new Button(composite, SWT.RADIO);
        button.setText("Radio Button");

        combo.select(0);
        combo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                switch (combo.getSelectionIndex()) {
                case 0:
                    stackLayout.topControl = text;
                    break;
                case 1:
                    stackLayout.topControl = combo1;
                    break;
                case 2:
                    stackLayout.topControl = button;
                    break;
                }
                composite.layout();
            }
        });

        // stackLayout.topControl = text;

        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
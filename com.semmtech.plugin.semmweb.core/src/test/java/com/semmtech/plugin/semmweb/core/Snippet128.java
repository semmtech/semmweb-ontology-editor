package com.semmtech.plugin.semmweb.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Snippet128 {
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        int style = SWT.MULTI | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        final Table table = new Table(shell, style);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText("Column 1");
        column1.setResizable(true);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText("Column 2");
        column2.setResizable(true);
        for (int i = 0; i < 10; i++) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(new String[] { "item " + i, "doubleclick to edit this value" });
        }
        column1.pack();
        column2.pack();

        final TableEditor editor = new TableEditor(table);
        // The editor must have the same size as the cell and must
        // not be any smaller than 50 pixels.
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;
        // editing the second column
        final int EDITABLECOLUMN = 1;

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                disposeEditor(editor);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                disposeEditor(editor);

                // Identify the selected row
                TableItem item = (TableItem) e.item;
                if (item == null) {
                    return;
                }

                // The control that will be the editor must be a child of the
                // Table
                StyledText newEditor = new StyledText(table, SWT.H_SCROLL | SWT.BORDER | SWT.ON_TOP);
                newEditor.setText(item.getText(EDITABLECOLUMN));
                newEditor.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        StyledText text = (StyledText) editor.getEditor();
                        editor.getItem().setText(EDITABLECOLUMN, text.getText());
                    }
                });
                newEditor.selectAll();
                newEditor.setFocus();
                newEditor.setBackground(newEditor.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
                editor.minimumHeight = 40;
                editor.setEditor(newEditor, item, EDITABLECOLUMN);
            }

            private void disposeEditor(final TableEditor editor) {
                // Clean up any previous editor control
                Control oldEditor = editor.getEditor();
                if (oldEditor != null) {
                    oldEditor.dispose();
                }
            }
        });

        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}

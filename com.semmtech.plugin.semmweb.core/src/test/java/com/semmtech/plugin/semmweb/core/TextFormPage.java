package com.semmtech.plugin.semmweb.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class TextFormPage extends FormPage {
	private Text txtThisIsThe;

	/**
	 * Create the form page.
	 * @param id
	 * @param title
	 */
	public TextFormPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public TextFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Empty FormPage");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		
		managedForm.getForm().getBody().setLayout(new TableWrapLayout());
		
		Composite composite = new Composite(managedForm.getForm().getBody(), SWT.BORDER);
		{
			TableWrapLayout twl_composite = new TableWrapLayout();
			twl_composite.numColumns = 3;
			composite.setLayout(twl_composite);
		}
		TableWrapData twd_composite = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1);
		twd_composite.align = TableWrapData.FILL;
		twd_composite.valign = TableWrapData.FILL;
		twd_composite.grabVertical = true;
		twd_composite.grabHorizontal = true;
		composite.setLayoutData(twd_composite);
		managedForm.getToolkit().adapt(composite);
		managedForm.getToolkit().paintBordersFor(composite);
		
		Label lblX = new Label(composite, SWT.NONE);
		lblX.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.TOP, 1, 1));
		managedForm.getToolkit().adapt(lblX, true, true);
		lblX.setText("X");
		
		txtThisIsThe = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		txtThisIsThe.setText("By convention, skos:broader is only used to met nog wat extra tekst erbij assert an immediate (i.e. direct) hierarchical link between two conceptual resources. {@en}");
		txtThisIsThe.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
		managedForm.getToolkit().adapt(txtThisIsThe, true, true);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		managedForm.getToolkit().adapt(lblNewLabel, true, true);
		lblNewLabel.setText("V");
	}
}

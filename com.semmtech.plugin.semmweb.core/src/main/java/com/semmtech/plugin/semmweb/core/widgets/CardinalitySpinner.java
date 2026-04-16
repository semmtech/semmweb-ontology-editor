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

package com.semmtech.plugin.semmweb.core.widgets;


import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


public class CardinalitySpinner extends Composite {
    private SquareButton rightUpButton;
    private SquareButton rightDownButton;
    private SquareButton unboundedButton;

    private Composite lowerButtons;

    private SquareButton leftUpButton;
    private SquareButton leftDownButton;

    private Text leftText;
    private Text rightText;
    private Menu menu;

    private MenuItem unboundedItem;
    private MenuItem someItem;
    private MenuItem oneItem;

    private static final String DEFAULT_SEPARATOR = "to";

    private boolean allowUnbounded = true;
    private int lowerBound = 0;
    private int upperBound = 1024;

    private int leftValue = 0;
    private int rightValue = 1;
    private boolean unbounded = allowUnbounded;
    private MenuItem optionalItem;
    private MenuItem noneItem;

    private boolean ignoreModify = false;
    private boolean minimal = true;
    private SquareButton zeroButton;

    private int previousLeftValue;
    private int previousRightValue;
    private boolean previousUnbounded;

    private List<KeyListener> keyListeners = Lists.newArrayList();
    private Label seperatorLabel;
    private List<ModifyListener> modifyListeners = Lists.newArrayList();

    @Override
    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    public CardinalitySpinner(Composite parent, int style, boolean minimal) {
        this(parent, style, minimal, DEFAULT_SEPARATOR);
    }

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public CardinalitySpinner(Composite parent, int style, boolean minimal, String separator) {
        super(parent, style);
        this.minimal = minimal;
        this.previousLeftValue = leftValue;
        this.previousRightValue = rightValue;
        this.previousUnbounded = unbounded;

        GridLayout layout = new GridLayout(7, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        this.setLayout(layout);
        this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        menu = new Menu(this);
        this.setMenu(menu);

        if (!minimal) {
            zeroButton = new SquareButton(this, SWT.NONE);
            {
                GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
                data.widthHint = 16;
                data.heightHint = 16;
                zeroButton.setLayoutData(data);
            }
            zeroButton.setRoundedCorners(false);
            zeroButton.setText("");
            zeroButton.setBackgroundImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_CARDINALITY_FROM_ZERO));
            zeroButton.setBackgroundImageStyle(SquareButton.BG_IMAGE_CENTER);
            zeroButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setValues(0, rightValue, unbounded);
                }
            });

            lowerButtons = new Composite(this, SWT.NONE);
            layout = new GridLayout(1, false);
            layout.verticalSpacing = 0;
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            lowerButtons.setLayout(layout);

            leftUpButton = new SquareButton(lowerButtons, SWT.NONE);
            GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
            layoutData.widthHint = 11;
            layoutData.heightHint = 7;
            leftUpButton.setLayoutData(layoutData);

            leftUpButton.setText("");
            leftUpButton.setRoundedCorners(false);
            leftUpButton.setInnerMarginWidth(2);
            leftUpButton.setInnerMarginHeight(2);
            leftUpButton.setBackgroundImageStyle(3);
            leftUpButton.setBackgroundImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_SPINNER_UP));
            leftUpButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    increaseLeft();
                }
            });

            leftDownButton = new SquareButton(lowerButtons, SWT.NONE);
            layoutData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
            layoutData.widthHint = 11;
            layoutData.heightHint = 7;
            leftDownButton.setLayoutData(layoutData);

            leftDownButton.setText("");
            leftDownButton.setRoundedCorners(false);
            leftDownButton.setInnerMarginWidth(2);
            leftDownButton.setInnerMarginHeight(2);
            leftDownButton.setBackgroundImageStyle(3);
            leftDownButton.setBackgroundImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_SPINNER_DOWN));
            leftDownButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    decreaseLeft();
                }
            });
        }

        // / Left Text
        leftText = new Text(this, SWT.RIGHT);
        leftText.setMenu(menu);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        layoutData.widthHint = 22;
        leftText.setLayoutData(layoutData);
        leftText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.KEYPAD_ADD)
                    increaseLeft();
                else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.KEYPAD_SUBTRACT)
                    decreaseLeft();
                else if (e.keyCode == SWT.CR)
                    validateLeftText();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                for (KeyListener listener : keyListeners)
                    listener.keyReleased(e);
            }
        });
        leftText.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseScrolled(MouseEvent e) {
                int count = e.count;
                if (count > 0)
                    increaseLeft();
                else if (count < 0)
                    decreaseLeft();
            }
        });
        leftText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validateLeftText();
            }
        });
        // leftText.addFocusListener(new FocusAdapter() {
        // @Override
        // public void focusLost(FocusEvent e) {
        // validateLeftText();
        // }
        // });

        seperatorLabel = new Label(this, SWT.NONE);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        layoutData.widthHint = 11;
        layoutData.horizontalIndent = 0;
        layoutData.verticalIndent = 0;
        seperatorLabel.setLayoutData(layoutData);
        seperatorLabel.setText(separator);
        seperatorLabel.setMenu(menu);
        seperatorLabel.setBackground(getBackground());

        rightText = new Text(this, SWT.NONE);
        rightText.setMenu(menu);

        layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        layoutData.widthHint = 22;
        rightText.setLayoutData(layoutData);
        rightText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.KEYPAD_ADD)
                    increaseRight();
                else if (e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.KEYPAD_SUBTRACT)
                    decreaseRight();
                else if (e.keyCode == SWT.CR)
                    validateRightText();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                for (KeyListener listener : keyListeners)
                    listener.keyReleased(e);
            }
        });
        rightText.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseScrolled(MouseEvent e) {
                int count = e.count;
                if (count > 0)
                    increaseRight();
                else if (count < 0)
                    decreaseRight();
            }
        });
        rightText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validateLeftText();
            }
        });
        // rightText.addFocusListener(new FocusAdapter() {
        // @Override
        // public void focusLost(FocusEvent e) {
        // validateRightText();
        // }
        // });

        if (!minimal) {
            Composite upperButtons = new Composite(this, SWT.NONE);
            layout = new GridLayout(1, false);
            layout.verticalSpacing = 0;
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            upperButtons.setLayout(layout);

            rightUpButton = new SquareButton(upperButtons, SWT.NONE);
            layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
            layoutData.heightHint = 7;
            layoutData.widthHint = 11;
            rightUpButton.setLayoutData(layoutData);

            rightUpButton.setInnerMarginHeight(2);
            rightUpButton.setInnerMarginWidth(2);
            rightUpButton.setBackgroundImageStyle(SquareButton.BG_IMAGE_CENTER);
            rightUpButton.setBackgroundImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_SPINNER_UP));
            rightUpButton.setRoundedCorners(false);
            rightUpButton.setText("");
            rightUpButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    increaseRight();
                }
            });

            rightDownButton = new SquareButton(upperButtons, SWT.NONE);
            layoutData = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
            layoutData.widthHint = 11;
            layoutData.heightHint = 7;
            rightDownButton.setLayoutData(layoutData);

            rightDownButton.setText("");
            rightDownButton.setRoundedCorners(false);
            rightDownButton.setInnerMarginWidth(2);
            rightDownButton.setInnerMarginHeight(2);
            rightDownButton.setBackgroundImageStyle(SquareButton.BG_IMAGE_CENTER);
            rightDownButton.setBackgroundImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_SPINNER_DOWN));
            rightDownButton.setBounds(0, 0, 13, 11);
            rightDownButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    decreaseRight();
                }
            });

            if (allowUnbounded) {
                unboundedButton = new SquareButton(this, SWT.NONE);
                {
                    GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
                    data.widthHint = 16;
                    data.heightHint = 16;
                    unboundedButton.setLayoutData(data);
                }
                unboundedButton.setRoundedCorners(false);
                unboundedButton.setText("");
                unboundedButton.setBackgroundImage(CorePlugin.getDefault().getImage(
                        CorePluginImages.IMG_CARDINALITY_TO_UNBOUNDED));
                unboundedButton.setBackgroundImageStyle(SquareButton.BG_IMAGE_CENTER);
                unboundedButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        setUnbounded();
                    }
                });
            }
        }

        createPopupMenu();

        updateButtons();
        updateTextValues();
    }

    protected void validateLeftText() {
        if (ignoreModify)
            return;
        ignoreModify = true;
        String text = leftText.getText();
        int value = leftValue;
        try {
            value = Integer.parseInt(text);
        }
        catch (NumberFormatException ex) {
            MessageDialog.openError(getShell(), "Invalid Value", ex.getMessage());
        }
        if (value > upperBound)
            MessageDialog.openError(getShell(), "Invalid Value", "An upper bound of max "
                    + upperBound + " is allowed");
        else if (value < lowerBound)
            MessageDialog.openError(getShell(), "Invalid Value", "A lower bound of min "
                    + lowerBound + " is allowed");
        else {
            leftValue = value;
            notifyValueChange();
        }
        if (leftValue > rightValue)
            rightValue = leftValue;

        updateButtons();
        updateTextValues();
        ignoreModify = false;
    }

    protected void validateRightText() {
        if (ignoreModify)
            return;
        ignoreModify = true;

        String text = rightText.getText();
        if (text.equals("n") || text.equals("N") || text.equals("*")) {
            if (!allowUnbounded) {
                MessageDialog.openError(getShell(), "Invalid Value",
                        "An unbounded upper limit is not allowed");
            }
            else {
                unbounded = true;
                rightText.setText("n");
            }
        }
        else {
            int value = rightValue;
            try {
                value = Integer.parseInt(text);
                unbounded = false;
            }
            catch (NumberFormatException ex) {
                MessageDialog.openError(getShell(), "Invalid Value", ex.getMessage());
            }
            if (value > upperBound)
                MessageDialog.openError(getShell(), "Invalid Value", "An upper bound of max "
                        + upperBound + " is allowed");
            else if (value < lowerBound)
                MessageDialog.openError(getShell(), "Invalid Value", "A lower bound of min "
                        + lowerBound + " is allowed");
            else {
                rightValue = value;
                notifyValueChange();
            }
            if (rightValue < leftValue)
                leftValue = rightValue;
        }

        updateButtons();
        updateTextValues();
        ignoreModify = false;
    }

    private void notifyValueChange() {
        notifyModifyListeners();
    }

    private void notifyModifyListeners() {
        Event event = new Event();
        event.widget = this;
        for (ModifyListener listener : modifyListeners) {
            listener.modifyText(new ModifyEvent(event));
        }
    }

    public void addModifyListener(ModifyListener listener) {
        modifyListeners.add(listener);
    }

    public void removeModifyListener(ModifyListener listener) {
        modifyListeners.remove(listener);
    }

    private void createPopupMenu() {
        if (lowerBound == 0) {
            noneItem = new MenuItem(menu, SWT.NONE);
            noneItem.setImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_CARDINALITY_NONE));
            noneItem.setText("None");
            noneItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setValues(0, 0, false);
                }
            });
        }

        if (lowerBound == 0 && upperBound >= 1) {
            optionalItem = new MenuItem(menu, SWT.NONE);
            optionalItem.setImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_CARDINALITY_OPTIONAL));
            optionalItem.setText("Optional");
            optionalItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setValues(0, 1, false);
                }
            });
        }

        if (lowerBound <= 1 && upperBound >= 1) {
            oneItem = new MenuItem(menu, SWT.NONE);
            oneItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_CARDINALITY_ONE));
            oneItem.setText("One");
            oneItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setValues(1, 1, false);
                }
            });
        }

        if (allowUnbounded && lowerBound <= 1) {
            someItem = new MenuItem(menu, SWT.NONE);
            someItem.setImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_CARDINALITY_SOME));
            someItem.setText("Some");
            someItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setValues(1, 1, true);
                }
            });
        }

        if (allowUnbounded && lowerBound == 0) {
            unboundedItem = new MenuItem(menu, SWT.NONE);
            unboundedItem.setImage(CorePlugin.getDefault().getImage(
                    CorePluginImages.IMG_CARDINALITY_UNBOUNDED));
            unboundedItem.setText("Unbounded");
            unboundedItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setValues(0, 0, true);
                }
            });
        }
    }

    public void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            leftText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            seperatorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            rightText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }
        else {
            leftText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            seperatorLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            rightText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
        this.update();
    }

    protected void setUnbounded() {
        unbounded = true;
        updateButtons();
        updateTextValues();
    }

    protected void updateButtons() {
        if (!minimal) {
            leftDownButton.setEnabled((leftValue > lowerBound));
            leftUpButton.setEnabled((leftValue < upperBound));
            rightDownButton.setEnabled((rightValue > lowerBound));
            rightUpButton.setEnabled((rightValue < upperBound));
        }
    }

    protected void updateTextValues() {
        leftText.setText("" + leftValue);
        rightText.setText("" + rightValue);
        if (unbounded)
            rightText.setText("n");

        checkModify();
    }

    private void checkModify() {
        if (leftValue != previousLeftValue)
            notifyListeners(SWT.Modify, new Event());
        else if (rightValue != previousRightValue)
            notifyListeners(SWT.Modify, new Event());
        else if (unbounded != previousUnbounded)
            notifyListeners(SWT.Modify, new Event());
        previousLeftValue = leftValue;
        previousRightValue = rightValue;
        previousUnbounded = unbounded;
    }

    protected void decreaseLeft() {
        leftValue--;
        checkLeftValue();
        updateButtons();
        updateTextValues();
    }

    protected void increaseLeft() {
        leftValue++;
        checkLeftValue();
        updateButtons();
        updateTextValues();
    }

    protected void checkLeftValue() {
        if (leftValue < lowerBound)
            leftValue = lowerBound;
        if (leftValue > upperBound)
            leftValue = upperBound;
        if (leftValue > rightValue)
            rightValue = leftValue;
    }

    protected void decreaseRight() {
        rightValue--;
        checkRightValue();
        updateButtons();
        updateTextValues();
    }

    protected void increaseRight() {
        rightValue++;
        checkRightValue();
        updateButtons();
        updateTextValues();
    }

    protected void checkRightValue() {
        if (rightValue < lowerBound)
            rightValue = lowerBound;
        if (rightValue > upperBound)
            rightValue = upperBound;
        if (rightValue < leftValue)
            leftValue = rightValue;
        if (rightValue == upperBound)
            unbounded = true;
        else
            unbounded = false;
    }

    public void setValues(int lower, int upper, boolean unbounded) {
        if (lower < lowerBound || lower > upperBound || upper < lowerBound || upper > upperBound)
            throw new IllegalArgumentException("Invalid value lower = '" + lower
                    + "' and upper = '" + upper + "', must be between [" + lowerBound + ","
                    + upperBound + "]");
        if (lower > upper)
            throw new IllegalArgumentException("Invalid values lower = '" + lower
                    + "' and upper = '" + upper + "', left value must be smaller than right value!");
        if (unbounded && !allowUnbounded)
            throw new IllegalArgumentException(
                    "Invalid values, cannot set unbounded if not allowed");
        this.leftValue = lower;
        this.rightValue = upper;
        this.unbounded = unbounded;
        updateButtons();
        updateTextValues();
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public boolean getUnbounded() {
        return unbounded;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setMin(int min) {
        leftValue = min;
        checkLeftValue();
        updateButtons();
        updateTextValues();
    }

    public int getMin() {
        return leftValue;
    }

    public Cardinality getCardinality() {
        if (unbounded) {
            return new Cardinality(leftValue, true);
        }
        return new Cardinality(leftValue, rightValue);
    }

    public void setCardinality(Cardinality cardinality) {
        if (cardinality != null) {
            this.leftValue = cardinality.getMin();
            checkLeftValue();
            this.unbounded = cardinality.getUnbounded();
            this.rightValue = cardinality.getMax();
            // else
            // this.rightValue = upperBound;
            // checkRightValue();
            updateButtons();
            updateTextValues();
        }
    }

    public void setMax(int max) {
        rightValue = max;
        checkRightValue();
        updateButtons();
        updateTextValues();
    }

    public int getMax() {
        return rightValue;
    }

}

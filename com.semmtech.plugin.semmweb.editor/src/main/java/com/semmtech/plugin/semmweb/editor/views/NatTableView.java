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

package com.semmtech.plugin.semmweb.editor.views;


import java.util.Date;

//import org.eclipse.nebula.widgets.nattable.NatTable;
//import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
//import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
//import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
//import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
//import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
//import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
//import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
//import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
//import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
//import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
//import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
//import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
//import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
//import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
//import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


public class NatTableView extends ViewPart {

    // private BodyLayerStack bodyLayer;
    // private IDataProvider columnDataProvider;
    // private IDataProvider rowDataProvider;

    public class Person {
        private int id;
        private String name;
        private Date birthDate;

        public Person(int id, String name, Date birthDate) {
            this.id = id;
            this.name = name;
            this.birthDate = birthDate;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Date getBirthDate() {
            return birthDate;
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        // final List<Person> people = Lists.newArrayList(new Person(1, "Mike",
        // new Date()));
        // final String[] propertyNames = new String[] { "id", "name",
        // "birthDate" };
        // ListDataProvider<Person> dataProvider = new
        // ListDataProvider<Person>(people,
        // new ReflectiveColumnPropertyAccessor<Person>(propertyNames));
        //
        // columnDataProvider = new IDataProvider() {
        //
        // @Override
        // public void setDataValue(int columnIndex, int rowIndex, Object
        // newValue) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public int getRowCount() {
        // // TODO Auto-generated method stub
        // return 1;
        // }
        //
        // @Override
        // public Object getDataValue(int columnIndex, int rowIndex) {
        // return propertyNames[columnIndex];
        // }
        //
        // @Override
        // public int getColumnCount() {
        // return propertyNames.length;
        // }
        // };
        // rowDataProvider = new IDataProvider() {
        //
        // @Override
        // public void setDataValue(int columnIndex, int rowIndex, Object
        // newValue) {
        // // TODO Auto-generated method stub
        //
        // }
        //
        // @Override
        // public int getRowCount() {
        // // TODO Auto-generated method stub
        // return people.size();
        // }
        //
        // @Override
        // public Object getDataValue(int columnIndex, int rowIndex) {
        // return rowIndex + 1;
        // }
        //
        // @Override
        // public int getColumnCount() {
        // return 1;
        // }
        // };
        // bodyLayer = new BodyLayerStack(dataProvider);
        //
        // ColumnHeaderLayerStack columnHeaderLayer = new
        // ColumnHeaderLayerStack(columnDataProvider);
        // RowHeaderLayerStack rowHeaderLayer = new
        // RowHeaderLayerStack(rowDataProvider);
        //
        // DefaultCornerDataProvider cornerDataProvider = new
        // DefaultCornerDataProvider(
        // columnDataProvider, rowDataProvider);
        // CornerLayer cornerLayer = new CornerLayer(new
        // DataLayer(cornerDataProvider),
        // rowHeaderLayer, columnHeaderLayer);
        //
        // GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer,
        // rowHeaderLayer,
        // cornerLayer);
        // gridLayer.addConfiguration(new ModernNatTableThemeConfiguration());
        // NatTable natTable = new NatTable(parent, gridLayer);
        //
        // GridDataFactory.fillDefaults().applyTo(natTable);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    // public class BodyLayerStack extends AbstractLayerTransform {
    //
    // private SelectionLayer selectionLayer;
    //
    // public BodyLayerStack(IDataProvider dataProvider) {
    // DataLayer bodyDataLayer = new DataLayer(dataProvider);
    // ColumnReorderLayer columnReorderLayer = new
    // ColumnReorderLayer(bodyDataLayer);
    // ColumnHideShowLayer columnHideShowLayer = new
    // ColumnHideShowLayer(columnReorderLayer);
    // selectionLayer = new SelectionLayer(columnHideShowLayer);
    // ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
    // setUnderlyingLayer(viewportLayer);
    // }
    //
    // public SelectionLayer getSelectionLayer() {
    // return selectionLayer;
    // }
    // }
    //
    // public class ColumnHeaderLayerStack extends AbstractLayerTransform {
    //
    // public ColumnHeaderLayerStack(IDataProvider dataProvider) {
    // DataLayer dataLayer = new DataLayer(dataProvider);
    // ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer,
    // bodyLayer,
    // bodyLayer.getSelectionLayer());
    // setUnderlyingLayer(colHeaderLayer);
    // }
    // }
    //
    // public class RowHeaderLayerStack extends AbstractLayerTransform {
    //
    // public RowHeaderLayerStack(IDataProvider dataProvider) {
    // DataLayer dataLayer = new DataLayer(dataProvider, 50, 20);
    // RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer, bodyLayer,
    // bodyLayer.getSelectionLayer());
    // setUnderlyingLayer(rowHeaderLayer);
    // }
    // }

}

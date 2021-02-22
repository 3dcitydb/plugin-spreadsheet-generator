/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2020
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.citydb.plugins.spreadsheet_gen.gui.view;

import org.citydb.ade.ADEExtension;
import org.citydb.ade.kmlExporter.ADEBalloonExtension;
import org.citydb.ade.kmlExporter.ADEBalloonExtensionManager;
import org.citydb.config.exception.ErrorCode;
import org.citydb.config.geometry.BoundingBox;
import org.citydb.config.i18n.Language;
import org.citydb.config.project.global.LogLevel;
import org.citydb.config.project.query.simple.SimpleAttributeFilter;
import org.citydb.config.project.query.simple.SimpleFeatureVersionFilter;
import org.citydb.config.project.query.simple.SimpleFeatureVersionFilterMode;
import org.citydb.database.DatabaseController;
import org.citydb.event.Event;
import org.citydb.event.EventDispatcher;
import org.citydb.event.global.InterruptEvent;
import org.citydb.gui.components.common.TitledPanel;
import org.citydb.gui.components.dialog.ConfirmationCheckDialog;
import org.citydb.gui.factory.PopupMenuDecorator;
import org.citydb.gui.modules.common.filter.AttributeFilterView;
import org.citydb.gui.modules.common.filter.BoundingBoxFilterView;
import org.citydb.gui.modules.common.filter.FeatureTypeFilterView;
import org.citydb.gui.modules.common.filter.FeatureVersionFilterView;
import org.citydb.gui.modules.common.filter.SQLFilterView;
import org.citydb.gui.util.GuiUtil;
import org.citydb.log.Logger;
import org.citydb.plugin.extension.view.ViewController;
import org.citydb.plugins.spreadsheet_gen.SPSHGPlugin;
import org.citydb.plugins.spreadsheet_gen.config.ExportConfig;
import org.citydb.plugins.spreadsheet_gen.config.GuiConfig;
import org.citydb.plugins.spreadsheet_gen.config.OutputFileType;
import org.citydb.plugins.spreadsheet_gen.config.SimpleQuery;
import org.citydb.plugins.spreadsheet_gen.controller.SpreadsheetExporter;
import org.citydb.plugins.spreadsheet_gen.controller.TableExportException;
import org.citydb.plugins.spreadsheet_gen.controller.TemplateWriter;
import org.citydb.plugins.spreadsheet_gen.database.Translator;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.Delimiter;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.NewCSVColumnDialog;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.StatusDialog;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.TableDataModel;
import org.citydb.plugins.spreadsheet_gen.util.Util;
import org.citydb.registry.ObjectRegistry;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.xml.datatype.DatatypeConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class SPSHGPanel extends JPanel {
    private final Logger log = Logger.getInstance();
    private final ReentrantLock mainLock = new ReentrantLock();
    private final ViewController viewController;
    private final DatabaseController dbController;
    private final SPSHGPlugin plugin;

    private TitledPanel csvColumnsPanel;
    private final JLabel templateLabel = new JLabel();
    private final JTextField browseText = new JTextField();
    private final JButton browseButton = new JButton();
    private final JButton editTemplateButton = new JButton();
    private final JButton manuallyTemplateButton = new JButton();

    private JCheckBox useFeatureVersionFilter;
    private JCheckBox useAttributeFilter;
    private JCheckBox useSQLFilter;
    private JCheckBox useBBoxFilter;
    private JCheckBox useFeatureFilter;

    private TitledPanel featureVersionPanel;
    private TitledPanel attributeFilterPanel;
    private TitledPanel sqlFilterPanel;
    private TitledPanel featureFilterPanel;
    private TitledPanel bboxFilterPanel;

    private FeatureVersionFilterView featureVersionFilter;
    private AttributeFilterView attributeFilter;
    private SQLFilterView sqlFilter;
    private FeatureTypeFilterView featureTypeFilter;
    private BoundingBoxFilterView bboxFilter;

    private TitledPanel outputPanel;
    private final JRadioButton csvRadioButton = new JRadioButton();
    private final JTextField browseOutputText = new JTextField();
    private final JButton browseOutputButton = new JButton();
    private final JLabel delimiterLabel = new JLabel();
    private final JTextField separatorText = new JTextField();
    private final JComboBox<Delimiter> delimiterComboBox = new JComboBox<>();
    private final JRadioButton xlsxRadioButton = new JRadioButton();
    private final JTextField xlsxBrowseOutputText = new JTextField();
    private final JButton xlsxBrowseOutputButton = new JButton();

    private final JButton exportButton = new JButton();
    private JPanel manualPanel;
    private JPanel buttonsPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private final TableDataModel tableDataModel = new TableDataModel();
    private final JButton upButton = new JButton();
    private final JButton downButton = new JButton();
    private final JButton editButton = new JButton();
    private final JButton addButton = new JButton();
    private final JButton removeButton = new JButton();
    private final JButton saveButton = new JButton();
    private final JLabel saveMessage = new JLabel();

    private String previousVisitBySaveTemplate = "";

    SPSHGPanel(ViewController viewController, SPSHGPlugin plugin) {
        this.viewController = viewController;
        this.plugin = plugin;
        dbController = ObjectRegistry.getInstance().getDatabaseController();

        initGui();
        clearGui();
    }

    private void initGui() {
        useFeatureVersionFilter = new JCheckBox();
        useAttributeFilter = new JCheckBox();
        useSQLFilter = new JCheckBox();
        useFeatureFilter = new JCheckBox();
        useBBoxFilter = new JCheckBox();

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        {
            JPanel outputContentPanel = new JPanel();
            outputContentPanel.setLayout(new GridBagLayout());

            // radio buttons
            ButtonGroup outputButtonGroup = new ButtonGroup();
            outputButtonGroup.add(csvRadioButton);
            outputButtonGroup.add(xlsxRadioButton);
            csvRadioButton.setSelected(true);

            JPanel csvOutputPanel = new JPanel();
            csvOutputPanel.setLayout(new GridBagLayout());
            {
                Arrays.stream(Delimiter.values()).forEach(delimiterComboBox::addItem);
                int lmargin = GuiUtil.getTextOffset(csvRadioButton);

                csvOutputPanel.add(csvRadioButton, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 5));
                csvOutputPanel.add(browseOutputText, GuiUtil.setConstraints(1, 0, 1, 0, GridBagConstraints.HORIZONTAL, 0, 5, 0, 0));
                csvOutputPanel.add(browseOutputButton, GuiUtil.setConstraints(2, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 10, 0, 0));
                csvOutputPanel.add(delimiterLabel, GuiUtil.setConstraints(0, 1, 0, 0, GridBagConstraints.HORIZONTAL, 5, lmargin, 0, 5));
                csvOutputPanel.add(delimiterComboBox, GuiUtil.setConstraints(1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, 5, 5, 0, 0));
            }

            JPanel xlsxOutputPanel = new JPanel();
            xlsxOutputPanel.setLayout(new GridBagLayout());
            {
                xlsxOutputPanel.add(xlsxRadioButton, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 5));
                xlsxOutputPanel.add(xlsxBrowseOutputText, GuiUtil.setConstraints(1, 0, 1, 0, GridBagConstraints.HORIZONTAL, 0, 5, 0, 0));
                xlsxOutputPanel.add(xlsxBrowseOutputButton, GuiUtil.setConstraints(2, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 10, 0, 0));
            }

            outputContentPanel.add(csvOutputPanel, GuiUtil.setConstraints(0, 0, 1, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 0));
            outputContentPanel.add(xlsxOutputPanel, GuiUtil.setConstraints(0, 1, 1, 0, GridBagConstraints.HORIZONTAL, 5, 0, 0, 0));

            outputPanel = new TitledPanel().build(outputContentPanel);
        }
        {
            JPanel columnsPanel = new JPanel();
            columnsPanel.setLayout(new GridBagLayout());

            JPanel browsePanel = new JPanel();
            browsePanel.setLayout(new GridBagLayout());
            {
                browseText.setColumns(10);
                browsePanel.add(templateLabel, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 0));
                browsePanel.add(browseText, GuiUtil.setConstraints(0, 1, 1, 1, GridBagConstraints.HORIZONTAL, 3, 0, 0, 0));
                browsePanel.add(browseButton, GuiUtil.setConstraints(1, 1, 0, 0, GridBagConstraints.HORIZONTAL, 3, 10, 0, 0));
                browsePanel.add(manuallyTemplateButton, GuiUtil.setConstraints(0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, 5, 0, 0, 0));
                browsePanel.add(editTemplateButton, GuiUtil.setConstraints(1, 2, 0, 0, GridBagConstraints.HORIZONTAL, 5, 10, 0, 0));
            }

            manualPanel = new JPanel();
            manualPanel.setVisible(false);
            manualPanel.setLayout(new GridBagLayout());
            {
                table = new JTable(tableDataModel);
                table.setShowVerticalLines(true);
                table.setShowHorizontalLines(true);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                table.setCellSelectionEnabled(false);
                table.setColumnSelectionAllowed(false);
                table.setRowSelectionAllowed(true);
                modifyTableColumnsSize();

                scrollPane = new JScrollPane(table);

                buttonsPanel = new JPanel();
                buttonsPanel.setLayout(new GridBagLayout());
                {
                    buttonsPanel.add(addButton, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 0));
                    buttonsPanel.add(removeButton, GuiUtil.setConstraints(0, 1, 0, 0, GridBagConstraints.HORIZONTAL, 5, 0, 0, 0));
                    buttonsPanel.add(editButton, GuiUtil.setConstraints(0, 2, 0, 0, GridBagConstraints.HORIZONTAL, 5, 0, 0, 0));
                    buttonsPanel.add(upButton, GuiUtil.setConstraints(0, 3, 0, 0, GridBagConstraints.HORIZONTAL, 5, 0, 0, 0));
                    buttonsPanel.add(downButton, GuiUtil.setConstraints(0, 4, 0, 0, GridBagConstraints.HORIZONTAL, 5, 0, 0, 0));
                }

                manualPanel.add(scrollPane, GuiUtil.setConstraints(0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0, 0, 0, 0));
                manualPanel.add(buttonsPanel, GuiUtil.setConstraints(1, 0, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, 0, 10, 0, 0));
                manualPanel.add(saveMessage, GuiUtil.setConstraints(0, 1, 1, 0, GridBagConstraints.HORIZONTAL, 10, 0, 0, 0));
                manualPanel.add(saveButton, GuiUtil.setConstraints(1, 1, 0, 0, GridBagConstraints.HORIZONTAL, 10, 10, 0, 0));
            }

            columnsPanel.add(browsePanel, GuiUtil.setConstraints(0, 0, 1, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 0));
            columnsPanel.add(manualPanel, GuiUtil.setConstraints(0, 1, 1, 0, GridBagConstraints.HORIZONTAL, 15, 0, 0, 0));

            csvColumnsPanel = new TitledPanel().build(columnsPanel);
        }
        {
            featureVersionFilter = new FeatureVersionFilterView();

            featureVersionPanel = new TitledPanel()
                    .withIcon(featureVersionFilter.getIcon())
                    .withToggleButton(useFeatureVersionFilter)
                    .withCollapseButton()
                    .build(featureVersionFilter.getViewComponent());
        }
        {
            attributeFilter = new AttributeFilterView()
                    .withNameFilter()
                    .withLineageFilter();

            attributeFilterPanel = new TitledPanel()
                    .withIcon(attributeFilter.getIcon())
                    .withToggleButton(useAttributeFilter)
                    .withCollapseButton()
                    .build(attributeFilter.getViewComponent());
        }
        {
            sqlFilter = new SQLFilterView(() -> plugin.getConfig().getGuiConfig().getSQLExportFilterComponent());

            sqlFilterPanel = new TitledPanel()
                    .withIcon(sqlFilter.getIcon())
                    .withToggleButton(useSQLFilter)
                    .withCollapseButton()
                    .build(sqlFilter.getViewComponent());
        }
        {
            bboxFilter = new BoundingBoxFilterView(viewController);

            bboxFilterPanel = new TitledPanel()
                    .withIcon(bboxFilter.getIcon())
                    .withToggleButton(useBBoxFilter)
                    .withCollapseButton()
                    .build(bboxFilter.getViewComponent());
        }
        {
            featureTypeFilter = new FeatureTypeFilterView(e -> e instanceof ADEBalloonExtension);

            featureFilterPanel = new TitledPanel()
                    .withIcon(featureTypeFilter.getIcon())
                    .withToggleButton(useFeatureFilter)
                    .withCollapseButton()
                    .build(featureTypeFilter.getViewComponent());
        }

        content.add(outputPanel, GuiUtil.setConstraints(0, 0, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        content.add(csvColumnsPanel, GuiUtil.setConstraints(0, 1, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        content.add(featureVersionPanel, GuiUtil.setConstraints(0, 2, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        content.add(attributeFilterPanel, GuiUtil.setConstraints(0, 3, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        content.add(sqlFilterPanel, GuiUtil.setConstraints(0, 4, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        content.add(bboxFilterPanel, GuiUtil.setConstraints(0, 5, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        content.add(featureFilterPanel, GuiUtil.setConstraints(0, 6, 0, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));

        JPanel view = new JPanel();
        view.setLayout(new GridBagLayout());
        view.add(content, GuiUtil.setConstraints(0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 10, 0, 10));

        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

        setLayout(new GridBagLayout());
        add(scrollPane, GuiUtil.setConstraints(0, 1, 1, 1, GridBagConstraints.BOTH, 15, 0, 0, 0));
        add(exportButton, GuiUtil.setConstraints(0, 2, 0, 0, GridBagConstraints.NONE, 10, 10, 10, 10));

        exportButton.addActionListener(e -> {
            Thread thread = new Thread(this::doExport);
            thread.start();
        });

        browseButton.addActionListener(e -> {
            if (choseTemplateFile()) {
                plugin.getConfig().getTemplate().setManualTemplate(false);
                setOutputEnabledValues();
            }
        });

        browseOutputButton.addActionListener(e -> outputFile());
        xlsxBrowseOutputButton.addActionListener(e -> xlsxOutputFile());
        csvRadioButton.addActionListener(e -> setOutputEnabledValues());
        xlsxRadioButton.addActionListener(e -> setOutputEnabledValues());

        useFeatureVersionFilter.addActionListener(e -> setEnabledFeatureVersionFilter());
        useAttributeFilter.addItemListener(e -> setEnabledAttributeFilter());
        useSQLFilter.addItemListener(e -> setEnabledSQLFilter());
        useBBoxFilter.addActionListener(e -> setEnabledBBoxFilter());
        useFeatureFilter.addActionListener(e -> setEnabledFeatureFilter());

        editTemplateButton.addActionListener(e -> {
            if (isFilePathValid(browseText.getText())) {
                plugin.getConfig().getTemplate().setManualTemplate(true);
                loadExistingTemplate();
                setOutputEnabledValues();
            } else if (choseTemplateFile()) {
                plugin.getConfig().getTemplate().setManualTemplate(true);
                loadExistingTemplate();
                setOutputEnabledValues();
            } else
                plugin.getConfig().getTemplate().setManualTemplate(false);
        });

        manuallyTemplateButton.addActionListener(e -> makeNewTemplate());
        addButton.addActionListener(e -> showAddNewColumnDialog(false));
        removeButton.addActionListener(e -> removeSelectedColumnFromManualTemplate());

        upButton.addActionListener(arg0 -> {
            if (table.getSelectedRowCount() > 1) return;
            int selectedRow = table.getSelectedRow();
            tableDataModel.move(selectedRow, true);
            selectedRow--;
            if (selectedRow >= 0)
                table.setRowSelectionInterval(selectedRow, selectedRow);
        });

        downButton.addActionListener(arg0 -> {
            if (table.getSelectedRowCount() > 1) return;
            int selectedRow = table.getSelectedRow();
            tableDataModel.move(selectedRow, false);
            selectedRow++;
            if (selectedRow <= table.getRowCount() - 1)
                table.setRowSelectionInterval(selectedRow, selectedRow);
        });

        editButton.addActionListener(arg0 -> showAddNewColumnDialog(true));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
                    showAddNewColumnDialog(true);
            }
        });
        table.getModel().addTableModelListener(e -> modifyButtonsVisibility());
        table.getSelectionModel().addListSelectionListener(e -> modifyButtonsVisibility());

        saveButton.addActionListener(e -> saveManuallyGeneratedTemplate());

        PopupMenuDecorator.getInstance().decorate(browseText, browseOutputText, separatorText);
        PopupMenuDecorator.getInstance().decorateAndGetTitledPanelGroup(featureVersionPanel, attributeFilterPanel,
                sqlFilterPanel, bboxFilterPanel, featureFilterPanel);
    }

    private void setOutputEnabledValues() {
        browseText.setEnabled(true);
        manualPanel.setVisible(plugin.getConfig().getTemplate().isManualTemplate());
        if (plugin.getConfig().getTemplate().isManualTemplate())
            checkButtonsVisibilityInManualTemplate();

        browseOutputButton.setEnabled(csvRadioButton.isSelected());
        browseOutputText.setEnabled(csvRadioButton.isSelected());
        delimiterLabel.setEnabled(csvRadioButton.isSelected());
        separatorText.setEnabled(csvRadioButton.isSelected());
        delimiterComboBox.setEnabled(csvRadioButton.isSelected());

        xlsxBrowseOutputButton.setEnabled(xlsxRadioButton.isSelected());
        xlsxBrowseOutputText.setEnabled(xlsxRadioButton.isSelected());
    }

    private void setEnabledFeatureVersionFilter() {
        featureVersionFilter.setEnabled(useFeatureVersionFilter.isSelected());
    }

    private void setEnabledAttributeFilter() {
        attributeFilter.setEnabled(useAttributeFilter.isSelected());
    }

    private void setEnabledSQLFilter() {
        sqlFilter.setEnabled(useSQLFilter.isSelected());
    }

    private void setEnabledBBoxFilter() {
        bboxFilter.setEnabled(useBBoxFilter.isSelected());
    }

    private void setEnabledFeatureFilter() {
        featureTypeFilter.setEnabled(useFeatureFilter.isSelected());
    }

    public void switchLocale() {
        resetPreferredSize();
        csvColumnsPanel.setTitle(Util.I18N.getString("spshg.csvcolumns.border"));
        templateLabel.setText(Util.I18N.getString("spshg.csvcolumns.usetemplate"));

        browseButton.setText(Util.I18N.getString("spshg.button.browse"));
        editTemplateButton.setText(Util.I18N.getString("spshg.button.edit"));
        manuallyTemplateButton.setText(Util.I18N.getString("spshg.button.new"));

        upButton.setText(Util.I18N.getString("spshg.button.up"));
        downButton.setText(Util.I18N.getString("spshg.button.down"));
        editButton.setText(Util.I18N.getString("spshg.button.edit"));
        addButton.setText(Util.I18N.getString("spshg.button.add"));
        removeButton.setText(Util.I18N.getString("spshg.button.remove"));
        saveButton.setText(Util.I18N.getString("spshg.button.save"));
        saveMessage.setText(Util.I18N.getString("spshg.csvcolumns.manual.save"));

        featureVersionPanel.setTitle(featureVersionFilter.getLocalizedTitle());
        attributeFilterPanel.setTitle(attributeFilter.getLocalizedTitle());
        sqlFilterPanel.setTitle(sqlFilter.getLocalizedTitle());
        bboxFilterPanel.setTitle(bboxFilter.getLocalizedTitle());
        featureFilterPanel.setTitle(featureTypeFilter.getLocalizedTitle());

        outputPanel.setTitle(Util.I18N.getString("spshg.outputPanel.border"));
        csvRadioButton.setText(Util.I18N.getString("spshg.csvPanel.border"));
        xlsxRadioButton.setText(Util.I18N.getString("spshg.xlsxPanel.border"));
        delimiterLabel.setText(Util.I18N.getString("spshg.csvPanel.delimiter"));

        browseOutputButton.setText(Util.I18N.getString("spshg.button.browse"));
        xlsxBrowseOutputButton.setText(Util.I18N.getString("spshg.button.browse"));
        separatorText.setText(Util.I18N.getString("spshg.csvPanel.delimiter.comma"));

        exportButton.setText(Util.I18N.getString("spshg.button.export"));

        if (tableDataModel != null) {
            tableDataModel.updateColumnsTitle();
            modifyTableColumnsSize();
        }

        int selected = delimiterComboBox.getSelectedIndex();
        delimiterComboBox.removeAllItems();
        Arrays.stream(Delimiter.values()).forEach(delimiterComboBox::addItem);
        delimiterComboBox.setSelectedIndex(selected);

        featureVersionFilter.doTranslation();
        attributeFilter.doTranslation();
        sqlFilter.doTranslation();
        featureTypeFilter.doTranslation();
        alignGUI();
    }

    private void alignGUI() {
        int rightHandMargin = Math.max(buttonsPanel.getPreferredSize().width, browseButton.getPreferredSize().width);
        buttonsPanel.setPreferredSize(new Dimension(rightHandMargin, buttonsPanel.getPreferredSize().height));
        browseButton.setPreferredSize(new Dimension(rightHandMargin, browseButton.getPreferredSize().height));
        saveButton.setPreferredSize(new Dimension(rightHandMargin, saveButton.getPreferredSize().height));
        scrollPane.setPreferredSize(new Dimension(browseText.getPreferredSize().width, 7 * 20));
        editTemplateButton.setPreferredSize(new Dimension(rightHandMargin, editTemplateButton.getPreferredSize().height));
    }

    private void modifyTableColumnsSize() {
        TableColumn column;
        table.setSurrendersFocusOnKeystroke(true);
        // title
        column = table.getColumnModel().getColumn(0);
        column.setMinWidth(30);
        column.setPreferredWidth(30);
        // content
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(140);
        column.setMinWidth(140);
    }

    private void resetPreferredSize() {
        buttonsPanel.setPreferredSize(null);
        browseButton.setPreferredSize(null);
        saveButton.setPreferredSize(null);
        browseOutputButton.setPreferredSize(null);
        xlsxBrowseOutputButton.setPreferredSize(null);
        scrollPane.setPreferredSize(null);
        editTemplateButton.setPreferredSize(null);
        manuallyTemplateButton.setPreferredSize(null);
    }

    private void clearGui() {
        csvRadioButton.setSelected(true);
        setOutputEnabledValues();
    }

    private void makeNewTemplate() {
        plugin.getConfig().getTemplate().setManualTemplate(true);
        tableDataModel.reset();
        setOutputEnabledValues();
        browseText.setEnabled(false);
    }

    public void modifyButtonsVisibility() {
        checkButtonsVisibilityInManualTemplate();

        if (table.getSelectedRowCount() != 1) {
            editButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            return;
        }

        if (table.getSelectionModel().getMaxSelectionIndex() == tableDataModel.getRowCount() - 1 &&
                tableDataModel.getRowCount() > 0) {
            downButton.setEnabled(false);

        }
        if (table.getSelectionModel().getMinSelectionIndex() == 0) {
            upButton.setEnabled(false);
        }
    }

    public void checkButtonsVisibilityInManualTemplate() {
        if (tableDataModel.getRowCount() == 0 || table.getSelectedRowCount() == 0) {
            editButton.setEnabled(false);
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
            upButton.setEnabled(true);
            downButton.setEnabled(true);
        }
    }

    private void doExport() {
        final ReentrantLock lock = this.mainLock;
        lock.lock();

        try {
            setSettings();
            viewController.clearConsole();

            // check all input values...
            // template
            ExportConfig config = plugin.getConfig();
            if (!config.getTemplate().isManualTemplate()) {
                if (config.getTemplate().getPath().trim().equals("")) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.template.file"));
                    return;
                }
            } else { // manually
                if (config.getTemplate().getColumnsList().size() == 0) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.template.mabually"));
                    return;
                }
            }

            if (config.getOutput().getType() == OutputFileType.CSV) {
                // csv file
                if (config.getOutput().getCsvFile().getOutputPath().trim().equals("")) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.csvout"));
                    return;
                }
            } else if (config.getOutput().getType() == OutputFileType.XLSX) {
                // xlsx file
                if (config.getOutput().getXlsxFile().getOutputPath().trim().equals("")) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.csvout"));
                    return;
                }
            }

            SimpleQuery query = config.getQuery();

            // feature version filter
            if (query.isUseFeatureVersionFilter()) {
                SimpleFeatureVersionFilter featureVersionFilter = query.getFeatureVersionFilter();

                if (featureVersionFilter.getMode() != SimpleFeatureVersionFilterMode.LATEST) {
                    if (!featureVersionFilter.isSetStartDate()) {
                        viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                                Language.I18N.getString("export.dialog.error.featureVersion.startDate"));
                        return;
                    }

                    if (featureVersionFilter.getMode() == SimpleFeatureVersionFilterMode.BETWEEN) {
                        if (!featureVersionFilter.isSetEndDate()) {
                            viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                                    Language.I18N.getString("export.dialog.error.featureVersion.endDate"));
                            return;
                        } else if (featureVersionFilter.getStartDate().compare(featureVersionFilter.getEndDate()) != DatatypeConstants.LESSER) {
                            viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                                    Language.I18N.getString("export.dialog.error.featureVersion.range"));
                            return;
                        }
                    }
                }
            }

            // attribute filter
            if (query.isUseAttributeFilter()) {
                SimpleAttributeFilter attributeFilter = query.getAttributeFilter();
                if (!attributeFilter.getResourceIdFilter().isSetResourceIds()
                        && !attributeFilter.getNameFilter().isSetLiteral()
                        && !attributeFilter.getLineageFilter().isSetLiteral()) {
                    viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                            Language.I18N.getString("common.dialog.error.incorrectData.attributes"));
                    return;
                }
            }

            // SQL filter
            if (query.isUseSQLFilter()
                    && !query.getSQLFilter().isSetValue()) {
                viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                        Language.I18N.getString("export.dialog.error.incorrectData.sql"));
                return;
            }

            // bbox
            if (query.isUseBboxFilter()) {
                BoundingBox bbox = query.getBboxFilter();
                Double xMin = bbox.getLowerCorner().getX();
                Double yMin = bbox.getLowerCorner().getY();
                Double xMax = bbox.getUpperCorner().getX();
                Double yMax = bbox.getUpperCorner().getY();

                if (xMin == null || yMin == null || xMax == null || yMax == null) {
                    viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                            Language.I18N.getString("common.dialog.error.incorrectData.bbox"));
                    return;
                }
            }

            // feature types
            if (query.isUseTypeNames() && query.getFeatureTypeFilter().getTypeNames().isEmpty()) {
                viewController.errorMessage(Language.I18N.getString("common.dialog.error.incorrectFilter"),
                        Language.I18N.getString("common.dialog.error.incorrectData.featureClass"));
                return;
            }

            if (!dbController.connect()) {
                return;
            }

            // warn the non-supported CityGML ADEs
            if (showADEWarningDialog() != JOptionPane.OK_OPTION) {
                log.warn("Table data export aborted.");
                return;
            }

            viewController.setStatusText(Util.I18N.getString("spshg.status.generation.start"));
            log.info("Initializing table data export...");

            // initialize event dispatcher
            final EventDispatcher eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
            final StatusDialog status = new StatusDialog(viewController.getTopFrame(),
                    Util.I18N.getString("spshg.dialog.status.title"),
                    Util.I18N.getString("spshg.dialog.status.state.start"),
                    Util.I18N.getString("spshg.dialog.status.message.start"),
                    true);

            SwingUtilities.invokeLater(() -> {
                status.setLocationRelativeTo(viewController.getTopFrame());
                status.setVisible(true);
            });

            status.getButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            eventDispatcher.triggerEvent(new InterruptEvent(
                                    "User abort of database export.",
                                    LogLevel.WARN,
                                    Event.GLOBAL_CHANNEL,
                                    this));
                        }
                    });
                }
            });

            boolean success = false;
            try {
                success = new SpreadsheetExporter(plugin.getConfig()).doProcess();
            } catch (TableExportException e) {
                log.error(e.getMessage(), e.getCause());
                if (e.getErrorCode() == ErrorCode.SPATIAL_INDEXES_NOT_ACTIVATED) {
                    log.error("Please use the database tab to activate the spatial indexes.");
                }
            }

            SwingUtilities.invokeLater(status::dispose);

            if (success) {
                log.info("Table data export successfully finished.");
            } else {
                log.warn("Table data export aborted.");
            }

            viewController.setStatusText(Language.I18N.getString("main.status.ready.label"));
        } finally {
            lock.unlock();
        }
    }

    //------------------ Manual Template
    // show dialog -add
    private void showAddNewColumnDialog(boolean isEdit) {
        final NewCSVColumnDialog csvColumnDialog;
        if (!isEdit)
            csvColumnDialog = new NewCSVColumnDialog(viewController, this);
        else {
            if (table.getSelectedRow() == -1 || table.getSelectedRowCount() > 1) return;
            CSVColumns csvColumn = tableDataModel.getCSVColumn(table.getSelectedRow());
            if (csvColumn == null) return;
            csvColumnDialog = new NewCSVColumnDialog(viewController, this, csvColumn);
        }

        SwingUtilities.invokeLater(() -> {
            csvColumnDialog.setLocationRelativeTo(viewController.getTopFrame());
            csvColumnDialog.setVisible(true);
        });
    }

    // add new row
    public void addNewColumnInManualCSV(CSVColumns csvColumns) {
        tableDataModel.addNewRow(csvColumns);
    }

    // edit an specific row
    public void editColumnInManualCSV(CSVColumns csvColumns) {
        tableDataModel.editRow(csvColumns);
    }

    // remove a selected row
    private void removeSelectedColumnFromManualTemplate() {
        tableDataModel.removeRow(table.getSelectedRows());
    }

    private void saveManuallyGeneratedTemplate() {
        JFileChooser fileChooserTemplate = createFileChooser(
                new FileNameExtensionFilter("Normal text file (*.txt)", "txt")
        );
        if (previousVisitBySaveTemplate != null && previousVisitBySaveTemplate.length() > 0) {
            fileChooserTemplate.setCurrentDirectory((new File(previousVisitBySaveTemplate)));
        }
        int result = fileChooserTemplate.showSaveDialog(getTopLevelAncestor());
        if (result == JFileChooser.CANCEL_OPTION) return;
        try {
            String exportString = fileChooserTemplate.getSelectedFile().toString();
            if (exportString.lastIndexOf('.') != -1 &&
                    exportString.lastIndexOf('.') > exportString.lastIndexOf(File.separator)) {
                exportString = exportString.substring(0, exportString.lastIndexOf('.'));
            }

            exportString = exportString + ".txt";
            previousVisitBySaveTemplate = exportString;
            TemplateWriter templateWriter = new TemplateWriter(exportString, tableDataModel);
            Thread t = new Thread(templateWriter);
            t.start();
        } catch (Exception e) {
            //
        }
    }

    private boolean choseTemplateFile() {
        JFileChooser fileChooserTemplate = createFileChooser(
                new FileNameExtensionFilter("Normal text file (*.txt)", "txt")
        );

        String previousVisit = plugin.getConfig().getTemplate().getLastVisitPath();
        if (previousVisit != null && previousVisit.length() > 0) {
            fileChooserTemplate.setCurrentDirectory((new File(previousVisit)));
        }
        int result = fileChooserTemplate.showOpenDialog(getTopLevelAncestor());
        if (result == JFileChooser.CANCEL_OPTION) return false;
        try {
            String exportString = fileChooserTemplate.getSelectedFile().toString();
            browseText.setText(exportString);
            plugin.getConfig().getTemplate().setLastVisitPath(fileChooserTemplate.getCurrentDirectory().getAbsolutePath());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void outputFile() {
        JFileChooser fileChooserCSVOut = createFileChooser(
                new FileNameExtensionFilter("Comma-separated Values Files (*.csv)", "csv")
        );

        String previousVisit = plugin.getConfig().getOutput().getCsvFile().getLastVisitPath();
        if (previousVisit != null && previousVisit.length() > 0) {
            fileChooserCSVOut.setCurrentDirectory((new File(previousVisit)).getParentFile());
        }
        int result = fileChooserCSVOut.showSaveDialog(getTopLevelAncestor());
        if (result == JFileChooser.CANCEL_OPTION) return;
        try {
            String exportString = fileChooserCSVOut.getSelectedFile().toString();
            if (exportString.lastIndexOf('.') != -1 &&
                    exportString.lastIndexOf('.') > exportString.lastIndexOf(File.separator)) {
                exportString = exportString.substring(0, exportString.lastIndexOf('.'));

            }

            exportString = exportString + ".csv";
            browseOutputText.setText(exportString);
            plugin.getConfig().getOutput().getCsvFile().setLastVisitPath(fileChooserCSVOut.getCurrentDirectory().getAbsolutePath());
        } catch (Exception e) {
            //
        }
    }

    private void xlsxOutputFile() {
        JFileChooser fileChooserXLSXOut = createFileChooser(
                new FileNameExtensionFilter("Microsoft Excel Files (*.xlsx)", "xlsx")
        );

        String previousVisit = plugin.getConfig().getOutput().getXlsxFile().getLastVisitPath();
        if (previousVisit != null && previousVisit.length() > 0) {
            fileChooserXLSXOut.setCurrentDirectory((new File(previousVisit)).getParentFile());
        }
        int result = fileChooserXLSXOut.showSaveDialog(getTopLevelAncestor());
        if (result == JFileChooser.CANCEL_OPTION) return;
        try {
            String exportString = fileChooserXLSXOut.getSelectedFile().toString();
            if (exportString.lastIndexOf('.') != -1 &&
                    exportString.lastIndexOf('.') > exportString.lastIndexOf(File.separator)) {
                exportString = exportString.substring(0, exportString.lastIndexOf('.'));

            }

            exportString = exportString + ".xlsx";
            xlsxBrowseOutputText.setText(exportString);
            plugin.getConfig().getOutput().getXlsxFile().setLastVisitPath(fileChooserXLSXOut.getCurrentDirectory().getAbsolutePath());
        } catch (Exception e) {
            //
        }
    }

    public void loadSettings() {
        ExportConfig config = plugin.getConfig();

        browseText.setText(config.getTemplate().getPath());
        config.getTemplate().setLastVisitPath(browseText.getText());

        browseOutputText.setText(config.getOutput().getCsvFile().getOutputPath());
        delimiterComboBox.setSelectedItem(Delimiter.fromValue(config.getOutput().getCsvFile().getDelimiter()));
        xlsxBrowseOutputText.setText(config.getOutput().getXlsxFile().getOutputPath());

        csvRadioButton.setSelected(true);
        if (config.getOutput().getType() == OutputFileType.XLSX)
            xlsxRadioButton.setSelected(true);

        SimpleQuery query = config.getQuery();
        useFeatureVersionFilter.setSelected(query.isUseFeatureVersionFilter());
        useAttributeFilter.setSelected(query.isUseAttributeFilter());
        useSQLFilter.setSelected(query.isUseSQLFilter());
        useBBoxFilter.setSelected(query.isUseBboxFilter());
        useFeatureFilter.setSelected(query.isUseTypeNames());

        featureVersionFilter.loadSettings(query.getFeatureVersionFilter());
        attributeFilter.loadSettings(query.getAttributeFilter());
        sqlFilter.loadSettings(query.getSQLFilter());
        bboxFilter.loadSettings(query.getBboxFilter());
        featureTypeFilter.loadSettings(query.getFeatureTypeFilter());

        setEnabledFeatureVersionFilter();
        setEnabledAttributeFilter();
        setEnabledSQLFilter();
        setEnabledBBoxFilter();
        setEnabledFeatureFilter();
        setOutputEnabledValues();

        GuiConfig guiConfig = config.getGuiConfig();
        featureVersionPanel.setCollapsed(guiConfig.isCollapseFeatureVersionFilter());
        attributeFilterPanel.setCollapsed(guiConfig.isCollapseAttributeFilter());
        sqlFilterPanel.setCollapsed(guiConfig.isCollapseSQLFilter());
        bboxFilterPanel.setCollapsed(guiConfig.isCollapseBoundingBoxFilter());
        featureFilterPanel.setCollapsed(guiConfig.isCollapseFeatureTypeFilter());
    }

    public void setSettings() {
        ExportConfig config = plugin.getConfig();

        config.getTemplate().setPath(browseText.getText());
        config.getTemplate().setColumnsList(tableDataModel.getRows());

        if (csvRadioButton.isSelected())
            config.getOutput().setType(OutputFileType.CSV);
        else if (xlsxRadioButton.isSelected())
            config.getOutput().setType(OutputFileType.XLSX);

        config.getOutput().getCsvFile().setOutputPath(browseOutputText.getText());
        config.getOutput().getCsvFile().setDelimiter(((Delimiter) delimiterComboBox.getSelectedItem()).getDelimiter());
        config.getOutput().getXlsxFile().setOutputPath(xlsxBrowseOutputText.getText());

        SimpleQuery query = config.getQuery();
        query.setUseFeatureVersionFilter(useFeatureVersionFilter.isSelected());
        query.setUseAttributeFilter(useAttributeFilter.isSelected());
        query.setUseSQLFilter(useSQLFilter.isSelected());
        query.setUseBboxFilter(useBBoxFilter.isSelected());
        query.setUseTypeNames(useFeatureFilter.isSelected());

        query.setFeatureVersionFilter(featureVersionFilter.toSettings());
        query.setAttributeFilter(attributeFilter.toSettings());
        query.setSQLFilter(sqlFilter.toSettings());
        query.setBboxFilter(bboxFilter.toSettings());
        query.setFeatureTypeFilter(featureTypeFilter.toSettings());

        GuiConfig guiConfig = config.getGuiConfig();
        guiConfig.setCollapseFeatureVersionFilter(featureVersionPanel.isCollapsed());
        guiConfig.setCollapseAttributeFilter(attributeFilterPanel.isCollapsed());
        guiConfig.setCollapseSQLFilter(sqlFilterPanel.isCollapsed());
        guiConfig.setCollapseBoundingBoxFilter(bboxFilterPanel.isCollapsed());
        guiConfig.setCollapseFeatureTypeFilter(featureFilterPanel.isCollapsed());
    }

    private void errorMessage(String title, String text) {
        JOptionPane.showMessageDialog(viewController.getTopFrame(), text, title, JOptionPane.ERROR_MESSAGE);
    }

    private int showADEWarningDialog() {
        int selectedOption = JOptionPane.OK_OPTION;
        List<ADEExtension> unsupported = ADEBalloonExtensionManager.getInstance().getUnsupportedADEExtensions();

        if (plugin.getConfig().getGuiConfig().isShowUnsupportedADEWarning() && !unsupported.isEmpty()) {
            String formattedMessage = MessageFormat.format(Util.I18N.getString("spshg.dialog.warning.ade.unsupported"),
                    org.citydb.util.Util.collection2string(unsupported.stream().map(ade -> ade.getMetadata().getName()).collect(Collectors.toList()), "<br>"));

            ConfirmationCheckDialog dialog = ConfirmationCheckDialog.defaults()
                    .withParentComponent(viewController.getTopFrame())
                    .withTitle(Language.I18N.getString("common.dialog.warning.title"))
                    .withOptionType(JOptionPane.YES_NO_OPTION)
                    .withMessageType(JOptionPane.WARNING_MESSAGE)
                    .addMessage(formattedMessage);

            selectedOption = dialog.show();
            plugin.getConfig().getGuiConfig().setShowUnsupportedADEWarning(dialog.keepShowingDialog());
        }

        return selectedOption;
    }

    public void loadExistingTemplate() {
        if (browseText.getText() == null || browseText.getText().trim().length() < 1)
            return;

        final File mTemplate = new File(browseText.getText().trim());
        if (!mTemplate.exists())
            return;

        Thread t = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(mTemplate), StandardCharsets.UTF_8))) {
                Translator translator = new Translator();
                String strLine;
                // Clean the table. Without notification.
                tableDataModel.reset();
                // Read File Line By Line
                // Initial values
                StringBuilder comment = new StringBuilder();
                String header;
                String content;
                while ((strLine = br.readLine()) != null) {
                    if (strLine.startsWith("//") || strLine.startsWith(";")) {
                        if (strLine.startsWith("//"))
                            comment.append(strLine.substring(2));
                        else // startsWith(";")
                            comment.append(strLine.substring(1));
                        comment.append(System.getProperty("line.separator"));
                        continue;
                    } else if (strLine.indexOf(':') > 0) {
                        header = strLine.substring(0, strLine.indexOf(':'));
                        content = strLine.substring(strLine.indexOf(':') + 1);
                    } else {
                        content = strLine;
                        header = translator.getProperHeader(content);
                    }
                    if (comment.length() > 0)
                        comment.setLength(comment.lastIndexOf(System.getProperty("line.separator")));
                    tableDataModel.addNewRow(new CSVColumns(header,
                            content, comment.substring(0),
                            translator.getFormatedDocument(content)));
                    comment.setLength(0);
                }
            } catch (IOException ioe) {
                //
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private boolean isFilePathValid(String path) {
        try {
            if (path == null || path.trim().length() == 0)
                return false;
            File f = new File(path);
            return f.exists();
        } catch (Exception e) {
            return false;
        }
    }

    private JFileChooser createFileChooser(FileNameExtensionFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        fileChooser.addChoosableFileFilter(fileChooser.getAcceptAllFileFilter());

        if (!browseText.getText().trim().isEmpty()) {
            File file = new File(browseText.getText().trim());
            if (!file.isDirectory())
                file = file.getParentFile();

            fileChooser.setCurrentDirectory(file);
        }

        return fileChooser;
    }
}
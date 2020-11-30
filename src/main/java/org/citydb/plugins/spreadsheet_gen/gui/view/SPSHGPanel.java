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

import org.citydb.config.i18n.Language;
import org.citydb.config.project.global.LogLevel;
import org.citydb.config.project.query.filter.type.FeatureTypeFilter;
import org.citydb.database.DatabaseController;
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.event.Event;
import org.citydb.event.EventDispatcher;
import org.citydb.event.EventHandler;
import org.citydb.gui.components.checkboxtree.DefaultCheckboxTreeCellRenderer;
import org.citydb.gui.components.common.TitledPanel;
import org.citydb.gui.components.feature.FeatureTypeTree;
import org.citydb.gui.util.GuiUtil;
import org.citydb.log.Logger;
import org.citydb.plugin.extension.view.ViewController;
import org.citydb.plugin.extension.view.components.BoundingBoxPanel;
import org.citydb.plugins.spreadsheet_gen.SPSHGPlugin;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.controller.SpreadsheetExporter;
import org.citydb.plugins.spreadsheet_gen.controller.TemplateWriter;
import org.citydb.plugins.spreadsheet_gen.database.Translator;
import org.citydb.plugins.spreadsheet_gen.events.EventType;
import org.citydb.plugins.spreadsheet_gen.events.InterruptEvent;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.NewCSVColumnDialog;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.StatusDialog;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.TableDataModel;
import org.citydb.plugins.spreadsheet_gen.util.Util;
import org.citydb.registry.ObjectRegistry;
import org.citygml4j.model.module.citygml.CityGMLVersion;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
public class SPSHGPanel extends JPanel implements EventHandler {
    protected static final int BORDER_THICKNESS = 3;
    private static final int PREFERRED_WIDTH = 560;
    private static final int PREFERRED_HEIGHT = 780;

    private final Logger log = Logger.getInstance();

    private final ViewController viewController;
    private final DatabaseController dbController;
    private final DatabaseConnectionPool dbPool;

    private final SPSHGPlugin plugin;

    private final ReentrantLock mainLock = new ReentrantLock();
    private JPanel jPanelInput;

    // +columns panel
    private TitledPanel csvColumnsPanel;
    private JLabel templateLabel = new JLabel("");
    private JPanel browsePanel;
    private JTextField browseText = new JTextField("");
    private JButton browseButton = new JButton("");
    private JButton editTemplateButton = new JButton("");
    private JButton manuallyTemplateButton = new JButton("");

    //-------------
    private JPanel contentSource;
    private JLabel gfPrefLabel = new JLabel("");
    private JTextArea generateDataFor = new JTextArea(2, 10);
    private JLabel editGenerateData = new JLabel();
    private JPopupMenu cityObjectPopup = new JPopupMenu();

    // feature type panel
    private FeatureTypeTree typeTree;
    private JPanel featureTreePanel;
    private TitledPanel featureFilterPanel;

    // +BBX Panel
    private BoundingBoxPanel bboxPanel;
    private TitledPanel bboxFilterPanel;

    //+Output Panel
    private TitledPanel outputPanel;
    private ButtonGroup outputButtonGroup = new ButtonGroup();

    //++ CSV RadioButtun
    private JRadioButton csvRadioButton = new JRadioButton("");
    private JPanel csvPanel;

    private JLabel browseOutputLabel = new JLabel("");
    private JTextField browseOutputText = new JTextField("");
    private JButton browseOutputButton = new JButton("");

    //	private JPanel advanceTemplate;
    private JLabel separatorLabel = new JLabel("");
    private JTextField separatorText = new JTextField("");
    private JComboBox<String> separatorComboBox;

    //++ xlsx RadioButtun
    private JRadioButton xlsxRadioButton = new JRadioButton("");
    private JPanel xlsxPanel;

    private JTextField xlsxBrowseOutputText = new JTextField("");
    private JButton xlsxBrowseOutputButton = new JButton("");

    //++ Online RadioButtun
    private JButton exportButton = new JButton("");
    private ConfigImpl config;

    // manual template generator
    private JPanel manualPanel;
    private JPanel rightHandMenu;
    private JScrollPane scrollPane;
    private JTable table;
    private TableDataModel tableDataModel = new TableDataModel();
    private JButton upButton = new JButton("");
    private JButton downButton = new JButton("");
    private JButton editButton = new JButton("");

    private JButton addButton = new JButton("");
    private JButton removeButton = new JButton("");
    private JButton saveButton = new JButton("");
    private JLabel saveMessage = new JLabel("");

    private String previousvisitBySaveTemplate = "";

    final JFileChooser fileChooserTemplate = new JFileChooser();
    final JFileChooser fileChooserCSVOut = new JFileChooser();
    final JFileChooser fileChooserXLSXOut = new JFileChooser();

    SPSHGPanel(ViewController viewController, SPSHGPlugin plugin) {
        this.viewController = viewController;
        this.plugin = plugin;
        dbController = ObjectRegistry.getInstance().getDatabaseController();
        dbPool = DatabaseConnectionPool.getInstance();
        ObjectRegistry.getInstance().getEventDispatcher().addEventHandler(EventType.UPLOAD_EVENT, this);
        ObjectRegistry.getInstance().getEventDispatcher().addEventHandler(org.citydb.event.global.EventType.DATABASE_CONNECTION_STATE, this);
        config = plugin.getConfig();

        initGui();
        addListeners();
        clearGui();
    }

    private void initGui() {
        csvColumnsPanel = new TitledPanel();
        Box insideCSVColumnsPanel = Box.createVerticalBox();

        JPanel templatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, BORDER_THICKNESS, BORDER_THICKNESS));
        templatePanel.add(templateLabel);

        browsePanel = new JPanel();
        browsePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = Util.setConstraints(0, 0, 1.0, 1.0, GridBagConstraints.HORIZONTAL, BORDER_THICKNESS, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS);
        gbc.gridwidth = 3;
        browsePanel.add(browseText, gbc);
        browseText.setColumns(10);
        browsePanel.add(browseButton, Util.setConstraints(3, 0, 0.0, 0.0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));

        JPanel buttonPanels = new JPanel();
        buttonPanels.setLayout(new GridBagLayout());
        JLabel jl = new JLabel();
        buttonPanels.add(jl, Util.setConstraints(0, 0, 1.0, 1.0, GridBagConstraints.BOTH, BORDER_THICKNESS, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS));
        gbc = Util.setConstraints(1, 0, 1.0, 1.0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, 0);
        gbc.anchor = GridBagConstraints.EAST;
        buttonPanels.add(manuallyTemplateButton, gbc);

        gbc = Util.setConstraints(0, 1, 1.0, 1.0, GridBagConstraints.BOTH, BORDER_THICKNESS, BORDER_THICKNESS * 6, 0, BORDER_THICKNESS);
        gbc.gridwidth = 3;
        browsePanel.add(buttonPanels, gbc);
        browsePanel.add(editTemplateButton, Util.setConstraints(3, 1, 0, 0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, 0, BORDER_THICKNESS));

        manualPanel = new JPanel();
        rightHandMenu = new JPanel();
        rightHandMenu.setLayout(new GridLayout(0, 1));

        // make a table
        table = new JTable(tableDataModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        modifyTableColumnsSize();

        scrollPane = new JScrollPane(table);

        rightHandMenu.add(addButton);
        rightHandMenu.add(removeButton);
        rightHandMenu.add(editButton);
        rightHandMenu.add(upButton);
        rightHandMenu.add(downButton);

        manualPanel.setLayout(new GridBagLayout());
        gbc = Util.setConstraints(0, 0, 1.0, 1.0, GridBagConstraints.HORIZONTAL, BORDER_THICKNESS, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS);
        gbc.gridwidth = 3;
        manualPanel.add(scrollPane, gbc);
        manualPanel.add(rightHandMenu, Util.setConstraints(3, 0, 0.0, 0.0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
        gbc = Util.setConstraints(0, 1, 1.0, 1.0, GridBagConstraints.HORIZONTAL, BORDER_THICKNESS, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS);
        gbc.gridwidth = 3;
        manualPanel.add(saveMessage, gbc);
        manualPanel.add(saveButton, Util.setConstraints(3, 1, 0.0, 0.0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));

        insideCSVColumnsPanel.add(templatePanel);
        insideCSVColumnsPanel.add(browsePanel);
        insideCSVColumnsPanel.add(manualPanel);
        manualPanel.setVisible(false);
        csvColumnsPanel.build(insideCSVColumnsPanel);

        //-------------------------------------------------
        contentSource = new JPanel(new BorderLayout());
        contentSource.setBorder(BorderFactory.createTitledBorder(""));
        Box countentSourceBox = Box.createVerticalBox();

        typeTree = new FeatureTypeTree(CityGMLVersion.v2_0_0, true);
        typeTree.setRowHeight((int)(new JCheckBox().getPreferredSize().getHeight()) - 1);
        DefaultCheckboxTreeCellRenderer renderer = (DefaultCheckboxTreeCellRenderer)typeTree.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        featureTreePanel = new JPanel();
        featureTreePanel.setLayout(new GridBagLayout());
        {
            featureTreePanel.add(typeTree, GuiUtil.setConstraints(0, 0, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        }
        featureFilterPanel = new TitledPanel();
        featureFilterPanel.build(featureTreePanel);

        gfPrefLabel.setAlignmentY(TOP_ALIGNMENT);
        generateDataFor.setAlignmentY(TOP_ALIGNMENT);
        editGenerateData.setAlignmentY(TOP_ALIGNMENT);

        bboxFilterPanel = new TitledPanel();
        bboxPanel = viewController.getComponentFactory().createBoundingBoxPanel();
        bboxFilterPanel.build(bboxPanel);

        outputPanel = new TitledPanel();

        // radio buttons
        outputButtonGroup.add(csvRadioButton);
        outputButtonGroup.add(xlsxRadioButton);
        csvRadioButton.setIconTextGap(10);
        xlsxRadioButton.setIconTextGap(10);
        csvRadioButton.setSelected(true);

        countentSourceBox.add(featureFilterPanel);
        countentSourceBox.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS)));
        countentSourceBox.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS)));
        countentSourceBox.add(bboxFilterPanel);
        contentSource.add(countentSourceBox, BorderLayout.CENTER);

        //--------------------------csv file
        JPanel csvRadioButtonPanel = new JPanel();
        Box outpuPanelBox = Box.createVerticalBox();
        csvRadioButtonPanel.setLayout(new BorderLayout());
        csvRadioButtonPanel.add(csvRadioButton, BorderLayout.WEST);

        separatorComboBox = new JComboBox<>();
        separatorComboBox.setPreferredSize(new Dimension(100, separatorComboBox.getPreferredSize().height));
        SeparatorPhrase.getInstance().load();
        SeparatorPhrase.getInstance().getNicknames().forEach(name -> separatorComboBox.addItem(name));

        csvPanel = new JPanel();
        csvPanel.setLayout(new GridBagLayout());

        gbc = Util.setConstraints(0, 0, 1.0, 1.0, GridBagConstraints.HORIZONTAL, BORDER_THICKNESS, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS);
        gbc.gridwidth = 3;
        csvPanel.add(browseOutputText, gbc);
        browseOutputText.setColumns(10);
        csvPanel.add(browseOutputButton, Util.setConstraints(3, 0, 0.0, 0.0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
        Box separatorPhraseBox = Box.createHorizontalBox();
        separatorPhraseBox.add(separatorLabel);
        separatorPhraseBox.add(Box.createRigidArea(new Dimension(BORDER_THICKNESS*3, 0)));
        separatorPhraseBox.add(separatorComboBox);
        csvPanel.add(separatorPhraseBox, Util.setConstraints(0, 1, 0, 0, GridBagConstraints.HORIZONTAL, 0, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS));

        //--------------------------xlsx file
        JPanel xlsxRadioButtonPanel = new JPanel();
        xlsxRadioButtonPanel.setLayout(new BorderLayout());
        xlsxRadioButtonPanel.add(xlsxRadioButton, BorderLayout.WEST);

        xlsxPanel = new JPanel();
        xlsxPanel.setLayout(new GridBagLayout());

        gbc = Util.setConstraints(0, 0, 1.0, 1.0, GridBagConstraints.HORIZONTAL, BORDER_THICKNESS, BORDER_THICKNESS * 6, BORDER_THICKNESS, BORDER_THICKNESS);
        gbc.gridwidth = 3;
        xlsxPanel.add(xlsxBrowseOutputText, gbc);
        xlsxBrowseOutputText.setColumns(10);
        xlsxPanel.add(xlsxBrowseOutputButton, Util.setConstraints(3, 0, 0.0, 0.0, GridBagConstraints.NONE, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));

        outpuPanelBox.add(csvRadioButtonPanel);
        outpuPanelBox.add(csvPanel);
        outpuPanelBox.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS*2)));
        outpuPanelBox.add(xlsxRadioButtonPanel);
        outpuPanelBox.add(xlsxPanel);
        outputPanel.build(outpuPanelBox);

        JPanel exportButtonPanel = new JPanel();
        exportButtonPanel.add(exportButton);

        jPanelInput = new JPanel();
        jPanelInput.setLayout(new GridBagLayout());
        {
            jPanelInput.add(csvColumnsPanel, GuiUtil.setConstraints(0, 0, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
            jPanelInput.add(countentSourceBox, GuiUtil.setConstraints(0, 1, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
            jPanelInput.add(outputPanel, GuiUtil.setConstraints(0, 2, 1, 0, GridBagConstraints.BOTH, 0, 0, 0, 0));
        }

        JPanel view = new JPanel();
        view.setLayout(new GridBagLayout());
        view.add(jPanelInput, GuiUtil.setConstraints(0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 10, 0, 10));

        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

        setLayout(new GridBagLayout());
        add(scrollPane, GuiUtil.setConstraints(0, 1, 1, 1, GridBagConstraints.BOTH, 10, 10, 10, 10));
        add(exportButton, GuiUtil.setConstraints(0, 2, 0, 0, GridBagConstraints.NONE, 10, 10, 10, 10));

        viewController.getComponentFactory().createPopupMenuDecorator().decorate(browseText, browseOutputText, separatorText);
        SwingUtilities.invokeLater(() -> initialzeFileChoosers());
    }

    public void switchLocale() {
        resetPreferedSize();
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

        contentSource.setBorder(BorderFactory.createTitledBorder(Util.I18N.getString("spshg.border.contentsource")));
        gfPrefLabel.setText("<html>" + Util.I18N.getString("spshg.contentsource.generatedatafor.prefix") + "</html>");

        featureFilterPanel.setTitle(Language.I18N.getString("filter.border.featureClass"));

        bboxFilterPanel.setTitle(Util.I18N.getString("spshg.bbxPanel.border"));

        outputPanel.setTitle(Util.I18N.getString("spshg.outputPanel.border"));
        csvRadioButton.setText(Util.I18N.getString("spshg.csvPanel.border"));
        xlsxRadioButton.setText(Util.I18N.getString("spshg.xlsxPanel.border"));
        separatorLabel.setText(Util.I18N.getString("spshg.csvPanel.separator"));

        browseOutputLabel.setText(Util.I18N.getString("spshg.csvPanel.browselabel"));
        browseOutputButton.setText(Util.I18N.getString("spshg.button.browse"));
        xlsxBrowseOutputButton.setText(Util.I18N.getString("spshg.button.browse"));
        separatorText.setText(Util.I18N.getString("spshg.csvPanel.separator.comma"));

        exportButton.setText(Util.I18N.getString("spshg.button.export"));
        //other GUI components
        if (tableDataModel != null) {
            tableDataModel.updateColumnsTitle();
            modifyTableColumnsSize();
        }

        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                SwingUtilities.invokeLater(this::updateComponentUI);
            }
        });

        alignGUI();
        updateComponentUI();
    }

    private void updateComponentUI() {
        featureTreePanel.setBorder(UIManager.getBorder("ScrollPane.border"));
    }

    private void alignGUI() {
        int righthandMargin = Math.max(rightHandMenu.getPreferredSize().width, browseButton.getPreferredSize().width);
        rightHandMenu.setPreferredSize(new Dimension(righthandMargin, rightHandMenu.getPreferredSize().height));
        browseButton.setPreferredSize(new Dimension(righthandMargin, browseButton.getPreferredSize().height));
        saveButton.setPreferredSize(new Dimension(righthandMargin, saveButton.getPreferredSize().height));
        browseOutputButton.setPreferredSize(new Dimension(righthandMargin, browseOutputButton.getPreferredSize().height));
        xlsxBrowseOutputButton.setPreferredSize(new Dimension(righthandMargin, xlsxBrowseOutputButton.getPreferredSize().height));
        scrollPane.setPreferredSize(new Dimension(browseText.getPreferredSize().width, 7 * 20));
        editTemplateButton.setPreferredSize(new Dimension(righthandMargin, editTemplateButton.getPreferredSize().height));
        manuallyTemplateButton.setPreferredSize(new Dimension(righthandMargin, manuallyTemplateButton.getPreferredSize().height));
    }

    private void modifyTableColumnsSize() {
        TableColumn column = null;
        table.setRowHeight(15);
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

    private void resetPreferedSize() {
        rightHandMenu.setPreferredSize(null);
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

    private void addListeners() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        doExport();
                    }
                };
                thread.start();
            }
        });

        editGenerateData.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cityObjectPopup.show(e.getComponent(), e.getX(), e.getY());
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });

        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (choseTemplateFile()) {
                    config.getTemplate().setManualTemplate(false);
                    setOutputEnabledValues();
                }

            }
        });

        browseOutputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputFile();
            }
        });

        xlsxBrowseOutputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xlsxOutputFile();
            }
        });

        ActionListener outputListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setOutputEnabledValues();
            }
        };

        csvRadioButton.addActionListener(outputListener);
        xlsxRadioButton.addActionListener(outputListener);

        editTemplateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFilePathValid(browseText.getText())) {
                    config.getTemplate().setManualTemplate(true);
                    loadExistingTemplate();
                    setOutputEnabledValues();
                } else if (choseTemplateFile()) {
                    config.getTemplate().setManualTemplate(true);
                    loadExistingTemplate();
                    setOutputEnabledValues();
                } else
                    config.getTemplate().setManualTemplate(false);

            }
        });

        manuallyTemplateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeNewTemplate();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddNewColumnDialog(false);
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedColumenFromManualTemolate();
            }
        });

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (table.getSelectedRowCount() > 1) return;
                int selectedRow = table.getSelectedRow();
                tableDataModel.move(selectedRow, true);
                selectedRow--;
                if (selectedRow >= 0)
                    table.setRowSelectionInterval(selectedRow, selectedRow);

            }
        });
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (table.getSelectedRowCount() > 1) return;
                int selectedRow = table.getSelectedRow();
                tableDataModel.move(selectedRow, false);
                selectedRow++;
                if (selectedRow <= table.getRowCount() - 1)
                    table.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                showAddNewColumnDialog(true);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
                    showAddNewColumnDialog(true);

            }
        });
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                modifyButtonsVisibility();
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                modifyButtonsVisibility();
//			}
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveManuallyGeneratedTemplate();

            }
        });
    }

    public void panelIsVisible(boolean b) {
        // nothing to do!
    }

    private void makeNewTemplate() {
        config.getTemplate().setManualTemplate(true);
        tableDataModel.reset();
        setOutputEnabledValues();
        browseText.setEnabled(false);
    }

    public void modifyButtonsVisibility() {
        checkButtonsVisibilityInManuallTemplate();
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

    public void checkButtonsVisibilityInManuallTemplate() {
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

            saveSettings();
            viewController.clearConsole();

            // check all input values...
            // template
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

            if (config.getFeatureTypeFilter().getTypeNames().size() == 0) {
                errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                        Util.I18N.getString("spshg.dialog.error.incompleteData.featureclass"));
                return;
            }

            // bbox
            if (!(config.getBoundingbox().getLowerCorner().isSetX() ||
                    config.getBoundingbox().getLowerCorner().isSetY() ||
                    config.getBoundingbox().getUpperCorner().isSetX() ||
                    config.getBoundingbox().getUpperCorner().isSetY())) {
                errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                        Util.I18N.getString("spshg.dialog.error.incompleteData.bbx"));
                return;
            }

            if (config.getOutput().getType().equals(Output.CSV_FILE_CONFIG)) {
                // csv file
                if (config.getOutput().getCsvfile().getOutputPath().trim().equals("")) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.csvout"));
                    return;
                }
                if (config.getOutput().getCsvfile().getSeparator().trim().equals("")) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.seperatorChar"));
                    return;
                }
                // check if the file exist
                try {
                    String filename = "";
                    String path = config.getOutput().getCsvfile().getOutputPath().trim();
                    if (path.lastIndexOf(File.separator) == -1) {
                        filename = path;
                        path = ".";
                    } else {
                        if (path.lastIndexOf(".") == -1) {
                            filename = path
                                    .substring(path.lastIndexOf(File.separator) + 1);
                        } else {
                            filename = path.substring(
                                    path.lastIndexOf(File.separator) + 1,
                                    path.lastIndexOf("."));
                        }
                        path = path.substring(0, path.lastIndexOf(File.separator));
                    }
                    File outputfile = new File(path + File.separator + filename + ".csv");

                    if (outputfile.exists()) {
                        int option = JOptionPane.showConfirmDialog(viewController.getTopFrame(),
                                String.format(Util.I18N.getString("spshg.dialog.error.csvfile.exist.message"), outputfile.getPath()),
                                Util.I18N.getString("spshg.dialog.error.csvfile.exist.title"),
                                JOptionPane.YES_NO_OPTION);
                        if (option != JOptionPane.YES_OPTION)
                            return;

                    }
                } catch (Exception e) {
                    errorMessage("Error",
                            "Error during creating the output CSV file.");
                }
            } else if (config.getOutput().getType() == Output.XLSX_FILE_CONFIG) {
                // xlsx file
                if (config.getOutput().getXlsxfile().getOutputPath().trim().equals("")) {
                    errorMessage(Util.I18N.getString("spshg.dialog.error.incompleteData"),
                            Util.I18N.getString("spshg.dialog.error.incompleteData.csvout"));
                    return;
                }

                // check if the file exist
                try {
                    String filename = "";
                    String path = config.getOutput().getXlsxfile().getOutputPath().trim();
                    if (path.lastIndexOf(File.separator) == -1) {
                        filename = path;
                        path = ".";
                    } else {
                        if (path.lastIndexOf(".") == -1) {
                            filename = path
                                    .substring(path.lastIndexOf(File.separator) + 1);
                        } else {
                            filename = path.substring(
                                    path.lastIndexOf(File.separator) + 1,
                                    path.lastIndexOf("."));
                        }
                        path = path.substring(0, path.lastIndexOf(File.separator));
                    }
                    File outputfile = new File(path + File.separator + filename + ".xlsx");

                    if (outputfile.exists()) {
                        int option = JOptionPane.showConfirmDialog(viewController.getTopFrame(),
                                String.format(Util.I18N.getString("spshg.dialog.error.csvfile.exist.message"), outputfile.getPath()),
                                Util.I18N.getString("spshg.dialog.error.csvfile.exist.title"),
                                JOptionPane.YES_NO_OPTION);
                        if (option != JOptionPane.YES_OPTION)
                            return;

                    }
                } catch (Exception e) {
                    errorMessage("Error",
                            "Error during creating the output XLSX file.");
                }
            }

            if (!dbPool.isConnected()) {

                dbController.connect(true);

                if (!dbController.isConnected())
                    return;
            }

            viewController.setStatusText(Util.I18N.getString("spshg.status.generation.start"));
            log.info(Util.I18N.getString("spshg.message.export.init"));

            // initialize event dispatcher
            final EventDispatcher eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
            final StatusDialog status = new StatusDialog(viewController.getTopFrame(),
                    Util.I18N.getString("spshg.dialog.status.title"),
                    Util.I18N.getString("spshg.dialog.status.state.start"),
                    Util.I18N.getString("spshg.dialog.status.message.start"),
                    null,
                    true);
            status.setSize(226, 140);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    status.setLocationRelativeTo(viewController.getTopFrame());
                    status.setVisible(true);
                }
            });

            SeparatorPhrase.getInstance().renewTempPhrase();
            SpreadsheetExporter exporter = new SpreadsheetExporter(plugin.getConfig());

            status.getButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            eventDispatcher.triggerEvent(new InterruptEvent(
                                    Util.I18N.getString("spshg.message.export.cancel"),
                                    LogLevel.INFO,
                                    this));
                        }
                    });
                }
            });

            boolean success = exporter.doProcess();

            try {
                eventDispatcher.flushEvents();
            } catch (InterruptedException e1) {
                //
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    status.dispose();
                }
            });

            // cleanup
            exporter.cleanup();

            if (success) {
                log.info(Util.I18N.getString("spshg.message.export.success"));
            } else {
                log.warn(Util.I18N.getString("spshg.message.export.abort"));
            }
            //log.info(exporter.getExportLog());
            viewController.setStatusText(Util.I18N.getString("main.status.ready.label"));
        } finally {
            lock.unlock();
        }
    }

    //------------------ Manual Template
    // show dialog -add
    private void showAddNewColumnDialog(boolean isedit) {
        final NewCSVColumnDialog csvColumnDialog;
        if (!isedit)
            csvColumnDialog = new NewCSVColumnDialog(viewController, this);
        else {
            if (table.getSelectedRow() == -1 || table.getSelectedRowCount() > 1) return;
            CSVColumns csvcolumn = tableDataModel.getCSVColumn(table.getSelectedRow());
            if (csvcolumn == null) return;
            csvColumnDialog = new NewCSVColumnDialog(viewController, this, csvcolumn);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                csvColumnDialog.setLocationRelativeTo(viewController.getTopFrame());
                csvColumnDialog.setVisible(true);
            }
        });
    }

    // add new row
    public void addNewColumnInManualCSV(CSVColumns csvc) {
        tableDataModel.addNewRow(csvc);

    }

    // edit an specific row
    public void editColumnInManualCSV(CSVColumns csvc) {
        tableDataModel.editRow(csvc);

    }

    // remove a selected row
    private void removeSelectedColumenFromManualTemolate() {
        tableDataModel.removeRow(table.getSelectedRows());
    }

    private void initialzeFileChoosers() {
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter("Normal text file (*.txt)", "txt");
        fileChooserTemplate.addChoosableFileFilter(filter1);
        fileChooserTemplate.addChoosableFileFilter(fileChooserTemplate.getAcceptAllFileFilter());
        fileChooserTemplate.setFileFilter(filter1);

        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Comma-separated Values Files (*.csv)", "csv");
        fileChooserCSVOut.addChoosableFileFilter(filter2);
        fileChooserCSVOut.addChoosableFileFilter(fileChooserCSVOut.getAcceptAllFileFilter());
        fileChooserCSVOut.setFileFilter(filter2);

        FileNameExtensionFilter filter3 = new FileNameExtensionFilter("Microsoft Excel Files (*.xlsx)", "xlsx");
        fileChooserXLSXOut.addChoosableFileFilter(filter3);
        fileChooserXLSXOut.addChoosableFileFilter(fileChooserXLSXOut.getAcceptAllFileFilter());
        fileChooserXLSXOut.setFileFilter(filter3);
    }

    private void saveManuallyGeneratedTemplate() {
        if (previousvisitBySaveTemplate != null && previousvisitBySaveTemplate.length() > 0) {
            fileChooserTemplate.setCurrentDirectory((new File(previousvisitBySaveTemplate)));
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
            previousvisitBySaveTemplate = exportString;
            TemplateWriter templateWriter = new TemplateWriter(exportString, tableDataModel);
            Thread t = new Thread(templateWriter);
            t.start();
        } catch (Exception e) {
            //
        }
    }

    private boolean choseTemplateFile() {
        String previousvisit = config.getTemplate().getLastVisitPath();
        if (previousvisit != null && previousvisit.length() > 0) {
            fileChooserTemplate.setCurrentDirectory((new File(previousvisit)));
        }
        int result = fileChooserTemplate.showOpenDialog(getTopLevelAncestor());
        if (result == JFileChooser.CANCEL_OPTION) return false;
        try {
            String exportString = fileChooserTemplate.getSelectedFile().toString();
            browseText.setText(exportString);
            config.getTemplate().setLastVisitPath(fileChooserTemplate.getCurrentDirectory().getAbsolutePath());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void outputFile() {
        String previousvisit = config.getOutput().getCsvfile().getLastVisitPath();
        if (previousvisit != null && previousvisit.length() > 0) {
            fileChooserCSVOut.setCurrentDirectory((new File(previousvisit)).getParentFile());
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
            config.getOutput().getCsvfile().setLastVisitPath(fileChooserCSVOut.getCurrentDirectory().getAbsolutePath());
        } catch (Exception e) {
            //
        }
    }

    private void xlsxOutputFile() {
        String previousvisit = config.getOutput().getXlsxfile().getLastVisitPath();
        if (previousvisit != null && previousvisit.length() > 0) {
            fileChooserXLSXOut.setCurrentDirectory((new File(previousvisit)).getParentFile());
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
            config.getOutput().getXlsxfile().setLastVisitPath(fileChooserXLSXOut.getCurrentDirectory().getAbsolutePath());
        } catch (Exception e) {
            //
        }
    }

    private void setOutputEnabledValues() {
        browseText.setEnabled(true);
        manualPanel.setVisible(config.getTemplate().isManualTemplate());
        if (config.getTemplate().isManualTemplate())
            checkButtonsVisibilityInManuallTemplate();

        browseOutputButton.setEnabled(csvRadioButton.isSelected());
        browseOutputText.setEnabled(csvRadioButton.isSelected());
        browseOutputLabel.setEnabled(csvRadioButton.isSelected());
        separatorLabel.setEnabled(csvRadioButton.isSelected());
        separatorText.setEnabled(csvRadioButton.isSelected());
        separatorComboBox.setEnabled(csvRadioButton.isSelected());

        xlsxBrowseOutputButton.setEnabled(xlsxRadioButton.isSelected());
        xlsxBrowseOutputText.setEnabled(xlsxRadioButton.isSelected());
    }

    public Dimension getPreferredSize() {
        return new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    public void loadSettings() {
        config = plugin.getConfig();
        if (config == null) return;

        FeatureTypeFilter featureTypeFilter = config.getFeatureTypeFilter();
        typeTree.getCheckingModel().clearChecking();
        typeTree.setSelected(featureTypeFilter.getTypeNames());

        browseText.setText(config.getTemplate().getPath());
        config.getTemplate().setLastVisitPath(browseText.getText());

        bboxPanel.setBoundingBox(config.getBoundingbox());

        browseOutputText.setText(config.getOutput().getCsvfile().getOutputPath());
        separatorComboBox.setSelectedItem(config.getOutput().getCsvfile().getSeparator());
        xlsxBrowseOutputText.setText(config.getOutput().getXlsxfile().getOutputPath());

        csvRadioButton.setSelected(true);
        if (config.getOutput().getType().equalsIgnoreCase(Output.XLSX_FILE_CONFIG))
            xlsxRadioButton.setSelected(true);

        setOutputEnabledValues();
    }

    public void saveSettings() {
        if (config == null) return;

        config.getTemplate().setPath(browseText.getText());
        config.getTemplate().setColumnsList(tableDataModel.getRows());

        // feature type filter
        FeatureTypeFilter featureTypeFilter = config.getFeatureTypeFilter();
        featureTypeFilter.reset();
        featureTypeFilter.setTypeNames(typeTree.getSelectedTypeNames());

        config.setBoundingbox(bboxPanel.getBoundingBox());

        if (csvRadioButton.isSelected())
            config.getOutput().setType(Output.CSV_FILE_CONFIG);
        else if (xlsxRadioButton.isSelected())
            config.getOutput().setType(Output.XLSX_FILE_CONFIG);

        config.getOutput().getCsvfile().setOutputPath(browseOutputText.getText());
        config.getOutput().getCsvfile().setSeparator((String)separatorComboBox.getSelectedItem());
        config.getOutput().getXlsxfile().setOutputPath(xlsxBrowseOutputText.getText());
    }

    private void errorMessage(String title, String text) {
        JOptionPane.showMessageDialog(viewController.getTopFrame(), text, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void handleEvent(Event e) throws Exception {
        //
    }

    public void loadExistingTemplate() {
        if (browseText.getText() == null || browseText.getText().trim().length() < 1)
            return;
        final File mTemplate = new File(browseText.getText().trim());
        if (mTemplate == null || !mTemplate.exists())
            return;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileInputStream fstream = new FileInputStream(mTemplate);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
                    String strLine;
                    // Clean the table. Without notification.
                    tableDataModel.reset();
                    // Read File Line By Line
                    // Initial values
                    StringBuffer comment = new StringBuffer();
                    String header = "";
                    String content = "";
                    while ((strLine = br.readLine()) != null) {
                        try {
                            header = "";
                            content = "";
                            if (strLine.startsWith("//") || strLine.startsWith(";")) {
//							if (comment.length()>0)
//								comment.append(System.getProperty("line.separator"));
                                if (strLine.startsWith("//"))
                                    comment.append(strLine.substring(2));
                                else // startsWith(";")
                                    comment.append(strLine.substring(1));
                                comment.append(System.getProperty("line.separator"));
                                continue;
                            } else if (strLine.indexOf(':') > 0) {
                                header = strLine.substring(0, strLine.indexOf(':'));
                                content = strLine.substring(strLine.indexOf(':') + 1,
                                        strLine.length());
                            } else {
                                content = strLine;
                                header = Translator.getInstance().getProperHeader(content);
                            }
                        } catch (Exception e) {
                        }
                        if (comment.length() > 0)
                            comment.setLength(comment.lastIndexOf(System.getProperty("line.separator")));
                        tableDataModel.addNewRow(new CSVColumns(header,
                                content, comment.substring(0),
                                Translator.getInstance().getFormatedDocument(content)));
                        comment.setLength(0);
                    }
                } catch (IOException ioe) {
                }
                ;

            }
        });
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

}
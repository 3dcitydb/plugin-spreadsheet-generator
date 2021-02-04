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
package org.citydb.plugins.spreadsheet_gen.gui.view.components;

import org.citydb.gui.util.GuiUtil;
import org.citydb.modules.kml.util.BalloonTemplateHandler;
import org.citydb.plugin.extension.view.ViewController;
import org.citydb.plugins.spreadsheet_gen.database.Translator;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.view.SPSHGPanel;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class NewCSVColumnDialog extends JDialog {
	private static final int PREFERRED_WIDTH = 50;
	private static final int PREFERRED_HEIGHT = 50;
	public static final String EOL="[EOL]";
	
	private final ViewController viewController;
	
	private JTextField titleText;
	private JTextArea commentText;
	private JTextPane content;
	private JButton addFieldButton, insertButton, cancelButton, functionButton,eolButton;
	private JTree tree;
	private CSVColumns newCSVColumn;

	private StyledDocument document;
	private static Style labelStyle,defaultStyle,EOLStyle;
	private int caretPositionDot;
	private int caretPositionMark;
	private boolean isEditMode;

	private JPopupMenu popup;
	private Set<String> aggregations;
	private HashMap<String, Set<String>> _3dcitydbcontent;
	final SPSHGPanel panel;

	// Highlight
	final Highlighter hilit;
	final Highlighter.HighlightPainter painter;
	private Color highlightColor = Color.lightGray;
	
	private String oldTreeSelectedPaths=null;
	public NewCSVColumnDialog(ViewController viewController, SPSHGPanel panel) {
		super(viewController.getTopFrame(), Util.I18N.getString("spshg.dialog.addnewcolumn.header"), true);
		hilit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);

		this.viewController = viewController;
		this.panel = panel;
		newCSVColumn = new CSVColumns();
		isEditMode = false;
		init();
	}

	public NewCSVColumnDialog(ViewController viewController, SPSHGPanel panel, CSVColumns ncsvc) {
		super(viewController.getTopFrame(), Util.I18N.getString("spshg.dialog.addnewcolumn.header"), true);
		hilit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);

		this.viewController = viewController;
		this.panel = panel;
		newCSVColumn = ncsvc;
		isEditMode = true;
		init();
	}

	private void init() {
		BalloonTemplateHandler dummy = new BalloonTemplateHandler("", null);
		aggregations = dummy.getSupportedAggregationFunctions();
		_3dcitydbcontent = dummy.getSupportedTablesAndColumns();
		
		this.setSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new GridBagLayout());
		{
			JLabel titleLabel = new JLabel(Util.I18N.getString("spshg.dialog.addnewcolumn.title"));
			titleText = new JTextField(newCSVColumn.title);
			titlePanel.add(titleLabel, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.NONE, 0, 0, 0, 0));
			titlePanel.add(titleText, GuiUtil.setConstraints(1, 0, 1, 0, GridBagConstraints.HORIZONTAL, 0, 10, 0, 0));
		}

		// right box
		JLabel contentLabel = new JLabel(Util.I18N.getString("spshg.dialog.addnewcolumn.content"));
		JPanel contentLabelPanel = new JPanel(new BorderLayout());
		contentLabelPanel.add(contentLabel, BorderLayout.WEST);

		document= newCSVColumn.document;
		getDefaultStyle();
		getLabelStyle();
		getEOLStyle();		
		
		content = new JTextPane(document);		
		
		content.setSize(PREFERRED_WIDTH * 3 / 7, 300);
		content.setPreferredSize(new Dimension(150, 250));
		content.setEditable(true);
		caretPositionDot = caretPositionMark = 0;
		content.setCaretPosition(caretPositionDot);
		content.setFont(new Font("Tahoma", Font.PLAIN, 12));
		content.getCaret().setSelectionVisible(true);
		content.setHighlighter(hilit);
		
		JScrollPane jspContent = new JScrollPane(content);

		// right box
		JLabel availableFiledsLabel = new JLabel(Util.I18N.getString("spshg.dialog.addnewcolumn.avilablefromdb"));
		JPanel availableFiledPanel = new JPanel(new BorderLayout());
		availableFiledPanel.add(availableFiledsLabel, BorderLayout.WEST);
		tree = generateTree();
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		
		JScrollPane treescrollPane = new JScrollPane(tree);

		jspContent.setPreferredSize(treescrollPane.getPreferredSize());
		treescrollPane.setPreferredSize(jspContent.getPreferredSize());
		jspContent.setMinimumSize(treescrollPane.getPreferredSize());
		treescrollPane.setMinimumSize(jspContent.getPreferredSize());

		Box rigthHandBox = Box.createVerticalBox();
		rigthHandBox.add(contentLabelPanel);
		rigthHandBox.add(Box.createRigidArea(new Dimension(0, 3)));
		rigthHandBox.add(jspContent);

		JPanel centralBox = new JPanel();
		centralBox.setLayout(new GridLayout(0, 1, 0, 5));
		addFieldButton = new JButton(Util.I18N.getString("spshg.dialog.addnewcolumn.addbutton"));
		functionButton = new JButton(Util.I18N.getString("spshg.dialog.addnewcolumn.funcaddbutton"));
		eolButton = new JButton(Util.I18N.getString("spshg.dialog.addnewcolumn.eolbutton"));
		eolButton.setToolTipText(Util.I18N.getString("spshg.dialog.addnewcolumn.tooltip.eol"));
		functionButton.setToolTipText(Util.I18N.getString("spshg.dialog.addnewcolumn.tooltip.funcaddbutton"));
		addFieldButton.setToolTipText(Util.I18N.getString("spshg.dialog.addnewcolumn.tooltip.addbutton"));
		
		centralBox.add(addFieldButton);
		centralBox.add(functionButton);
		centralBox.add(eolButton);

		Box leftHandBox = Box.createVerticalBox();
		leftHandBox.add(availableFiledPanel);
		leftHandBox.add(Box.createRigidArea(new Dimension(0, 3)));
		leftHandBox.add(treescrollPane);

		JPanel centralcontentPanel = new JPanel();
		centralcontentPanel.setLayout(new GridBagLayout());
		{
			centralcontentPanel.add(leftHandBox, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.NONE, 0, 0, 0, 0));
			centralcontentPanel.add(centralBox, GuiUtil.setConstraints(1, 0, 0, 0, GridBagConstraints.NONE, 0, 15, 0, 15));
			centralcontentPanel.add(rigthHandBox, GuiUtil.setConstraints(2, 0, 0, 0, GridBagConstraints.NONE, 0, 0, 0, 0));
		}

		JLabel commentLabel = new JLabel(Util.I18N.getString("spshg.dialog.addnewcolumn.comment"));
		JPanel commentLabellPanel = new JPanel(new BorderLayout());
		commentLabellPanel.add(commentLabel, BorderLayout.WEST);

		commentText = new JTextArea(3, 10);
		commentText.setLineWrap(true);
		commentText.setText(newCSVColumn.comment);
		JScrollPane commentscroll = new JScrollPane(commentText);

		JPanel southPanel = new JPanel(new BorderLayout());
		Box southPanelBox= Box.createHorizontalBox();
		southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		if (isEditMode)
			insertButton = new JButton(Util.I18N.getString("spshg.dialog.addnewcolumn.edit"));
		else
			insertButton = new JButton(Util.I18N.getString("spshg.dialog.addnewcolumn.insert"));
		cancelButton = new JButton(Util.I18N.getString("common.button.cancel"));
		southPanelBox.add(insertButton);
		southPanelBox.add(Box.createRigidArea(new Dimension(10, 0)));
		southPanelBox.add(cancelButton);
		southPanel.add(southPanelBox,BorderLayout.EAST);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		{
			mainPanel.add(titlePanel, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.HORIZONTAL, 15, 10, 0, 10));
			mainPanel.add(centralcontentPanel, GuiUtil.setConstraints(0, 1, 1, 1, GridBagConstraints.BOTH, 10, 10, 0, 10));
			mainPanel.add(commentLabellPanel, GuiUtil.setConstraints(0, 2, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, 10, 10, 0, 10));
			mainPanel.add(commentscroll, GuiUtil.setConstraints(0, 3, 1, 0, GridBagConstraints.HORIZONTAL, 3, 10, 0, 10));
			mainPanel.add(southPanel, GuiUtil.setConstraints(0, 4, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, 15, 10, 10, 10));
		}

		setLayout(new GridBagLayout());
		add(mainPanel, GuiUtil.setConstraints(0, 0, 1, 1, GridBagConstraints.BOTH, 0, 0, 0, 0));

		makeFunctionPopup();

		viewController.getComponentFactory().createPopupMenuDecorator().decorate(titleText, content, commentText);
		
		addListeners();
        pack();
        // enforces the minimum size of both frame and component
        setMinimumSize(getMinimumSize());
        setPreferredSize(getPreferredSize());
	}

	private void addListeners() {
		// UI listeners
		tree.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {}
			

			public void mousePressed(MouseEvent e) {}
			

			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2)
					addFieldFromDB(null);
				else{
					if (oldTreeSelectedPaths!=null && tree.getSelectionPath()!=null
							&&tree.getSelectionPath().toString().trim().equalsIgnoreCase(oldTreeSelectedPaths))
						tree.clearSelection();
				}
				if (tree.getSelectionPath()!=null)
					oldTreeSelectedPaths= tree.getSelectionPath().toString().trim();
			}


			public void mouseEntered(MouseEvent e) {}
		});

		content.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				
				caretPositionDot = e.getDot();
				caretPositionMark = e.getMark();
		}});
		

		content.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				content.setCharacterAttributes(defaultStyle, true);
			}
			
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()== KeyEvent.VK_ENTER){
					insertEOL();
					e.consume();
				}
				
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()== KeyEvent.VK_ENTER)
					e.consume();
			}
		});
		
		content.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				try {
					
					hilit.addHighlight(content.getSelectionStart(),content.getSelectionEnd(), painter);
				} catch (BadLocationException e1) {
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				hilit.removeAllHighlights();
			}
		});
		
		
		//---
		addFieldButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addFieldFromDB(null);
			}
		});

		functionButton.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}

			public void mousePressed(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {}

			public void mouseEntered(MouseEvent arg0) {	}

			public void mouseClicked(MouseEvent arg0) { }
		});
		eolButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				insertEOL();
			}
		});
		insertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String title = titleText.getText();
				String cont = content.getText();

				if (title != null && title.trim().length() == 0){
					title= Translator.getInstance().getProperHeader(cont);
					
				}
					
				if (title != null && title.trim().length() > 0
						&& (title.startsWith(";") || title.startsWith("\\"))) {
					errorMessage(
							"Error",
							Util.I18N
									.getString("spshg.dialog.addnewcolumn.error.title.start"));
					return;
				}
				
				if (title != null && title.trim().length() == 0) {
					errorMessage("Error", Util.I18N
							.getString("spshg.dialog.addnewcolumn.error.title"));
					return;
				}
				if (cont!=null && cont.trim().length()==0) {
					errorMessage(
							"Error",
							Util.I18N
									.getString("spshg.dialog.addnewcolumn.error.content"));
					return;
				}
				if (cont.startsWith(":")) {
					errorMessage(
							"Error",
							Util.I18N
									.getString("spshg.dialog.addnewcolumn.error.content.start"));
					return;
				}
				newCSVColumn.setValues(title, cont, commentText.getText(),document);
				if (isEditMode)
					panel.editColumnInManualCSV(newCSVColumn);
				else
					panel.addNewColumnInManualCSV(newCSVColumn);
				setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
			}
		});
	}
	
	private void makeFunctionPopup() {
		popup = new JPopupMenu();
		JMenuItem menuItem;
		for (String func:aggregations) {
			menuItem = new JMenuItem(func);
			menuItem.addActionListener(new PopupActionListener(this, "["
					+ func + "]"));
			popup.add(menuItem);
		}
	}
	
	public void insertInContent(String st){
		try {
			if (caretPositionDot != caretPositionMark)
				document.remove(Math.min(caretPositionDot, caretPositionMark) , 
						Math.abs(caretPositionMark-caretPositionDot));

				document.insertString(caretPositionDot,st, labelStyle);
				content.setCharacterAttributes(defaultStyle, true);
				content.requestFocus();
				
				//tree.clearSelection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	
	public void insertEOL(){
		try {
			if (caretPositionDot != caretPositionMark)
				document.remove(Math.min(caretPositionDot, caretPositionMark) , 
						Math.abs(caretPositionMark-caretPositionDot));

				document.insertString(caretPositionDot,EOL, EOLStyle);
				content.setCharacterAttributes(defaultStyle, true);
				content.requestFocus();
				
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	
	public void addFieldFromDB(String aggr) {
		TreePath[] paths = tree.getSelectionPaths();
		String result;
		if (paths == null || paths.length < 1){
			replaceAggregationFunction(aggr);
			return;
		}
		String st = paths[0].toString().trim();
		if (st.matches(".*,.*,.*")) {
			String cont = st.substring(st.indexOf(',') + 1, st.length() - 1)
					.trim();
			cont = cont.replaceAll(", ", "/");
			if (aggr!=null)
				result = cont.replaceAll("/", "/"+aggr);
			else 
				result = cont;
			
			insertInContent(result);
		}
	}
	
	private void replaceAggregationFunction(String aggr){
		try {
			if (caretPositionDot == caretPositionMark) // nothing selected.
				return ;
			if (aggr==null || aggr.length()<1) // there is not any aggregation fuction selected
				return ;

			int start= Math.min(content.getSelectionStart(),content.getSelectionEnd());
			int len =Math.max(content.getSelectionStart(),content.getSelectionEnd())-start;
			
			String selectedText="";
			try{
			 selectedText=document.getText(start,len);
			}catch(Exception e){e.printStackTrace(); return;}

			String tmp;
					
			int index=-1;
			boolean aggIsFound=false;
			// replace
			for (String func:aggregations) {
				if (aggr.contains(func)) continue;
				tmp ="["+func+"]";
				while( (index= selectedText.indexOf(tmp))>=0 ){
					document.remove(start+index,tmp.length());
					document.insertString(start+index,aggr, labelStyle);
					selectedText=selectedText.replaceFirst("\\Q"+tmp+"\\E", aggr);
					aggIsFound=true;
				}
			}

			
			if (!aggIsFound){// add new one
				index=-1;
				while( (index= selectedText.indexOf("/",index+1))>=0 ){
					if (selectedText.regionMatches(index, "/[", 0, 2))
						continue;
					document.insertString(start+index+1,aggr, labelStyle);
					len=len+aggr.length();
					selectedText=document.getText(start,len);
				}

			}
			
		} catch (BadLocationException e) {
		}
	}
	
	

	private JTree generateTree() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("3DCityDB");
		createNodes(top);
		return new JTree(top);
	}

	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode node = null;
		if (_3dcitydbcontent == null)
			return;
		TreeSet<String> tableNames = new TreeSet<String>(_3dcitydbcontent.keySet());

		Set<String> columnNames;
		for (String name : tableNames) {
			category = new DefaultMutableTreeNode(new DBTable(name));
			top.add(category);
			columnNames = _3dcitydbcontent.get(name); // new
														// TreeSet<String>(_3dcitydbcontent.get(name));
			for (String cname : columnNames) {
				node = new DefaultMutableTreeNode(new DBColumn(cname, name));
				category.add(node);
			}

		}
	}

	private void errorMessage(String title, String text) {
		JOptionPane.showMessageDialog(this, text, title,
				JOptionPane.ERROR_MESSAGE);
	}
	
	public static Style getDefaultStyle(){
		if (defaultStyle==null){
			StyleContext context = new StyleContext();
			defaultStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
			StyleConstants.setAlignment(defaultStyle, StyleConstants.ALIGN_LEFT);
			StyleConstants.setSpaceAbove(defaultStyle, 4);
			StyleConstants.setSpaceBelow(defaultStyle, 4);
			StyleConstants.setFontFamily(defaultStyle, "Tahoma");
			StyleConstants.setFontSize(defaultStyle, 12);
		}
		return defaultStyle;
	}
	
	public static Style getLabelStyle(){
		if (labelStyle!=null)
			return labelStyle;
		if (defaultStyle==null)
			getDefaultStyle();
		StyleContext context = new StyleContext();
		labelStyle = context.addStyle("Label", defaultStyle);
		StyleConstants.setForeground(labelStyle, Color.red);
		StyleConstants.setBackground(labelStyle, Color.yellow);
		return labelStyle;
	}
			
	public static Style getEOLStyle(){
		if (EOLStyle!=null)
			return EOLStyle;
		if (defaultStyle==null)
			getDefaultStyle();
		StyleContext context = new StyleContext();
		EOLStyle = context.addStyle("EOL", defaultStyle);
		StyleConstants.setForeground(EOLStyle, Color.black);
		StyleConstants.setBackground(EOLStyle, Color.lightGray);
		return EOLStyle;
	}
	
}

class DBTable {
	public String tableName;

	public DBTable(String name) {
		tableName = name;
	}

	public String toString() {
		return tableName;
	}
}

class DBColumn {
	public String fieldName;
	public String parentTable;

	public DBColumn(String name, String parentTableName) {
		fieldName = name;
		parentTable = parentTableName;
	}

	public String toString() {
		return fieldName;
	}

	public String getFullName() {
		return parentTable + "/" + fieldName;
	}
}

class PopupActionListener implements ActionListener {

	String funcName;
	NewCSVColumnDialog frame;

	public PopupActionListener(NewCSVColumnDialog frame, String functName) {
		this.funcName = functName;
		this.frame = frame;
	}

	public void actionPerformed(ActionEvent e) {
		frame.addFieldFromDB(funcName);
	}

}



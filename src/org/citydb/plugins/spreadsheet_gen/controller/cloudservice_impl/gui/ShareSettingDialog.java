/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2017
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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudServiceRegistery;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.GoogleSpreadSheetService;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.ShareSettingController;
import org.citydb.plugins.spreadsheet_gen.events.EventType;
import org.citydb.plugins.spreadsheet_gen.events.SharingEvent;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import org.citydb.api.controller.ViewController;
import org.citydb.api.event.Event;
import org.citydb.api.event.EventDispatcher;
import org.citydb.api.event.EventHandler;
import org.citydb.api.registry.ObjectRegistry;


@SuppressWarnings("serial")
public class ShareSettingDialog extends JDialog implements EventHandler{
	protected static final int BORDER_THICKNESS = 5;
	protected static final int MAX_TEXTFIELD_HEIGHT = 20;
	protected static final int MAX_LABEL_WIDTH = 60;
	private static final int PREFERRED_WIDTH = 400;
	private static final int PREFERRED_HEIGHT = 410;
	
	private final ViewController viewController;
	private JComboBox accessSettings = new JComboBox();
	
	private SharingListTableDataModel tableDataModel=new SharingListTableDataModel();
	private JTable table ;
	private JScrollPane scrollPane ;
	
	private JTextArea emailText=new JTextArea();
	private JComboBox scopeComboBox;
	private JButton addNewPersonButton = new JButton("");
	private JProgressBar progressBar;
	private JButton okButton=new JButton("");

	
	final GoogleSpreadSheetService gsss;
	
	private Users currentUser;
	final EventDispatcher eventDispatcher;
	public int selectedScopeIndex=0;
	
	public ShareSettingDialog(JFrame frame) {
		super(frame, Util.I18N.getString("spshg.dialog.sharesettings.dialogtitle"), true);
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		eventDispatcher.addEventHandler(EventType.SHARING_EVENT, this);
		viewController = ObjectRegistry.getInstance().getViewController();
		gsss= (GoogleSpreadSheetService)CloudServiceRegistery.getInstance().getSelectedService();
		init();
	}


	private void init() {
		this.setSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		Box mainPanel = Box.createVerticalBox();

		JLabel whohasaccess = new JLabel(
				Util.I18N.getString("spshg.dialog.sharesettings.whohasaccess"));

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.add(whohasaccess, BorderLayout.WEST);
		
		initializeScopeComboBox();
		// make a table
		table = new JTable(tableDataModel);
		table.setPreferredScrollableViewportSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT*2/5));
	    table.setFillsViewportHeight(true);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		TableColumn column = null;
		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//REMOVE
		column = table.getColumnModel().getColumn(2);
		column.setPreferredWidth(10);

		// permission
		column = table.getColumnModel().getColumn(1); 
		column.setPreferredWidth(50);

		column.setCellEditor(new DefaultCellEditor(Users.getPermissionComboBox()));

		// email
		column = table.getColumnModel().getColumn(0); 
		column.setPreferredWidth(150);
				
		scrollPane = new JScrollPane(table);
		table.setCellSelectionEnabled(true);
		table.getSelectionModel().addListSelectionListener(new TableSelectionListener(this));
		table.getColumnModel().getSelectionModel().
        addListSelectionListener(new TableSelectionListener(this));
		table.setShowVerticalLines(false);	

		JLabel addPeople = new JLabel(
				Util.I18N.getString("spshg.dialog.sharesettings.addpeople"));
		
		JPanel addPeoplePanel = new JPanel(new BorderLayout());
		addPeoplePanel.add(addPeople, BorderLayout.WEST);
		scopeComboBox = Users.getPermissionComboBox();
		addNewPersonButton.setText(Util.I18N.getString("spshg.dialog.sharesettings.addperson"));
		
		
		JPanel addPanel = new JPanel();
		addPanel.setLayout(new GridBagLayout());
		
		emailText.setLineWrap(true);
		emailText.setWrapStyleWord(true);
		emailText.setRows(2);
		addPanel.add(new JScrollPane(emailText),Util.setConstraints(0,0,1.0,1.0,GridBagConstraints.BOTH,BORDER_THICKNESS,0,BORDER_THICKNESS,BORDER_THICKNESS));
		addPanel.add(scopeComboBox,Util.setConstraints(GridBagConstraints.PAGE_START,1,0,0,0,GridBagConstraints.NONE,BORDER_THICKNESS,BORDER_THICKNESS,BORDER_THICKNESS,BORDER_THICKNESS));
		addPanel.add(addNewPersonButton,Util.setConstraints(GridBagConstraints.PAGE_START,2,0,0,0,GridBagConstraints.NONE,BORDER_THICKNESS-2,BORDER_THICKNESS,BORDER_THICKNESS,0));				
		emailTextFieldLostFocus();

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButton.setText(
					Util.I18N.getString("spshg.dialog.sharesettings.ok"));
		
		okButton.setPreferredSize(addNewPersonButton.getMinimumSize());
		southPanel.add(okButton);

		

		mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS)));
		mainPanel.add(titlePanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS)));
		mainPanel.add(accessSettings);
		mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS)));
		mainPanel.add(scrollPane);
		mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS*2)));
		mainPanel.add(addPeoplePanel);
//		mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS/2)));
		mainPanel.add(addPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_THICKNESS)));
		mainPanel.add(progressBar);
		
		this.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_THICKNESS,
				BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
		this.add(mainPanel, BorderLayout.NORTH);
		this.add(southPanel, BorderLayout.SOUTH);

		viewController.getComponentFactory().createPopupMenuDecorator().decorate(emailText);
		
		addListeners();
		loadPermissionList();
		
	}
	ActionListener ac;
	private void addListeners() {
		addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent e) {
				eventDispatcher.removeEventHandler(ShareSettingDialog.this);
			}
			public void windowActivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShareSettingDialog.this.dispose();
			}
		});
		
		addNewPersonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				String email = emailText.getText().trim();	
				if (email == null || email.trim().length() == 0 ||email.equalsIgnoreCase(Util.I18N.getString("spshg.dialog.sharesettings.message")))
					return;
				String[] emails =email.split("\\,");
				for (int i=0;i<emails.length;i++){
					if (emails[i]==null|| emails[i].trim().length()==0 )
						continue;
					addNewPersonButton.setEnabled(false);
					okButton.setEnabled(false);
					currentUser=new Users(null,emails[i],(String)scopeComboBox.getSelectedItem(),GoogleSpreadSheetService.SCOPE_USER);
					showProcessGUI(true);
					ShareSettingController ssc= new ShareSettingController(gsss);
					ssc.addNewACLENtry(currentUser);
					Thread t= new Thread(ssc);
					t.start();
				}
			}
		});
		 ac=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				if (cb.getSelectedIndex()!=selectedScopeIndex){
					showProcessGUI(true);
					selectedScopeIndex= cb.getSelectedIndex();
			        Users user= getScopeComboBoxState(cb.getSelectedIndex());
			        ShareSettingController ssc= new ShareSettingController(gsss);
			        ssc.updatePermission(user);
			        Thread t= new Thread(ssc);
					t.start();
				}
			}
		};

		accessSettings.addActionListener(ac);
		emailText.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (emailText.getText().trim().length()==0){
					emailTextFieldLostFocus();
				}
				
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (emailText.getText().equalsIgnoreCase(
						Util.I18N.getString("spshg.dialog.sharesettings.message"))){
					emailText.setText("");
					emailText.setForeground(Color.black);
		
				}
				
			}
		});

	}
	
	private void emailTextFieldLostFocus(){
		emailText.setText(Util.I18N.getString("spshg.dialog.sharesettings.message"));
		emailText.setForeground(Color.gray);
	}
	
	private void loadPermissionList(){
		showProcessGUI(true);
		ShareSettingController ssc= new ShareSettingController(gsss);
		ssc.loadInitialState();
		Thread t= new Thread(ssc);
		t.start();
	}
	
	
	public void actionPerformedTableSelection(){

		if (table.getSelectedColumn()==SharingListTableDataModel.delColumnNumber){
			int rowNum= table.getSelectedRow();
			Users user =tableDataModel.getRow(rowNum);
			if (user !=null && !user.isOwner()){
				showProcessGUI(true);
				ShareSettingController ssc= new ShareSettingController(gsss);
				ssc.removeACLEntrl(user);
				Thread t= new Thread(ssc);
				t.start();
			}
		}
	}
	
	private void initializeScopeComboBox(){
		//selectedIndex:0
		accessSettings.addItem(Util.I18N.getString("spshg.dialog.sharesettings.access.private"));
		//selectedIndex:1
		accessSettings.addItem(Util.I18N.getString("spshg.dialog.sharesettings.access.link.view"));
		//selectedIndex:2		
		accessSettings.addItem(Util.I18N.getString("spshg.dialog.sharesettings.access.link.edit"));
		//selectedIndex:3		
		accessSettings.addItem(Util.I18N.getString("spshg.dialog.sharesettings.access.public.view"));
		//selectedIndex:4		
		accessSettings.addItem(Util.I18N.getString("spshg.dialog.sharesettings.access.public.edit"));
		selectedScopeIndex=0;
		accessSettings.removeActionListener(ac);
		accessSettings.setSelectedIndex(selectedScopeIndex);
		accessSettings.addActionListener(ac);
	}
	
	private Users getScopeComboBoxState(int selectedIndex){
		int type,scope;
		switch (selectedIndex){
			case 0://spshg.dialog.sharesettings.access.private
				type=GoogleSpreadSheetService.ROLE_READER; scope=GoogleSpreadSheetService.SCOPE_PRIVATE; break;
			case 1://spshg.dialog.sharesettings.access.link.view
				type=GoogleSpreadSheetService.ROLE_READER; scope=GoogleSpreadSheetService.SCOPE_DEFAULT_WITH_KEY; break;
			case 2://spshg.dialog.sharesettings.access.link.edit
				type=GoogleSpreadSheetService.ROLE_WRITER; scope=GoogleSpreadSheetService.SCOPE_DEFAULT_WITH_KEY; break;
			case 3://spshg.dialog.sharesettings.access.public.view
				type=GoogleSpreadSheetService.ROLE_READER; scope=GoogleSpreadSheetService.SCOPE_DEFAULT; break;
			case 4://spshg.dialog.sharesettings.access.public.edit
				type=GoogleSpreadSheetService.ROLE_WRITER; scope=GoogleSpreadSheetService.SCOPE_DEFAULT; break;
			default: type=GoogleSpreadSheetService.ROLE_READER; scope=GoogleSpreadSheetService.SCOPE_PRIVATE; break;
		}
		return new Users(null, null, type, scope);
	}
	
	private void setScopeComboBox(Users user){
		if (user.getScope()== GoogleSpreadSheetService.SCOPE_DEFAULT){
			if (user.getPermissionType()== GoogleSpreadSheetService.ROLE_READER){
				accessSettings.removeActionListener(ac);
				accessSettings.setSelectedItem(Util.I18N.getString("spshg.dialog.sharesettings.access.public.view"));
				selectedScopeIndex=accessSettings.getSelectedIndex();
				accessSettings.addActionListener(ac);
				return;
			}
			if (user.getPermissionType()== GoogleSpreadSheetService.ROLE_WRITER){
				accessSettings.removeActionListener(ac);
				accessSettings.setSelectedItem(Util.I18N.getString("spshg.dialog.sharesettings.access.public.edit"));
				selectedScopeIndex=accessSettings.getSelectedIndex();
				accessSettings.addActionListener(ac);
				return;
			}
		}
		if (user.getScope()== GoogleSpreadSheetService.SCOPE_DEFAULT_WITH_KEY){
			if (user.getPermissionType()== GoogleSpreadSheetService.ROLE_READER){
				accessSettings.removeActionListener(ac);
				accessSettings.setSelectedItem(Util.I18N.getString("spshg.dialog.sharesettings.access.link.view"));
				selectedScopeIndex=accessSettings.getSelectedIndex();
				accessSettings.addActionListener(ac);
				return;
			}
			if (user.getPermissionType()== GoogleSpreadSheetService.ROLE_WRITER){
				accessSettings.removeActionListener(ac);
				accessSettings.setSelectedItem(Util.I18N.getString("spshg.dialog.sharesettings.access.link.edit"));
				selectedScopeIndex=accessSettings.getSelectedIndex();
				accessSettings.addActionListener(ac);
				return;
			}
		}
		if (user.getScope()== GoogleSpreadSheetService.SCOPE_PRIVATE){
			accessSettings.removeActionListener(ac);
			accessSettings.setSelectedItem(Util.I18N.getString("spshg.dialog.sharesettings.access.private"));
			selectedScopeIndex=accessSettings.getSelectedIndex();
			accessSettings.addActionListener(ac);
			return;
		}
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		if (event.getEventType() == EventType.SHARING_EVENT) {
			final SharingEvent se =(SharingEvent)event;
			switch(se.getType()){
				case ShareSettingController.TASK_ADD_NEW:
					showProcessGUI(false);
					emailTextFieldLostFocus();
					addNewPersonButton.setEnabled(true);
					okButton.setEnabled(true);
					tableDataModel.addNewRow(se.getUser());
					return;
				case ShareSettingController.TASK_LOAD_CURRENT_STATE:
					showProcessGUI(false);
					if (se.getUser().getScope()== GoogleSpreadSheetService.SCOPE_DEFAULT ||
							se.getUser().getScope()== GoogleSpreadSheetService.SCOPE_DEFAULT_WITH_KEY )
						setScopeComboBox(se.getUser());
					else
						tableDataModel.addNewRow(se.getUser());
					return;
				case ShareSettingController.FINISH_UPDATE:
					showProcessGUI(false);
					return;
				case ShareSettingController.TASK_UPDATE_PERMISSION: 
					showProcessGUI(false);
					if (se.getUser().getEmail()==null)
						setScopeComboBox(se.getUser());
					return;
				case ShareSettingController.TASK_REMOVE:
					showProcessGUI(false);
					table.getSelectionModel().clearSelection();
					tableDataModel.removeUser(se.getUser());
					return;
				case ShareSettingController.TASK_CANCELD:
					showProcessGUI(false);
					emailTextFieldLostFocus();
					addNewPersonButton.setEnabled(true);
					okButton.setEnabled(true);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(ShareSettingDialog.this,se.getMessage(),
									se.getMessageTitle(),JOptionPane.ERROR_MESSAGE);
						}
					});
					
					return;
				case ShareSettingController.TASK_VALUE_CHANGED:
					showProcessGUI(true);
					ShareSettingController ssc= new ShareSettingController(gsss);
					ssc.updatePermission(se.getUser());
					Thread t= new Thread(ssc);
					t.start();
					return;
			}
		}
	}
	
	private void showProcessGUI(boolean b){
		progressBar.setIndeterminate(b);
		progressBar.setVisible(b);
	}
	
	
}


class TableSelectionListener implements ListSelectionListener {
	private ShareSettingDialog ssd;
	TableSelectionListener(ShareSettingDialog ssd){
		this.ssd=ssd;
	}
    public void valueChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        ssd.actionPerformedTableSelection();
    }
}



 

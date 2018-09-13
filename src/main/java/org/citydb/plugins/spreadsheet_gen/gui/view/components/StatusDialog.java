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
/**
 * copy from matching plugin writen by Claus.
 */
package org.citydb.plugins.spreadsheet_gen.gui.view.components;

import org.citydb.event.Event;
import org.citydb.event.EventDispatcher;
import org.citydb.event.EventHandler;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorker;
import org.citydb.plugins.spreadsheet_gen.database.DBManager;
import org.citydb.plugins.spreadsheet_gen.events.EventType;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogMessage;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogTitle;
import org.citydb.plugins.spreadsheet_gen.util.Util;
import org.citydb.registry.ObjectRegistry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


@SuppressWarnings("serial")
public class StatusDialog extends JDialog implements EventHandler {
	final EventDispatcher eventDispatcher;
	
	private JLabel titleLabel;
	private JLabel messageLabel;
	private JProgressBar progressBar;
	private JLabel detailsLabel;
	private JPanel main;
	private JPanel row;
	private JButton button;
	private volatile boolean acceptStatusUpdate = true;

	public StatusDialog(JFrame frame, 
			String windowTitle, 
			String statusTitle,
			String statusMessage,
			String statusDetails, 
			boolean setButton) {
		super(frame, windowTitle, true);
		
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		eventDispatcher.addEventHandler(EventType.STATUS_DIALOG_MESSAGE, this);
		eventDispatcher.addEventHandler(EventType.STATUS_DIALOG_TITLE, this);
		eventDispatcher.addEventHandler(EventType.INTERRUPT, this);
		
		initGUI(windowTitle, statusTitle, statusMessage, statusDetails, setButton);
	}

	private void initGUI(String windowTitle, 
			String statusTitle, 
			String statusMessage, 
			String statusDetails, 
			boolean setButton) {		
		if (statusTitle == null)
			statusTitle = "";
		
		if (statusMessage == null)
			statusMessage = "";
		
		String[] details = null;
		if (statusDetails != null)
			details = statusDetails.split("<br\\s*/*>");
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		titleLabel = new JLabel(statusTitle);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		messageLabel = new JLabel(statusMessage);
		button = new JButton(Util.I18N.getString("common.button.cancel"));		
		progressBar = new JProgressBar(0, 100);

		setLayout(new GridBagLayout()); {
			main = new JPanel();
			add(main, Util.setConstraints(0,0,1.0,0.0,GridBagConstraints.BOTH,5,5,5,5));
			main.setLayout(new GridBagLayout());
			{
				main.add(titleLabel, Util.setConstraints(0,0,0.0,0.5,GridBagConstraints.HORIZONTAL,5,5,5,5));
				main.add(messageLabel, Util.setConstraints(0,1,0.0,0.5,GridBagConstraints.HORIZONTAL,5,5,0,5));
				main.add(progressBar, Util.setConstraints(0,2,1.0,0.0,GridBagConstraints.HORIZONTAL,0,5,5,5));

				if (details != null) {
					detailsLabel = new JLabel("Details");
					main.add(detailsLabel, Util.setConstraints(0,3,1.0,0.0,GridBagConstraints.HORIZONTAL,5,5,0,5));

					row = new JPanel();
					row.setBackground(new Color(255, 255, 255));
					row.setBorder(BorderFactory.createEtchedBorder());
					main.add(row, Util.setConstraints(0,4,1.0,0.0,GridBagConstraints.BOTH,0,5,5,5));
					row.setLayout(new GridBagLayout());
					{				
						for (int i = 0; i < details.length; ++i) {
							JLabel detail = new JLabel(details[i]);
							detail.setBackground(row.getBackground());
							row.add(detail, Util.setConstraints(0,i,1.0,0.0,GridBagConstraints.HORIZONTAL,i == 0 ? 5 : 2,5,i == details.length - 1 ? 5 : 0,5));
						}
					}
				}
			}

			if (setButton)
				add(button, Util.setConstraints(0,1,0.0,0.0,GridBagConstraints.NONE,5,5,5,5));

			pack();
			//progressBar.setIndeterminate(true);
			progressBar.setValue(0);
			
			addWindowListener(new WindowListener() {
				public void windowClosed(WindowEvent e) {
					eventDispatcher.removeEventHandler(StatusDialog.this);
				}
				public void windowActivated(WindowEvent e) {}
				public void windowClosing(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowOpened(WindowEvent e) {}
			});
		}
	}

	public JLabel getStatusTitleLabel() {
		return titleLabel;
	}

	public JLabel getStatusMessageLabel() {
		return messageLabel;
	}
	
	public JButton getButton() {
		return button;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public void handleEvent(Event e) throws Exception {
		if (e.getEventType() == EventType.INTERRUPT) {
			acceptStatusUpdate = false;
			messageLabel.setText(Util.I18N.getString("common.dialog.msg.abort"));
			//progressBar.setIndeterminate(true);
			//TODO stop current work.
		}
		else if (e.getEventType() == EventType.STATUS_DIALOG_MESSAGE && acceptStatusUpdate) {
			messageLabel.setText(((StatusDialogMessage)e).getMessage());
			progressBar.setValue(((int) SPSHGWorker.counter) * 100 / ((int) DBManager.numCityObjects));
		}

		else if (e.getEventType() == EventType.STATUS_DIALOG_TITLE && acceptStatusUpdate) {
			titleLabel.setText(((StatusDialogTitle)e).getTitle());
		}
	}
	
}

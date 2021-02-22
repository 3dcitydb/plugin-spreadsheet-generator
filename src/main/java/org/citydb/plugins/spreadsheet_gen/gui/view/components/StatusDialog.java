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

import org.citydb.config.i18n.Language;
import org.citydb.event.Event;
import org.citydb.event.EventDispatcher;
import org.citydb.event.EventHandler;
import org.citydb.event.global.CounterEvent;
import org.citydb.event.global.EventType;
import org.citydb.event.global.ProgressBarEventType;
import org.citydb.event.global.StatusDialogMessage;
import org.citydb.event.global.StatusDialogProgressBar;
import org.citydb.event.global.StatusDialogTitle;
import org.citydb.gui.util.GuiUtil;
import org.citydb.registry.ObjectRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class StatusDialog extends JDialog implements EventHandler {
	final EventDispatcher eventDispatcher;
	
	private JLabel titleLabel;
	private JLabel messageLabel;
	private JProgressBar progressBar;
	private JButton button;

	private volatile boolean acceptStatusUpdate = true;
	private long featureCounter;
	private long hits;

	public StatusDialog(JFrame frame,
			String windowTitle,
			String statusTitle,
			String statusMessage,
			boolean setButton) {
		super(frame, windowTitle, true);
		
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		eventDispatcher.addEventHandler(EventType.STATUS_DIALOG_MESSAGE, this);
		eventDispatcher.addEventHandler(EventType.STATUS_DIALOG_TITLE, this);
		eventDispatcher.addEventHandler(EventType.COUNTER, this);
		eventDispatcher.addEventHandler(EventType.INTERRUPT, this);
		eventDispatcher.addEventHandler(EventType.STATUS_DIALOG_PROGRESS_BAR, this);

		initGUI(statusTitle, statusMessage, setButton);
	}

	private void initGUI(String statusTitle,
			String statusMessage, 
			boolean setButton) {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		button = new JButton(Language.I18N.getString("common.button.cancel"));

		progressBar = new JProgressBar(0, 100);

		setLayout(new GridBagLayout());
		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());
		{
			titleLabel = new JLabel(statusTitle);
			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
			messageLabel = new JLabel(statusMessage);

			main.add(titleLabel, GuiUtil.setConstraints(0, 0, 0, 0, GridBagConstraints.HORIZONTAL, 0, 0, 5, 0));
			main.add(messageLabel, GuiUtil.setConstraints(0, 1, 0, 0, GridBagConstraints.HORIZONTAL, 5, 0, 5, 0));
			main.add(progressBar, GuiUtil.setConstraints(0, 2, 1, 0, GridBagConstraints.HORIZONTAL, 0, 0, 0, 0));
			main.add(Box.createVerticalGlue(), GuiUtil.setConstraints(0, 3, 1, 1, GridBagConstraints.BOTH, 0, 0, 0, 0));
		}

		add(main, GuiUtil.setConstraints(0, 0, 1, 1, GridBagConstraints.BOTH, 10, 10, 10, 10));
		if (setButton) {
			add(button, GuiUtil.setConstraints(0, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, 5, 10, 10, 10));
		}

		setMinimumSize(new Dimension(300, 100));
		pack();

		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				if (eventDispatcher != null) {
					eventDispatcher.removeEventHandler(StatusDialog.this);
				}
			}
		});
	}
	
	public JButton getButton() {
		return button;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public void handleEvent(Event e) throws Exception {
		if (e.getEventType() == EventType.COUNTER) {
			CounterEvent counterEvent = (CounterEvent) e;
			featureCounter += counterEvent.getCounter();

			String status = String.valueOf(featureCounter);
			if (hits > 0) {
				status += " / " + hits;
				progressBar.setValue((int) featureCounter);
			}

			messageLabel.setText(status);
		} else if (e.getEventType() == org.citydb.event.global.EventType.INTERRUPT) {
			acceptStatusUpdate = false;
			messageLabel.setText(Language.I18N.getString("common.dialog.msg.abort"));
		} else if (e.getEventType() == EventType.STATUS_DIALOG_MESSAGE && acceptStatusUpdate) {
			messageLabel.setText(((StatusDialogMessage) e).getMessage());
		} else if (e.getEventType() == EventType.STATUS_DIALOG_TITLE && acceptStatusUpdate) {
			titleLabel.setText(((StatusDialogTitle) e).getTitle());
		} else if (e.getEventType() == EventType.STATUS_DIALOG_PROGRESS_BAR && acceptStatusUpdate) {
			StatusDialogProgressBar progressBarEvent = (StatusDialogProgressBar) e;
			if (progressBarEvent.getType() == ProgressBarEventType.INIT) {
				SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(progressBarEvent.isSetIntermediate()));
				if (!progressBarEvent.isSetIntermediate()) {
					progressBar.setMaximum(progressBarEvent.getValue());
					progressBar.setValue(0);
					hits = progressBarEvent.getValue();
				}
			}
		}
	}
}

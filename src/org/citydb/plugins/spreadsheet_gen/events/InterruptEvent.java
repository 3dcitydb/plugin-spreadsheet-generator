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
package org.citydb.plugins.spreadsheet_gen.events;

import org.citydb.api.event.Event;
import org.citydb.api.log.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class InterruptEvent.
 */
public class InterruptEvent extends Event {
	
	/** The log message. */
	private String logMessage;
	
	/** The log level type. */
	private LogLevel logLevelType;
	
	/**
	 * Instantiates a new interrupt event.
	 *
	 * @param logMessage the log message
	 * @param logLevelType the log level type
	 * @param source the source
	 */
	public InterruptEvent(String logMessage, LogLevel logLevelType, Object source) {
		super(EventType.INTERRUPT, GLOBAL_CHANNEL, source);
		this.logMessage = logMessage;
		this.logLevelType = logLevelType;
	}

	/**
	 * Gets the log message.
	 *
	 * @return the log message
	 */
	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * Gets the log level type.
	 *
	 * @return the log level type
	 */
	public LogLevel getLogLevelType() {
		return logLevelType;
	}
	
}

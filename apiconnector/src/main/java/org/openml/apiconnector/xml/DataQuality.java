/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class DataQuality {
	
	private final String oml = Constants.OPENML_XMLNS;
	private Integer did;
	private Quality[] qualities;
	
	public DataQuality( Integer did, Quality[] qualities ) {
		this.did = did;
		this.qualities = qualities;
	}
	
	public Integer getDid() {
		return did;
	}
	
	public Quality[] getQualities() {
		return qualities;
	}
	
	public String getOml() {
		return oml;
	}
	
	public class Quality {
		private String name;
		private String value;
		
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
	}
}
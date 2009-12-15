/**
 *   Copyright (C) 2008  GIP RECIA (Groupement d'Intérêt Public REgion 
 *   Centre InterActive)
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;

/**
 * EmptyCleaner used to disable a String cleaner property.
 * @author GIP RECIA - A. Deman
 * 10 déc. 2009
 *
 */
public class EmptyCleaner implements IStringCleaner, Serializable {

	
	/** Serial version UID.*/
	private static final long serialVersionUID = 3122275546012523223L;

	/**
	 * Builds an instance of EmptyCleaner.
	 */
	public EmptyCleaner() {
		super();
	}
	
	/**
	 * @param str
	 * @return
	 * @see org.esco.ws4Internet2Grouper.domain.beans.IStringCleaner#clean(java.lang.String)
	 */
	public String clean(final String str) {
		return str;
	}

}

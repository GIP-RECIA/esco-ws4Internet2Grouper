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

/**
 * ESUP-Portail Commons - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-commons
 */
package org.esco.ws4Internet2Grouper.services.exceptionHandling;

/**
 * An application-specific exception service.
 * 
 * See /properties/exceptionHandling/exceptionHandling-example.xml.
 */
public class CachingEmailExceptionServiceFactoryImpl 
extends org.esupportail.commons.services.exceptionHandling.CachingEmailExceptionServiceFactoryImpl {
	
	/**
	 * The developer's list for bugs.
	 */
	private static final String DEVEL_EMAIL = "commons-bugs@esup-portail.org";

	/**
	 * Bean constructor.
	 */
	public CachingEmailExceptionServiceFactoryImpl() {
		super();
	}

	/**
	 * @return The developer's list for bugs.
	 */
	@Override
	public String getDevelEmail() {
		return DEVEL_EMAIL;
	}
	
}

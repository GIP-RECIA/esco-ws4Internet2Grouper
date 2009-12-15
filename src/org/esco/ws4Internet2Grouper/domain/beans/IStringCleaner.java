/**
*   SARAPIS has the main goal to constitute a reliable information system
*   for the ENT (Environnement Numérique de Travail) and their application 
*   components. This information system could be established from several 
*   sources like the "Annuaire Fédérateur" (which provide xml data), which 
*   could make export on various applications or datasources after data 
*   processing.
*   Copyright (C) 2008  GIP RECIA (Groupement d'Intérêt Public REgion 
*   Centre InterActive)
* 
*   Authors : see 'CREDITS' file.
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
**/
package org.esco.ws4Internet2Grouper.domain.beans;

/**
 * Interface for the string cleaners.
 * @author GIP RECIA - A. Deman
 * 10 December 2009
 *
 */
public interface IStringCleaner {

    /**
     * Cleans the string. 
     * @param str The string to clean.
     * @return The cleaned string..
     */
    String clean(final String str);
}

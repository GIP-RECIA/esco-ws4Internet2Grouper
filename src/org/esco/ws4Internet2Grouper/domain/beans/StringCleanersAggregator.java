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
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * StringCleanersAggregator<br/>
 * Class used to aggregate the cleaners.
 * @author GIP RECIA - A. Deman
 * 1 October 08
 */
public class StringCleanersAggregator implements IStringCleaner, Serializable, InitializingBean {

    /** Serial version UID.*/
    private static final long serialVersionUID = 8548060133701462871L;

    /** The string used to clean the string.*/
    private List<IStringCleaner> cleaners = new ArrayList<IStringCleaner>();
    
    /**
     * Builds an instance of StringCleanersAggregator.
     */
    public StringCleanersAggregator() {
        super();
    }
    
    /**
     * {@inheritDoc}
     * @see org.esco.ws4Internet2Grouper.domain.beans.IStringCleaner#clean(java.lang.String)
     */
    public String clean(final String str) {
        if (str == null) {
            return str;
        }
        String cleaned = str;
        for (IStringCleaner cleaner : cleaners) {
            cleaned = cleaner.clean(cleaned);
        }
        return cleaned.trim();
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if (cleaners.isEmpty()) {
            throw new IllegalStateException("The property cleaners of the class " 
                    + getClass().getSimpleName() + " must be set.");
        }
    }

    /**
     * Getter for cleaners.
     * @return cleaners.
     */
    public List<IStringCleaner> getCleaners() {
        return cleaners;
    }

    /**
     * Setter for cleaners.
     * @param cleaners the new value for cleaners.
     */
    public void setCleaners(final List<IStringCleaner> cleaners) {
        this.cleaners = cleaners;
    }

}

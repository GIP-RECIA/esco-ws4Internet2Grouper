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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Reference implementation of a cleaner.
 * @author GIP RECIA - A. Deman
 * 10 december 2009
 *
 */
public class Cleaner implements IStringCleaner, InitializingBean {
    
    /** The char to replace.*/
    private char replacedChar;
    
    /** The regular expression used to perform the replacement. */
    private String regex;
    
    /** The new string.*/
    private String replacementString;

    /**
     * Builds an instance of Cleaner..
     */
    public Cleaner() {
        super();
    }
    
    /**
     * {@inheritDoc}
     * @see org.esco.sarapis.grouper.domain.beans.IStringCleaner#clean(java.lang.String)
     */
    public String clean(final String str) {
        if (str == null) {
            return null;
        }
        if (str.indexOf(replacedChar) < 0) {
            return str;
        }
        return str.replaceAll(regex, replacementString);
    }

	/**
	 * Checks the beans injection.
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.replacedChar, "The property replacedChar in the class " 
				+ getClass().getName() + " cannot be null.");
		Assert.notNull(this.regex, "The property regex in the class " 
				+ getClass().getName() + " cannot be null.");
		Assert.notNull(this.replacementString, "The property replacementString in the class " 
				+ getClass().getName() + " cannot be null.");
	}

	/**
	 * Getter for replacedChar.
	 * @return replacedChar.
	 */
	public char getReplacedChar() {
		return replacedChar;
	}

	/**
	 * Setter for replacedChar.
	 * @param replacedChar the new value for replacedChar.
	 */
	public void setReplacedChar(final char replacedChar) {
		this.replacedChar = replacedChar;
	}

	/**
	 * Getter for regex.
	 * @return regex.
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * Setter for regex.
	 * @param regex the new value for regex.
	 */
	public void setRegex(final String regex) {
		this.regex = regex;
	}

	/**
	 * Getter for replacementString.
	 * @return replacementString.
	 */
	public String getReplacementString() {
		return replacementString;
	}

	/**
	 * Setter for replacementString.
	 * @param replacementString the new value for replacementString.
	 */
	public void setReplacementString(final String replacementString) {
		this.replacementString = replacementString;
	}
}

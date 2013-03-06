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
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.esco.ws4Internet2Grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * Class used to group and facilitate the management of evaluable strings.
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class EvaluableStrings implements Serializable {

    /** Serial version UID.*/
    private static final long serialVersionUID = 949887214078985250L;
    
    /** The evaluable strings. */
    private List<EvaluableString> strings;
    
    /** Flag to determine if the instance is evaluated or not. 
     * This flags is not updated when one EvaluableString is evaluated directly.
     * */
    private boolean evaluated;
    
    /**
     * Builds an instance of EvaluableStrings.
     */
    public EvaluableStrings() {
        this.strings = new ArrayList<EvaluableString>();
    }
    
    /**
     * Builds an instance of EvaluableStrings.
     * @param strings The evaluable strings.
     */
    public EvaluableStrings(final EvaluableString[] strings) {
        this.strings = new ArrayList<EvaluableString>(strings.length);
        for (int i = 0; i < strings.length && evaluated; i++) {
            this.strings.add(strings[i]);
            if (!strings[i].isEvaluated()) {
                evaluated = false;
            }
        }
    }
    
    /**
     * Builds an instance of EvaluableStrings.
     * @param strings The strings to use.
     * @throws UnknownTemplateElementTempateElement If there is a template element in a string
     * which is unknown.
     */
    public EvaluableStrings(final String[] strings) throws UnknownTemplateElementTempateElement {
        evaluated = true;
        if (strings != null) {
            this.strings = new ArrayList<EvaluableString>(strings.length);
            for (int i = 0; i < strings.length && evaluated; i++) {
                this.strings.add(new EvaluableString(strings[i]));
                if (!this.strings.get(i).isEvaluated()) {
                    evaluated = false;
                }
            }
        }
    }
    
    /**
     * Builds an instance of EvaluableStrings.
     * @param strings The string to use.
     * @param evaluated The evaluated flag.
     */
    private EvaluableStrings(final List<EvaluableString> strings, final boolean evaluated) {
        this.strings = strings;
        this.evaluated = evaluated;
    }
    
    /**
     * Evaluates the evaluable strings contained in this instance.
     * @param values The values to substitue to the template elements.
     * @return The evaluated instance.
     */
    public EvaluableStrings evaluate(final String...values) {
        List<EvaluableString> newEvalStrings = new ArrayList<EvaluableString>(countEvaluableStrings());
        for (int i = 0; i < countEvaluableStrings(); i++) {
            newEvalStrings.add(getEvaluableString(i).evaluate(values));
        }
        return new EvaluableStrings(newEvalStrings, true);
    }
    
    /**
     * Give the string representation of this instance.
     * @return The string that represents this instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < countEvaluableStrings(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getEvaluableString(i));
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Gives the number of evaluable strings.
     * @return The number of evaluable strings.
     */
    public int countEvaluableStrings() {
        if (strings == null) {
            return 0;
        }
        return strings.size();
    }
    
    /**
     * Gives a specified evaluable string.
     * @param index The position of the string to get.
     * @return The EvaluableString instance.
     */
    public EvaluableString getEvaluableString(final int index) {
        return strings.get(index);
    }
    
    /**
     * Adds an evaluable string.
     * @param string The string to add.
     */
    public void addEvaluableString(final EvaluableString string) {
        strings.add(string);
    }
    
    /**
     * Adds an evaluable string.
     * @param string The string to add.
     * @throws UnknownTemplateElementTempateElement If there is a template element in the string
     * which is unknown.
     */
    public void addEvaluableString(final String string) throws UnknownTemplateElementTempateElement {
        strings.add(new EvaluableString(string));
    }

    /**
     * Getter for evaluated.
     * @return evaluated.
     */
    public boolean isEvaluated() {
        return evaluated;
    }
    
    
    
    
}

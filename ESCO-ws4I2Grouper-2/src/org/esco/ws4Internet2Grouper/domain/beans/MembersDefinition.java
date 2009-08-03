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



/**
 * Definition of the members of a group.
 * The definition can handles a type of people (e.g. teacher, student, tos, etc.) and/or
 * an attribute distributionElement (e.g. %NOM_CLASSE%, %NOM_ETAB%,etc.)
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class MembersDefinition implements Serializable {


    /** Serial default UID.*/
    private static final long serialVersionUID = 5449177734260605352L;

    /** The type of members of the group. */
    private PersonType membersType;

    /** The element used to performs the distribution 
     * of the subjects in the groups. */
    private TemplateElement distributionElement;

    /**
     * Builds an instance of MembersDefinition with both members type and matching attribute
     * informations.
     * @param membersType The type of memebrs of the group.
     * @param distributionElement The matching attribute.
     */
    public MembersDefinition(final PersonType membersType, 
            final TemplateElement distributionElement) {
        this.membersType = membersType;
        this.distributionElement = distributionElement;
    }

    /**
     * Builds an instance of MembersDefinition with members information.
     * @param membersType The type of memebrs of the group.
     */
    public MembersDefinition(final PersonType membersType) {
        this.membersType = membersType;
    }

    /**
     * Builds an instance of MembersDefinition with matching attribute information.
     * @param distributionElement The matching attrinbute.
     */
    public MembersDefinition(final TemplateElement distributionElement) {
        this.distributionElement = distributionElement;
    }
    
    /**
     * Gives the string representation of this members definition.
     * @return The String that represents this members definition.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("Members def. [");
        if (membersType != null) {
            sb.append(membersType);
        }
        if (distributionElement != null) {
            sb.append(distributionElement);
        }
        sb.append("]");
        return sb.toString();
    }
    
    
    
    /**
     * Computes the hashcode for this defitinion.
     * @return The hashcode for this defintion.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int mult = 31;
        int hashcode = 0;
        if (membersType != null) {
            hashcode = membersType.hashCode();
        }
        if (distributionElement != null) {
            hashcode = hashcode + mult * distributionElement.hashCode();
        }
        return hashcode;
    }

    /**
     * Tests if this instance is equal to another object.
     * @param obj The object to test.
     * @return True if the tested object is equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MembersDefinition)) {
            return false;
        }
        final MembersDefinition other = (MembersDefinition) obj;
        if (distributionElement == null) {
            if (other.distributionElement != null) {
                return false;
            }
        } else if (!distributionElement.equals(other.distributionElement)) {
            return false;
        }
        if (membersType == null) {
            if (other.membersType != null) {
                return false;
            }
        } 
        return membersType.equals(other.membersType);
    }

    /**
     * Tests if there is a distribution element in this definition.
     * @return True if there is a distribution element.
     */
    public boolean hasDistributionElement() {
        return distributionElement != null;
    }

    /**
     * Getter for membersType.
     * @return membersType.
     */
    public PersonType getMembersType() {
        return membersType;
    }

    /**
     * Getter for distributionElement.
     * @return distributionElement.
     */
    public TemplateElement getMatchingElement() {
        return distributionElement;
    }

    /**
     * Setter for distributionElement.
     * @param distributionElement the new value for distributionElement.
     */
    public void setMatchingElement(final TemplateElement distributionElement) {
        this.distributionElement = distributionElement;
    }

}

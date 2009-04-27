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
package org.esco.ws4Internet2Grouper.services.remote;

import java.util.Collection;

import org.esco.ws4Internet2Grouper.domain.beans.PersonType;


/**
 * Implementation of a person description.
 * N.B.: Somme information are specific to a type of person.
 * @author GIP RECIA - A. Deman
 * 3 déc. 08
 *
 */
public class PersonDescriptionImpl implements IPersonDescription {

   
    /** Serial version UID.*/
    private static final long serialVersionUID = 1L;

    /** The type of the person. */
    private PersonType type;
    
    /** The Id of the person. */
    private String id;
    
    /** The name of the establishment. */
    private String establishmentName;
    
    /** The UAI of the establishment. */
    private String establishmentUAI;
    
    /** The level associated to the person. */
    private String level = "";
    
    /** The name of the class associated to the person. */
    private String className = "";
    
    /** The description of the class. */
    private String classDescription = "";
    
    /** The disciplines. */
    private Collection<String> disciplines;
    
    /** The fonction of the person. */
    private String function = "";
    
    /**
     * Builds an instance of PersonDescriptionImpl.
     * @param id The id of the person.
     */
    public PersonDescriptionImpl(final String id) {
        this.id = id;
    }
            
    /**
     * Builds an instance of PersonDescriptionImpl.
     * @param type The type of the person.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param id The id of the person.
     */
    public PersonDescriptionImpl(final PersonType type,
            final String establishmentUAI, 
            final String establishmentName, final String id) {
        this.type = type;
        this.establishmentUAI = establishmentUAI;
        this.establishmentName = establishmentName;
        this.id = id;
    }
    
    /**
     * Gives the string representation of the instance.
     * @return The String that denotes the person.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
     final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
     sb.append("#{");
     boolean comma = false;
     
     if (!"".equals(type.toString()))  {
         comma = true;
         sb.append("type=");
         sb.append(type);
     }
     
     if (!"".equals(id)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("id=");
         sb.append(id);
     }
     
     if (!"".equals(establishmentUAI)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("UAI=");
         sb.append(establishmentUAI);
     }
     
     if (!"".equals(establishmentName)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("establishment=");
         sb.append(establishmentName);
     }
     
     if (!"".equals(level)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("level=");
         sb.append(level);
     }
     
     if (!"".equals(className)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("className=");
         sb.append(className);
     }
     
     if (!"".equals(classDescription)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("classDescription=");
         sb.append(classDescription);
     }
     
     if (disciplines != null) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("classDescription=");
         sb.append(classDescription);
     }
     
     if (!"".equals(function)) {
         if (comma) {
             sb.append(", ");
         }
         comma = true;
         sb.append("function=");
         sb.append(function);
     }
     sb.append("}");
     
     return sb.toString();
    }
    
    /**
     * Getter for type.
     * @return type.
     */
    public PersonType getType() {
        return type;
    }
    
    /**
     * Setter for type.
     * @param type the new value for type.
     */
    public void setType(final PersonType type) {
        this.type = type;
    }
    
    /**
     * Tests if there is a type information.
     * @return True if there is a type information.
     */
    public boolean hasTypeInformation() {
        return type != null;
    }
    
    /**
     * Getter for establishmentName.
     * @return establishmentName.
     */
    public String getEstablishmentName() {
        return establishmentName;
    }
    /**
     * Setter for establishmentName.
     * @param establishmentName the new value for establishmentName.
     */
    public void setEstablishmentName(final String establishmentName) {
        this.establishmentName = establishmentName;
    }
    
    /**
     * Tests if there is an EstablishmentName information.
     * @return True if there is an EstablishmentName information.
     */
    public boolean hasEstablishmentNameInformation() {
        return !"".equals(establishmentName);
    }
    
    /**
     * Getter for establishmentUAI.
     * @return establishmentUAI.
     */
    public String getEstablishmentUAI() {
        return establishmentUAI;
    }
    
    /**
     * Setter for establishmentUAI.
     * @param establishmentUAI the new value for establishmentUAI.
     */
    public void setEstablishmentUAI(final String establishmentUAI) {
        this.establishmentUAI = establishmentUAI;
    }
    
    
    /**
     * Tests if there is an EstablishmentUAI information.
     * @return True if there is an EstablishmentUAI information.
     */
    public boolean hasEstablishmentUAIInformation() {
        return !"".equals(establishmentUAI);
    }
    
    /**
     * Getter for level.
     * @return level.
     */
    public String getLevel() {
        return level;
    }
    
    /**
     * Tests if there is a level information.
     * @return True if there is a level information.
     */
    public boolean hasLevelInformation() {
        return !"".equals(level);
    }
    
    /**
     * Setter for level.
     * @param level the new value for level.
     */
    public void setLevel(final String level) {
        this.level = level;
    }
    
    /**
     * Getter for className.
     * @return className.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Tests if there is a class name information.
     * @return True if there is a class name information.
     */
    public boolean hasClassNameInformation() {
        return !"".equals(className);
    }
    
    /**
     * Setter for className.
     * @param className the new value for className.
     */
    public void setClassName(final String className) {
        this.className = className;
    }
    
    /**
     * Getter for classDescription.
     * @return classDescription.
     */
    public String getClassDescription() {
        return classDescription;
    }
    
    /**
     * Setter for classDescription.
     * @param classDescription the new value for classDescription.
     */
    public void setClassDescription(final String classDescription) {
        this.classDescription = classDescription;
    }

    /**
     * Tests if there is a class description information.
     * @return True if there is a class description information.
     */
    public boolean hasClassDescriptionInformation() {
        return !"".equals(classDescription);
    }
    
    /**
     * Getter for disciplines.
     * @return disciplines.
     */
    public Collection<String> getDisciplines() {
        return disciplines;
    }
    
    /**
     * Setter for disciplines.
     * @param disciplines the new value for disciplines.
     */
    public void setDisciplines(final Collection<String> disciplines) {
        this.disciplines = disciplines;
    }

    /**
     * Tests if there is a disciplines information.
     * @return True if there is a disciplines information.
     */
    public boolean hasDisciplinesInformation() {
        return disciplines != null;
    }
    
    /**
     * Getter for function.
     * @return function.
     */
    public String getFunction() {
        return function;
    }
    /**
     * Setter for function.
     * @param function the new value for function.
     */
    public void setFunction(final String function) {
        this.function = function;
    }

    /**
     * Tests if there is a function information.
     * @return True if there is a function information.
     */
    public boolean hasFunctionInformation() {
        return !"".equals(function);
    }
    
    /**
     * Getter for id.
     * @return id.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for id.
     * @param id the new value for id.
     */
    public void setId(final String id) {
        this.id = id;
    }
    
    /**
     * Tests if there is an id information.
     * @return True if there is an id information.
     */
    public boolean hasIdInformation() {
        return !"".equals(id);
    }
   
}

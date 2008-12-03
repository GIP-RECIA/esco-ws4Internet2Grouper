/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;

import java.util.Collection;

import org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupService.PersonType;

/**
 * Implementation of a person description.
 * N.B.: Somme information are specific to a type of person.
 * @author GIP RECIA - A. Deman
 * 3 déc. 08
 *
 */
public class PersonDescriptionImpl implements IPersonDescription {

    /** The type of the person. */
    private PersonType type;
    
    /** The Id of the person. */
    private String id;
    
    /** The name of the establishment. */
    private String establishmentName;
    
    /** The UAI of the establishment. */
    private String establishmentUAI;
    
    /** The level associated to the person. */
    private String level;
    
    /** The name of the class associated to the person. */
    private String className;
    
    /** The description of the class. */
    private String classDescription;
    
    /** The disciplines. */
    private Collection<String> disciplines;
    
    /** The fonction of the person. */
    private String function;
    
    
    /**
     * Builds an instance of PersonDescriptionImpl.
     * @param id The id of the person.
     */
    public PersonDescriptionImpl(final String id) {
        this.id = id;
    }
            
    /**
     * Builds an instance of PersonDescriptionImpl.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param id The id of the person.
     */
    public PersonDescriptionImpl(final String establishmentUAI, 
            final String establishmentName, final String id) {
        this.establishmentUAI = establishmentUAI;
        this.establishmentName = establishmentName;
        this.id = id;
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
     * Getter for level.
     * @return level.
     */
    public String getLevel() {
        return level;
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
    

}

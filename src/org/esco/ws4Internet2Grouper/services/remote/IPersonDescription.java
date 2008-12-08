/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;

import java.io.Serializable;
import java.util.Collection;

import org.esco.ws4Internet2Grouper.domain.beans.PersonType;


/**
 * Interface for the person description.
 * Some informations may not be relevant, depending on the type of 
 * the person.
 * @author GIP RECIA - A. Deman
 * 3 déc. 08
 *
 */
public interface IPersonDescription  extends Serializable {
    
    /**
     * Gives the type of the person.
     * @return The type of the person. 
     */
    PersonType getType();
    
    /**
     * Tests if there is a type information.
     * @return True if there is a type information.
     */
    boolean hasTypeInformation();
    
    /**
     * Sets the person type.
     * @param personType The new person type.
     */
    void setType(final PersonType personType);
    
    /**
     * Gives the id of the person.
     * @return The id of the person.
     */
    String getId();
    
    /**
     * Sets the id.
     * @param id The new id.
     */
    void setId(final String id);
    
    /**
     * Tests if there is an id information.
     * @return True if there is an id information.
     */
    boolean hasIdInformation();
    
    /**
     * Gives the UAI identifier of the establishment associated to the person.
     * @return The UAI of the person.
     */
    String getEstablishmentUAI();
    
    /**
     * Sets the establishment UAI.
     * @param establishmentUAI The new establishment UAI.
     */
    void setEstablishmentUAI(final String establishmentUAI);
    
    /**
     * Tests if there is an EstablishmentUAI information.
     * @return True if there is an EstablishmentUAI information.
     */
    boolean hasEstablishmentUAIInformation();
    
    /**
     * Gives the establishment name.
     * @return The establishement name.
     */
    String getEstablishmentName();
    
    /**
     * Sets the establishment name.
     * @param establishmentName The new value for establishment name.
     */
    void setEstablishmentName(final String establishmentName);
    
    /**
     * Tests if there is an EstablishmentName information.
     * @return True if there is an EstablishmentName information.
     */
    boolean hasEstablishmentNameInformation();
    
    /**
     * Gives the level.
     * @return The level.
     */
    String getLevel();
    
    /**
     * Sets the level.
     * @param level The new level.
     */
    void setLevel(final String level);
    
    /**
     * Tests if there is a level information.
     * @return True if there is a level information.
     */
    boolean hasLevelInformation();
    
    /**
     * Gives the Name of the class.
     * @return The name of the class.
     */
    String getClassName();
    
    /**
     * Sets the className.
     * @param className The new class name.
     */
    void setClassName(final String className);
    
    /**
     * Tests if there is a class name information.
     * @return True if there is a class name information.
     */
    boolean hasClassNameInformation();
    
    /**
     * Gives the class description.
     * @return The class description.
     */
    String getClassDescription();
    
    /**
     * Sets the class description.
     * @param classDescription The new class description.
     */
    void setClassDescription(final String classDescription);
    
    /**
     * Tests if there is a class description information.
     * @return True if there is a class description information.
     */
    boolean hasClassDescriptionInformation();
    
    /**
     * Gives the discipline associated the the person.
     * @return The disciplines.
     */
    Collection<String> getDisciplines();
    
    /**
     * Sets the disciplines.
     * @param disciplines The new disciplines.
     */
    void setDisciplines(final Collection<String> disciplines);

    /**
     * Tests if there is a disciplines information.
     * @return True if there is a disciplines information.
     */
    boolean hasDisciplinesInformation();
    
    /**
     * Gives the function of the person.
     * @return The fuction of the person.
     */
    String getFunction();
        
    /**
     * Sets the function.
     * @param function The new function.
     */
    void setFunction(final String function);
    
    /**
     * Tests if there is a function information.
     * @return True if there is a function information.
     */
    boolean hasFunctionInformation();
}

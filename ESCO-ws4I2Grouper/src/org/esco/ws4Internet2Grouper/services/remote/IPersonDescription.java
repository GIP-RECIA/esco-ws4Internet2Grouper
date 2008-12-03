/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;

import java.util.Collection;

import org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupService.PersonType;

/**
 * Interface for the person description.
 * Some informations may not be relevant, depending on the type of 
 * the person.
 * @author GIP RECIA - A. Deman
 * 3 déc. 08
 *
 */
public interface IPersonDescription {
    
    /**
     * Gives the type of the person.
     * @return The type of the person. 
     */
    PersonType getType();
    
    /**
     * Gives the id of the person.
     * @return The id of the person.
     */
    String getId();
    
    /**
     * Gives the UAI identifier of the establishment associated to the person.
     * @return The UAI of the person.
     */
    String getEstablishmentUAI();
    
    /**
     * Gives the establishment name.
     * @return The establishement name.
     */
    String getEstablishmentName();
    
    /**
     * Gives the level.
     * @return The level.
     */
    String getLevel();
    
    /**
     * Gives the Name of the class.
     * @return The name of the class.
     */
    String getClassName();
    
    /**
     * Gives the class description.
     * @return The class description.
     */
    String getClassDescription();
    
    /**
     * Gives the discipline associated the the person.
     * @return The disciplines.
     */
    Collection<String> getDisciplines();
    
    /**
     * Gives the function of the person.
     * @return The fuction of the person.
     */
    String getFunction();
        
    
}

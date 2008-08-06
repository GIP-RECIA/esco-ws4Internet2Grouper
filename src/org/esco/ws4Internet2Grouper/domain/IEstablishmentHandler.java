package org.esco.ws4Internet2Grouper.domain;



import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GrouperSession;

import java.io.Serializable;
import org.springframework.beans.factory.InitializingBean;

/** 
 * Interface for the components used to handle the folders and groups
 * associated to an establishment. 
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public interface IEstablishmentHandler extends Serializable, InitializingBean {

    
    /**
     * Retrieves or create a teachers group for a given establishment.
     * @param session The Grouper session.
     * @param establishementUAI The UAI of the establishement.
     * @param establishementName The name of the establishment.
     * @return The teachers group.
     */
    Group retrieveOrCreateTeachersGroup(final GrouperSession session,
            final String establishementUAI, 
            final String establishementName);

    /**
     * Retrieves or create a students group for a given establishment.
     * @param session The Grouper session.
     * @param establishementUAI The UAI of the establishement.
     * @param establishementName The name of the establishment.
     * @return The students group.
     */
    Group retrieveOrCreateStudentsGroup(final GrouperSession session,
            final String establishementUAI, 
            final String establishementName);

}

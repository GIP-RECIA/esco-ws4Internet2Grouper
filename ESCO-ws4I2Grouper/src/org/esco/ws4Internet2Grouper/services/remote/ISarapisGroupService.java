package org.esco.ws4Internet2Grouper.services.remote;


import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;

/**
 * Interface for the use of groups in an establishment.
 * @author GIP RECIA - A. Deman
 * 3 juin 08
 *
 */
public interface ISarapisGroupService {
    


    
    /**
     * Adds a person to groups.
     * @param personDescription The description of the person.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO addToGroups(final IPersonDescription personDescription);
    
    /**
     * Updates the memberships of a person.
     * @param personDescription The description of the person.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO updateMemberships(final IPersonDescription personDescription);
    
    /**
     * Removes a person from the groups managed by this service.
     * @param userId The id of the person to remove from the groups.
     * @return The result of the Grouper operation.
     */
    GrouperOperationResultDTO removeFromGroups(final String userId);
    
    /**
     * Updates the establishment groups for an administrative employee.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @param function The function of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO updateAdministrativeToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId,
            final String function);
}

package org.esco.ws4Internet2Grouper.services.remote;

import java.util.Collection;
import java.util.List;

import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;

/**
 * Interface for the use of groups in an establishment.
 * @author GIP RECIA - A. Deman
 * 3 juin 08
 *
 */
public interface ISarapisGroupService {
    
    /**
     * Adds an administrative employee to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO addAdministrativeToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Updates the establishment groups for an administrative employee.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO updateAdministrativeToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Adds a parent to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the parent.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO addParentToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Updates establishment groups for a parent.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the parent.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO updateParentToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Adds a student to a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param classDescription The description of the class.
     * @param userId The id of the student.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO addStudentToClass(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription,
            final String userId);
    
    /**
     * Updates establishment groups for a student.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param classDescription The description of the class.
     * @param userId The id of the student.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO updateStudentToClass(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription,
            final String userId);

    /**
     * Adds a teacher to a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param classDescription The description of the class.
     * @param userId The id of the teacher.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO addTeacherToClass(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription,
            final String userId);

    /**
     * Adds a teacher to a list of disciplines in an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param disciplines The list of diciplines for the teacher.
     * @param userId The id of the teacher.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO addTeacherToDisciplines(final String establishmentUAI,
            final String establishmentName,
            final Collection<String> disciplines,
            final String userId);

    /**
     * Adds a TOS employee to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO addTOSToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Udaptes establishment groups for TOS employee.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO updateTOSToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Removes an administrative employee from an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO removeAdministrativeFromEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);

    /**
     * Removes a parent from an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the parent.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO removeParentFromEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
    
    /**
     * Removes a person from all his establishment groups.
     * @param userId The id of the user.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO removePersonFromAllManagedGroups(final String userId);
    
    /**
     * Removes a student from a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param userId The id of the student.
     * @return The object that denotes the result of the operation.
     */
    GrouperOperationResultDTO removeStudentFromClass(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String userId);
    
    /**
     * Removes a teacher from a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param userId The id of the teacher.
     * @return The object that denotes the result of the operation.
     */
    GrouperOperationResultDTO removeTeacherFromClass(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String userId);

    /**
     * Removes a teacher from a list of disciplines in an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param disciplines The list of diciplines.
     * @param userId The id of the teacher.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO removeTeacherFromDisciplines(final String establishmentUAI,
            final String establishmentName,
            final List<String> disciplines,
            final String userId);
    
    /**
     * Removes a TOS employee from an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object of the operation.
     */
    GrouperOperationResultDTO removeTOSFromEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId);
  
}

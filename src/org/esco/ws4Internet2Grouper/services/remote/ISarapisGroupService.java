package org.esco.ws4Internet2Grouper.services.remote;

import java.util.Collection;
import java.util.List;

import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;

/**
 * Interface for the use of groups in an establishment.
 * @author GIP RECIA - A. Deman
 * 3 juin 08
 *
 */
public interface ISarapisGroupService {
    


    
    /** The type of subject that are members of a group. */
    public static enum PersonType {
        /** All type.*/
        ALL, 
        
        /** The members are the teachers.*/
        TEACHER, 
    
        /** The members are the students.*/
        STUDENT,
    
        /** The members are the administrative employee.*/
        ADMINISTRATIVE,
    
        /** The members are the TOS employee.*/
        TOS,
    
        /** The members are the parents. */
        PARENT;
    
        /**
         * Parse a string to a PersonType Instance.
         * @param value The value to parse.
         * @return The PersonType that is equal to the value if it exists,
         * null otherwise.
         */
        public static PersonType parseIgnoreCase(final String value) {
            try {
                return valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new WS4GrouperException("Invalid Type of member: " + value 
                        + ". Legal values are: "
                        + PersonType.STUDENT + ", " 
                        + PersonType.TEACHER + ", " 
                        + PersonType.PARENT + ", " 
                        + PersonType.ADMINISTRATIVE + ", " 
                        + PersonType.TOS + ", " 
                        + PersonType.ALL + ".", e);
            }
        } 
    }

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
     * Adds an administrative employee to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @param function The function of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO addAdministrativeToEstablishment(final String establishmentUAI,
            final String establishmentName,
            final String userId,
            final String function);
    
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

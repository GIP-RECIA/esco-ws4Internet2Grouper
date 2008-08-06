/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain;

import java.io.Serializable;

import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;

/**
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public interface IStudentHandler extends Serializable {

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
}

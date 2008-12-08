package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;

import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;

/**
 * The type of a person.
 * @author GIP RECIA - A. Deman
 * 4 déc. 08
 */
public enum PersonType implements Serializable {
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
/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;

/**
 * Result of a grouper operation.
 * @author GIP RECIA - A. Deman
 * 27 mai 08
 *
 */
public class GrouperOperationResultDTO implements Serializable {

    /** Serial version UID.*/
    private static final long serialVersionUID = 3643694431079789466L;

    /** key of the stem or Group. */
//    private String key;
    
    /** Flag of error.*/
    private boolean error;
    
    /** Exception if there is an error. */
    private Exception exception;
    
    
    
}

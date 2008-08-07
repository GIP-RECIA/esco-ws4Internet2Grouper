/**
 * 
 */
package org.esco.ws4Internet2Grouper.exceptions;

import org.esco.ws4Internet2Grouper.domain.beans.TemplateElement;

/**
 * 
 * @author GIP RECIA - A. Deman
 * 7 août 08
 *
 */
public class UnknownTemplateElementTempateElement extends Exception {

    /** Serial version UID.*/
    private static final long serialVersionUID = 8273546847763385330L;

    /**
     * Builds an instance of UnknownTemplateElementTempateElement.
     * @param invalidTemplateKey The template key that is the cause of the error.
     */
    public UnknownTemplateElementTempateElement(final String invalidTemplateKey) {
        super("Unknown Template Element in the string: " + invalidTemplateKey 
                + " - Known template elements are: " 
                + TemplateElement.getAvailableTemplateElements() + ".");
    }

}

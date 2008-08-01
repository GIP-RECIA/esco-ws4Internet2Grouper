/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;

/**
 * String tha may contain (or not) a template element.
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class EvaluableString implements Serializable {

    /** Serial version UID.*/
    private static final long serialVersionUID = -3881318995251360541L;

    /** The evaluable string. */
    private String string;

    /** The template mask. */
    private int templateMask;

    /**
     * Builds an instance of EvaluableString.
     * @param string The orioginal string.
     */
    public EvaluableString(final String string) {
        this.string = string;
        this.templateMask = TemplateElement.computeTemplateMask(this.string);
    }

    /**
     * Evaluates the string by replacing the template elements by a value.
     * @param establishmentUAI The value to use as establishment UAI.
     * @param establishmentName  The value to use as establishment name.
     * @param level  The value to use as level.
     * @param className  The value to use as class name.
     * @param classDescription The value to use as class description.
     * @return The evaluated instance.
     */
    public EvaluableString evaluate(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription) {
        final String newString = TemplateElement.evaluate(templateMask, 
                string, establishmentUAI, establishmentName, level, className, 
                classDescription);
        return new EvaluableString(newString);
    }

    /**
     * Gives the string without checking the evaluation. 
     * @return The string.
     */
    public String getString() {
        return string;
    }

    /**
     * Gives the string representation of the instance.
     * @return The string that represents this instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return string;
    }

    /**
     * Tests if an object is equal to this instance.
     * @param obj The object to test.
     * @return True if the object is equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof EvaluableString)) {
            return false;
        }

        return string.equals(((EvaluableString) obj).string);
    }

    /**
     * Gives the hash value of this instance.
     * @return The hash value.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return string.hashCode();
    }

    /**
     * Tests if the evaluable string is already evaluated.
     * @return True if the instance is already evaluated.
     */
    public boolean isEvaluated() {
        return templateMask == 0;
    }



}

/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import org.esco.ws4Internet2Grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * @author GIP RECIA - A. Deman
 * 12 août 08
 *
 */
public class ReversibleEvaluableString extends EvaluableString {

    /** Serial version UID.*/
    private static final long serialVersionUID = 2519079352524420095L;
    
    /** The String before the evaluation. */
    private String templateString;

    
    /**
     * Builds an instance of ReversibleEvaluableString.
     */
    protected ReversibleEvaluableString() {
        super();
    }
    
    /**
     * Builds an instance of ReversibleEvaluableString.
     * @param string The source string.
     * @throws UnknownTemplateElementTempateElement
     */
    public ReversibleEvaluableString(final String string)
    throws UnknownTemplateElementTempateElement {
        super(string);
        templateString = string;
    }
    
    /**
     * Evaluates the string by replacing the template elements by a value.
     * @param values The substitution values used to perform the evaluation.
     * @return The evaluated instance.
     */
    @Override
    public ReversibleEvaluableString evaluate(final String...values) {
        if (isEvaluated()) {
            return this;
        }
        final String newString = TemplateElement.evaluate(getTemplateMask(), 
                getString(), values);
        final ReversibleEvaluableString evaluated = new ReversibleEvaluableString();
        evaluated.setString(newString);
        evaluated.setTemplateString(getTemplateString());
        return evaluated;
    }


    /**
     * Getter for templateString.
     * @return templateString.
     */
    public String getTemplateString() {
        return templateString;
    }

    /**
     * Setter for templateString.
     * @param templateString the new value for templateString.
     */
    public void setTemplateString(final String templateString) {
        this.templateString = templateString;
    }

}

/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;


/**
 * Element of a template definition.
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 */
public class TemplateElement {
    
    /** Template element for the etablishment name. */
    public static final TemplateElement ETAB_NAME = new TemplateElement("NOM_ETAB");
    
    /** Template element for the etablishment UAI. */
    public static final TemplateElement ETAB_UAI = new TemplateElement("UAI_ETAB");

    /** Template element for the level. */
    public static final TemplateElement LEVEL = new TemplateElement("NIVEAU");

    /** Template element for the class. */
    public static final TemplateElement CLASS_NAME = new TemplateElement("NOM_CLASSE");

    /** Template element for the class description. */
    public static final TemplateElement CLASS_DESC = new TemplateElement("DESC_CLASSE");
    
    /** Separator for the template elements. */
    private static final char SEPARATOR = '%';
    
    /** Counter used to buikd the masks. */
    private static int maskCount;

    /** Key for the template. */
    private String key;
    
    /** Mask associated to this template element. */
    private int mask = nextMask();
    
    /**
     * Builds an instance of TemplateElement.
     * @param key The key of the template.
          */
    protected TemplateElement(final String key) {
        this.key = key;
        if (this.key.charAt(0) != SEPARATOR) {
            this.key = SEPARATOR + this.key;
        }
        if (this.key.charAt(key.length() - 1) != SEPARATOR) {
            this.key += SEPARATOR;
        }
    }
    
    /**
     * Gives the hash value for this template element.
     * @return The hash value.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
    /**
     * Tests if this instance is equal to this instance.
     * @param o The object to test.
     * @return True if the object is equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        
        if (this == o) {
            return true;
        }
        
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof TemplateElement)) {
            return false;
        }
        
        return key.equals(((TemplateElement) o).key);
    }
    
    /**
     * Gives the next available mask.
     * @return The next available mask.
     */
    private static synchronized int nextMask() {
        return (int) Math.pow(2, maskCount++);
    }
    
    /**
     * Gives the string representation of this template definition element.
     * @return The string that represents this template element.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return key;
    }
    
    /**
     * Replaces the template element in a string.
     * @param src The string that may contain the template element to repace.
     * @param replacement The replacement value.
     * @return The new string. 
     */
    public String replace(final String src, final String replacement) {
        return src.replaceAll(key, replacement);
    }
    
    /**
     * Tests if a mask is flaged for this definition.
     * @param testedMask The mask to test.
     * @return True if the mask is flagged for this definition.
     */
    public boolean hasFlag(final int testedMask) {
        return (mask & testedMask) == mask;
    }
    
    /**
     * Marks a mask for this element if a string contains this template element.
     * @param src The string to check.
     * @param currentMask The mask to use.
     * @return the same mask if the string does not contain the template element,
     * the mask with the flag associated to this element otherwise.
     */
    protected int markIfNecessary(final String src, final int currentMask) {
        if (!isContainedBy(src)) {
            return currentMask;
        }
        return currentMask | mask;
    }
    
    /**
     * Computes the mask for all the template elements.
     * @param src The string that may contain the template elements.
     * @return The mask corresponding to all the template elements 
     * contained in the string.
     */
    public static int computeTemplateMask(final String src) {
        int currentMask = 0;
        currentMask = ETAB_UAI.markIfNecessary(src, currentMask);
        currentMask = ETAB_NAME.markIfNecessary(src, currentMask);
        currentMask = LEVEL.markIfNecessary(src, currentMask);
        currentMask = CLASS_NAME.markIfNecessary(src, currentMask);
        return CLASS_DESC.markIfNecessary(src, currentMask);
    }
    
    /**
     * Resolves the template element in a source string.
     * @param testedMask The mask associated to the source string. 
     * @param src The source string that may contains the key of this template element.
     * @param value The value to use in replacement of the key.
     * @return The String with the  keys replaces by values.
     */
    public String evaluate(final int testedMask, final String src, final String value) {
        if (hasFlag(testedMask)) {
            return replace(src, value);
        }
        return src;
    }
    
    /**
     * Evaluates a source string for alla the available Template elements.
     * @param testedMask The mask associated to the source string.
     * @param src The source string.
     * @param etabUAIValue The value for the establishment UAI.
     * @param etabNameValue The value for the establishement name.
     * @param levelValue The value for the level.
     * @param classNameValue The value for the class name.
     * @param classDescriptionValue The value for the class description.
     * @return The evaluated string.
     */
    public static String evaluate(final int testedMask, final String src,
            final String etabUAIValue,
            final String etabNameValue,
            final String levelValue,
            final String classNameValue,
            final String classDescriptionValue) {
        String evaluated = ETAB_UAI.evaluate(testedMask, src, etabUAIValue);
        evaluated = ETAB_NAME.evaluate(testedMask, evaluated, etabNameValue);
        evaluated = LEVEL.evaluate(testedMask, evaluated, levelValue);
        evaluated = CLASS_NAME.evaluate(testedMask, evaluated, classNameValue);
        evaluated = CLASS_DESC.evaluate(testedMask, evaluated, classDescriptionValue);
        return evaluated;
    }
    
    /**
     * Tests if a string contains this definition.
     * @param testedString The string to test.
     * @return True if the String contains the definition.
     */
    public boolean isContainedBy(final String testedString) {
        if (testedString.indexOf(SEPARATOR) < 0) {
            return false;
        }
        return testedString.contains(key);
    }
}

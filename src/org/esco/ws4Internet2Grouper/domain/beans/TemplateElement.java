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
    
    /** Separator for the template elements. */
    private static final char SEPARATOR = '%';
    
    /** Counter used to buikd the masks. */
    private static int maskCount;

    /** Key for the template. */
    private String key;
    
    /** Mask associated to this template element. */
    private int mask = nextMask();
    
    /**
     * 
     * Builds an instance of TemplateElement.
     * @param key The key of the template.
          */
    public TemplateElement(final String key) {
        this.key = key;
        if (this.key.charAt(0) != SEPARATOR) {
            this.key = SEPARATOR + this.key;
        }
        if (this.key.charAt(key.length() - 1) != SEPARATOR) {
            this.key += SEPARATOR;
        }
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
    public boolean hasFlag(final byte testedMask) {
        return (mask & testedMask) == mask;
    }
    
    /**
     * Marks a mask for this element if a string contains this template element.
     * @param src The string to check.
     * @param currentMask The mask to use.
     * @return the same mask if the string does not contain the template element,
     * the mask with the flag associated to this element otherwise.
     */
    public int markIfNecessary(final String src, final int currentMask) {
        if (!isContainedBy(src)) {
            return currentMask;
        }
        return currentMask | mask;
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

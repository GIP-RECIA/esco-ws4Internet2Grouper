/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;

/**
 * Class used to facilitate the management of Evaluale strings arrays.
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class EvaluableStringArray implements Serializable {

    /** Serial version UID.*/
    private static final long serialVersionUID = 949887214078985250L;
    
    /** The evaluable strings. */
    private EvaluableString[] strings;
    
    /** Flag to determine if the instance is evaluated or not. 
     * This flags is not updated when one EvaluableString is evaluated directly.
     * */
    private boolean evaluated;
    
    /**
     * Builds an instance of EvaluableStringArray.
     * @param strings The evaluable strings.
     */
    public EvaluableStringArray(final EvaluableString[] strings) {
        this.strings = strings;
        for (int i = 0; i < strings.length && evaluated; i++) {
            if (!strings[i].isEvaluated()) {
                evaluated = false;
            }
        }
    }
    
    /**
     * Builds an instance of EvaluableStringArray.
     * @param strings The strings to use.
     */
    public EvaluableStringArray(final String[] strings) {
        evaluated = true;
        if (strings != null) {
            this.strings = new EvaluableString[strings.length];
            for (int i = 0; i < strings.length && evaluated; i++) {
                this.strings[i] = new EvaluableString(strings[i]);
                if (!this.strings[i].isEvaluated()) {
                    evaluated = false;
                }
            }
        }
    }
    
    /**
     * Builds an instance of EvaluableStringArray.
     * @param strings The string to use.
     * @param evaluated The evaluated flag.
     */
    private EvaluableStringArray(final EvaluableString[] strings, final boolean evaluated) {
        this.strings = strings;
        this.evaluated = evaluated;
    }
    
    /**
     * Evaluates the evaluable strings contained in this instance.
     * @param establishmentUAI The value to use as establishment UAI.
     * @param establishmentName  The value to use as establishment name.
     * @param level  The value to use as level.
     * @param className  The value to use as class name.
     * @param classDescription The value to use as class description.
     * @return The evaluated instance.
     */
    public EvaluableStringArray evaluate(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription) {
        EvaluableString[] newEvalStrings = new EvaluableString[countEvaluableStrings()];
        for (int i = 0; i < countEvaluableStrings(); i++) {
            newEvalStrings[i] = getEvaluableString(i).evaluate(establishmentUAI, 
                    establishmentName, level, className, classDescription);
        }
        return new EvaluableStringArray(newEvalStrings, true);
    }
    
    /**
     * Give the string representation of this instance.
     * @return The string that represents this instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < countEvaluableStrings(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getEvaluableString(i));
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Gives the number of evaluable strings.
     * @return The number of evaluable strings.
     */
    public int countEvaluableStrings() {
        if (strings == null) {
            return 0;
        }
        return strings.length;
    }
    
    /**
     * Gives a specified evaluable string.
     * @param index The position of the string to get.
     * @return The EvaluableString instance.
     */
    public EvaluableString getEvaluableString(final int index) {
        return strings[index];
    }

    /**
     * Getter for evaluated.
     * @return evaluated.
     */
    public boolean isEvaluated() {
        return evaluated;
    }
    
    
    
    
}

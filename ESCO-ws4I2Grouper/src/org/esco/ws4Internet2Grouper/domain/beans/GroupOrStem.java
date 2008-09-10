/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.Stem;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;



/**
 * used to manage in the same way a group or a stem.
 * @author GIP RECIA - A. Deman
 * 15 mai 08
 * 
 */
public class GroupOrStem implements Serializable {
    
    /** Serial version UID.*/
    private static final long serialVersionUID = -8707511102005096577L;
    
    /** Logger.*/
    private static final Logger LOGGER = Logger.getLogger(GroupOrStem.class);

    /** The group. */
    private Group group;
    
    /** The stem. */
    private Stem stem;
    
    /**
     * Constructor for GroupOrStem.
     * @param stem The stem.
     */
    public GroupOrStem(final Stem stem) {
        this.stem = stem;
    }
    
    /**
     * Constructor for GroupOrStem.
     * @param group The group.
     */
    public GroupOrStem(final Group group) {
        this.group = group;
    }
    
    /**
     * Gives the String representation of the instance.
     * @return The String representation of the instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String image = getClass().getSimpleName() + "#{";
        if (isGroup()) {
            image += "group, " + asGroup();
            
        } else if (isStem()) {
            image += "stem, " + asStem();
        }
        image += "}";
        return image;
        
    }
    
    /**
     * Tests if the instance denotes a group.
     * @return True if the instance denotes a group.
     */
    public boolean isGroup() {
        return group != null;
    }
    
    /**
     * Tests if the instance denotes a stem.
     * @return True if the instance denotes a stem.
     */
    public boolean isStem() {
        return stem != null;
    }
    
    /**
     * Gives the group underlying by this instance.
     * @return The group.
     */
    public Group asGroup() {
        if (isStem()) {
            final String msg = "ERROR : trying to access to a folder as a group.";
            LOGGER.error(msg);
            throw new WS4GrouperException(msg);
        }
        return group;
    }
    
    /**
     * Gives the stem underlying by this instance.
     * @return The stem.
     */
    public Stem asStem() {
        if (isGroup()) {
            final String msg = "Error: Trying to access to a group as a folder.";
            LOGGER.error(msg);
            throw new WS4GrouperException(msg);
        }
        return stem;
    }
}

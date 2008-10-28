/**
 * 
 */
package org.esco.ws4Internet2Grouper.util;

import edu.internet2.middleware.grouper.Privilege;

/**
 * Constants.
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public abstract class Constants {
    
    /** Separator for the name and UAI in the group names .*/
    public static final String NAME_UAI_SEP = "_";

    /** The admin right for a group. */
    public static final Privilege ADMIN_PRIV = Privilege.getInstance("admin");

    /** The view right for a group. */
    public static final Privilege VIEW_PRIV = Privilege.getInstance("view");

    /** The read right for a group. */
    public static final Privilege READ_PRIV = Privilege.getInstance("read");
    
    /** The stem privilege for the stems. */
    public static final Privilege STEM_PRIV = Privilege.getInstance("stem");

    /** The create privilege for the stems. */
    public static final Privilege CREATE_PRIV = Privilege.getInstance("create");
    
    /**
     * Builds an instance of Constants.
     */
    protected Constants() {
        super();
    }

}

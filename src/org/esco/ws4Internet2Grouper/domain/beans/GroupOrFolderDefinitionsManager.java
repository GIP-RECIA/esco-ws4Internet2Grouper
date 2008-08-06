/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Manager for the groups or folders definitions. 
 * 
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 *
 */
public class GroupOrFolderDefinitionsManager implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 3388637657107893254L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GroupOrFolderDefinition.class);

    /** Preexisting definitions. */
    private Set<GroupOrFolderDefinition> preexistingDefinitnons = 
        new HashSet<GroupOrFolderDefinition>();

    /** All the groups and folder definitions, by path. */
    private Map<String, GroupOrFolderDefinition> definitionsByPath = 
        new HashMap<String, GroupOrFolderDefinition>();

    /**
     * Builds an instance of GroupOrFolderDefinitionsManager.
     */
    public GroupOrFolderDefinitionsManager() {
        LOGGER.info("Initialization of the manager for the groups or folders defintions");
    }

   
    
   
    /**
     * Stores a group or folder definition.
     * @param definition The group or folder definition to store.
     */
    public void addDefinition(final GroupOrFolderDefinition definition) {
        final String path = definition.getPath();
        
        
            definitionsByPath.put(path, definition);
            if (definition.isPreexisting())  {
                preexistingDefinitnons.add(definition);
            }

    }
}

/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 *
 */
public class GroupOrFolderDefManager {
    
    /** Stores the Group or folder definitions by members definition.*/
    private Map<String, List<GroupOrFolderDef>> definitionsByMembers = new HashMap<String, List<GroupOrFolderDef>>();
    
    /** Preexisting definitions. */
    private Set<GroupOrFolderDef> preexistingDefinitnons = new HashSet<GroupOrFolderDef>();
    
    /** All the groups and folder definitions. */
    private Set<GroupOrFolderDef> allDefinitions = new HashSet<GroupOrFolderDef>();
    
    
    
    /**
     * 
     * @param definition The definition of the group or folder.
     */
    public void addPreexistingDefinition(final GroupOrFolderDef definition) {
        allDefinitions.add(definition);
    }
    
    
}

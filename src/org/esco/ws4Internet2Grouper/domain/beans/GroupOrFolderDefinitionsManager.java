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

    /** Stores the Group or folder definitions by members type.*/
    private Map<MembersDefinition, Set<GroupOrFolderDefinition>> definitionsByMembers = 
        new HashMap<MembersDefinition, Set<GroupOrFolderDefinition>>();

    /** Preexisting definitions. */
    private Set<GroupOrFolderDefinition> preexistingDefinitnons = 
        new HashSet<GroupOrFolderDefinition>();

    /** All the groups and folder definitions, by path. */
    private Map<String, GroupOrFolderDefinition> definitionsByPath = 
        new HashMap<String, GroupOrFolderDefinition>();

    /** Associates a group or folder definition with the path of a group with
     * administrating privileges. 
     */
    private Map<String, Set<GroupOrFolderDefinition>> definitionsByAdminisitingPath = 
        new HashMap<String, Set<GroupOrFolderDefinition>>();

    /**
     * Builds an instance of GroupOrFolderDefinitionsManager.
     */
    public GroupOrFolderDefinitionsManager() {
        LOGGER.info("Initialization of the manager for the groups or folders defintions");
    }
    
    /**
     * Adds a preexisting defintion.
     * @param definition The definition of the group or folder.
     */
    public void addPreexistingDefinition(final GroupOrFolderDefinition definition) {
        addDefinition(definition);
        preexistingDefinitnons.add(definition);
    }

    /**
     * Adds a preexisting defintion and administration information.
     * @param definition The definition of the group or folder.
     * @param administratingPaths Paths of groups with administrating privileges.
     */
    public void addPreexistingDefinition(final GroupOrFolderDefinition definition, 
            final String[] administratingPaths) {

        addPreexistingDefinition(definition);
        handlesAdministrationInformation(definition, administratingPaths);
    }
    
    /**
     * Stores the administration information.
     * @param definition The definition of the group or folder.
     * @param administratingPaths Paths of groups with administrating privileges.
     */
    private void handlesAdministrationInformation(final GroupOrFolderDefinition definition, 
            final String[] administratingPaths) {

        // Handles administrating group path.
        for (String path : administratingPaths) {
            Set<GroupOrFolderDefinition> targetDefs = definitionsByAdminisitingPath.get(path);
            if (targetDefs == null) {
                targetDefs = new HashSet<GroupOrFolderDefinition>();
            }
            targetDefs.add(definition);
        }
    }
    
    /**
     * Stores a group or folder definition.
     * @param definition The group or folder definition to store.
     */
    public void addDefinition(final GroupOrFolderDefinition definition) {
        definitionsByPath.put(definition.getPath(), definition);

        for (int i = 0; i < definition.countMembersDefinitions(); i++) {
            
            // Handles the definition by type.
            final MembersDefinition membersDefintion = definition.getMembersDefiniton(i);
           
            Set<GroupOrFolderDefinition> relatedDef = definitionsByMembers.get(membersDefintion);
            if (relatedDef == null) {
                relatedDef = new HashSet<GroupOrFolderDefinition>();
                definitionsByMembers.put(membersDefintion, relatedDef);
            }
            relatedDef.add(definition);
        }
    }


    /**
     * Stores a group or folder definition.
     * @param definition The group or folder definition to store.
     * @param administratingPaths Paths of groups with administrating privileges.
     */
    public void addDefinition(final GroupOrFolderDefinition definition,
            final String[] administratingPaths) {
        addDefinition(definition);
        handlesAdministrationInformation(definition, administratingPaths);
    }


}

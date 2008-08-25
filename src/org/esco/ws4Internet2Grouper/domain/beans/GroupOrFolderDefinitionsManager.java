/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.cache.SGSCache;
import org.esco.ws4Internet2Grouper.domain.beans.MembersDefinition.MembersType;

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

    /** Stores the group or folder definition by type of members in the ditribution definition.*/
    private Map<MembersType, List<GroupOrFolderDefinition>> definitionByType;

    /** The Cache for the Sarapis Group Service. */
    private SGSCache cache = SGSCache.instance();

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
    public void registerDefinition(final GroupOrFolderDefinition definition) {
        final String path = definition.getPath();
        definitionsByPath.put(path, definition);
        if (definition.isPreexisting())  {
            preexistingDefinitnons.add(definition);
        }
    }


    /**
     * Returns an iterator over the preexisting definitions.
     * @return An iterator over the preexisting definitions 
     * of groups or folders.
     */
    public Iterator<GroupOrFolderDefinition> preexistingDefinitions() {
        return preexistingDefinitnons.iterator();
    }
    

    /**
     * Gives the groups defined as templates for a type of memebers 
     * and the values of some attributes.
     * @param type The type of the member of the groups.
     * @param attributes The attributes of the member of the groups which are used 
     * for the templates evaluations.
     * @return An iterator over the groups.
     */
    public Iterator<GroupOrFolderDefinition> getMembershipsForTemplates(final MembersDefinition.MembersType type, 
            final String...attributes) {

        if (definitionByType == null) {
            initilizeDefinitionsByTypeOfMembers();
        }

        // Tries to retrieves the memebrships from the cache. 
        Set<GroupOrFolderDefinition> memberships = cache.getMembershipsForTemplates(type, attributes); 
        if (memberships != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Memberships for templates retrieved from cache for type " + type 
                        + ": " + Arrays.toString(attributes) + ": " 
                        + memberships);
            }
            return memberships.iterator();
        }

        // The memberships has to be evaluated.
        memberships = new HashSet<GroupOrFolderDefinition>();
        final List<GroupOrFolderDefinition> globalDefs = definitionByType.get(type);

        if (globalDefs == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No memberships for members of type " + type + ".");
            }
        } else {
            for (GroupOrFolderDefinition globalDef : globalDefs) {
                // Only template definitions are handled.
                if (globalDef.isTemplate()) {
                    memberships.add(globalDef.evaluateTemplate(attributes));
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Memberships for template groups evaluated for " + type 
                        + " " + Arrays.toString(attributes) + ": " 
                        + memberships);
            }

        }
        cache.cacheMemebrships(memberships, type, attributes);

        return memberships.iterator();
    }

    /**
     * Gives the groups for a type and The values of some attributes.
     * @param type The type of the member of the groups.
     * @param attributes The attributes of the member of the groups which are used 
     * for the templates evaluations.
     * @return An iterator over the groups.
     */
    public Iterator<GroupOrFolderDefinition> getMemberships(final MembersDefinition.MembersType type, 
            final String...attributes) {

        if (definitionByType == null) {
            initilizeDefinitionsByTypeOfMembers();
        }

        // Tries to retrieves the memebrships from the cache. 
        Set<GroupOrFolderDefinition> memberships = cache.getMemberships(type, attributes); 
        if (memberships != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Memberships retrieved from cache for type " + type 
                        + ": " + Arrays.toString(attributes) + ": " 
                        + memberships);
            }
            return memberships.iterator();
        }

        // The memberships has to be evaluated.
        memberships = new HashSet<GroupOrFolderDefinition>();
        final List<GroupOrFolderDefinition> globalDefs = definitionByType.get(type);

        if (globalDefs == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No memberships for members of type " + type + ".");
            }
        } else {
            for (GroupOrFolderDefinition globalDef : globalDefs) {
                if (!globalDef.isTemplate()) {
                    memberships.add(globalDef);
                } else {
                    memberships.add(globalDef.evaluateTemplate(attributes));
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Memberships evaluated for " + type 
                        + " " + Arrays.toString(attributes) + ": " 
                        + memberships);
            }

        }
        cache.cacheMemebrships(memberships, type, attributes);

        return memberships.iterator();
    }

    /**
     * Retrieves a definition by the path of the group or folder.
     * @param path The path of the group or folder.
     * @param values The values used to evaluate templates.
     * @return The group or folder definition.
     */
    public GroupOrFolderDefinition getDefinition(final String path, final String...values) {
        final GroupOrFolderDefinition def = definitionsByPath.get(path);
        if (def.isTemplate()) {
            return def.evaluateTemplate(values);
        }
        return def; 
    }

    /**
     * Initilizes the map of the groups definition by type of members.
     */
    protected void initilizeDefinitionsByTypeOfMembers() {

        definitionByType = new HashMap<MembersType, List<GroupOrFolderDefinition>>();

        for (GroupOrFolderDefinition gofd : definitionsByPath.values()) {

            // Stores the definition by type of subject fr their membres
            // definition.
            for (int i = 0; i < gofd.countMembersDefinitions(); i++) {

                final MembersDefinition mbDef = gofd.getMembersDefiniton(i);

                List<GroupOrFolderDefinition> defs = definitionByType.get(mbDef.getMembersType());

                if (defs == null) {
                    defs = new ArrayList<GroupOrFolderDefinition>();
                    definitionByType.put(mbDef.getMembersType(), defs);
                }
                defs.add(gofd);
            }
        }
    }

    /**
     * Checks the paths references.
     * @return The List of error messages (empty if there is no error). 
     */
    public List<String> checksPathsReferences() {

        // Error messages.
        final List<String> errorsMsg = new ArrayList<String>();

        // Browses all the definition to cheks the path.
        for (GroupOrFolderDefinition gofd : definitionsByPath.values()) {

            String typeOfDef;
            if (gofd.isFolder()) {
                typeOfDef = "folder";
            } else {
                typeOfDef = "group";
            }

            // Checks the administrating groups paths
            for (int i = 0; i < gofd.countAdministratingGroupsPaths(); i++) {
                final String adminPath = gofd.getAdministratingGroupPath(i);
                final GroupOrFolderDefinition ref = definitionsByPath.get(adminPath);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Finilizing definition for " + gofd.getPath() + ".");
                }

                if (ref == null) {
                    // The path referes to a group that is not defined.
                    final String msg = "Invalid administrating group path for the " + typeOfDef + ": "
                    + gofd.getPath() + " - Can't find a group definition for the administrating group path: " 
                    + adminPath + ".";
                    errorsMsg.add(msg);
                } else if (ref.isFolder()) {
                    // The administrating path denotes a folder.
                    final String msg = "Invalid administrating group path for the " + typeOfDef + ": "
                    + gofd.getPath() + " - It should be a group definition but it is a FOLDER defintion: " 
                    + adminPath + ".";
                    errorsMsg.add(msg);
                } 
            }

            // Checks the containing groups paths
            if (gofd.isFolder()) {
                // A folder can'be a member of a group
                if (gofd.countContainingGroupsPaths() > 0) {
                    final String msg = "The folder: " + gofd.getPath() 
                    + " can't be member of a group (only groups can).";
                    errorsMsg.add(msg);

                }
            } else {

                for (int i = 0; i < gofd.countContainingGroupsPaths(); i++) {
                    final String containingPath = gofd.getContainingGroupPath(i);
                    final GroupOrFolderDefinition ref = definitionsByPath.get(containingPath);

                    if (ref == null) {
                        // The path referes to a group that is not defined.
                        final String msg = "Invalid containing group path for the group: "
                            + gofd.getPath() + " - Can't find a group definition for the containing group path: " 
                            + containingPath + ".";
                        errorsMsg.add(msg);
                    } else if (ref.isFolder()) {
                        // The administrating path denotes a folder.
                        final String msg = "Invalid containing group path for the group: "
                            + gofd.getPath() + " - It should be a group definition but it is a FOLDER defintion: " 
                            + containingPath + ".";
                        errorsMsg.add(msg);
                    } else if (gofd.equals(ref)) {

                        // The administrating path denotes the same group.
                        final String msg = "The containing group path for the group: "
                            + gofd.getPath() + " references to itself.";
                        errorsMsg.add(msg);
                    } 
                }
            }
        }
        return errorsMsg;
    }
}


package org.esco.ws4Internet2Grouper.util;



import edu.internet2.middleware.grouper.GrantPrivilegeException;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupAddException;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupModifyException;
import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.SchemaException;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemAddException;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.StemModifyException;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.subject.Subject;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.cache.SGSCache;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * Util class for the grouper groups or stems manipulations.
 * @author GIP RECIA - A. Deman
 * 29 juil. 08
 * 
 */
public class GrouperUtil implements InitializingBean {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GrouperUtil.class);

    /** Cache. */
    private final SGSCache sgsCache = SGSCache.instance();

    /** The central administration group. */
    private Group centralAdminGroup;

    /** Used to add administration privileges to the central admin group. */
    private Subject centralAdminGroupAsSubject;

    /** The user parameters. */
    private SGSParameters parameters;

    /**
     * Builds an instance of GrouperUtil.
     */
    public GrouperUtil() {
        super();
    }

    /**
     * Checks the sping injections.
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(this.parameters, 
                "property parameters of class " + this.getClass().getName() 
                + " can not be null");

        // Retrieves and stores central admin group, wich is used used very often.
        final GrouperSessionUtil sessionUtil = new GrouperSessionUtil(parameters.getUser());
        final GrouperSession session = sessionUtil.createSession();
        try {

            // Central administration group. 
            final String centralAdminGroupName = parameters.getCentralAdminGroup();
            centralAdminGroup = GroupFinder.findByName(session, centralAdminGroupName);
            centralAdminGroupAsSubject = centralAdminGroup.toSubject();
            

        } finally {
            sessionUtil.stopSession(session);
        }
    }


    /**
     * Creates a folder and sets the rights for the central admin group.
     * @param session The grouper session.
     * @param folderSummary The information concerning the folder to create.
     * @return The folder if it has been created, null otherwise (for instance if the containing folder 
     * does not exist).
     */
    protected Stem createFolder(final GrouperSession session,
            final GroupOrFolderSummary folderSummary) {

        try {
            final Stem containingFolder = retrieveFolder(session, folderSummary.getContainingPath());
            if (containingFolder == null) {
                return null;
            }
            final Stem folder = containingFolder.addChildStem(folderSummary.getExtension(), 
                    folderSummary.getExtension());
            folder.setDescription(folderSummary.getExtension());
            folder.grantPriv(centralAdminGroupAsSubject, Constants.STEM_PRIV);
            folder.grantPriv(centralAdminGroupAsSubject, Constants.CREATE_PRIV);
            sgsCache.cacheFolder(folder);
            return folder;
        } catch (InsufficientPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (StemAddException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (StemModifyException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (SchemaException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GrantPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        }
    }


    /**
     * Creates a folder and assign privileges to a local administration group (which may be created too).
     * @param session The grouper session.
     * @param folderSummary Information about the folder to created.
     * @param localAdminGroupSummary Information about the local administration group.
     * If this group can't be retrieved or created then the folder is not created.
     * @return The folder if it has been created, null otherwise.
     */
    public Stem createLocallyAdministratedFolder(final GrouperSession session,
            final GroupOrFolderSummary folderSummary,
            final GroupOrFolderSummary localAdminGroupSummary) {

        final Group localAdminGroup = retrieveOrCreateGroup(session, 
                localAdminGroupSummary);
        if (localAdminGroup == null) {
            return null;
        }

        final Stem folder = createFolder(session, folderSummary);

        if (folder == null) {
            return null;
        }

        try {
            folder.grantPriv(localAdminGroup.toSubject(), Constants.STEM_PRIV);
            folder.grantPriv(localAdminGroup.toSubject(), Constants.CREATE_PRIV);
            sgsCache.cacheFolder(folder);
            return folder;
        } catch (GrouperRuntimeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (SchemaException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (InsufficientPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GrantPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        }
    }    

    /**
     * Retrieves a folder.
     * @param session The grouper session.
     * @param name the name of the folder to retrieve.
     * @return The stem if it is retrieved, null otherwhise.
     */
    public Stem retrieveFolder(final GrouperSession session,
            final String name) {

        // Try to retrieve the folder from the cache.
        final Stem cachedFolder = sgsCache.getFolder(name);
        if (cachedFolder != null) {
            return cachedFolder;
        }

        // The folder has to be retrieved from Grouper.
        try {
            final Stem folder = StemFinder.findByName(session, name);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for folder " + name + ": Found.");
            }
            return folder;
        } catch (StemNotFoundException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for folder " + name + ": Not found.");
            }

            return null;
        }
    }

    /**
     * Retrieves or creates a folder.
     * @param session The grouper session.
     * @param folderSummary Informations about the folder to created.
     * @return Null if the folder does not exist and cannot be created, 
     * the retrieved/created stem.
     */
    public Stem retrieveOrCreateFolder(final GrouperSession session,
            final GroupOrFolderSummary folderSummary) {

        final Stem folder = retrieveFolder(session, folderSummary.getPath());

        // The folder is retrieved
        if (folder != null) {
            return folder;
        }
        // The folder is created.
        return createFolder(session, 
                folderSummary);
    }

    /**
     * Retrieves or creates a local administrated folder. The local group that has administration privileges
     * on the folder may be created too.
     * @param session The grouper session.
     * @param folderSummary Information about the folder to created.
     * @param localAdminGroupSummary Information about the local administration group.
     * If this group can't be retrieved or created, then the folder is not created.
     * @return Null if the folder does not exist and cannot be created, 
     * the retrieved/created folder otherwise.
     */
    public Stem retrieveOrCreateLocallyAdministratedFolder(final GrouperSession session,
            final GroupOrFolderSummary folderSummary,
            final GroupOrFolderSummary localAdminGroupSummary) {

        final Stem folder = retrieveFolder(session, folderSummary.getPath());

        // The folder is retrieved
        if (folder != null) {
            return folder;
        }
        // The folder is created.
        return createLocallyAdministratedFolder(session, 
                folderSummary,
                localAdminGroupSummary);
    }

    /**
     * Creates a group in a given folder and add administration privileges to the central administration
     * group.
     * @param session The grouper session.
     * @param groupSummary The informations about the group to create.
     * @return The new group if it can be created, null otherwise.
     */
    public Group createGroup(final GrouperSession session, 
            final GroupOrFolderSummary groupSummary) {
        try {
            final Stem containingFolder = retrieveFolder(session, groupSummary.getContainingPath());
            if (containingFolder == null) {
                return null;
            }

            final Group group = containingFolder.addChildGroup(groupSummary.getExtension(), 
                    groupSummary.getDisplayname());
            group.setDescription(groupSummary.getDescription());
            group.grantPriv(centralAdminGroupAsSubject, Constants.ADMIN_PRIV);
            sgsCache.cacheGroup(group);
            return group;
        } catch (GroupAddException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (InsufficientPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (SchemaException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GrantPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GroupModifyException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        }
    }

    /**
     * Creates a group in a given folder and add administration privileges to the central administration
     * group and to a specified local administration group (wich may be created).
     * @param session The grouper session.
     * @param groupSummary The information baout the group to create.
     * @param localAdminGroupSummary The informations about the local administraton group.
     * If this group can't be retrieved or created then the group is not created.
     * is searched or created. 
     * @return The new group if it can be created, null otherwise.
     */
    public Group createLocallyAdministratedGroup(final GrouperSession session, 
            final GroupOrFolderSummary groupSummary,
            final GroupOrFolderSummary localAdminGroupSummary) {

        final Group localAdminGroup = retrieveOrCreateGroup(session, 
                localAdminGroupSummary);

        if (localAdminGroup == null) {
            return null;
        }

        final Group group = createGroup(session, groupSummary);

        if (group == null) {
            return null;
        }

        try {
            group.grantPriv(localAdminGroup.toSubject(), Constants.ADMIN_PRIV);
            return group;
        } catch (GrouperRuntimeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (SchemaException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (InsufficientPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GrantPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        }
    }

    /**
     * Retrieves a group.
     * @param session The grouper session.
     * @param name the name of the group to retrieve.
     * @return The group if it is retrieved, null otherwhise.
     */
    public Group retrieveGroup(final GrouperSession session,
            final String name) {

        // Try to retrieves from the cache.
        final Group cachedGroup = sgsCache.getGroup(name);
        if (cachedGroup != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for group " + name + ": in the cache.");
            }
            return cachedGroup;
        }

        // Retrieves the group from grouper.
        try {
            final Group group = GroupFinder.findByName(session, name);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for group " + name + ": found.");
            }
            return group;

        } catch (GroupNotFoundException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for group: " + name + ": not found.");
            }
            return null;
        }
    }

    /**
     * Retrieves a group or creates it if it does not exsist.
     * @param session The grouper session.
     * @param groupSummary The informations about the group to create.
     * @return The retrieved or created group.
     */
    public Group retrieveOrCreateGroup(final GrouperSession session, 
            final GroupOrFolderSummary groupSummary) {

        Group group = retrieveGroup(session, groupSummary.getPath());
        if (group != null) {
            return group;
        }
        return createGroup(session, groupSummary);
    }    

    /**
     * Retrieves a group or creates it as a local administrated group if it does not exsist.
     * The local administration group my be created too.
     * @param session The grouper session.
     * @param groupSummary The informations about the group to create.
     * @param localAdminGroupSummary The informations about the local administration group.
     * If this group can't be retrieved or created, the group is not created.
     * @return Null if the group doesn't exist and can't be created, the retrieved or created group 
     * otherwise.
     */
    public Group retrieveOrCreateLocallyAdministratedGroup(final GrouperSession session, 
            final GroupOrFolderSummary groupSummary,
            final GroupOrFolderSummary localAdminGroupSummary) {
        
        Group group = retrieveGroup(session, groupSummary.getPath());
        if (group != null) {
            return group;
        }
        return createLocallyAdministratedGroup(session, groupSummary, localAdminGroupSummary);
    }    

    /**
     * Getter for centralAdminGroup.
     * @return centralAdminGroup.
     */
    public Group getCentralAdminGroup() {
        return centralAdminGroup;
    }

    /**
     * Getter for centralAdminGroupAsSubject.
     * @return centralAdminGroupAsSubject.
     */
    public Subject getCentralAdminGroupAsSubject() {
        return centralAdminGroupAsSubject;
    }

    /**
     * Getter for parameters.
     * @return parameters.
     */
    public SGSParameters getParameters() {
        return parameters;
    }

    /**
     * Setter for parameters.
     * @param parameters the new value for parameters.
     */
    public void setParameters(final SGSParameters parameters) {
        this.parameters = parameters;
    }

    
}

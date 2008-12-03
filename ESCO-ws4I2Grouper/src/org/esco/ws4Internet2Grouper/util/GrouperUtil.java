package org.esco.ws4Internet2Grouper.util;

import edu.internet2.middleware.grouper.GrantPrivilegeException;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupAddException;
import edu.internet2.middleware.grouper.GroupDeleteException;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupModifyException;
import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.MemberAddException;
import edu.internet2.middleware.grouper.MemberDeleteException;
import edu.internet2.middleware.grouper.MemberFinder;
import edu.internet2.middleware.grouper.MemberNotFoundException;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.SchemaException;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemAddException;
import edu.internet2.middleware.grouper.StemDeleteException;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.StemModifyException;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;


import java.util.Set;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrStem;
import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;
import org.esco.ws4Internet2Grouper.domain.beans.PrivilegeDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.PrivilegeDefinition.Right;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;

/**
 * Util class for the grouper groups or stems manipulations.
 * @author GIP RECIA - A. Deman
 * 29 juil. 08
 * 
 */
public class GrouperUtil  {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GrouperUtil.class);

    /** The definition manager. */
    private GroupOrFolderDefinitionsManager definitionsManager;

    /** Flag to detemine the behaviour for the empty groups when removing
     * a member. */
    private boolean deleteEmptyGroups;

    /** Flag to determine if empty folder should be deleted. */
    private boolean deleteEmptyFolders;
    
    /** Flag to force the privileges. */
    private boolean forcePrivileges;

    /**
     * Builds an instance of GrouperUtil.
     */
    public GrouperUtil() {
        super();
    }

    /**
     * Checks if a group or a folder exists in Grouper.
     * @param session The Grouper session.
     * @param definition The definition of the group or folder to look for.
     * @return True if the group or folder exists in Grouper.
     */
    public boolean exists(final GrouperSession session, 
            final GroupOrFolderDefinition definition) {
        return retrieve(session, definition) != null;
    }

    /**
     * Handles the privileges for a folder.
     * This privileges are added if the folder is empty and is not a preexistiong one.
     * @param session The grouper session.
     * @param groupOrStem The folder.
     * @param definition The folder definition which contains the privileges
     * to add.
     * @param values The values used to evaluate the template elements.
     */
    private void handlePrivilegesForFolder(final GrouperSession session, 
            final GroupOrStem groupOrStem, 
            final GroupOrFolderDefinition definition,
            final String...values) {

        if (!definition.isPreexisting()) {
            final Stem folder = groupOrStem.asStem();
          
            if ((folder.getChildStems().size() == 0 && folder.getChildGroups().size() == 0) || forcePrivileges) {
                // The administration privileges are checked for the empty folders.

                if (LOGGER.isDebugEnabled()) {
                    
                    if (forcePrivileges) {
                        LOGGER.debug("Forcing privileges on the folder" 
                                + " " + definition.getPath() 
                                + ".");
                    } else {
                        LOGGER.debug("The folder " 
                                + definition.getPath() 
                                + " is empty so the privileges are checked.");
                    }
                }
                
                // Adds privileges for some groups if needed.
                for (int i = 0; i < definition.countPrivileges(); i++) {
                    final PrivilegeDefinition privDef = definition.getPrivilege(i); 
                    final String path = privDef.getPath().getString();
                    GroupOrFolderDefinition privilegedGroupDef = definitionsManager.getDefinition(path, values);
                    GroupOrStem privilegedGroupWrapper = retrieveOrCreate(session, privilegedGroupDef, values);

                    final Subject subj = privilegedGroupWrapper.asGroup().toSubject();
                    try {
                        if (Right.ADMIN.equals(privDef.getPrivilege())) {
                            addAdminPrivilege(definition.getPath(), privilegedGroupDef.getPath(), folder, subj);
                        }
                        if (Right.FOLDER_CREATION.equals(privDef.getPrivilege())) {
                            addStemPrivilege(definition.getPath(), privilegedGroupDef.getPath(), folder, subj);
                        }
                        if (Right.GROUP_CREATION.equals(privDef.getPrivilege())) {
                            addCreatePrivilege(definition.getPath(), privilegedGroupDef.getPath(), folder, subj);
                        }
                    } catch (GrantPrivilegeException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    } catch (InsufficientPrivilegeException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    } catch (SchemaException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    }
                } 
            } else if (!forcePrivileges) {
                // The folder is not empty: administration privileges should be right as they
                // are checked before adding the first child.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("The folder " 
                            + definition.getPath() 
                            + " is not empty so the privileges are supposed to be valid.");
                }
            }
        }
    }

    /**
     * Handles the privileges for a group.
     * This privileges are added if the group is empty and is not a preexisting one.
     * @param session The grouper session.
     * @param groupOrStem The group.
     * @param definition The group definition which contains the privileges
     * to add.
     * @param values The values used to evaluate the template elements.
     */
    private void handlePrivilegesForGroup(final GrouperSession session, 
            final GroupOrStem groupOrStem, 
            final GroupOrFolderDefinition definition,
            final String...values) {


        if (!definition.isPreexisting()) {

            final Group group = groupOrStem.asGroup();
            
            if (group.getImmediateMembers().size() == 0 || forcePrivileges) {

                // The administration privileges are checked for the empty groups.
                if (LOGGER.isDebugEnabled()) {
                    if (forcePrivileges) {
                        LOGGER.debug("Forcing privileges on the group" 
                                + " " + definition.getPath() 
                                + ".");
                    } else {
                        LOGGER.debug("The group" 
                                + " " + definition.getPath() 
                                + " is empty so the privileges are checked.");
                    
                    }
                    
                }

                for (int i = 0; i < definition.countPrivileges(); i++) {
                    final PrivilegeDefinition privDef = definition.getPrivilege(i); 
                    final String path = privDef.getPath().getString();
                    final GroupOrFolderDefinition privilegedGroupDef = definitionsManager.getDefinition(path, values);
                    final GroupOrStem privilegedGroupWrapper = retrieveOrCreate(session, privilegedGroupDef, values);
                    final Subject subj = privilegedGroupWrapper.asGroup().toSubject(); 
                    try {
                        if (Right.ADMIN.equals(privDef.getPrivilege())) {
                            addAdminPrivilege(definition.getPath(), privilegedGroupDef.getPath(), group, subj);
                            addReadPrivilege(definition.getPath(), privilegedGroupDef.getPath(), group, subj);
                            addViewPrivilege(definition.getPath(), privilegedGroupDef.getPath(), group, subj);
                        } else if (Right.READ.equals(privDef.getPrivilege())) {
                            addReadPrivilege(definition.getPath(), privilegedGroupDef.getPath(), group, subj);
                            addViewPrivilege(definition.getPath(), privilegedGroupDef.getPath(), group, subj);
                        }
                    } catch (GrantPrivilegeException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    } catch (InsufficientPrivilegeException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    } catch (SchemaException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    }
                }

            } else {
                // The folder is not empty: administration privileges should be right as they
                // are checked before adding the first child.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("The group " 
                            + definition.getPath() 
                            + " is not empty so the privileges are supposed to be valid.");
                }
            }
        }
    }


    /**
     * Handles the memeberships of a group.
     * The group is added as member of the groups sepecified in the group definition
     * if it is empty and is not a preexisting group. 
     * @param session The grouper session.
     * @param groupOrStem The group.
     * @param definition The group definition which contains the administration privileges
     * to add.
     * @param values The values used to evaluate the template elements.
     */
    private void handleMembershipsForGroup(final GrouperSession session, 
            final GroupOrStem groupOrStem, 
            final GroupOrFolderDefinition definition,
            final String...values) {

        if (!definition.isPreexisting()) {
            final Group group = groupOrStem.asGroup();

            if (group.getImmediateMembers().size() == 0) {

                // The memeberships are checked for the empty groups.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("The group " 
                            + definition.getPath() 
                            + " is empty so the memberships for this group are checked.");
                }

                // Adds this group as a member of other group(s) if needed.
                for (int i = 0; i < definition.countContainingGroupsPaths(); i++) {
                    final String path = definition.getContainingGroupPath(i);
                    final GroupOrFolderDefinition containingGroupDef = definitionsManager.getDefinition(path, values);
                    final GroupOrStem containingGroupWrapper = retrieveOrCreate(session, containingGroupDef, values);
                    final Group containingGroup =  containingGroupWrapper.asGroup();
                    try {
                        final Subject subj = group.toSubject();
                        if (!containingGroup.hasImmediateMember(subj)) {
                            containingGroup.addMember(subj);
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Adding the group: " + definition.getPath() 
                                        + " as a member of: " + containingGroupDef.getPath());
                            }
                        }
                    } catch (GrouperRuntimeException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    } catch (InsufficientPrivilegeException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    } catch (MemberAddException e) {
                        LOGGER.fatal(e, e);
                        throw new WS4GrouperException(e);
                    }
                }
            } else {
                // The folder is not empty: memberships should be ok as they
                // are checked before adding the first child.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("The group " 
                            + definition.getPath() 
                            + " is not empty so the memberships are supposed to be valid.");
                }
            }
        }
    }

    /**
     * Adds admin privilege to a folder (i.e. : create and stem).
     * @param folderPath The path of the target folder.
     * @param privilegedPath The path of the group with privileges.
     * @param folder The target folder.
     * @param subject The subject that corresponds to the privileged group.
     * @throws SchemaException 
     * @throws InsufficientPrivilegeException 
     * @throws GrantPrivilegeException
     */
    private void addAdminPrivilege(final String folderPath, 
            final String privilegedPath, 
            final Stem folder, 
            final Subject subject) 
    throws GrantPrivilegeException, InsufficientPrivilegeException, SchemaException {
        addCreatePrivilege(folderPath, privilegedPath, folder, subject);
        addStemPrivilege(folderPath, privilegedPath, folder, subject);
    }
    
    /**
     * Adds create privilege to a folder.
     * @param folderPath The path of the target folder.
     * @param privilegedPath The path of the group with privileges.
     * @param folder The target folder.
     * @param subject The subject that corresponds to the privileged group.
     * @throws SchemaException 
     * @throws InsufficientPrivilegeException 
     * @throws GrantPrivilegeException
     */
    private void addCreatePrivilege(final String folderPath, 
            final String privilegedPath, 
            final Stem folder, 
            final Subject subject) 
    throws GrantPrivilegeException, InsufficientPrivilegeException, SchemaException {
        if (!folder.hasCreate(subject)) {
            folder.grantPriv(subject, Constants.CREATE_PRIV);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding create privilege to the group: " + privilegedPath 
                        + " on the folder: " + folderPath + ".");
            }
        }
    }
    
    /**
     * Adds Stem privilege to a folder.
     * @param folderPath The path of the target folder.
     * @param privilegedPath The path of the group with privileges.
     * @param folder The target folder.
     * @param subject The subject that corresponds to the privileged group.
     * @throws SchemaException 
     * @throws InsufficientPrivilegeException 
     * @throws GrantPrivilegeException
     */
    private void addStemPrivilege(final String folderPath, 
            final String privilegedPath, 
            final Stem folder, 
            final Subject subject) 
    throws GrantPrivilegeException, InsufficientPrivilegeException, SchemaException {
      
        if (!folder.hasStem(subject)) {
            folder.grantPriv(subject, Constants.STEM_PRIV);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding stem privilege to the group: " + privilegedPath 
                        + " on the folder: " + folderPath + ".");
            }
        }
    }
    
    /**
     * Adds administration privilege to a group.
     * @param groupPath The path of the target group.
     * @param privilegedPath The path of the group with privileges.
     * @param group The target group.
     * @param subject The subject that corresponds to the privileged group.
     * @throws GrantPrivilegeException
     * @throws InsufficientPrivilegeException
     * @throws SchemaException
     */
    private void addAdminPrivilege(final String groupPath, 
            final String privilegedPath, 
            final Group group, 
            final Subject subject) 
    throws GrantPrivilegeException, InsufficientPrivilegeException, SchemaException {
        
            if (!group.hasAdmin(subject)) {
                group.grantPriv(subject, Constants.ADMIN_PRIV);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Adding administration privilege to the group: " 
                            + privilegedPath + " on the group: " + groupPath);
            }
        }
    }
    
    /**
     * Adds Read privilege to a group.
     * @param groupPath The path of the target group.
     * @param privilegedPath The path of the group with privileges.
     * @param group The target group.
     * @param subject The subject that corresponds to the privileged group.
     * @throws GrantPrivilegeException
     * @throws InsufficientPrivilegeException
     * @throws SchemaException
     */
    private void addReadPrivilege(final String groupPath, 
            final String privilegedPath, 
            final Group group, 
            final Subject subject) 
    throws GrantPrivilegeException, InsufficientPrivilegeException, SchemaException {
        
        if (!group.hasRead(subject)) {
            group.grantPriv(subject, Constants.READ_PRIV);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding read privilege to the group: " 
                        + privilegedPath + " on the group: " + groupPath);
            }
        }
    }
    
    /**
     * Adds view privilege to a group.
     * @param groupPath The path of the target group.
     * @param privilegedPath The path of the group with privileges.
     * @param group The target group.
     * @param subject The subject that corresponds to the privileged group.
     * @throws GrantPrivilegeException
     * @throws InsufficientPrivilegeException
     * @throws SchemaException
     */
    private void addViewPrivilege(final String groupPath, 
            final String privilegedPath, 
            final Group group, 
            final Subject subject) 
    throws GrantPrivilegeException, InsufficientPrivilegeException, SchemaException {
        
        if (!group.hasView(subject)) {
            group.grantPriv(subject, Constants.VIEW_PRIV);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding read privilege to the group: " 
                        + privilegedPath + " on the group: " + groupPath);
            }
        }
    }

    /**
     * Creates a group or a folder.
     * @param session The Grouper session.
     * @param definition The definition of the group or folder to create.
     * @param values The values used  to evaluate templates.
     * @return The created group or folder.
     */
    protected GroupOrStem create(final GrouperSession session, 
            final GroupOrFolderDefinition definition, final String...values) {
        try {

            final String containingPath = definition.getContainingPathAsTemplate();
            final GroupOrFolderDefinition containingDef = definitionsManager.getDefinition(containingPath, values);
            final GroupOrStem containingFolderWrapper = retrieveOrCreate(session, containingDef, values);

            // The containing folder can'be retrieved.
            if (containingFolderWrapper == null) {
                final String msg = "Error the containing folder of the group " 
                    + definition.getPath() + " cant be retrieved.";
                LOGGER.fatal(msg);
                throw new WS4GrouperException(msg);
            }

            // Checks the administration privileges for the containing folder.
            handlePrivilegesForFolder(session, containingFolderWrapper, containingDef, values);
            final Stem containingFolder = containingFolderWrapper.asStem();

            // The defintion denotes a folder to create.
            if (definition.isFolder()) {

                final Stem folder = containingFolder.addChildStem(definition.getExtension(), 
                        definition.getDisplayExtension());
                folder.setDescription(definition.getDescription());

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(">>> Folder " + definition.getPath() + " created.");
                }

                final GroupOrStem gos = new GroupOrStem(folder); 
                handlePrivilegesForFolder(session, gos, definition, values);
                return gos;
            }

            // The definition denotes a group to create.
            final Group group = containingFolder.addChildGroup(definition.getExtension(), 
                    definition.getDisplayExtension());
            group.setDescription(definition.getDescription());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(">>> Group " + definition.getPath() + " created.");
            }

            final GroupOrStem gos = new GroupOrStem(group); 
            handlePrivilegesForGroup(session, gos, definition, values);
            handleMembershipsForGroup(session, gos, definition, values);
            return gos;

        } catch (InsufficientPrivilegeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (StemAddException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (StemModifyException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GroupAddException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GroupModifyException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        } catch (GrouperRuntimeException e) {
            LOGGER.fatal(e, e);
            throw new WS4GrouperException(e);
        }
    }



    /**
     * Removes a member from its groups.
     * @param session The grouper session.
     * @param userId The id of the member.
     * @return The Grouper operation result.
     */
    public GrouperOperationResultDTO removeFromAllGroups(final GrouperSession session, 
            final String userId) {
        try {

            final Subject subject = SubjectFinder.findById(userId);
            final Member member = MemberFinder.findBySubject(session, subject);
            @SuppressWarnings("unchecked")
            final Set memberships = member.getImmediateMemberships();

            for (Object o : memberships) {
                final Membership m = (Membership) o;
                final Group g = m.getGroup();
                if (session.getSubject().equals(g.getCreateSubject())) {
                    m.getGroup().deleteMember(subject);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Removes subject: " + userId 
                                + " from group: " + m.getGroup());
                    }

                    handlesEmptyGroupIfNeeded(session, m.getGroup());
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("User " + userId + " not removed from " 
                                + g.getName() + ": group not managed by the module.");

                    }
                }

            }
            return GrouperOperationResultDTO.RESULT_OK;

        } catch (SubjectNotFoundException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (SubjectNotUniqueException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (MemberNotFoundException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (InsufficientPrivilegeException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (MemberDeleteException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (GroupNotFoundException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (WS4GrouperException e) {
            return new GrouperOperationResultDTO(e);
        }
    }

    /**
     * Handles a folder which may (or not) be empty.
     * Depending on the strategy, the folder may be deleted if it is empty.
     * This method does nothing if the group is not empty or is a preexisting one.
     * @param session The Grouper session. 
     * @param folder The folder to handle.
     */
    protected void handlesEmptyFolderIfNeeded(final GrouperSession session, 
            final Stem folder) {
        if (deleteEmptyFolders) {
            final String folderName = folder.getName();
            try {
                final boolean canDelete = folder.getCreateSubject().equals(session.getSubject());
                if (!definitionsManager.isPreexistingDefinition(folderName) && canDelete) {

                    // Checks if the folder has to be deleted.
                    int nbChildren = folder.getChildGroups().size();
                    if (nbChildren == 0) {
                        nbChildren = folder.getChildStems().size(); 
                    }
                    if (nbChildren == 0) {

                        final Stem containingFolder = folder.getParentStem();
                        folder.delete();
                        handlesEmptyFolderIfNeeded(session, containingFolder);



                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("The folder " + folderName + " is deleted as it is empty.");
                        }
                    } else {
                        // The folder still contains children so it is not deleted.
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("The folder " + folderName 
                                    + " contains now: " 
                                    + nbChildren + " children - Not deleted.");
                        }
                    }
                }
            }  catch (StemNotFoundException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (InsufficientPrivilegeException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (StemDeleteException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (GrouperRuntimeException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (SubjectNotFoundException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            }
        }
    }

    /**
     * Handles a group which may (or not) be empty.
     * Depending on the strategy, the group may be deleted if it is empty.
     * This method does nothing if the group is not empty or is a preexisting one. 
     * @param group The group to handle.
     * @param session The Grouper session.
     */
    protected void handlesEmptyGroupIfNeeded(final GrouperSession session, final Group group) {
        if (deleteEmptyGroups) {
            final String groupName = group.getName();
            try {
                final boolean canDelete = group.getCreateSubject().equals(session.getSubject());

                if (!definitionsManager.isPreexistingDefinition(groupName) && canDelete) {

                    // Checks if the group has to be deleted.
                    final int nbMembers = group.getImmediateMembers().size(); 

                    if (nbMembers == 0) {
                        // The group has to be deleted.
                        @SuppressWarnings("unchecked")
                        Set containingGroups = group.toMember().getImmediateMemberships();
                        final Stem folder = group.getParentStem();
                        group.delete();
                        handlesEmptyFolderIfNeeded(session, folder);
                        for (Object containingGroupObj : containingGroups) {
                            final Membership containingGroup = (Membership) containingGroupObj;
                            handlesEmptyGroupIfNeeded(session, containingGroup.getGroup());
                        }

                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("The group " + groupName + " is deleted as it is empty.");
                        }

                    } else {
                        // The group stil contains members so it is not deleted.
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("The group " + groupName
                                    + " contains now: " 
                                    + nbMembers + " member(s) - Not deleted.");
                        }
                    }
                }
            } catch (GroupDeleteException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (InsufficientPrivilegeException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (GroupNotFoundException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (GrouperRuntimeException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            } catch (SubjectNotFoundException e) {
                LOGGER.error(e, e);
                throw new WS4GrouperException(e);
            }
        }
    }
    /**
     * Removes a member of a group.
     * @param session The grouper session. 
     * @param definition The group definitnion.
     * @param subjectId The subject Id.
     * @return The result of the Grouper operation.
     */
    public GrouperOperationResultDTO removeMember(final GrouperSession session,
            final GroupOrFolderDefinition definition, 
            final String subjectId) {
        try {
            final Subject subj = SubjectFinder.findById(subjectId);
            final GroupOrStem groupWrapper = retrieve(session, definition);
            if (groupWrapper == null) {
                final String msg = "The group: " + definition.getPath()  
                + " can't be retrieved while removing member: "
                + subjectId + ".";
                LOGGER.error(msg);
                throw new WS4GrouperException(msg);
            }
            final Group group = groupWrapper.asGroup();
            if (group.hasMember(subj)) {

                // The subject is removed from the group.
                group.deleteMember(subj);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Subject: " + subjectId 
                            + " removed from group: " 
                            +  definition.getPath() 
                            + ".");
                }
            } else {

                // The subject can't be removed from the group.
                final String msg = "Subject: " + subjectId 
                + " is not a memeber of: " 
                +  definition.getPath() 
                + ", so it can't be removed from it.";
                LOGGER.error(msg);
                throw new WS4GrouperException("msg");

            }

            handlesEmptyGroupIfNeeded(session, group);

            return GrouperOperationResultDTO.RESULT_OK;

        } catch (SubjectNotFoundException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (SubjectNotUniqueException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (InsufficientPrivilegeException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (MemberDeleteException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (WS4GrouperException e) {
            return new GrouperOperationResultDTO(e);
        }
    }

    /**
     * Adds a subject as a member of a group.
     * @param session The grouper session. 
     * @param definition The group definitnion.
     * @param subjectId The subject Id.
     * @param values The values used to evaluate templates.
     * @return The result of the Grouper operation.
     */
    public GrouperOperationResultDTO addMember(final GrouperSession session,
            final GroupOrFolderDefinition definition, 
            final String subjectId,
            final String...values) {
        try {
            final Subject subj = SubjectFinder.findById(subjectId);
            final GroupOrStem groupWrapper = retrieveOrCreate(session, definition, values);

            // Checks the administration privileges and the memberships of the group.
            handlePrivilegesForGroup(session, groupWrapper, definition, values);
            handleMembershipsForGroup(session, groupWrapper, definition, values);

            final Group group = groupWrapper.asGroup();
            if (group.hasMember(subj)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Subejct " + subjectId 
                            + " already member of group: " 
                            + definition.getPath());
                }
            } else {
                group.addMember(subj);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Subejct " + subjectId 
                            + " added as member of group: " 
                            + definition.getPath());
                }
            }
            return GrouperOperationResultDTO.RESULT_OK;
        } catch (SubjectNotFoundException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (SubjectNotUniqueException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (InsufficientPrivilegeException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (MemberAddException e) {
            LOGGER.error(e, e);
            return new GrouperOperationResultDTO(e);
        } catch (WS4GrouperException e) {
            return new GrouperOperationResultDTO(e);
        }
    }

    /**
     * Retrieves a group or a stem from Grouper.
     * @param session The grouper session.
     * @param definition The group or folder definition.
     * @return The Group or the folder.
     */
    protected GroupOrStem retrieve(final GrouperSession session, final GroupOrFolderDefinition definition) {

        final String name = definition.getPath();

        // The definition denotes a folder to retrieve.
        if (definition.isFolder()) {

            // The folder has to be retrieved from Grouper.
            try {
                final Stem folder = StemFinder.findByName(session, name);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Searching for folder " + name + ": Found.");
                }

                final GroupOrStem gos = new GroupOrStem(folder); 
                return gos;


            } catch (StemNotFoundException e) {
                // The folder can't be fetched from Grouper.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Searching for folder " + name + ": Not found.");
                }

                return null;
            }
        }

        // The defition denotes a group to retrieve.
        // Retrieves the group from grouper.
        try {

            // The group can't be fetched from Grouper.
            final Group group = GroupFinder.findByName(session, name);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for group " + name + ": found.");
            }

            final GroupOrStem gos = new GroupOrStem(group); 
            return gos;

        } catch (GroupNotFoundException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Searching for group: " + name + ": not found.");
            }
            return null;
        }

    }

    /**
     * Retrieves or creates a group or folder.
     * If the group or folder cant be retrieved, it is created.
     * @param session The grouper session.
     * @param definition The definition of the group or folder.
     * @param values The values used to evaluate templates.
     * @return The group or folder.
     */
    public GroupOrStem retrieveOrCreate(final GrouperSession session, 
            final GroupOrFolderDefinition definition, 
            final String...values) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RetrieveOrCreate definition: " + definition);
        }
        final GroupOrStem gos = retrieve(session, definition);
        if (gos != null) {
            return gos;
        }
        return create(session, definition, values);
    }

    /**
     * Getter for definitionsManager.
     * @return definitionsManager.
     */
    public GroupOrFolderDefinitionsManager getDefinitionsManager() {
        return definitionsManager;
    }

    /**
     * Setter for definitionsManager.
     * @param definitionsManager the new value for definitionsManager.
     */
    public void setDefinitionsManager(final GroupOrFolderDefinitionsManager definitionsManager) {
        this.definitionsManager = definitionsManager;
    }

    /**
     * Getter for deleteEmptyGroups.
     * @return deleteEmptyGroups.
     */
    public Boolean getDeleteEmptyGroups() {
        return deleteEmptyGroups;
    }

    /**
     * Setter for deleteEmptyGroups.
     * @param deleteEmptyGroups the new value for deleteEmptyGroups.
     */
    public void setDeleteEmptyGroups(final boolean deleteEmptyGroups) {
        this.deleteEmptyGroups = deleteEmptyGroups;
    }

    /**
     * Getter for deleteEmptyFolders.
     * @return deleteEmptyFolders.
     */
    public boolean getDeleteEmptyFolders() {
        return deleteEmptyFolders;
    }

    /**
     * Setter for deleteEmptyFolders.
     * @param deleteEmptyFolders the new value for deleteEmptyFolders.
     */
    public void setDeleteEmptyFolders(final boolean deleteEmptyFolders) {
        this.deleteEmptyFolders = deleteEmptyFolders;
    }

    /**
     * Getter for forcePrivileges.
     * @return forcePrivileges.
     */
    public boolean getForcePrivileges() {
        return forcePrivileges;
    }

    /**
     * Setter for forcePrivileges.
     * @param forcePrivileges the new value for forcePrivileges.
     */
    public void setForcePrivileges(final boolean forcePrivileges) {
        this.forcePrivileges = forcePrivileges;
    }
}

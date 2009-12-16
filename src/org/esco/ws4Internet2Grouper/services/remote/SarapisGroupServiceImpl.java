/**
 *   Copyright (C) 2008  GIP RECIA (Groupement d'Intérêt Public REgion 
 *   Centre InterActive)
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;


import edu.internet2.middleware.grouper.GrouperSession;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.cache.SGSCache;
import org.esco.ws4Internet2Grouper.dao.GrouperDAO;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrStem;
import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;
import org.esco.ws4Internet2Grouper.domain.beans.PersonType;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;
import org.esco.ws4Internet2Grouper.parsing.SGSParsingUtil;
import org.esco.ws4Internet2Grouper.util.GrouperSessionUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;




/** 
 * @author GIP RECIA - A. Deman
 * 11 août 08
 *
 */
public class SarapisGroupServiceImpl implements ISarapisGroupService, InitializingBean {

	/** Logger. */
	private static final Logger LOGGER = Logger.getLogger(SarapisGroupServiceImpl.class);

	/** Separator. */
	private static final String SEP = "---------------------------------";

	/** The definition manager. */
	private GroupOrFolderDefinitionsManager definitionsManager;

	/** The grouper session util class. */
	private GrouperSessionUtil grouperSessionUtil;

	/** The Grouper DAO class. */
	private GrouperDAO grouperDAO;

	/** Parsing Util. */
	private SGSParsingUtil parsingUtil;

	/**
	 * Builds an instance of SarapisGroupsServiceImpl.
	 */
	public SarapisGroupServiceImpl() {
		super();
	}

	/**
	 * Checks the sping injections.
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(this.definitionsManager, 
				"property definitionManager of class " + this.getClass().getName() 
				+ " can not be null");

		Assert.notNull(this.grouperDAO, 
				"property grouperUtil of class " + this.getClass().getName() 
				+ " can not be null");

		Assert.notNull(this.grouperSessionUtil, 
				"property grouperSessionUtil of class " + this.getClass().getName() 
				+ " can not be null");

		Assert.notNull(this.parsingUtil, 
				"property parsingUtil of class " + this.getClass().getName() 
				+ " can not be null");

		// Parses the configuration file to read the group and folders definitions.
		parsingUtil.parse();

		// Checks that the preexisting definitions. can be retrieved from Grouper.
		final Iterator<GroupOrFolderDefinition> preexitingIt = definitionsManager.preexistingDefinitions();
		final GrouperSession session = grouperSessionUtil.createSession();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(SEP);
			LOGGER.info("Checking the preexisting groups");
			LOGGER.info("and folders.");
			LOGGER.info(SEP);
		}

		while (preexitingIt.hasNext()) {
			final GroupOrFolderDefinition definition = preexitingIt.next();
			if (grouperDAO.exists(session, definition)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Preexisting definition " + definition.getPath() + " checked.");
				}
			} else {
				// Error: one of the preexisting group or folder can't be retrieved from Grouper.
				String msg = "Unable to retrieve ";
				if (definition.isGroup()) {
					msg += "group";
				} else {
					msg += "folder";
				}
				msg += " for the preexisting definition: " + definition;
				LOGGER.fatal(msg);
				grouperSessionUtil.stopSession(session);
				throw new WS4GrouperException(msg);
			}
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(SEP);
			LOGGER.info("Preexisting definitions checked.");
			LOGGER.info(SEP);
		}

		// Creates the group or folders that have to be created even if they have no mebers.
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(SEP);
			LOGGER.info("Creating Empty groups groups or folders (if needed).");
			LOGGER.info(SEP);
		}
		final Iterator<GroupOrFolderDefinition> createIt = definitionsManager.getGroupsOrFoldersToCreate();
		while (createIt.hasNext()) {
			final GroupOrFolderDefinition def = createIt.next();
			final GroupOrStem result = grouperDAO.retrieveOrCreate(session, def);

			if (result == null) {
				// Error : One group or folder definition can't be retrieved or created.
				String msg = "Error while creating group or folder for the definition: " + def;
				LOGGER.fatal(msg);
				grouperSessionUtil.stopSession(session);
				throw new WS4GrouperException(msg);
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(SEP);
			LOGGER.info("Groups and folders created.");
			LOGGER.info(SEP);
		}

		grouperSessionUtil.stopSession(session);
	}



	/**
	 * Handles the groups or folders definition template to create even if there is no
	 * memebrs to add.
	 * @param session The Grouper session.
	 * @param attributes The attributes used to evaluate the template elements.
	 * @return The Grouper operation result.
	 */
	protected GrouperOperationResultDTO handlesEmptyGroupsOrFoldersDefinitionTemplates(
			final GrouperSession session, 
			final String...attributes) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("Handles empty groups or folders templates to create.");
			LOGGER.debug(SEP);
		}
		final Iterator<GroupOrFolderDefinition> it = 
			definitionsManager.getGroupsOrFoldersTemplatesToCreate(attributes);
		
		while (it.hasNext()) {

			final GroupOrFolderDefinition def =  it.next();
			if (SGSCache.instance().emptyTemplateIsCached(def)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Definition " + def + " already handled.");
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Definition: " + def + ".");
				}

				final GroupOrStem result = grouperDAO.retrieveOrCreate(session, def, attributes);
				if (result == null) {
					final StringBuilder msg = new StringBuilder("Error while creating empty ");
					msg.append("group or folder template for the definition: ");
					msg.append(def);
					msg.append(" with the attribute values: ");
					msg.append(attributes);
					msg.append(".");
					LOGGER.fatal(msg);
					return new GrouperOperationResultDTO(new WS4GrouperException(msg.toString()));
				}
				SGSCache.instance().cacheEmptyTemplate(def);
			}   
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("End of empty creation of group or folder templates.");
			LOGGER.debug(SEP);
		}

		return GrouperOperationResultDTO.RESULT_OK;
	}


	/**
	 * Updates the memberships for a given user and a set of attributes.
	 * @param type The type of user (student, teacher, etc.)
	 * @param userId The id of the user.
	 * @param attributes The user attributes.
	 * @return The grouper operation result.
	 */
	protected GrouperOperationResultDTO updateGroups(final PersonType type, 
			final String userId, 
			final String...attributes) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("Starting to update groups ");
			LOGGER.debug("for the user: " + userId);
			LOGGER.debug("Type: " + type + ".");
			LOGGER.debug(SEP);
		}
		final GrouperSession session = grouperSessionUtil.createSession();
		final Set<String> previousManagedGroups =  new HashSet<String>();
		GrouperOperationResultDTO result = 
			grouperDAO.retrieveManagedGroups(session, userId, previousManagedGroups);

		if (result.isError()) {
			LOGGER.error("Error while retrieving the previous managed groups for user: " + userId);
			LOGGER.error(result.getException(), result.getException());
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Previous managed groups " + previousManagedGroups);
			}

			result = updateGroups(type, userId, session, previousManagedGroups, attributes);
		}
		grouperSessionUtil.stopSession(session);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("End of updating groups.");
			LOGGER.debug(SEP);
		}

		return result;
	}

	/**
	 * Handles the memberships for a given user.
	 * @param type The type of user (student, teacher, etc.)
	 * @param userId The id of the user.
	 * @param attributes The user attributes.
	 * @return The grouper operation result.
	 */
	protected GrouperOperationResultDTO addToGroups(final PersonType type, 
			final String userId, 
			final String...attributes) {
		final GrouperSession session = grouperSessionUtil.createSession();
		GrouperOperationResultDTO result = handlesEmptyGroupsOrFoldersDefinitionTemplates(session, attributes);
		if (!result.isError()) {
			result = addToGroups(type, userId, session, attributes);
		}
		grouperSessionUtil.stopSession(session);
		return result;
	}

	/**
	 * Handles the memberships for a given user.
	 * @param type The type of user (student, teacher, etc.)
	 * @param userId The id of the user.
	 * @param session The Grouper session.
	 * @param attributes The user attributes.
	 * @return The grouper operation result.
	 */
	protected GrouperOperationResultDTO addToGroups(final PersonType type, 
			final String userId,
			final GrouperSession session, 
			final String...attributes) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("Starting to add to groups");
			LOGGER.debug("for the user: " + userId);
			LOGGER.debug("Type: " + type + ".");
			LOGGER.debug(SEP);
		}

		// Handles specific memberships.
		final Iterator<GroupOrFolderDefinition> specificMemberships = 
			definitionsManager.getMemberships(type, attributes);

		while (specificMemberships.hasNext()) {
			final GrouperOperationResultDTO result = grouperDAO.addMember(session, 
					specificMemberships.next(), userId, attributes);
			if (result.isError()) {
				return result;
			}
		}

		// Handles memberships for all type of persons.
		final Iterator<GroupOrFolderDefinition> allMemberships = 
			definitionsManager.getMemberships(PersonType.ALL, attributes);

		while (allMemberships.hasNext()) {
			final GrouperOperationResultDTO result = grouperDAO.addMember(session, 
					allMemberships.next(), userId, attributes);
			if (result.isError()) {
				return result;
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("End of adding to groups.");
			LOGGER.debug(SEP);
		}

		return GrouperOperationResultDTO.RESULT_OK;
	}

	/**
	 * Handles the memberships for a given user.
	 * @param type The type of user (student, teacher, etc.)
	 * @param userId The id of the user.
	 * @param session The Grouper session.
	 * @param previousManagedGroups The previous managed groups of the user.
	 * @param attributes The user attributes.
	 * @return The grouper operation result.
	 */
	protected GrouperOperationResultDTO updateGroups(final PersonType type, 
			final String userId,
			final GrouperSession session,
			final Set<String> previousManagedGroups,
			final String...attributes) {


		// Handles specific memberships.
		final Iterator<GroupOrFolderDefinition> specificMemberships = 
			definitionsManager.getMemberships(type, attributes);

		while (specificMemberships.hasNext()) {
			final GroupOrFolderDefinition groupDefinition = specificMemberships.next();

			if (previousManagedGroups.contains(groupDefinition.getPath())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User " + userId + " is already a member of the group " 
							+ groupDefinition.getPath());
				}
			} else {
				final GrouperOperationResultDTO result = grouperDAO.addMember(session, 
						groupDefinition, userId, attributes);

				if (result.isError()) {
					return result;
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User " + userId + " is added to the group " 
							+ groupDefinition.getPath());
				}
			}
			previousManagedGroups.remove(groupDefinition.getPath());
		}

		// Handles memberships for all type of persons.
		final Iterator<GroupOrFolderDefinition> allMemberships = 
			definitionsManager.getMemberships(PersonType.ALL, attributes);

		while (allMemberships.hasNext()) {
			final GroupOrFolderDefinition groupDefinition = allMemberships.next();

			if (previousManagedGroups.contains(groupDefinition.getPath())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User " + userId + " is already a member of the group " 
							+ groupDefinition.getPath());
				}
			} else {
				final GrouperOperationResultDTO result = grouperDAO.addMember(session, 
						groupDefinition, userId, attributes);
				if (result.isError()) {
					return result;
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User " + userId + " is added to the group " 
							+ groupDefinition.getPath());
				}
			}
			previousManagedGroups.remove(groupDefinition.getPath());
		}

		// Removes the user from its old managed groups.
		final GrouperOperationResultDTO result = 
			grouperDAO.removeFromGroups(session, userId, previousManagedGroups);
		if (result.isError()) {
			return result;
		}

		return GrouperOperationResultDTO.RESULT_OK;
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
	 * Getter for grouperDAO.
	 * @return grouperDAO.
	 */
	public GrouperDAO getGrouperDAO() {
		return grouperDAO;
	}

	/**
	 * Setter for grouperUtil.
	 * @param grouperDAO the new value for grouperDAO.
	 */
	public void setGrouperDAO(final GrouperDAO grouperDAO) {
		this.grouperDAO = grouperDAO;
	}

	/**
	 * Getter for grouperSessionUtil.
	 * @return grouperSessionUtil.
	 */
	public GrouperSessionUtil getGrouperSessionUtil() {
		return grouperSessionUtil;
	}

	/**
	 * Setter for grouperSessionUtil.
	 * @param grouperSessionUtil the new value for grouperSessionUtil.
	 */
	public void setGrouperSessionUtil(final GrouperSessionUtil grouperSessionUtil) {
		this.grouperSessionUtil = grouperSessionUtil;
	}

	/**
	 * Getter for parsingUtil.
	 * @return parsingUtil.
	 */
	public SGSParsingUtil getParsingUtil() {
		return parsingUtil;
	}

	/**
	 * Setter for parsingUtil.
	 * @param parsingUtil the new value for parsingUtil.
	 */
	public void setParsingUtil(final SGSParsingUtil parsingUtil) {
		this.parsingUtil = parsingUtil;
	}


	/**
	 * Adds a person to groups.
	 * @param personDescription The description of the person.
	 * @return The result object which contains the informations about how the operation
	 * has been performed.
	 */
	public GrouperOperationResultDTO addToGroups(final IEntityDescription personDescription) {
		GrouperOperationResultDTO result = GrouperOperationResultDTO.RESULT_OK;
		for (String[] attrValues : personDescription.getValuesArrays()) {
			result =  addToGroups(personDescription.getType(), 
					personDescription.getId(),
					attrValues);
			if (result.isError()) {
				return result;
			}
		}
		return result;
	}

	/**
	 * Updates the memberships of a person.
	 * @param personDescription The description of the person.
	 * @return The result object which contains the informations about how the operation
	 * has been performed.
	 */
	public GrouperOperationResultDTO updateMemberships(final IEntityDescription personDescription) {
		GrouperOperationResultDTO result = GrouperOperationResultDTO.RESULT_OK;
		for (String[] attrValues : personDescription.getValuesArrays()) {
			result =  updateGroups(personDescription.getType(), 
					personDescription.getId(), attrValues);
			if (result.isError()) {
				return result;
			}
		}
		return result;
	}

	/**
	 * Removes a person from the groups, including the groups not managed by this service.
	 * @param userId The id of the person to remove from the groups.
	 * @return The result of the Grouper operation.
	 */
	public GrouperOperationResultDTO removeFromAllGroups(final String userId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("Starting to remove from all groups ");
			LOGGER.debug("for the user: " + userId);
			LOGGER.debug(SEP);
		}
		final GrouperSession session = grouperSessionUtil.createSession();
		GrouperOperationResultDTO result = grouperDAO.removeFromAllGroups(session, userId);

		if (result.isError()) {
			LOGGER.error("Error while removing from groups for user: " + userId);
			LOGGER.error(result.getException(), result.getException());
		} 
		grouperSessionUtil.stopSession(session);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("End of removing from groups.");
			LOGGER.debug(SEP);
		}

		return result; 
	}
	/**
	 * Removes a person from the groups, including the groups not managed by this service.
	 * @param userId The id of the person to remove from the groups.
	 * @return The result of the Grouper operation.
	 */
	public GrouperOperationResultDTO removeFromManagedGroups(final String userId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("Starting to remove from managed groups ");
			LOGGER.debug("for the user: " + userId);
			LOGGER.debug(SEP);
		}
		final GrouperSession session = grouperSessionUtil.createSession();
		GrouperOperationResultDTO result = grouperDAO.removeFromManagedGroups(session, userId);

		if (result.isError()) {
			LOGGER.error("Error while removing from groups for user: " + userId);
			LOGGER.error(result.getException(), result.getException());
		} 
		grouperSessionUtil.stopSession(session);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(SEP);
			LOGGER.debug("End of removing from groups.");
			LOGGER.debug(SEP);
		}

		return result; 
	}
}

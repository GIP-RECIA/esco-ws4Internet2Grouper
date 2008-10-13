/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;

import org.esco.ws4Internet2Grouper.domain.beans.GrouperDTO;



 
/**
 * Interface for the web service used to expose some methods of the grouper api.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 */
public interface IGrouperAPIExposer {
	
	/**
	 * Tests if a group contains a subject.
	 * @param groupName The name of the group.
	 * @param subjectId The id of the subject.
	 * @return True if the subject is member of the group.
	 */
	boolean hasMember(final String groupName, final String subjectId);
	
	/**
	 * Tests if a group (or stem) or its subgroups contains a subject.
	 * @param groupName The name of the group.
	 * @param subjectId The id of the subject.
	 * @return True if the subject is member of the group or of one of it subgroups.
	 */
	boolean hasDeepMember(final String groupName, final String subjectId);
	
	/**
	 * Returns the DTO that denotes a group or a stem.
	 * @param key The key of the group or stem to search for.
	 * @return The group if it can be found, null otherwise.
	 */
	GrouperDTO findGroupOrStem(final String key); 
	
	
	/**
	 * Gives the root groups from a given stem. 
	 * @param key The name of the stem.
	 * @return The list of the groups in the specified stem its child stems. 
	 */ 
	GrouperDTO[] getAllRootGroupsFromStem(final String key);
	
	/**
	 * Gives the subjects members of the group.
	 * @param key The name of the group/stem.
	 * @return The terminal elements members of the group/stem 
	 * (i.e. all the terminal elements contained in the group and subgroups).
	 */
	GrouperDTO[] getMemberSubjects(final String key);
	
	/**
	 * Gives the groups members of a group or stem.
	 * @param key The key of the group/stem.
	 * @return subgroups/stems.
	 */
	GrouperDTO[] getMemberGroups(final String key);
	
	/**
	 * Gives all the groups and subjects that are members of this group or stem.
	 * @param key The key of the target group or stem.
	 * @return All the members of the group or stem denoted by the key.
	 */
	GrouperDTO[] getAllMembers(final String key);

	/**
	 * Gives all the groups that are members of this group or stem.
	 * @param key The key of the target group or stem.
	 * @return All the groups members of the group or stem.
	 */
	GrouperDTO[] getAllMemberGroups(final String key);
	
	/**
	 * Gives all the subjects that are members of this group or stem.
	 * @param key The key of the target group or stem.
	 * @return All the subjects members of the group or stem.
	 */
	GrouperDTO[] getAllMemberSubjects(final String key);
	
	 /**
	  * If the key denotes a group, gives the group descriptions of the groups to which 
	  * a given group belongs to. If the key corresponds to a stem, gives the parent stem.
	  * @param key The key of the considered group/stem.
	  * @return The list of the groups which the group identified by key belongs to, 
	  * or the parent stem in the case of a stem.
	  */ 
	GrouperDTO[] getMembershipsForGroupOrStem(final String key);
	
	/**
	 * Gives the groups description of the groups to which a given subject belongs to.
	 * @param subjectKey The key of the considered subject.
	 * @return The list of the groups which the subject identified by subjectKey belongs to. 
	 */
	GrouperDTO[] getMembershipsForSubject(final String subjectKey);
	
	/**
	 * Search groups or stems whose name matches the query string according to the specified method.
	 * @param query The part of the name of the group.
	 * @param method The method used to perform the comparison.
	 * @return The array of groups or stems descriptions whose name match query according 
	 * to the method of comparison.
	 */
	GrouperDTO[]searchForGroupsOrStems(final String query, final int method);
	
	/**
	 * Search subjects whose name matches the query string according to the specified method.
	 * @param query The part of the name of the subject.
	 * @param method The method used to perform the comparison.
	 * @return The array of subject id whose name match query according 
	 * to the method of comparison.
	 */
	String[]searchForSubjects(final String query, final int method);

}

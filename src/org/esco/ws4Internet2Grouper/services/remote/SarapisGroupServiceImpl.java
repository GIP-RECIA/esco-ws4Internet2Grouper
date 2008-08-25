/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;


import edu.internet2.middleware.grouper.GrouperSession;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;
import org.esco.ws4Internet2Grouper.domain.beans.MembersDefinition.MembersType;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;
import org.esco.ws4Internet2Grouper.parsing.SGSParsingUtil;
import org.esco.ws4Internet2Grouper.util.GrouperSessionUtil;
import org.esco.ws4Internet2Grouper.util.GrouperUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.Assert;




/** 
 * @author GIP RECIA - A. Deman
 * 11 août 08
 *
 */
public class SarapisGroupServiceImpl implements ISarapisGroupsService, InitializingBean {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SarapisGroupServiceImpl.class);

    /** Separator. */
    private static final String SEP = "---------------------------------";

    /** The definition manager. */
    private GroupOrFolderDefinitionsManager definitionsManager;

    /** The grouper session util class. */
    private GrouperSessionUtil grouperSessionUtil;

    /** The Grouper util class. */
    private GrouperUtil grouperUtil;

    /** Parsing Util. */
    private SGSParsingUtil parsingUtil;

    /**
     * Builds an instance of SarapisGroupServiceImpl.
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

        Assert.notNull(this.grouperUtil, 
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
            if (grouperUtil.exists(session, definition)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Preexistion definition " + definition.getPath() + " checked.");
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
        grouperSessionUtil.stopSession(session);
    }

    /**
     * Removes the memebrships for a given user and a set of attributes.
     * @param type The type of user (student, teacher, etc.)
     * @param userId The id of the user.
     * @param attributes The user attributes.
     * @return The grouper operation result.
     */
    protected GrouperOperationResultDTO removeFromGroups(final MembersType type, 
            final String userId, 
            final String...attributes) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(SEP);
            LOGGER.debug("Starting to remove from groups ");
            LOGGER.debug("for the user: " + userId);
            LOGGER.debug("Type: " + type + ".");
            LOGGER.debug(SEP);
        }

        // Handles specific memeberships.
        final GrouperSession session = grouperSessionUtil.createSession();
        final Iterator<GroupOrFolderDefinition> specificMemberships = 
            definitionsManager.getMembershipsForTemplates(type, attributes);

        while (specificMemberships.hasNext()) {
            final GrouperOperationResultDTO result = grouperUtil.removeMember(session, 
                    specificMemberships.next(), userId);
            if (result.isError()) {
                return result;
            }
        }

        // Handle memberships for all type of persons.
        final Iterator<GroupOrFolderDefinition> allMemberships = 
            definitionsManager.getMembershipsForTemplates(MembersType.ALL, attributes);

        while (allMemberships.hasNext()) {
            final GrouperOperationResultDTO result = grouperUtil.removeMember(session, 
                    allMemberships.next(), userId);
            if (result.isError()) {
                return result;
            }
        }

        grouperSessionUtil.stopSession(session);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(SEP);
            LOGGER.debug("End of removing from groups.");
            LOGGER.debug(SEP);
        }

        return GrouperOperationResultDTO.RESULT_OK;
    }

    /**
     * Handles the memebrships for a given user.
     * @param type The type of user (student, teacher, etc.)
     * @param userId The id of the user.
     * @param attributes The user attributes.
     * @return The grouper operation result.
     */
    protected GrouperOperationResultDTO addToGroups(final MembersType type, 
            final String userId, 
            final String...attributes) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(SEP);
            LOGGER.debug("Starting to add to groups");
            LOGGER.debug("for the user: " + userId);
            LOGGER.debug("Type: " + type + ".");
            LOGGER.debug(SEP);
        }
        
        // Handles specific memeberships.
        final GrouperSession session = grouperSessionUtil.createSession();
        final Iterator<GroupOrFolderDefinition> specificMemberships = 
            definitionsManager.getMemberships(type, attributes);

        while (specificMemberships.hasNext()) {
            final GrouperOperationResultDTO result = grouperUtil.addMember(session, 
                    specificMemberships.next(), userId, attributes);
            if (result.isError()) {
                return result;
            }
        }

        // Handles memberships for all type of persons.
        final Iterator<GroupOrFolderDefinition> allMemberships = 
            definitionsManager.getMemberships(MembersType.ALL, attributes);

        while (allMemberships.hasNext()) {
            final GrouperOperationResultDTO result = grouperUtil.addMember(session, 
                    allMemberships.next(), userId, attributes);
            if (result.isError()) {
                return result;
            }
        }

        grouperSessionUtil.stopSession(session);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(SEP);
            LOGGER.debug("End of adding to groups.");
            LOGGER.debug(SEP);
        }

        return GrouperOperationResultDTO.RESULT_OK;
    }


    /**
     * Adds an administrative employee to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #addAdministrativeToEstablishment(String, String, String)
     */
    public GrouperOperationResultDTO addAdministrativeToEstablishment(final  String establishmentUAI, 
            final String establishmentName, 
            final String userId) {
        return addToGroups(MembersType.ADMINISTRATIVE, userId, establishmentUAI, establishmentName);
    }

    /**
     * Adds a parent to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the parent.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #addParentToEstablishment(String, String, String)
     */
    public GrouperOperationResultDTO addParentToEstablishment(final  String establishmentUAI, 
            final String establishmentName, 
            final String userId) {
        return addToGroups(MembersType.PARENT, userId, establishmentUAI, establishmentName);
    }

    /**
     * Adds a student to a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param classDescription The description of the class.
     * @param userId The id of the student.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #addStudentToClass(String, String, String, String, String, String)
     */
    public GrouperOperationResultDTO addStudentToClass(final  String establishmentUAI,
            final String establishmentName, 
            final String level, 
            final String className,
            final String classDescription, 
            final String userId) {

        return addToGroups(MembersType.STUDENT, 
                userId,
                establishmentUAI, 
                establishmentName, 
                level, 
                className, 
                classDescription);
    }

    /**
     * Adds a teacher to a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param classDescription The description of the class.
     * @param userId The id of the teacher.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #addTeacherToClass(String, String, String, String, String, String)
     */
    public GrouperOperationResultDTO addTeacherToClass(final  String establishmentUAI,
            final String establishmentName, 
            final String level, 
            final String className,
            final String classDescription, 
            final String userId) {
        return addToGroups(MembersType.TEACHER, 
                userId,
                establishmentUAI, 
                establishmentName, 
                level, 
                className, 
                classDescription);
    }

    /**
     * Adds a teacher to a list of disciplines in an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param disciplines The list of diciplines for the teacher.
     * @param userId The id of the teacher.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #addTeacherToDisciplines(java.lang.String, java.lang.String, java.util.List, java.lang.String)
     */
    public GrouperOperationResultDTO addTeacherToDisciplines(final String establishmentUAI, 
            final String establishmentName,
            final List<String> disciplines, 
            final String userId) {
        GrouperOperationResultDTO result = GrouperOperationResultDTO.RESULT_OK;
        for (String discipline : disciplines) {
            result =  addToGroups(MembersType.TEACHER, userId, 
                    establishmentUAI, 
                    establishmentName, 
                    "",
                    "",
                    "",
                    discipline);
            if (result.isError()) {
                return result;
            }
        }
        return result;
    }

    /**
     * Adds a TOS employee to an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #addTOSToEstablishment(String, String, String)
     */
    public GrouperOperationResultDTO addTOSToEstablishment(final String establishmentUAI, 
            final String establishmentName, 
            final String userId) {
        return addToGroups(MembersType.TOS,  
                userId,
                establishmentUAI, 
                establishmentName);
    }

    /**
     * Removes an administrative employee from an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #removeAdministrativeFromEstablishment(String, String, String)
     */
    public GrouperOperationResultDTO removeAdministrativeFromEstablishment(final String establishmentUAI, 
            final String establishmentName, final String userId) {
        return removeFromGroups(MembersType.ADMINISTRATIVE, userId, establishmentUAI, establishmentName);
    }

    /**
     * Removes a parent from an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the parent.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #removeParentFromEstablishment(String, String, String)
     */
    public GrouperOperationResultDTO removeParentFromEstablishment(
            final String establishmentUAI, 
            final String establishmentName, 
            final String userId) {
        return removeFromGroups(MembersType.PARENT, userId, establishmentUAI, establishmentName);
    }

    /**
     * Removes a person from all his establishment groups.
     * @param userId The id of the user.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #removePersonFromAllEstablishmentGroups(String)
     */
    public GrouperOperationResultDTO removePersonFromAllEstablishmentGroups(final String userId) {
        final GrouperSession session = grouperSessionUtil.createSession();
        final GrouperOperationResultDTO result =  grouperUtil.removeFromAllGroups(session, userId);
        grouperSessionUtil.stopSession(session);
        return result;
    }

    /**
     * Removes a student from a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param userId The id of the student.
     * @return The object that denotes the result of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #removeStudentFromClass(String, String, String, String, String)
     */
    public GrouperOperationResultDTO removeStudentFromClass(final String establishmentUAI, 
            final String establishmentName, 
            final String level,
            final String className, 
            final String userId) {
        return removeFromGroups(MembersType.STUDENT, userId, establishmentUAI, establishmentName, level, className);
    }

    /**
     * Removes a teacher from a class of an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param level The level of the class.
     * @param className The name of the class.
     * @param userId The id of the teacher.
     * @return The object that denotes the result of the operation.
     */
    public GrouperOperationResultDTO removeTeacherFromClass(final String establishmentUAI, 
            final String establishmentName, 
            final String level,
            final String className, 
            final String userId) {
        return removeFromGroups(MembersType.TEACHER, userId, establishmentUAI, establishmentName, level, className);
    }

    /**
     * Removes a teacher from a list of disciplines in an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param disciplines The list of diciplines.
     * @param userId The id of the teacher.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #removeTeacherFromDisciplines(String, String, List, String)
     */
    public GrouperOperationResultDTO removeTeacherFromDisciplines(final String establishmentUAI, 
            final String establishmentName,
            final List<String> disciplines, 
            final String userId) {
        GrouperOperationResultDTO result = GrouperOperationResultDTO.RESULT_OK;
        for (String discipline : disciplines) {
            result =  removeFromGroups(MembersType.TEACHER, userId, 
                    establishmentUAI, 
                    establishmentName, 
                    "",
                    "",
                    "",
                    discipline);
            if (result.isError()) {
                return result;
            }
        }
        return result;
    }

    /**
     * Removes a TOS employee from an establishment.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @return The result object of the operation.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupsService
     * #removeTOSFromEstablishment(String, String, String)
     */
    public GrouperOperationResultDTO removeTOSFromEstablishment(final String establishmentUAI, 
            final String establishmentName, 
            final String userId) {
        return removeFromGroups(MembersType.TOS, userId, establishmentUAI, establishmentName);
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
     * Getter for grouperUtil.
     * @return grouperUtil.
     */
    public GrouperUtil getGrouperUtil() {
        return grouperUtil;
    }

    /**
     * Setter for grouperUtil.
     * @param grouperUtil the new value for grouperUtil.
     */
    public void setGrouperUtil(final GrouperUtil grouperUtil) {
        this.grouperUtil = grouperUtil;
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


    public static void main(final String args[]) {

        final ThreadLocal<ApplicationContext> springCtx = new ThreadLocal<ApplicationContext>();
        springCtx.set(new FileSystemXmlApplicationContext("classpath:properties/applicationContext.xml"));
        final ISarapisGroupsService sgs = (ISarapisGroupsService) springCtx.get().getBean("SarapisGroupService");
        final String usersPrefix = "STRESS_TEST__Person_";
        final int nbUsers = 3000;
        final int thousand = 1000;


        final int nbStudents = 00;
        final int nbTeachers = 3;
        final int nbParents = 00;
        final int nbAdministrative = 00;
        final int nbTos = 00;
        final int nbEstab = 1;
        final int nbLevels = 3;
        final int nbClasses = 3;
        final int nbDisciplines = 10;
        final int nbMaxDisplPerTeacher = 5;
        final String[] estabNames = new String[nbEstab];
        final String[] estabUAI = new String[nbEstab];
        final String[] levels = new String[nbLevels];
        final String[] classesNames = new String[nbClasses];
        final String[] classesDesc = new String[nbClasses];
        final String[] disciplinesNames = new String[nbDisciplines];

        for (int  i = 0; i < nbEstab; i++) {
            estabNames[i] = "Estab_name_" + i;
            estabUAI[i] = "Estab_UAI_" + i;
        }

        for (int  i = 0; i < nbLevels; i++) {
            levels[i] = "Level_" + i;
        }

        for (int  i = 0; i < nbClasses; i++) {
            classesNames[i] = "Class_name_" + i;
            classesDesc[i] = "Class_desc_" + i;
        }
        
        for (int  i = 0; i < nbDisciplines; i++) {
            disciplinesNames[i] = "discipl_name_" + i;
        }

        final Random rand = new Random();
        rand.setSeed(1L);

        int userIndex = 0;
        long top1 = System.currentTimeMillis();
        System.out.println("--- Students: " + nbStudents + " ---");
        for (int i = 0; i < nbStudents; i++) {
            final int estabInd = userIndex % nbEstab;
            final int levelInd = userIndex % nbLevels;
            final int classInd = userIndex % nbClasses;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.addStudentToClass(estabUAI[estabInd], 
                    estabNames[estabInd], levels[levelInd], 
                    classesNames[classInd], classesDesc[classInd], userId);
            System.out.println(userId + " ==> " + result);
        }

        long ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End students (" + ellapsed + "s) ---");



        System.out.println("\n\n\n--- Teachers : " + nbTeachers + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbTeachers; i++) {
            rand.setSeed(userIndex);
            final int estabInd = userIndex % nbEstab;
            final int levelInd = userIndex % nbLevels;
            final int classInd = userIndex % nbClasses;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.addTeacherToClass(estabUAI[estabInd], 
                    estabNames[estabInd], levels[levelInd], 
                    classesNames[classInd], classesDesc[classInd], userId);
            System.out.println(userId + " to class  ==> " + result);
            
            // Builds a random list of dispciplines for the teacher
            List<String> discipl = new ArrayList<String>();
            int nbDispl = rand.nextInt(nbMaxDisplPerTeacher - 1) + 1;
            for (int j = 0; j < nbDispl; j++) {
                String d = disciplinesNames[rand.nextInt(nbDisciplines)];
                if (!discipl.contains(d)) {
                    discipl.add(d);
                }
            }
            
            result = sgs.addTeacherToDisciplines(estabUAI[estabInd], 
                    estabNames[estabInd], discipl, userId);
            System.out.println(userId + " to dsciplines  ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End teachers (" + ellapsed + "s) ---");

        
        
        System.out.println("\n\n\n--- Parents: " + nbParents + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbParents; i++) {
            final int estabInd = userIndex % nbEstab;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.addParentToEstablishment(estabUAI[estabInd], 
                    estabNames[estabInd], userId);
            System.out.println(userId + " ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End parents (" + ellapsed + "s) ---");

        System.out.println("\n\n\n--- Administrative employees: " + nbAdministrative + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbAdministrative; i++) {
            final int estabInd = rand.nextInt(nbEstab);
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.addAdministrativeToEstablishment(estabUAI[estabInd], 
                    estabNames[estabInd], userId);
            System.out.println(userId + " ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End Administrative employees (" + ellapsed + "s) ---");



        System.out.println("\n\n\n--- TOS employees: " + nbTos + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbTos; i++) {
            final int estabInd = userIndex % nbEstab;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.addTOSToEstablishment(estabUAI[estabInd], 
                    estabNames[estabInd], userId);
            System.out.println(userId + " ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End TOS employees (" + ellapsed + "s) ---");


        // --------- Suppression

        userIndex = 0;
        top1 = System.currentTimeMillis();
        System.out.println("--- Remove Students: " + nbStudents + " ---");
        for (int i = 0; i < nbStudents; i++) {
            final int estabInd = userIndex % nbEstab;
            final int levelInd = userIndex % nbLevels;
            final int classInd = userIndex % nbClasses;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.removeStudentFromClass(estabUAI[estabInd], 
                    estabNames[estabInd], levels[levelInd], 
                    classesNames[classInd], userId);
            System.out.println(userId + " ==> " + result);
        }

        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End students (" + ellapsed + "s) ---");


        


        System.out.println("\n\n\n--- Removes teachers : " + nbTeachers + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbTeachers; i++) {
            rand.setSeed(userIndex);
            final int estabInd = userIndex % nbEstab;
            final int levelInd = userIndex % nbLevels;
            final int classInd = userIndex % nbClasses;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.removeTeacherFromClass(estabUAI[estabInd], 
                    estabNames[estabInd], levels[levelInd], 
                    classesNames[classInd], userId);
            System.out.println(userId + " to class  ==> " + result);
            
            // Builds a random list of dispciplines for the teacher
            List<String> discipl = new ArrayList<String>();
            int nbDispl = rand.nextInt(nbMaxDisplPerTeacher - 1) + 1;
            for (int j = 0; j < nbDispl; j++) {
                String d = disciplinesNames[rand.nextInt(nbDisciplines)];
                if (!discipl.contains(d)) {
                    discipl.add(d);
                }
            }
            
            result = sgs.removeTeacherFromDisciplines(estabUAI[estabInd], 
                    estabNames[estabInd], discipl, userId);
            System.out.println(userId + " to dsciplines  ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End teachers (" + ellapsed + "s) ---");
        
     
        
        System.out.println("\n\n\n--- Removes Parents: " + nbParents + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbParents; i++) {
            final int estabInd = userIndex % nbEstab;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.removeParentFromEstablishment(estabUAI[estabInd], 
                    estabNames[estabInd], userId);
            System.out.println(userId + " ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End parents (" + ellapsed + "s) ---");

        System.out.println("\n\n\n--- Removes Administrative employees: " + nbAdministrative + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbAdministrative; i++) {
            final int estabInd = rand.nextInt(nbEstab);
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.removeAdministrativeFromEstablishment(estabUAI[estabInd], 
                    estabNames[estabInd], userId);
            System.out.println(userId + " ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End Administrative employees (" + ellapsed + "s) ---");



        System.out.println("\n\n\n--- Removes TOS employees: " + nbTos + " ---");
        top1 = System.currentTimeMillis();
        for (int i = 0; i < nbTos; i++) {
            final int estabInd = userIndex % nbEstab;
            final String userId = usersPrefix + userIndex++;
            GrouperOperationResultDTO result = sgs.removeTOSFromEstablishment(estabUAI[estabInd], 
                    estabNames[estabInd], userId);
            System.out.println(userId + " ==> " + result);
        }
        ellapsed = (System.currentTimeMillis() - top1) / thousand;
        System.out.println("--- End TOS employees (" + ellapsed + "s) ---");


    }

}

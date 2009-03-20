/**
 * 
 */
package org.esco.ws4Internet2Grouper.services.remote;


import edu.internet2.middleware.grouper.GrouperSession;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.cache.SGSCache;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrStem;
import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;
import org.esco.ws4Internet2Grouper.domain.beans.PersonType;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;
import org.esco.ws4Internet2Grouper.parsing.SGSParsingUtil;
import org.esco.ws4Internet2Grouper.util.GrouperSessionUtil;
import org.esco.ws4Internet2Grouper.util.GrouperUtil;
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

    /** The Grouper util class. */
    private GrouperUtil grouperUtil;

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
            final GroupOrStem result = grouperUtil.retrieveOrCreate(session, def);

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
    protected GrouperOperationResultDTO handlesEmptyGroupsOrFoldersDefinitionTemplates(final GrouperSession session, 
            final String...attributes) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(SEP);
            LOGGER.debug("Handles empty groups or folders templates to create.");
            LOGGER.debug(SEP);
        }
        final Iterator<GroupOrFolderDefinition> it = definitionsManager.getGroupsOrFoldersTemplatesToCreate(attributes);
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

                final GroupOrStem result = grouperUtil.retrieveOrCreate(session, def, attributes);
                if (result == null) {
                    final StringBuilder msg = 
                        new StringBuilder("Error while creating empty group or folder template for the definition: ");
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
     * Removes the memebrships for a given user and a set of attributes.
     * @param type The type of user (student, teacher, etc.)
     * @param userId The id of the user.
     * @param attributes The user attributes.
     * @return The grouper operation result.
     */
    protected GrouperOperationResultDTO removeFromGroups(final PersonType type, 
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
            definitionsManager.getMembershipsForTemplates(PersonType.ALL, attributes);

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
     * Updates the memebrships for a given user and a set of attributes.
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
        GrouperOperationResultDTO result = grouperUtil.removeFromAllGroups(session, userId);

        if (result.isError()) {
            LOGGER.error("Error while removing from previous groups for user: " + userId);
            LOGGER.error(result.getException(), result.getException());
        } else {

            result = addToGroups(type, userId, session, attributes);
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
     * Handles the memebrships for a given user.
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
     * Handles the memebrships for a given user.
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

        // Handles specific memeberships.
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
            definitionsManager.getMemberships(PersonType.ALL, attributes);

        while (allMemberships.hasNext()) {
            final GrouperOperationResultDTO result = grouperUtil.addMember(session, 
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

    /**
     * Updates the establishment groups for an administrative employee.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param userId The id of the employee.
     * @param function The function of the employee.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     * @see org.esco.ws4Internet2Grouper.services.remote.ISarapisGroupService
     * #updateAdministrativeToEstablishment(String, String, String, String)
     */
    public GrouperOperationResultDTO updateAdministrativeToEstablishment(final String establishmentUAI, 
            final String establishmentName, 
            final String userId,
            final String function) {
        return updateGroups(PersonType.ADMINISTRATIVE, 
                userId, 
                establishmentUAI, 
                establishmentName,
                "",
                "",
                "",
                "",
                function);
    }
    
    /**
     * Adds a person to groups.
     * @param personDescription The descriptionof the person.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    public GrouperOperationResultDTO addToGroups(final IPersonDescription personDescription) {
        GrouperOperationResultDTO result = GrouperOperationResultDTO.RESULT_OK;
        if (personDescription.getDisciplines() != null) {
            for (String discipline : personDescription.getDisciplines()) {
                result =  addToGroups(personDescription.getType(), 
                        personDescription.getId(), 
                        personDescription.getEstablishmentUAI(), 
                        personDescription.getEstablishmentName(), 
                        personDescription.getLevel(),
                        personDescription.getClassName(),
                        personDescription.getClassDescription(),
                        discipline,
                        personDescription.getFunction());
                if (result.isError()) {
                    return result;
                }
            }
        } else {
            result = addToGroups(personDescription.getType(), 
                    personDescription.getId(), 
                    personDescription.getEstablishmentUAI(), 
                    personDescription.getEstablishmentName(), 
                    personDescription.getLevel(),
                    personDescription.getClassName(),
                    personDescription.getClassDescription(),
                    "",
                    personDescription.getFunction());
        }
        return result;
    }

    /**
     * Updates the memberships of a peron.
     * @param personDescription The descriptionof the person.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    public GrouperOperationResultDTO updateMemberships(final IPersonDescription personDescription) {
        GrouperOperationResultDTO result = GrouperOperationResultDTO.RESULT_OK;
        if (personDescription.getDisciplines() != null) {
            for (String discipline : personDescription.getDisciplines()) {
                result =  updateGroups(personDescription.getType(), 
                        personDescription.getId(), 
                        personDescription.getEstablishmentUAI(), 
                        personDescription.getEstablishmentName(), 
                        personDescription.getLevel(),
                        personDescription.getClassName(),
                        personDescription.getClassDescription(),
                        discipline,
                        personDescription.getFunction());
                if (result.isError()) {
                    return result;
                }
            }
        } else {
            result = updateGroups(personDescription.getType(), 
                    personDescription.getId(), 
                    personDescription.getEstablishmentUAI(), 
                    personDescription.getEstablishmentName(), 
                    personDescription.getLevel(),
                    personDescription.getClassName(),
                    personDescription.getClassDescription(),
                    "",
                    personDescription.getFunction());
        }
        return result;
    }

    /**
     * Removes a person from the groups managed by this service.
     * @param userId The id of the person to remove from the groups.
     * @return The result of the Grouper operation.
     */
    public GrouperOperationResultDTO removeFromGroups(final String userId) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(SEP);
            LOGGER.debug("Starting to remove from groups ");
            LOGGER.debug("for the user: " + userId);
            LOGGER.debug(SEP);
        }
        final GrouperSession session = grouperSessionUtil.createSession();
        GrouperOperationResultDTO result = grouperUtil.removeFromAllGroups(session, userId);

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


//    public static void main(final String args[]) {

//      import java.util.ArrayList;
//      import java.util.Random;
//      import org.springframework.context.ApplicationContext;
//      import org.springframework.context.support.FileSystemXmlApplicationContext;

//        final ThreadLocal<org.springframework.context.ApplicationContext> springCtx = 
//            new ThreadLocal<org.springframework.context.ApplicationContext>();
//        springCtx.set(new org.springframework.context.support.FileSystemXmlApplicationContext(
//            "classpath:properties/applicationContext.xml"));
//        final ISarapisGroupService sgs = (ISarapisGroupService) springCtx.get().getBean("SarapisGroupService");
//      final String usersPrefix = "STRESS_TEST__Person_";
//      final int nbUsers = 3000;
//      final int thousand = 1000;


//      final int nbStudents = 00;
//      final int nbTeachers = 3;
//      final int nbParents = 00;
//      final int nbAdministrative = 00;
//      final int nbTos = 00;
//      final int nbEstab = 1;
//      final int nbLevels = 3;
//      final int nbClasses = 3;
//      final int nbDisciplines = 10;
//      final int nbMaxDisplPerTeacher = 5;
//      final String[] estabNames = new String[nbEstab];
//      final String[] estabUAI = new String[nbEstab];
//      final String[] levels = new String[nbLevels];
//      final String[] classesNames = new String[nbClasses];
//      final String[] classesDesc = new String[nbClasses];
//      final String[] disciplinesNames = new String[nbDisciplines];

//      for (int  i = 0; i < nbEstab; i++) {
//      estabNames[i] = "Estab_name_" + i;
//      estabUAI[i] = "Estab_UAI_" + i;
//      }

//      for (int  i = 0; i < nbLevels; i++) {
//      levels[i] = "Level_" + i;
//      }

//      for (int  i = 0; i < nbClasses; i++) {
//      classesNames[i] = "Class_name_" + i;
//      classesDesc[i] = "Class_desc_" + i;
//      }

//      for (int  i = 0; i < nbDisciplines; i++) {
//      disciplinesNames[i] = "discipl_name_" + i;
//      }

//      final Random rand = new Random();
//      rand.setSeed(1L);

//      int userIndex = 0;
//      long top1 = System.currentTimeMillis();
//      System.out.println("--- Students: " + nbStudents + " ---");
//      for (int i = 0; i < nbStudents; i++) {
//      final int estabInd = userIndex % nbEstab;
//      final int levelInd = userIndex % nbLevels;
//      final int classInd = userIndex % nbClasses;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.addStudentToClass(estabUAI[estabInd], 
//      estabNames[estabInd], levels[levelInd], 
//      classesNames[classInd], classesDesc[classInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }

//      long ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End students (" + ellapsed + "s) ---");



//      System.out.println("\n\n\n--- Teachers : " + nbTeachers + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbTeachers; i++) {
//      rand.setSeed(userIndex);
//      final int estabInd = userIndex % nbEstab;
//      final int levelInd = userIndex % nbLevels;
//      final int classInd = userIndex % nbClasses;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.addTeacherToClass(estabUAI[estabInd], 
//      estabNames[estabInd], levels[levelInd], 
//      classesNames[classInd], classesDesc[classInd], userId);
//      System.out.println(userId + " to class  ==> " + result);

//      // Builds a random list of dispciplines for the teacher
//      List<String> discipl = new ArrayList<String>();
//      int nbDispl = rand.nextInt(nbMaxDisplPerTeacher - 1) + 1;
//      for (int j = 0; j < nbDispl; j++) {
//      String d = disciplinesNames[rand.nextInt(nbDisciplines)];
//      if (!discipl.contains(d)) {
//      discipl.add(d);
//      }
//      }

//      result = sgs.addTeacherToDisciplines(estabUAI[estabInd], 
//      estabNames[estabInd], discipl, userId);
//      System.out.println(userId + " to dsciplines  ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End teachers (" + ellapsed + "s) ---");



//      System.out.println("\n\n\n--- Parents: " + nbParents + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbParents; i++) {
//      final int estabInd = userIndex % nbEstab;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.addParentToEstablishment(estabUAI[estabInd], 
//      estabNames[estabInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End parents (" + ellapsed + "s) ---");
//        final IPersonDescription persDescr = new PersonDescriptionImpl(PersonType.ADMINISTRATIVE,
//                "TESTETAB_UAI", "TEST_ETAB_NOM", "Apd00000");
//        GrouperOperationResultDTO result = sgs.addToGroups(persDescr);
//        
//        result = sgs.updateAdministrativeToEstablishment("TESTETAB_UAI", 
//                "TEST_ETAB_NOM", "Apd00000", "Une Fonction");
        

//      System.out.println("\n\n\n--- Administrative employees: " + nbAdministrative + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbAdministrative; i++) {
//      final int estabInd = rand.nextInt(nbEstab);
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.addAdministrativeToEstablishment(estabUAI[estabInd], 
//      estabNames[estabInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End Administrative employees (" + ellapsed + "s) ---");



//      System.out.println("\n\n\n--- TOS employees: " + nbTos + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbTos; i++) {
//      final int estabInd = userIndex % nbEstab;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.addTOSToEstablishment(estabUAI[estabInd], 
//      estabNames[estabInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End TOS employees (" + ellapsed + "s) ---");


//      // --------- Suppression

//      userIndex = 0;
//      top1 = System.currentTimeMillis();
//      System.out.println("--- Remove Students: " + nbStudents + " ---");
//      for (int i = 0; i < nbStudents; i++) {
//      final int estabInd = userIndex % nbEstab;
//      final int levelInd = userIndex % nbLevels;
//      final int classInd = userIndex % nbClasses;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.removeStudentFromClass(estabUAI[estabInd], 
//      estabNames[estabInd], levels[levelInd], 
//      classesNames[classInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }

//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End students (" + ellapsed + "s) ---");





//      System.out.println("\n\n\n--- Removes teachers : " + nbTeachers + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbTeachers; i++) {
//      rand.setSeed(userIndex);
//      final int estabInd = userIndex % nbEstab;
//      final int levelInd = userIndex % nbLevels;
//      final int classInd = userIndex % nbClasses;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.removeTeacherFromClass(estabUAI[estabInd], 
//      estabNames[estabInd], levels[levelInd], 
//      classesNames[classInd], userId);
//      System.out.println(userId + " to class  ==> " + result);

//      // Builds a random list of dispciplines for the teacher
//      List<String> discipl = new ArrayList<String>();
//      int nbDispl = rand.nextInt(nbMaxDisplPerTeacher - 1) + 1;
//      for (int j = 0; j < nbDispl; j++) {
//      String d = disciplinesNames[rand.nextInt(nbDisciplines)];
//      if (!discipl.contains(d)) {
//      discipl.add(d);
//      }
//      }

//      result = sgs.removeTeacherFromDisciplines(estabUAI[estabInd], 
//      estabNames[estabInd], discipl, userId);
//      System.out.println(userId + " to dsciplines  ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End teachers (" + ellapsed + "s) ---");



//      System.out.println("\n\n\n--- Removes Parents: " + nbParents + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbParents; i++) {
//      final int estabInd = userIndex % nbEstab;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.removeParentFromEstablishment(estabUAI[estabInd], 
//      estabNames[estabInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End parents (" + ellapsed + "s) ---");

//      System.out.println("\n\n\n--- Removes Administrative employees: " + nbAdministrative + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbAdministrative; i++) {
//      final int estabInd = rand.nextInt(nbEstab);
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.removeAdministrativeFromEstablishment(estabUAI[estabInd], 
//      estabNames[estabInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End Administrative employees (" + ellapsed + "s) ---");



//      System.out.println("\n\n\n--- Removes TOS employees: " + nbTos + " ---");
//      top1 = System.currentTimeMillis();
//      for (int i = 0; i < nbTos; i++) {
//      final int estabInd = userIndex % nbEstab;
//      final String userId = usersPrefix + userIndex++;
//      GrouperOperationResultDTO result = sgs.removeTOSFromEstablishment(estabUAI[estabInd], 
//      estabNames[estabInd], userId);
//      System.out.println(userId + " ==> " + result);
//      }
//      ellapsed = (System.currentTimeMillis() - top1) / thousand;
//      System.out.println("--- End TOS employees (" + ellapsed + "s) ---");


//    }

}

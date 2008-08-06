/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.util.GroupOrFolderSummary;
import org.esco.ws4Internet2Grouper.util.GrouperUtil;
import org.esco.ws4Internet2Grouper.util.INamingRules;
import org.esco.ws4Internet2Grouper.util.SGSParameters;
import org.springframework.util.Assert;


/**
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class EstablishmentHandlerImpl implements IEstablishmentHandler {

    /** Serial version UID. */
    private static final long serialVersionUID = -4532532840312248794L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(EstablishmentHandlerImpl.class);

    /** User parameters. */
    private SGSParameters parameters;

    /** Util class to manage Grouper groups. */
    private GrouperUtil grouperUtil;

    /** The namming rules to use. */
    private INamingRules namingRule;

    /**
     * Builds an instance of EstablishmentHandlerImpl.
     */
    public EstablishmentHandlerImpl() {
        super();
        LOGGER.info(" Creation of Establishment handler: " + getClass());
    }

    /**
     * Checks the state of the bean after the injection process.
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.parameters, 
                "property parameters of class " + this.getClass().getName() 
                + " can not be null.");

        Assert.notNull(this.grouperUtil, 
                "property parameters of class " + this.getClass().getName() 
                + " can not be null.");

        Assert.notNull(this.namingRule, 
                "property parameters of class " + this.getClass().getName() 
                + " can not be null.");
    }

    /**
     * Retrieves or create the establishment folder.
     * @param session The Grouper session.
     * @param establishmentFolderInfos The establishemnt folder.
     * @param localAdminGroupInfos The local admin group.
     * @return The establishment folder.
     */
    public Stem retrieveOrCreateEstablishmentFolder(final GrouperSession session,
            final GroupOrFolderSummary establishmentFolderInfos, 
            final GroupOrFolderSummary localAdminGroupInfos) {
//        return grouperUtil.retrieveOrCreateLocallyAdministratedFolder(session, 
//                grouperUtil.getEstablishmentsFolder(), 
//                establishmentFolderInfos,
//                localAdminGroupInfos);
        return null;
    }

    /**
     * Retrieves or create a teachers group for a given establishment.
     * @param session The Grouper session.
     * @param establishmentUAI The UAI of the establishement.
     * @param establishmentName The name of the establishment.
     * @return The teachers group.
     * @see org.esco.ws4Internet2Grouper.domain.IEstablishmentHandler#
     * retrieveOrCreateTeachersGroup(edu.internet2.middleware.grouper.GrouperSession, String, String)
     */
    public Group retrieveOrCreateTeachersGroup(final GrouperSession session, 
            final String establishmentUAI, 
            final String establishmentName) {

        final GroupOrFolderSummary establishmentFolderInfos = 
            namingRule.nameEstablishmentFolder(establishmentUAI, establishmentName);
        
        final GroupOrFolderSummary localAdminGroupInfos = 
            namingRule.nameLocalAdminGroup(establishmentUAI, establishmentName);
        
        final GroupOrFolderSummary establishmentTeacherGroupInfos = 
            namingRule.nameTeachersGroup(establishmentUAI, establishmentName);

        final Stem establishmentFolder = 
            retrieveOrCreateEstablishmentFolder(session, establishmentFolderInfos, localAdminGroupInfos);

//        return grouperUtil.retrieveOrCreateLocallyAdministratedGroup(session, 
//                establishmentFolder, 
//                establishmentTeacherGroupInfos, 
//                localAdminGroupInfos);
        return null;
    }

    /**
     * Retrieves or creates a students group for a given establishment.
     * @param session The Grouper session
     * @param establishementUAI The establishment UAI
     * @param establishementName The establishment name.
     * @return The students group.
     * @see org.esco.ws4Internet2Grouper.domain.IEstablishmentHandler#
     * retrieveOrCreateStudentsGroup(edu.internet2.middleware.grouper.GrouperSession, String, String)
     */
    public Group retrieveOrCreateStudentsGroup(final GrouperSession session,
            final String establishementUAI, 
            final String establishementName) {
        
        return null;
    }

    /**
     * Getter for namingRule.
     * @return namingRule.
     */
    public INamingRules getNamingRule() {
        return namingRule;
    }

    /**
     * Setter for namingRule.
     * @param namingRule the new value for namingRule.
     */
    public void setNamingRule(final INamingRules namingRule) {
        this.namingRule = namingRule;
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
     * 
     * @param args
     */
//  public static void main(final String args[]) {
//  final ThreadLocal<ApplicationContext> springCtx = new ThreadLocal<ApplicationContext>();
//  springCtx.set(new FileSystemXmlApplicationContext("classpath:applicationContext.xml"));
//  IEstablishmentHandler estabService = (IEstablishmentHandler) springCtx.get().getBean("EstablishmentHandler");
//  estabService.createGroupsAndFoldersIfNeeded("testEstabUAI", "testEstablishementName");
//  }

}

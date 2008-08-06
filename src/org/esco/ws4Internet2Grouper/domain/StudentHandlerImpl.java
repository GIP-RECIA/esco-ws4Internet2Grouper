/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.domain.beans.GrouperOperationResultDTO;
import org.esco.ws4Internet2Grouper.util.GroupOrFolderSummary;
import org.esco.ws4Internet2Grouper.util.GrouperSessionUtil;
import org.esco.ws4Internet2Grouper.util.GrouperUtil;
import org.esco.ws4Internet2Grouper.util.INamingRules;
import org.esco.ws4Internet2Grouper.util.SGSParameters;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;

/**
 * @author GIP RECIA - A. Deman
 * 29 juil. 08
 *
 */
public class StudentHandlerImpl implements IStudentHandler, InitializingBean {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 2881073655689901112L;
    
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(StudentHandlerImpl.class); 

    /** The naming rules for the groups and folders. */
    private INamingRules namingRules;
    
    /** Grouper util class. */
    private GrouperUtil grouperUtil;
    
    /** Grouper session util class. */
    private GrouperSessionUtil sessionUtil;
    
    /** User parameters. */
    private SGSParameters parameters;
    
    /** The prefix for the path of establishment folders. */
    private String establishmentsPathPrefix;
    
    /**
     * Builds an instance of StudentHandlerImpl.
     */
    public StudentHandlerImpl() {
        LOGGER.info("Creation of the Sudent handler: " + getClass() + ".");
    }

    /**
     * Checks the injected bean.
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
        
        sessionUtil = new GrouperSessionUtil(parameters.getUser());
        establishmentsPathPrefix = parameters.getRoot() + Stem.DELIM + parameters.getEstablishmentsFolderInfos().getExtension();
    }

    
    /**
     * Adds a student to a class.
     * @param establishmentUAI The estazblishment UAI.
     * @param establishmentName The establishment name.
     * @param level The levec of the class.
     * @param className The name of the class.
     * @param classDescription The description of the class.
     * @param userId The id of the strudent.
     * @return The result of the operation.
     * @see org.esco.ws4Internet2Grouper.domain.IStudentHandler
     * #addStudentToClass(String, String, String, String, String, String)
     */
    public GrouperOperationResultDTO addStudentToClass(final String establishmentUAI,
            final String establishmentName, 
            final String level, 
            final String className,
            final String classDescription, 
            final String userId) {
//        final GrouperSession session = sessionUtil.createSession();
//        final GroupOrFolderSummary studentsGroupInfos = namingRules.nameStudentsGroup();
//        final GroupOrFolderSummary localAdminGroupInfos = namingRules.nameLocalAdminGroup(establishmentUAI, establishmentName);
//        
//        Group studentsGroup = grouperUtil.retrieveOrCreateLocallyAdministratedFolder(session
//                ,localAdminGroupSummary)Group(session, name);  
        return null;
    }
  

  
    /**
     * Getter for sessionUtil.
     * @return sessionUtil.
     */
    public GrouperSessionUtil getSessionUtil() {
        return sessionUtil;
    }

    /**
     * Setter for sessionUtil.
     * @param sessionUtil the new value for sessionUtil.
     */
    public void setSessionUtil(final GrouperSessionUtil sessionUtil) {
        this.sessionUtil = sessionUtil;
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

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
package org.esco.ws4Internet2Grouper.util;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.SessionException;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.exceptions.WS4GrouperException;
import org.springframework.beans.factory.InitializingBean;
/**
 * Util class used to handle the grouper sessions.
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class GrouperSessionUtil implements InitializingBean {
    
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GrouperSessionUtil.class);
    
    /** Subject id used to open the sessions. */
    private String subjectId;
    
    /**
     * Builds an instance of GrouperSessionUtil.
     */
    public GrouperSessionUtil() {
        super();
    }
    
    /**
     * Builds an instance of GrouperSessionUtil.
     * @param subjectId The subject id used to open sessions.
     */
    public GrouperSessionUtil(final String subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * Checks the spring data injection.
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        /* */
    }
    
    /**
     * Creates a Grouper session instance.
     * @return The session object.
     */
    public GrouperSession createSession() {

        try {
            final Subject subject = SubjectFinder.findById(subjectId);
            final GrouperSession session = GrouperSession.start(subject);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Starting a new session: " + session.getSessionId());
            }
            return session;

        } catch (SubjectNotFoundException e) {
            LOGGER.error(e, e);
            throw new WS4GrouperException(e);
        } catch (SubjectNotUniqueException e) {
            LOGGER.error(e, e);
            throw new WS4GrouperException(e);
        } catch (SessionException e) {
            LOGGER.error(e, e);
            throw new WS4GrouperException(e);
        }
    }

    /**
     * Closes a grouper session.
     * @param session The session to close.
     */
    public void stopSession(final GrouperSession session) {
        try {
            LOGGER.debug("Stopping the session : " + session.getSessionId());
            session.stop();
        } catch (SessionException e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Getter for subjectId.
     * @return subjectId.
     */
    public String getSubjectId() {
        return subjectId;
    }

    /**
     * Setter for subjectId.
     * @param subjectId the new value for subjectId.
     */
    public void setSubjectId(final String subjectId) {
        this.subjectId = subjectId;
    }
}

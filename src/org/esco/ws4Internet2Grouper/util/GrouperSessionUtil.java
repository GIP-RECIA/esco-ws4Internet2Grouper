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
/**
 * Util class used to handle the grouper sessions.
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class GrouperSessionUtil {
    
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GrouperSessionUtil.class);
    
    /** Subject id used to open the sessions. */
    private String subjectId;
    
    /**
     * Builds an instance of GrouperSessionUtil.
     * @param subjectId The subject id used to create the sessions.
     */
    public GrouperSessionUtil(final String subjectId) {
        this.subjectId = subjectId;
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
}

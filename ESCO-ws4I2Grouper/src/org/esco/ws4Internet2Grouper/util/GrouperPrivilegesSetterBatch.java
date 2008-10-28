/**
 * 
 */
package org.esco.ws4Internet2Grouper.util;

import java.util.Set;

import edu.internet2.middleware.grouper.GrantPrivilegeException;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.SchemaException;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.StemNotFoundException;
import edu.internet2.middleware.grouper.Stem.Scope;
import edu.internet2.middleware.subject.Subject;

/**
 * @author GIP RECIA - A. Deman
 * 27 oct. 08
 *
 */
public class GrouperPrivilegesSetterBatch {

    /** Root folder for the groups to change. */
    private static final String ROOT_FOLDER = "esco:Etablissements:LEONARD DE VINCI_0370001A";
//    private static final String ROOT_FOLDER = "esco:Etablissements:CLAUDE DE FRANCE_0410017W";
    
    /** Root folder for the groups to change. */
    private static final String PRINCIPAL = "esco:admin:local:admin_LEONARD DE VINCI_0370001A";
//    private static final String PRINCIPAL = "esco:admin:local:admin_CLAUDE DE FRANCE_0410017W";
    
    /**
     * @param args
     * @throws StemNotFoundException 
     * @throws GroupNotFoundException 
     * @throws SchemaException 
     * @throws InsufficientPrivilegeException 
     * @throws GrantPrivilegeException 
     */
    public static void main(final String[] args) 
        throws StemNotFoundException, 
            GroupNotFoundException, 
            GrantPrivilegeException, 
            InsufficientPrivilegeException, 
            SchemaException {
        
        GrouperSessionUtil grouperSession = new GrouperSessionUtil("GrouperSystem");
        GrouperSession session = grouperSession.createSession();
        final Group principal = GroupFinder.findByName(session, PRINCIPAL);
        final Subject subj = principal.toSubject();
        final Stem folder = StemFinder.findByName(session, ROOT_FOLDER);
        Set<Group> groups = folder.getChildGroups(Scope.SUB);
        
        for (Group group : groups) {
            if (!group.hasView(subj)) {
                group.grantPriv(subj, Constants.VIEW_PRIV);
            }
            if (!group.hasRead(subj)) {
                group.grantPriv(subj, Constants.READ_PRIV);
            }
        }
        grouperSession.stopSession(session);
    }

}

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



import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.Stem.Scope;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.subject.Subject;

import java.util.Set;
/**
 * @author GIP RECIA - A. Deman
 * 27 oct. 08
 *
 */
public class GrouperPrivilegesSetterBatch {

    /** Root folder for the groups to change. */
    private static final String ROOT_FOLDER = "esco:Etablissements:LEONARD DE VINCI_0370001A";
    
    /** Root folder for the groups to change. */
    private static final String PRINCIPAL = "esco:admin:local:admin_LEONARD DE VINCI_0370001A";
    
    /**
     * Builds an instance of GrouperPrivilegesSetterBatch.
     */
    private GrouperPrivilegesSetterBatch() {
        super();
    }
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(final String[] args) 
        throws Exception {
        
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

/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import edu.internet2.middleware.grouper.Stem;
import java.io.Serializable;


/**
 * Class for the definition of groups or folder.
 * A definition can be a template that can be instanciate on
 * attributes values.
 * For instance the extension could be %ETABLISSEMENT_UAI% wich can be used then to
 * instanciate the definition for each value of the attribute denoted by the key
 * %ETABLISSEMENT_UAI%.
 * The mapping between the key and its value is done by the method instanciateTemplate(..).
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 *
 */
public class GroupOrFolderDefinition implements Serializable, Cloneable {
    
   
    
    /** Type of the definition. */
    public static enum DefinitionType {

        /** The definition denotes a group. */
        GROUP, 

        /** The definition denotes a folder. */
        FOLDER
    }
    
    /** Serial version UID.*/
    private static final long serialVersionUID = 5441857690289508441L;

    /** Flag used to determine if the definition denotes a group or a folder. */
    private DefinitionType definitionType;
   
    /** The description of the group or folder. */
    private EvaluableString description;

    /** The extension of the group or folder. */
    private EvaluableString extension;

    /** The display extension of the group or folder.*/
    private EvaluableString displayExtension;

    /** The containing path of the folder or group. */
    private EvaluableString containingPath;
    
    /** The definition of the members of the group. */
    private MembersDefinition[] membersDefinitions;

    /** The groups that administrate this group or folder. */
    private GroupOrFolderDefinition[] administratingGroups;
    
    /** The path of the groups that contains this group (may be a template). */
    private EvaluableStringArray containingGroupsPaths;

    /** The hash code.*/
    private int hashcode;
    
    /**
     * Builds an empty instance of GroupOrFolderDefinition.
     */
    protected GroupOrFolderDefinition() {
        super();
    }
    
    /**
     * Builds an instance of GroupOrFolderDefinition.
     * @param definitionType The definition type, folder or group.
     * @param containingPath The containing path.
     * @param extension The extension of the group or folder.
     * @param displayExtension The display extension of the group or folder.
     * @param description The description of the group or folder.
     */
    public GroupOrFolderDefinition(final DefinitionType definitionType,
            final String containingPath,
            final String extension, 
            final String displayExtension,
            final String description) {
        this.definitionType = definitionType;
        this.containingPath = new EvaluableString(containingPath);
        this.extension = new EvaluableString(extension);
        this.displayExtension = new EvaluableString(displayExtension);
        this.description = new EvaluableString(description);
    }
    
   
    
    /**
     * Builds an instance of Group Definition.
     * @param containingPath The containing path.
     * @param extension The extension of the group or folder.
     * @param displayExtension The display extension of the group or folder.
     * @param description The description of the group or folder.
     * @param membersDefinitions The definitions for the members of the group (can be null if there
     * is no members definition).
     * @param containingGroupsPaths the paths of the groups this group belongs to (can also be null if there is 
     * no membership).
     * @param administratingGroups the groups with administrating privile on
     * the group(s) denoted by this definition. This parameter can be null if there is
     * no administrating group. 
     * @return the instance of group definition.
     */
    public static GroupOrFolderDefinition buildGroupDefinition(final String containingPath,
            final String extension, 
            final String displayExtension,
            final String description,
            final MembersDefinition[] membersDefinitions,
            final EvaluableStringArray containingGroupsPaths,
            final GroupOrFolderDefinition[] administratingGroups) {
        final GroupOrFolderDefinition groupDef = new GroupOrFolderDefinition(DefinitionType.GROUP,
                containingPath,
                extension,
                displayExtension,
                description);
        groupDef.administratingGroups = administratingGroups;
        groupDef.membersDefinitions = membersDefinitions;
        groupDef.containingGroupsPaths = containingGroupsPaths;
        return groupDef;
    }
    
    /**
     * Builds an instance of folder Definition.
     * @param containingPath The containing path.
     * @param extension The extension of the group or folder.
     * @param displayExtension The display extension of the group or folder.
     * @param description The description of the group or folder.
     * @param membersDefinitions The definitions for the members of the group (can be null if there
     * is no members definition).
     * @param administratingGroups the groups with administrating privile on
     * the group(s) denoted by this definition. This parameter can be null if there is
     * no administrating group. 
     * @return the instance of folder definition.
     */
    public static GroupOrFolderDefinition buildFolderDefinition(final String containingPath,
            final String extension, 
            final String displayExtension,
            final String description,
            final MembersDefinition[] membersDefinitions,
            final GroupOrFolderDefinition[] administratingGroups) {
        final GroupOrFolderDefinition folderDef = new GroupOrFolderDefinition(DefinitionType.GROUP,
                containingPath,
                extension,
                displayExtension,
                description);
        folderDef.administratingGroups = administratingGroups;
        folderDef.membersDefinitions = membersDefinitions;
        return folderDef;
    }
    
    /**
     * Evaluates a template by replacing the template elements by a value.
     * @param establishmentUAI The value to use as establishment UAI.
     * @param establishmentName  The value to use as establishment name.
     * @param level  The value to use as level.
     * @param className  The value to use as class name.
     * @param classDescription The value to use as class description.
     * @return The GroupOrFolderDefinition with the correct values.
     */
    public GroupOrFolderDefinition evaluateTemplate(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription) {
        
        
        final EvaluableString newExtension = extension.evaluate(establishmentUAI, 
                establishmentName, level, className, classDescription); 

        final EvaluableString newDisplayExtension = displayExtension.evaluate(establishmentUAI, establishmentName, 
                level, className, classDescription);
        
        final EvaluableString newDescription = description.evaluate(establishmentUAI, establishmentName, 
                level, className, classDescription);
        
        final EvaluableString newContainingPath = description.evaluate(establishmentUAI, establishmentName, 
                level, className, classDescription);
        
        // Evaluates the containing Groups paths
        EvaluableStringArray newContainingGroupsPaths = null;
        if (countContainingGroupsPaths() > 0) {
            newContainingGroupsPaths = containingGroupsPaths.evaluate(establishmentUAI,
                         establishmentName, level, className, classDescription);
        }
        
        // Evaluates all the definitions corresponding to the groups with administrative privileges.
        GroupOrFolderDefinition[] newAdministratingGroups = null;
        if (countAdministratingGroups() > 0) {
            newAdministratingGroups = new GroupOrFolderDefinition[countAdministratingGroups()];
            for (int i = 0; i < countAdministratingGroups(); i++) {
                if (administratingGroups[i].isTemplate()) {
                    newAdministratingGroups[i] = administratingGroups[i].evaluateTemplate(establishmentUAI, 
                            establishmentName, 
                            level, 
                            className, 
                            classDescription);
                } else {
                    newAdministratingGroups[i] = administratingGroups[i];
                }
            }
        }
        
        GroupOrFolderDefinition gofd = new GroupOrFolderDefinition();
        gofd.definitionType = definitionType;
        gofd.containingPath = newContainingPath;
        gofd.extension = newExtension;
        gofd.displayExtension = newDisplayExtension;
        gofd.description = newDescription;
        gofd.membersDefinitions = membersDefinitions;
        gofd.containingGroupsPaths = newContainingGroupsPaths;
        gofd.administratingGroups = newAdministratingGroups;
        
        return gofd;
 
       
    }
    
    /**
     * Gives the path associated to this definition.
     * If this definition is a template, then the returned path is also a template.
     * @return The path.
     */
    public String getPath() {
        return containingPath + Stem.DELIM + extension;
    }
    
    /**
     * Comutes the has code for this instance.
     * @return The hash value for this instance.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashcode == 0) {
            hashcode = getPath().hashCode();
        }
        return hashcode;
    }
    
    /**
     * Test if the instance is equal to an object.
     * @param o The object to test.
     * @return True if the object is equal to this one.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof GroupOrFolderDefinition)) {
            return false;
        }
        
        final GroupOrFolderDefinition gofd = (GroupOrFolderDefinition) o;
        if (!gofd.extension.equals(extension)) {
            return false;
        }
        return gofd.containingPath.equals(containingPath);
    }
    
    /**
     * Adds Administrating groups.
     * @param administratingGroups  The administrating groups.
     */
    public void setAdministratingGroups(final GroupOrFolderDefinition[] administratingGroups) {
            this.administratingGroups = administratingGroups;
    }
        
    /**
     * Tests if the definition denotes a group.
     * @return True if the definition denotes a group.
     */
    boolean isGroup() {
        return definitionType == DefinitionType.GROUP;
    }

    /**
     * Tests if the definition denotes a folder.
     * @return True if the definition denotes a folder.
     */
    boolean isFolder() {
        return definitionType == DefinitionType.FOLDER;
    }

    /**
     * Tests if the method denotes a template definition.
     * @return True if the definition denotes a template.
     */
    boolean isTemplate() {
        if (!extension.isEvaluated()) {
            return true;
        }
        if (!displayExtension.isEvaluated()) {
            return true;
        }
        if (!description.isEvaluated()) {
            return true;
        }
        if (!containingPath.isEvaluated()) {
            return true;
        }
        return !containingGroupsPaths.isEvaluated();
    }

    /**
     * Getter for description.
     * @return description.
     */
    public String getDescription() {
        return description.getString();
    }

    /**
     * Getter for extension.
     * @return extension.
     */
    public String getExtension() {
        return extension.getString();
    }

    /**
     * Getter for displayExtension.
     * @return displayExtension.
     */
    public String getDisplayExtension() {
        return displayExtension.getString();
    }

    /**
     * Getter for containingPath.
     * @return containingPath.
     */
    public String getContainingPath() {
        return containingPath.getString();
    }
           
    /**
     * Gives the number of groups who have administration privileges
     * on the group or folder denoted by this definition.
     * @return The number of groups that have administration privileges.
     */
    public int countAdministratingGroups() {
        if (administratingGroups == null) {
            return 0;
        }
        return administratingGroups.length;
    }
    
    /**
     * Gives the administrating group at a given position.
     * @param index The position of the group in the list.
     * @return the administrating group at the specified position
     */
    public GroupOrFolderDefinition getAdministrativeGroup(final int index) {
        return administratingGroups[index];
    }
    
    /**
     * Gives the number of members definitions for the group or folder definiton.
     * @return The number of members definition for this group or folder
     * definition.
     */
    public int countMembersDefinitions() {
        if (membersDefinitions == null) {
            return 0;
        }
        return membersDefinitions.length;
    }
    
    /**
     * Gives a specified members definition.
     * @param index The position of the target members definition.
     * @return The members definiton at the specified position.
     */
    public MembersDefinition getMembersDefiniton(final int index) {
        return membersDefinitions[index];
    }
    
    /**
     * Gives the number of path for the group or folder definiton.
     * @return The number of membership paths for this group or folder
     * definition.
     */
    public int countContainingGroupsPaths() {
        if (containingGroupsPaths == null) {
            return 0;
        }
        return containingGroupsPaths.countEvaluableStrings();
    }
    
    /**
     * Gives a specified containing group path.
     * @param index The position of the target membership path.
     * @return The membership path at the specified position.
     */
    public String getContainingGroupPath(final int index) {
        return containingGroupsPaths.getEvaluableString(index).getString();
    }
    
    /**
     * Gives the string representaton of this group or folder definition.
     * @return The string that represents this definition.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        if (isGroup()) {
            sb.append("(Group, ");
        } else {
            sb.append("(Folder,");
        }
        
        sb.append(containingPath);
        sb.append(", ");
        sb.append(extension);
        sb.append(", ");
        sb.append(displayExtension);
        sb.append(", ");
        sb.append(description);
        
        if (countMembersDefinitions() > 0) {
            sb.append(", Members definitions: ");
            for (int i = 0; i < countMembersDefinitions(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(getMembersDefiniton(i));
            }
            
        }
        
        if (countContainingGroupsPaths() > 0) {
            sb.append(", Memberships ");
            sb.append(containingGroupsPaths);
        }

        if (countAdministratingGroups() > 0) {
            sb.append(", Admin. grp: ");
            for (int i = 0; i < countAdministratingGroups(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(getAdministrativeGroup(i));
            }
            
        }
        
        sb.append(")");
        return sb.toString();
    }
    
   
    
//    public static final void main(final String[] argsv) {
//       GroupOrFolderDefinition gof1 = new GroupOrFolderDefinition(GroupOrFolderDefinition.DefinitionType.GROUP,
//               "eraezra/araeraae", "erareara%NOM_ETAB%_%UAI_ETAB% erea%NOM_CLASSE%zrae",
//               "%NOM_ETAB%_%UAI_ETAB%", "description %DESC_CLASSE%"); 
//        System.out.println("1=>" + gof1);
//        System.out.println("2=>" 
//                + gof1.evaluateTemplate("establishmentUAI", 
//                        "establishmentName", 
//                        "level", 
//                        "className", 
//                        "classDescription"));
//        
//    }
    

}

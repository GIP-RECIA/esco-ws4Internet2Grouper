/**
 * 
 */
package org.esco.ws4Internet2Grouper.domain.beans;

import edu.internet2.middleware.grouper.Stem;

import java.io.Serializable;

/**
 * Class for the definition of groups or folder.
 * A definition can be a template that can be instanciate on
 * attribute value.
 * For instance the extension could be %ETABLISSEMENT_UAI% wich can be used then to
 * instanciate the definition for each value of the attribute denoted by the key
 * %ETABLISSEMENT_UAI%.
 * The mapping between the key and its value is done by the method instanciateTemplate(..).
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 *
 */
public class GroupOrFolderDef implements Serializable, Cloneable {
    
    /** Template element for the etablishment name. */
    public static final TemplateElement TEMPL_ETAB_NAME = new TemplateElement("NOM_ETAB");
    
    /** Template element for the etablishment UAI. */
    public static final TemplateElement TEMPL_ETAB_UAI = new TemplateElement("UAI_ETAB");

    /** Template element for the level. */
    public static final TemplateElement TEMPL_LEVEL = new TemplateElement("NIVEAU");

    /** Template element for the class. */
    public static final TemplateElement TEMPL_CLASS_NAME = new TemplateElement("NOM_CLASSE");

    /** Template element for the class description. */
    public static final TemplateElement TEMPL_CLASS_DESC = new TemplateElement("DESC_CLASSE");
    
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
    private String description;

    /** The extension of the group or folder. */
    private String extension;

    /** The display extension of the group or folder.*/
    private String displayExtension;

    /** The containing path of the folder or group. */
    private String containingPath;

    /** The groups that administrate this group or folder. */
    private GroupOrFolderDef[] administratingGroups;

    /** The mask that denotes the template elements contained in extension. */
    private int extensionMask;
    
    /** The mask that denotes the template elements contained in displayExtension. */
    private int displayExtensionMask;
    
    /** The mask that denotes the template elements contained in description. */
    private int descriptionMask;
    
    /** The hash code.*/
    private int hashcode;

    /**
     * Builds an instance of GroupOrFolderDef.
     * @param definitionType The definition type, folder or group.
     * @param containingPath The containing path.
     * @param extension The extension of the group or folder.
     * @param displayExtension The display extension of the group or folder.
     * @param description The description of the group or folder.
     * @param administratingGroups the groups with administrating privile on
     * the group(s) denoted by this definition.
     */
    public GroupOrFolderDef(final DefinitionType definitionType,
            final String containingPath,
            final String extension, 
            final String displayExtension,
            final String description,
            final GroupOrFolderDef[] administratingGroups) {
        this.definitionType = definitionType;
        this.containingPath = containingPath;
        this.extension = extension;
        this.displayExtension = displayExtension;
        this.description = description;
        this.administratingGroups = administratingGroups;
        
        // Computes the masks used to determine which templates 
        // elements are contained in this members.
        extensionMask = computeMask(this.extension);
        displayExtensionMask = computeMask(this.displayExtension);
        descriptionMask = computeMask(this.description);
    }
    
    /**
     * Computes the mask for all the template elements.
     * @param src The string that may contain the template elements.
     * @return The mask corresponding to all the template elements 
     * contained in the string.
     */
    int computeMask(final String src) {
        int currentMask = 0;
        currentMask = TEMPL_ETAB_UAI.markIfNecessary(src, currentMask);
        currentMask = TEMPL_ETAB_NAME.markIfNecessary(src, currentMask);
        currentMask = TEMPL_LEVEL.markIfNecessary(src, currentMask);
        currentMask = TEMPL_CLASS_NAME.markIfNecessary(src, currentMask);
        return TEMPL_CLASS_DESC.markIfNecessary(src, currentMask);
    }
    
    /**
     * Evaluates a template by replacing the template elements by a value.
     * @param establishmentUAI The value to use as establishment UAI.
     * @param establishmentName  The value to use as establishment name.
     * @param level  The value to use as level.
     * @param className  The value to use as class name.
     * @param classDescription The value to use as class description.
     * @return The GroupOrFolderDef with the correct values.
     */
    public GroupOrFolderDef evaluateTemplate(final String establishmentUAI,
            final String establishmentName,
            final String level,
            final String className,
            final String classDescription) {
        
        String newExtension = extension;
        String newDisplayExtension = displayExtension;
        String newDescription = description;
        
        // Establishement UAI
        if (TEMPL_ETAB_UAI.isContainedBy(newExtension)) {
            newExtension = TEMPL_ETAB_UAI.replace(newExtension, establishmentUAI);
        }
        if (TEMPL_ETAB_UAI.isContainedBy(newDisplayExtension)) {
            newDisplayExtension = TEMPL_ETAB_UAI.replace(newDisplayExtension, establishmentUAI);
        }
        if (TEMPL_ETAB_UAI.isContainedBy(newDescription)) {
            newDescription = TEMPL_ETAB_UAI.replace(newDescription, establishmentUAI);
        }
        
        // Establishement Name
        if (TEMPL_ETAB_NAME.isContainedBy(newExtension)) {
            newExtension = TEMPL_ETAB_NAME.replace(newExtension, establishmentName);
        }
        if (TEMPL_ETAB_NAME.isContainedBy(newDisplayExtension)) {
            newDisplayExtension = TEMPL_ETAB_NAME.replace(newDisplayExtension, establishmentName);
        }
        if (TEMPL_ETAB_NAME.isContainedBy(newDescription)) {
            newDescription = TEMPL_ETAB_NAME.replace(newDescription, establishmentName);
        }
        
        //Level
        if (TEMPL_LEVEL.isContainedBy(newExtension)) {
            newExtension = TEMPL_LEVEL.replace(newExtension, level);
        }
        if (TEMPL_LEVEL.isContainedBy(newDisplayExtension)) {
            newDisplayExtension = TEMPL_LEVEL.replace(newDisplayExtension, level);
        }
        if (TEMPL_LEVEL.isContainedBy(newDescription)) {
            newDescription = TEMPL_LEVEL.replace(newDescription, level);
        }
        
        //Class name
        if (TEMPL_CLASS_NAME.isContainedBy(newExtension)) {
            newExtension = TEMPL_CLASS_NAME.replace(newExtension, className);
        }
        if (TEMPL_CLASS_NAME.isContainedBy(newDisplayExtension)) {
            newDisplayExtension = TEMPL_CLASS_NAME.replace(newDisplayExtension, className);
        }
        if (TEMPL_CLASS_NAME.isContainedBy(newDescription)) {
            newDescription = TEMPL_CLASS_NAME.replace(newDescription, className);
        }
        
        //Class description
        if (TEMPL_CLASS_DESC.isContainedBy(newExtension)) {
            newExtension = TEMPL_CLASS_DESC.replace(newExtension, classDescription);
        }
        if (TEMPL_CLASS_DESC.isContainedBy(newDisplayExtension)) {
            newDisplayExtension = TEMPL_CLASS_DESC.replace(newDisplayExtension, classDescription);
        }
        if (TEMPL_CLASS_DESC.isContainedBy(newDescription)) {
            newDescription = TEMPL_CLASS_DESC.replace(newDescription, classDescription);
        }
        
        
        // Evaluates all the definition corresponding to the groups with administrative privileges.
        GroupOrFolderDef[] newAdministratingGroups = null;
        if (countAdministratingGroups() > 0) {
            newAdministratingGroups = new GroupOrFolderDef[countAdministratingGroups()];
            for (int i = 0; i < countAdministratingGroups(); i++) {
                newAdministratingGroups[i] = administratingGroups[i].evaluateTemplate(establishmentUAI, 
                        establishmentName, 
                        level, 
                        className, 
                        classDescription);
            }
        }
        
        return new GroupOrFolderDef(definitionType, 
                containingPath, 
                newExtension, 
                newDisplayExtension, 
                newDescription, 
                newAdministratingGroups);
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
        
        if (!(o instanceof GroupOrFolderDef)) {
            return false;
        }
        
        final GroupOrFolderDef gofd = (GroupOrFolderDef) o;
        if (!gofd.extension.equals(extension)) {
            return false;
        }
        return gofd.containingPath.equals(containingPath);
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
        if (extensionMask > 0) {
            return true;
        }
        if (displayExtensionMask > 0) {
            return true;
        }
        if (descriptionMask > 0) {
            return true;
        }
        return false;
    }

    /**
     * Getter for description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for extension.
     * @return extension.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Getter for displayExtension.
     * @return displayExtension.
     */
    public String getDisplayExtension() {
        return displayExtension;
    }

    /**
     * Getter for containingPath.
     * @return containingPath.
     */
    public String getContainingPath() {
        return containingPath;
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
        sb.append(")");
        
        return sb.toString();
    }
    
    /**
     * Gives the administrating group at a given position.
     * @param index The position of the group in the list.
     * @return the administrating group at the specified position
     */
    public GroupOrFolderDef getAdministrativeGroup(final int index) {
        return administratingGroups[index];
    }
    
//    public static final void main(final String[] argsv) {
//       GroupOrFolderDef gof1 = new GroupOrFolderDef(GroupOrFolderDef.DefinitionType.GROUP,
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

/**
 * 
 */
package org.esco.ws4Internet2Grouper.parsing;

import edu.internet2.middleware.grouper.Stem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.ws4Internet2Grouper.domain.beans.MembersDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.TemplateElement;
import org.esco.ws4Internet2Grouper.exceptions.UnknownTemplateElementTempateElement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;




/**
 * Content handler used read the defintion of groups, folders, administration elements, etc, 
 * for the Sarapis Group Service (SGS). 
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class SGSParsingUtil extends org.xml.sax.helpers.DefaultHandler implements InitializingBean {

    /** Constant use to go up in the path. */
    private static final String UP_IN_PATH = "..:";

    /** Constants to use the same path. */
    private static final String CURRENT_PATH = "."; 

    /** Constants to use the same path (escaped for replacements). */
    private static final String ESC_CURRENT_PATH = "\\."; 

    /** Folder tag. */
    private static final String FOLDER_TAG = "folder";

    /** Folder template tag. */
    private static final String FOLDER_TEMPL_TAG = "folder-template";

    /** Group tag. */
    private static final String GROUP_TAG = "group";

    /** Group template tag. */
    private static final String GROUP_TEMPL_TAG = "group-template";

    /** Administrated by tag. */
    private static final String ADMIN_BY_TAG = "administrated-by";

    /** Member of tag. */
    private static final String MEMBER_OF_TAG = "member-of";

    /** Members  tag. */
    private static final String MEMBERS_TAG = "members";

    /** Template element tag. */
    private static final String TEMPLATE_ELT_TAG = "template-element";

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SGSParsingUtil.class);

    /** Locator. */
    private Locator locator;

    /** Recursive administrating paths. */
    private Set<String> recAdminPaths = new HashSet<String>();

    /** Starting path of recursive administrating paths. */
    private Map<String, List<String>> startOfRecAdminPaths = new HashMap<String, List<String>>();

    /** The group or folder definitions manager. */
    private GroupOrFolderDefinitionsManager definitionsManager;

    /** The attribute handler. */
    private SGSAttributeHandler attributeHandler = new SGSAttributeHandler();

    /** The current group or folder folder under construction. */
    private GroupOrFolderDefinition current;

    /** The containing path. */
    private String currentPath = "";

    /** The URI of the xml file that contains the definitions for the folder, groups, administration
     * privileges and memberships. */
    private String definitionsFileURI;

    /**
     * Builds an instance of SGSParsingUtil.
     */
    public SGSParsingUtil() {
        super();
        locator = new LocatorImpl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creation of a ContentHandler: " + getClass().getSimpleName() + ".");
        }
    }

    /**
     * Checks the data injection.
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.definitionsManager, 
                "property definitionManager of class " + this.getClass().getName() 
                + " can not be null");
        Assert.notNull(this.definitionsFileURI, 
                "property definitionsFileURI of class " + this.getClass().getName() 
                + " can not be null");
    }

    /**
     * Error method.
     * @param e The initial exception.
     * @throws SAXException The initial exception.
     */
    @Override
    public void error(final SAXParseException e) throws SAXException {
        LOGGER.debug(e, e);
        throw e;
    }



    /**
     * Receives notification of the end of a document.
     * The validity of the path references, for administration or memebrships are checked
     * at this step.
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        LOGGER.debug("End of parsing.");
        final List<String> errors = definitionsManager.checksPathsReferences();
        if (!errors.isEmpty()) {
            for (String error : errors) {
                LOGGER.fatal(error);
            }
            throw new SAXParseException("Error(s) detected while checking the validity of "
                    + "the paths references (" + errors.size() 
                    + " error(s) detected). See the log file for more details.", 
                    locator);
        }

    }

    /**
     * Receive notification of the end of an element.
     * @param nameSpaceURI the Namespace URI, or the empty string if the
     * element has no Namespace URI or if Namespace
     * processing is not being performed.
     * @param localName localName the local name (without prefix), or the
     * empty string if Namespace processing is not being
     * performed.
     * @param qualifiedName The qualified XML name (with prefix), or the
     * empty string if qualified names are not available.
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String nameSpaceURI, 
            final String localName, 
            final String qualifiedName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Closing Tag: " + localName);
        }
        if (FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName)) {

            // Removes the recursive administrating path for wich
            // this folder is the starting point.
            final List<String> recAdminPathsForCurrent = startOfRecAdminPaths.get(currentPath);
            if (recAdminPathsForCurrent != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Removing recursive administrating path for " 
                            + currentPath + ": " + recAdminPathsForCurrent + ".");
                }
                for (String rap : recAdminPathsForCurrent) {
                    recAdminPaths.remove(rap);
                }
                startOfRecAdminPaths.remove(currentPath);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No recursive administrating path for " 
                        + currentPath + ".");
            }

            // Returns to the previous path.
            final int index = currentPath.lastIndexOf(Stem.DELIM);
            if (index > 0) {
                currentPath = currentPath.substring(0, index);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Returning to the previous path: " + currentPath);
            }

        }
    }

    /**
     * Resolve relative path.
     * @param tag The current processed tag.
     * @param path The  path to resolve.
     * @return The resolved path.
     * @throws SAXException If the path can't be resolved.
     */
    protected String resolvePath(final String tag, final String path) 
    throws SAXException {


        // Resolves the paths with ../../../reference/to/a/folder/
        if (path.startsWith(UP_IN_PATH)) {

            // Error the current path is not set.
            String relativePath = path;
            String currentReference = current.getPath();
            while (relativePath.startsWith(UP_IN_PATH)) {

                final int index = currentReference.lastIndexOf(Stem.DELIM);

                // Error can't go up.
                if (index < 0) {
                    final String msg = "Error while resolving path: " + path
                    + " - too much \"" + UP_IN_PATH + "\"(Tag " + tag + " - line " + locator.getLineNumber() + ").";
                    LOGGER.fatal(msg);
                    throw new SAXException(msg);
                }
                currentReference = currentReference.substring(0, index);
                relativePath = relativePath.replaceFirst(UP_IN_PATH, "");
            }
            if (!relativePath.startsWith(Stem.DELIM)) {
                relativePath = Stem.DELIM + relativePath;
            }
            return currentReference + relativePath;
        }

        // The path is relative and referes to the current path.
        if (path.startsWith(CURRENT_PATH)) {

            String resolvedPath = path.replaceFirst(CURRENT_PATH, current.getPath());
            return resolvedPath.replaceAll(ESC_CURRENT_PATH, "");
        }


        // The path is absolute.
        return path;
    }

    /**
     * Setter for the object for locating the origin of SAX document events.
     * @param locator The new locator to use.
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;

    }

    /**
     * Start of the document.
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    @Override
    public void startDocument() {
        LOGGER.debug("Starting the parsing.");

    }

    /** 
     * Registers a template element.
     */
    protected void handleTemplateElement() {
        TemplateElement.registerTemplateElement(attributeHandler.getKey());
    }

    /**
     * Handles the administration privilege informations.
     * @throws SAXException If there is a parsing exception.
     * @throws UnknownTemplateElementTempateElement If the definition of the administration
     * privileges used a template element wich is not defined.
     */
    protected void handleAdministrationPrivileges() throws SAXException, 
    UnknownTemplateElementTempateElement {

        // --- Administration Path ---//

        // Checks the administration path.
        if (attributeHandler.getPath() == null || "".equals(attributeHandler.getPath())) {
            handleAttributeError(ADMIN_BY_TAG, SGSAttributeHandler.PATH_ATTR);
        }

        // Adds administration privilege to the current group or folder definition.
        final String resolvedPath = resolvePath(ADMIN_BY_TAG, attributeHandler.getPath());
        current.addAdministratingGroupPath(resolvedPath);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding administrating path " +  attributeHandler.getPath()
                    + " to the group: " + current.getPath());
        }

        // The administrating path must registered as a recursive one.
        if (attributeHandler.getRecursive()) {
            recAdminPaths.add(resolvedPath);
            List<String> currentRecAdminPath = startOfRecAdminPaths.get(currentPath);

            if (currentRecAdminPath == null) {
                currentRecAdminPath = new ArrayList<String>();
                startOfRecAdminPaths.put(currentPath, currentRecAdminPath);
            }
            currentRecAdminPath.add(resolvedPath);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Registering starting point " + currentPath 
                        + " for recursive administrating path: " + attributeHandler.getPath() + ".");
            }
        }
    }

    /**
     * Handles the definition of who has to be a member of a group.
     * @throws SAXParseException If there is a parsing error.
     */
    protected void handleMembersRule() throws SAXParseException {

        // The tag members-of should contains :
        // Required : Type = ALL | TEACHER | STUDENT | TOS | ADMINISTRATIVE | PARENT
        // Optional : distribution-by = %aTemplateElement%
        if (attributeHandler.getType() == null) {
            handleAttributeError(MEMBERS_TAG, 
                    SGSAttributeHandler.TYPE_ATTR, 
                    MembersDefinition.MembersType.values(), 
                    null);
        }
        if (attributeHandler.getDistributionBy() != null) {
            if (!TemplateElement.isTemplateElement(attributeHandler.getDistributionBy())) {
                handleAttributeError(MEMBERS_TAG, 
                        SGSAttributeHandler.DISTRIB_BY_ATTR, 
                        TemplateElement.getAvailableTemplateElements().toArray(), 
                        attributeHandler.getDistributionBy());
            }
        }

        // Adds the definition of the members.
        final String templateKey = attributeHandler.getDistributionBy();
        final TemplateElement templateElt = TemplateElement.getAvailableTemplateelementByKey(templateKey);
        final MembersDefinition membersDef = new MembersDefinition(attributeHandler.getType(), templateElt);
        current.addMembersDefinition(membersDef);


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding members definition " +  membersDef
                    + " to the group: " + current.getPath());
        }
    }

    /**
     * Handles a membership definition.
     * @throws UnknownTemplateElementTempateElement If the definition of the membership refers to
     * an undifined template elment.
     * @throws SAXException If an error occures during the parsing process.
     */
    protected void handleMembership() throws UnknownTemplateElementTempateElement, SAXException {

        // Checks that the current definition denotes a group.
        if (current.isFolder()) {
            handleAttributeError(ADMIN_BY_TAG, SGSAttributeHandler.PATH_ATTR, 
                    "!!! Not allowed for a folder", null);
        }

        // Cheks the path this group is member of.
        if (attributeHandler.getPath() == null || "".equals(attributeHandler.getPath())) {
            handleAttributeError(ADMIN_BY_TAG, SGSAttributeHandler.PATH_ATTR);
        }

        // Handles relative paths.
        final String resolvedPath = resolvePath(MEMBER_OF_TAG, attributeHandler.getPath());
        current.addContainingGroupPath(resolvedPath);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding containing group path " +  resolvedPath
                    + " to the group: " + current.getPath());
        }
    }
    /**
     * Handles the definition of a (template) group or (template) folder.
     * @param localName the local name (without prefix), or the
     * empty string if Namespace processing is not being
     * performed.
     * @throws SAXParseException If there is a parsing error.
     * @throws UnknownTemplateElementTempateElement If the definition use an undefined template element.
     */
    protected void handleGroupOrFolder(final String localName) 
    throws SAXParseException, UnknownTemplateElementTempateElement {

        // Checks that the attributes are valid.
        if ("".equals(attributeHandler.getExtension())) {  
            handleAttributeError(localName, SGSAttributeHandler.EXT_ATTR, "", attributeHandler.getExtension());
        }
        if ("".equals(attributeHandler.getDisplayExtension())) {  
            handleAttributeError(localName, SGSAttributeHandler.DISP_EXT_ATTR, "", 
                    attributeHandler.getExtension());
        }
        if ("".equals(attributeHandler.getDescription())) {  
            handleAttributeError(localName, SGSAttributeHandler.DESC_ATTR, "", attributeHandler.getExtension());
        }

        final boolean isFolder = FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName);

        current = new GroupOrFolderDefinition(isFolder,
                attributeHandler.getPreexisting(),
                currentPath, 
                attributeHandler.getExtension(), 
                attributeHandler.getDisplayExtension(), 
                attributeHandler.getDescription());

        // Adds the recursive administrating path if needed.
        for (String recAdminPath : recAdminPaths) {
            current.addAdministratingGroupPath(recAdminPath);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding recursive admin. path: " 
                        + recAdminPath + " to the group or folder: " 
                        + current.getPath());
            }
        }

        // Registers the defintion to the  defintion manager.
        definitionsManager.registerDefinition(current);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Folder creation: " + current);
        }

        if (isFolder) {
            currentPath = current.getPath();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("New current path " + currentPath);
            }
        }
    }


    /**
     * Recieves the notification of the start of an element.
     * @param uri the Namespace URI, or the empty string if the
     * element has no Namespace URI or if Namespace
     * processing is not being performed.
     * @param localName the local name (without prefix), or the
     * empty string if Namespace processing is not being
     * performed.
     * @param qName the qualified name (with prefix), or the
     * empty string if qualified names are not available.
     * @param atts the attributes attached to the element.  If
     * there are no attributes, it shall be an empty
     * Attributes object.  The value of this object after
     * startElement returns is undefined.
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     * wrapping another exception.
     * @see org.xml.sax.ContentHandler
     * #startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, 
            final String localName, 
            final String qName, 
            final Attributes atts) throws SAXException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Opening TAG = " + localName);
        }

        attributeHandler.reset();
        attributeHandler.handleAttributes(locator, atts);
        try {
            if (TEMPLATE_ELT_TAG.equals(localName)) {

                // Defintion of a template element.
                handleTemplateElement();

            } else if (ADMIN_BY_TAG.equals(localName)) {

                // Definition of an administration privilege.
                handleAdministrationPrivileges();

            } else if (MEMBERS_TAG.equalsIgnoreCase(localName)) {
                // Definition of the members of a group.
                handleMembersRule();
            } else if (MEMBER_OF_TAG.equals(localName)) {

                // Definition of a membership of a group.
                handleMembership();
            } else if (FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName) 
                    || GROUP_TAG.equals(localName) || GROUP_TEMPL_TAG.equals(localName)) { 

                // Definition of a group, a template group, a folder or a template folder.
                handleGroupOrFolder(localName);
            } 
        } catch (UnknownTemplateElementTempateElement e) {

            // Error on a template element.
            final String msg = "Error while parsing a string with template element - Tag: " 
                + localName + " - line: " + locator.getLineNumber();
            LOGGER.fatal(msg);
            throw new SAXParseException(msg, locator, e);
        }
    }

    /**
     * Handles an attribute error.
     * @param tag The current tag.
     * @param attribute The attribute.
     * @throws SAXParseException The thrown exception.
     */
    private void handleAttributeError(final String tag, 
            final String attribute) throws SAXParseException {
        handleAttributeError(tag, attribute, "", null);
    }

    /**
     * Handles an attribute error.
     * @param tag The current tag.
     * @param attribute The attribute.
     * @param legalValues The legal values for the attribute.
     * @param actualValue The actual value of the attribute.
     * @throws SAXParseException The thrown exception.
     */
    private void handleAttributeError(final String tag, 
            final String attribute,
            final Object[] legalValues, 
            final String actualValue) throws SAXParseException {
        String legalValuesStr = null;
        if (legalValues != null) {
            legalValuesStr = Arrays.toString(legalValues);
        }
        handleAttributeError(tag, attribute, legalValuesStr, actualValue);
    }


    /**
     * Handles an attribute error.
     * @param tag The current tag.
     * @param attribute The attribute.
     * @param legalValues The string that represents the legal values for the attribute.
     * @param actualValue The actual value of the attribute.
     * @throws SAXParseException The thrown exception.
     */
    private void handleAttributeError(final String tag, 
            final String attribute,
            final String legalValues, 
            final String actualValue) throws SAXParseException {
        String msg = "Tag: " + tag
        + " - Path of the tag: " + current.getPath()  
        + " - Invalid value for the attribute {" + attribute + "}" 
        + " (line " + locator.getLineNumber() + ")";

        if (legalValues != null && !"".equals(legalValues)) {
            msg += " - Legal values are: " + legalValues;
        }

        if (actualValue != null) {
            msg += " - Actual value is: " + actualValue;
        }
        msg +=  ".";

        LOGGER.error(msg);
        throw new SAXParseException(msg, locator);
    }

    /**
     * Lanches the parsing process. 
     * @throws SAXException If there is an error during the parsing (invalid group defintion for instance).
     * @throws IOException  If there is an IO error.
     */
    public void parse() throws IOException, SAXException {


        XMLReader saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        saxReader.setFeature("http://xml.org/sax/features/validation", true);
        saxReader.setContentHandler(this);
        saxReader.setErrorHandler(this);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("---------------------------------");
            LOGGER.info("Parsing definition file: "); 
            LOGGER.info(definitionsFileURI);
            LOGGER.info("---------------------------------");
        }
        
        saxReader.parse(definitionsFileURI);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("---------------------------------");
            LOGGER.info("Definition file parsed."); 
            LOGGER.info("---------------------------------");
        }
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
     * Getter for definitionsFileURI.
     * @return definitionsFileURI.
     */
    public String getDefinitionsFileURI() {
        return definitionsFileURI;
    }

    /**
     * Setter for definitionsFileURI.
     * @param definitionsFileURI the new value for definitionsFileURI.
     */
    public void setDefinitionsFileURI(final String definitionsFileURI) {
        this.definitionsFileURI = definitionsFileURI;
    }


}

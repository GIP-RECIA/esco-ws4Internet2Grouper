/**
 * 
 */
package org.esco.ws4Internet2Grouper.util;

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
import org.apache.taglibs.standard.extra.spath.AbsolutePath;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.ws4Internet2Grouper.domain.beans.MembersDefinition;
import org.esco.ws4Internet2Grouper.domain.beans.TemplateElement;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;




/**
 * Content handler used to set the parameters for the Sarapis Grouper Service (sgs). 
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class SGSContentHandler extends org.xml.sax.helpers.DefaultHandler {



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

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SGSContentHandler.class);

    /** Locator. */
    private Locator locator;

    /** Recursive administrating paths. */
    private Set<String> recAdminPaths = new HashSet<String>();
    
    /** Starting path of recursive administrating paths. */
    private Map<String, List<String>> startOfRecAdminPaths = new HashMap<String, List<String>>();

    /** The group or folder definition manager. */
    private GroupOrFolderDefinitionsManager definitionManager = new GroupOrFolderDefinitionsManager();

    /** The attribute handler. */
    private SGSAttributeHandler attributeHandler = new SGSAttributeHandler();

    /** Definitions under construction. */
//  private Stack<GroupOrFolderDefinition> definitions = new Stack<GroupOrFolderDefinition>();

    /** The current group or folder folder under construction. */
    private GroupOrFolderDefinition current;

    /** The containing path. */
    private String currentPath = "";

    /**
     * Builds an instance of SGSContentHandler.
     */
    public SGSContentHandler() {
        super();
        locator = new LocatorImpl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creation of a ContentHandler: " + getClass().getSimpleName() + ".");
        }
    }

    /**
     * 
     * @param e
     * @throws SAXException
     */
    @Override
    public void error(final SAXParseException e) throws SAXException {
        LOGGER.debug(e, e);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(final char[] arg0, 
            final int arg1, 
            final int arg2) throws SAXException {
        //
    }

    /**
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        LOGGER.debug("End of parsing.");

    }

    /**
     * @param nameSpaceURI 
     * @param localName
     * @param rawName
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(final String nameSpaceURI, 
            final String localName, 
            final String rawName) {
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
     * @param arg0
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(final String arg0) {
        // Noting to do.
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
    throws SAXException {
        // TODO Auto-generated method stub

    }

    /**
     * @param arg0
     * @param arg1
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String arg0, String arg1)
    throws SAXException {
        LOGGER.debug("Processing instruction :" + arg0 + arg1);

    }

    /**
     * @param locator The new locator to use.
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;

    }

    /**
     * @param arg0
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String arg0) throws SAXException {
        // TODO Auto-generated method stub

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
     * @param uri the Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed
     * @param localName the local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed
     * @param qName the qualified name (with prefix), or the
     *        empty string if qualified names are not available
     * @param atts the attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.  The value of this object after
     *        startElement returns is undefined
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     *            wrapping another exception
     * @see org.xml.sax.ContentHandler
     * #startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, 
            final String localName, 
            final String qName, 
            final Attributes attributs) throws SAXException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Opening TAG = " + localName);
        }


        attributeHandler.reset();
        attributeHandler.handleAttributes(locator, attributs);

        if (ADMIN_BY_TAG.equals(localName)) {
            // --- Administration Path ---//

            // Checks the administration path.
            if (attributeHandler.getPath() == null || "".equals(attributeHandler.getPath())) {
                handleAttributeError(ADMIN_BY_TAG, SGSAttributeHandler.PATH_ATTR);
            }

            // Adds administration privilege to the current group or folder definition.
            current.addAdministratingGroupPath(attributeHandler.getPath());
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding administrating path " +  attributeHandler.getPath()
                        + " to the group: " + current.getPath());
            }
            
            
            
            // The administrating path must registered as a recursive one.
            if (attributeHandler.getRecursive()) {
                recAdminPaths.add(attributeHandler.getPath());
                List<String> currentRecAdminPath = startOfRecAdminPaths.get(currentPath);
                
                if (currentRecAdminPath == null) {
                    currentRecAdminPath = new ArrayList<String>();
                    startOfRecAdminPaths.put(currentPath, currentRecAdminPath);
                }
                currentRecAdminPath.add(attributeHandler.getPath());
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Registering starting point " + currentPath 
                            + " for recursive administrating path: " + attributeHandler.getPath() + ".");
                }
            }

           

        } else if (MEMBERS_TAG.equalsIgnoreCase(localName)) {
            // --- Members ---//

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


        } else if (MEMBER_OF_TAG.equals(localName)) {

            // --- Member of --- //

            // Checks that the current definition denotes a group.
            if (current.isFolder()) {
                handleAttributeError(ADMIN_BY_TAG, SGSAttributeHandler.PATH_ATTR, "!!! Not allowed for a folder", null);
            }

            // Cheks the path this group is member of.
            if (attributeHandler.getPath() == null || "".equals(attributeHandler.getPath())) {
                handleAttributeError(ADMIN_BY_TAG, SGSAttributeHandler.PATH_ATTR);
            }
            
            // Handles relative paths.
            String absolutePath = attributeHandler.getPath();
            if (absolutePath.indexOf(Stem.DELIM) < 0) {
                absolutePath = current.getContainingPath() + Stem.DELIM + absolutePath;
            }
            
            
            current.addContainingGroupPath(absolutePath);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding containing group path " +  absolutePath
                        + " to the group: " + current.getPath());
            }


        } else if (FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName) 
                || GROUP_TAG.equals(localName) || GROUP_TEMPL_TAG.equals(localName)) { 
            // --- Folder or group---//

            // Checks that the attributes are valid.
            if ("".equals(attributeHandler.getExtension())) {  
                handleAttributeError(localName, SGSAttributeHandler.EXT_ATTR, "", attributeHandler.getExtension());
            }
            if ("".equals(attributeHandler.getDisplayExtension())) {  
                handleAttributeError(localName, SGSAttributeHandler.DISP_EXT_ATTR, "", attributeHandler.getExtension());
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




//      if (LOGGER.isDebugEnabled()) {
//      LOGGER.debug("Attribute: " + attributs.getLocalName(index) + " = " + attributs.getValue(index));
//      }

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
        + " - Invalid value for the attribute {" + attribute + "}";

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
     * @param arg0
     * @param arg1
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(final String arg0, final String arg1) {
        // Noting to do.
    }

    /**
     * @param args
     * @throws SAXException 
     * @throws IOException 
     */
    public static void main (final String[] args) throws IOException, SAXException {


        XMLReader saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        saxReader.setFeature("http://xml.org/sax/features/validation", true);
        SGSContentHandler sgsHandler = new SGSContentHandler(); 
        saxReader.setContentHandler(sgsHandler);
        saxReader.setErrorHandler(sgsHandler);
        final String uri = "properties/export/test.xml";

        saxReader.parse(uri);
    }

}

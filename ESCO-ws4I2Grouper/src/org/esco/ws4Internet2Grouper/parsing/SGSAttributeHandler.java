/**
 * 
 */
package org.esco.ws4Internet2Grouper.parsing;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import static org.esco.ws4Internet2Grouper.domain.beans.MembersDefinition.MembersType;

/**
 * Atribute handler for the Sarapis Group Service (SGS). 
 * @author GIP RECIA - A. Deman
 * 6 août 08
 *
 */
public class SGSAttributeHandler implements Serializable {

    /** UID attribute. */
    public static final String UID_ATTR = "uid";

    /** Extension attribute. */
    public static final String EXT_ATTR = "extension";
    
    /** Display extension attribute. */
    public static final String DISP_EXT_ATTR = "display-extension";

    /** Description attribute. */
    public static final String DESC_ATTR = "description";

    /** Preexisting attribute. */
    public static final String PREEXIST_ATTR = "preexisting";

    /** Create attribute. */
    public static final String CREATE_ATTR = "create";
    
    /** Path attribute. */
    public static final String PATH_ATTR = "path";

    /** Right attribute. */
    public static final String RIGHT_ATTR = "right";
    
    /** Recursive attribute. */
    public static final String RECURS_ATTR = "recursive";

    /** Type attribute. */
    public static final String TYPE_ATTR = "type";

    /** Distribution by attribute. */
    public static final String DISTRIB_BY_ATTR = "distribution-by";
    
    /** Key attribute. */
    public static final String KEY_ATTR = "key";
    
    /** value attribute. */
    public static final String VALUE_ATTR = "value";
    
    /** Serial version UID.*/
    private static final long serialVersionUID = 8391732649319550004L;
    
    /** Logger.*/
    private static final Logger LOGGER = Logger.getLogger(SGSAttributeHandler.class);
    
    /** UID value. */
    private String uid;
    
    /** Extension value. */
    private String extension;
    
    /** Display extension value. */
    private String displayExtension;
    
    /** Description value. */
    private String description;
    
    /** Preexisting value. */
    private Boolean preexisting;
    
    /** Create value. */
    private Boolean create;
    
    /** Path value. */
    private String path;

    /** Right value. */
    private String right;
    
    /** Recursive value. */
    private Boolean recursive;
    
    /** Type value. */
    private MembersType type;
    
    /** Distribution by value. */
    private String distributionBy;
    
    /** Key for a template element. */
    private String key;
    
    /** value attribute. */
    private Boolean value;
    
    /** 
     * Builds an instance of SGSAttributeHandler.
     */
    public SGSAttributeHandler() {
        LOGGER.debug("Creation of an instance of " + getClass().getSimpleName() + ".");
    }
    
    /**
     * handles attributes.
     * @param locator The locator.
     * @param attributs The attributs to handle.
     * @throws SAXParseException If one of the  parsed value is not valid.
     */
    public void handleAttributes(final Locator locator, 
            final Attributes attributs) throws SAXParseException {
        
        for (int index = 0; index < attributs.getLength(); index++) {
            handleAttribute(locator, 
                    attributs.getLocalName(index), 
                    attributs.getValue(index));
        }
    }
    
    /**
     * Handles an attribute.
     * @param locator The current locator in the xml file.
     * @param attributeName The name of the attribute to handle.
     * @param attributeValue The value of the attribute to handle.
     * @throws SAXParseException If the value of the attribute has to be parse and is
     * not valid.
     */
    public void handleAttribute(final Locator locator, 
            final String attributeName, 
            final String attributeValue) throws SAXParseException {
        
        final String trimedAttrName = attributeName.trim();
        
        
        if (UID_ATTR.equals(trimedAttrName)) {
            uid = attributeValue.trim();
        } else if (EXT_ATTR.equals(trimedAttrName)) {
            extension = attributeValue.trim();
        } else if (DISP_EXT_ATTR.equals(trimedAttrName)) {
            displayExtension = attributeValue.trim();
        } else if (DESC_ATTR.equals(trimedAttrName)) {
            description = attributeValue.trim();
        } else if (VALUE_ATTR.equals(trimedAttrName)) {
           value = parseBoolean(locator, trimedAttrName, attributeValue);
        } else if (PREEXIST_ATTR.equals(trimedAttrName)) {
            preexisting = parseBoolean(locator, trimedAttrName, attributeValue);
            // Checks that the create and preexisting attribut are not true together.
            if (preexisting && create) {
                throw new SAXParseException("The attribute " + PREEXIST_ATTR + " and " + CREATE_ATTR  
                        + " can't be true at the same time.", locator);
            }
        
        } else if (CREATE_ATTR.equals(trimedAttrName)) {
            create = parseBoolean(locator, trimedAttrName, attributeValue);
            // Checks that the create and preexisting attribut are not true together.
            if (preexisting && create) {
                throw new SAXParseException("The attribute " + PREEXIST_ATTR + " and " + CREATE_ATTR  
                        + " can't be true at the same time.", locator);
            }
        } else if (PATH_ATTR.equals(trimedAttrName)) {
            path = attributeValue.trim();
        } else if (RIGHT_ATTR.equals(trimedAttrName)) {
            right = attributeValue.trim();
        } else if (RECURS_ATTR.equals(trimedAttrName)) {
            recursive = parseBoolean(locator, trimedAttrName, attributeValue);
        } else if (TYPE_ATTR.equals(trimedAttrName)) {
            type = MembersType.parseIgnoreCase(attributeValue);
            if (type == null) {
                throw new SAXParseException("Illegal value for atribute " 
                        + attributeName + " legal values are : " 
                        + Arrays.toString(MembersType.values()) 
                        + " found : " + attributeValue + ".", locator);
            }
        } else if (DISTRIB_BY_ATTR.equals(trimedAttrName)) {
            distributionBy = attributeValue.trim();
        } else if (KEY_ATTR.equals(trimedAttrName)) {
            key = attributeValue.trim();
        }
        

    }
    
    /**
     * Resets the handler.
     */
    public void reset() {
        extension = null;
        displayExtension = null;
        description = null;
        preexisting = false;
        create = false;
        path = null;
        recursive = false;
        type = null;
        right = null;
        distributionBy = null;
    }

    /**
     * Parse a boolean attribute.
     * @param locator The locator for the parsed file..
     * @param attribute The attribute to parse.
     * @param valueStr The value to parse.
     * @return True of false.
     * @throws SAXParseException If the value is not true or false (case insensitive).
     */
    protected boolean parseBoolean(final Locator locator, 
            final String attribute, 
            final String valueStr) throws SAXParseException {
        final String trimedValue = valueStr.trim();
        if (trimedValue.equalsIgnoreCase("true")) {
            return true;
        } else if (trimedValue.equalsIgnoreCase("false")) {
            return false;
        }
        throw new SAXParseException("Illegal value for boolean atribute " 
                + attribute + " (should be true or fale): " + valueStr + ".", locator);
        
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
     * Getter for description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for preexisting.
     * @return preexisting.
     */
    public Boolean getPreexisting() {
        return preexisting;
    }

    /**
     * Getter for path.
     * @return path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter for recursive.
     * @return recursive.
     */
    public Boolean getRecursive() {
        return recursive;
    }

    /**
     * Getter for type.
     * @return type.
     */
    public MembersType getType() {
        return type;
    }

    /**
     * Getter for distributionBy.
     * @return distributionBy.
     */
    public String getDistributionBy() {
        return distributionBy;
    }

    /**
     * Getter for key.
     * @return key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for uid.
     * @return uid.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Getter for value.
     * @return value.
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * Getter for right.
     * @return right.
     */
    public String getRight() {
        return right;
    }

    /**
     * Getter for create.
     * @return create.
     */
    public Boolean getCreate() {
        return create;
    }
}

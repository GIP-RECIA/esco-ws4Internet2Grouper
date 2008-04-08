/**
 * Package for the beans.
 */
package org.esco.ws4Internet2Grouper.domain.beans;


import java.io.Serializable;

/**
 * Data (key and description) about a group or a stem fetched from Grouper.
 * @author GIP RECIA - A. Deman
 * 27 nov. 07
 *
 */
public class GrouperDTO implements Serializable {
	
	/** Serial version UID. */
	private static final long serialVersionUID = -2273437556752084889L;

	/** Name of the group. */
	private String key;
	
	/** Name of the group. */
	private String name;
	
	/** Description of the group. */
	private String description;
	
	/** The parent stem in the case of a group. */
	private String parentStem;


    /** If the DTO denotes a group, this flag is true if the group is not a subgroup
     * of another group.*/
    private boolean root;

    /**
	 * Constructor for IGroupOrStemInformations.
	 */
	public GrouperDTO() { /**/ }
	
	/**
	 * Constructor for IGroupOrStemInformations.
	 * @param key The key of the group.
	 * @param name The name of the group.
	 * @param description The description of the group.
	 */
	public GrouperDTO(final String key, final String name, final String description) {
		this.key = key;
		this.name = name;
		this.description = description;
	}
	
	/**
     * Constructor for IGroupOrStemInformations.
     * @param parentStem the name of the parent stem (only for groups).
     * @param root True if the group is not contained in another group.
     * @param key The key of the group.
     * @param name The name of the group.
     * @param description The description of the group.
     */
    public GrouperDTO(final String parentStem,
            final boolean root,
            final String key, 
            final String name, 
            final String description) {
        this(key, name, description);
        this.parentStem = parentStem;
        this.root = root;
    }
	
	/**
	 * Gives the hash value for this instance.
	 * @return The hash value. 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (key == null) {
			return 0;
		} 
		return key.hashCode();
	}	
	
	/**
	 * Tests if an object is equal to this instance.
	 * @param o The object to test.
	 * @return True if o is an instance of IGroupOrStemInformations 
	 * equal to this instance.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof GrouperDTO)) {
			return false;
		}
		
		final GrouperDTO gsi = (GrouperDTO) o; 
		if (key != null) {
			return key.equals(gsi.getKey());
		}
		
		return gsi.getKey() == null;
	}
	
	/**
	 * Gives the string representation of this object.
	 * @return The string representation of the values of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append("#(");
		
		if (hasParentStem()) {
		    sb.append("(");
		    sb.append(parentStem);
		    sb.append("), ");
		    
		    if (isRoot()) {
		        sb.append("root, ");
		    } 
		}
		
		sb.append(key);
		sb.append(", ");
		sb.append(name);
		sb.append(", ");
		sb.append(description);
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Tests if the DTO has a parentStem information.
	 * @return True if the DTO has a parentStem information.
	 */
	public boolean hasParentStem() {
	    return parentStem != null;
	}
	
	/**
	 * Getter for parentStem.
	 * @return parentStem.
	 */
	public String getParentStem() {
	    return parentStem;
	}
	

    /**
     * Getter for root.
     * @return the root
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Setter for root.
     * @param root the root to set
     */
    public void setRoot(final boolean root) {
        this.root = root;
    }
	
	/**
	 * Setter for parentStem.
	 * @param parentStem The new value for parentStem.
	 */
	public void setParentStem(final String parentStem) {
        this.parentStem = parentStem;
    }
        
	/**
	 * Getter for key.
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Setter for key.
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Getter for description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for description.
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Getter for name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name.
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
}

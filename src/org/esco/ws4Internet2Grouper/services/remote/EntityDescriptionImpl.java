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

package org.esco.ws4Internet2Grouper.services.remote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.esco.ws4Internet2Grouper.domain.beans.PersonType;

/**
 * Reference implementation of anIEntityDescription.
 * This class can be overridden in order to facilitate the manipulation of the
 * attributes values.
 * @author GIP RECIA - A. Deman
 * 9 déc. 2009
 *
 */
public class EntityDescriptionImpl implements IEntityDescription {
	
	/**
	 * Iterator on a personDescription. The values associated to this iterator are the
	 * arrays of attributes values. If the person description use a grouped attribute value,
	 * the number of arrays is equal to the number of grouped values.
	 * @author GIP RECIA - A. Deman
	 * 9 dec. 2009
	 *
	 */
	private class EntityDescriptionIterator implements Iterator<String[]> {

		/** The person description associated to this iterator. */
		private IEntityDescription personDescription;
		
		/** The number of arrays of values associated to the person description. */
		private int size;
		
		/** The current position of the iterator regarding the available arrays of values. */
		private int index;
		
		/** Iterator for the grouped values if needed. */
		private Iterator<String> subIt;
		
		/**
		 * Builds an instance of PersonDescriptionIterator.
		 * @param personDescription The person description associated to this instance.
		 */
		public EntityDescriptionIterator(final EntityDescriptionImpl personDescription) {
			this.personDescription = personDescription;
			if (!personDescription.hasGroupedAttributeValues()) {
				size = 1;
			} else {
				size = personDescription.getGroupedAttributeValues().size();
				subIt = personDescription.getGroupedAttributeValues().iterator();
			}
		}
		
		/**
		 * Tests if there is another array of values.
		 * @return Trues if the iterator has another value.
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return index < size;
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.Iterator#next()
		 */
		public String[] next() {
			if (index >= size) {
				throw new NoSuchElementException("Requested element: " + index 
						+ " - Last available position: " + (size - 1));
			}
			final Collection<String> attValSet = personDescription.getGroupedAttributeValues(); 
			final String[]  attValArr = attValSet.toArray(new String[attValSet.size()]);
			if (personDescription.hasGroupedAttributeValues()) {
				attValArr[index] = subIt.next();
			}
			
			index++;
			return attValArr;
		}

		/**
		 * Unsupported method.
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException("The method remove in the class"
					+ getClass().getName() + " is not allowed.");
		}
	}
	
	/** Serial version UID.*/
	private static final long serialVersionUID = -3025736883765730578L;
	
	/** Values of the attributes with the order used to give them to the web service. */
	private List<String> values = new ArrayList<String>();
	
	/** Order of the grouped attribute values, if used. */
	private int groupedAttributeValuesPosition;
	
	/** Grouped attribute values. */
	private Collection<String> groupedAttributeValues;

    /** The type of the person. */
    private PersonType type;
    
    /** The Id of the person. */
    private String id;
    
    /** The position with the max value. */
    private int maxPostionValue;
    
    /**
     * Builds an instance of EntityDescriptionImpl.
     * @param id The id of the person.
     */
    public EntityDescriptionImpl(final String id) {
    	super();
    }
    
	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#getType()
	 */
	public PersonType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#hasIdInformation()
	 */
	public boolean hasIdInformation() {
		return !"".equals(id) && id != null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#hasTypeInformation()
	 */
	public boolean hasTypeInformation() {
		return type != null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IPerson2Description#
	 * @throws IllegalStateException if the value for the position position is already set,
	 * for another attribute value or for the groupedAttributeValues.
	 * setAttributeValue(int, java.lang.String)
	 */
	public void setAttributeValue(final int position, final String value) {
		
		// Handles the new position.
		handleNewPosition(position);
		values.set(position, value);
	}
	

	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#getAttributeValue(int)
	 */
	public String getAttributeValue(final int position) {
		if (position > maxPostionValue) {
			throw new NoSuchElementException("Request element at position: "
					+ position + " - Max position: " + maxPostionValue);
		}
		
		if (groupedAttributeValuesPosition == position) {
			return groupedAttributeValues.toString();
		}
		
		return values.get(position);
		
	}
	
	/**
	 * Handles a new position.
	 * @param position The new position to handle.
	 */
	private void handleNewPosition(final int position) {
		if (position <= maxPostionValue) {
			if (groupedAttributeValuesPosition == position) {
				throw new IllegalStateException("The position " + position 
						+ " is already used by the property groupedAttributeValuesPosition.");
			}
			if (!"".equals(values.get(position))) {
				throw new IllegalStateException("The position " + position 
						+ " is already used for an attribute value.");
			}
		} else if (position > maxPostionValue) {
			for (int i = maxPostionValue + 1; i < position; i++) {
				values.set(i, "");
			}
			maxPostionValue = position;
		}
	}
	
		/**
	 * @param position
	 * @param values
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#
	 * setGroupedAttributeValues(int, java.util.Set)
	 * @throws IllegalStateException If the property groupedAttributeValues is already set
	 * or if the position is already used.
	 */
	public void setGroupedAttributeValues(final int position, final Collection<String> groupedAttributeValues) {
		handleNewPosition(position);
		this.groupedAttributeValues = groupedAttributeValues;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#getGroupedAttributeValues()
	 */
	public Collection<String> getGroupedAttributeValues() {
		return groupedAttributeValues; 

	}
	
	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#hasGroupedAttributeValues()
	 */
	public boolean hasGroupedAttributeValues() {
		return groupedAttributeValues != null;
	}


	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#setId(java.lang.String)
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.ws4Internet2Grouper.services.remote.IEntityDescription#
	 * setType(org.esco.ws4Internet2Grouper.domain.beans.PersonType)
	 */
	public void setType(final PersonType type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<String[]> iterator() {
		return new EntityDescriptionIterator(this);
	}

	
}

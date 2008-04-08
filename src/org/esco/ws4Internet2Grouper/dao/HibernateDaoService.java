/**
 * ESCO Web Service for Grouper - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/ws4Internet2Grouper
 */
package org.esco.ws4Internet2Grouper.dao;


import org.esupportail.commons.dao.AbstractHibernateDaoService;

/**
 * The Hiberate implementation of the DAO service.
 * 
 * See /properties/dao/dao-example.xml
 */
public class HibernateDaoService extends AbstractHibernateDaoService implements DaoService {

	/**
	 * Bean constructor.
	 */
	public HibernateDaoService() {
		super();
	}
}

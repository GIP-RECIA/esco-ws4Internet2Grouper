/**
 * ESCO Web Service for Grouper - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/ws4Internet2Grouper
 */
package org.esco.ws4Internet2Grouper.web.controllers;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.utils.ContextUtils;
import org.esupportail.commons.utils.strings.StringUtils;
import org.esupportail.commons.web.controllers.ExceptionController;

/**
 * A bean to memorize the context of the application.
 */
public class SessionController extends AbstractDomainAwareBean {

	/**
	 * The name of the parameter that gives the logout URL.
	 */
	private static final String LOGOUT_URL_PARAM = "edu.yale.its.tp.cas.client.logoutUrl";

	/**
	 * The exception controller (called when logging in/out).
	 */
	private ExceptionController exceptionController;
	
	/**
	 * Constructor.
	 */
	public SessionController() {
		super();
	}

	/**
	 * @see org.esco.ws4Internet2Grouper.web.controllers.AbstractDomainAwareBean
	 * #afterPropertiesSetInternal()
	 */
	@Override
	public void afterPropertiesSetInternal() {
		Assert.notNull(this.exceptionController, "property exceptionController of class " 
				+ this.getClass().getName() + " can not be null");
		
	}

	
	/**
	 * @return a debug String.
	 */
	public String getDebug() {
		return toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + "#" + hashCode();
	}

	
	/**
	 * JSF callback.
	 * @return a String.
	 * @throws IOException 
	 */
	public String logout() throws IOException {
		if (ContextUtils.isPortlet()) {
			throw new UnsupportedOperationException("logout() should not be called in portlet mode.");
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String logoutUrl = externalContext.getInitParameter(LOGOUT_URL_PARAM);
		if (logoutUrl == null) {
			throw new ConfigException("context parameter '" + LOGOUT_URL_PARAM + "' not found");
		}
		String returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/[^/]*$", "");
		String forwardUrl = String.format(logoutUrl, StringUtils.utf8UrlEncode(returnUrl));
		// note: the session beans will be kept event when invalidating 
		// the session so they have to be reset (by the exception controller).
		// We invalidate the session however for the other attributes.
		request.getSession().invalidate();
		request.getSession(true);
		// calling this method will reset all the beans of the application
		exceptionController.restart();
		externalContext.redirect(forwardUrl);
		facesContext.responseComplete();
		return null;
	}

	/**
	 * @param exceptionController the exceptionController to set
	 */
	public void setExceptionController(final ExceptionController exceptionController) {
		this.exceptionController = exceptionController;
	}


}

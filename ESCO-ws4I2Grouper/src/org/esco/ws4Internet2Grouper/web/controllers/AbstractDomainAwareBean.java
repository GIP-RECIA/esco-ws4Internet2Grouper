/**
 * ESCO Web Service for Grouper - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/ws4Internet2Grouper
 */
package org.esco.ws4Internet2Grouper.web.controllers;



import org.esco.ws4Internet2Grouper.domain.DomainService;
import org.esupportail.commons.beans.AbstractApplicationAwareBean;
import org.esupportail.commons.services.urlGeneration.UrlGenerator;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.web.controllers.Resettable;
/**
 * An abstract class inherited by all the beans for them to get:
 * - the domain service (domainService).
 * - the application service (applicationService).
 * - the i18n service (i18nService).
 */
public abstract class AbstractDomainAwareBean extends AbstractApplicationAwareBean implements Resettable {

	/**
	 * A logger.
	 */
	//private final Logger logger = new LoggerImpl(this.getClass());
	
	/**
	 * see {@link DomainService}.
	 */
	private DomainService domainService;
	
	/**
	 * The URL generator.
	 */
	private UrlGenerator urlGenerator;

	/**
	 * Constructor.
	 */
	protected AbstractDomainAwareBean() {
		super();
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public final void afterPropertiesSet() {
		super.afterPropertiesSet(); 
		Assert.notNull(this.domainService, 
				"property domainService of class " + this.getClass().getName() + " can not be null");
		Assert.notNull(this.urlGenerator, 
				"property urlGenerator of class " + this.getClass().getName() + " can not be null");
		afterPropertiesSetInternal();
		reset();
	}

	/**
	 * This method is run once the object has been initialized, just before reset().
	 */
	protected void afterPropertiesSetInternal() {
		// override this method
	}
	
	/**
	 * @see org.esupportail.commons.web.controllers.Resettable#reset()
	 */
	public void reset() {
		// nothing to reset
		
	}

	/**
	 * @param domainService the domainService to set
	 */
	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}

	/**
	 * @return the domainService
	 */
	public DomainService getDomainService() {
		return domainService;
	}

	/**
	 * @return the urlGenerator
	 */
	protected UrlGenerator getUrlGenerator() {
		return urlGenerator;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(final UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

}

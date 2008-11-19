package org.esco.ws4Internet2Grouper.cache;



import java.util.Arrays;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.esco.ws4Internet2Grouper.domain.beans.GroupOrFolderDefinition;
import static org.esco.ws4Internet2Grouper.domain.beans.MembersDefinition.MembersType;

/** 
 * Cache manager for the Sarapis Group Service.
 * This cache is a high level cache as Grouper manage its own caches.
 * @author GIP RECIA - A. Deman
 * 30 juil. 08
 *
 */
public class SGSCache {

  

    /** Cache name for the memberships. */
    private static final String MEMBERSHIPS_CACHE_NAME =  SGSCache.class + ".memberships";
    
    /** Cache name for the memberships for groups defined as template. */
    private static final String MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME =  SGSCache.class + ".memberships-for-templates";

    /** Cache name for the template which are created even if they are empty. */
    private static final String EMPTY_TEMPLATES_CACHE_NAME =  SGSCache.class + ".empty-templates";

    /** Singleton. */
    private static final SGSCache INSTANCE = new SGSCache();


    /** Cache for the memberships. */
    private Cache membershipsCache;
    
    /** Cache for the memberships for template groups. */
    private Cache membershipsTemplatesCache;
    
    /** Cache for the empty templates. */
    private Cache emptyTemplatesCache;


    /**
     * Builds an instance of SGSCache.
     */
    protected SGSCache() {
        final CacheManager cacheManager = CacheManager.getInstance(); 
       
        if (!cacheManager.cacheExists(MEMBERSHIPS_CACHE_NAME)) {
            cacheManager.addCache(MEMBERSHIPS_CACHE_NAME);
        }
        
        if (!cacheManager.cacheExists(MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME)) {
            cacheManager.addCache(MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME);
        }
        
        if (!cacheManager.cacheExists(EMPTY_TEMPLATES_CACHE_NAME)) {
            cacheManager.addCache(EMPTY_TEMPLATES_CACHE_NAME);
        }

        membershipsCache = cacheManager.getCache(MEMBERSHIPS_CACHE_NAME);
        membershipsTemplatesCache = cacheManager.getCache(MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME);
        emptyTemplatesCache = cacheManager.getCache(EMPTY_TEMPLATES_CACHE_NAME);
    }

    /**
     * Gives the singleton instance.
     * @return The available instance.
     */
    public static SGSCache instance() {
        return INSTANCE;
    }

  

    /**
     * Caches the memeberships for a given type of member and a set of attributes.
     * @param definitions The groups definitions.
     * @param type The type of members.
     * @param attributes The list of attributes.
     */
    public void cacheMemebrships(final Set<GroupOrFolderDefinition> definitions,
            final MembersType type, 
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        membershipsCache.put(new Element(key, definitions));
    }
    
   
    
    /**
     * Caches the memeberships of template groups for a given type of member and a set of attributes.
     * @param definitions The groups definitions.
     * @param type The type of members.
     * @param attributes The list of attributes.
     */
    public void cacheMemebrshipsForTemplates(final Set<GroupOrFolderDefinition> definitions,
            final MembersType type, 
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        membershipsTemplatesCache.put(new Element(key, definitions));
    }

    /**
     * Tries to retrieve the memeberships from the cache.
     * @param type The type of the member.
     * @param attributes The attributes of the memeber.
     * @return The memberships if found, null otherwise
     */
    @SuppressWarnings("unchecked") 
    public Set<GroupOrFolderDefinition> getMemberships(final MembersType type, 
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        final Element elt = membershipsCache.get(key);
        if (elt != null) {

            return (Set<GroupOrFolderDefinition>) elt.getObjectValue();
        }
        return null;
    }
    
    /**
     * Tries to retrieve the memeberships for template groups from the cache.
     * @param type The type of the member.
     * @param attributes The attributes of the member.
     * @return The memberships if found, null otherwise
     */
    @SuppressWarnings("unchecked") 
    public Set<GroupOrFolderDefinition> getMembershipsForTemplates(final MembersType type, 
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        final Element elt = membershipsTemplatesCache.get(key);
        if (elt != null) {
            
            return (Set<GroupOrFolderDefinition>) elt.getObjectValue();
        }
        return null;
    }
    
    /**
     * Tests if an empty template is cached.
     * @param definition The definition that corresponds to the empty template.
     * @return True if the definition is cached.
     */
    public boolean emptyTemplateIsCached(final GroupOrFolderDefinition definition) {
        return emptyTemplatesCache.get(definition.getPath()) != null;
    }
    
    /**
     * Caches the definition for an empty template.
     * @param definition The definition associated to the template.
     */
    public void cacheEmptyTemplate(final GroupOrFolderDefinition definition) {
        emptyTemplatesCache.put(new Element(definition.getPath(), true));
    }
}

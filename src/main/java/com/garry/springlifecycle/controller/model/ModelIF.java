package com.garry.springlifecycle.controller.model;


import org.springframework.cache.annotation.Cacheable;

import java.io.Serializable;

/**
 * Domain Model should normal live in memory not in database.
 * so cache in memory is very important for domain model life cycle.
 * 
 * the class  be cached or setModified is important, this method can be
 * used to refresh the cache. the controller is in com.jdon.model.cache.ModelCacheManager
 * 
 * the difference with setModified and setCacheable;
 * setCacheable to false, the model will never be saved in the cache.
 * 
 * setModified to true, if the model exists in the cache, the client will not 
 * get it from cache, and in the meantime framework delete the model from the cache.
 * 
 * deleting the model from cache must have event condition that the deleting operator
 * can access the cache of the container, if it cann't access the container, 
 * it cann't delete the model from cache. such it is EJB. 
 * 
 * 
 * 
 * @see  com.jdon.domain.model.cache.ModelCacheManager
 */
public interface ModelIF extends Cacheable, Cloneable, Serializable {

    /**
     * in the past version, this method name is isCacheble,
     * now change it after 1.3 !
     */
    public boolean isCacheable();
    
    /**
     * in the past version, this method name is setCacheble,
     * now change it  after 1.3 !
     */
    public void setCacheable(boolean cacheable);
    
    public boolean isModified();

    /**
     * set the property has been modified such as : setName(String name){
     * this.name = name; setModified(true); }
     * 
     */
    public void setModified(boolean modified) ;
    
    

}

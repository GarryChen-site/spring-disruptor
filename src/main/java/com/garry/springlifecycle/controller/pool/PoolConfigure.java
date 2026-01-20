/*
 * 
 */
package com.garry.springlifecycle.controller.pool;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class PoolConfigure {

     private final String maxPoolSize;

    /**
     * @param maxPoolSize
     */
    public PoolConfigure() {
        super();
        this.maxPoolSize = "1000";
    }
    
    
    /**
     * @return Returns the maxPoolSize.
     */
    public String getMaxPoolSize() {
        return maxPoolSize;
    }
   
}

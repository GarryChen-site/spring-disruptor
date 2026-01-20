/*
 * 
 */
package com.garry.springlifecycle.businessproxy.target;


/**
 *
 */
public interface TargetServiceFactory {
    
    public abstract Object create() ;
    
    public abstract Object destroy();
    
}
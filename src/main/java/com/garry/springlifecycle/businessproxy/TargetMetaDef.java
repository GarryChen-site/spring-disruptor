package com.garry.springlifecycle.businessproxy;


import com.garry.springlifecycle.businessproxy.target.TargetObjectFactory;

import java.io.Serializable;


/**
 * meta definition for service
 *
 *
 */
public interface TargetMetaDef extends Serializable {

  boolean isEJB();
  
  String getName();

  String getClassName();

  String getCacheKey();
  
  Class[] getInterfaces();
  
  void setInterfaces(Class[] interfaces);
  
  TargetObjectFactory getTargetObjectFactory();
  
  


}

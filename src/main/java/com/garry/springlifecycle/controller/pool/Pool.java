/**
 */

package com.garry.springlifecycle.controller.pool;


public interface Pool {

  public void setMaxPoolSize(int maxPoolSize);

  public int getMaxPoolSize();

  public Object acquirePoolable() throws Exception;

  public void releasePoolable(Object target) throws Exception;

  public int getNumActive();

  public int getNumIdle();

}

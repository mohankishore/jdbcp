/**
 * Copyright 2012 Apple, Inc
 * Apple Internal Use Only
 **/


package com.github.mohankishore.jdbcp.pool;

public interface PoolableObjectFactory<T> {
    
    public void activateObject(T obj) throws Exception;
    
    public void destroyObject(T obj) throws Exception;
    
    public T makeObject() throws Exception;
    
    public void passivateObject(T obj) throws Exception;
    
    public boolean validateObject(T obj) throws Exception;
    
}

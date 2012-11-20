/**
 * Copyright 2012 Apple, Inc
 * Apple Internal Use Only
 **/


package com.github.mohankishore.jdbcp.pool;

import java.util.Properties;

public interface ObjectPool<T> {
    
    public T borrowObject() throws Exception;
    
    public void returnObject(T obj) throws Exception;
    
    public void invalidateObject(T obj) throws Exception;
    
    // optional?
    
    public void clean() throws Exception; 
    // options to: graceful-destroy / force-destroy the existing object
    // is the min-object setting honored?     
    
    // oracle's shutdown
    public void close() throws Exception; // options to: graceful-destroy / force-destroy
    public boolean isClosed();
    
    // oracle's start
    public void suspend() throws Exception; // options to: keep-objects / graceful-destroy / force-destroy
    public boolean isSuspended();
    
    // oracle's stop
    public void resume() throws Exception;
    
    public int getNumActive() throws Exception;
    public int getNumIdle() throws Exception;
    // oracle stats:
    // numCreated
    // numDestroyed
    
    // more from commons-pool
    public void addObject() throws Exception;
    public void setFactory(PoolableObjectFactory<T> factory);
    
    //more from oracle
    // couple of registerXxxCallback methods
    public void refresh() throws Exception; // all connections; graceful - borrowed connections are flagged and refreshed on return
    public void recycle() throws Exception; // only affects the idle connections that are detected to be invalid 
    public void purge() throws Exception; // all connections are aborted and removed; forceful - borrowed connections error out
    public void reconfigure(Properties props) throws Exception; // graceful - async if needed.
    public Object getState(); // { STOPPED -> STARTING -> RUNNING -> STOPPING -> STOPPED } + FAILED
    public Object getStatistics() throws Exception; // fairly long list
    /*
     * - totalConnectionsCount - available + borrowed
     * - availableConnectionsCount
     * - borrowedConnectionsCount
     * 
     * - averageBorrowedConnectionsCount - averaged over ??
     * - peakConnectionsCount
     * - remainingPoolCapacityCount = (maxSize - borrowed)
     *
     * - connectionsCreatedCount
     * - connectionsClosedCount
     * 
     * - averageConnectionWaitTime
     * - peakConnectionWaitTime
     * - abandonedConnectionsCount
     * - pendingRequestsCount
     * 
     * - cumulativeConnectionBorrowedCount
     * - cmulativeConnectionReturnedCount
     * - cumulativeConnectionUseTime
     * - cumulativeConnectionWaitTime
     * - cumulativeSuccessfulConnectionWaitCount
     * - cumulativeSuccessfulConnectionWaitTime
     * - cumulativeFailedConnectionWaitCount
     * - cumulativeFailedConnectionWaitTime
     */
    
    // additional attributes from GenericObjectPool implementation:
    /*
     * int minIdle, 
     * int maxIdle, [8]
     * int maxActive, [8]
     * byte whenExhaustedAction, // [block]/fail/grow
     * long maxWait, // in millis - works with the block action above; [-1]
     * boolean testOnBorrow, [false]
     * boolean testOnReturn, [false]
     * boolean testWhileIdle, [false] - only useful if timeBetweenEvictionRunsMillis > 0
     * long timeBetweenEvictionRunsMillis, [-1] 
     * int numTestsPerEvictionRun, // [3]; if -ve, used as a fraction/percentage indicator: numIdle * 1/abs(xxx)
     * long minEvictableIdleTimeMillis, // [30 mins]; does not honor the minIdle
     * long softMinEvictableIdleTimeMillis, // [-1]; honors the minIdle
     * boolean lifo // [true]; to let connections be idle and get reaped..
     * 
     * new in 2.0
     * boolean logAbandoned,
     * boolean removeAbandonedOnBorrow,
     * boolean removeAbandonedOnMaintenance,
     * int removeAbandonedTimeout
     */
    
    // additional attributes from oracle ucp:
    /*
     * int maxConnectionReuseCount, // max-check-out-count, [0] -> disabled
     * int maxConnectionReuseTime, // timout-since-creation in seconds [0] -> disabled
     * int abandonedConnectionTimeout, // timeout-since-last-use in seconds, [0] -> disabled
     * int timeToLiveConnectionTimeout // timeout-since-checkout in seconds, [0] -> disabled
     * 
     * int waitTimeout == dbcp.maxWait // but in seconds, [3]
     * int inactiveConnectionTimeout == minEvictableIdleTimeMillis // but in seconds, [0] -> disabled
     * int timeoutCheckInterval // in seconds; the timeout enforcer thread frequency [30]
     * 
     * boolean validateConnectionOnBorrow == dbcp.testOnBorrow
     * String sqlForValidateConnection // defaults to null -> issues an internal ping
     * 
     * int minPoolSize, // [0]
     * int initialPoolSize, // [0]
     * int maxPoolSize, [MAX_INT]
     * 
     */
    
    // Oracle's PooledConnection implements an interface called ValidConnection, which has two methods: isValid and setValid
    // - allows clients to mark a connection as bad; will be recycled after being returned to the pool.
    
    // logging support
    
    // metrics support - ability to dynamically turn on and off (if performance is an issue)
    
    // JMX
    
    // exception handling
    
    // look for opportunities to simplify/choose-for-everyone
    /*
     * - size (no min/max)
     * - fail on hitting max
     */
}

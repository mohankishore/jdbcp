jdbcp
=====

Open, pluggable, extensible, cluster-aware, enterprise ready database connection pool

Clean room implementation that parallels the commons-pool and commons-dbcp libraries, but makes it lighter-weight, cluster-aware and failover-ready. There will be a strong focus on keeping the core clean and small, with pluggable extension points to support the advanced/optional features.

The following sections list out some interesting "add-on" features and how they can be made pluggable.

## Extensible design
* event based
** pool.start
** pool.resume
** pool.error
** pool.suspend
** pool.stop
** object.create
** object.borrow
** object.error
** object.invalid?
** object.return
** object.destroy
* bone-cp allows few additional ones:
** create-fail
** before-execute
** execute-timeout
** after-execute
** database-potentially-down
* We should allow plugins to fire their own events. What does the payload look like? Event handling method signature?

## Failover
* Failover can be broken into different phases: configure (alternatives), detect, decide (local/cluster), execute (local/cluster)
* Configuration:
** composite pools - pointing to other pools?
* Detection:
** timer based database health check: can be plugged in via pool-start/stop listeners, which marks the pool as "potentially down"?
** error monitoring: can be plugged in as error-listener?
* Decide
** local JVM
** cluster-wide quorum based synchronized failover: listen to potentially-down events, and call "suspend()" method -OR- fire an "actually down" event.
** automated failback? If yes, will need to keep testing the database to detect when its back up.
** keep checking if it is "suspended", but stop checking if it is "stopped"?
** fixed rate of checking, or gradually slow down the frequency with which we are checking?
* Execute
** ??

## Clustering
* with out of box support for? jgroups? zookeeper? JMS?
* interaction points? 

## Connection validation
* Used to make sure that the connections handed out to the client are valid. Pessimistic approach would be to test it before handing out to clients. Optimistic approach is to test it on-return and then on-idle.
* when: { timer, on borrow, on return }
* how: { test-sql, isValid, custom }
* Should just mark the connection as invalid - and the underlying adapter/factory is responsible for cleanup.

## Abandoned connections
* In order to identify connections that were checked out and never returned
* The error should include the caller stack trace - to identify the application code that is causing the leak.

## Idle connections?
* do we really need this?

## Bad connections
* Any operation on a bad connection may hang. 
* Hence, should NOT try to close/discard connections on the application/caller thread.
* Need separate thread(s) + handlers to close the physical connections. e.g. abort/cancel etc.

## Max-use limits
* Some drivers may leak memory over time.
* Hence, we should close the physical connections even if they are still valid
* The usage limits could be in terms of number of checkouts, or time since creation.

## Lock/suspend and unlock/resume
* Manual overrides - can be called locally or across the cluster.
* Probably best to route the automated calls (auto-failover via error detection) through the same call path as the manually triggered calls.

## Connection storms
* When you have a large application cluster, starting all of the instances can lead to a connection storm on the database.
* A more likely scenario is the pool.resume/refresh call across a cluster.
* Can support warm-up duration, or create-delay (can also be applied if config changed dynamically)
* Oracle UCP supports async application of configuration changes - eventually consistent..
* Creation failures should mark database as "potentially-down"? retry threshold? retry delay?
* Connection creation timeout

## Connection modifiers
* Applications may wish to initialize the connections a certain way - e.g. to setup default auto-commit, transaction isolation etc.
* These can be hooked into create or borrow event handlers 

## Trace sql statements
* Applications may wish to log the SQL along with the time taken.
* Default behavior should just log the SQL - without the parameter/return values. 
* Can support additional config for logging parameters and return-value (summary).
* Hooked into? before/after-execute? around-execute? The base pool cannot expose these events - who does? JdbcPool?

## Slow running queries?
* fixed (configured) threshold? 
* infer "normal" values? average +/- (2 x std-devn)
* Hooked into?

## Application attachments?
* support attaching arbitrary application object to the connection?

## Underlying connection
* via standard unwrap() method
* any benefit in having a proxy that implements all the interfaces from the base object?

## read-write separation
* just define different pools and use the right one
* will need composite-pool that supports load-balance (simple round-robin? extensible?)

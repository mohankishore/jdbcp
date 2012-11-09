jdbcp
=====

Open, pluggable, extensible, cluster-aware, enterprise ready database connection pool

Clean room implementation that parallels the commons-pool and commons-dbcp libraries, but makes it lighter-weight, cluster-aware and failover-ready. There will be a strong focus on keeping the core clean and small, with pluggable extension points to support the advanced/optional features.

The following sections list out some interesting "add-on" features and how they can be made pluggable.

## Extensible design
event based e.g. create, borrow, error, return, close
bone-cp allows: create-fail, before-execute, execute-timeout, after-execute, database-potentially-down
allow plugins to fire their own events?

## Failure detection
timer based database health check
error monitoring

## Failover configuration

## Failover mehanism
local JVM
cluster-wide quorum based synchronized
automated failback?
even if there is no failback, should probably keep testing the database to detect when its back up?
Or, keep checking if it is "suspended", but stop checking if it is "stopped"?
fixed rate of checking, or gradually slow down the frequency with which we are checking?

## Clustering
with out of box jgroups and zookeeper support

## Connection validation
when: { timer, on borrow, on return }
how: { test-sql, isValid, custom }

## Abandoned connections
checked out and never returned
with stack trace

## Idle connections?
do we really need this?

## Bad connections
need special threads + handlers to close the physical connections. e.g. abort/cancel etc.

## Max-use limits
close physical connections even if they are still valid
to address memory leaks in drivers

## Lock/suspend and unlock/resume
manual overrides
probably best to route the automated ones also through the same call path

## Connection storms
especially when unlocking/resuming a pool
support warm-up duration, or create-delay (can also be applied if config changed dynamically)
oracle UCP supports async application of configuration changes - eventually consistent..
creation failures should mark database as "potentially-down"? retry threshold? retry delay?
connection creation timeout

## Connection modifiers
can be hooked into create or borrow event handlers - to setup default auto-commit, transaction isolation etc.

## Trace sql statements
with additional config for parameters

## Slow running queries?
fixed threshold?

## Application attachments?
support attaching arbitrary application object to the connection?

## Underlying connection
via standard unwrap() method
any benefit in having a proxy that implements all the interfaces from the base object?

## read-write separation
just define different pools and use the right one
will need composite-pool that supports load-balance (simple round-robin? extensible?)

jdbcp
=====

Open, pluggable, extensible, cluster-aware, enterprise ready database connection pool

Clean room implementation that parallels the commons-pool and commons-dbcp libraries, but makes it lighter-weight, cluster-aware and failover-ready. There will be a strong focus on keeping the core clean and small, with pluggable extension points to support the advanced/optional features.

The following sections list out some interesting "add-on" features and how they can be made pluggable.

## Extensible design
event based e.g. create, borrow, error, return, close

## Failure detection
timer based database health check
error monitoring

## Failover urls

## clustering
with out of box jgroups and zookeeper support

## synchronized failover
quorum based

## connection validation
timer
on borrow
on return

## abandoned connections
with stack trace

## max-use limits
to address memory leaks in drivers

## lock/suspend and unlock/resume a pool

## prevent connection storms
especially when unlocking/resuming a pool

## trace sql statements
with additional config for parameters

## track slow running queries?

## support attaching arbitrary application object to the connection?

## expose underlying connection
via standard unwrap() method
any benefit in having a proxy that implements all the interfaces from the base object?

## read-write separation
just define different pools and use the right one
will need composite-pool that supports load-balance (simple round-robin? extensible?)

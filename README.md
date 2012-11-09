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

## Clustering
with out of box jgroups and zookeeper support

## Synchronized failover
quorum based

## Connection validation
timer
on borrow
on return

## Abandoned connections
checked out and never returned
with stack trace

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

## Trace sql statements
with additional config for parameters

## Slow running queries?

## Application attachments?
support attaching arbitrary application object to the connection?

## Underlying connection
via standard unwrap() method
any benefit in having a proxy that implements all the interfaces from the base object?

## read-write separation
just define different pools and use the right one
will need composite-pool that supports load-balance (simple round-robin? extensible?)

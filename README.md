jdbcp
=====

Open, pluggable, extensible, cluster-aware, enterprise ready database connection pool

Clean room implementation that parallels the commons-pool and commons-dbcp libraries, but makes it lighter-weight, cluster-aware and failover-ready.

Enterprise features:
* failover urls
* pluggable clustering - with out of box jgroups and zookeeper support
* quorum based synchronized failover
* connection validation
* abandoned connections - with stack trace
* max-use limits - to address memory leaks in drivers
* lock/suspend and unlock/resume a pool
* prevent connection storms - especially when unlocking/resuming a pool
* extensible design with event - e.g. create, borrow, error, return, close
* trace sql statements - with additional config for parameters
* track slow running queries?
* support attaching arbitrary application object to the connection?

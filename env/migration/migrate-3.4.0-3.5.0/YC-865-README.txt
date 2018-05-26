REF: YC-865 Review clustered tasks API

OVERVIEW
========

In order to keep frontend up to date modification to entities such as shop, categories, products etc need to be propagated.

Originally this was accomplished by AdminInterceptor which was using the cache-config mapping to determine if proparation is
required and if so was creating a task for executor to evict single cache. The propagation is performed using the current
cluster communication channel (most commonly WS) which was sending a message for every update to the frontend servers.
However due to how hibernate works in many cases the triggering was also done on cascading entities as well causing one
update to product entity for example to turn into hundrds of WS calls.

In order to distill this the processing of WS calls is now done asynchronously via dedicated job which utilises
CacheEvictionQueueProcessor. The processing feeds of the CacheEvictionQueue which is populated using AdminInterceptor and
performs some deduplication thus minimising the number of WS calls. As a side effect however there is now a delay for
cache evict on frontend nodes until the job is run. Currently it is set to run every 30 seconds using
admin.cron.cacheEvictionQueueJob schedule.

To prevent OOM eviction queue has a max size. When overflowing it will disregard evction requests and send a system alert.
The max size is configured using "admin.cache.eviction-queue.max"

PRODUCTION
==========

Ensure schedule is correctly set for the job
Update ehcache and environment properties
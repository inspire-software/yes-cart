REF: YC-711 Replace HibernateSearch

Hibernate search had been completely removed and pure Lucene 6.5.x is now used.

As a result this led to some restructuring of modules and creation of new dedicated
"search" module that encapsulates all "full text" related functionality.

In addition to this we NO LONGER SUPPORT SHARED FULL TEXT INDEX. Each webapp now has
own path to FT index directory and it will use an exclusive lock, so no sharing of
full text index is now possible.

Configuration for full text index path are now moved from config-db-XXXX.properties
to config-fs.properties as thr Full Index functionality is completely independent
of the persistence layer. Please examine options available in config-fs.properties
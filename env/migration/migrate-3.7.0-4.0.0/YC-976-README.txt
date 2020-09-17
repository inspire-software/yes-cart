REF: YC-976 Upgrades 4.x.x

OVERVIEW
========

Notes to describe upgrade release 4.0.0

1. Lucene upgrade from 8.1.1 to 8.6.2
   - Upgrade required upgrade of the ASM to 7.1+, upgraded to 8.0.1 and consequently cglib to 3.3.0
   - Index is not compatible, it has to be manually deleted and full reindex must be done during deployment

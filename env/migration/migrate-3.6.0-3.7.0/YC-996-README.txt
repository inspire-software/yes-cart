REF: YC-996 Refactor PromotionCouponUsageEntity to use code

OVERVIEW:
=========

Removing foreign key from promotion coupon usage allows to decouple order entity from promotion domain model hierarchy.
This is one of the prerequisites for moving towards document based persistence layer.

PRODUCTION:
===========

Requires schema changes and update to existing data.
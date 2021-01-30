REF: YC-1037 Job schedules abstraction layer

OVERVIEW:
=========

Introduction of JobDefinition and Job entities to provide a persistence layer backed configuration for jobs.

CHANGES:
========

New caches
jobDefinitionService-getById
jobService-getById


manager-cronjob.xml:BulkMailProcessorImpl
- admin.mail.delay-between-emails-ms moved to JobDefinition.context.delay-between-emails-ms
- admin.mail.exceptions-threshold moved to JobDefinition.context.exceptions-threshold
- SYSTEM[JOB_SEND_MAIL_PAUSE] proxied to job with definition code "sendMailJob"
- sendMailJob,sendMailJobCronTrigger - becomes job definition, refer to SQL migration script

core-manager-cronjob.xml:BulkCustomerTagProcessorImpl
- SYSTEM[JOB_CUSTOMER_TAG_BATCH_SIZE] moved to JobDefinition.context.process-batch-size
- SYSTEM[JOB_CUSTOMER_TAG_PAUSE] removed
- customerTagJob,customerTagJobCronTrigger - becomes job definition, refer to SQL migration script

core-manager-cronjob.xml:BulkAbandonedShoppingCartProcessorImpl
- SYSTEM[JOB_ABANDONED_CARTS_BATCH_SIZE] moved to JobDefinition.context.process-batch-size
- SYSTEM[CART_ABANDONED_TIMEOUT_SECONDS] moved to JobDefinition.context.abandoned-timeout-seconds and JobDefinition.context.abandoned-timeout-seconds-[SHOPCODE]
- SYSTEM[JOB_ABANDONED_CARTS_PAUSE] removed
- abandonedShoppingCartJob,abandonedShoppingCartJobCronTrigger - becomes job definition, refer to SQL migration script

core-manager-cronjob.xml:BulkEmptyAnonymousShoppingCartProcessorImpl
- SYSTEM[JOB_EMPTY_CARTS_BATCH_SIZE] moved to JobDefinition.context.process-batch-size
- SYSTEM[CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS] moved to JobDefinition.context.empty-timeout-seconds and JobDefinition.context.empty-timeout-seconds-[SHOPCODE]
- SYSTEM[JOB_EMPTY_CARTS_PAUSE] removed
- abandonedShoppingCartJob,abandonedShoppingCartJobCronTrigger - becomes job definition, refer to SQL migration script

core-manager-cronjob.xml:BulkExpiredGuestsProcessorImpl
- SYSTEM[JOB_EXPIRE_GUESTS_BATCH_SIZE] moved to JobDefinition.context.process-batch-size
- SYSTEM[GUESTS_EXPIRY_TIMEOUT_SECONDS] moved to JobDefinition.context.guest-timeout-seconds and JobDefinition.context.guest-timeout-seconds-[SHOPCODE]
- SYSTEM[JOB_EXPIRE_GUESTS_PAUSE] removed
- expiredGuestsJob,expiredGuestsJobCronTrigger - becomes job definition, refer to SQL migration script

core-manager-cronjob.xml:RemoveObsoleteProductProcessorImpl
- SYSTEM[JOB_PROD_OBS_BATCH_SIZE] moved to JobDefinition.context.process-batch-size
- SYSTEM[JOB_PROD_OBS_MAX] moved to JobDefinition.context.obsolete-timeout-days
- SYSTEM[JOB_PROD_OBS_PAUSE] removed
- removeObsoleteProductProcessorJob,removeObsoleteProductProcessorJobCronTrigger - becomes job definition, refer to SQL migration script

core-index-cronjob.xml:ProductsGlobalIndexProcessorImpl
- SYSTEM[JOB_REINDEX_PRODUCT_BATCH_SIZE] moved to JobDefinition.context.reindex-batch-size
- SYSTEM[JOB_GLOBALREINDEX_PAUSE] removed
- productsGlobalIndexProcessorJob,productsGlobalIndexProcessorTrigger - becomes job definition, refer to SQL migration script

core-index-cronjob.xml:ProductInventoryChangedProcessorImpl
- SYSTEM[JOB_PRODINVUP_LR_xxx] moved to Job.checkpoint
- SYSTEM[JOB_REINDEX_PRODUCT_BATCH_SIZE] moved to JobDefinition.context.reindex-batch-size
- SYSTEM[JOB_PRODINVUP_DELTA] moved to JobDefinition.context.inventory-update-delta
- SYSTEM[JOB_PRODINVUP_FULL] moved to JobDefinition.context.inventory-full-threshold
- SYSTEM[JOB_PRODINVUP_DELTA_S] moved to JobDefinition.context.inventory-delta-seconds
- SYSTEM[JOB_PRODINVUP_PAUSE] removed
- productsGlobalIndexProcessorJob,productsGlobalIndexProcessorTrigger - becomes job definition, refer to SQL migration script

core-export-cronjob.xml:OrderAutoExportProcessorImpl
- SYSTEM[JOB_ORDER_AUTO_EXPORT_PAUSE] removed
- orderAutoExportProcessorJob,orderAutoExportProcessorJobTrigger - becomes job definition, refer to SQL migration script

core-orderstate-cronjob.xml:BulkAwaitingInventoryDeliveriesProcessorImpl
- SYSTEM[JOB_DELWAITINV_LR] removed
- SYSTEM[JOB_DELIVERY_WAIT_INVENTORY_PAUSE] removed
- preorderJob,preOrderJobCronTrigger - becomes job definition, refer to SQL migration script

core-orderstate-cronjob.xml:OrderDeliveryInfoUpdateProcessorImpl
- SYSTEM[JOB_DELIVERY_INFO_UPDATE_PAUSE] removed
- deliveryInfoUpdateJob,deliveryInfoUpdateJobCronTrigger - becomes job definition, refer to SQL migration script

manager-cronjob.xml:LocalFileShareImportListenerImpl
- SYSTEM[JOB_LOCAL_FILE_IMPORT_PAUSE] removed
- SYSTEM[JOB_LOCAL_FILE_IMPORT_FS_ROOT] moved to JobDefinition.context.file-import-root
- SYSTEM[JOB_LOCAL_FILE_IMPORT_FS_ROOT]/config.properties is now part of JobDefinition.context with config properties to be prefixed with shop.code
- autoImportJob,autoImportJobCronTrigger - becomes job definition, refer to SQL migration script

manager-cronjob.xml:LocalFileShareImageVaultProcessorImpl
- SYSTEM[JOB_LOCAL_IMAGEVAULT_SCAN_PAUSE] removed
- config.properties moved to JobDefinition.context
- imageVaultProcessorJob,imageVaultProcessorJobTrigger - becomes job definition, refer to SQL migration script

manager-cronjob.xml:LocalFileShareProductImageVaultCleanupProcessorImpl
- SYSTEM[JOB_LOCAL_PRODIMAGEVAULT_CLEAN_MODE] moved to JobDefinition.context.clean-mode
- SYSTEM[JOB_LOCAL_PRODIMAGECLEAN_SCAN_PAUSE] removed
- productImageVaultCleanupProcessorJob,productImageVaultCleanupProcessorJobCronTrigger - becomes job definition, refer to SQL migration script

manager-cronjob.xml:CacheEvictionQueueProcessorImpl
- SYSTEM[JOB_CACHE_EVICT_PAUSE] removed
- cacheEvictionQueueJob,cacheEvictionQueueJobCronTrigger - becomes job definition, refer to SQL migration script

Special:
When server starts TJOB entries will be auto-created. The TJOB.PAUSED value will be set according to
TJOBDEFINITION.DEFAULT_PAUSED. Before upgrading check which jobs you had paused previously:
e.g. `select CODE from TSYSTEMATTRVALUE where CODE like '%PAUSE%' and VAL = true;`
When server starts up and TJOB are auto-created - update the pause flag
e.g. `update TJOB set PAUSED = 0 where JOB_DEFINITION_CODE in ('xxx', 'xxx');` where xxx are codes of job definitions
for which to unset the pause flag. After the update clear cache.

NOTE: on SaaS you can simply manage this from the admin


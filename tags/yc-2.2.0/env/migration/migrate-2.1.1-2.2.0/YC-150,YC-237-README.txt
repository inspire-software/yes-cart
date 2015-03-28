REF: YC-150 RemoteImageServiceImpl addImageToRepository path resolution review
REF: YC-237 Image vault resolution

Referred task concerned unification of the image repository and enhancement of directory structure.

Changes that affect existing systems:

1. Image repository has to be available to all nodes in the cluster hence all references to it from
   storefront had been removed. This includes Shop domain object and Backdoor service that previously
   provided partial resolution of the path to repository on the local file system. Therefore the image
   vault now need to be available to all nodes from a central location outside of the webapp context
   of any specific webapp. Recommended approach for single Tomcat installation is directory "imagevault"
   on the same level as "manager" and "yes-shop" webapp contexts. This is specified by system preference
   "SYSTEM_IMAGE_VAULT" and preconfigured to "context://../imagevault". For multi Tomcat installations it
   is recommended to have a shared directory to which each Tomcat instance has access and configure
   "SYSTEM_IMAGE_VAULT" with an absolute path e.g. "file:///var/www/yes-cart/imagevault", which would
   resolve on unix to absolute path "/var/www/yes-cart/imagevault"

2. Product images are now contained within "product" sub directory within imagevault.

3. Category images are now contained within "category" sub directory within imagevault. Category images
   are now resolved in a similar fashion as product images by matching "[anystring]_[CODE]_[SUFFIX]" where
   CODE is either SEO.URI or NAME of the category and SUFFIX is letter numbering starting with 'a'. Similarly
   as with product images directory structure will be created to optimise access to files.

4. Brand images are now contained within "brand" sub directory within imagevault. Brand images are now resolved
   in a similar fashion as product images by matching "[anystring]_[CODE]_[SUFFIX]" where CODE is either NAME of
   the brand and SUFFIX is letter numbering starting with 'a'.


Actions for migration:

1. Existing imagevault directory in storefront has to be copied to required unified imagevault directory (as
   described in point #1 above).
2. "product" sub directory has to be created within "imagevault" and all directory structure relating to product
   images has to be copied as is to this subdirectory
3. "category" sub directory has to be created within "imagevault". All category images directory structure and
   file names has to be reworked. It is recommended to rename all category images (as described in point #3
   above) and re-import them, so that appropriate sub directories are created.
4. "brand" sub directory has to be created within "imagevault". All brand images directory structure and
   file names has to be reworked. It is recommended to rename all brand images (as described in point #4
   above) and re-import them, so that appropriate sub directories are created.

Actions for development:

1. Depending on how you run webapps (standalone Tomcat or from within IDE) you need to create directory for
   imagevault and modify "SYSTEM_IMAGE_VAULT" configuration.

   If you followed the IDE setup guide and run from Intellij then it is recommended to create "imagevault"
   directory in $YC_HOME and then set the SYSTEM_IMAGE_VAULT to "context://../../../../imagevault", which
   should work properly for all applications: "manager", "yes-shop" and "yes-api".

   If you use standalone Tomcat deployment then default initdata.sql should be sufficient. i.e. SYSTEM_IMAGE_VAULT
   is "context://../imagevault".
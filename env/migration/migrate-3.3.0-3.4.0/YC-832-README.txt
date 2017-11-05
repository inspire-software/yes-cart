REF: YC-830 / YC-832 Refactor object images API

As part of performance improvement to speed up image resolution a new column INDEXVAL(varchar 255) with index was
added to all AV tables.

After code upgrade all new images and files AVs will automatically populate this field. For old legacy values
schema updates will be needed, which are provided in the schema-changes.sql. Without this all OLD image attributes
will not resolve on the storefront.

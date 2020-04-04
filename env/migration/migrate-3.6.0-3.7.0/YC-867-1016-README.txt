REF: YC-867 Bundle products and Collections
     YC-1016 Configurable products

OVERVIEW:
=========
There is a need for configurable products that could have various component options as well as optional extras.
Some examples:
- TV + optional warranty
- MacBook Pro with different CPU, RAM etc

In order to facilitate this additional ProductOptions construct will be used that will define a link between the
main product and its component parts. Each component part is represented by a separate SKU in terms of domain to
facilitate fully featured component definitions (i.e. descriptions, attributes), control availability when necessary
and specify additional cost incurred (if any).

Therefore components have the following features:
- specified as required or optional in ProductOption.mandatory
- quantity proportional to the main item should be specified (default is 1:1) in ProductOption.quantity
- ability to specify price specifically for the component, facilitated by SkuPrice as component is ProductSku
- ability to specify option per SKU in ProductOption.skuCode (if specified applies only to that SKU within Product)
- ability to control UI rendering: ProductOption.rank for sorting, ProductOption.attributeCode for labels

Open questions:
- component products that cannot be purchased separately need to be skipped in global searches and should be skipped
  in SKU cart commands?

CHANGES:
========

- schema:
  TPRODUCTTYPE removed ENSEMBLE
  TPRODUCT added CONFIGURABLE
  dropped table TENSEMBLEOPT
  added new table TPRODUCTOPT


SEARCH KEYWORDS:
================


PRODUCTION:
===========

Schema update required




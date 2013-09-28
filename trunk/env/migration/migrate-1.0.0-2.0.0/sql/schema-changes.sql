--
-- YC-67 Add LOCALE column on TCUSTOMERORDER
--

alter table TCUSTOMERORDER add column LOCALE varchar(5) default 'en';

--
-- YC-277 Remove direct relationship between order details and product sku
--

-- add columns that allow blanks
alter table TCUSTOMERORDERDET add column CODE varchar(255);
-- alter table TCUSTOMERORDERDET add column PRODUCTNAME varchar(4000);
alter table TCUSTOMERORDERDET add column PRODUCTNAME longtext;
alter table TCUSTOMERORDERDELIVERYDET add column CODE varchar(255);
-- alter table TCUSTOMERORDERDELIVERYDET add column PRODUCTNAME varchar(4000);
alter table TCUSTOMERORDERDELIVERYDET add column PRODUCTNAME longtext;

-- set values from current relationship
update TCUSTOMERORDERDET o set CODE = (select s.CODE from TSKU s where s.SKU_ID = o.SKU_ID);
update TCUSTOMERORDERDELIVERYDET o set CODE = (select s.CODE from TSKU s where s.SKU_ID = o.SKU_ID);
-- This is a non-localised copy - so may need some clever way to do this but this is out of scope for YC
update TCUSTOMERORDERDET o set PRODUCTNAME = (select s.NAME from TSKU s where s.SKU_ID = o.SKU_ID);
update TCUSTOMERORDERDELIVERYDET o set PRODUCTNAME = (select s.NAME from TSKU s where s.SKU_ID = o.SKU_ID);

-- alter column not to allow blanks
-- alter table TCUSTOMERORDERDET alter column CODE not null;
alter table TCUSTOMERORDERDET modify column CODE varchar(255) not null;
-- alter table TCUSTOMERORDERDET alter column PRODUCTNAME not null;
alter table TCUSTOMERORDERDET modify column PRODUCTNAME longtext not null;
-- alter table TCUSTOMERORDERDELIVERYDET alter column CODE not null;
alter table TCUSTOMERORDERDELIVERYDET modify column CODE varchar(255) not null;
-- alter table TCUSTOMERORDERDELIVERYDET alter column PRODUCTNAME not null;
alter table TCUSTOMERORDERDELIVERYDET modify column PRODUCTNAME longtext not null;

-- drop indexes (this may be specific to MySQL, so possibly need to modify these steps)
alter table TCUSTOMERORDERDET drop foreign key FK_ODET_SKU;
alter table TCUSTOMERORDERDET drop index FK_ODET_SKU;
alter table TCUSTOMERORDERDET drop column SKU_ID;
alter table TCUSTOMERORDERDELIVERYDET drop foreign key FK_CODD_SKU;
alter table TCUSTOMERORDERDELIVERYDET drop index FK_CODD_SKU;
alter table TCUSTOMERORDERDELIVERYDET drop column SKU_ID;





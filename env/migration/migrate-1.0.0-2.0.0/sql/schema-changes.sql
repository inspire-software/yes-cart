-- Add LOCALE column on TCUSTOMERORDER
alter table TCUSTOMERORDER add column LOCALE varchar(5) default 'en';
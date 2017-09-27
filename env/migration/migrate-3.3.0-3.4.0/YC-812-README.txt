REF: YC-812 Upgrade org.hibernate

This task involed updating hibernate from version 4.2.21.Final to 5.2.11.Final.

Main points are:
- switching Spring wiring classes from org.springframework.orm.hibernate4 to org.springframework.orm.hibernate5
- small adjustment to cache related classes
- updated deprecated Query and SQLQuery APIs
- TODO: remove Criteria API and this will not longer be supported by Hibernate
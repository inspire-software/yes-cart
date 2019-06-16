REF: YC-944 Review dependencies 3.6.x

OVERVIEW
========

Recurring upgrade of major dependencies.


NOTES:
======

Upgrading to Wicket 8.x:
https://cwiki.apache.org/confluence/display/WICKET/Migration+to+Wicket+8.0

- onSelectionChanged() -> FormComponentUpdatingBehavior.onUpdate()
- Form -> Form<Object>
- AbstractRequestCycleListener (deprecated) -> IRequestCycleListener
- IProvider (deprecated) -> Supplier
- IRequestCycleProvider.get() -> IRequestCycleProvider.apply()
- AbstractReadOnlyModel (deprecated) -> anonymous IModel
- added "asm-util" to pom to prevent pulling v.7.1 which is not compatible with JDK8

REF: YC-542 Improve customer registration/account details field configuration.

Attributes' choiceData of business type CommaSeparatedList and validationFailedMessage are now treated as
localisable data.

Therefore normal CommaSeparatedList: [Value1]-[DisplayName1],[Value2]-[DisplayName2], ... [ValueN]-[DisplayNameN]
is now per locale and hence the format is: [locale1]#~#[CommaSeparatedList1]#~#[locale2]#~#[CommaSeparatedList2] ...

For example an attribute had choice data:
R-Red,Y-Yellow,G-Green

Now it should be:
en#~#R-Red,Y-Yellow,G-Green#~#uk#~#R-Червоний,Y-Жовтий,G-Зелений ...
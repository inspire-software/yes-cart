**Build cheatsheet**

| Build Type                      | Maven Command                                                                      |
| ------------------------------- | ---------------------------------------------------------------------------------- |
| First DEV build                 | `mvn clean install`                                                                |
| Example build no tests          | `mvn install -DskipTests=true`                                                     |
| First IDEA/DEV build            | `mvn clean install -PdevIntellijIDEA,derby,paymentBase,embededLucene`              |
| IDEA/DEV build (all payments)   | `mvn install -PdevIntellijIDEA,derby,paymentAll,embededLucene`                     |

**Tips**

* First build must be `clean install` as this ensure that npm dependencies in jam-jsclient modules are correctly installed
* If at least one profile (`-Pxxx`) is specified then defaults are disabled and you have to name them explicitly (e.g. in "First IDEA/DEV" build all profiles are listed explicitly because we want "devIntellijIDEA")
* Refer to documentation site for full details of the available **maven profiles specific to YC version you are using**

**Useful maven commands**

`mvn dependency:tree` print dependencies
`mvn help:all-profiles` print all available profiles
`mvn help:active-profiles` print active profiles
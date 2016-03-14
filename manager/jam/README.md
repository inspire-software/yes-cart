# Building JAM

## Product to install

 - Download and install node.js . ATM tested with 4.x version. https://nodejs.org/en/download/
 - Install npm 3 with command       
```sh
$ npm install -g npm@3
```
## Well know problems

 - Under Windows platform maven exec plugin may not run npm. Solution locate and copy npm.cmd to npm.bat

## Base dir property

 - located in typescript/tools/config/seed.config.ts
 
## Hints

 - indexing of typescript/node_modules and typescript/dist make take some time, so better to mark this folders as excluded
 - mvn -Pmysql,dev tomcat:run-war
 
 
 

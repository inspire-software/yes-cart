# Building JAM

## Product to install

 - Download and install node.js . ATM tested with 4.x version. https://nodejs.org/en/download/
 - Install npm 3 with command       
```sh
$ npm install -g npm@3
```

## Development process

During development run 
```
npm install
npm start
```
in yes-cart/manager/jam/src/main/typescript folder. 
It will install dependencies and after perform lint checks of changed files and incremental typescript translation. 

## Well know problems

 - Under Windows platform maven exec plugin may not run npm. Is this case you may get  "CreateProcess error=193, %1 is not a valid Win32 application" error message
 Solution locate and copy npm.cmd to npm.bat and delete npm shell script (without extension). 
 
 
## Base dir property

 - located in typescript/tools/config/seed.config.ts
 
## Hints

 - indexing of typescript/node_modules and typescript/dist make take some time, so better to mark this folders as excluded
 - mvn -Pmysql,dev tomcat:run-war
 
 
 

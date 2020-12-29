# Building JAM

## Product to install

 - Download and install node.js **12.x.x+** https://nodejs.org/en/download/
 - Install npm **6.14.x+**
 - Install Angular cli **11.x.x** https://angular.io/cli

## Before first build

Perform npm install to download all dependencies.

```
manager/jam-jsclient/src/main/typescript$ npm install
```

Note that in v.4.0.0+ jsclient build is not part of the mvn build process. Full mvn build uses pre-built version of 
Mission Control (JAM) SPA which is defined by the **ts.target.home** (default is dist/admin/*).

## Development process

Mission Control (JAM) server is CORS enabled in dev mode for **devIntellijIDEA** profile, use **admincors** profile to
enable it in conjunction with other profiles. This allows to run development server on your local to provide the API.

Client development now can be fully managed by npm

```
manager/jam-jsclient/src/main/typescript$ npm start
```

start script by default starts `ng serve` in development mode which would connect to the API at 
`http://localhost:8082/cp/`, which is default deployment setup for  **devIntellijIDEA** profile.

If you need to connect to any other API server, you need to make sure it was CORS enabled build and
addjust the path in `manager/jam-jsclient/src/main/typescript/src/environments/environment.ts`

## Pre-build

After changes to the client are completed it needs to be re-built, which updates the dist/admin directory.

```
manager/jam-jsclient/src/main/typescript$ npm run buildprod
```

Changed files need to be committed into repo, which serves several purposes:

- There is no need to setup node related development environment if you do not intend to change Mission Controll SPA
- mvn build are much faster as JAM is using static pre-built version of SPA intead of re-building it every time 


## Well know problems

 - Under Windows platform maven exec plugin may not run npm. Is this case you may get  
 "CreateProcess error=193, %1 is not a valid Win32 application" error message
 Solution locate and copy npm.cmd to npm.bat and delete npm shell script (without extension). 
 
 
## Base dir property

 - located in angular.json as per default Angular CLI setup
 
## Updating npm-shrinkwrap.json

 If you need to update dependencies this is the recommended way:

```sh
# backup & remove
cp npm-shrinkwrap.json npm-shrinkwrap.json.bkp
rm npm-shrinkwrap.json
# remove downloaded dependencies that you need to update (e.g. deep-extend)
rm -rf node_modules/deep-extend
# Re-run install
npm install
# redo shrinkwrap
npm shrinkwrap
```

 After re-creating npm-shrinkwrap.json compare it to npm-shrinkwrap.json.bkp to ensure the changes are valid as 
 multiple dependencies may have updated
 
 
## Hints

 - exclude Intellij IDEA indexing of typescript/node_modules and typescript/dist to improve IDE performance
 
 
 

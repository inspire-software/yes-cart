REF: YC-1041 Separate out npm build from the mvn full build

OVERVIEW
========

As a developer I would like the platform builds to be separated into two phases:
1) npm build for Admin SPA and
2) mvn build of the platform,
in order to speed up the development.

Rationale: Admin re-builds are infrequent and in some cases unnecessary (e.g. platform uses do not change Admin).
Having pre-built Admin SPA reduces project setup effort and time re-quired to perform rebuilds of the SPA.

CHANGES:
========

mvn pom.xml files have been adjusted so that full builds assume that a fully pre-built SPA exists in repo.
By default only "prod" version of SPA is committed and used now in all builds.

It is anticipated that for SPA "dev" mode developers will run SPA side by side the server and SPA served by npm will
CORS connect to APIs. Default API location for local is pointing to localohost:8082/cp (see
manager/jam-jsclient/src/main/typescript/tools/env/local.ts) which is the default IDEA dev setup. It is also possible
however to connect to any "development" server by putting fully qualified URL in local.ts e.g.

const DevConfig: EnvConfig = {
  ENV: 'DEV',
  API: 'https://myadmin.mydomain.com/cp/',
  DEBUG_ON: true,
};

To start npm in "local" mode there are convenience scripts jam-jsclient/src/main/typescript/startlocal.*, which
simply trigger: "npm run serve.local"

After local development is completed use make scripts jam-jsclient/make.* to pre-build SPA and commit it to repo.

After this is done, you can perform mvn build of the platform that will use new pre-built version.







REF: YC-842 Upgrade Angular to 4LTS

This is upgrade of Angular2 to Angular4.
Please ensure you fully delete "node_modules" directory and check the nodejs/npm version requirements
before building JAM.

Some issues encountered during upgrade:

- The following are required to run the build on Angular 4.4.6
   "rxjs": "5.4.3",
   "zone.js": "^0.8.4",
   "ts-node": "^3.1.0"

- few adjustments to tasks to allow copying all files from /assets/

- ngx-translate changed considerably, caused several non-obvious issues (failing traceur.js)

- template in now replaced by ng-template

- routing is now inside modules (as opposed to injected to AppModule)

Improvements:

- moved all YC dependent dependencies to project.config.ts

- added configuration for supported languages in Config


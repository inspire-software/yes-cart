import { join } from 'path';

import { SeedConfig } from './seed.config';
import { ExtendPackages } from './seed.config.interfaces';

/**
 * This class extends the basic seed configuration, allowing for project specific overrides. A few examples can be found
 * below.
 */
export class ProjectConfig extends SeedConfig {

  PROJECT_TASKS_DIR = join(process.cwd(), this.TOOLS_DIR, 'tasks', 'project');

  constructor() {
    super();
    this.APP_TITLE = 'JAM YC - pure eCommerce';
    // this.GOOGLE_ANALYTICS_ID = 'Your site's ID';

    /* Enable typeless compiler runs (faster) between typed compiler runs. */
    // this.TYPED_COMPILE_INTERVAL = 5;

    // Add `NPM` third-party libraries to be injected/bundled.
    this.NPM_DEPENDENCIES = [
      ...this.NPM_DEPENDENCIES,
      // {src: 'jquery/dist/jquery.min.js', inject: 'libs'},
      // {src: 'lodash/lodash.min.js', inject: 'libs'},
    ];

    // Add `local` third-party libraries to be injected/bundled.
    this.APP_ASSETS = [
      { src: `${this.CSS_SRC}/bootstrap.min.css`, inject: true, vendor: false },
      { src: `${this.CSS_SRC}/font-awesome.min.css`, inject: true, vendor: false },
      { src: `${this.CSS_SRC}/yc-main.css`, inject: true, vendor: false },
      // {src: `${this.APP_SRC}/your-path-to-lib/libs/jquery-ui.js`, inject: true, vendor: false}
      // {src: `${this.CSS_SRC}/path-to-lib/test-lib.css`, inject: true, vendor: false},
    ];

    // Add packages (e.g. ng2-translate)
    let additionalPackagesDev: ExtendPackages[] = [
      // needed to load ngx-translate resources
      {
        name: '@angular/common/http',
        path: 'node_modules/@angular/common/bundles/common-http.umd.js',
        packageMeta: { defaultExtension: 'js' }
      },
      // needed by @angular/common/http
      {
        name: 'tslib',
        path: 'node_modules/tslib/tslib.js'
      },
      // ngx-translate
      {
        name: '@ngx-translate/core',
        path: 'node_modules/@ngx-translate/core/bundles/core.umd.js',
        packageMeta: { defaultExtension: 'js' }
      },
      {
        name: '@ngx-translate/http-loader',
        path: 'node_modules/@ngx-translate/http-loader/bundles/http-loader.umd.js',
        packageMeta: { defaultExtension: 'js' }
      },
      // ngx-bootstrap
      {
        name: 'ngx-bootstrap',
        path: 'node_modules/ngx-bootstrap/bundles/ngx-bootstrap.umd.js',
        packageMeta: { defaultExtension: 'js' }
      }
    ];

    this.addPackagesBundlesDev(additionalPackagesDev);


    let additionalPackagesProdCfg: string[] = [
      join('node_modules', '@ngx-translate', '*', 'package.json'),
      join('node_modules', 'ngx-translate', '*', 'package.json')
    ];

    let additionalPackagesProd: ExtendPackages[] = [
      // needed to load ngx-translate resources
      {
        name: '@angular/common/http',
        path: 'node_modules/@angular/common/bundles/common-http.umd.js',
        packageMeta: { main: 'bundles/common-http.umd.js', defaultExtension: 'js' }
      },
      // ngx-translate
      {
        name: '@ngx-translate/core',
        packageMeta: { main: 'bundles/core.umd.js', defaultExtension: 'js' }
      },
      {
        name: '@ngx-translate/http-loader',
        packageMeta: { main: 'bundles/http-loader.umd.js', defaultExtension: 'js' }
      },
      // ngx-bootstrap
      {
        name: 'ngx-bootstrap',
        packageMeta: { main: 'bundles/ngx-bootstrap.umd.js', defaultExtension: 'js' }
      }
    ];

    this.addPackagesBundlesProd(additionalPackagesProd, additionalPackagesProdCfg);


    /* Add proxy middleware */
    // this.PROXY_MIDDLEWARE = [
    //   require('http-proxy-middleware')({ ws: false, target: 'http://localhost:3003' })
    // ];

    /* Add to or override NPM module configurations: */
    // this.PLUGIN_CONFIGS['browser-sync'] = { ghostMode: false };
  }


  addPackagesBundlesDev(packs: ExtendPackages[]) {

    packs.forEach((pack: ExtendPackages) => {
      if (pack.path) {
        this.SYSTEM_CONFIG_DEV.paths[pack.name] = pack.path;
      }

      if (pack.packageMeta) {
        this.SYSTEM_CONFIG_DEV.packages[pack.name] = pack.packageMeta;
      }
    });

  }


  addPackagesBundlesProd(packs: ExtendPackages[], packageConfigPaths: string[]) {

    packs.forEach((pack: ExtendPackages) => {
      if (pack.path) {
        this.SYSTEM_BUILDER_CONFIG.paths[pack.name] = pack.path;
      }

      if (pack.packageMeta) {
        this.SYSTEM_BUILDER_CONFIG.packages[pack.name] = pack.packageMeta;
      }
    });

    packageConfigPaths.forEach((config: string) => {

      this.SYSTEM_BUILDER_CONFIG.packageConfigPaths.push(config);

    });

  }


}

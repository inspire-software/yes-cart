import { Component, Inject } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';
import { HTTP_PROVIDERS } from '@angular/http';

import { Config, SidebarComponent, ShopEventBus } from './shared/index';

/**
 * This class represents the main application component. Within the @Routes annotation is the configuration of the
 * applications routes, configuring the paths for the lazy loaded components (HomeComponent, AboutComponent).
 */
@Component({
  moduleId: module.id,
  selector: 'yc-app',
  viewProviders: [HTTP_PROVIDERS],
  templateUrl: 'app.component.html',
  directives: [ROUTER_DIRECTIVES, SidebarComponent],
  providers:  [ShopEventBus]
})
export class AppComponent {

  constructor(@Inject(ShopEventBus)  _shopEventBus:ShopEventBus) {
    console.log('Environment config', Config);
    ShopEventBus.init(_shopEventBus);
  }

}

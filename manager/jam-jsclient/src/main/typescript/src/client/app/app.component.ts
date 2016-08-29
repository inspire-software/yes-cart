import { Component, Inject, OnDestroy } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';
import { HTTP_PROVIDERS } from '@angular/http';

import { Config, SidebarComponent, ShopEventBus, ErrorEventBus, I18nEventBus, ValidationService } from './shared/index';
import { YcValidators } from './shared/validation/validators';
import { ErrorModalComponent } from './shared/error/index';
import { LicenseModalComponent } from './shared/license/index';

import { TranslateService, TranslatePipe } from 'ng2-translate/ng2-translate';


/**
 * This class represents the main application component. Within the @Routes annotation is the configuration of the
 * applications routes, configuring the paths for the lazy loaded components (HomeComponent, AboutComponent).
 */
@Component({
  moduleId: module.id,
  selector: 'yc-app',
  viewProviders: [HTTP_PROVIDERS],
  templateUrl: 'app.component.html',
  directives: [ROUTER_DIRECTIVES, SidebarComponent, ErrorModalComponent, LicenseModalComponent],
  pipes: [TranslatePipe]
})
export class AppComponent implements OnDestroy {

  private langSub:any;

  constructor(@Inject(ShopEventBus)      _shopEventBus:ShopEventBus,
              @Inject(ErrorEventBus)     _errorEventBus:ErrorEventBus,
              @Inject(I18nEventBus)      _i18nEventBus:I18nEventBus,
              @Inject(ValidationService) _validationService:ValidationService,
              translate: TranslateService) {

    console.log('AppComponent environment config', Config);
    ErrorEventBus.init(_errorEventBus);
    ShopEventBus.init(_shopEventBus);
    I18nEventBus.init(_i18nEventBus);
    YcValidators.init(_validationService);

    var userLang = navigator.language.split('-')[0]; // use navigator lang if available
    userLang = /(uk|ru|en|de)/gi.test(userLang) ? userLang : 'en';     // TODO: move languages to config
    console.log('AppComponent language', userLang);
    translate.setDefaultLang('en');

    this.langSub = I18nEventBus.getI18nEventBus().i18nUpdated$.subscribe(lang => {
      translate.use(lang);
    });
    I18nEventBus.getI18nEventBus().emit(userLang);

  }


  ngOnDestroy() {
    console.debug('AppComponent ngOnDestroy');
    if (this.langSub) {
      this.langSub.unsubscribe();
    }
  }


}

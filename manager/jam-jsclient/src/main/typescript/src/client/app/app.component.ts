import { Component, OnDestroy, Inject } from '@angular/core';
import { Config } from './shared/index';
import './operators';

import { ShopEventBus, ErrorEventBus, I18nEventBus, WindowMessageEventBus, UserEventBus, ValidationService, ManagementService } from './shared/services/index';
import { YcValidators } from './shared/validation/validators';
import { CookieUtil } from './shared/cookies/index';
import { LogUtil } from './shared/log/index';

import { TranslateService } from 'ng2-translate/bundles/index';


/**
 * This class represents the main application component. Within the @Routes annotation is the configuration of the
 * applications routes, configuring the paths for the lazy loaded components (HomeComponent, AboutComponent).
 */
@Component({
  moduleId: module.id,
  selector: 'yc-app',
  templateUrl: 'app.component.html',
})

export class AppComponent implements OnDestroy {

  private langSub:any;

  constructor(@Inject(ShopEventBus)          _shopEventBus:ShopEventBus,
              @Inject(ErrorEventBus)         _errorEventBus:ErrorEventBus,
              @Inject(I18nEventBus)          _i18nEventBus:I18nEventBus,
              @Inject(WindowMessageEventBus) _windowMessageEventBus:WindowMessageEventBus,
              @Inject(UserEventBus)          _userEventBus:UserEventBus,
              @Inject(ValidationService)     _validationService:ValidationService,
              @Inject(ManagementService)     _managementService:ManagementService,
              translate: TranslateService) {
    LogUtil.debug('Environment config', Config);

    ErrorEventBus.init(_errorEventBus);
    ShopEventBus.init(_shopEventBus);
    I18nEventBus.init(_i18nEventBus);
    WindowMessageEventBus.init(_windowMessageEventBus);
    UserEventBus.init(_userEventBus);
    YcValidators.init(_validationService);

    var userLang = navigator.language.split('-')[0]; // use navigator lang if available
    userLang = /(uk|ru|en|de)/gi.test(userLang) ? userLang : 'en';     // TODO: move languages to config
    LogUtil.debug('AppComponent language', userLang);
    translate.setDefaultLang(userLang);

    this.langSub = I18nEventBus.getI18nEventBus().i18nUpdated$.subscribe(lang => {
      if (translate.currentLang != lang) {
        translate.use(lang);
      }
    });

    let lang = CookieUtil.readCookie('YCJAM_UI_LANG', userLang);
    translate.use(lang);
    I18nEventBus.getI18nEventBus().emit(lang);

    this.loadUiPreferences();

    var _sub:any = _managementService.getMyUI().subscribe( myui => {
      LogUtil.debug('Loading ui', myui);
      _sub.unsubscribe();

      var _sub2:any = _managementService.getMyself().subscribe( myself => {
        LogUtil.debug('Loading user', myself);
        UserEventBus.getUserEventBus().emit(myself);
        _sub2.unsubscribe();
      });

    });

  }

  loadUiPreferences() {

    LogUtil.debug('Load UI configurations');

    this.configureIntPreference('UI_INPUT_DELAY');
    this.configureIntPreference('UI_BULKSERVICE_DELAY');
    this.configureIntPreference('UI_FILTER_CAP');
    this.configureIntPreference('UI_FILTER_NO_CAP');
    this.configureIntPreference('UI_TABLE_PAGE_SIZE');
    this.configureIntPreference('UI_TABLE_PAGE_NUMS');

  }

  configureIntPreference(configName:string):void {
    let cfg:any = Config;
    let cookieName = 'YCJAM_' + configName;
    let defaultValue = ''+cfg[configName];
    let value = CookieUtil.readCookie(cookieName, defaultValue);
    if (defaultValue !== value) {
      cfg[configName] = parseInt(value);
    }
    LogUtil.debug('Load UI configuration', configName, value, defaultValue);
  }


  ngOnDestroy() {
    LogUtil.debug('AppComponent ngOnDestroy');
    if (this.langSub) {
      this.langSub.unsubscribe();
    }
  }


}

import { Component, OnDestroy, Inject } from '@angular/core';
import { Config } from './shared/config/env.config';
import './operators';

import { ShopEventBus, ErrorEventBus, I18nEventBus, WindowMessageEventBus, UserEventBus, ValidationService, ManagementService, ShopService } from './shared/services/index';
import { YcValidators } from './shared/validation/validators';
import { CookieUtil } from './shared/cookies/index';
import { LogUtil } from './shared/log/index';

import { TranslateService } from '@ngx-translate/core';


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

  private showNav:boolean = false;

  constructor(@Inject(ShopEventBus)          _shopEventBus:ShopEventBus,
              @Inject(ErrorEventBus)         _errorEventBus:ErrorEventBus,
              @Inject(I18nEventBus)          _i18nEventBus:I18nEventBus,
              @Inject(WindowMessageEventBus) _windowMessageEventBus:WindowMessageEventBus,
              @Inject(UserEventBus)          _userEventBus:UserEventBus,
              @Inject(ValidationService)     _validationService:ValidationService,
              @Inject(ManagementService)     _managementService:ManagementService,
              @Inject(ShopService)           _shopService:ShopService,
              @Inject(TranslateService)      translate: TranslateService) {
    LogUtil.debug('Environment config', Config);

    ErrorEventBus.init(_errorEventBus);
    ShopEventBus.init(_shopEventBus);
    I18nEventBus.init(_i18nEventBus);
    WindowMessageEventBus.init(_windowMessageEventBus);
    UserEventBus.init(_userEventBus);
    YcValidators.init(_validationService);

    let cookieLang = CookieUtil.readCookie('YCJAM_UI_LANG', '-');
    let lang = Config.DEFAULT_LANG;
    if (cookieLang != '-' && (new RegExp(Config.SUPPORTED_LANGS, 'gi')).test(cookieLang)) {
      LogUtil.debug('AppComponent language found supported lang cookie', cookieLang);
      lang = cookieLang;
    } else {
      let userLang = navigator.language.split('-')[0]; // use navigator lang if available
      if ((new RegExp(Config.SUPPORTED_LANGS, 'gi')).test(userLang)) {
        LogUtil.debug('AppComponent language found supported navigator lang', userLang);
        lang = userLang;
      }
    }
    LogUtil.debug('AppComponent language', lang);
    translate.setDefaultLang(lang);

    this.langSub = I18nEventBus.getI18nEventBus().i18nUpdated$.subscribe(lang => {
      LogUtil.debug('AppComponent language change', lang);
      if (lang != null) {
        translate.use(lang);
      }
    });

    translate.use(lang);
    I18nEventBus.getI18nEventBus().emit(lang);

    this.loadUiPreferences();

    let _sub:any = _managementService.getMyUI().subscribe( myui => {
      LogUtil.debug('Loading ui', myui);
      _sub.unsubscribe();

      let _sub2:any = _managementService.getMyself().subscribe( myself => {
        LogUtil.debug('Loading user', myself);
        UserEventBus.getUserEventBus().emit(myself);
        _sub2.unsubscribe();
      });

      let _sub3:any = _shopService.getAllShops().subscribe( allshops => {
        LogUtil.debug('Loading user shops', allshops);
        ShopEventBus.getShopEventBus().emitAll(allshops);
        _sub3.unsubscribe();
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
    this.configureStringPreference('UI_ORDER_TOTALS_GROSS');

  }


  configureStringPreference(configName:string):void {
    let cfg:any = Config;
    let cookieName = 'YCJAM_' + configName;
    let defaultValue = ''+cfg[configName];
    let value = CookieUtil.readCookie(cookieName, defaultValue);
    if (defaultValue !== value) {
      cfg[configName] = value;
    }
    LogUtil.debug('Load UI configuration', configName, value, defaultValue);
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

  changeNav() {
    this.showNav = !this.showNav;
  }

  menuExpandRequired(event:boolean) {
    let menuIsOpen = this.showNav;
    let itemIsNowOpen = event;
    if (!this.showNav) {
      this.showNav = itemIsNowOpen;
    } else {
      if (!itemIsNowOpen) {
        this.showNav = false;
      }
    }
    LogUtil.debug('AppComponent menuExpandRequired', itemIsNowOpen, menuIsOpen, this.showNav);
  }


}

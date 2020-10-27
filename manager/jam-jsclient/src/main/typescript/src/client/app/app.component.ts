import { Component, OnDestroy, Inject, HostListener } from '@angular/core';
import { Config } from './shared/config/env.config';
import './operators';

import { ShopEventBus, ErrorEventBus, I18nEventBus, WindowMessageEventBus, UserEventBus, CommandEventBus, ValidationService } from './shared/services/index';
import { CustomValidators } from './shared/validation/validators';
import { CookieUtil } from './shared/cookies/index';
import { Futures, Future } from './shared/event/index';
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
  private userSub:any;

  private showNav:boolean = false;

  private userActivity:Future;
  private userActivityCheckBufferMs:number = Config.AUTH_USERCHECK_BUFFER;

  constructor(@Inject(ShopEventBus)          _shopEventBus:ShopEventBus,
              @Inject(ErrorEventBus)         _errorEventBus:ErrorEventBus,
              @Inject(I18nEventBus)          _i18nEventBus:I18nEventBus,
              @Inject(WindowMessageEventBus) _windowMessageEventBus:WindowMessageEventBus,
              @Inject(UserEventBus)          _userEventBus:UserEventBus,
              @Inject(CommandEventBus)       _commandEventBus:CommandEventBus,
              @Inject(ValidationService)     _validationService:ValidationService,
              @Inject(TranslateService)      translate: TranslateService) {

    LogUtil.debug('Environment config', Config);

    ErrorEventBus.init(_errorEventBus);
    CommandEventBus.init(_commandEventBus);
    ShopEventBus.init(_shopEventBus);
    I18nEventBus.init(_i18nEventBus);
    WindowMessageEventBus.init(_windowMessageEventBus);
    UserEventBus.init(_userEventBus);
    CustomValidators.init(_validationService);

    let cookieLang = CookieUtil.readCookie('ADM_UI_LANG', '-');
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

    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      LogUtil.debug('AppComponent user change', user);
      if (user != null) {
        if (this.userActivity) {
          LogUtil.debug('AppComponent user change - old activity tracker exists, assuming token refresh');
        } else {
          let jwt = UserEventBus.getUserEventBus().currentJWT();
          if (jwt) {
            let sessionTimeoutMs = (jwt.decoded.exp - jwt.decoded.iat) * 1000 - this.userActivityCheckBufferMs;
            LogUtil.debug('AppComponent user change - new user, started activity tracker', user, jwt, sessionTimeoutMs, this.userActivityCheckBufferMs);
            this.userActivity = Futures.perpetual(function () {
              UserEventBus.getUserEventBus().emitActive(false);
            }, sessionTimeoutMs);
            this.userActivity.delay(); // start tracker
          } else {
            LogUtil.error('AppComponent user change - new user, attempted to start activity tracker, but no JWT', user);
            if (this.userActivity) {
              this.userActivity.delay(); // start tracker using previous config as fallback
            }
          }
        }
      } else if (this.userActivity) {
        LogUtil.debug('AppComponent user change - no user, assume log out, removing activity tracker');
        this.userActivity.cancel();
        this.userActivity = null; // Logged out so we do not need tracker
      }
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
    let cookieName = 'ADM_' + configName;
    let defaultValue = ''+cfg[configName];
    let value = CookieUtil.readCookie(cookieName, defaultValue);
    if (defaultValue !== value) {
      cfg[configName] = value;
    }
    LogUtil.debug('Load UI configuration', configName, value, defaultValue);
  }


  configureIntPreference(configName:string):void {
    let cfg:any = Config;
    let cookieName = 'ADM_' + configName;
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
    if (this.userSub) {
      this.userSub.unsubscribe();
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

  @HostListener('window:mousemove') refreshUserState() {
    if (UserEventBus.getUserEventBus().current() != null) {

      UserEventBus.getUserEventBus().emitActive(true);
      if (this.userActivity) {
        this.userActivity.delay();
      }

    } // else if no user no point in tracking
  }

}

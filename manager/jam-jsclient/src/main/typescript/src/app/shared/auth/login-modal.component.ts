/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { UserEventBus, ShopEventBus, CommandEventBus, ManagementService, ShopService } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { Futures, Future } from './../../shared/event/index';
import { LoginVO, JWTAuth } from './../model/index';
import { Config } from './../../../environments/environment';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-login-modal',
  templateUrl: 'login-modal.component.html',
})

export class LoginModalComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild('loginModalDialog')
  private loginModalDialog:ModalComponent;

  public validForSave:boolean = false;

  private _login:LoginVO = { username: null, password: null, organisation: null };

  public delayedChange:Future;

  public loginForm:any;

  private userSub:any;
  private userTokenRefreshBufferMs:number = Config.AUTH_JWT_BUFFER;

  private cmdSub:any;

  public loading:boolean = false;

  public authError:string = null;
  public changePassword:boolean = false;
  public changePasswordSuccess:boolean = false;

  public firstLoad:boolean = true;

  constructor(private _managementService:ManagementService,
              private _shopService:ShopService,
              private _router : Router,
              fb: FormBuilder) {
    LogUtil.debug('LoginModalComponent constructed');

    let that = this;

    this.loginForm = fb.group({
      'username': ['', Validators.required],
      'password': ['', Validators.required],
      'organisation': [''],
      'npassword': [''],
      'cpassword': [''],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.userSub = UserEventBus.getUserEventBus().jwtUpdated$.subscribe((jwt:JWTAuth) => {
      LogUtil.debug('LoginModalComponent jwtUpdated$', jwt);
      if (jwt) {

        if (jwt.status != 200) {

          if (jwt.attemptResume) {
            this.authError = null; // do not show errors on first load
          } else {
            this.authError = jwt.message || 'MODAL_LOGIN_FAILED_AUTH';
          }
          UserEventBus.getUserEventBus().emit(null);

        } else {

          let refreshInMs = (jwt.decoded.exp - jwt.decoded.iat) * 1000 - this.userTokenRefreshBufferMs;
          LogUtil.debug('LoginModalComponent refreshInMs', refreshInMs, this.userTokenRefreshBufferMs);
          Futures.once(function() {
            if (UserEventBus.getUserEventBus().currentJWT() != null) {
              if (UserEventBus.getUserEventBus().currentActive()) {
                LogUtil.debug('LoginModalComponent refreshing token');
                that._managementService.refreshToken().subscribe(jwt => {
                  LogUtil.debug('LoginModalComponent refreshing token', jwt);
                });
              } else {
                LogUtil.debug('LoginModalComponent NOT refreshing token, user not active');
                that.authError = 'AUTH_TOKEN_EXPIRED';
                UserEventBus.getUserEventBus().emit(null);
              }
            } // else user is not available, no refresh possible
          }, refreshInMs).delay();

          this._managementService.getMyUI().subscribe( myui => {
            LogUtil.debug('LoginModalComponent loading ui', myui);

            this._managementService.getMyself().subscribe( myself => {
              LogUtil.debug('LoginModalComponent loading user', myself);
              UserEventBus.getUserEventBus().emit(myself);
            });

            this._shopService.getAllShops().subscribe( allshops => {
              LogUtil.debug('LoginModalComponent loading user shops', allshops);
              ShopEventBus.getShopEventBus().emitAll(allshops);
              if (this._router.isActive('', true) || this._router.isActive('index.html', true)) {
                this._router.navigate(['/dashboard']);
              } // else stay on same page
            });


          });

        }
      }
    });

    this.cmdSub = CommandEventBus.getCommandEventBus().commandUpdated$.subscribe(cmd => {
      LogUtil.debug('LoginModalComponent commandUpdated$', cmd);
      switch (cmd) {
        case 'login':
          this.showLoginModal();
          break;
        case 'logout':
          this._managementService.logoff().subscribe(res => {
            this._router.navigate(['']);
          });
          break;
        case 'changepassword':
          this.showLoginModal(true);
          break;
      }
    });

  }


  formBind():void {
    UiUtil.formBind(this, 'loginForm', 'delayedChange');
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'loginForm');
  }

  formChange():void {
    LogUtil.debug('LoginModalComponent formChange', this.loginForm.valid, this.login);
    this.validForSave = this.loginForm.valid;
  }

  get login(): LoginVO {
    return this._login;
  }

  set login(value: LoginVO) {
    this._login = value;
  }

  ngOnInit() {
    this.formBind();
    LogUtil.debug('LoginModalComponent ngOnInit');
  }


  ngOnDestroy() {
    LogUtil.debug('LoginModalComponent ngOnDestroy');
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
    if (this.cmdSub) {
      this.cmdSub.unsubscribe();
    }
    this.formUnbind();
  }

  showLoginModal(changePassword:boolean = false) {
    LogUtil.debug('LoginModalComponent showLoginModal', changePassword);
    if (this.loginModalDialog) {
      this.loading = false;
      this.changePassword = changePassword;
      let user = UserEventBus.getUserEventBus().current();
      LogUtil.debug('LoginModalComponent showLoginModal', changePassword, user);
      if (user != null) {
        this._login.username = user.manager.email;
      } else {
        this._login.username = '';
      }
      this._login.password = null;
      this.loginModalDialog.show();
      if (this.firstLoad) {
        this.firstLoad = false;
        this._managementService.attemptResume().subscribe((resume:JWTAuth) => {
          LogUtil.debug('LoginModalComponent showLoginModal firstLoad resume', resume);
          if (resume && resume.status == 200) {
            this.loginModalDialog.cancelAction();
          }
        });
      }
    }
  }

  ngAfterViewInit() {
    LogUtil.debug('LoginModalComponent ngAfterViewInit');
    // Here you get a reference to the modal so you can control it programmatically
    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(event => {
      LogUtil.debug('LoginModalComponent login event', event);
      if (event === null) {
        let that = this;
        Futures.once(function() {
          that.showLoginModal(that.authError == 'AUTH_CREDENTAILS_EXPIRED');
        }, 200).delay();
      } else {
        this._login = {
          username : event.manager.email,
          password : null,
          organisation: event.manager.companyName1
        };
        this.authError = null;
      }
    });
  }

  onLoginResult(modalresult: ModalResult) {
    LogUtil.debug('LoginModalComponent onLoginResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.loading = true;
      this.validForSave = false;
      this.changePasswordSuccess = false;
      if (this.changePassword) {
        this._managementService.changePassword(this._login.username, this._login.password, this._login.npassword, this._login.cpassword).subscribe(change => {
          this.loading = false;
          LogUtil.debug('LoginModalComponent onLoginResult change pass', change);

          this._login.password = null;
          this._login.npassword = null;
          this._login.cpassword = null;

          if (change != null) {
            this.authError = null;
            this.changePasswordSuccess = true;
            this.changePassword = false;
            UserEventBus.getUserEventBus().emitJWT(null);
            UserEventBus.getUserEventBus().emit(null);
          } else {
            this.authError = 'AUTH_CREDENTAILS_INVALID';
            this.changePasswordSuccess = false;
            this.showLoginModal(true);
          }

        }, error => {
          LogUtil.debug('LoginModalComponent onLoginResult change pass', error);

          this._login.password = null;
          this._login.npassword = null;
          this._login.cpassword = null;

          this.authError = error;
          this.showLoginModal(true);
        });
      } else {
        this._managementService.login(this._login.username, this._login.password, this._login.organisation).subscribe(jwt => {
          this.loading = false;
          this.authError = null;
          this._login.password = null;
          if (jwt == null || jwt.status != 200) {
            LogUtil.debug('LoginModalComponent onLoginResult auth failed');
          }
        });
      }
    } else if (UserEventBus.getUserEventBus().current() == null) {

      this.showLoginModal();

    }
  }

}

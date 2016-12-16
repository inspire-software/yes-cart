/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
import { ErrorEventBus, Util } from './../services/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-error-modal',
  moduleId: module.id,
  templateUrl: 'error-modal.component.html',
})

export class ErrorModalComponent implements OnInit, OnDestroy, AfterViewInit {

  @ViewChild('errorModalDialog')
  private errorModalDialog:ModalComponent;

  private errorTitleKey:string = '';
  private errorTitleParams:any = {};
  private errorString:string = '';

  private errorSub:any;

  constructor() {
    LogUtil.debug('ErrorModalComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ErrorModalComponent ngOnInit');
  }


  ngOnDestroy() {
    LogUtil.debug('ErrorModalComponent ngOnDestroy');
    if (this.errorSub != null) {
      this.errorSub.unsubscribe();
    }
  }

  showErrorModal() {
    LogUtil.debug('ErrorModalComponent showErrorModal', this.errorString, this.errorTitleKey, this.errorTitleParams);
    if (this.errorModalDialog) {
      this.errorModalDialog.show();
    }
  }

  ngAfterViewInit() {
    LogUtil.debug('ErrorModalComponent ngAfterViewInit');
    // Here you get a reference to the modal so you can control it programmatically
    this.errorSub = ErrorEventBus.getErrorEventBus().errorUpdated$.subscribe(event => {
      LogUtil.debug('ErrorModalComponent error event', event);
      if (event !== 'init') {
        this.setErrorString(event);
        this.showErrorModal();
      }
    });
  }

  protected onErrorResult(modalresult: ModalResult) {
    LogUtil.debug('ErrorModalComponent onErrorResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      window.location.href = '../j_spring_security_logout'; //.reload();
    }
  }

  private setErrorString(event:any) {

    let message = Util.determineErrorMessage(event);
    LogUtil.debug('ErrorModalComponent setErrorString', message);

    let key = 'MODAL_ERROR_MESSAGE';
    let keyParams:any = {};

    if (message.message) {
      if (message.message.indexOf('JSON Parse error') != -1) {
        key = 'MODAL_ERROR_MESSAGE_AUTH200';
      } else if (message.message.indexOf('unique or primary key constraint') != -1) {
        key = 'MODAL_ERROR_MESSAGE_UNIQUE';
      }
      this.errorString = message.message;
    } else {
      this.errorString = 'Server error';
    }

    if (message.code == 403) {
      key = 'MODAL_ERROR_MESSAGE_AUTH403';
    }

    if (message.code == 404) {
      key = 'MODAL_ERROR_MESSAGE_AUTH404';
    }

    if (message.key) {
      key = message.key;
      if (message.params) {
        if (key.indexOf('fatal') != -1 && message.params.length > 0) {
          this.errorString += ' ' + message.params[message.params.length - 1]; // Last is error from server usually
        }
        message.params.forEach((item:string, index:number) => {
          keyParams['p' + index] = item;
        });
      }
    }

    this.errorTitleKey = key;
    this.errorTitleParams = keyParams;

  }

}

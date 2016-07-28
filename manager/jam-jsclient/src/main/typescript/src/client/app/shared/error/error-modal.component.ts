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
import {Component, OnInit, OnDestroy, OnChanges, Input} from '@angular/core';
import {ErrorEventBus, Util} from './../services/index';
import {ModalComponent, ModalResult, ModalAction} from './../modal/index';

@Component({
  selector: 'yc-error-modal',
  moduleId: module.id,
  templateUrl: 'error-modal.component.html',
  directives: [ModalComponent]
})

export class ErrorModalComponent implements OnInit, OnDestroy {

  errorModalDialog:ModalComponent;

  private errorString:string = '';

  private errorSub:any;

  constructor() {
    console.debug('ErrorModalComponent constructed');
  }

  private setErrorString(event:any) {

    let message = Util.determineErrorMessage(event);
    console.debug('ErrorModalComponent setErrorString', message);
    if (message && message.indexOf('JSON Parse error') != -1) {
      this.errorString = 'AUTH';
    } else {
      this.errorString = message;
    }

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ErrorModalComponent ngOnInit');
  }


  /** {@inheritDoc} */
  public ngOnDestroy() {
    console.debug('ErrorModalComponent ngOnDestroy');
    if (this.errorSub != null) {
      this.errorSub.unsubscribe();
    }
  }

  protected showErrorModal() {
    console.debug('ErrorModalComponent showErrorModal', this.errorString);
    if (this.errorModalDialog) {
      this.errorModalDialog.show();
    }
  }

  protected errorModalDialogLoaded(modal: ModalComponent) {
    console.debug('ErrorModalComponent errorModalDialogLoaded');
    // Here you get a reference to the modal so you can control it programmatically
    this.errorModalDialog = modal;
    this.errorSub = ErrorEventBus.getErrorEventBus().errorUpdated$.subscribe(event => {
      if (event != null) {
        this.setErrorString(event);
        this.showErrorModal();
      }
    });
  }

  protected onErrorResult(modalresult: ModalResult) {
    console.debug('ErrorModalComponent onErrorResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      window.location.reload();
    }
  }

}

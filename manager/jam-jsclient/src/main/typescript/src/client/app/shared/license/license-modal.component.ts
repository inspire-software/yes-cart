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
import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ManagementService } from './../services/index';
import { LicenseAgreementVO } from './../model/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-license-modal',
  moduleId: module.id,
  templateUrl: 'license-modal.component.html',
})

export class LicenseModalComponent implements OnInit, AfterViewInit {

  @ViewChild('licenseModalDialog')
    licenseModalDialog:ModalComponent;

  private license:LicenseAgreementVO;

  constructor(private _managementService:ManagementService) {
    LogUtil.debug('LicenseModalComponent constructed');
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('LicenseModalComponent ngOnInit');
  }

  protected showErrorModal() {
    LogUtil.debug('LicenseModalComponent showErrorModal', this.license);
    if (this.licenseModalDialog) {
      this.licenseModalDialog.show();
    }
  }

  public ngAfterViewInit() {
    LogUtil.debug('LicenseModalComponent ngAfterViewInit');
    // Here you get a reference to the modal so you can control it programmatically
    var _sub:any = this._managementService.getMyAgreement().subscribe(license => {
      LogUtil.debug('LicenseModalComponent event', license);
      this.license = license;
      if (!license.agreed) {
        this.showErrorModal();
      }
      _sub.unsubscribe();
    });
  }

  protected onAgreeResult(modalresult: ModalResult) {
    LogUtil.debug('LicenseModalComponent onAgreeResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      var _sub:any = this._managementService.acceptMyAgreement().subscribe(license => {
        LogUtil.debug('LicenseModalComponent event', license);
        this.license = license;
        if (!license.agreed) {
          this.showErrorModal();
        }
        _sub.unsubscribe();
      });
    } else if (!this.license.agreed) {
      window.location.href = '../j_spring_security_logout';
    }
  }

}

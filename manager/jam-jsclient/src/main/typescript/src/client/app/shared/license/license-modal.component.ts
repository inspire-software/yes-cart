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
import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { UserEventBus, ManagementService } from './../services/index';
import { LicenseAgreementVO } from './../model/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-license-modal',
  moduleId: module.id,
  templateUrl: 'license-modal.component.html',
})

export class LicenseModalComponent implements OnInit, OnDestroy {

  @ViewChild('licenseModalDialog')
  licenseModalDialog:ModalComponent;

  private license:LicenseAgreementVO;

  private userSub:any;

  constructor(private _managementService:ManagementService) {
    LogUtil.debug('LicenseModalComponent constructed');
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('LicenseModalComponent ngOnInit');
    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(user => {
      if (user != null) {
        // Here you get a reference to the modal so you can control it programmatically
        let _sub:any = this._managementService.getMyAgreement().subscribe(license => {
          LogUtil.debug('LicenseModalComponent event', license);
          this.license = license;
          if (!license.agreed) {
            this.showLicenseModal();
          }
          _sub.unsubscribe();
        });
      }
    });
  }

  ngOnDestroy() {
    LogUtil.debug('LicenseModalComponent ngOnDestroy');
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }

  protected showLicenseModal() {
    LogUtil.debug('LicenseModalComponent showLicenseModal', this.license);
    if (this.licenseModalDialog) {
      this.licenseModalDialog.show();
    }
  }

  protected onAgreeResult(modalresult: ModalResult) {
    LogUtil.debug('LicenseModalComponent onAgreeResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let _sub:any = this._managementService.acceptMyAgreement().subscribe(license => {
        LogUtil.debug('LicenseModalComponent event', license);
        this.license = license;
        if (!license.agreed) {
          this.showLicenseModal();
        }
        _sub.unsubscribe();
      });
    }
  }

}

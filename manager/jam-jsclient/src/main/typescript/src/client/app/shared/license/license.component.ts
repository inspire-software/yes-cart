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
import { Component, OnInit } from '@angular/core';
import { ManagementService } from './../services/index';
import { LicenseAgreementVO } from './../model/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-license',
  moduleId: module.id,
  templateUrl: 'license.component.html'
})

export class LicenseComponent implements OnInit {


  private license:LicenseAgreementVO;

  constructor(private _managementService:ManagementService) {
    LogUtil.debug('LicenseComponent constructed');
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('LicenseComponent ngOnInit');
    var _sub:any = this._managementService.getMyAgreement().subscribe(license => {
      LogUtil.debug('LicenseComponent event', license);
      this.license = license;
      _sub.unsubscribe();
    });
  }

}

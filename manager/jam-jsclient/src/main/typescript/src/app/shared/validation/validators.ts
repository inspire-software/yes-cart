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

import { Validators } from '@angular/forms';
import { ValidationRequestVO } from './../model/validation.model';
import { ValidationService } from './../services/validation.service';
import { LRUCache } from '../model/internal/cache.model';
import { LogUtil } from './../log/index';


export class CustomValidators {

  static requiredValidDomainName = Validators.compose([Validators.required, CustomValidators.validDomainName]);

  static requiredValidDomainName64 = Validators.compose([CustomValidators.requiredValidDomainName, Validators.maxLength(64)]);

  static validDomainName64 = Validators.compose([CustomValidators.validDomainName, Validators.maxLength(64)]);

  static validCode = Validators.pattern('[A-Za-z0-9\\-_]+');

  static validCode36 = Validators.compose([CustomValidators.validCode, Validators.maxLength(36)]);

  static validCode255 = Validators.compose([CustomValidators.validCode, Validators.maxLength(255)]);

  static requiredValidCode = Validators.compose([Validators.required, CustomValidators.validCode]);

  static requiredValidCode255 = Validators.compose([Validators.required, Validators.maxLength(255), CustomValidators.validCode]);

  static validLogin = Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(255), Validators.pattern('[A-Za-z0-9]+([\\._A-Za-z0-9\\-@]+)')]);

  static validLoginLoose = Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(255)]);

  static validSeoUri = Validators.pattern('[A-Za-z0-9.\\-_]+');

  static validSeoUri255 = Validators.compose([CustomValidators.validSeoUri, Validators.maxLength(255)]);

  static noWhitespace = Validators.pattern('\\S+');

  static noWhitespace255 = Validators.compose([CustomValidators.noWhitespace, Validators.maxLength(255)]);

  static nonBlankTrimmed25 = Validators.compose([Validators.maxLength(25), CustomValidators.nonBlankTrimmed]);

  static nonBlankTrimmed64 = Validators.compose([Validators.maxLength(64), CustomValidators.nonBlankTrimmed]);

  static nonBlankTrimmed128 = Validators.compose([Validators.maxLength(128), CustomValidators.nonBlankTrimmed]);

  static nonBlankTrimmed255 = Validators.compose([Validators.maxLength(255), CustomValidators.nonBlankTrimmed]);

  static requiredNonBlankTrimmed64 = Validators.compose([Validators.maxLength(64), CustomValidators.requiredNonBlankTrimmed]);

  static requiredNonBlankTrimmed128 = Validators.compose([Validators.maxLength(128), CustomValidators.requiredNonBlankTrimmed]);

  static requiredNonBlankTrimmed255 = Validators.compose([Validators.maxLength(255), CustomValidators.requiredNonBlankTrimmed]);

  static validLanguageCode = Validators.pattern('[a-z]{2}');

  static validCountryCode = Validators.pattern('[A-Z]{2}');

  static requiredValidCountryCode = Validators.compose([Validators.required, CustomValidators.validCountryCode]);

  static validCountryIsoCode = Validators.pattern('[0-9]{3}');

  static requiredValidCountryIsoCode = Validators.compose([Validators.required, CustomValidators.validCountryIsoCode]);

  static validRole = Validators.pattern('ROLE_[A-Z_]+');

  static requiredValidRole = Validators.compose([Validators.required, CustomValidators.validRole]);

  static positiveWholeNumber = Validators.pattern('[0-9]+');

  static requiredPositiveWholeNumber = Validators.compose([Validators.required, CustomValidators.positiveWholeNumber]);

  static number = Validators.pattern('\\-?[0-9]+(\\.[0-9]+)?');

  static positiveNumber = Validators.pattern('[0-9]+(\\.[0-9]+)?');

  static requiredPositiveNumber = Validators.compose([Validators.required, CustomValidators.positiveNumber]);

  static nonZeroPositiveNumber = Validators.pattern('[1-9][0-9]*(\\.[0-9]+)?');

  static requiredNonZeroPositiveNumber = Validators.compose([Validators.required, CustomValidators.nonZeroPositiveNumber]);

  static rank = Validators.pattern('\\-?[0-9]+');

  static requiredRank = Validators.compose([Validators.required, CustomValidators.rank]);

  static pk = Validators.pattern('[1-9][0-9]*');

  static requiredPk = Validators.compose([Validators.required, CustomValidators.pk]);

  static validPhone = Validators.pattern('([+]){0,1}([()0-9- ]){5,}');

  static validEmail = Validators.pattern('[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,8})');

  static requiredValidEmail = Validators.compose([Validators.required, CustomValidators.validEmail]);

  static validDate = Validators.pattern('[0-9]{4}\\-([0][1-9]|[1][0-2])\\-([0][1-9]|[1-2][0-9]|[3][0-1])( ([0][0-9]|[1][0-9]|[2][0-3]):[0-5][0-9]:[0-5][0-9])?');

  static requiredValidDate = Validators.compose([Validators.required, CustomValidators.validDate]);

  private static _cache:LRUCache = new LRUCache();

  private static topLevelDomainNames:Array<string> =
    ['aero','asia','biz','cat','com','coop','edu','gov','info','int','jobs','mil','mobi','museum','name','net','org','pro','tel'
      ,'travel','ac','ad','ae','af','ag','ai','al','am','an','ao','aq','ar','as','at','au','aw','ax','az','ba','bb','bd','be','bf'
      ,'bg','bh','bi','bj','bm','bn','bo','br','bs','bt','bv','bw','by','bz','ca','cc','cd','cf','cg','ch','ci','ck','cl','cm','cn'
      ,'co','cr','cu','cv','cx','cy','cz','de','dj','dk','dm','do','dz','ec','ee','eg','er','es','et','eu','fi','fj','fk','fm','fo'
      ,'fr','ga','gb','gd','ge','gf','gg','gh','gi','gl','gm','gn','gp','gq','gr','gs','gt','gu','gw','gy','hk','hm','hn','hr','ht'
      ,'hu','id','ie','il','im','in','io','iq','ir','is','it','je','jm','jo','jp','ke','kg','kh','ki','km','kn','kp','kr','kw','ky'
      ,'kz','la','lb','lc','li','lk','lr','ls','lt','lu','lv','ly','ma','mc','md','me','mg','mh','mk','ml','mm','mn','mo','mp','mq'
      ,'mr','ms','mt','mu','mv','mw','mx','my','mz','na','nc','ne','nf','ng','ni','nl','no','np','nr','nu','nz','om','pa','pe','pf'
      ,'pg','ph','pk','pl','pm','pn','pr','ps','pt','pw','py','qa','re','ro','rs','ru','rw','sa','sb','sc','sd','se','sg','sh','si'
      ,'sj','sk','sl','sm','sn','so','sr','st','su','sv','sy','sz','tc','td','tf','tg','th','tj','tk','tl','tm','tn','to','tp','tr'
      ,'tt','tv','tw','tz','ua','ug','uk_UK','us','uy','uz','va','vc','ve','vg','vi','vn','vu','wf','ws','ye','yt','za','zm','zw'];

  private static validationService:ValidationService;

  public static init(validationService:ValidationService) {
    CustomValidators.validationService = validationService;
  }


  static nonBlankTrimmed(control:any):any {

    let val:string = control.value;
    if (val == null || val.length == 0 || val.trim().length === val.length) {
      return null;
    }
    return {
      'invalidValue': true
    };

  }

  static requiredNonBlankTrimmed(control:any):any {

    let val:string = control.value;
    if (val != null && val.length > 0 && val.trim().length === val.length) {
      return null;
    }
    return {
      'invalidValue': true
    };

  }


  static validRemoteCheck(control:any, request:ValidationRequestVO):any {

    // Attempt cache only on persistent
    let cached = request.subjectId > 0 ? CustomValidators._cache.getValue(request) : null;
    if (cached !== null) {

      LogUtil.debug('CustomValidators validRemoteCheck cached', control, request, cached);
      if (cached.errorCode) {
        return { [cached.errorCode]: true };
      }
      return null;

    } else {

      LogUtil.debug('CustomValidators validRemoteCheck', control, request);
      let service = CustomValidators.validationService;

      service.validate(request).subscribe(
          data => {

            if (request.subjectId > 0) { // cache only persistent
              CustomValidators._cache.putValue(request, data, 5000);
            }

            LogUtil.debug('CustomValidators validRemoteCheck result', data);
            control.setErrors(null);
            if (data.errorCode) {
              control.setErrors({[data.errorCode]: true}, {emitEvent: true});
            }
        });

      return {checking: true};
    }
  }

  static validDomainName(control:any):any {

    let url:string = control.value;
    if (url === 'localhost' || (url.length > 5) && (CustomValidators.validateDomain(url) || CustomValidators.validateIp(url))) {
      return null;
    }
    return {
      'invalidDomainName': true
    };

  }

  private static validateDomain(url:string):boolean {
    let dn:string;
    for (let i=0; i<CustomValidators.topLevelDomainNames.length; i++) {
      dn = '.'+CustomValidators.topLevelDomainNames[i];
      if (url.lastIndexOf(dn) == (url.length - dn.length)) {
        return true;
      }
    }
    return false;
  }

  private static validateIp(url:string):boolean {
    let regex:RegExp = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
    return regex.test(url);
  }

}

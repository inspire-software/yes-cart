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
import { Component } from '@angular/core';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';


/**
 * This class represents the lazy loaded EmptyComponent.
 */
@Component({
  moduleId: module.id,
  selector: 'cw-empty',
  templateUrl: 'empty.component.html',
})
export class EmptyComponent {

  contextPath:string = Config.CONTEXT_PATH;
  yearNow:number = new Date().getFullYear();

  constructor() {
    LogUtil.debug('EmptyComponent constructed');
  }

}

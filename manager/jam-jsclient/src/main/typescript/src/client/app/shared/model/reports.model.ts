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

import { Pair } from './common.model';

export interface DashboardWidgetVO {

  widgetId : string;
  data  : any;

}

export interface ReportParameterVO {

  parameterId : string;
  businesstype : string;
  mandatory : boolean;

}

export interface ReportDescriptorVO {

  reportId : string;
  parameters : ReportParameterVO[];

}

export interface ReportRequestParameterVO {

  parameterId : string;
  options : Pair<string, string>[];
  value : string;
  mandatory : boolean;

}

export interface ReportRequestVO {

  reportId : string;
  lang : string;

  parameters : ReportRequestParameterVO[];

}

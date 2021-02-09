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

import { Pair } from './common.model';

export interface DashboardWidgetInfoVO {

  widgetId : string;
  language : string;
  widgetDescription : string;

}

export interface DashboardWidgetVO extends DashboardWidgetInfoVO {

  data  : any;

}

export interface ReportParameterVO {

  parameterId : string;
  displayNames :  Pair<string, string>[];
  businesstype : string;
  mandatory : boolean;
  editorType : string;
  editorProperty : string;
  displayProperty : string;

}

export interface ReportDescriptorVO {

  reportId : string;
  displayNames :  Pair<string, string>[];
  parameters : ReportParameterVO[];
  reportType : string;
  reportFileExtension : string;
  reportFileMimeType : string;

}

export interface ReportRequestParameterVO {

  parameterId : string;
  displayNames :  Pair<string, string>[];
  businesstype : string;
  mandatory : boolean;
  editorType : string;
  editorProperty : string;
  displayProperty : string;
  options : Pair<string, string>[];
  value : string;
  displayValue : string;

}

export interface ReportRequestVO {

  reportId : string;
  displayNames :  Pair<string, string>[];
  parameters : ReportRequestParameterVO[];
  reportType : string;
  reportFileExtension : string;
  reportFileMimeType : string;
  lang : string;

}

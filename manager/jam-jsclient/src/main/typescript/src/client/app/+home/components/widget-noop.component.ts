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
import { Component, Input } from '@angular/core';
import { DashboardWidgetVO } from '../../shared/model/index';

@Component({
  selector: 'cw-widget-noop',
  template: `    
  <div class="col-lg-3 col-md-4 col-sm-6">
    <div class="panel panel-red">
      <div class="panel-heading widget-body">
        <div class="row">
          <div class="col-xs-12">
            <i class="fa fa-warning"></i> {{ widget.widgetDescription }}
          </div>
        </div>
      </div>
      <div class="panel-footer">
        <div>ERROR ...</div>
        <div class="clearfix"></div>
      </div>
    </div>
  </div>
  `
})
export class WidgetNoopComponent {

  @Input() widget: DashboardWidgetVO;

}

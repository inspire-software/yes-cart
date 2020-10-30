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
  selector: 'cw-widget-alert',
  template: `    
  <div class="col-lg-12 col-md-12 col-sm-12" *ngIf="widget.data.length > 0">
    <div class="panel panel-red">
      <div class="panel-heading">
        <div class="row">
          <div class="col-xs-12">
            <i class="fa fa-warning"></i> {{ 'DASHBOARD_ALERTS' | translate }}
          </div>
        </div>
      </div>
      <div class="panel-footer" style="max-height: 200px;overflow-y: auto; overflow-x: hidden">
        <div *ngFor="let alert of widget.data" title="{{ alert.first }}: {{ alert.second }}">{{ alert.first | translate: { value: alert.second } }}</div>
        <div class="clearfix"></div>
      </div>
    </div>
  </div>
  `
})
export class WidgetAlertComponent {

  @Input() widget: DashboardWidgetVO;

}

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


import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Config } from '../config/env.config';
import { DashboardWidgetVO, ReportDescriptorVO, ReportRequestVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ReportsService {

  private _serviceBaseUrl = Config.API + 'service/reports';  // URL to web api

  /**
   * Construct management service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('ReportsService constructed');
  }

  /**
   * Get current user widgets,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getDashboard() {
    return this.http.get(this._serviceBaseUrl + '/dashboard')
      .map(res => <DashboardWidgetVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Get current user reports,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getReportDescriptors() {
    return this.http.get(this._serviceBaseUrl + '/report/all')
      .map(res => <ReportDescriptorVO[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Refine report options.
   * @param req report request with selections
   * @returns {Observable<R>}
   */
  updateReportRequestValues(req:ReportRequestVO) {

    let body = JSON.stringify(req);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/report/configure', body, options)
        .map(res => <ReportRequestVO> res.json())
        .catch(this.handleError);
  }


  /**
   * Generate report and return file name on server.
   * @param req report request with selections
   * @returns {Observable<R>}
   */
  generateReport(req:ReportRequestVO) {

    let body = JSON.stringify(req);
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this._serviceBaseUrl + '/report/generate', body, options)
        .map(res => res.text())
        .catch(this.handleError);
  }


  private handleError (error:any) {

    LogUtil.error('ReportsService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}

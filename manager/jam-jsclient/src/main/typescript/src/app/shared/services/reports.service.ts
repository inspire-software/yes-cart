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


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Config } from '../../../environments/environment';
import { DashboardWidgetInfoVO, DashboardWidgetVO, ReportDescriptorVO, ReportRequestVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, of, throwError } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ReportsService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct management service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('ReportsService constructed');
  }

  /**
   * Get current user widgets,
   * @returns {Observable<T>}
   */
  getDashboard(lang:string):Observable<DashboardWidgetVO[]> {
    return this.http.get<DashboardWidgetVO[]>(this._serviceBaseUrl + '/reports/dashboard?lang=' + lang, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get current user widgets,
   * @returns {Observable<T>}
   */
  getDashboardWidget(widget:string, lang:string):Observable<DashboardWidgetVO[]> {
    return this.http.get<DashboardWidgetVO[]>(this._serviceBaseUrl + '/reports/dashboard/' + widget + '?lang=' + lang, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get current user widgets,
   * @returns {Observable<T>}
   */
  getAvailableWidgets(lang:string):Observable<DashboardWidgetInfoVO[]> {
    return this.http.get<DashboardWidgetInfoVO[]>(this._serviceBaseUrl + '/reports/dashboard/available?lang=' + lang, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get current user widgets,
   * @returns {Observable<T>}
   */
  updateDashboardWidgets(widgets:DashboardWidgetInfoVO[], lang:string):Observable<boolean> {

    let body = JSON.stringify(widgets);

    return this.http.post(this._serviceBaseUrl + '/reports/dashboard?lang=' + lang, body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));

  }

  /**
   * Get current user reports,
   * @returns {Observable<T>}
   */
  getReportDescriptors():Observable<ReportDescriptorVO[]> {
    return this.http.get<ReportDescriptorVO[]>(this._serviceBaseUrl + '/reports', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Refine report options.
   * @param req report request with selections
   * @returns {Observable<T>}
   */
  updateReportRequestValues(req:ReportRequestVO):Observable<ReportRequestVO> {

    let body = JSON.stringify(req);

    return this.http.post<ReportRequestVO>(this._serviceBaseUrl + '/reports/configure', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
  }


  /**
   * Generate report and return file name on server.
   * @param req report request with selections
   * @returns {Observable<T>}
   */
  generateReport(req:ReportRequestVO):Observable<string> {

    let body = JSON.stringify(req);

    return this.http.post(this._serviceBaseUrl + '/reports/generate', body, { headers: Util.requestOptions(), responseType: 'text' })
        .pipe(catchError(this.handleError));
  }


  /**
   * Download file.
   * @param fileName file
   * @returns {Observable<T>}
   */
  downloadReport(filePath:string, fileName:string = null):Observable<boolean> {

    if (fileName == null) {
      let pos = filePath.lastIndexOf('/');
      if (pos != -1) {
        fileName = filePath.substring(pos + 1) + '.zip';
      }
    }

    return this.http.get(this._serviceBaseUrl + '/filemanager/download?fileName=' + encodeURI(filePath) + '&nocache=' + Math.random(),
        { headers: Util.requestOptions({ accept: null }), observe: 'response', responseType: 'arraybuffer'})
      .pipe(catchError(this.handleError), map(res => {
        let options = { type: res.headers.get('Content-Type')};
        let data = new Blob([res.body], options);

        if (fileName != null) {
          // Open with a nice file name
          let ahref = document.createElement('a');
          if (ahref.download !== undefined) {
            // Browsers that support HTML5 download attribute
            const url = URL.createObjectURL(data);
            ahref.setAttribute('href', url);
            ahref.setAttribute('download', fileName);
            ahref.style.visibility = 'hidden';
            document.body.appendChild(ahref);
            ahref.click();
            document.body.removeChild(ahref);
            return true;
          }
        }
        // default open with hash as file name
        window.open(URL.createObjectURL(data), '_blank');
        return true;
      }));

  }

  /**
   * Download file.
   * @param fileName file
   * @returns {Observable<T>}
   */
  downloadReportObject(object:any, fileName:string = null):Observable<boolean> {

    let data = new Blob([ Util.toCsv(object, true) ], { type: 'text/csv' });

    if (fileName != null) {
      // Open with a nice file name
      let ahref = document.createElement('a');
      if (ahref.download !== undefined) {
        // Browsers that support HTML5 download attribute
        const url = URL.createObjectURL(data);
        ahref.setAttribute('href', url);
        ahref.setAttribute('download', fileName);
        ahref.style.visibility = 'hidden';
        document.body.appendChild(ahref);
        ahref.click();
        document.body.removeChild(ahref);
        return of(true);
      }
    }
    // default open with hash as file name
    window.open(URL.createObjectURL(data), '_blank');

    return of(true);

  }


  private handleError (error:any) {

    LogUtil.error('ReportsService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}

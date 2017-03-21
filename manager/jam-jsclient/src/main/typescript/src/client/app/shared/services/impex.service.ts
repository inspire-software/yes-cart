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
import { Util } from './util';
import { LogUtil } from './../log/index';
import { DataGroupInfoVO, Pair, JobStatusVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ImpexService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct system service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('ImpexService constructed');
  }


  /**
   * Get import groups.
   * @returns {Observable<R>}
   */
  getGroups(lang:string, mode:string) {
    return this.http.get(this._serviceBaseUrl + '/impex/' + mode + '/groups/' + lang)
      .map(res => <DataGroupInfoVO[]> res.json())
      .catch(this.handleError);
  }

  /**
   * Import from file.
   * @returns {Observable<R>}
   */
  importFromFile(group:string, fileName:string) {
    let body = fileName;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/impex/import/' + group + '/', body, options)
      .map(res => <string> res.text())
      .catch(this.handleError);
  }

  /**
   * Get import status.
   * @returns {Observable<R>}
   */
  getImportStatus(token:string) {
    return this.http.get(this._serviceBaseUrl + '/impex/import/status?token=' + encodeURI(token))
      .map(res => <JobStatusVO> res.json())
      .catch(this.handleError);
  }

  /**
   * Export to file.
   * @returns {Observable<R>}
   */
  exportToFile(group:string, fileName:string) {
    let body = fileName;
    let headers = new Headers({ 'Content-Type': 'text/plain; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this._serviceBaseUrl + '/impex/export/' + group + '/', body, options)
      .map(res => <string> res.text())
      .catch(this.handleError);
  }


  /**
   * Get export status.
   * @returns {Observable<R>}
   */
  getExportStatus(token:string) {
    return this.http.get(this._serviceBaseUrl + '/impex/export/status?token=' + encodeURI(token))
      .map(res => <JobStatusVO> res.json())
      .catch(this.handleError);
  }



  /**
   * Get list of files.
   * @returns {Observable<R>}
   */
  getFiles(mode:string) {
    return this.http.get(this._serviceBaseUrl + '/filemanager/list/' + mode)
      .map(res => <Pair<string, string>[]> res.json())
      .catch(this.handleError);
  }


  /**
   * Remove file.
   * @param fileName file
   * @returns {Observable<R>}
   */
  removeFile(fileName:string) {
    let headers = new Headers({ 'Content-Type': 'application/json; charset=utf-8' });
    let options = new RequestOptions({ headers: headers });

    return this.http.delete(this._serviceBaseUrl + '/filemanager/delete?fileName=' + encodeURI(fileName), options)
      .catch(this.handleError);
  }



  private handleError (error:any) {

    LogUtil.error('ImpexService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}

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
import { Util } from './util';
import { LogUtil } from './../log/index';
import { VoDataGroupImpEx, Pair, JobStatusVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

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
  constructor (private http: HttpClient) {
    LogUtil.debug('ImpexService constructed');
  }


  /**
   * Get import groups.
   * @returns {Observable<T>}
   */
  getGroups(lang:string, mode:string):Observable<VoDataGroupImpEx[]> {
    return this.http.get<VoDataGroupImpEx[]>(this._serviceBaseUrl + '/impex/' + mode + '/groups?lang=' + lang,
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Import from file.
   * @returns {Observable<T>}
   */
  importFromFile(group:string, fileName:string):Observable<JobStatusVO> {
    let body = JSON.stringify({ group: group, fileName: fileName });
    return this.http.post<JobStatusVO>(this._serviceBaseUrl + '/impex/import', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get import status.
   * @returns {Observable<T>}
   */
  getImportStatus(token:string):Observable<JobStatusVO> {
    return this.http.get<JobStatusVO>(this._serviceBaseUrl + '/impex/import/status?token=' + encodeURIComponent(token),
        { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Export to file.
   * @returns {Observable<T>}
   */
  exportToFile(group:string, fileName:string):Observable<JobStatusVO> {
    let body = JSON.stringify({ group: group, fileName: fileName });
    return this.http.post<JobStatusVO>(this._serviceBaseUrl + '/impex/export', body,
          { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get export status.
   * @returns {Observable<T>}
   */
  getExportStatus(token:string):Observable<JobStatusVO> {
    return this.http.get<JobStatusVO>(this._serviceBaseUrl + '/impex/export/status?token=' + encodeURIComponent(token), { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }



  /**
   * Get list of files.
   * @returns {Observable<T>}
   */
  getFiles(mode:string):Observable<Pair<string, string>[]> {
    return this.http.get<Pair<string, string>[]>(this._serviceBaseUrl + '/filemanager/list?mode=' + mode, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Remove file.
   * @param fileName file
   * @returns {Observable<T>}
   */
  removeFile(fileName:string):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/filemanager/delete?fileName=' + encodeURIComponent(fileName), { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }

  /**
   * Download file.
   * @param fileName file
   * @returns {Observable<T>}
   */
  downloadFile(filePath:string, fileName:string = null):Observable<boolean> {

    return this.http.get(this._serviceBaseUrl + '/' + filePath,
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
            ahref.setAttribute('download', fileName + '.zip');
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
   * Upload file.
   * @param file file
   * @returns {Observable<T>}
   */
  uploadFile(file:File):Observable<boolean> {

    let _body:FormData = new FormData();
    _body.append('file', file);

    return this.http.post(this._serviceBaseUrl + '/filemanager/upload', _body,
      { headers: Util.requestOptions({ includeAuth: true, type: 'multipart/form-data', accept: '*/*' }), observe: 'response', responseType: 'arraybuffer'})
      .pipe(catchError(this.handleError), map(res => true));

  }


  /**
   * Download templates.
   * @param fileName file
   * @returns {Observable<T>}
   */
  downloadTemplates(groupId:number, fileName:string = null):Observable<boolean> {

    return this.http.get(this._serviceBaseUrl + '/impex/datagroups/' + groupId + '/templates',
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
            ahref.setAttribute('download', fileName + '.zip');
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



  private handleError (error:any) {

    LogUtil.error('ImpexService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}

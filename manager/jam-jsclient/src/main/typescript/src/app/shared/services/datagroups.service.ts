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
import { DataGroupVO, DataDescriptorVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class DataGroupsService {

  private _serviceBaseUrl = Config.API + 'service';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: HttpClient) {
    LogUtil.debug('DataGroupsService constructed');
  }

  /**
   * Get list of all groups,
   * @returns {Observable<T>}
   */
  getAllDataGroups():Observable<DataGroupVO[]> {
    return this.http.get<DataGroupVO[]>(this._serviceBaseUrl + '/impex/datagroups', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Get group,
   * @param id group id
   * @returns {Observable<T>}
   */
  getDataGroupById(id:number):Observable<DataGroupVO> {
    return this.http.get<DataGroupVO>(this._serviceBaseUrl + '/impex/datagroups/' + id, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create group.
   * @param group group
   * @returns {Observable<T>}
   */
  saveDataGroup(group:DataGroupVO):Observable<DataGroupVO> {

    let body = JSON.stringify(group);

    if (group.datagroupId > 0) {
      return this.http.put<DataGroupVO>(this._serviceBaseUrl + '/impex/datagroups', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<DataGroupVO>(this._serviceBaseUrl + '/impex/datagroups', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }


  /**
   * Remove group.
   * @param group group
   * @returns {Observable<T>}
   */
  removeDataGroup(group:DataGroupVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/impex/datagroups/' + group.datagroupId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }


  /**
   * Get list of all descriptors,
   * @returns {Observable<T>}
   */
  getAllDataDescriptors():Observable<DataDescriptorVO[]> {
    return this.http.get<DataDescriptorVO[]>(this._serviceBaseUrl + '/impex/datadescriptors', { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }


  /**
   * Get descriptor,
   * @param id descriptor id
   * @returns {Observable<T>}
   */
  getDataDescriptorById(id:number):Observable<DataDescriptorVO> {
    return this.http.get<DataDescriptorVO>(this._serviceBaseUrl + '/impex/datadescriptors/' + id, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError));
  }

  /**
   * Create descriptor.
   * @param descriptor descriptor
   * @returns {Observable<T>}
   */
  saveDataDescriptor(descriptor:DataDescriptorVO):Observable<DataDescriptorVO> {
    let body = JSON.stringify(descriptor);

    if (descriptor.datadescriptorId > 0) {
      return this.http.put<DataDescriptorVO>(this._serviceBaseUrl + '/impex/datadescriptors', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    } else {
      return this.http.post<DataDescriptorVO>(this._serviceBaseUrl + '/impex/datadescriptors', body, { headers: Util.requestOptions() })
        .pipe(catchError(this.handleError));
    }
  }

  /**
   * Remove descriptor.
   * @param descriptor descriptor
   * @returns {Observable<T>}
   */
  removeDataDescriptor(descriptor:DataDescriptorVO):Observable<boolean> {

    return this.http.delete(this._serviceBaseUrl + '/impex/datadescriptors/' + descriptor.datadescriptorId, { headers: Util.requestOptions() })
      .pipe(catchError(this.handleError), map(res => true));
  }



  /**
   * Download template.
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

    LogUtil.error('DataGroupsService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return throwError(message.message || 'Server error');
  }

}

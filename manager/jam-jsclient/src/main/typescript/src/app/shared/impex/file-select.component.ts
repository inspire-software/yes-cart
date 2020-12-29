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
import { Component,  OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { Pair } from './../model/index';
import { ImpexService, UserEventBus } from './../services/index';
import { Futures, Future } from './../event/index';
import { Config } from './../../../environments/environment';

import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-file-select',
  templateUrl: 'file-select.component.html',
})

export class FileSelectComponent implements OnInit, OnDestroy {

  @Input() showNewLink: boolean = true;

  @Input() mode: string = null;

  @Output() dataSelected: EventEmitter<Pair<string,string>> = new EventEmitter<Pair<string,string>>();

  private files : Pair<string,string>[] = null;
  public filteredFiles : Pair<string,string>[] = [];
  public fileFilter : string;

  public selectedFile : Pair<string,string> = null;
  public uploadFileName : string = null;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public loading:boolean = false;

  constructor (private _fileService : ImpexService) {
    LogUtil.debug('FileSelectComponent constructed');
  }

  @Input()
  set filter(filter:string) {
    this.fileFilter = filter;
    this.onRefresh();
  }

  ngOnDestroy() {
    LogUtil.debug('FileSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    LogUtil.debug('FileSelectComponent ngOnInit');
    if (this.files == null && UserEventBus.getUserEventBus().current() != null) {
      this.getAllFiles();
    }
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.reloadFileList();
    }, this.delayedFilteringMs);

  }

  onSelectClick(file: Pair<string, string>) {
    LogUtil.debug('FileSelectComponent onSelectClick', file);
    this.selectedFile = file;
    this.dataSelected.emit(this.selectedFile);
  }

  onFilterChange() {

    this.delayedFiltering.delay();

  }

  onClearFilter() {
    this.fileFilter = '';
    this.delayedFiltering.delay();
  }

  onRefresh() {
    if (UserEventBus.getUserEventBus().current() != null) {
      this.dataSelected.emit(null);
      this.getAllFiles();
      this.reloadFileList();
    }
  }

  onDeleteClick(file:Pair<string,string>) {
    LogUtil.debug('FileSelectComponent onDeleteClick');
    this.loading = true;
    this._fileService.removeFile(file.first).subscribe( rez => {
      LogUtil.debug('FileSelectComponent removeFile', file.first);
      this.selectedFile = null;
      this.loading = false;
      this.getAllFiles();
      this.reloadFileList();
    });
  }


  isUploadDisabled():boolean {
    let input:any = document.getElementById('avmodaluploadimpex');
    return input == null || input.disabled;
  }

  onUploadClickRelay() {
    LogUtil.debug('FileSelectComponent impex upload relay button click');
    document.getElementById('avmodaluploadimpex').click();
  }

  onImpexFileSelected(event:any) {
    LogUtil.debug('FileSelectComponent onImpexFileSelected');

    let srcElement:any = event.target || event.srcElement;
    let impex:any = srcElement.files[0];
    if (impex != null) {
      this.uploadFileName = impex.name;
      LogUtil.debug('FileSelectComponent impex file selected', this.uploadFileName);

      this.loading = true;
      this._fileService.uploadFile(impex).subscribe(res => {
        LogUtil.debug('FileSelectComponent impex file uploaded', res);
        this.fileFilter = this.uploadFileName;
        this.uploadFileName = null;
        this.loading = false;
        srcElement.value = '';
        this.onRefresh();
      })

    }
  }


  private getAllFiles() {
    LogUtil.debug('FileSelectComponent getAllFiles');

    this.loading = true;
    this._fileService.getFiles(this.mode).subscribe( allfiles => {
      LogUtil.debug('FileSelectComponent getFiles', this.mode, allfiles);
      this.files = allfiles;
      this.selectedFile = null;
      this.uploadFileName = null;
      this.loading = false;
      this.reloadFileList();
    });
  }

  /**
   * Reload list of files
   */
  private reloadFileList() {

    if (this.files != null) {

      if (this.fileFilter) {
        let _filter = this.fileFilter.toLowerCase();
        this.filteredFiles = this.files.filter(file =>
          file.first.toLowerCase().indexOf(_filter) != -1
        );
        LogUtil.debug('FileSelectComponent reloadFileList filter: ' + _filter, this.filteredFiles);
      } else {
        this.filteredFiles = this.files.slice(0, this.files.length);
        LogUtil.debug('FileSelectComponent reloadFileList no filter', this.filteredFiles);
      }
    }

  }

}

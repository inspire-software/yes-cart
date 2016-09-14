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
import {Component,  OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgFor} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {Pair} from './../model/index';
import {ImpexService, WindowMessageEventBus} from './../services/index';
import {Futures, Future} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-file-select',
  moduleId: module.id,
  templateUrl: 'file-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class FileSelectComponent implements OnInit, OnDestroy {

  private files : Pair<string,string>[] = null;
  private filteredFiles : Pair<string,string>[] = [];
  private fileFilter : string;

  private selectedFile : Pair<string,string> = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  @Input() showNewLink: boolean = true;

  @Input() mode: string = null;

  @Output() dataSelected: EventEmitter<Pair<string,string>> = new EventEmitter<Pair<string,string>>();

  winSub:any;

  constructor (private _fileService : ImpexService) {
    console.debug('FileSelectComponent constructed');

    this.winSub = WindowMessageEventBus.getWindowMessageEventBus().messageUpdated$.subscribe(content => {
      this.onRefresh();
    })

  }

  getAllFiles() {
    var _sub:any = this._fileService.getFiles(this.mode).subscribe( allfiles => {
      console.debug('FileSelectComponent getFiles', this.mode, allfiles);
      this.files = allfiles;
      this.selectedFile = null;
      _sub.unsubscribe();
      this.reloadFileList();
    });
  }

  ngOnDestroy() {
    console.debug('FileSelectComponent ngOnDestroy');
    this.winSub.unsubscribe();
  }

  ngOnInit() {
    console.debug('FileSelectComponent ngOnInit');
    if (this.files == null) {
      this.getAllFiles();
    }
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.reloadFileList();
    }, this.delayedFilteringMs);

  }

  onNewClick() {
    console.debug('FileSelectComponent onNewClick');

    let that = this;

    let myWindow = window.open("/yes-manager/resources/assets/uploader/uploader.html", "UPLOAD", "width=300,height=100");
    myWindow.onbeforeunload = function() {
      that.onRefresh();
    }
  }

  onSelectClick(file: Pair<string, string>) {
    console.debug('FileSelectComponent onSelectClick', file);
    this.selectedFile = file;
    this.dataSelected.emit(this.selectedFile);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

  /**
   * Reload list of files
   */
  reloadFileList() {

    if (this.files != null) {

      if (this.fileFilter) {
        let _filter = this.fileFilter.toLowerCase();
        this.filteredFiles = this.files.filter(file =>
          file.first.toLowerCase().indexOf(_filter) != -1
        );
        console.debug('FileSelectComponent reloadFileList filter: ' + _filter, this.filteredFiles);
      } else {
        this.filteredFiles = this.files.slice(0, this.files.length);
        console.debug('FileSelectComponent reloadFileList no filter', this.filteredFiles);
      }
    }

  }

  protected onRefresh() {
    this.dataSelected.emit(null);
    this.getAllFiles();
    this.reloadFileList();
  }

}

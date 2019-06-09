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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { DataGroupsService, UserEventBus, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { DataDescriptorVO } from './../shared/model/index';
import { FormValidationEvent } from './../shared/event/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-impex-datadescriptors',
  moduleId: module.id,
  templateUrl: 'impex-datadescriptors.component.html',
})

export class ImpexDataDescriptorsComponent implements OnInit, OnDestroy {

  private static DESCRIPTORS:string = 'descriptors';
  private static DESCRIPTOR:string = 'descriptor';

  private viewMode:string = ImpexDataDescriptorsComponent.DESCRIPTORS;

  private datadescriptors:Array<DataDescriptorVO> = [];
  private datadescriptorFilter:string;

  private selectedDataDescriptor:DataDescriptorVO;

  private datadescriptorEdit:DataDescriptorVO;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _dataDroupService:DataGroupsService) {
    LogUtil.debug('ImpexDataDescriptorsComponent constructed');
  }

  newDataDescriptorInstance():DataDescriptorVO {
    return {
      datadescriptorId: 0, name: '', type: 'WEBINF_XML/CSV', value: ''
    };
  }

  ngOnInit() {
    LogUtil.debug('ImpexDataDescriptorsComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    LogUtil.debug('ImpexDataDescriptorsComponent ngOnDestroy');
  }

  protected onRefreshHandler() {
    LogUtil.debug('ImpexDataDescriptorsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getAllDataDescriptors();
    }
  }

  protected onDataDescriptorSelected(data:DataDescriptorVO) {
    LogUtil.debug('ImpexDataDescriptorsComponent onDataDescriptorSelected', data);
    this.selectedDataDescriptor = data;
  }

  protected onDataDescriptorChanged(event:FormValidationEvent<DataDescriptorVO>) {
    LogUtil.debug('ImpexDataDescriptorsComponent onDataDescriptorChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.datadescriptorEdit = event.source;
  }

  protected onBackToList() {
    LogUtil.debug('ImpexDataDescriptorsComponent onBackToList handler');
    if (this.viewMode === ImpexDataDescriptorsComponent.DESCRIPTOR) {
      this.datadescriptorEdit = null;
      this.viewMode = ImpexDataDescriptorsComponent.DESCRIPTORS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('ImpexDataDescriptorsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ImpexDataDescriptorsComponent.DESCRIPTORS) {
      this.datadescriptorEdit = this.newDataDescriptorInstance();
      this.viewMode = ImpexDataDescriptorsComponent.DESCRIPTOR;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('ImpexDataDescriptorsComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    this.onRowDelete(this.selectedDataDescriptor);
  }


  protected onRowEditDataDescriptor(row:DataDescriptorVO) {
    LogUtil.debug('ImpexDataDescriptorsComponent onRowEditDataDescriptor handler', row);
    this.datadescriptorEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = ImpexDataDescriptorsComponent.DESCRIPTOR;
  }

  protected onRowEditSelected() {
    this.onRowEditDataDescriptor(this.selectedDataDescriptor);
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.datadescriptorEdit != null) {

        LogUtil.debug('ImpexDataDescriptorsComponent Save handler datadescriptor', this.datadescriptorEdit);

        this.loading = true;
        let _sub:any = this._dataDroupService.saveDataDescriptor(this.datadescriptorEdit).subscribe(
            rez => {
            if (this.datadescriptorEdit.datadescriptorId > 0) {
              let idx = this.datadescriptors.findIndex(rez => rez.datadescriptorId == this.datadescriptorEdit.datadescriptorId);
              if (idx !== -1) {
                this.datadescriptors[idx] = rez;
                this.datadescriptors = this.datadescriptors.slice(0, this.datadescriptors.length); // reset to propagate changes
                LogUtil.debug('ImpexDataDescriptorsComponent datadescriptor changed', rez);
              }
            } else {
              this.datadescriptors.push(rez);
              this.datadescriptorFilter = rez.name;
              LogUtil.debug('ImpexDataDescriptorsComponent datadescriptor added', rez);
            }
            this.changed = false;
            this.selectedDataDescriptor = rez;
            this.datadescriptorEdit = null;
              this.loading = false;
            this.viewMode = ImpexDataDescriptorsComponent.DESCRIPTORS;
            _sub.unsubscribe();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ImpexDataDescriptorsComponent discard handler');
    if (this.viewMode === ImpexDataDescriptorsComponent.DESCRIPTOR) {
      if (this.selectedDataDescriptor != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ImpexDataDescriptorsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

     if (this.selectedDataDescriptor != null) {
        LogUtil.debug('ImpexDataDescriptorsComponent onDeleteConfirmationResult', this.selectedDataDescriptor);

       this.loading = true;
        let _sub:any = this._dataDroupService.removeDataDescriptor(this.selectedDataDescriptor).subscribe(res => {
          LogUtil.debug('ImpexDataDescriptorsComponent removeDataDescriptor', this.selectedDataDescriptor);
          let idx = this.datadescriptors.indexOf(this.selectedDataDescriptor);
          this.datadescriptors.splice(idx, 1);
          this.datadescriptors = this.datadescriptors.slice(0, this.datadescriptors.length); // reset to propagate changes
          this.selectedDataDescriptor = null;
          this.datadescriptorEdit = null;
          this.loading = false;
          _sub.unsubscribe();
        });
      }
    }
  }

  protected onClearFilterDataDescriptor() {

    this.datadescriptorFilter = '';

  }

  private getAllDataDescriptors() {
    this.loading = true;
    let _sub:any = this._dataDroupService.getAllDataDescriptors().subscribe( alldatadescriptors => {
      LogUtil.debug('ImpexDataDescriptorsComponent getAllDataDescriptors', alldatadescriptors);
      this.datadescriptors = alldatadescriptors;
      this.selectedDataDescriptor = null;
      this.datadescriptorEdit = null;
      this.viewMode = ImpexDataDescriptorsComponent.DESCRIPTORS;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      _sub.unsubscribe();
    });
  }

}

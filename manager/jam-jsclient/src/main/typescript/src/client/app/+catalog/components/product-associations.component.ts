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
import { Component, OnInit, OnChanges, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { ProductVO, ProductWithLinksVO, ProductAssociationVO, AssociationVO } from './../../shared/model/index';
import { PIMService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { ProductSelectComponent } from './../../shared/catalog/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-product-associations',
  moduleId: module.id,
  templateUrl: 'product-associations.component.html',
})

export class ProductAssociationsComponent implements OnInit, OnChanges {

  private static associations:Array<AssociationVO> = null;

  @Input() showHelp:boolean = false;

  @Output() dataSelected: EventEmitter<ProductAssociationVO> = new EventEmitter<ProductAssociationVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<Array<ProductAssociationVO>>> = new EventEmitter<FormValidationEvent<Array<ProductAssociationVO>>>();

  private _masterObject:ProductWithLinksVO;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;


  private _objectAssociations:Array<ProductAssociationVO>;
  private _associationFilter:string;
  private filteredObjectAssociations:Array<ProductAssociationVO>;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  @ViewChild('addModalDialog')
  private addModalDialog:ModalComponent;

  private selectedRow:ProductAssociationVO;
  private selectedAssociation:AssociationVO;

  private associationToEdit:ProductAssociationVO;

  @ViewChild('productSelectDialog')
  private productSelectDialog:ProductSelectComponent;


  /**
   * Construct association panel
   */
  constructor(_pimService:PIMService) {
    LogUtil.debug('ProductAssociationsComponent constructed');

    this.associationToEdit = null;
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAssociations();
    }, this.delayedFilteringMs);

    if (ProductAssociationsComponent.associations == null) {
      var _sub:any = _pimService.getAllAssociations().subscribe(allassoc => {
        ProductAssociationsComponent.associations = allassoc;
        LogUtil.debug('ProductAssociationsComponent getAllAssociations', allassoc);
        _sub.unsubscribe();
      });
    }

  }

  get associations():AssociationVO[] {
    return ProductAssociationsComponent.associations;
  }

  @Input()
  set masterObject(master:ProductWithLinksVO) {
    this._masterObject = master;
    this.loadData();
  }

  @Input()
  set associationFilter(associationFilter:string) {
    this._associationFilter = associationFilter;
    this.delayedFiltering.delay();
  }

  get masterObject():ProductWithLinksVO {
    return this._masterObject;
  }

  ngOnInit() {
    LogUtil.debug('ProductAssociationsComponent ngOnInit', this.masterObject);
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('ProductAssociationsComponent ngOnChanges', changes);
    this.delayedFiltering.delay();
  }


  onRowAdd() {
    let assocId = 0;
    if (ProductAssociationsComponent.associations != null && ProductAssociationsComponent.associations.length > 0) {
      assocId = ProductAssociationsComponent.associations[0].associationId;
    }

    this.associationToEdit = {
      productassociationId: 0, rank: 0, associationId: assocId,
      productId: this.masterObject.productId,
      associatedProductId: 0, associatedCode: null, associatedName: null, associatedDescription: null
    };
    LogUtil.debug('ProductAssociationsComponent onRowAdd', this.associationToEdit);
    this.addModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }

  protected onRowDelete(row:ProductAssociationVO) {
    LogUtil.debug('ProductAssociationsComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowEdit(row:ProductAssociationVO) {
    LogUtil.debug('ProductAssociationsComponent onRowEdit handler', row);
    this.validForSave = false;
    this.associationToEdit = Util.clone(row);
    this.editModalDialog.show();
  }

  protected onAssociationSelected(row:AssociationVO) {
    this.selectedAssociation = row;
  }

  protected onSelectRow(row:ProductAssociationVO) {
    LogUtil.debug('ProductAssociationsComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
    this.dataSelected.emit(this.selectedRow);
  }

  protected onDataChange(event:any) {

    this.validForSave = this.associationToEdit.associatedProductId > 0 && this.associationToEdit.associationId > 0;
    LogUtil.debug('ProductAssociationsComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);

  }


  protected onSearchProduct() {
    if (this.associationToEdit != null && this.associationToEdit.productassociationId <= 0) {
      this.productSelectDialog.showDialog();
    }
  }



  protected onProductSelected(event:FormValidationEvent<ProductVO>) {
    LogUtil.debug('ProductAssociationsComponent onProductSelected');
    if (event.valid && this.associationToEdit != null && this.associationToEdit.productassociationId <= 0) {
      this.associationToEdit.associatedCode = event.source.code;
      this.associationToEdit.associatedName = event.source.name;
      this.associationToEdit.associatedDescription = event.source.description;
      this.associationToEdit.associatedProductId = event.source.productId;
      LogUtil.debug('ProductAssociationsComponent onProductSelected', this.associationToEdit);
      this.onDataChange(event);
    }
  }


  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductAssociationsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.productassociationId;
      if (attrToDelete === 0) {
        let idx = this._objectAssociations.findIndex(attrVo => {
          return attrVo.associationId === this.selectedRow.associationId && attrVo.associatedProductId === this.selectedRow.associatedProductId;
        });
        if (idx != -1) {
          this._objectAssociations.splice(idx, 1);
        }
        LogUtil.debug('ProductAssociationsComponent onDeleteConfirmationResult index in array of new association ' + idx);
      } else {
        let idx = this._objectAssociations.findIndex(attrVo => {
          return attrVo.productassociationId === attrToDelete;
        });
        if (idx != -1) {
          this._objectAssociations.splice(idx, 1);
        }
        LogUtil.debug('ProductAssociationsComponent onDeleteConfirmationResult association ' + attrToDelete);
      }
      this.filterAssociations();
      this.onSelectRow(null);
      this.changed = true;
      this.processDataChangesEvent();
    } else {
      this.associationToEdit = null;
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    LogUtil.debug('ProductAssociationsComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.associationToEdit.productassociationId === 0) { // add new
        LogUtil.debug('ProductAssociationsComponent onEditModalResult add new association', this._objectAssociations);
        let idx = this._objectAssociations.findIndex(attrVo => {
          return attrVo.associationId === this.associationToEdit.associationId && attrVo.associatedCode === this.associationToEdit.associatedCode;
        });
        if (idx != -1) {
          this._objectAssociations[idx] = this.associationToEdit;
        } else {
          this._objectAssociations.push(this.associationToEdit);
        }
      } else { // edit existing
        LogUtil.debug('ProductAssociationsComponent onEditModalResult update existing', this._objectAssociations);
        let idx = this._objectAssociations.findIndex(attrVo =>  {
          return attrVo.productassociationId === this.associationToEdit.productassociationId;
        });
        this._objectAssociations[idx] = this.associationToEdit;
      }
      this.selectedRow = this.associationToEdit;
      this.changed = true;
      this.filterAssociations();
      this.processDataChangesEvent();
    } else {
      this.associationToEdit = null;
    }
  }

  protected resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected getAssociationType(row:ProductAssociationVO):string {
    if (row != null && ProductAssociationsComponent.associations != null) {
      let idx = ProductAssociationsComponent.associations.findIndex(assoc => {
        return assoc.associationId == row.associationId;
      });
      if (idx != -1) {
        return ProductAssociationsComponent.associations[idx].name;
      }
    }
    return '-';
  }

  private loadData() {
    if (this.masterObject) {

      this._objectAssociations = this.masterObject.associations;

      LogUtil.debug('ProductAssociationsComponent associations', this._objectAssociations);
      this.filterAssociations();

    } else {

      this.filteredObjectAssociations = [];

    }

    this.changed = false;
    this.onSelectRow(null);
  }


  private filterAssociations() {
    let _filter = this._associationFilter ? this._associationFilter.toLowerCase() : null;
    let _filteredObjectAssociations:Array<ProductAssociationVO> = [];
    if (_filter && this._objectAssociations) {
      if (_filter.indexOf('#') === 0) {

        let _type = _filter.substr(1);

        let ids:Array<number> = [];
        ProductAssociationsComponent.associations.forEach(assoc => {
          if (assoc.code.toLowerCase().indexOf(_type) !== -1 ||
              assoc.name.toLowerCase().indexOf(_type) !== -1) {
            ids.push(assoc.associationId);
          }
        });

        _filteredObjectAssociations = this._objectAssociations.filter(val =>
          ids.indexOf(val.associationId) !== -1
        );
      } else {
        _filteredObjectAssociations = this._objectAssociations.filter(val =>
          val.associatedCode.toLowerCase().indexOf(_filter) !== -1 ||
          val.associatedName.toLowerCase().indexOf(_filter) !== -1
        );
      }
      LogUtil.debug('ProductAssociationsComponent filterAssociations ' +  _filter, _filteredObjectAssociations);
    } else {
      _filteredObjectAssociations = this._objectAssociations;
      LogUtil.debug('ProductAssociationsComponent filterAssociations no filter', _filteredObjectAssociations);
    }

    if (_filteredObjectAssociations === null) {
      _filteredObjectAssociations = [];
    }

    _filteredObjectAssociations.sort(function(a, b) {
      var assoc:number = a.associationId - b.associationId;
      if (assoc == 0) {
        var rank:number = a.rank - b.rank;
        if (rank == 0) {
          return a.associatedCode > b.associatedCode ? 1 : -1;
        }
        return rank;
      }
      return assoc;
    });

    this.filteredObjectAssociations = _filteredObjectAssociations;

    let _total = this.filteredObjectAssociations.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }


  private processDataChangesEvent() {

    LogUtil.debug('ProductAssociationsComponent data changes', this.masterObject);
    if (this.masterObject && this._objectAssociations) {

      LogUtil.debug('ProductAssociationsComponent data changes update', this._objectAssociations);

      this.dataChanged.emit({ source: this._objectAssociations, valid: true });

    }

  }

}

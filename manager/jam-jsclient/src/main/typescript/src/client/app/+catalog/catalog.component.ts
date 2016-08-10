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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {CategoryService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CategoriesComponent, /* , CategoryComponent */} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {CategorySelectComponent} from './../shared/catalog/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {CategoryVO, BasicCategoryVO} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-catalog',
  moduleId: module.id,
  templateUrl: 'catalog.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CategoriesComponent, CategorySelectComponent, /* CategoryComponent, */ ModalComponent, DataControlComponent ],
})

export class CatalogComponent implements OnInit, OnDestroy {

  private static CATEGORIES:string = 'categories';
  private static CATEGORY:string = 'category';

  private forceShowAll:boolean = false;
  private viewMode:string = CatalogComponent.CATEGORIES;

  private categories:Array<CategoryVO> = [];
  private categoryFilter:string;
  private categoryFilterRequired:boolean = true;
  private categoryFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedCategory:CategoryVO;

  private categoryAdd:BasicCategoryVO;
  private categoryEdit:CategoryVO;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  @ViewChild('categorySelectComponent')
  categorySelectComponent:CategorySelectComponent;

  constructor(private _categoryService:CategoryService) {
    console.debug('CatalogComponent constructed');
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newCategoryInstance():BasicCategoryVO {
    return { guid: null, name: ''};
  }

  ngOnInit() {
    console.debug('CatalogComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCategories();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    console.debug('CatalogComponent ngOnDestroy');
  }


  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredCategories() {
    this.categoryFilterRequired = !this.forceShowAll && (this.categoryFilter == null || this.categoryFilter.length < 2);

    console.debug('CatalogComponent getFilteredCategories' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.categoryFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._categoryService.getFilteredCategories(this.categoryFilter, max).subscribe( allcategories => {
        console.debug('CatalogComponent getAllCountries', allcategories);
        this.categories = allcategories;
        this.selectedCategory = null;
        this.categoryEdit = null;
        this.viewMode = CatalogComponent.CATEGORIES;
        this.changed = false;
        this.validForSave = false;
        this.categoryFilterCapped = this.categories.length >= max;
        _sub.unsubscribe();
      });
    } else {
      this.categories = [];
      this.selectedCategory = null;
      this.categoryEdit = null;
      this.viewMode = CatalogComponent.CATEGORIES;
      this.changed = false;
      this.validForSave = false;
      this.categoryFilterCapped = false;
    }
  }

  protected onRefreshHandler() {
    console.debug('CatalogComponent refresh handler');
    this.getFilteredCategories();
  }

  onCategorySelected(data:CategoryVO) {
    console.debug('CatalogComponent onCategorySelected', data);
    this.selectedCategory = data;
  }

  onCategoryChanged(event:FormValidationEvent<CategoryVO>) {
    console.debug('CatalogComponent onCategoryChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.categoryEdit = event.source;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCategories();
  }

  protected onBackToList() {
    console.debug('CatalogComponent onBackToList handler');
    if (this.viewMode === CatalogComponent.CATEGORY) {
      this.categoryEdit = null;
      this.viewMode = CatalogComponent.CATEGORIES;
    }
  }

  protected onViewTree() {
    console.debug('CatalogComponent onViewTree handler');
    this.categorySelectComponent.showDialog();
  }

  protected onCatalogTreeDataSelected(event:FormValidationEvent<CategoryVO>) {
    console.debug('CatalogComponent onCatalogTreeDataSelected handler', event);
    if (event.valid) {
      this.categoryFilter = event.source.guid;
      this.getFilteredCategories();
    }
  }

  protected onRowNew() {
    console.debug('CatalogComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogComponent.CATEGORIES) {
      this.categoryAdd = this.newCategoryInstance();
      this.viewMode = CatalogComponent.CATEGORY;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('CatalogComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCategory != null) {
      this.onRowDelete(this.selectedCategory);
    }
  }


  protected onRowEditCategory(row:CategoryVO) {
    console.debug('CatalogComponent onRowEditCategory handler', row);
    this.categoryEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = CatalogComponent.CATEGORY;
  }

  protected onRowEditSelected() {
    if (this.selectedCategory != null) {
      this.onRowEditCategory(this.selectedCategory);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.categoryEdit != null) {

        console.debug('CatalogComponent Save handler category', this.categoryEdit);

        //var _sub:any = this._categoryService.save(this.categoryEdit).subscribe(
        //    rez => {
        //    if (this.categoryEdit.categoryId > 0) {
        //      let idx = this.categories.findIndex(rez => rez.categoryId == this.categoryEdit.categoryId);
        //      if (idx !== -1) {
        //        this.categories[idx] = rez;
        //        this.categories = this.categories.slice(0, this.categories.length); // reset to propagate changes
        //        console.debug('CatalogComponent category changed', rez);
        //      }
        //    } else {
        //      this.categories.push(rez);
        //      this.categoryFilter = rez.categoryCode;
        //      console.debug('CatalogComponent category added', rez);
        //    }
        //    this.changed = false;
        //    this.selectedCategory = rez;
        //    this.categoryEdit = null;
        //    this.viewMode = CatalogComponent.CATEGORIES;
        //    _sub.unsubscribe();
        //  }
        //);
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('CatalogComponent discard handler');
    if (this.viewMode === CatalogComponent.CATEGORY) {
      if (this.selectedCategory != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('CatalogComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCategory != null) {
        console.debug('CatalogComponent onDeleteConfirmationResult', this.selectedCategory);

        //var _sub:any = this._categoryService.removeCategory(this.selectedCategory).subscribe(res => {
        //  console.debug('CatalogComponent removeCategory', this.selectedCategory);
        //  let idx = this.categories.indexOf(this.selectedCategory);
        //  this.categories.splice(idx, 1);
        //  this.categories = this.categories.slice(0, this.categories.length); // reset to propagate changes
        //  this.selectedCategory = null;
        //  this.categoryEdit = null;
        //  _sub.unsubscribe();
        //});
      }
    }
  }


}

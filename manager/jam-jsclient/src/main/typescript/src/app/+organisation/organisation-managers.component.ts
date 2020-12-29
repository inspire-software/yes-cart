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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { OrganisationService, CatalogService, ShopEventBus, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ManagerInfoVO, ManagerVO, ShopVO, RoleVO, ProductSupplierCatalogVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-organisation-managers',
  templateUrl: 'organisation-managers.component.html',
})

export class OrganisationManagerComponent implements OnInit, OnDestroy {

  private static MANAGERS:string = 'managers';
  private static MANAGER:string = 'manager';

  public viewMode:string = OrganisationManagerComponent.MANAGERS;

  public managers:SearchResultVO<ManagerInfoVO>;
  public managerFilter:string;

  private delayedFilteringManager:Future;
  private delayedFilteringManagerMs:number = Config.UI_INPUT_DELAY;

  public selectedManager:ManagerInfoVO;
  public managerEdit:ManagerVO;

  public shops:Array<ShopVO> = [];
  public roles:Array<RoleVO> = [];
  public suppliers:Array<ProductSupplierCatalogVO> = [];

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('disableConfirmationModalDialog')
  private disableConfirmationModalDialog:ModalComponent;

  @ViewChild('resetConfirmationModalDialog')
  private resetConfirmationModalDialog:ModalComponent;

  public deleteValue:String;
  private shopAllSub:any;

  public loading:boolean = false;

  public changed:boolean = false;
  public validForSave:boolean = false;

  constructor(private _organisationService:OrganisationService,
              private _catalogService:CatalogService) {
    LogUtil.debug('OrganisationManagerComponent constructed');
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
    this.managers = this.newSearchResultInstance();
  }

  newManagerInstance():ManagerVO {
    return {
      managerId: 0, login: null,
      email: null, firstName: null, lastName: null, enabled: false,
      companyName1: null, companyName2: null, companyDepartment: null,
      managerShops: [], managerRoles: [], managerSupplierCatalogs: [], managerCategoryCatalogs: []
    };
  }

  newSearchResultInstance():SearchResultVO<ManagerInfoVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('OrganisationManagerComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFilteringManager = Futures.perpetual(function() {
      that.getAllManagers();
    }, this.delayedFilteringManagerMs);
  }

  ngOnDestroy() {
    LogUtil.debug('OrganisationManagerComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }


  onManagerFilterChange(event:any) {
    this.managers.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFilteringManager.delay();
  }

  onRefreshHandler() {
    LogUtil.debug('OrganisationManagerComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      if (this.roles == null || this.roles.length == 0) {
        this.getAllRoles();
      } else {
        this.getAllManagers();
      }
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('LocationsComponent onPageSelected', page);
    this.managers.searchContext.start = page;
    this.delayedFilteringManager.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('LocationsComponent ononSortSelected', sort);
    if (sort == null) {
      this.managers.searchContext.sortBy = null;
      this.managers.searchContext.sortDesc = false;
    } else {
      this.managers.searchContext.sortBy = sort.first;
      this.managers.searchContext.sortDesc = sort.second;
    }
    this.delayedFilteringManager.delay();
  }

  onManagerSelected(data:ManagerVO) {
    LogUtil.debug('OrganisationManagerComponent onManagerSelected', data);
    this.selectedManager = data;
  }

  onManagerChanged(event:FormValidationEvent<ManagerVO>) {
    LogUtil.debug('OrganisationManagerComponent onManagerChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.managerEdit = event.source;
  }

  onBackToList() {
    LogUtil.debug('OrganisationManagerComponent onBackToList handler');
    if (this.viewMode === OrganisationManagerComponent.MANAGER) {
      this.managerEdit = null;
      this.viewMode = OrganisationManagerComponent.MANAGERS;
    }
  }

  onRowNew() {
    LogUtil.debug('OrganisationManagerComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === OrganisationManagerComponent.MANAGERS) {
      this.managerEdit = this.newManagerInstance();
      this.viewMode = OrganisationManagerComponent.MANAGER;
    }
  }

  onRowDelete(row:any) {
    LogUtil.debug('OrganisationManagerComponent onRowDelete handler', row);
    this.deleteValue = row.email;
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedManager != null) {
      this.onRowDelete(this.selectedManager);
    }
  }

  onRowEditManager(row:ManagerInfoVO) {
    LogUtil.debug('OrganisationManagerComponent onRowEditManager handler', row);
    this.loading = true;
    this._organisationService.getManagerById(row.managerId).subscribe( manager => {
      LogUtil.debug('OrganisationManagerComponent get manager by email', manager);
      this.managerEdit = manager;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = OrganisationManagerComponent.MANAGER;
      this.loading = false;
    });
  }

  onRowEditSelected() {
    if (this.selectedManager != null) {
      this.onRowEditManager(this.selectedManager);
    }
  }

  onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.managerEdit != null) {

        LogUtil.debug('OrganisationManagerComponent Save handler manager', this.managerEdit);

        this.loading = true;
        this._organisationService.saveManager(this.managerEdit).subscribe(
            rez => {
              this.changed = false;
              this.selectedManager = rez;
              this.validForSave = false;
              this.managerEdit = null;
              this.loading = false;
              this.getAllManagers();
          }
        );
      }

    }

  }

  onDiscardEventHandler() {
    LogUtil.debug('OrganisationManagerComponent discard handler');
    if (this.viewMode === OrganisationManagerComponent.MANAGER) {
      if (this.selectedManager != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('OrganisationManagerComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedManager != null) {
        LogUtil.debug('OrganisationManagerComponent onDeleteConfirmationResult', this.selectedManager);

        this.loading = true;
        this._organisationService.removeManager(this.selectedManager.managerId).subscribe(res => {
          LogUtil.debug('OrganisationManagerComponent removeManager', this.selectedManager);
          this.selectedManager = null;
          this.managerEdit = null;
          this.loading = false;
          this.viewMode = OrganisationManagerComponent.MANAGERS;
          this.getAllManagers();
        });
      }
    }
  }

  onRowEnableSelected() {
    if (this.selectedManager != null) {
      this.deleteValue = this.selectedManager.email;
      this.disableConfirmationModalDialog.show();
    }
  }


  onDisableConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('OrganisationManagerComponent onDisableConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedManager != null) {
        this.loading = true;
        this._organisationService.updateDisabledFlag(this.selectedManager.managerId, this.selectedManager.enabled).subscribe( done => {
          LogUtil.debug('OrganisationManagerComponent updateDisabledFlag', done);
          this.selectedManager.enabled = !this.selectedManager.enabled;
          this.changed = false;
          this.validForSave = false;
          this.loading = false;
        });
      }
    }
  }


  onRowResetSelected() {
    if (this.selectedManager != null) {
      this.deleteValue = this.selectedManager.email;
      this.resetConfirmationModalDialog.show();
    }
  }


  onResetConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('OrganisationManagerComponent onResetConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedManager != null) {
        this.loading = true;
        this._organisationService.resetPassword(this.selectedManager.managerId).subscribe( done => {
          LogUtil.debug('OrganisationManagerComponent resetPassword', done);
          this.changed = false;
          this.validForSave = false;
          this.loading = false;
        });
      }
    }
  }


  onClearFilter() {

    this.managerFilter = '';
    this.getAllManagers();

  }

  private getAllManagers() {

    LogUtil.debug('OrganisationManagerComponent getAllManagers');

    this.loading = true;

    this.managers.searchContext.parameters.filter = [ this.managerFilter ];
    this.managers.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

    this._organisationService.getFilteredManagers(this.managers.searchContext).subscribe( allmanagers => {
      LogUtil.debug('OrganisationManagerComponent getAllManagers', allmanagers);
      this.managers = allmanagers;
      this.selectedManager = null;
      this.managerEdit = null;
      this.viewMode = OrganisationManagerComponent.MANAGERS;
      this.changed = false;
      this.validForSave = false;
      this.loading = false;
    });
  }

  private getAllRoles() {
    this.loading = true;
    this._organisationService.getAllRoles().subscribe( allroles => {
      LogUtil.debug('OrganisationManagerComponent getAllRoles', allroles);
      this.roles = allroles;
      this._catalogService.getAllProductSuppliersCatalogs().subscribe(allsup => {
        LogUtil.debug('OrganisationManagerComponent getAllProductSuppliersCatalogs', allsup);
        this.suppliers = allsup;
        this.loading = false;
        this.getAllManagers();
      });
    });
  }

}

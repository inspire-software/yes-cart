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
import {I18nEventBus, ShopEventBus, ShippingService, PaymentService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CarriersComponent, CarrierComponent, SlasComponent, SlaComponent } from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {CarrierShopLinkVO, CarrierLocaleVO, CarrierVO, ShopCarrierVO, CarrierSlaVO, PaymentGatewayInfoVO, ShopVO} from './../shared/model/index';
import {FormValidationEvent} from './../shared/event/index';

@Component({
  selector: 'yc-shipping',
  moduleId: module.id,
  templateUrl: 'shipping.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CarriersComponent, CarrierComponent, SlasComponent, SlaComponent, ModalComponent, DataControlComponent ],
})

export class ShippingComponent implements OnInit, OnDestroy {

  private static CARRIERS:string = 'carriers';
  private static CARRIER:string = 'carrier';
  private static SLAS:string = 'slas';
  private static SLA:string = 'sla';

  private viewMode:string = ShippingComponent.CARRIERS;

  private carriers:Array<CarrierVO> = [];
  private carrierFilter:string;

  private selectedCarrier:CarrierVO;

  private carrierEdit:CarrierVO;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  private slas:Array<CarrierSlaVO> = [];
  private slaFilter:string;
  private pgs:Array<CarrierSlaVO> = [];
  private shops:Array<ShopVO> = [];

  private selectedSla:CarrierSlaVO;

  private slaEdit:CarrierSlaVO;

  private deleteValue:String;

  private shopAllSub:any;

  constructor(private _shippingService:ShippingService,
              private _paymentService:PaymentService) {
    console.debug('ShippingComponent constructed');
    this.shopAllSub = ShopEventBus.getShopEventBus().shopsUpdated$.subscribe(shopsevt => {
      this.shops = shopsevt;
    });
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newCarrierInstance():CarrierVO {
    return { carrierId: 0, name: '', description: '', displayNames: [], displayDescriptions: [], carrierShops: [] };
  }

  newSlaInstance():CarrierSlaVO {
    let carrierId = this.selectedCarrier != null ? this.selectedCarrier.carrierId : 0;
    return {
      carrierslaId: 0, carrierId: carrierId, code: null, name: '', description: '',
      displayNames: [], displayDescriptions: [],
      maxDays: 1, slaType: 'F', script: '',
      billingAddressNotRequired: false, deliveryAddressNotRequired: false,
      supportedPaymentGateways: []
    };
  }

  ngOnInit() {
    console.debug('ShippingComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    console.debug('ShippingComponent ngOnDestroy');
    if (this.shopAllSub) {
      this.shopAllSub.unsubscribe();
    }
  }


  getAllCarriers() {
    var _sub:any = this._shippingService.getAllCarriers().subscribe( allcarriers => {
      console.debug('ShippingComponent getAllCarriers', allcarriers);
      this.carriers = allcarriers;
      this.selectedCarrier = null;
      this.carrierEdit = null;
      this.viewMode = ShippingComponent.CARRIERS;
      this.changed = false;
      this.validForSave = false;
      _sub.unsubscribe();
    });
  }

  getAllSlas() {
    if (this.selectedCarrier != null) {
      var _sub:any = this._shippingService.getCarrierSlas(this.selectedCarrier.carrierId).subscribe(allslas => {
        console.debug('ShippingComponent getCarrierSlas', allslas);
        this.slas = allslas;
        this.selectedSla = null;
        this.slaEdit = null;
        this.carrierEdit = null;
        this.viewMode = ShippingComponent.SLAS;
        this.changed = false;
        this.validForSave = false;
        _sub.unsubscribe();

        let lang = I18nEventBus.getI18nEventBus().current();
        var _sub2:any = this._paymentService.getPaymentGateways(lang).subscribe(allpgs => {
          console.debug('ShippingComponent getPaymentGateways', allpgs);
          this.pgs = allpgs;
          _sub2.unsubscribe();
        });

      });
    }
  }

  protected onRefreshHandler() {
    console.debug('ShippingComponent refresh handler');
    if (this.viewMode === ShippingComponent.CARRIERS ||
        this.viewMode === ShippingComponent.CARRIER ||
        this.selectedCarrier == null) {
      this.getAllCarriers();
    } else {
      this.getAllSlas();
    }
  }

  onCarrierSelected(data:CarrierVO) {
    console.debug('ShippingComponent onCarrierSelected', data);
    this.selectedCarrier = data;
    this.slaFilter = '';
  }

  onCarrierChanged(event:FormValidationEvent<CarrierVO>) {
    console.debug('ShippingComponent onCarrierChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.carrierEdit = event.source;
  }

  onSlaSelected(data:CarrierSlaVO) {
    console.debug('ShippingComponent onSlaSelected', data);
    this.selectedSla = data;
  }

  onSlaChanged(event:FormValidationEvent<CarrierSlaVO>) {
    console.debug('ShippingComponent onSlaChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.slaEdit = event.source;
  }

  protected onBackToList() {
    console.debug('ShippingComponent onBackToList handler');
    if (this.viewMode === ShippingComponent.SLA) {
      this.slaEdit = null;
      this.viewMode = ShippingComponent.SLAS;
    } else if (this.viewMode === ShippingComponent.SLAS) {
      this.slaEdit = null;
      this.selectedSla = null;
      this.viewMode = ShippingComponent.CARRIERS;
    } else if (this.viewMode === ShippingComponent.CARRIER) {
      this.carrierEdit = null;
      this.slaEdit = null;
      this.selectedSla = null;
      this.viewMode = ShippingComponent.CARRIERS;
    }
  }

  protected onRowNew() {
    console.debug('ShippingComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ShippingComponent.CARRIERS) {
      this.carrierEdit = this.newCarrierInstance();
      this.viewMode = ShippingComponent.CARRIER;
    } else if (this.viewMode === ShippingComponent.SLAS) {
      this.slaEdit = this.newSlaInstance();
      this.viewMode = ShippingComponent.SLA;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('ShippingComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedSla != null) {
      this.onRowDelete(this.selectedSla);
    } else if (this.selectedCarrier != null) {
      this.onRowDelete(this.selectedCarrier);
    }
  }


  protected onRowEditCarrier(row:CarrierVO) {
    console.debug('ShippingComponent onRowEditCarrier handler', row);
    this.carrierEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = ShippingComponent.CARRIER;
  }

  protected onRowEditSla(row:CarrierSlaVO) {
    console.debug('ShippingComponent onRowEditSla handler', row);
    this.slaEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = ShippingComponent.SLA;
  }

  protected onRowEditSelected() {
    if (this.selectedSla != null) {
      this.onRowEditSla(this.selectedSla);
    } else if (this.selectedCarrier != null) {
      this.onRowEditCarrier(this.selectedCarrier);
    }
  }


  protected onRowList(row:CarrierVO) {
    console.debug('ShippingComponent onRowList handler', row);
    this.getAllSlas();
  }


  protected onRowListSelected() {
    if (this.selectedCarrier != null) {
      this.onRowList(this.selectedCarrier);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

     if (this.slaEdit != null) {

        console.debug('ShippingComponent Save handler sla', this.slaEdit);

        var _sub:any = this._shippingService.saveCarrierSla(this.slaEdit).subscribe(
            rez => {
            if (this.slaEdit.carrierslaId > 0) {
              let idx = this.slas.findIndex(rez => rez.carrierslaId == this.slaEdit.carrierslaId);
              if (idx !== -1) {
                this.slas[idx] = rez;
                this.slas = this.slas.slice(0, this.slas.length); // reset to propagate changes
                console.debug('ShippingComponent sla changed', rez);
              }
            } else {
              this.slas.push(rez);
              this.slaFilter = rez.name;
              console.debug('ShippingComponent sla added', rez);
            }
            this.changed = false;
            this.selectedSla = rez;
            this.slaEdit = null;
            this.viewMode = ShippingComponent.SLAS;
            _sub.unsubscribe();
          }
        );
      } else if (this.carrierEdit != null) {

        console.debug('ShippingComponent Save handler carrier', this.carrierEdit);

        var _sub:any = this._shippingService.saveCarrier(this.carrierEdit).subscribe(
            rez => {
            if (this.carrierEdit.carrierId > 0) {
              let idx = this.carriers.findIndex(rez => rez.carrierId == this.carrierEdit.carrierId);
              if (idx !== -1) {
                this.carriers[idx] = rez;
                this.carriers = this.carriers.slice(0, this.carriers.length); // reset to propagate changes
                console.debug('ShippingComponent carrier changed', rez);
              }
            } else {
              this.carriers.push(rez);
              this.carrierFilter = rez.name;
              console.debug('ShippingComponent carrier added', rez);
            }
            this.changed = false;
            this.selectedCarrier = rez;
            this.carrierEdit = null;
            this.viewMode = ShippingComponent.CARRIERS;
            _sub.unsubscribe();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('ShippingComponent discard handler');
    if (this.viewMode === ShippingComponent.SLA) {
      if (this.selectedSla != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
    if (this.viewMode === ShippingComponent.CARRIER) {
      if (this.selectedCarrier != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShippingComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

     if (this.selectedSla != null) {
        console.debug('ShippingComponent onDeleteConfirmationResult', this.selectedSla);

        var _sub:any = this._shippingService.removeCarrierSla(this.selectedSla).subscribe(res => {
          console.debug('ShippingComponent removeSla', this.selectedSla);
          let idx = this.slas.indexOf(this.selectedSla);
          this.slas.splice(idx, 1);
          this.slas = this.slas.slice(0, this.slas.length); // reset to propagate changes
          this.selectedSla = null;
          this.slaEdit = null;
          _sub.unsubscribe();
        });

      } else if (this.selectedCarrier != null) {
        console.debug('ShippingComponent onDeleteConfirmationResult', this.selectedCarrier);

        var _sub:any = this._shippingService.removeCarrier(this.selectedCarrier).subscribe(res => {
          console.debug('ShippingComponent removeCarrier', this.selectedCarrier);
          let idx = this.carriers.indexOf(this.selectedCarrier);
          this.carriers.splice(idx, 1);
          this.carriers = this.carriers.slice(0, this.carriers.length); // reset to propagate changes
          this.selectedCarrier = null;
          this.carrierEdit = null;
          _sub.unsubscribe();
        });
      }
    }
  }


}

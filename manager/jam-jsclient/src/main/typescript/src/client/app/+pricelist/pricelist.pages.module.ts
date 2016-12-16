import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { PriceListComponent, PromotionComponent, PromotionCouponsComponent, PromotionsComponent, TaxConfigsComponent, TaxesComponent } from './components/index';
import { ShopPriceListComponent, ShopPromotionsComponent, ShopTaxesComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      PriceListComponent, PromotionComponent, PromotionCouponsComponent, PromotionsComponent, TaxConfigsComponent, TaxesComponent,
      ShopPriceListComponent, ShopPromotionsComponent, ShopTaxesComponent,
    ],
    exports: [
      PriceListComponent, PromotionComponent, PromotionCouponsComponent, PromotionsComponent, TaxConfigsComponent, TaxesComponent,
      ShopPriceListComponent, ShopPromotionsComponent, ShopTaxesComponent,
    ]
})

export class PriceListPagesModule { }

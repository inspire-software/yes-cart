import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { TranslateModule } from '@ngx-translate/core';

import { YcDatePipe, YcDateTimePipe, YcQuantityPipe, YcPricePipe } from './pipes/index';

import { ServicesModule } from './services/services.module';

import { PaginationModule, AccordionModule, TabsModule, BsDatepickerModule } from 'ngx-bootstrap';

import { ModalComponent } from './modal/index';

import { I18nComponent, DateTimeComponent, SortSelectComponent } from './common/index';

import { BrandSelectComponent, CategorySelectComponent, CategoryMinSelectComponent, ProductTypeSelectComponent, ProductSelectComponent, ProductSkuSelectComponent } from './catalog/index';

import { ContentSelectComponent, ContentMinSelectComponent, MailPreviewComponent } from './content/index';

import { FulfilmentCentreSelectComponent, InventoryInfoComponent } from './fulfilment/index';

import { FileSelectComponent, DataGroupSelectComponent } from './impex/index';

import { AttributeValuesComponent, ProductAttributeSelectComponent, ProductAttributeUsageComponent } from './attributes/index';

import { SidebarComponent, TopbarComponent, DataControlComponent } from './sidebar/index';
import { ErrorModalComponent } from './error/index';
import { LicenseComponent, LicenseModalComponent } from './license/index';

import { LoginModalComponent } from './auth/index';

import { TreeViewComponent } from './tree-view/index';

import { CarrierSlaSelectComponent, CountrySelectComponent, CountryStateSelectComponent } from './shipping/index';

import { ShopSelectComponent } from './shop/index';

import { CurrencySelectComponent, TaxSelectComponent } from './price/index';

/**
 * Do not specify providers for modules that might be imported by a lazy loaded module.
 */

@NgModule({
  imports: [
    CommonModule, RouterModule,
    FormsModule, ReactiveFormsModule,
    TranslateModule, ServicesModule,
    BrowserAnimationsModule,
    AccordionModule.forRoot(), PaginationModule.forRoot(), TabsModule.forRoot(), BsDatepickerModule.forRoot()
  ],
  declarations: [
    YcDatePipe, YcDateTimePipe, YcQuantityPipe, YcPricePipe,
    ModalComponent,
    I18nComponent, DateTimeComponent, SortSelectComponent,
    BrandSelectComponent, CategorySelectComponent, CategoryMinSelectComponent, ProductTypeSelectComponent, ProductSelectComponent, ProductSkuSelectComponent,
    ContentSelectComponent, ContentMinSelectComponent, MailPreviewComponent,
    FulfilmentCentreSelectComponent, InventoryInfoComponent,
    FileSelectComponent, DataGroupSelectComponent,
    AttributeValuesComponent, ProductAttributeSelectComponent, ProductAttributeUsageComponent,
    SidebarComponent, TopbarComponent, DataControlComponent,
    ErrorModalComponent,
    LicenseComponent, LicenseModalComponent,
    CarrierSlaSelectComponent,
    CountrySelectComponent, CountryStateSelectComponent,
    ShopSelectComponent,
    CurrencySelectComponent, TaxSelectComponent,
    TreeViewComponent,
    LoginModalComponent,
  ],
  exports: [
    YcDatePipe, YcDateTimePipe, YcQuantityPipe, YcPricePipe,
    ModalComponent,
    I18nComponent, DateTimeComponent, SortSelectComponent,
    BrandSelectComponent, CategorySelectComponent, CategoryMinSelectComponent, ProductTypeSelectComponent, ProductSelectComponent, ProductSkuSelectComponent,
    ContentSelectComponent, ContentMinSelectComponent, MailPreviewComponent,
    FulfilmentCentreSelectComponent, InventoryInfoComponent,
    FileSelectComponent, DataGroupSelectComponent,
    AttributeValuesComponent, ProductAttributeSelectComponent, ProductAttributeUsageComponent,
    SidebarComponent, TopbarComponent, DataControlComponent,
    ErrorModalComponent,
    LicenseComponent, LicenseModalComponent,
    CarrierSlaSelectComponent,
    CountrySelectComponent, CountryStateSelectComponent,
    ShopSelectComponent,
    CurrencySelectComponent, TaxSelectComponent,
    TreeViewComponent,
    CommonModule, RouterModule,
    FormsModule, ReactiveFormsModule,
    TranslateModule,
    LoginModalComponent,
    AccordionModule, PaginationModule, TabsModule, BsDatepickerModule
  ]
})
export class SharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SharedModule,
      providers: [ ]
    };
  }
}

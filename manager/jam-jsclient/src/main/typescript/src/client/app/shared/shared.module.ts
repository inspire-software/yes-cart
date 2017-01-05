import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { TranslateModule } from 'ng2-translate/bundles/index';

import { YcDatePipe, YcDateTimePipe, YcQuantityPipe, YcPricePipe } from './pipes/index';

import { ServicesModule } from './services/services.module';

import { PaginationModule } from 'ng2-bootstrap/components/pagination';
import { AccordionModule } from 'ng2-bootstrap/components/accordion';
import { TabsModule } from 'ng2-bootstrap/components/tabs';

import { ModalComponent } from './modal/index';

import { I18nComponent } from './i18n/index';

import { BrandSelectComponent } from './catalog/index';
import { CategorySelectComponent } from './catalog/index';
import { ProductTypeSelectComponent } from './catalog/index';

import { ContentSelectComponent } from './content/index';

import { FulfilmentCentreSelectComponent, InventoryInfoComponent } from './fulfilment/index';

import { FileSelectComponent, DataGroupSelectComponent } from './impex/index';

import { AttributeValuesComponent, ProductAttributeSelectComponent, ProductAttributeUsageComponent } from './attributes/index';

import { SidebarComponent, DataControlComponent } from './sidebar/index';
import { ErrorModalComponent } from './error/index';
import { LicenseComponent, LicenseModalComponent  } from './license/index';

import { TreeViewComponent  } from './tree-view/index';

import { ShopSelectComponent  } from './shop/index';

import { CurrencySelectComponent  } from './price/index';

/**
 * Do not specify providers for modules that might be imported by a lazy loaded module.
 */

@NgModule({
  imports: [
    CommonModule, RouterModule,
    FormsModule, ReactiveFormsModule,
    TranslateModule, ServicesModule,
    AccordionModule, PaginationModule, TabsModule
  ],
  declarations: [
    YcDatePipe, YcDateTimePipe, YcQuantityPipe, YcPricePipe,
    ModalComponent,
    I18nComponent,
    BrandSelectComponent, CategorySelectComponent, ProductTypeSelectComponent,
    ContentSelectComponent,
    FulfilmentCentreSelectComponent, InventoryInfoComponent,
    FileSelectComponent, DataGroupSelectComponent,
    AttributeValuesComponent, ProductAttributeSelectComponent, ProductAttributeUsageComponent,
    SidebarComponent, DataControlComponent,
    ErrorModalComponent,
    LicenseComponent, LicenseModalComponent,
    ShopSelectComponent,
    CurrencySelectComponent,
    TreeViewComponent,
  ],
  exports: [
    YcDatePipe, YcDateTimePipe, YcQuantityPipe, YcPricePipe,
    ModalComponent,
    I18nComponent,
    BrandSelectComponent, CategorySelectComponent, ProductTypeSelectComponent,
    ContentSelectComponent,
    FulfilmentCentreSelectComponent, InventoryInfoComponent,
    FileSelectComponent, DataGroupSelectComponent,
    AttributeValuesComponent, ProductAttributeSelectComponent, ProductAttributeUsageComponent,
    SidebarComponent, DataControlComponent,
    ErrorModalComponent,
    LicenseComponent, LicenseModalComponent,
    ShopSelectComponent,
    CurrencySelectComponent,
    TreeViewComponent,
    CommonModule, RouterModule,
    FormsModule, ReactiveFormsModule,
    TranslateModule,
    AccordionModule, PaginationModule, TabsModule
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

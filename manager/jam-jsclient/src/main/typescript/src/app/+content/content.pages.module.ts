import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { ContentRoutingModule } from './content-routing.module';
import { ContentComponent, ContentsComponent, ShopMailTemplatesComponent } from './components/index';
import { ShopContentComponent } from './index';

@NgModule({
    imports: [ContentRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      ContentComponent, ContentsComponent, ShopMailTemplatesComponent,
      ShopContentComponent
    ],
    exports: [
      ContentComponent, ContentsComponent, ShopMailTemplatesComponent,
      ShopContentComponent
    ]
})

export class ContentPagesModule { }

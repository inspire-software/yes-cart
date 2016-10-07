import { RouterConfig } from '@angular/router';

import { ShopContentComponent } from './index';

export const ContentRoutes: RouterConfig = [
  {
    path: 'content/:shopId',
    component: ShopContentComponent
  }
];

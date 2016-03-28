import {bootstrap}    from 'angular2/platform/browser';
import {HTTP_PROVIDERS} from 'angular2/http';
import {AppCmp} from './app';
import {ShopService} from '../../service/shop_service';

bootstrap(AppCmp, [HTTP_PROVIDERS, ShopService]);

package org.yes.cart.web.constants;

import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/17/11
 * Time: 2:34 PM
 */
public interface ManagedBeanELNames {

    /**
     * Application director
     */
    String EL_APPLICATION_DYNAMYC_CACHE = "#{" + WebParametersKeys.APPLICATION_DYNAMYC_CACHE + "}";

    /**
     * Currency switch request bean.
     */
    String EL_REQUEST_CURRENCY = "#{" + WebParametersKeys.REQUEST_CURRENCY + "}";

    /**
     * Session shoping cart.
     */
    String EL_SESSION_SHOPPING_CART = "#{" + WebParametersKeys.SESSION_SHOPPING_CART + "}";


    //----------------------------------------------------- spring services on web layer ----------------------------//

    String EL_CURRENCY_SYMBOL_SERVICE = "#{currencySymbolService}";

    /** Shop service EL name.  */
    String EL_SHOP_SERVICE = "#{" + ServiceSpringKeys.SHOP_SERVICE + "}";

    /** Category service EL name. */
    String EL_CATEGORY_SERVICE = "#{" + ServiceSpringKeys.CATEGORY_SERVICE + "}";


}

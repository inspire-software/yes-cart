package org.yes.cart.web.facelet;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.ShopUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * Represent top level categories container for current shop.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 3:15 PM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_TOP_LEVEL_CATEGORIES)
@RequestScoped
public class TopLevelCategories extends BaseFacelet {

    /**
     * Get the top level categories for current shop.
     * @return  list of top level categories.
     */
    public List<Category> getCategoies() {
        return getCategoryService().getTopLevelCategories(ShopUtil.getShop());
    }
}

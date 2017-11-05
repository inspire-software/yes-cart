package org.yes.cart.domain.dto.impl;

import org.yes.cart.domain.dto.ProductSearchResultNavDTO;
import org.yes.cart.domain.dto.ProductSearchResultNavItemDTO;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.*;

/**
 * User: denispavlov
 * Date: 03/11/2017
 * Time: 10:58
 */
public class ProductSearchResultNavDTOImpl implements ProductSearchResultNavDTO {

    private final Map<String, List<ProductSearchResultNavItemDTO>> data = new HashMap<String, List<ProductSearchResultNavItemDTO>>();

    public ProductSearchResultNavDTOImpl(final Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> data) {
        if (data != null) {
            for (final Map.Entry<String, List<Pair<Pair<String, I18NModel>, Integer>>> entry : data.entrySet()) {
                final List<ProductSearchResultNavItemDTO> list = new ArrayList<ProductSearchResultNavItemDTO>();
                this.data.put(entry.getKey(), list);
                for (final Pair<Pair<String, I18NModel>, Integer> entryItem : entry.getValue()) {
                    list.add(new ProductSearchResultNavItemDTOImpl(entryItem));
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getNavCodes() {
        return this.data.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductSearchResultNavItemDTO> getItems(final String code) {
        return this.data.get(code);
    }
}

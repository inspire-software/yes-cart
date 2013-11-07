/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.promotion.impl.action;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 06/11/2013
 * Time: 18:01
 */
public class CustomerTagPromotionAction extends AbstractPromotionAction {

    /** {@inheritDoc} */
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        final Promotion promotion = getPromotion(context);
        return new BigDecimal(Integer.MAX_VALUE - promotion.getRank());
    }

    /** {@inheritDoc} */
    public void perform(final Map<String, Object> context) {

        final String tag = getRawPromotionActionContext(context);
        final Customer customer = getCustomer(context);

        if (StringUtils.isNotBlank(tag) && customer != null) {

            final Set<String> tags = new TreeSet<String>();
            // Add existing tags - do not use the getCustomerTags() as this is the initial tags
            if (StringUtils.isNotBlank(customer.getTag())) {
                tags.addAll(Arrays.asList(StringUtils.split(customer.getTag(), ' ')));
            }
            // Add new tags from this promo
            tags.addAll(Arrays.asList(StringUtils.split(tag, ' ')));

            customer.setTag(StringUtils.join(tags, ' '));

        }
    }
}

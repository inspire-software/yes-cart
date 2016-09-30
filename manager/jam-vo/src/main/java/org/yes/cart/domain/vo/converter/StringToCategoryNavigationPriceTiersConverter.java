/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo.converter;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.entity.xml.CategoryPriceNavigationXStreamProvider;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierNodeImpl;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierTreeImpl;
import org.yes.cart.domain.vo.VoCategoryNavigationPriceTier;
import org.yes.cart.domain.vo.VoCategoryNavigationPriceTiers;
import org.yes.cart.stream.xml.XStreamProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 30/08/2016
 * Time: 12:04
 */
public class StringToCategoryNavigationPriceTiersConverter implements ValueConverter {

    private static final XStreamProvider<PriceTierTree> xStreamProvider = new CategoryPriceNavigationXStreamProvider();

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {

        final CategoryDTO dto = (CategoryDTO) object;
        final VoCategoryNavigationPriceTiers tiers = new VoCategoryNavigationPriceTiers();
        if (StringUtils.isNotBlank(dto.getNavigationByPriceTiers())) {
            final List<MutablePair<String, List<VoCategoryNavigationPriceTier>>> voTiers = new ArrayList<>();
            final PriceTierTree priceTierTree = xStreamProvider.fromXML(dto.getNavigationByPriceTiers());
            for (final String currency : priceTierTree.getSupportedCurrencies()) {

                final List<VoCategoryNavigationPriceTier> voTierItems = new ArrayList<>();

                for (final PriceTierNode node : priceTierTree.getPriceTierNodes(currency)) {

                    final VoCategoryNavigationPriceTier tier = new VoCategoryNavigationPriceTier();
                    tier.setFrom(node.getFrom());
                    tier.setTo(node.getTo());
                    voTierItems.add(tier);

                }

                if (!voTierItems.isEmpty()) {
                    voTiers.add(new MutablePair<>(currency, voTierItems));
                }

            }
            if (!voTiers.isEmpty()) {
                tiers.setTiers(voTiers);
            }
        }
        return tiers;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {

        final String nav;
        final VoCategoryNavigationPriceTiers tiers = (VoCategoryNavigationPriceTiers) object;
        if (tiers != null && CollectionUtils.isNotEmpty(tiers.getTiers())) {

            final PriceTierTree priceTierTree = new PriceTierTreeImpl();
            for (final MutablePair<String, List<VoCategoryNavigationPriceTier>> tier : tiers.getTiers()) {

                if (StringUtils.isNotBlank(tier.getFirst()) && CollectionUtils.isNotEmpty(tier.getSecond())) {

                    final List<PriceTierNode> nodes = new ArrayList<>();
                    for (final VoCategoryNavigationPriceTier tierItem : tier.getSecond()) {

                        final PriceTierNode node = new PriceTierNodeImpl();
                        node.setFrom(tierItem.getFrom());
                        node.setTo(tierItem.getTo());
                        nodes.add(node);

                    }

                    priceTierTree.addPriceTierNode(tier.getFirst(), nodes);
                }
            }

            nav = xStreamProvider.toXML(priceTierTree);
        } else {
            nav = null;
        }
        final CategoryDTO dto = (CategoryDTO) oldEntity;
        dto.setNavigationByPriceTiers(nav);
        return nav;
    }

}

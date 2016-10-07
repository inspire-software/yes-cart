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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.entity.xml.ProductTypeRangeListXStreamProvider;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.navigation.range.DisplayValue;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.domain.misc.navigation.range.impl.DisplayValueImpl;
import org.yes.cart.domain.misc.navigation.range.impl.RangeListImpl;
import org.yes.cart.domain.misc.navigation.range.impl.RangeNodeImpl;
import org.yes.cart.domain.vo.VoProductTypeAttrNavigationRange;
import org.yes.cart.domain.vo.VoProductTypeAttrNavigationRanges;
import org.yes.cart.stream.xml.XStreamProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 22/08/2016
 * Time: 10:31
 */
public class StringToProductTypeAttrNavigationRangesConverter implements ValueConverter {

    private static final XStreamProvider<RangeList> xStreamProvider = new ProductTypeRangeListXStreamProvider();

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {

        final ProductTypeAttrDTO dto = (ProductTypeAttrDTO) object;
        final VoProductTypeAttrNavigationRanges ranges = new VoProductTypeAttrNavigationRanges();
        if (StringUtils.isNotBlank(dto.getRangeNavigation())) {
            final List<VoProductTypeAttrNavigationRange> voRanges = new ArrayList<>();
            final RangeList rangeList = xStreamProvider.fromXML(dto.getRangeNavigation());
            if (rangeList.getRanges() != null) {
                for (final RangeNode item : rangeList.getRanges()) {
                    final VoProductTypeAttrNavigationRange range = new VoProductTypeAttrNavigationRange();
                    range.setRange(item.getFrom().concat("-").concat(item.getTo()));
                    final List<DisplayValue> displayValues = item.getI18n();
                    final List<MutablePair<String, String>> names = new ArrayList<>();
                    if (displayValues != null) {
                        for (final DisplayValue displayValue : displayValues) {
                            names.add(MutablePair.of(displayValue.getLang(), displayValue.getValue()));
                        }
                    }
                    if (!names.isEmpty()) {
                        range.setDisplayVals(names);
                    }
                    voRanges.add(range);
                }
            }
            if (!voRanges.isEmpty()) {
                ranges.setRanges(voRanges);
            }
        }
        return ranges;
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {

        final VoProductTypeAttrNavigationRanges ranges = (VoProductTypeAttrNavigationRanges) object;
        final RangeList rangeList = new RangeListImpl();
        if (ranges.getRanges() != null) {
            final List<RangeNode> rangeNodes = new ArrayList<>();
            for (final VoProductTypeAttrNavigationRange range : ranges.getRanges()) {
                if (StringUtils.isNotBlank(range.getRange()) && range.getRange().contains("-")) {
                    final RangeNode node = new RangeNodeImpl();
                    final String[] fromAndTo = StringUtils.split(range.getRange(), '-');
                    node.setFrom(fromAndTo[0]);
                    node.setTo(fromAndTo[1]);
                    if (range.getDisplayVals() != null) {
                        final List<DisplayValue> displayValues = new ArrayList<>();
                        for (final MutablePair<String, String> pair : range.getDisplayVals()) {
                            displayValues.add(new DisplayValueImpl(pair.getFirst(), pair.getSecond()));
                        }
                        if (!displayValues.isEmpty()) {
                            node.setI18n(displayValues);
                        }
                    }
                    rangeNodes.add(node);
                }
            }
            if (!rangeNodes.isEmpty()) {
                rangeList.setRanges(rangeNodes);
            }
        }
        final String nav;
        if (rangeList.getRanges() != null) {
            nav = xStreamProvider.toXML(rangeList);
        } else {
            nav = null;
        }
        final ProductTypeAttrDTO dto = (ProductTypeAttrDTO) oldEntity;
        dto.setRangeNavigation(nav);
        return nav;
    }
}

package org.yes.cart.web.page.component.filterednavigation;

import org.apache.lucene.search.BooleanQuery;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.AttributiveSearchQueryBuilderImpl;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * Product attribute value filtering component.
 * Supported simple value and value ranges, for example
 * product color can be one of Black, Red, White and
 * weight can be filtered within some range from 1 to 3 Kg, 3 - 5 Kg, 5 - 10 Kg
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/30/11
 * Time: 2:57 PM
 */
public class AttributeProductFilter extends AbstractProductFilter {

    private boolean filteredNavigationByAttribute = false;

    /**
     * Construct attributive filtering component.
     * @param id         panel id
     * @param query      current query.
     * @param categoryId current category id
     */
    public AttributeProductFilter(final String id, final BooleanQuery query, final long categoryId) {

        super(id, query, categoryId);

        if (categoryId > 0) {

            filteredNavigationByAttribute = getCategory().getNavigationByAttributes() == null ? false : getCategory().getNavigationByAttributes();

            final ProductType productType = getCategory().getProductType();

            if (filteredNavigationByAttribute  && productType != null) {

                setNavigationRecords(
                        getFilteredNagigationRecords(
                                getProductService().getDistinctAttributeValues(productType.getProducttypeId())
                        )
                );

            }
        }

    }


    /**
     * {@inheritDoc}
     */
    List<FiteredNavigationRecord> getFilteredNagigationRecords(List<FiteredNavigationRecord> allNavigationRecords) {

        final AttributiveSearchQueryBuilderImpl queryBuilder = new AttributiveSearchQueryBuilderImpl();

        final List<FiteredNavigationRecord> navigationList = new ArrayList<FiteredNavigationRecord>();

        for (FiteredNavigationRecord record : allNavigationRecords) {

            if (!isAttributeAlreadyFiltered(ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD + ":" + record.getCode())) {

                final BooleanQuery candidateQuery = getQueryCandidate(queryBuilder, record);

                final int candidateResultCount = getProductService().getProductQty(candidateQuery);

                if (candidateResultCount > 0) {
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }

            }

        }

        return navigationList;

    }

    private BooleanQuery getQueryCandidate(final AttributiveSearchQueryBuilderImpl queryBuilder, final FiteredNavigationRecord record) {

        final Map map;
        final BooleanQuery booleanQuery;

        if ("S".equals(record.getType())) {
            map = Collections.singletonMap(record.getCode(), record.getValue());
            booleanQuery = queryBuilder.createQuery(getCategories(), map);
        } else { // range navigarion
            String [] range = record.getValue().split("-");
            map = Collections.singletonMap(
                    record.getCode(),
                    new Pair<String,String>(range[0],range[1])
            );
            booleanQuery = queryBuilder.createQueryWithRangeValues(getCategories(), map);
        }

        return getLuceneQueryFactory().getSnowBallQuery(
                getQuery(),
                booleanQuery
        );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {
        return  /*super.isPanelVisible()
                && */filteredNavigationByAttribute
                && getNavigationRecords() != null
                && !getNavigationRecords().isEmpty();
    }
}

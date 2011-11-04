package org.yes.cart.web.support.util;

import org.hibernate.criterion.Criterion;
import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.AttributeService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:27:53 AM
 */
@Ignore
public class TestNavigationUtil {

    private final AttributeService attributeService = new AttributeService() {


        public AttrValue removeAttrValue(List<AttrValue> values, final String attrName) {
            return null;
        }

        public List<AttrValue> removeAttrValues(final List<Pair<String, List<AttrValue>>> fromList, final String sectionName) {
            return null;
        }

        public List<Pair<String, List<AttrValue>>> merge(final List<Pair<String, List<AttrValue>>> to, final List<Pair<String, List<AttrValue>>> from) {
            return null;
        }

        public GenericDAO getGenericDao() {
            return null;
        }

        public List<Attribute> findByCriteria(final Criterion... criterion) {
            return null;
        }

        public Attribute findSingleByCriteria(final Criterion... criterion) {
            return null;
        }

        public List<Attribute> findAll() {
            return null;
        }

        public Attribute getById(long pk) {
            return null;
        }

        public Attribute create(Attribute instance) {
            return null;
        }

        public Attribute update(Attribute instance) {
            return null;
        }

        public void delete(Attribute instance) {

        }

        public Map<String, String> getAttributeNamesByCodes(List<String> codes) {
            return null;
        }

        public List<Attribute> findByAttributeGroupCode(String attributeGroupCode) {
            return null;
        }

        public Attribute findByAttributeCode(final String attributeCode) {
            return null;
        }

        public List<Attribute> findAvailableAttributes(final String attributeGroupCode, final List<String> assignedAttributeCodes) {
            return null;
        }

        public List<Attribute> findAttributesWithMultipleValues(final String attributeGroupCode) {
            return null;
        }


        public List<String> getAllAttributeCodes() {
            String[] obj = new String[]{"ATTR1", "ATTR2", "ATTR3", "ATTR4", "ATTR5", "ATTR6", "MATERIAL", "COLOR", "WIGTH"};
            return Arrays.asList(obj);
        }

    };


    @Test
    public void testGetFilteredRequestParameters() {
        /*LinkedHashMap params = new LinkedHashMap();

        params.put("ATTR5", "a");
        params.put("ATTR2", "b");
        params.put("ATTR3", "c");
        params.put("ATTR4", "d");
        params.put("ATTR1", "e");
        params.put("long_live_robots", "ImAmNotAQueryCookies:Some_val");

        params.toString();

        LinkedHashMap valueMap = NavigationUtil.getFilteredRequestParameters(
                params,
                attributeService.getAllAttributeCodes());

        assertEquals(1, valueMap.size());
        assertNotNull(valueMap.get("long_live_robots"));

        valueMap = NavigationUtil.getRetainedRequestParameters(
                params,
                attributeService.getAllAttributeCodes());

        assertEquals(5, valueMap.size());
        assertNotNull(valueMap.get("ATTR1"));
        assertNotNull(valueMap.get("ATTR2"));
        assertNotNull(valueMap.get("ATTR3"));
        assertNotNull(valueMap.get("ATTR4"));
        assertNotNull(valueMap.get("ATTR5")); */


    }


    /**
     * Test, that only allowed fileds will be in lucene queries.
     */
    /*public void testGetSnowBallQueryWithComplexQueries() {

        List<Cookie> cookies = new ArrayList<Cookie>();
        cookies.add(new Cookie(WebParametersKeys.FILTERED_NAV_QUERY_PREFIX+"a", "+productCategory.category:200 +attribute.attribute:MATERIAL +attribute.val:Stone"));

        List<Cookie> result = NavigationUtil.getFilteredNavigationQueryChain(cookies.toArray(new Cookie[cookies.size()]));


        Query query = NavigationUtil.getSnowBallQuery(result, "+productCategory.category:200 +attribute.attribute:COLOR +attribute.val:Red");
        assertEquals("+(+productCategory.category:200 +attribute.attribute:MATERIAL +attribute.val:Stone) +(+productCategory.category:200 +attribute.attribute:COLOR +attribute.val:Red)", query.toString());
    }  */


}

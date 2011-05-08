/*
 * Copyright (c) 2010. The intellectual rights for this code remain to the NPA developer team.
 * Code distribution, sale or modification is prohibited unless authorized by all members of NPA
 * development team.
 */

package org.yes.cart.domain.entity.bridge;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.TestCase;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierNodeImpl;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierTreeImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 *
 * This test just prove, that we can serialize / deserialize Price Tier Object.
 *
 */
public class PriceTierTreeTest  extends TestCase {

    private XStream getXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias(Category.PRICE_TREE_ALIAS, PriceTierTreeImpl.class);
        xStream.alias(Category.PRICE_NODE_ALIAS, PriceTierNodeImpl.class);
        return xStream;
    }

    public void testXmlSerialization() {

        PriceTierTree tree = new PriceTierTreeImpl();

        List<PriceTierNode> list = new ArrayList<PriceTierNode>();
        list.add(new PriceTierNodeImpl(BigDecimal.ZERO, BigDecimal.ONE));
        list.add(new PriceTierNodeImpl(BigDecimal.ONE, BigDecimal.TEN));
        tree.addPriceTierNode("EUR", list);

        list = new ArrayList<PriceTierNode>();
        list.add(new PriceTierNodeImpl(BigDecimal.ONE, new BigDecimal(2)));
        list.add(new PriceTierNodeImpl(new BigDecimal(2), new BigDecimal(9)));
        tree.addPriceTierNode("USD", list);

        String result = getXStream().toXML(tree);
        assertNotNull(result);
        assertTrue(result.indexOf("USD") > -1);
        assertTrue(result.indexOf("EUR") > -1);
        assertTrue(result.indexOf("UAH") == -1);




    }

}

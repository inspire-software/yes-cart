/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.cluster.node.impl;

import org.junit.Test;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 09:59
 */
public class NodeConfigurationXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {


        final XStreamProvider<List<Node>> provider = new NodeConfigurationXStreamProvider();

        final String xml = new Scanner(new File("src/test/resources/cluster.xml")).useDelimiter("\\Z").next();
        final List<Node> cluster = provider.fromXML(xml);

        assertNotNull(cluster);
        assertEquals(3, cluster.size());

        final Node yes0 = cluster.get(0);
        assertEquals("TESTCLUSTER.YES0", yes0.getId());
        assertEquals("TESTCLUSTER", yes0.getClusterId());
        assertEquals("YES0", yes0.getNodeId());
        assertEquals("SFW", yes0.getNodeType());
        assertEquals("DEFAULT", yes0.getNodeConfig());
        assertFalse(yes0.isFtIndexDisabled());
        assertEquals("http://localhost:8080/yes-shop/services/backdoor", yes0.getChannel());

        final Node yes1 = cluster.get(1);
        assertEquals("TESTCLUSTER.YES1", yes1.getId());
        assertEquals("TESTCLUSTER", yes1.getClusterId());
        assertEquals("YES1", yes1.getNodeId());
        assertEquals("API", yes1.getNodeType());
        assertEquals("DEFAULT", yes1.getNodeConfig());
        assertTrue(yes1.isFtIndexDisabled());
        assertEquals("http://localhost:8081/yes-api/services/backdoor", yes1.getChannel());

        final Node yum = cluster.get(2);
        assertEquals("TESTCLUSTER.JAM", yum.getId());
        assertEquals("TESTCLUSTER", yum.getClusterId());
        assertEquals("JAM", yum.getNodeId());
        assertEquals("ADM", yum.getNodeType());
        assertEquals("DEFAULT", yum.getNodeConfig());
        assertTrue(yum.isFtIndexDisabled());
        assertNull(yum.getChannel());

    }
}

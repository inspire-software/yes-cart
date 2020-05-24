/*
 * Copyright 2009 Inspire-Software.com
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

        final Node node0 = cluster.get(0);
        assertEquals("TESTCLUSTER.SF0", node0.getId());
        assertEquals("TESTCLUSTER", node0.getClusterId());
        assertEquals("SF0", node0.getNodeId());
        assertEquals("SFW", node0.getNodeType());
        assertEquals("DEFAULT", node0.getNodeConfig());
        assertFalse(node0.isFtIndexDisabled());
        assertEquals("http://localhost:8080/services/connector", node0.getChannel());

        final Node node1 = cluster.get(1);
        assertEquals("TESTCLUSTER.SF1", node1.getId());
        assertEquals("TESTCLUSTER", node1.getClusterId());
        assertEquals("SF1", node1.getNodeId());
        assertEquals("API", node1.getNodeType());
        assertEquals("DEFAULT", node1.getNodeConfig());
        assertTrue(node1.isFtIndexDisabled());
        assertEquals("http://localhost:8081/api/services/connector", node1.getChannel());

        final Node admin = cluster.get(2);
        assertEquals("TESTCLUSTER.JAM", admin.getId());
        assertEquals("TESTCLUSTER", admin.getClusterId());
        assertEquals("JAM", admin.getNodeId());
        assertEquals("ADM", admin.getNodeType());
        assertEquals("DEFAULT", admin.getNodeConfig());
        assertTrue(admin.isFtIndexDisabled());
        assertNull(admin.getChannel());

    }
}

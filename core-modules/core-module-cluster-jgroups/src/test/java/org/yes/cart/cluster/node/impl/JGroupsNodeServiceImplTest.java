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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.service.domain.SystemService;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * User: denispavlov
 * Date: 16/06/2015
 * Time: 18:03
 */
public class JGroupsNodeServiceImplTest {

    private final Logger LOG = LoggerFactory.getLogger(JGroupsNodeServiceImplTest.class);

    private final Mockery context = new JUnit4Mockery();

    private boolean isTestAllowed() {
        return "true".equals(System.getProperty("testJGroupsMulticast"));
    }

    @Test
    public void testClusterNotifications() throws Exception {

        final boolean allowed = isTestAllowed();

        if (!allowed) {
            LOG.warn("\n\n" +
                    "***\n" +
                    "JGroup multicast test is DISABLED.\n" +
                    "You can enable test in /env/maven/${env}/config-cluster.properties\n" +
                    "Set:\n" +
                    "testJGroupsMulticast=true\n\n" +
                    "NOTE: The configurations used in test are in src/test/resources/yc-jgroups-udp.xml\n" +
                    "***\n\n\n");
        }

        assumeTrue(allowed);

        System.setProperty("java.net.preferIPv4Stack", "true");

        final SystemService systemService = context.mock(SystemService.class, "systemService");

        final Executor executor = new Executor() {
            @Override
            public void execute(final Runnable command) {
                new Thread(command).start();
            }
        };

        final JGroupsNodeServiceImpl ns1 = new JGroupsNodeServiceImpl(systemService, executor);
        final JGroupsNodeServiceImpl ns2 = new JGroupsNodeServiceImpl(systemService, executor);
        final JGroupsNodeServiceImpl ns3 = new JGroupsNodeServiceImpl(systemService, executor);

        final ServletContext ctx1 = context.mock(ServletContext.class, "ctx1");
        final ServletContext ctx2 = context.mock(ServletContext.class, "ctx2");
        final ServletContext ctx3 = context.mock(ServletContext.class, "ctx3");

        setExpectations(ctx1, new NodeImpl(false, "SF0", "SFW", "DEFAULT", "YCTEST", "0.0.0", "ABC", false));
        setExpectations(ctx2, new NodeImpl(false, "SF1", "API", "DEFAULT", "YCTEST", "0.0.0", "ABC", true));
        setExpectations(ctx3, new NodeImpl(false, "JAM", "ADM", "DEFAULT", "YCTEST", "0.0.0", "ABC", true));

        ns1.setServletContext(ctx1);
        Thread.sleep(200L);

        ns2.setServletContext(ctx2);
        Thread.sleep(200L);

        ns3.setServletContext(ctx3);
        Thread.sleep(200L);

        final List<Node> ns1cluster = ns1.getCluster();
        for (final Node node : ns1cluster) {
            LOG.info("NS1: {}", node);
        }
        assertEquals(3, ns1cluster.size());

        final List<Node> ns2cluster = ns2.getCluster();
        for (final Node node : ns2cluster) {
            LOG.info("NS2: {}", node);
        }
        assertEquals(3, ns2cluster.size());

        final List<Node> ns3cluster = ns3.getCluster();
        for (final Node node : ns3cluster) {
            LOG.info("NS3: {}", node);
        }
        assertEquals(3, ns3cluster.size());


        ns1.broadcast(new BasicMessageImpl("SF0", Arrays.asList("JAM"), "subj", "message1"));
        Thread.sleep(200L);

        ns2.broadcast(new BasicMessageImpl("SF1", Arrays.asList("JAM"), "subj", "message2"));
        Thread.sleep(200L);

        ns3.broadcast(new BasicMessageImpl("JAM", Arrays.asList("SF0", "SF1"), "subj", "message2"));
        Thread.sleep(200L);

        context.assertIsSatisfied();

        ns1.destroy();
        Thread.sleep(200L);


        final List<Node> ns2cluster2 = ns2.getCluster();
        for (final Node node : ns2cluster2) {
            LOG.info("NS2: {}", node);
        }
        assertEquals(2, ns2cluster2.size());


        ns2.destroy();
        Thread.sleep(200L);


        final List<Node> ns3cluster2 = ns3.getCluster();
        for (final Node node : ns3cluster2) {
            LOG.info("NS3: {}", node);
        }
        assertEquals(1, ns3cluster2.size());


        ns3.destroy();

    }

    private void setExpectations(final ServletContext ctx, final Node node) {

        final Enumeration ctxe = context.mock(Enumeration.class, ctx + "e");

        context.checking(new Expectations() {{
            oneOf(ctx).getInitParameterNames(); will(returnValue(ctxe));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.NODE_ID));
            oneOf(ctx).getInitParameter(NodeService.NODE_ID); will(returnValue(node.getNodeId()));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.NODE_TYPE));
            oneOf(ctx).getInitParameter(NodeService.NODE_TYPE); will(returnValue(node.getNodeType()));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.NODE_CONFIG));
            oneOf(ctx).getInitParameter(NodeService.NODE_CONFIG); will(returnValue(node.getNodeConfig()));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.CLUSTER_ID));
            oneOf(ctx).getInitParameter(NodeService.CLUSTER_ID); will(returnValue(node.getClusterId()));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.VERSION));
            oneOf(ctx).getInitParameter(NodeService.VERSION); will(returnValue(node.getVersion()));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.BUILD_NO));
            oneOf(ctx).getInitParameter(NodeService.BUILD_NO); will(returnValue(node.getBuildNo()));
            oneOf(ctxe).hasMoreElements(); will(returnValue(true));
            oneOf(ctxe).nextElement(); will(returnValue(NodeService.LUCENE_INDEX_DISABLED));
            oneOf(ctx).getInitParameter(NodeService.LUCENE_INDEX_DISABLED); will(returnValue(String.valueOf(node.isFtIndexDisabled())));
            oneOf(ctxe).hasMoreElements(); will(returnValue(false));
        }});
    }

}

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

        assumeTrue(isTestAllowed());

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

        setExpectations(ctx1, new NodeImpl(false, "YES0", "SFW", "DEFAULT", "YCTEST", "0.0.0", "ABC", false));
        setExpectations(ctx2, new NodeImpl(false, "YES1", "API", "DEFAULT", "YCTEST", "0.0.0", "ABC", true));
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


        ns1.broadcast(new BasicMessageImpl("YES0", Arrays.asList("JAM"), "subj", "message1"));
        Thread.sleep(200L);

        ns2.broadcast(new BasicMessageImpl("YES1", Arrays.asList("JAM"), "subj", "message2"));
        Thread.sleep(200L);

        ns3.broadcast(new BasicMessageImpl("JAM", Arrays.asList("YES0", "YES1"), "subj", "message2"));
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
            one(ctx).getInitParameterNames(); will(returnValue(ctxe));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.NODE_ID));
            one(ctx).getInitParameter(NodeService.NODE_ID); will(returnValue(node.getNodeId()));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.NODE_TYPE));
            one(ctx).getInitParameter(NodeService.NODE_TYPE); will(returnValue(node.getNodeType()));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.NODE_CONFIG));
            one(ctx).getInitParameter(NodeService.NODE_CONFIG); will(returnValue(node.getNodeConfig()));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.CLUSTER_ID));
            one(ctx).getInitParameter(NodeService.CLUSTER_ID); will(returnValue(node.getClusterId()));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.VERSION));
            one(ctx).getInitParameter(NodeService.VERSION); will(returnValue(node.getVersion()));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.BUILD_NO));
            one(ctx).getInitParameter(NodeService.BUILD_NO); will(returnValue(node.getBuildNo()));
            one(ctxe).hasMoreElements(); will(returnValue(true));
            one(ctxe).nextElement(); will(returnValue(NodeService.LUCENE_INDEX_DISABLED));
            one(ctx).getInitParameter(NodeService.LUCENE_INDEX_DISABLED); will(returnValue(String.valueOf(node.isFtIndexDisabled())));
            one(ctxe).hasMoreElements(); will(returnValue(false));
        }});
    }

}

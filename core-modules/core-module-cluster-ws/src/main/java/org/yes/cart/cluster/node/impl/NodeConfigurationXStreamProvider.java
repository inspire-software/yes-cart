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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 09:28
 */
public class NodeConfigurationXStreamProvider implements XStreamProvider<List<Node>> {

    private XStream xStream;

    /** {@inheritDoc} */
    public List<Node> fromXML(final String xml) {
        return (List<Node>) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    public List<Node> fromXML(final InputStream is) {
        return (List<Node>) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    public String toXML(final List<Node> object) {
        return provide().toXML(object);
    }


    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());
            xStream.alias("nodes", ArrayList.class);
            xStream.alias("node", NodeImpl.class);
            xStream.aliasField("node-id", NodeImpl.class, "nodeId");
            xStream.aliasField("node-type", NodeImpl.class, "nodeType");
            xStream.aliasField("node-config", NodeImpl.class, "nodeConfig");
            xStream.aliasField("cluster-id", NodeImpl.class, "clusterId");
            xStream.aliasField("lucene-index-disabled", NodeImpl.class, "ftIndexDisabled");
            xStream.aliasField("channel", NodeImpl.class, "channel");
            xStream.aliasField("version", NodeImpl.class, "version");
            xStream.aliasField("build-number", NodeImpl.class, "buildNo");
            xStream.addDefaultImplementation(NodeImpl.class, Node.class);
            xStream.addDefaultImplementation(ArrayList.class, List.class);
            this.xStream = xStream;
        }
        return this.xStream;
    }

}

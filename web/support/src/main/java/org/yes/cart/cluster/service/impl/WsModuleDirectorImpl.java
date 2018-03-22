/*
 * Copyright 2013 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.cluster.service.impl;

import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.service.ModuleDirector;

import java.util.ArrayList;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 22:36
 */
public class WsModuleDirectorImpl extends ModuleDirectorImpl implements ModuleDirector {

    private NodeService nodeService;

    public NodeService getNodeService() {
        return nodeService;
    }

    /**
     * Spring IoC.
     *
     * @param nodeService node service
     */
    public void setNodeService(final NodeService nodeService) {

        this.nodeService = nodeService;

        nodeService.subscribe("ModuleDirector.getModules", message -> new ArrayList<>(WsModuleDirectorImpl.this.getModules()));
    }

}

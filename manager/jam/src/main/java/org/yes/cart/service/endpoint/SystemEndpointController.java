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
package org.yes.cart.service.endpoint;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 22:01
 */
@Controller
@Api(value = "System", description = "System controller", tags = "system")
@RequestMapping("/system")
public interface SystemEndpointController {

    /**
     * All registered nodes in this cluster.
     *
     * E.g. if we have JAM, SF0, SF1 and SF2 nodes
     * this methods should return four nodes.
     *
     * @return node objects
     */
    @ApiOperation(value = "Retrieve cluster nodes information")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cluster", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoClusterNode> getClusterInfo() throws Exception;

    /**
     * All registered modules in this cluster's node.
     *
     * @return module objects
     */
    @ApiOperation(value = "Retrieve cluster node modules information")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cluster/{node}/modules", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoModule> getModuleInfo(@ApiParam(value = "Node ID", required = true) @PathVariable("node") String node) throws Exception;

    /**
     * All registered modules in this cluster's node.
     *
     * @return module objects
     */
    @ApiOperation(value = "Retrieve cluster configurations")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cluster/configurations", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoConfiguration> getConfigurationInfo() throws Exception;


    /**
     * Reload system configurations on all nodes.
     */
    @ApiOperation(value = "Update cluster configurations")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cluster/configurations", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoClusterNode> reloadConfigurations() throws Exception;


    /**
     * Map of supported queries by node.
     *
     * @return map of supported queries
     */
    @ApiOperation(value = "Retrieve query API supported")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<MutablePair<String, List<String>>> supportedQueries() throws Exception;


    /**
     * Execute sql and return result.
     * DML operation also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    @ApiOperation(value = "Create query request")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/query/{node}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Object[]> runQuery(@ApiParam(value = "Query", name = "vo", required = true) @RequestBody VoSystemQuery query, @ApiParam(value = "Node ID", required = true) @PathVariable("node") String node) throws Exception;


    /**
     * Get cache information.
     *
     * @return list of information per each cache per node.
     */
    @ApiOperation(value = "Retrieve cache information")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> getCacheInfo() throws Exception;

    /**
     * Evict all caches , which are represent in getCacheInfo list.
     *
     * @return state if the cache has evicted on nodes
     */
    @ApiOperation(value = "Delete all cache")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> evictAllCache() throws Exception;

    /**
     * Evict cache by name.
     *
     * @param name name of cache to evict
     *
     * @return state if the cache has evicted on nodes
     */
    @ApiOperation(value = "Delete cache")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache/{name}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> evictCache(@ApiParam(value = "Cache (all nodes)", required = true) @PathVariable("name") String name) throws Exception;

    /**
     * Enable cache statistics by name.
     *
     * @param name name of cache to evict
     */
    @ApiOperation(value = "Update cache")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cache/{name}/status", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> statusCache(@ApiParam(value = "Cache (all nodes)", required = true) @PathVariable("name") String name, @ApiParam(value = "Status", name = "vo", required = true) @RequestBody VoCacheStatus status) throws Exception;


    /**
     * Warm up all storefront servers.
     */
    @ApiOperation(value = "Create basic cache")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void warmUp() throws Exception;


    /**
     * Get index job status by token.
     *
     * @param token job token
     *
     * @return status of indexing
     */
    @ApiOperation(value = "Retrieve index status")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/index/{token}/status", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getIndexJobStatus(@ApiParam(value = "Index job token", required = true) @PathVariable("token") String token);

    /**
     * Reindex all products.
     *
     * @return status of indexing.
     */
    @ApiOperation(value = "Create index job")
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/index", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus reindexAllProducts();

    /**
     * Reindex all products.
     *
     * @param shopPk shop pk.
     *
     * @return status of indexing.
     */
    @ApiOperation(value = "Create index job")
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/index/shops/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus reindexShopProducts(@ApiParam(value = "Shop ID", required = true) @PathVariable("id") long shopPk);


}

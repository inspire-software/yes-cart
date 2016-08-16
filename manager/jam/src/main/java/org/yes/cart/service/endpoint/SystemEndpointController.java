/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.vo.VoCacheInfo;
import org.yes.cart.domain.vo.VoClusterNode;
import org.yes.cart.domain.vo.VoJobStatus;

import java.util.List;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 22:01
 */
@Controller
@RequestMapping("/system")
public interface SystemEndpointController {

    /**
     * All registered nodes in this cluster.
     *
     * E.g. if we have JAM, YES0, YES1 and YES2 nodes
     * this methods should return four nodes.
     *
     * @return node objects
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cluster", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoClusterNode> getClusterInfo() throws Exception;

    /**
     * Execute sql and return result.
     * DML operation also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/dbquery/{node}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Object[]> sqlQuery(@RequestBody String query, @PathVariable("node") String node) throws Exception;

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/hquery/{node}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Object[]> hsqlQuery(@RequestBody String query, @PathVariable("node") String node) throws Exception;

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     * @param node node on which to run query
     *
     * @return list of rows
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/ftquery/{node}", method = RequestMethod.POST, consumes = { MediaType.TEXT_PLAIN_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<Object[]> luceneQuery(@RequestBody String query, @PathVariable("node") String node) throws Exception;

    /**
     * Get cache information.
     *
     * @return list of information per each cache per node.
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> getCacheInfo() throws Exception;

    /**
     * Evict all caches , which are represent in getCacheInfo list.
     *
     * @return state if the cache has evicted on nodes
     */
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
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache/{name}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> evictCache(@PathVariable("name") String name) throws Exception;

    /**
     * Enable cache statistics by name.
     *
     * @param name name of cache to evict
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cache/statson/{name}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> enableStats(@PathVariable("name") String name) throws Exception;

    /**
     * Disable cache statistics by name.
     *
     * @param name name of cache to evict
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/cache/statsoff/{name}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoCacheInfo> disableStats(@PathVariable("name") String name) throws Exception;

    /**
     * Warm up all storefront servers.
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/cache/warmup", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    void warmUp() throws Exception;


    /**
     * Get index job status by token.
     *
     * @param token job token
     *
     * @return status of indexing
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/index/status/{token}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus getIndexJobStatus(@PathVariable("token") String token);

    /**
     * Reindex all products.
     *
     * @return status of indexing.
     */
    @Secured({"ROLE_SMADMIN"})
    @RequestMapping(value = "/index/all", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus reindexAllProducts();

    /**
     * Reindex all products.
     *
     * @param shopPk shop pk.
     *
     * @return status of indexing.
     */
    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN"})
    @RequestMapping(value = "/index/shop/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoJobStatus reindexShopProducts(@PathVariable("id") long shopPk);


}

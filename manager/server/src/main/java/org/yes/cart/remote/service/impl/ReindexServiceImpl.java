/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.remote.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.SingletonJobRunner;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.web.service.ws.client.AsyncFlexContextImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class ReindexServiceImpl extends SingletonJobRunner implements ReindexService {

    private static final Logger LOG = LoggerFactory.getLogger(ReindexServiceImpl.class);

    private final RemoteBackdoorService remoteBackdoorService;

    private final SystemService systemService;

    /**
     * Construct reindexer.
     *
     * @param executor task executor
     * @param remoteBackdoorService remote backdoor service.
     * @param systemService system service
     */
    public ReindexServiceImpl(final TaskExecutor executor,
                              final RemoteBackdoorService remoteBackdoorService,
                              final SystemService systemService) {
        super(executor);
        this.remoteBackdoorService = remoteBackdoorService;
        this.systemService = systemService;
    }

    /** {@inheritDoc} */
    public JobStatus getIndexAllStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    @Override
    protected Runnable createJobRunnable(final JobContext ctx) {

        return new Runnable() {

            private final JobStatusListener listener = ctx.getListener();
            public void run() {

                listener.notifyPing();
                try {
                    SecurityContextHolder.setContext((SecurityContext) ctx.getAttribute("security"));
                    listener.notifyMessage("Indexing stared");
                    final int cnt = remoteBackdoorService.reindexAllProducts(ctx);
                    // TODO: Need ping here too as if we get a lot of products we really only rely on the timeout
                    //       In fact we have the WS timeout, Listener timeout - so this needs to be refactored
                    //       We need a common indexing interface where we can pass in listener (or can get
                    //       incremental update out of).
                    listener.notifyMessage("Indexing completed. Indexed products count: " + cnt);
                    listener.notifyCompleted(JobStatus.Completion.OK);
                } catch (Throwable trw) {
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyCompleted(JobStatus.Completion.ERROR);
                } finally {
                    SecurityContextHolder.clearContext();
                }

            }
        };
    }

    /**
     * Reindex all products
     *
     * @return quantity product in created index.
     */
    public String reindexAllProducts() {
        return doJob(createAsyncContext());
    }

    /**
     * Reindex product by given sku code.
     *
     * @param pk          product primary key
     * @return quantity product in created index.
     */
    public int reindexProduct(long pk) {
        return remoteBackdoorService.reindexProduct(createAsyncContext(), pk);
    }

    private JobContext createAsyncContext() {

        final Map<String, Object> param = new HashMap<String, Object>();
        param.put(AsyncContext.WEB_SERVICE_URI, systemService.getBackdoorURI());

        final AsyncContext flex = new AsyncFlexContextImpl(param);

        return new JobContextImpl(true, new JobStatusListenerImpl(10000, 300000), new HashMap<String, Object>() {{
            putAll(flex.getAttributes());
            put("security", SecurityContextHolder.getContext());
        }});
    }

}

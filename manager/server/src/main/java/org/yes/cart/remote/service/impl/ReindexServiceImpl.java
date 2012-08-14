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

import flex.messaging.FlexContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.SingletonJobRunner;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.async.model.impl.JobContextImpl;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.client.BackdoorServiceClientFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class ReindexServiceImpl extends SingletonJobRunner implements ReindexService {

    private static final Logger LOG = LoggerFactory.getLogger(ReindexServiceImpl.class);

    public ReindexServiceImpl(final TaskExecutor executor) {
        super(executor);
    }

    /** {@inheritDoc} */
    public JobStatus getIndexAllStatus(final String token) {
        return getStatus(token);
    }

    /** {@inheritDoc} */
    @Override
    protected Runnable createJobRunnable(final JobContext ctx) {

        final BackdoorService backdoor = getBackdoorService(ctx.getListener().getTimeoutValue());

        return new Runnable() {

            private final JobStatusListener listener = ctx.getListener();
            private final BackdoorService backdoorService = backdoor;

            public void run() {

                listener.notifyPing();
                try {
                    listener.notifyMessage("Indexing stared");
                    final int cnt = backdoorService.reindexAllProducts();
                    listener.notifyMessage("Indexing completed. Indexed products count: " + cnt);
                    listener.notifyCompleted(JobStatus.Completion.OK);
                } catch (Throwable trw) {
                    LOG.error(trw.getMessage(), trw);
                    listener.notifyError(trw.getMessage());
                    listener.notifyCompleted(JobStatus.Completion.ERROR);
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
        return doJob(new JobContextImpl(true, new JobStatusListenerImpl(10000, 300000)));
    }

    /**
     * Reindex product by given sku code.
     *
     * @param pk          product primary key
     * @return quantity product in created index.
     */
    public int reindexProduct(long pk) {
        return getBackdoorService(15000).reindexProduct(pk);
    }


    private BackdoorServiceClientFactory backdoorServiceClientFactory = null;

    private synchronized BackdoorServiceClientFactory getBackdoorServiceClientFactory() {

        if (backdoorServiceClientFactory == null) {
            backdoorServiceClientFactory = new BackdoorServiceClientFactory();
        }

        return backdoorServiceClientFactory;

    }

    private BackdoorService getBackdoorService(final long timeout) {

        String userName = ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getName();
        //String password = (String) ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getCredentials();
        String password = (String) FlexContext.getFlexSession().getAttribute("pwd");

        return getBackdoorServiceClientFactory().getBackdoorService(
                userName,
                password,
                "http://localhost:8080/yes-shop/services/backdoor", timeout);

    }

}

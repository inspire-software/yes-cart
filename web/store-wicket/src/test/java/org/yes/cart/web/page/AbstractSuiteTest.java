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

package org.yes.cart.web.page;

import org.apache.wicket.protocol.http.WicketFilter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.MailService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.RuntimeConstants;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 19/10/2020
 * Time: 20:59
 */
public abstract class AbstractSuiteTest extends AbstractTestDAO {

    static String X_CW_TOKEN = "X-CW-TOKEN";
    static final String LOCATION = "Location";
    static final Locale LOCALE = Locale.ENGLISH;

    @Rule
    public TestName testName = new TestName();

    @Resource(name = "shopResolverFilter")
    private Filter shopResolverFilter;

    @Resource(name = "shoppingCartFilter")
    private Filter shoppingCartFilter;


    @Autowired
    private MailService mailService;

    @Autowired
    private RuntimeConstants runtimeConstants;


    @Autowired
    private ServletContext servletContext;

    private Filter wicketFilter;

    @Resource
    private WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @Override
    protected ApplicationContext createContext() {
        return webApplicationContext;
    }

    @Override
    @Before
    public void setUp() {

        super.setUp();

        final Map<String, String> cfg = new HashMap<>();
        cfg.put("filterMappingUrlPattern", "/*");
        cfg.put("applicationClassName", "org.yes.cart.web.application.StorefrontApplication");
        cfg.put("configuration", "development");
        cfg.put("ignorePaths", "services/,js/");
        cfg.put("secureMode", "false");
        cfg.put("securePort", "80");
        cfg.put("unsecurePort", "80");

        wicketFilter = new WicketFilter();
        try {
            wicketFilter.init(new FilterConfig() {
                @Override
                public String getFilterName() {
                    return getTestName();
                }

                @Override
                public ServletContext getServletContext() {
                    return servletContext;
                }

                @Override
                public String getInitParameter(final String name) {
                    return cfg.get(name);
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return Collections.enumeration(cfg.keySet());
                }
            });
        } catch (Exception exp) {
            fail(exp.getMessage());
        }

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(shopResolverFilter)
                .addFilter(shoppingCartFilter)
                .addFilter(wicketFilter)
                .build();

        X_CW_TOKEN = runtimeConstants.getConstant("webapp.token.name");
    }


    /**
     * Perform reindex.
     */
    protected void reindex() {
        PlatformTransactionManager transactionManager =   ctx().getBean("transactionManager", PlatformTransactionManager.class);
        TransactionTemplate tx = new TransactionTemplate(transactionManager);


        tx.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericFTSCapableDAO<Product, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO)).fullTextSearchReindex(false, 1000);
                ((GenericFTSCapableDAO<Product, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_SKU_DAO)).fullTextSearchReindex(false, 1000);

            }
        });
    }


    public String getTestName() {
        return this.getClass().getSimpleName() + "." + testName.getMethodName();
    }

    protected String redirectFromPrevious(final MvcResult result) {
        return redirectFromPrevious(result, "/");
    }

    protected String redirectFromPrevious(final MvcResult result, final String baseUrl) {
        final String location = result.getResponse().getHeader(LOCATION);
        assertNotNull(location);
        return relativeToAbsoluteUrl(baseUrl, location);
    }

    protected String relativeToAbsoluteUrl(final String uri) {
        return relativeToAbsoluteUrl("/", uri);
    }

    protected String relativeToAbsoluteUrl(final String base, final String uri) {
        if (uri.equals(".")) {
            return base;
        }
        if (uri.startsWith("./")) { // Redirects from /xxxx -> ./yyyy
            return base + uri.substring(2);
        }
        if (uri.startsWith("../")) { // Redirects from /xxxx/zzzz -> ../yyyy?bar=zzzz
            return base + uri.substring(3);
        }
        return uri;
    }

    protected String formAction(final Document html, final String formId) {
        return formAction(html, formId, "/");
    }

    protected String formAction(final Document html, final String formId, final String baseUrl) {
        final Elements formElems = html.select("form#" + formId);
        assertFalse("Form not found " + formId, formElems.isEmpty());
        final String action = formElems.get(0).attr("action");
        return relativeToAbsoluteUrl(baseUrl, action);
    }

    protected String formSelect(final Document html, final String formId, final String controlName) {
        return formSelect(html, formId, controlName, "/");
    }

    protected String formSelect(final Document html, final String formId, final String controlName, final String baseUrl) {
        final Elements formElems = html.select("form#" + formId);
        assertFalse("Form not found " + formId, formElems.isEmpty());
        final Elements selectElems = formElems.get(0).select("select[name=\"" + controlName + "\"]");
        assertFalse("Select element not found " + formId + "/" + controlName, selectElems.isEmpty());
        final String onChangeJs = selectElems.get(0).attr("onchange");
        final String action = onChangeJs.substring(onChangeJs.indexOf("f.action='") + 10, onChangeJs.indexOf("';f.submit();"));
        return relativeToAbsoluteUrl(baseUrl, action);
    }

    protected String formRadio(final Document html, final String formId, final String controlName) {
        return formRadio(html, formId, controlName, "/");
    }

    protected String formRadio(final Document html, final String formId, final String controlName, final String baseUrl) {
        final Elements formElems = html.select("form#" + formId);
        assertFalse("Form not found " + formId, formElems.isEmpty());
        final Elements selectElems = formElems.get(0).select("div[onclick]");
        assertFalse("Click element not found " + formId + "/" + controlName, selectElems.isEmpty());
        final String onChangeJs = selectElems.get(0).attr("onclick");
        final String action = onChangeJs.substring(onChangeJs.indexOf("f.action='") + 10, onChangeJs.indexOf("';f.submit();"));
        return relativeToAbsoluteUrl(baseUrl, action);
    }



    protected boolean hasEmails(final String recipient) {

        return !this.mailService.findByCriteria(" where e.recipients = ?1", recipient).isEmpty();

    }

    protected Mail getEmailBySubject(final String subject, final String recipient) {

        return this.mailService.findSingleByCriteria(" where e.recipients = ?1 and e.subject = ?2", recipient, subject);

    }

    protected Mail getEmailBySubjectLike(final String subject, final String recipient) {

        return this.mailService.findSingleByCriteria(" where e.recipients = ?1 and lower(e.subject) like ?2", recipient, HQLUtils.criteriaIlikeAnywhere(subject));

    }




}

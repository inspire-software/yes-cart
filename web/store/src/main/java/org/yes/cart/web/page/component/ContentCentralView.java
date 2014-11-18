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

package org.yes.cart.web.page.component;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ContentService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:04 AM
 */
public class ContentCentralView extends AbstractCentralView {


    @SpringBean(name = ServiceSpringKeys.CONTENT_SERVICE)
    protected ContentService contentService;

    /**
     * Construct panel.
     *
     * @param id panel id
     * @param booleanQuery     boolean query.
     */
    public ContentCentralView(String id, Query booleanQuery) {
        super(id, 0l, booleanQuery);
    }

    /**
     * This is an internal constructor used by HomePage class. It disregards the
     * categoryId value.
     *
     * @param id panel id
     * @param categoryId ignored
     * @param booleanQuery     boolean query.
     */
    public ContentCentralView(String id, long categoryId, BooleanQuery booleanQuery) {
        super(id, categoryId, booleanQuery);
    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();
        final String contentBody;
        if (getCategoryId() > 0l) {
            contentBody = contentService.getContentBody(getCategoryId(), lang);
        } else {
            contentBody = "";
        }
        add(new Label("contentBody", contentBody).setEscapeModelStrings(false));
        super.onBeforeRender();
    }
}

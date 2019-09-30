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

package org.yes.cart.web.support.entity.decorator.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.Seoable;
import org.yes.cart.domain.i18n.I18NModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SEO decorator allows graceful failover for basic SEO data if none provided.
 *
 * User: denispavlov
 * Date: 30/12/2015
 * Time: 13:08
 */
public class AbstractSeoableDecoratorImpl<T extends Seoable> implements Seoable {

    private final AbstractFailoverSeo<T> seo;

    public AbstractSeoableDecoratorImpl(final AbstractFailoverSeo<T> seo) {
        this.seo = seo;
    }

    /** {@inheritDoc} */
    @Override
    public Seo getSeo() {

        return this.seo;

    }

    /** {@inheritDoc} */
    @Override
    public void setSeo(final Seo seo) {
        throw new UnsupportedOperationException("Read only");
    }

    public static abstract class AbstractFailoverSeo<T extends Seoable> implements Seo {

        private final T seoable;
        private final Seo seo;

        protected AbstractFailoverSeo(final T seoable) {
            this.seoable = seoable;
            this.seo = seoable.getSeo();
        }

        /** {@inheritDoc} */
        @Override
        public String getUri() {
            return seo.getUri();
        }

        /** {@inheritDoc} */
        @Override
        public void setUri(final String uri) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        @Override
        public String getTitle() {
            if (StringUtils.isBlank(seo.getTitle())) {
                return getTitle(seoable);
            }
            return seo.getTitle();
        }

        /**
         * Failover for title.
         *
         * @param seoable seoable
         *
         * @return display title
         */
        protected abstract String getTitle(final T seoable);

        /** {@inheritDoc} */
        @Override
        public void setTitle(final String title) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        @Override
        public I18NModel getDisplayTitle() {
            if ((seo.getDisplayTitle() == null || seo.getDisplayTitle().getAllValues().isEmpty())
                    && StringUtils.isBlank(seo.getTitle())) {
                return getDisplayTitle(seoable);
            }
            return seo.getDisplayTitle();
        }

        /**
         * Failover for title.
         *
         * @param seoable seoable
         *
         * @return display title
         */
        protected abstract I18NModel getDisplayTitle(final T seoable);

        /** {@inheritDoc} */
        @Override
        public void setDisplayTitle(final I18NModel title) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        @Override
        public String getMetakeywords() {
            return seo.getMetakeywords();
        }

        /** {@inheritDoc} */
        @Override
        public void setMetakeywords(final String metakeywords) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        @Override
        public I18NModel getDisplayMetakeywords() {
            return seo.getDisplayMetakeywords();
        }

        /** {@inheritDoc} */
        @Override
        public void setDisplayMetakeywords(final I18NModel metakeywords) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        @Override
        public String getMetadescription() {
            if (StringUtils.isBlank(seo.getMetadescription())) {
                return getMetadescription(seoable);
            }
            return seo.getMetadescription();
        }

        /**
         * Failover for description.
         *
         * @param seoable seoable
         *
         * @return display description
         */
        protected abstract String getMetadescription(final T seoable);

        /** {@inheritDoc} */
        @Override
        public void setMetadescription(final String metadescription) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        @Override
        public I18NModel getDisplayMetadescription() {
            if ((seo.getDisplayMetadescription() == null || seo.getDisplayMetadescription().getAllValues().isEmpty())
                    && StringUtils.isBlank(seo.getMetadescription())) {
                return getDisplayMetadescription(seoable);
            }
            return seo.getDisplayMetadescription();
        }


        /**
         * Failover for description.
         *
         * @param seoable seoable
         *
         * @return display description
         */
        protected abstract I18NModel getDisplayMetadescription(final T seoable);

        /** {@inheritDoc} */
        @Override
        public void setDisplayMetadescription(final I18NModel metadescription) {
            throw new UnsupportedOperationException("Read only");
        }

        private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

        /**
         * Utility function to remove HTML tags from data.
         *
         * @param string raw string.
         *
         * @return not HTML tags string
         */
        public String removeTags(String string) {
            if (string == null || string.length() == 0) {
                return string;
            }

            Matcher m = REMOVE_TAGS.matcher(string);
            return m.replaceAll("");
        }


    }

}

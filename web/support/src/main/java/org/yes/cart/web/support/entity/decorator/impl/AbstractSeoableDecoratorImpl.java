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
    public Seo getSeo() {

        return this.seo;

    }

    /** {@inheritDoc} */
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
        public String getUri() {
            return seo.getUri();
        }

        /** {@inheritDoc} */
        public void setUri(final String uri) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
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
        public void setTitle(final String title) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        public String getDisplayTitle() {
            if (StringUtils.isBlank(seo.getDisplayTitle()) && StringUtils.isBlank(seo.getTitle())) {
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
        protected abstract String getDisplayTitle(final T seoable);

        /** {@inheritDoc} */
        public void setDisplayTitle(final String title) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        public String getMetakeywords() {
            return seo.getMetakeywords();
        }

        /** {@inheritDoc} */
        public void setMetakeywords(final String metakeywords) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        public String getDisplayMetakeywords() {
            return seo.getDisplayMetakeywords();
        }

        /** {@inheritDoc} */
        public void setDisplayMetakeywords(final String metakeywords) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
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
        public void setMetadescription(final String metadescription) {
            throw new UnsupportedOperationException("Read only");
        }

        /** {@inheritDoc} */
        public String getDisplayMetadescription() {
            if (StringUtils.isBlank(seo.getDisplayMetadescription()) && StringUtils.isBlank(seo.getMetadescription())) {
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
        protected abstract String getDisplayMetadescription(final T seoable);

        /** {@inheritDoc} */
        public void setDisplayMetadescription(final String metadescription) {
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


    };

}

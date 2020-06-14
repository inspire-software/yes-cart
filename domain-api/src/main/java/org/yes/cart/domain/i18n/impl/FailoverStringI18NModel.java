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

package org.yes.cart.domain.i18n.impl;

import org.yes.cart.domain.i18n.I18NModel;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-13
 * Time: 9:54 PM
 */
public class FailoverStringI18NModel implements I18NModel {

    private final I18NModel model;
    private final String failover;

    public FailoverStringI18NModel(final String localisable, final String failover) {
        this.model = new StringI18NModel(localisable);
        final String modelFailover = this.model.getValue(I18NModel.DEFAULT);
        this.failover = modelFailover == null || modelFailover.length() == 0 ? failover : modelFailover;
    }

    public FailoverStringI18NModel(final Map<String, String> values, final String failover) {
        this.model = new StringI18NModel(values);
        final String modelFailover = this.model.getValue(I18NModel.DEFAULT);
        this.failover = modelFailover == null || modelFailover.length() == 0 ? failover : modelFailover;
    }

    public FailoverStringI18NModel(final I18NModel model, final String failover) {
        this(model, failover, false);
    }

    public FailoverStringI18NModel(final I18NModel model, final String failover, final boolean updateDefault) {
        this.model = model != null ? model.copy() : new StringI18NModel();
        final String modelFailover = this.model.getValue(I18NModel.DEFAULT);
        if (modelFailover == null || modelFailover.length() == 0) {
            this.failover = failover;
            if (updateDefault) { // make sure we set xx
                this.model.putValue(I18NModel.DEFAULT, failover);
            }
        } else {
            this.failover = modelFailover;
            if (updateDefault) { // make sure we set xx
                this.model.putValue(I18NModel.DEFAULT, modelFailover);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(final String locale) {
        final String val = model.getValue(locale);
        if (val == null || val.length() == 0) {
            return this.failover;
        }
        return val;
    }

    /** {@inheritDoc} */
    @Override
    public void putValue(final String locale, final String value) {
        model.putValue(locale, value);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getAllValues() {
        return model.getAllValues();
    }

    /** {@inheritDoc} */
    @Override
    public I18NModel copy() {
        return new FailoverStringI18NModel(model.copy(), failover);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FailoverStringI18NModel)) return false;

        final FailoverStringI18NModel that = (FailoverStringI18NModel) o;

        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        return failover != null ? failover.equals(that.failover) : that.failover == null;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = model != null ? model.hashCode() : 0;
        result = 31 * result + (failover != null ? failover.hashCode() : 0);
        return result;
    }
}

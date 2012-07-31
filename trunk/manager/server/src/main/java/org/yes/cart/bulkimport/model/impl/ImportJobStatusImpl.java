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

package org.yes.cart.bulkimport.model.impl;

import org.yes.cart.bulkimport.model.ImportJobStatus;

/**
 * User: denispavlov
 * Date: 12-07-30
 * Time: 9:32 AM
 */
public class ImportJobStatusImpl implements ImportJobStatus {

    private String token;
    private State state;
    private String report;

    public ImportJobStatusImpl() {
    }

    public ImportJobStatusImpl(final String token, final State state, final String report) {
        this.token = token;
        this.state = state;
        this.report = report;
    }

    /** {@inheritDoc} */
    public String getToken() {
        return token;
    }

    /** {@inheritDoc} */
    public State getState() {
        return state;
    }

    /** {@inheritDoc} */
    public String getReport() {
        return report;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public void setReport(final String report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "ImportJobStatusImpl{" +
                "token='" + token + '\'' +
                ", state=" + state +
                '}';
    }
}

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

package org.yes.cart.orderexport;

/**
 * User: denispavlov
 * Date: 20/04/2017
 * Time: 07:49
 */
public class ExportProcessorException extends Exception {

    private final String exporter;

    public ExportProcessorException(final String exporter) {
        this.exporter = exporter;
    }

    public ExportProcessorException(final Throwable cause, final String exporter) {
        super(cause);
        this.exporter = exporter;
    }

    public String getExporter() {
        return exporter;
    }
}

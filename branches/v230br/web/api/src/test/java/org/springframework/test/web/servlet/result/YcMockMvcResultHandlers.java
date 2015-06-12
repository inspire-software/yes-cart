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

package org.springframework.test.web.servlet.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.util.CollectionUtils;

/**
 * User: denispavlov
 * Date: 10/06/2015
 * Time: 17:55
 */
public abstract class YcMockMvcResultHandlers {

    private YcMockMvcResultHandlers() {
    }

    /**
     * Print {@link org.springframework.test.web.servlet.MvcResult} details to the "standard" output stream.
     */
    public static ResultHandler print() {
        return new ConsolePrintingResultHandler();
    }


    /** An {@link PrintingResultHandler} that writes to the "standard" output stream */
    private static class ConsolePrintingResultHandler extends PrintingResultHandler {

        public ConsolePrintingResultHandler() {
            super(new ResultValuePrinter() {

                private final Logger LOG = LoggerFactory.getLogger("RESTTEST");

                public void printHeading(String heading) {
                    LOG.info("");
                    LOG.info(String.format("%20s:", heading));
                }

                public void printValue(String label, Object value) {
                    if (value != null && value.getClass().isArray()) {
                        value = CollectionUtils.arrayToList(value);
                    }
                    LOG.info(String.format("%20s = %s", label, value));
                }
            });
        }
    }


}

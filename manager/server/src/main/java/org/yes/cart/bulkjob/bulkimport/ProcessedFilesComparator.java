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

package org.yes.cart.bulkjob.bulkimport;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 22/10/2015
 * Time: 08:50
 */
public class ProcessedFilesComparator implements Comparator<File> {

    // [randomname].yyyyMMddHHmmss.[ext]
    final Pattern filenameWithTimestamp = Pattern.compile("(.*)\\.(\\d{14})\\.(.*)");

    @Override
    public int compare(final File f1, final File f2) {

        final Matcher m1 = filenameWithTimestamp.matcher(f1.getName());
        final Matcher m2 = filenameWithTimestamp.matcher(f2.getName());

        if (m1.matches() && m2.matches()) {

            final String t1 = m1.group(2);
            final String t2 = m2.group(2);

            final int timeCompare = t1.compareTo(t2);

            if (timeCompare == 0) {

                final String fn1 = m1.group(1) + m1.group(3);
                final String fn2 = m2.group(1) + m2.group(3);;

                return fn1.compareTo(fn2);

            }

            return timeCompare;

        } else {

            final long t1 = f1.lastModified();
            final long t2 = f2.lastModified();

            final int timeCompare = Long.compare(t1, t2);
            if (timeCompare == 0) {

                return f1.getName().compareTo(f2.getName());

            }

            return timeCompare;

        }

    }

}

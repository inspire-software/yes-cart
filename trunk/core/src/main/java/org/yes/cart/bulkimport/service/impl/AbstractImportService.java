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

package org.yes.cart.bulkimport.service.impl;

import org.yes.cart.bulkimport.csv.CvsImportDescriptor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Abstract import service to hold common methods.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 10:43 AM
 */
public class AbstractImportService {

    protected File[] getFilesToImport(final CvsImportDescriptor importDescriptor, final String fileName) {
        return getFilesToImport(importDescriptor.getImportFolder(),
                importDescriptor.getImportFile().getFileNameMask(),
                fileName);
    }

    protected File[] getFilesToImport(final String importFolder, final String fileMaskRe, final String fileName) {
        final FilenameFilter filenameFilter =
                new AbstractImportService.RegexPatternFilenameFilter(fileMaskRe);
        final File importDirectory = new File(importFolder);
        return filterFiles(importDirectory.listFiles(filenameFilter), fileName);
    }

    /**
     * Limit list of possible file to import.
     *
     * @param toFilter set of files to  import
     * @param fileName optional file name
     * @return the filtered file array
     */
    protected File[] filterFiles(final File[] toFilter, final String fileName) {
        if (fileName != null && toFilter != null) {
            final File fileAsFilter = new File(fileName);
            for (File file : toFilter) {
                if (file.compareTo(fileAsFilter) == 0) {
                    return new File[]{fileAsFilter};
                }
            }
            return new File[0];
        }
        return toFilter;
    }


    public static class RegexPatternFilenameFilter implements FilenameFilter {

        private final Pattern pattern;

        /**
         * Constructor.
         *
         * @param pattern reg exp pattern.
         */
        public RegexPatternFilenameFilter(final Pattern pattern) {
            this.pattern = pattern;
        }

        /**
         * Constructor.
         *
         * @param regExp regular expression.
         */
        public RegexPatternFilenameFilter(final String regExp) {
            pattern = Pattern.compile(regExp);
        }

        /**
         * {@inheritDoc
         */
        public boolean accept(final File dir, final String name) {
            return (name != null) && this.pattern.matcher(name).matches();
        }


    }
}

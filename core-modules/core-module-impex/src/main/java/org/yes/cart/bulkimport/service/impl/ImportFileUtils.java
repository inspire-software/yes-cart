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

package org.yes.cart.bulkimport.service.impl;

import org.yes.cart.bulkimport.model.ImportDescriptor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 04/11/2018
 * Time: 20:14
 */
public final class ImportFileUtils {


    public static File[] getFilesToImport(final ImportDescriptor importDescriptor, final String fileName) {
        return getFilesToImport(importDescriptor.getImportDirectory(),
                importDescriptor.getImportFileDescriptor().getFileNameMask(),
                fileName);
    }

    private static File[] getFilesToImport(final String importFolder, final String fileMaskRe, final String fileName) {
        final FilenameFilter filenameFilter =
                new RegexPatternFilenameFilter(fileMaskRe);
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
    private static File[] filterFiles(final File[] toFilter, final String fileName) {
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
         * @param regExp regular expression.
         */
        RegexPatternFilenameFilter(final String regExp) {
            pattern = Pattern.compile(regExp);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(final File dir, final String name) {
            return (name != null) && this.pattern.matcher(name).matches();
        }


    }


}

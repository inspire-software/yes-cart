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

import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/10/2015
 * Time: 08:58
 */
public class ProcessedFilesComparatorTest {

    @Test
    public void testCompareByConvention() throws Exception {

        final File file1 = new File("file1.20151022085900.csv");
        final File file2 = new File("file2.20151022085900.csv");
        final File oldFile1 = new File("file1.20151020085900.csv");

        final Set<File> prioritised = new TreeSet<File>(new ProcessedFilesComparator());

        prioritised.addAll(Arrays.asList(file2, file1, oldFile1));

        final List<File> list = new ArrayList<File>(prioritised);

        assertSame(oldFile1, list.get(0));
        assertSame(file1, list.get(1));
        assertSame(file2, list.get(2));

    }


    @Test
    public void testCompareByLastModified() throws Exception {

        try {
            final File file1 = new File("target/test-classes/file3.csv");
            if (file1.exists()) {
                file1.delete();
            }
            file1.createNewFile();
            file1.setLastModified(System.currentTimeMillis() - 3000);
            final File file2 = new File("target/test-classes/file2.csv");
            if (file2.exists()) {
                file2.delete();
            }
            file2.createNewFile();
            file2.setLastModified(System.currentTimeMillis() - 2000);
            final File file3 = new File("target/test-classes/file1.csv");
            if (file3.exists()) {
                file3.delete();
            }
            file3.createNewFile();
            file3.setLastModified(System.currentTimeMillis() - 1000);

            final Set<File> prioritised = new TreeSet<File>(new ProcessedFilesComparator());

            prioritised.addAll(Arrays.asList(file2, file3, file1));

            final List<File> list = new ArrayList<File>(prioritised);

            assertSame(file1, list.get(0));
            assertSame(file2, list.get(1));
            assertSame(file3, list.get(2));

        } catch (Exception exp) {
            final StringWriter sw = new StringWriter();
            exp.printStackTrace(new PrintWriter(sw));
            fail(exp.getMessage() + "\n" + sw.toString());
        }

    }
}
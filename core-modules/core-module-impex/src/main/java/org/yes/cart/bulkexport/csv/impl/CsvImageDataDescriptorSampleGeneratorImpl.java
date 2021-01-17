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

package org.yes.cart.bulkexport.csv.impl;

import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.domain.misc.Pair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 11:26
 */
public class CsvImageDataDescriptorSampleGeneratorImpl implements DataDescriptorSampleGenerator {

    @Override
    public boolean supports(final Object descriptor) {
        return descriptor instanceof CsvExportDescriptor &&
                "IMAGE".equalsIgnoreCase(((CsvExportDescriptor) descriptor).getEntityType());
    }

    @Override
    public List<Pair<String, byte[]>> generateSample(final Object descriptor) {

        final List<Pair<String, byte[]>> templates = new ArrayList<>();

        final CsvExportDescriptor csv = (CsvExportDescriptor) descriptor;

        final String encoding = csv.getExportFileDescriptor().getFileEncoding();
        final Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        String fileName = csv.getSelectCmd().replace("/", "");

        templates.add(new Pair<>(fileName + "-readme.txt",
                ("Images filename: " + csv.getExportFileDescriptor().getFileName() + "\n").getBytes(charset))
        );

        templates.add(new Pair<>(fileName + "-descriptor.xml",
                (csv.getSource() != null ? csv.getSource() : "<!-- no source -->").getBytes(charset))
        );

        return templates;
    }

}

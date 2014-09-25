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

package org.yes.cart.sampledata.cloudready.transform

/**
 * User: denispavlov
 * Date: 20/09/2014
 * Time: 11:46
 */
class UNECETransformer {

    private void parse(final String source, final String destination) {
        final File sourceFile = new File(source);
        final File countryFile = new File(destination + "countrynames.tmp.csv");
        final File stateFile = new File(destination + "statenames.tmp.csv");

        if (countryFile.exists()) {
            countryFile.delete();
            countryFile.createNewFile();
        }
        if (stateFile.exists()) {
            stateFile.delete();
            stateFile.createNewFile();
        }

        countryFile.withWriterAppend("UTF-8") {
            it << "countryCode;isoCode;countyName;countryLocalName\n"
        }
        stateFile.withWriterAppend("UTF-8") {
            it << "countryCode;isoCode;stateName;stateLocalName\n"
        }

        String[] country = new String[4];
        int[] count = new int[1];

        sourceFile.eachLine("UTF-8") {
            line ->
                String trimmed = line.substring(3);
                String countryCode = trimmed.substring(0, 2);
                String stateCode = trimmed.substring(3, 6);
                String countryName = country[2];
                String stateName = "";
                if (stateCode.contains(' ') && trimmed.charAt(7) == '.') {
                    countryName = trimmed.substring(8, 9) + trimmed.substring(9).trim().toLowerCase();
                    country[0] = countryCode;
                    country[2] = countryName;

                    countryFile.withWriterAppend("UTF-8") {
                        it << "\"${countryCode}\";\"\";\"${countryName}\";\"${countryName}\"\n"
                    }

                } else {
                    stateName = trimmed.substring(43, 77).trim();

                    stateFile.withWriterAppend("UTF-8") {
                        it << "\"${countryCode}\";\"${stateCode}\";\"${stateName} ${stateCode}\";\"${stateName} ${stateCode}\"\n"
                    }
                }

        }


    }


    public static void main(String ... args) {
        new UNECETransformer().parse(
                "env/sampledata/cloudready/export/locations/2014-1 UNLOCODE CodeList.txt",
                "/development/projects/java/yc/env/sampledata/cloudready/import/locations/"
        );
    }

}

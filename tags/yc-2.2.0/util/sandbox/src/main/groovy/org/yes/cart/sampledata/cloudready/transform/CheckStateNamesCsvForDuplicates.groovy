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
 * Date: 25/09/2014
 * Time: 13:58
 */
class CheckStateNamesCsvForDuplicates {

    private void parse(final String source) {
        final File sourceFile = new File(source);

        Set state = new HashSet();

        sourceFile.eachLine("UTF-8") {
            line ->
            if (line.indexOf('"') == -1) {
                return;
            }
            String trimmed = line.substring(6);
            String stateCode = trimmed.substring(0, trimmed.indexOf('"'));
            if (state.contains(stateCode)) {
                System.out.println(stateCode);
            } else {
                state.add(stateCode);
            }

        }


    }



    public static void main(String ... args) {
        new CheckStateNamesCsvForDuplicates().parse(
                "env/sampledata/cloudready/import/locations/statenames.csv"
        );
    }
}

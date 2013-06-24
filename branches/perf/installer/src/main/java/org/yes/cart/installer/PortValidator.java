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

package org.yes.cart.installer;

import com.google.common.base.Strings;
import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;

import java.util.Map;

/**
 * Validator which considers port to be valid if it's within (0..65535) range.
 * Empty value validation skipping supported by assigning "true" to the "required" parameter.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class PortValidator implements Validator {

  private static final String REQUIRED_PARAMETER = "required";

  public boolean validate(ProcessingClient client) {
    Map<String, String> validatorParams = client.getValidatorParams();
    boolean isValueRequired = validatorParams != null && Boolean.parseBoolean(validatorParams.get(REQUIRED_PARAMETER));
    for (int i = 0, numberOfFields = client.getNumFields(); i < numberOfFields; i++) {
      String value = client.getFieldContents(i);
      if (Strings.isNullOrEmpty(value)) {
        if (isValueRequired) {
          return false;
        }
        continue;
      }
      try {
        int port = Integer.parseInt(value);
        if (port < 0 || port > 0xFFFF) {
          return false;
        }
      } catch (NumberFormatException e) {
        return false;
      }
    }
    return true;
  }

}

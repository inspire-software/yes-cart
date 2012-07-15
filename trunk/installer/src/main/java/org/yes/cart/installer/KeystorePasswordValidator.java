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
import com.izforge.izpack.panels.PasswordGroup;
import com.izforge.izpack.panels.ProcessingClient;
import com.izforge.izpack.util.PasswordKeystoreValidator;
import com.izforge.izpack.util.VariableSubstitutor;

/**
 * Modified version of {@link PasswordKeystoreValidator} which skips password validation in case keystore file wasn't specified.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class KeystorePasswordValidator extends PasswordKeystoreValidator {

  @Override
  public boolean validate(ProcessingClient client) {
    return Strings.isNullOrEmpty(getKeystoreFile(client)) || super.validate(client);
  }

  private String getKeystoreFile(ProcessingClient client) {
    VariableSubstitutor variableSubstitutor = new VariableSubstitutor(((PasswordGroup) client).getIdata().getVariables());
    return variableSubstitutor.substitute(client.getValidatorParams().get("keystoreFile"), null);
  }
}

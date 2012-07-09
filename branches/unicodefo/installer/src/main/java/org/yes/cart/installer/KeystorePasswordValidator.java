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

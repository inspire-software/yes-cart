package org.yes.cart.installer;

import com.google.common.base.Strings;
import com.izforge.izpack.panels.ProcessingClient;
import com.izforge.izpack.panels.Validator;

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

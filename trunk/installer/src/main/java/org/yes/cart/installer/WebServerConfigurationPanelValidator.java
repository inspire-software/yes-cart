package org.yes.cart.installer;

import com.google.common.base.Strings;
import com.izforge.izpack.LocaleDatabase;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator of  WebServerConfigurationPanel. Currently it does following checks:
 * - http port availability
 * - https port availability (provided that keystore file was specified)
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class WebServerConfigurationPanelValidator implements DataValidator {

  public static final String VALIDATION_FAILED_TITLE = "WebServerConfigurationPanelValidator.validation.failed";
  public static final String HTTP_PORT_UNAVAILABLE = "WebServerConfigurationPanelValidator.http.port.unavailable";
  public static final String HTTPS_PORT_UNAVAILABLE = "WebServerConfigurationPanelValidator.https.port.unavailable";
  public static final String VALIDATION_FAILED_FOOTNOTE = "WebServerConfigurationPanelValidator.validation.confirmation";

  private String message;

  public Status validateData(AutomatedInstallData data) {
    int httpPort = Integer.parseInt(data.getVariable("http.port"));
    String httpsPort = data.getVariable("https.port");
    String sslKeyStoreFile = data.getVariable("ssl.keystore.file");
    List<String> failedValidations = new ArrayList<String>();
    if (!isPortAvailable(httpPort)) {
      failedValidations.add(HTTP_PORT_UNAVAILABLE);
    }
    if (!Strings.isNullOrEmpty(httpsPort) && !Strings.isNullOrEmpty(sslKeyStoreFile) &&
            !isPortAvailable(Integer.valueOf(httpsPort))) {
      failedValidations.add(HTTPS_PORT_UNAVAILABLE);
    }
    if (!failedValidations.isEmpty()) {
      LocaleDatabase bundle = data.langpack;
      StringBuilder sb = new StringBuilder(bundle.getString(VALIDATION_FAILED_TITLE));
      for (String failedValidation : failedValidations) {
        sb.append("\n").append(bundle.getString(failedValidation));
      }
      sb.append("\n").append(bundle.getString(VALIDATION_FAILED_FOOTNOTE));
      message = sb.toString();
      return Status.WARNING;
    }
    return Status.OK;
  }

  private boolean isPortAvailable(int port) {
    try {
      InetAddress inet = InetAddress.getByName("localhost");
      ServerSocket socket = new ServerSocket(port, 0, inet);
      try {
        return socket.getLocalPort() > 0;
      } finally {
        socket.close();
      }
    } catch (Exception ex) {
      return false;
    }
  }

  public String getErrorMessageId() {
    return message;
  }

  public String getWarningMessageId() {
    return message;
  }

  public boolean getDefaultAnswer() {
    return true;
  }
}

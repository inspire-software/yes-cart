package org.yes.cart.installer;

import com.google.common.collect.ImmutableMap;
import com.izforge.izpack.panels.ProcessingClient;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class PortValidatorTest {

  private final Map<String, String> mapWithRequiredParameterTurnedOn = ImmutableMap.of("required", "true");
  private final Map<String, String> mapWithRequiredParameterTurnedOff = ImmutableMap.of("required", "false");
  private final PortValidator portValidator = new PortValidator();

  @Test
  public void testValidateNegativePort() throws Exception {
    assertFalse(portValidator.validate(mockProcessingClient("-1", null)));
  }
  
  @Test
  public void testValidatePort0() throws Exception {
    assertTrue(portValidator.validate(mockProcessingClient("0", null)));
  }

  @Test
  public void testValidatePort65535() throws Exception {
    assertTrue(portValidator.validate(mockProcessingClient("65535", null)));
  }
  
  @Test
  public void testValidatePort65536() throws Exception {
    assertFalse(portValidator.validate(mockProcessingClient("65536", null)));
  }

  @Test
  public void testValidateInvalidPortWithRequiredParameterTurnedOff() throws Exception {
    assertFalse(portValidator.validate(mockProcessingClient("-1", mapWithRequiredParameterTurnedOff)));
  }

  @Test
  public void testValidateInvalidPortWithRequiredParameterTurnedOn() throws Exception {
    assertFalse(portValidator.validate(mockProcessingClient("-1", mapWithRequiredParameterTurnedOn)));
  }

  @Test
  public void testValidateEmptyPortWithRequiredParameterTurnedOn() throws Exception {
    assertFalse(portValidator.validate(mockProcessingClient("", mapWithRequiredParameterTurnedOn)));
  }

  @Test
  public void testValidateEmptyPortWithRequiredParameterTurnedOff() throws Exception {
    assertTrue(portValidator.validate(mockProcessingClient("", mapWithRequiredParameterTurnedOff)));
  }

  private ProcessingClient mockProcessingClient(final String value, final Map<String, String> parameters) {
    Mockery mockery = new Mockery();
    final ProcessingClient processingClientMock = mockery.mock(ProcessingClient.class);
    mockery.checking(new Expectations() {{
      allowing(processingClientMock).getValidatorParams();
      will(returnValue(parameters));
      allowing(processingClientMock).getNumFields();
      will(returnValue(1));
      allowing(processingClientMock).getFieldContents(0);
      will(returnValue(value));
    }});
    return processingClientMock;
  }
}

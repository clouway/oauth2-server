package com.clouway.oauth2.jws;

import com.google.common.io.BaseEncoding;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class VerifySignaturesWithRsaTest {

  private final String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
          "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCH/eazwg0BwuFx\n" +
          "PmXOoauqD54ZPN+3XRF8FxrYo0XvQ8TiJAEJBJo/qjNahn4YYl/6RbP8YLHCe3nd\n" +
          "40tf42fwvLDFBSyFjKIOOiEEilhxjVX1jPsE4fHO+gSthmyzGgjR4bPCPLCeocQj\n" +
          "UQTguDsl2NARDWHomeC0eJgkS9cPfBdRGyIsgQWsnON+SbMd7cN9iXbIhU/TguLE\n" +
          "aW6WAg/rGX9iMiSsl5XcW6wOGU24uRPTNIySzncLY+jEQeFmEw1g0oa7+ZKuxDce\n" +
          "jJTzo4V0BcEKNiiZvH4Y7t/qsXiDeY13NemZVMQ/H9BIyfBpPdiTphnZEwf5XlUJ\n" +
          "pKekD/KBAgMBAAECggEAZeFXltAIAovHbZl7mAQSoUM2BF5QlASLdtWwbSBU4l15\n" +
          "AJpMlD74eD3AX09m5Em+8baKksa2Jadvs0X3UA0D75zNKa0on5yuQ85UshwbCmcC\n" +
          "QQWvgQbsq00vd/i/MqaMeQCINTpWb2Ftma+24cvjtATsS/okoae2aj32bSrMIXKK\n" +
          "V9UC2IwN5rzoJPEFQ6fPaWcTnFMOINY1UFDerVNhn7aT18ubB25N3TcQFA+IkLjZ\n" +
          "+RXAVsDqmmdiCA0IxF18SkC22TCLvWMW8pYAYWYi9MLOCwwjUwv1C3w38qvlzfRP\n" +
          "VDObsDgrmYA5xyJU1LIzvnaWYa+br6VmnBdCqBTTSQKBgQDzc+pkjol6qDVJb8ZD\n" +
          "oa2mNZJCu3dNYSuB9fYGutIT8/BuBZ+qkmb54C6Ny7M0HjpxQpSOToOpH95HlznJ\n" +
          "NLY80s76E9MdX6xFIlTpBRJnu3iPizCXRQV5C0mwleARVsk0FjE5T0XLQMp3l1bd\n" +
          "AGMGJhM9fM0W3mx2gmTjU+DRGwKBgQCPACjFI1aFIbFtpOQtFgHtCMdymhzLVabc\n" +
          "SyY7fnMoQiiPJMja9reFiSuJbF/BpsAxaOKEaVjivPM3arVaf3pyrqC3ZEwurfwx\n" +
          "u+mnXhUftT8/1esZZBGhFPA321zIaDucKx7yfOgg+gRdi6r4gpYD0pQo7sco86Ve\n" +
          "ARzhgUWgkwKBgGVsaj8gXsgZ4bFJfrjYV4bCFL/2Z7p1+/E1rhyZokGrxAOiFiWy\n" +
          "vnHlYp+yOGNDIKfkzA0JSrKf0zPSHcHkUvO+A3qN3csD+7oFlohJk6RhptVucHzk\n" +
          "xWXrPPTzS5kNpd8sS6+LhhEqWe8+vnJt4dNC84sPPkYDvf4VTsCiRiv3AoGAGjx1\n" +
          "PnYVUae03eD63CrFf6+0qBoOXmAAlTpUcWXpyuEYf+rHzySk1yMrkbMIfocRi/8q\n" +
          "UBDj9fWkye4SB+CLnq7bXcpRD99r/dP0MnjYd1DRoeyljasGcP9ec2ETzNES3rwq\n" +
          "mWLBVAuK8X7Gh4Gt9FWWSUxFzgWluXGK0vTcyXECgYA7GyyBORukyfOb5mCrIXm5\n" +
          "kYztpvfhglrUZ23vEzXLk+KPmDao0X3K6fv6OuvuI2oVAZ6TzTT1OlmF/elvP7JX\n" +
          "4vlOXSxLBduB1cInuZFylB99qRmGMCBWhpIobXyRQIZWnaQnsGDfFJiBrBgzN55U\n" +
          "UqgbFBNjeedWV+Hm6ftwxw==\n" +
          "-----END PRIVATE KEY-----";

  @Test
  public void signatureIsMatching() {

    byte[] signatureValue = BaseEncoding.base64Url().decode("WBAzzss3J8Ea6-xxOCVS2OZ2HoqpiLdfCLhIJEevaPck377qTpiM__lHta_S8dSCuTl5FjREqixIiwGrJVJEIkfExUwS5YWekdJRniSKdqLjmXussePaCSgco3reJDqNcRCGiv9DSLH0GfZFdv11Ik5nyaHjNnS4ykEi76guaY8-T3uVFjOH4e2o8Wm0vBbq9hzo9UHdgnsI2BLrzDVoydGWM7uZW8MQNKTuGWY_Ywyj1hilr9rw4yy2FvBe7G-56qaq8--IlVNZ6ocJX2dYhZPqDtZUYwLRqwFyM_F53Kt81I8Qht6HBgH-fgrfbd7Ms67BeLGsupFvuM9sF-hGOQ");

    boolean isSignedWithThatKey = new RsaJwsSignature(signatureValue)
            .verify(String.format("%s.%s", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9", "eyJpc3MiOiJ4eHhAZGV2ZWxvcGVyLmNvbSIsInNjb3BlIjoidGVzdDEgdGVzdDIiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjkwMDIvb2F1dGgyL3Rva2VuIiwiZXhwIjoxNDYxMjM4OTQ4LCJpYXQiOjE0NjEyMzUzNDgsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJwcm4iOiJ1c2VyQGV4YW1wbGUuY29tIn0").getBytes(), privateKeyPem);

    assertThat(isSignedWithThatKey, is(equalTo(true)));
  }

  @Test
  public void signatureIsNotMatching() {
    byte[] signatureValue = BaseEncoding.base64Url().decode("WBAzzss3J8Ea6-xxOCVS2OZ2HoqpiLdfCLhIJEevaPck377qTpiM__lHta_S8dSCuTl5FjREqixIiwGrJVJEIkfExUwS5YWekdJRniSKdqLjmXussePaCSgco3reJDqNcRCGiv9DSLH0GfZFdv11Ik5nyaHjNnS4ykEi76guaY8-T3uVFjOH4e2o8Wm0vBbq9hzo9UHdgnsI2BLrzDVoydGWM7uZW8MQNKTuGWY_Ywyj1hilr9rw4yy2FvBe7G-56qaq8--IlVNZ6ocJX2dYhZPqDtZUYwLRqwFyM_F53Kt81I8Qht6HBgH-fgrfbd7Ms67BeLGsupFvuM9sF-hGOQ");

    boolean isSignedWithThatKey = new RsaJwsSignature(signatureValue)
            .verify(String.format("%s.%s", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXJJJJ", "eyJpc3MiOiJ4eHhAZGV2ZWxvcGVyLmNvbSIsInNjb3BlIjoidGVzdDEgdGVzdDIiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjkwMDIvb2F1dGgyL3Rva2VuIiwiZXhwIjoxNDYxMjM4OTQ4LCJpYXQiOjE0NjEyMzUzNDgsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJwcm4iOiJ1c2VyQGV4YW1wbGUuY29tIn0").getBytes(), privateKeyPem);

    assertThat(isSignedWithThatKey, is(equalTo(false)));
  }

}
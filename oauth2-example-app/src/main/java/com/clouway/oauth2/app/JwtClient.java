package com.clouway.oauth2.app;

import com.clouway.oauth2.client.BearerAuthenticationInterceptor;
import com.clouway.oauth2.client.JwtConfig;
import com.clouway.oauth2.client.TokenSource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class JwtClient {
  private static String key = "-----BEGIN PRIVATE KEY-----\n" +
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

  // JWT/Service Account Authorization
  // OAuth2 Client Authorization -> web app -> API Gateway
  public static void main(String[] args) {
    JwtConfig config = new JwtConfig.Builder(
            "xxx@apps.clouway.com",
            "http://localhost:9002/oauth2/token",
            key.getBytes())
            .subject("myapp ")
            .build();
    TokenSource tokenSource = config.tokenSource();
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BearerAuthenticationInterceptor(tokenSource))
            .build();
    try {
      Response response = client.newCall(new Request.Builder().get().url("http://localhost:9002/testapi").build()).execute();
      System.out.println(response.body().string());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

package com.clouway.oauth2.jws;

import com.google.common.io.BaseEncoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class PemFile {
  private static final Pattern BEGIN_PATTERN = Pattern.compile("-----BEGIN ([A-Z ]+)-----");
  private static final Pattern END_PATTERN = Pattern.compile("-----END ([A-Z ]+)-----");

  private final InputStream stream;

  public PemFile(InputStream stream) {
    this.stream = stream;
  }

  public byte[] content() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    String title = null;
    StringBuilder keyBuilder = null;
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        checkArgument(title == null, "missing end tag (%s)", title);
        return null;
      }
      if (keyBuilder == null) {
        Matcher m = BEGIN_PATTERN.matcher(line);
        if (m.matches()) {
          String curTitle = m.group(1);

          keyBuilder = new StringBuilder();
          title = curTitle;
        }
      } else {
        Matcher m = END_PATTERN.matcher(line);
        if (m.matches()) {
          String endTitle = m.group(1);
          checkArgument(endTitle.equals(title),
                  "end tag (%s) doesn't match begin tag (%s)", endTitle, title);
          return BaseEncoding.base64().decode(keyBuilder.toString());
        }

        keyBuilder.append(line);
      }
    }
  }
}

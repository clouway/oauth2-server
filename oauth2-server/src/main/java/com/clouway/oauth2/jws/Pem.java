package com.clouway.oauth2.jws;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class Pem {
  private static final Pattern BEGIN_PATTERN = Pattern.compile("-----BEGIN ([A-Z ]+)-----");
  private static final Pattern END_PATTERN = Pattern.compile("-----END ([A-Z ]+)-----");

  public static class Block {
    private final String type;
    private final Map<String, String> headers;
    private final byte[] content;

    public Block(String type, Map<String, String> headers, byte[] content) {
      this.type = type;
      this.headers = headers;
      this.content = content;
    }

    public byte[] getBytes() {
      return content;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Block block = (Block) o;
      return Objects.equals(type, block.type) &&
              Objects.equals(headers, block.headers) &&
              Arrays.equals(content, block.content);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, headers, content);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
              .add("type", type)
              .add("headers", headers)
              .add("content", content)
              .toString();
    }
  }

  public Block parse(InputStream stream) throws IOException {
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
          break;
        }

        keyBuilder.append(line);
      }
    }

    byte[] content = BaseEncoding.base64().decode(keyBuilder.toString());

    return new Block(title, ImmutableMap.<String, String>of(), content);
  }

  public String format(Block block) {
    String keyAsHex = BaseEncoding.base64().encode(block.content);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(bout);
    writer.println(String.format("-----BEGIN %s-----", block.type));
    for (String line : Splitter.fixedLength(64).split(keyAsHex)) {
      writer.println(line);
    }
    writer.println(String.format("-----END %s-----", block.type));
    writer.flush();
    return bout.toString();
  }
}


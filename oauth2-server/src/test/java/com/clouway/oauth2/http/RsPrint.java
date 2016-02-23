package com.clouway.oauth2.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RsPrint implements Response {
  private final Response response;

  public RsPrint(Response response) {
    this.response = response;
  }

  public String print() throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    printHead(baos);
    printBody(baos);
    return baos.toString();
  }

  /**
   * Print it into output stream.
   *
   * @param output Output to print into
   * @throws IOException If fails
   * @since 0.10
   */
  public void printHead(final OutputStream output) throws IOException {
    final String eol = "\r\n";
    final Writer writer = new OutputStreamWriter(output);
    Map<String, String> header = header();
    for (final String each : header.keySet()) {
      writer.write(each + ": " + header.get(each));
    }

    writer.append(eol);
    writer.flush();
  }

  public String printBody() throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    printBody(bout);
    return bout.toString();
  }

  public void printBody(final OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int read;

    try (InputStream body = this.body()) {

      while ((read = body.read(buffer)) > 0) {
        output.write(buffer, 0, read);
      }

    }

  }

  @Override
  public Status status() {
    return response.status();
  }

  @Override
  public Map<String,String> header() throws IOException {
    return response.header();
  }

  @Override
  public InputStream body() throws IOException {
    return response.body();
  }
}

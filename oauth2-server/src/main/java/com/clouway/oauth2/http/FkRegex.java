package com.clouway.oauth2.http;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fork by regular expression pattern.
 * <p/>
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 * <p/>
 * <pre> Take take = new TkFork(
 *   new FkRegex("/home", new TkHome()),
 *   new FkRegex("/account", new TkAccount())
 * );</pre>
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class FkRegex implements Fork {
  private final Pattern pattern;
  private final Take take;

  public FkRegex(String pattern, Take take) {
    this(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL), take);
  }

  public FkRegex(Pattern pattern, Take take) {
    this.pattern = pattern;
    this.take = take;
  }

  @Override
  public Optional<Response> route(Request request) throws IOException {
    String path = request.path();

    final Matcher matcher = this.pattern.matcher(path);
    if (matcher.matches()) {
      return Optional.fromNullable(take.ack(request));
    }

    return Optional.absent();
  }
}

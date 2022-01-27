package com.clouway.oauth2.exampleapp;

import com.clouway.oauth2.token.User;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@Service
public class UserInfoEndPoint {


  private final UserLoader userLoader;

  @Inject
  public UserInfoEndPoint(UserLoader userLoader) {
    this.userLoader = userLoader;
  }

  @Post
  @Get
  public Reply<?> getUserInfo(@Named("token") String token) {

    Optional<User> userResult = userLoader.load(token);

    if (userResult.isPresent()) {
      User user = userResult.get();
      return Reply.with(new UserDto(user.id, user.email, user.name)).as(Json.class).ok();
    }

    return Reply.with(new ErrorResponseDTO("123", "User not found or expired token!")).status(SC_BAD_REQUEST).as(Json.class);
  }

}

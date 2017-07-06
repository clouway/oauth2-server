package com.clouway.oauth2;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Set;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class ResourceOwnerIdentity {


  public final String idenityId;
  public final Set<String> scopes;

  public ResourceOwnerIdentity(String idenityId, Set<String> scopes) {
    this.idenityId = idenityId;
    this.scopes = scopes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ResourceOwnerIdentity)) return false;
    ResourceOwnerIdentity that = (ResourceOwnerIdentity) o;
    return Objects.equal(idenityId, that.idenityId) &&
            Objects.equal(scopes, that.scopes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(idenityId, scopes);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("idenityId", idenityId)
            .add("scopes", scopes)
            .toString();
  }
}

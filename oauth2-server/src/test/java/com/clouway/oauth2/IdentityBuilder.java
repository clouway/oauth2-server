package com.clouway.oauth2;

/**
 * Created by IaNiTyy on 29.03.17.
 */
public class IdentityBuilder {

    public static IdentityBuilder aNewIdentity() {
        return new IdentityBuilder();
    }

    private String id = "";

    public IdentityBuilder withId(String id) {
        this.id = id;
        return this;
    }


    public Identity build(){
        return new Identity(id, "", "", "", "", "", null);
    }
}

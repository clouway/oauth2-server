package com.clouway.oauth2;

/**
 * @author Ianislav Nachev <qnislav.nachev@gmail.com>
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

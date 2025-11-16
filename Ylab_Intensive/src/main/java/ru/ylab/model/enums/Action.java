package ru.ylab.model.enums;

public enum Action {
    ADD_PRODUCT("add product"),
    DELETE_PRODUCT("delete product"),
    SEARCH_PRODUCT("search product"),
    UPDATE_PRODUCT("update product"),
    LOGIN("login"),
    LOGOUT("logout"),
    REGISTER("register"),;

    private final String actionName;

    Action(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}

package main.java.ru.ylab.model.enums;

public enum Action {
    ADD("Add Product"),
    DELETE("Delete Product"),
    SEARCH("Search product"),
    UPDATE("Update Product"),
    LOGIN("Login"),
    LOGOUT("Logout");

    private final String actionName;

    Action(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}

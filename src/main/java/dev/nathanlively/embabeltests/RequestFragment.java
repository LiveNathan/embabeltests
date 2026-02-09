package dev.nathanlively.embabeltests;

public record RequestFragment(String description, RequestType type) {
    public enum RequestType {COMMAND, QUERY, OTHER}
}

package main.server.controller;

public class PathsConstants {
    static final String CATEGORY_ADMIN_PATH = "/admin/categories";
    static final String CATEGORY_ADMIN_BY_ID_PATH = CATEGORY_ADMIN_PATH + "/{catId}";
    static final String CATEGORY_PUBLIC_PATH = "/categories";
    static final String CATEGORY_PUBLIC_BY_ID_PATH = CATEGORY_PUBLIC_PATH + "/{catId}";
    static final String COMPILATION_ADMIN_PATH = "/admin/compilations";
    static final String COMPILATION_PUBLIC_PATH = "/compilations";
    static final String COMPILATION_ADMIN_BY_ID_PATH = COMPILATION_ADMIN_PATH + "/{compId}";
    static final String COMPILATION_PUBLIC_BY_ID_PATH = COMPILATION_PUBLIC_PATH + "/{compId}";
    static final String EVENT_PRIVATE_PATH = "/users/{userId}/events";
    static final String EVENT_ADMIN_PATH = "/admin/events";
    static final String EVENT_PUBLIC_PATH = "/events";
    static final String EVENT_PRIVATE_BY_ID_PATH = EVENT_PRIVATE_PATH + "/{eventId}";
    static final String EVENT_ADMIN_BY_ID_PATH = EVENT_ADMIN_PATH + "/{eventId}";
    static final String EVENT_PUBLIC_BY_ID_PATH = EVENT_PUBLIC_PATH + "/{eventId}";
    static final String EVENT_PRIVATE_BY_ID_AND_REQUEST_PATH = EVENT_PRIVATE_BY_ID_PATH + "/requests";
    static final String REQUESTS_BY_USER_ID_PATH = "/users/{userId}/requests";
    static final String CANCEL_OWN_REQUEST_PATH = "/{requestId}/cancel";
    static final String USER_ADMIN_PATH = "/admin/users";
    static final String USER_ADMIN_BY_ID_PATH = "/{userId}";
}

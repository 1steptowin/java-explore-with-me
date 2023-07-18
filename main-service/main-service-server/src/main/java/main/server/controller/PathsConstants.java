package main.server.controller;

public class PathsConstants {
    final static String CATEGORY_ADMIN_PATH = "/admin/categories";
    final static String CATEGORY_ADMIN_BY_ID_PATH = CATEGORY_ADMIN_PATH + "/{catId}";
    final static String CATEGORY_PUBLIC_PATH = "/categories";
    final static String CATEGORY_PUBLIC_BY_ID_PATH = CATEGORY_PUBLIC_PATH + "/{catId}";
    final static String COMPILATION_ADMIN_PATH = "/admin/compilations";
    final static String COMPILATION_PUBLIC_PATH = "/compilations";
    final static String COMPILATION_ADMIN_BY_ID_PATH = COMPILATION_ADMIN_PATH + "/{compId}";
    final static String COMPILATION_PUBLIC_BY_ID_PATH = COMPILATION_PUBLIC_PATH + "/{compId}";
    final static String EVENT_PRIVATE_PATH = "/users/{userId}/events";
    final static String EVENT_ADMIN_PATH = "/admin/events";
    final static String EVENT_PUBLIC_PATH = "/events";
    final static String EVENT_PRIVATE_BY_ID_PATH = EVENT_PRIVATE_PATH + "/{eventId}";
    final static String EVENT_ADMIN_BY_ID_PATH = EVENT_ADMIN_PATH + "/{eventId}";
    final static String EVENT_PUBLIC_BY_ID_PATH = EVENT_PUBLIC_PATH + "/{eventId}";
    final static String EVENT_PRIVATE_BY_ID_AND_REQUEST_PATH = EVENT_PRIVATE_BY_ID_PATH + "/requests";
    final static String REQUESTS_BY_USER_ID_PATH = "/users/{userId}/requests";
    final static String CANCEL_OWN_REQUEST_PATH = "/{requestId}/cancel";
    final static String USER_ADMIN_PATH = "/admin/users";
    final static String USER_ADMIN_BY_ID_PATH = "/{userId}";
}

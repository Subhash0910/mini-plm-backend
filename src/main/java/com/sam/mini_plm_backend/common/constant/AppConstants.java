package com.sam.mini_plm_backend.common.constant;

/**
 * Application-wide constants
 * Centralized constants for easy maintenance and updates
 */
public class AppConstants {

    // API
    public static final String API_VERSION = "v1";
    public static final String API_PREFIX = "/api/" + API_VERSION;

    // Document States
    public static final String DOC_STATE_DRAFT = "DRAFT";
    public static final String DOC_STATE_RELEASED = "RELEASED";
    public static final String DOC_STATE_OBSOLETE = "OBSOLETE";

    // Change States
    public static final String CHANGE_STATE_OPEN = "OPEN";
    public static final String CHANGE_STATE_IN_PROGRESS = "IN_PROGRESS";
    public static final String CHANGE_STATE_APPROVED = "APPROVED";
    public static final String CHANGE_STATE_REJECTED = "REJECTED";
    public static final String CHANGE_STATE_CLOSED = "CLOSED";

    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_ENGINEER = "ENGINEER";
    public static final String ROLE_VIEWER = "VIEWER";

    // Pagination
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Validation
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_DESCRIPTION_LENGTH = 10;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
}

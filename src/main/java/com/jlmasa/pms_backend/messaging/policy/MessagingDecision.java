package com.jlmasa.pms_backend.messaging.policy;

public record MessagingDecision(boolean allowed, String reasonCode) {

    public static final MessagingDecision ALLOW = new MessagingDecision(true, null);

    public static MessagingDecision deny(String reasonCode){
        return new MessagingDecision(false, reasonCode);
    }
    public static final String NOT_PARTICIPANT = "NOT_PARTICIPANT";
    public static final String CONVERSATION_CLOSED  = "CONVERSATION_CLOSED";
    public static final String TASK_CLOSED = "TASK_CLOSED";
    public static final String CROSS_DEPARTMENT_FORBIDDEN = "CROSS_DEPARTMENT_FORBIDDEN";
    public static final String NOT_TASK_AUTHORIZED = "NOT_TASK_AUTHORIZED";
    public static final String NOT_ORGANIZATION_MEMBER = "NOT_ORGANIZATION_MEMBER";
}


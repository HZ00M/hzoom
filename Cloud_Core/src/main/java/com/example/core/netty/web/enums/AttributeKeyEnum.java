package com.example.core.netty.web.enums;

import io.netty.util.AttributeKey;

public enum  AttributeKeyEnum {
    ENDPOINT_KEY(AttributeKey.valueOf("ENDPOINT_KEY")),
    CHANNEL_KEY(AttributeKey.valueOf("CHANNEL_KEY")),
    PATH_KEY(AttributeKey.valueOf("PATH_KEY")),
    URI_TEMPLATE(AttributeKey.valueOf("URI_TEMPLATE")),
    REQUEST_PARAM(AttributeKey.valueOf("REQUEST_PARAM"));
    private AttributeKey attributeKey;

    AttributeKeyEnum(AttributeKey attributeKey) {
        this.attributeKey = attributeKey;
    }

    public AttributeKey getAttributeKey() {
        return attributeKey;
    }
}

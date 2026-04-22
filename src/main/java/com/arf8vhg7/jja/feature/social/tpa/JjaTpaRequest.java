package com.arf8vhg7.jja.feature.social.tpa;

import java.util.UUID;

public record JjaTpaRequest(UUID senderId, String senderName, UUID receiverId, JjaTpaRequestType type, long createdAt) {
}

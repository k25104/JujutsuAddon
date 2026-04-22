package com.arf8vhg7.jja.feature.social.tpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class JjaTpaRequestStore {
    private static final Map<UUID, Map<UUID, JjaTpaRequest>> REQUESTS = new ConcurrentHashMap<>();

    private JjaTpaRequestStore() {
    }

    public static void upsert(UUID receiverId, UUID senderId, String senderName, JjaTpaRequestType type) {
        Map<UUID, JjaTpaRequest> bySender = REQUESTS.computeIfAbsent(receiverId, id -> new ConcurrentHashMap<>());
        bySender.put(senderId, new JjaTpaRequest(senderId, senderName, receiverId, type, System.currentTimeMillis()));
    }

    public static JjaTpaRequest get(UUID receiverId, UUID senderId) {
        Map<UUID, JjaTpaRequest> bySender = REQUESTS.get(receiverId);
        if (bySender == null) {
            return null;
        }
        return bySender.get(senderId);
    }

    public static Map<UUID, JjaTpaRequest> snapshot(UUID receiverId) {
        Map<UUID, JjaTpaRequest> bySender = REQUESTS.get(receiverId);
        if (bySender == null || bySender.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(bySender);
    }

    public static void remove(UUID receiverId, UUID senderId) {
        Map<UUID, JjaTpaRequest> bySender = REQUESTS.get(receiverId);
        if (bySender == null) {
            return;
        }
        bySender.remove(senderId);
        if (bySender.isEmpty()) {
            REQUESTS.remove(receiverId);
        }
    }

    public static void removeAllForReceiver(UUID receiverId) {
        REQUESTS.remove(receiverId);
    }

    public static void removeAllFromSender(UUID senderId) {
        for (Map.Entry<UUID, Map<UUID, JjaTpaRequest>> entry : REQUESTS.entrySet()) {
            Map<UUID, JjaTpaRequest> bySender = entry.getValue();
            bySender.remove(senderId);
            if (bySender.isEmpty()) {
                REQUESTS.remove(entry.getKey(), bySender);
            }
        }
    }
}

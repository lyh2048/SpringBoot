package com.example.utils;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketUtils {
    /**
     * 模拟存储 websocket session
     */
    public static final Map<String, Session> LIVING_SESSIONS_CACHE = new ConcurrentHashMap<>();

    public static void sendMessageAll(String message) {
        LIVING_SESSIONS_CACHE.forEach((sessionId, session) -> sendMessage(session, message));
    }

    /**
     * 向指定用户发送消息
     */
    public static void sendMessage(Session session, String message) {
        if (session == null) {
            return;
        }
        final RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
        if (basicRemote == null) {
            return;
        }
        try {
            basicRemote.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

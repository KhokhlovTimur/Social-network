//package ru.itis.websocket;
//
//import ru.itis.models.Message;
//
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@ServerEndpoint("/chat")
//public class WebSocketServer {
//    private List<Session> sessions = Collections.synchronizedList(new ArrayList<>());
//
//    @OnOpen
//    public void onOpen(Session session) {
//        sessions.add(session);
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) throws EncodeException, IOException {
//        session.getBasicRemote().sendObject(message);
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        sessions.remove(session);
//    }
//}

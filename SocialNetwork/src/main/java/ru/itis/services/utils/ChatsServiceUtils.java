package ru.itis.services.utils;

import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;
import ru.itis.models.User;

public interface ChatsServiceUtils {
    Message createChatMessage(ChatGlobalId chatGlobalId, String content, Message.MessageType messageType, User sender);
}

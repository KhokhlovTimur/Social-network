package ru.itis.models;

import java.util.Date;

public class Message {
    private Long chatId;
    private Long userId;
    private String content;
    private Date sendingTime;
    private Status status;


    public enum Status{

    }
}

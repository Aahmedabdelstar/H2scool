package com.schoolh2.h2scool.chatcomponents;

/**
 * Created by HP on 04/08/17.
 */

public class MessageModel {

    private String message;
    private String sender;
    private String Receiver;
    private String senderUserid;

    public MessageModel(String message, String sender, String receiver, String senderUserid) {
        this.message = message;
        this.sender = sender;
        this.Receiver = receiver;
        this.senderUserid = senderUserid;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public void setSenderUserid(String senderUserid) {
        this.senderUserid = senderUserid;
    }

    public String getReceiver() {
        return Receiver;
    }

    public String getSenderUserid() {
        return senderUserid;
    }

    public MessageModel() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}

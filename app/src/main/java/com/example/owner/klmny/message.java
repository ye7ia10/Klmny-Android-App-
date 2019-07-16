package com.example.owner.klmny;

public class message {
    private String message, from , type;
    public  message(){}

    public message(String text, String from, String type) {
        this.message = text;
        this.from = from;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setText(String text) {
        this.message = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

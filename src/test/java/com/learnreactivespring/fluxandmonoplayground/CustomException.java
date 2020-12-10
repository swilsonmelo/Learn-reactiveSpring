package com.learnreactivespring.fluxandmonoplayground;

public class CustomException extends Throwable {

    public String message;

    public CustomException(Throwable e) {
        this.message = e.getMessage();
    }

    @Override
    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

}

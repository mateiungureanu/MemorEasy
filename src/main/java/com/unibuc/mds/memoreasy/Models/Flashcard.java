package com.unibuc.mds.memoreasy.Models;

public class Flashcard {
    private String question;
    private String answer;

    public Flashcard(String s1, String s2){
        question=s1;
        answer=s2;
    }
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

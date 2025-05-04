package com.unibuc.mds.memoreasy.Models;

public class Flashcard {
    private int id_flashcard;
    private String question;
    private String answer;

    public Flashcard(int id_flashcard, String question, String answer) {
        this.id_flashcard = id_flashcard;
        this.question = question;
        this.answer = answer;
    }

    public int getId_flashcard() {return id_flashcard;}

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

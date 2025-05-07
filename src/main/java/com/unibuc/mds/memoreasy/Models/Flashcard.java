package com.unibuc.mds.memoreasy.Models;

public class Flashcard {
    private int id_flashcard;
    private String question;
    private String answer;
    private byte []image_q;
    private byte []image_a;

    public Flashcard(int id_flashcard, String question, String answer) {
        this.id_flashcard = id_flashcard;
        this.question = question;
        this.answer = answer;
    }

    public Flashcard(int id_flashcard, String question, String answer, byte[] image_q, byte[] image_a) {
        this.id_flashcard = id_flashcard;
        this.question = question;
        this.answer = answer;
        this.image_q = image_q;
        this.image_a = image_a;
    }


    public int getId_flashcard() {return id_flashcard;}

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }


    public byte[] getImage_q() {
        return image_q;
    }


    public byte[] getImage_a() {
        return image_a;
    }
}

package com.unibuc.mds.memoreasy.Models;

public class Flashcard {
    private int id_flashcard;
    private int id_chapter;
    private String question;
    private byte []image_q;
    private String answer;
    private byte []image_a;

    public Flashcard(){}

    public Flashcard(int id_flashcard, String question, String answer) {
        this.id_flashcard = id_flashcard;
        this.question = question;
        this.answer = answer;
    }

    public Flashcard(int id_flashcard,int id_chapter, String question, byte[] image_q, String answer, byte[] image_a) {
        this.id_flashcard = id_flashcard;
        this.id_chapter=id_chapter;
        this.question = question;
        this.image_q = image_q;
        this.answer = answer;
        this.image_a = image_a;
    }

    public Flashcard(int id_flashcard, String question, byte[] image_q, String answer, byte[] image_a) {
        this.id_flashcard = id_flashcard;
        this.question = question;
        this.image_q = image_q;
        this.answer = answer;
        this.image_a = image_a;
    }

    public int getId_flashcard() {return id_flashcard;}

    public int getId_chapter() {
        return id_chapter;
    }

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

    public void setId_flashcard(int id_flashcard) {
        this.id_flashcard = id_flashcard;
    }

    public void setId_chapter(int id_chapter) {
        this.id_chapter = id_chapter;
    }

    public void setQuestion(String question){this.question=question;}

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setImage_a(byte[] image_a) {
        this.image_a = image_a;
    }

    public void setImage_q(byte[] image_q) {
        this.image_q = image_q;
    }
}

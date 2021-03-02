package com.cygnus.model;

public class OptionsModel {
    String optiona, answera, optionb, answerb, optionc, answerc, optiond, answerd;

    public OptionsModel(String optiona, String answera, String optionb, String answerb,
                        String optionc, String answerc, String optiond, String answerd) {
        this.setOptiona(optiona);
        this.setAnswera(answera);
        this.setOptionb(optionb);
        this.setAnswerb(answerb);
        this.setOptionc(optionc);
        this.setAnswerc(answerc);
        this.setOptiond(optiond);
        this.setAnswerd(answerd);
    }

    public String getOptiona() {
        return optiona;
    }

    public void setOptiona(String optiona) {
        this.optiona = optiona;
    }

    public String getAnswera() {
        return answera;
    }

    public void setAnswera(String answera) {
        this.answera = answera;
    }

    public String getOptionb() {
        return optionb;
    }

    public void setOptionb(String optionb) {
        this.optionb = optionb;
    }

    public String getAnswerb() {
        return answerb;
    }

    public void setAnswerb(String answerb) {
        this.answerb = answerb;
    }

    public String getOptionc() {
        return optionc;
    }

    public void setOptionc(String optionc) {
        this.optionc = optionc;
    }

    public String getAnswerc() {
        return answerc;
    }

    public void setAnswerc(String answerc) {
        this.answerc = answerc;
    }

    public String getOptiond() {
        return optiond;
    }

    public void setOptiond(String optiond) {
        this.optiond = optiond;
    }

    public String getAnswerd() {
        return answerd;
    }

    public void setAnswerd(String answerd) {
        this.answerd = answerd;
    }
}

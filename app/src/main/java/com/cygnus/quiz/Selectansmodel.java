package com.cygnus.quiz;

public class Selectansmodel {
    String timestamp,username,selectedans,timestatus,ansstatus,flagstatus,markanswrong;

    public Selectansmodel(String timestamp, String username, String selectedans,String timestatus,
                          String ansstatus,String flagstatus,String markanswrong) {
      this.setTimestamp(timestamp);
      this.setUsername(username);
      this.setSelectedans(selectedans);
      this.setTimestatus(timestatus);
      this.setAnsstatus(ansstatus);
      this.setFlagstatus(flagstatus);
      this.setMarkanswrong(markanswrong);
    }

    public String getMarkanswrong() {
        return markanswrong;
    }

    public void setMarkanswrong(String markanswrong) {
        this.markanswrong = markanswrong;
    }

    public String getFlagstatus() {
        return flagstatus;
    }

    public void setFlagstatus(String flagstatus) {
        this.flagstatus = flagstatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSelectedans() {
        return selectedans;
    }

    public void setSelectedans(String selectedans) {
        this.selectedans = selectedans;
    }

    public String getTimestatus() {
        return timestatus;
    }

    public void setTimestatus(String timestatus) {
        this.timestatus = timestatus;
    }

    public String getAnsstatus() {
        return ansstatus;
    }

    public void setAnsstatus(String ansstatus) {
        this.ansstatus = ansstatus;
    }
}

package com.cygnus.coursefeature;

public class Courseofferedmodel {
    String tutorialno,description,url;

    public Courseofferedmodel( String tutorialno, String description, String url) {
      this.setTutorialno(tutorialno);
      this.setDescription(description);
      this.setUrl(url);
    }



    public String getTutorialno() {
        return tutorialno;
    }

    public void setTutorialno(String tutorialno) {
        this.tutorialno = tutorialno;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

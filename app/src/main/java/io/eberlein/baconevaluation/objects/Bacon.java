package io.eberlein.baconevaluation.objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Bacon extends RealmObject {
    @PrimaryKey
    private Long code;
    private String codeFormat;
    private String name;
    private int rating;
    private String description;
    private String pictureUri;

    public Bacon(){}

    public Bacon(Long code, String codeFormat){
        this.code = code;
        this.codeFormat = codeFormat;
        this.name = "";
        this.rating = 0;
        this.description = "";
    }

    public Long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getCodeFormat() {
        return codeFormat;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public void setCodeFormat(String codeFormat) {
        this.codeFormat = codeFormat;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }
}

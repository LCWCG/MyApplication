package com.lcw.myapplication.model;

/**
 *
 * @author 刘春旺
 *
 */
public class TopAdModel {

    public static final String CONTENT = "content";

    public static final String URL = "url";
    public static final String PIC = "pic";
    public static final String NAME = "name";

    private String url;
    private String pic;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

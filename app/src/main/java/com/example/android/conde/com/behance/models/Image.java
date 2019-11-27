package com.example.android.conde.com.behance.models;

public class Image {
    private String name, mImageUrl;

    public Image(String name, String imageUrl) {
        if(name.trim().equals("")){
            name = "no name";
        }
        this.name = name;
        mImageUrl = imageUrl;
    }

    public Image() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}

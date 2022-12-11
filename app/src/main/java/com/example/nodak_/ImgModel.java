package com.example.nodak_;

public class ImgModel {
    private String imageurl;
    ImgModel(){
    }
    public  ImgModel(String imageurl){
        this.imageurl = imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}

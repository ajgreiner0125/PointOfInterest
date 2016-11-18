package com.example.pointofinterest2;


import java.io.Serializable;

/**
 * Created by Alex on 11/14/2016.
 */

public class Place implements Serializable {

    String primaryCategory;
    String subCategories;
    String rating;
    String name;
    String photoHref;
    String description;
    String address0;
    String address1;
    String address2;
    String address3;
    Double lat;
    Double lng;

    public Place(String name){
        this.name = name;
    }

    public Place(){

    }

    public void setName(String x){
        this.name = x;
    }

    public void setDescription(String x){
        description = x;
    }

    public void setPrimaryCategory(String x){
        primaryCategory = x;
    }
    public void setSubCategories(String x){
        subCategories = x;
    }
    public void setRating(String x){
        rating = x;
    }
    public void setPhotoHref(String x){
        photoHref = x;
    }
    public void setAddress0(String x){
        address0 = x;
    }
    public void setAddress1(String x){
        address1 = x;
    }
    public void setAddress2(String x){
        address2 = x;
    }
    public void setAddress3(String x){
        address3 = x;
    }
    public void setLatLng(double x, double y){
        lat = x;
        lng = y;
    }





}

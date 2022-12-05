package com.example.wedding_management_system.model;

public class Wedding {

    private String wedding_id;
    private String couple_name;
    private String wedding_date;
    private long min_budget;
    private long max_budget;
    private String location_id;
    private String admin_id;

    public String getWedding_id() {
        return wedding_id;
    }

    public void setWedding_id(String wedding_id) {
        this.wedding_id = wedding_id;
    }

    public String getCouple_name() {
        return couple_name;
    }

    public void setCouple_name(String couple_name) {
        this.couple_name = couple_name;
    }

    public String getWedding_date() {
        return wedding_date;
    }

    public void setWedding_date(String wedding_date) {
        this.wedding_date = wedding_date;
    }

    public long getMin_budget() {
        return min_budget;
    }

    public void setMin_budget(int min_budget) {
        this.min_budget = min_budget;
    }

    public long getMax_budget() {
        return max_budget;
    }

    public void setMax_budget(int max_budget) {
        this.max_budget = max_budget;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }
}

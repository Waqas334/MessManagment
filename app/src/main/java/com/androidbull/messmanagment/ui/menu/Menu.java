package com.androidbull.messmanagment.ui.menu;

public class Menu {
    private String lunch;
    private String dinner;

    public Menu() {
    }

    public Menu(String lunch, String dinner) {
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}

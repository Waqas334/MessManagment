package com.androidbull.messmanagment.modal;

public class User {
    private String name;
    private String fatherName;
    private String roomNo;
    private String rank;
    private String phoneNumber;
    private String profilePicUrl;
    private boolean isProfileComplete;

    public User(String name, String fatherName, String roomNo, String rank, String phoneNumber, String profilePicUrl, boolean isProfileComplete) {
        this.name = name;
        this.fatherName = fatherName;
        this.roomNo = roomNo;
        this.rank = rank;
        this.phoneNumber = phoneNumber;
        this.profilePicUrl = profilePicUrl;
        this.isProfileComplete = isProfileComplete;
    }


    public User() {
    }

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        isProfileComplete = profileComplete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}


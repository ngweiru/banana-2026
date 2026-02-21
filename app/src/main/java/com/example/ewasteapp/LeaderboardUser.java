package com.example.ewasteapp;

public class LeaderboardUser {
    private int rank;
    private String name;
    private double weight;
    private String profileImageUrl;

    public LeaderboardUser(int rank, String name, double weight, String profileImageUrl) {
        this.rank = rank;
        this.name = name;
        this.weight = weight;
        this.profileImageUrl = profileImageUrl;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
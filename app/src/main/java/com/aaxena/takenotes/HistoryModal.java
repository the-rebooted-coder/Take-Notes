package com.aaxena.takenotes;

public class HistoryModal {
    private String courseName;
    private int id;

    // creating getter and setter methods
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public HistoryModal(String courseName) {
        this.courseName = courseName;
    }
}

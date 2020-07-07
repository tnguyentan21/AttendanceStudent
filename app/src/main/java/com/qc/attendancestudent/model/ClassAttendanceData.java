package com.qc.attendancestudent.model;

import java.util.List;

public class ClassAttendanceData {
    String maLop;
    String tenLop;
    List<Student> studentList;
    Boolean isHaveDiemDanh;
    String keyClass;
    String Lat;
    String Lng;

    public ClassAttendanceData(String maLop, String tenLop, Boolean isHaveDiemDanh, List<Student> students, String keyClass) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.isHaveDiemDanh = isHaveDiemDanh;
        this.studentList = students;
        this.keyClass = keyClass;
    }

    public String getMaLop() {
        return maLop;
    }

    public String gettenLop() {
        return tenLop;
    }

    public Boolean getisHaveDiemDanh() {
        return isHaveDiemDanh;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public String getLat() {
        return Lat;
    }

    public String getLng() {
        return Lng;
    }
}

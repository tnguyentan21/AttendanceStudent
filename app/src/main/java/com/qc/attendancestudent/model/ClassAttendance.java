package com.qc.attendancestudent.model;

public class ClassAttendance {
    String MaLop;
    String TenLop;
    String studentList;
    Boolean IsHaveDiemDanh;
    String keyClass;
    String Lat;
    String Lng;

    public ClassAttendance(String maLop, String tenLop, Boolean isHaveDiemDanh, String students, String keyClass, String lat, String lng) {
        this.MaLop = maLop;
        this.TenLop = tenLop;
        this.IsHaveDiemDanh = isHaveDiemDanh;
        this.studentList = students;
        this.keyClass = keyClass;
        this.Lat = lat;
        this.Lng = lng;
    }

    public String getMaLop() {
        return MaLop;
    }

    public String getTenLop() {
        return TenLop;
    }

    public Boolean getIsHaveDiemDanh() {
        return IsHaveDiemDanh;
    }

    public String getStudentList() {
        return studentList;
    }

    public String getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(String keyClass) {
        this.keyClass = keyClass;
    }

    public String getLat() {
        return Lat;
    }

    public String getLng() {
        return Lng;
    }

}

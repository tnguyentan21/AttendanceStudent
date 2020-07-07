package com.qc.attendancestudent.model;

import java.io.Serializable;

public class Student implements Serializable {
    String maLop;
    int mSSV;
    String hoTen;
    Boolean isHaveDiemDanh;

    public Student(String maLop, int ma, String hoTen, Boolean isHaveDiemDanh) {
        this.maLop = maLop;
        this.mSSV = ma ;
        this.hoTen = hoTen;
        this.isHaveDiemDanh = isHaveDiemDanh;
    }

    public String getmaLop() {
        return maLop;
    }

    public String gethoTen() {
        return hoTen;
    }

    public int getmSSV() {
        return mSSV;
    }

    public void setStatus(Boolean sts) {
        isHaveDiemDanh = sts;
    }

    public Boolean getisHaveDiemDanh() {
        return isHaveDiemDanh;
    }
}

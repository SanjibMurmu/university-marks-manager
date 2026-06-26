package com.sanju.marks;

/**
 * Data Transfer Object (DTO) used to flatten and package student academic records.
 * Securely transmits aggregated grade data to the React presentation layer without 
 * exposing sensitive entity relationships or password hashes.
 *
 * @author Sanjib Murmu
 */

public class StudentResultDTO {
    private Long rollNo;
    private String name;
    private int oos;
    private int cn;
    private int maths;
    private int gtc;
    private int ggm;

    public StudentResultDTO(Long rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }

    public Long getRollNo() { return rollNo; }
    public void setRollNo(Long rollNo) { this.rollNo = rollNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getOos() { return oos; }
    public void setOos(int oos) { this.oos = oos; }
    public int getCn() { return cn; }
    public void setCn(int cn) { this.cn = cn; }
    public int getMaths() { return maths; }
    public void setMaths(int maths) { this.maths = maths; }
    public int getGtc() { return gtc; }
    public void setGtc(int gtc) { this.gtc = gtc; }
    public int getGgm() { return ggm; }
    public void setGgm(int ggm) { this.ggm = ggm; }
    
    public int getTotal() { 
        return oos + cn + maths + gtc + ggm; 
    }
}
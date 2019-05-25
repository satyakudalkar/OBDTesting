package com.example.obdtesting;

public class ObdData {
    private int maf,etemp,speed,rpm,eload,tpos;
    private String behavior;
    public ObdData(int maf,int etemp,int speed,int rpm,int eload,int tpos,String behavior){
        this.maf=maf;
        this.etemp=etemp;
        this.rpm=rpm;
        this.speed=speed;
        this.eload=eload;
        this.tpos=tpos;
        this.behavior=behavior;
    }

    public String getBehavior() {
        return behavior;
    }

    public int getEload() {
        return eload;
    }

    public int getEtemp() {
        return etemp;
    }

    public int getMaf() {
        return maf;
    }

    public int getRpm() {
        return rpm;
    }

    public int getSpeed() {
        return speed;
    }

    public int getTpos() {
        return tpos;
    }
}

package com.wedp2.alybb.activityrecognition;

import java.util.Date;

public class Record {
    private int id;
    private Date time;
    private String activity;

    public Record(){

    }

    public Record(int id, Date time, String activity){
        this.id = id;
        this.time = time;
        this.activity = activity;
    }

    public int getId(){
        return id;
    }

    public void setId(){
        this.id = id;
    }

    public Date getTime(){
        return time;
    }

    public void setTime(){
        this.time = time;
    }

    public String getActivity(){
        return activity;
    }

    public void setActivity(){
        this.activity = activity;
    }
}

package com.room.accountbook.pojo;

/**
 * Created by subash on 7/11/17.
 */

public class Bill {
    private String sponsorName;
    private String spendFor;

    public String getSpendFor() {
        return spendFor;
    }

    public void setSpendFor(String spendFor) {
        this.spendFor = spendFor;
    }

    private String moneySpend;
    private String consumers;
    private String date;
    private String time;
    private String id;

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getMoneySpend() {
        return moneySpend;
    }

    public void setMoneySpend(String moneySpend) {
        this.moneySpend = moneySpend;
    }

    public String getConsumers() {
        return consumers;
    }

    public void setConsumers(String consumers) {
        this.consumers = consumers;
    }
}

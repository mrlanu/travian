package io.lanu.travian.templates;

public class Time {

    private static int ROUND = 10;

    private double a;
    private double k;
    private double b;

    public Time(double a, double k, double b) {
        this.a = a;
        this.k = k;
        this.b = b;
    }

    public Time(double a, double k) {
        this.a = a;
        this.k = 1.16;
        this.b = 1875 * this.k;
    }

    public Time(double a) {
        this.a = a;
        this.k = 1;
        this.b = 1875 * this.k;
    }

    public int valueOf(int lvl){
       double prev = this.a * Math.pow(this.k, lvl-1) - this.b;
       return (int) (Math.round(prev / ROUND) * ROUND);
    }
}

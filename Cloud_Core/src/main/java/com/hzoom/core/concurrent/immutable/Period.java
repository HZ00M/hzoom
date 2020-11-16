package com.hzoom.core.concurrent.immutable;

import java.util.Date;

// Broken "immutable" time period class
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * @param start the beginning of the period
     * @param end   the end of the period; must not precede start
     * @throws IllegalArgumentException if start is after end
     * @throws NullPointerException     if start or end is null
     */
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                    start + " after " + end);
//        this.start = start;
//        this.end   = end;
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }

    public static void main(String[] args) {
        Date start = new Date();
        Date end = new Date();
        Period p = new Period(start, end);
        System.out.println(p.end.toString());
        end.setYear(78);  // Modifies internals of p!
        System.out.println(p.end.toString());
    }
}

package model;

import java.time.LocalDate;

/**
 * Represents signle point on graph. Used for drawing graph implementation.
 */

public class Point {
    LocalDate date;
    int data;
    public Point(LocalDate date, int data) {
        this.date = date;
        this.data = data;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getData() {
        return data;
    }
}

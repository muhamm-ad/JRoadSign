package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class Range<T> {

    private T start;
    private T end;

    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "{start=" + start + ", end=" + end + "}";
    }
}
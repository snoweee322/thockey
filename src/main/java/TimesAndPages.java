import java.util.Comparator;
import java.util.Set;

public class TimesAndPages implements Comparable<TimesAndPages>{

    public Long unixTime;

    public Set<Integer> pages;

    TimesAndPages(){}

    TimesAndPages(Long unixTime, Set<Integer> pages) {

        this.unixTime = unixTime;
        this.pages = pages;
    }

    public Long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(Long unixTime) {
        this.unixTime = unixTime;
    }

    public Set<Integer> getPages() {
        return pages;
    }

    public void setPages(Set<Integer> pages) {
        this.pages = pages;
    }

    @Override
    public int compareTo(TimesAndPages timesAndPages) {
        return Long.compare(unixTime, timesAndPages.unixTime);
    }
}

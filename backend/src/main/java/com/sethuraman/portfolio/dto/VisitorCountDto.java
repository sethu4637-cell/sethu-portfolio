package com.sethuraman.portfolio.dto;

/**
 * DTO for visitor count data returned to the public portfolio page.
 * Only exposes aggregate numbers, never individual fingerprints.
 */
public class VisitorCountDto {

    private long totalVisits;
    private long todayVisits;
    private long totalUniqueVisitors;
    private long todayUniqueVisitors;

    public VisitorCountDto() {}

    public VisitorCountDto(long totalVisits, long todayVisits,
                           long totalUniqueVisitors, long todayUniqueVisitors) {
        this.totalVisits = totalVisits;
        this.todayVisits = todayVisits;
        this.totalUniqueVisitors = totalUniqueVisitors;
        this.todayUniqueVisitors = todayUniqueVisitors;
    }

    public long getTotalVisits() { return totalVisits; }
    public void setTotalVisits(long totalVisits) { this.totalVisits = totalVisits; }

    public long getTodayVisits() { return todayVisits; }
    public void setTodayVisits(long todayVisits) { this.todayVisits = todayVisits; }

    public long getTotalUniqueVisitors() { return totalUniqueVisitors; }
    public void setTotalUniqueVisitors(long totalUniqueVisitors) { this.totalUniqueVisitors = totalUniqueVisitors; }

    public long getTodayUniqueVisitors() { return todayUniqueVisitors; }
    public void setTodayUniqueVisitors(long todayUniqueVisitors) { this.todayUniqueVisitors = todayUniqueVisitors; }
}

package com.sethuraman.portfolio.dto;

import java.util.List;

/**
 * DTO for the admin dashboard — aggregates visitor stats and recent messages.
 */
public class DashboardDto {

    private long totalVisits;
    private long todayVisits;
    private long totalUniqueVisitors;
    private long todayUniqueVisitors;
    private long totalMessages;
    private List<ContactMessageDto> recentMessages;

    public DashboardDto() {}

    public DashboardDto(long totalVisits, long todayVisits, long totalUniqueVisitors,
                        long todayUniqueVisitors, long totalMessages,
                        List<ContactMessageDto> recentMessages) {
        this.totalVisits = totalVisits;
        this.todayVisits = todayVisits;
        this.totalUniqueVisitors = totalUniqueVisitors;
        this.todayUniqueVisitors = todayUniqueVisitors;
        this.totalMessages = totalMessages;
        this.recentMessages = recentMessages;
    }

    public long getTotalVisits() { return totalVisits; }
    public void setTotalVisits(long totalVisits) { this.totalVisits = totalVisits; }

    public long getTodayVisits() { return todayVisits; }
    public void setTodayVisits(long todayVisits) { this.todayVisits = todayVisits; }

    public long getTotalUniqueVisitors() { return totalUniqueVisitors; }
    public void setTotalUniqueVisitors(long totalUniqueVisitors) { this.totalUniqueVisitors = totalUniqueVisitors; }

    public long getTodayUniqueVisitors() { return todayUniqueVisitors; }
    public void setTodayUniqueVisitors(long todayUniqueVisitors) { this.todayUniqueVisitors = todayUniqueVisitors; }

    public long getTotalMessages() { return totalMessages; }
    public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }

    public List<ContactMessageDto> getRecentMessages() { return recentMessages; }
    public void setRecentMessages(List<ContactMessageDto> recentMessages) { this.recentMessages = recentMessages; }
}

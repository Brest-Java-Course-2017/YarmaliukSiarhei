package com.segniertomato.work.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Investigation {

    private static final Logger LOGGER = LogManager.getLogger();

    private Integer investigationId = -1;
    private Integer number;
    private String title;
    private String description;

    private OffsetDateTime startInvestigationDate;
    private OffsetDateTime endInvestigationDate;

    private List<Employee> involvedStaff = new LinkedList<>();

    public Investigation(Integer investigationId, Integer number, String title, String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate) {

        LOGGER.debug("constructor Investigation(Integer, Integer, String, String, OffsetDateTime, OffsetDateTime)");

        this.investigationId = investigationId;
        this.number = number;
        this.title = title;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
    }

    public Investigation(String description, OffsetDateTime startInvestigationDate) {

        LOGGER.debug("constructor Investigation(String, OffsetDateTime)");

        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
    }

    public Investigation(String description, OffsetDateTime startInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(String, OffsetDateTime, List<Employee>)");

        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Investigation(String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate) {

        LOGGER.debug("constructor Investigation(String, OffsetDateTime, OffsetDateTime)");

        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
    }

    public Investigation(String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(String, OffsetDateTime, OffsetDateTime, List<Employee>)");

        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Investigation(String title, String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate) {

        LOGGER.debug("constructor Investigation(String, String, OffsetDateTime, OffsetDateTime)");

        this.title = title;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
    }

    public Investigation(String title, String description, OffsetDateTime startInvestigationDate,
                         OffsetDateTime endInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(String, OffsetDateTime, OffsetDateTime, List<Employee>)");

        this.title = title;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Investigation(Integer number, String description, OffsetDateTime startInvestigationDate) {

        LOGGER.debug("constructor Investigation(Integer, String, OffsetDateTime)");

        this.number = number;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
    }

    public Investigation(Integer number, String description, OffsetDateTime startInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(Integer, String, OffsetDateTime, List<Employee>)");

        this.number = number;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Investigation(Integer number, String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate) {

        LOGGER.debug("constructor Investigation(Integer, String, OffsetDateTime, OffsetDateTime)");

        this.number = number;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
    }

    public Investigation(Integer number, String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(Integer, String, OffsetDateTime, OffsetDateTime, List<Employee>)");

        this.number = number;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Investigation(Integer number, String title, String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate) {

        LOGGER.debug("constructor Investigation(Integer, String, String, OffsetDateTime, OffsetDateTime)");

        this.number = number;
        this.title = title;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
    }

    public Investigation(Integer number, String title, String description, OffsetDateTime startInvestigationDate,
                         OffsetDateTime endInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(String, String, OffsetDateTime, OffsetDateTime, List<Employee>)");

        this.number = number;
        this.title = title;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {

        LOGGER.debug("setNumber(Integer)");
        this.number = number;
    }

    public Integer getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(Integer investigationId) {

        LOGGER.debug("setInvestigationId(Integer)");
        this.investigationId = investigationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        LOGGER.debug("setTitle(String)");
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        LOGGER.debug("setDescription(String)");
        this.description = description;
    }

    public OffsetDateTime getStartInvestigationDate() {
        return startInvestigationDate;
    }

    public void setStartInvestigationDate(OffsetDateTime startInvestigationDate) {

        LOGGER.debug("setStartInvestigationsDate(OffsetDateTime)");
        this.startInvestigationDate = startInvestigationDate;
    }

    public OffsetDateTime getEndInvestigationDate() {
        return endInvestigationDate;
    }

    public void setEndInvestigationDate(OffsetDateTime endInvestigationDate) {

        LOGGER.debug("setEndInvestigationDate(OffsetDateTime)");
        this.endInvestigationDate = endInvestigationDate;
    }

    public List<Employee> getInvolvedStaff() {
        return involvedStaff;
    }

    public void setInvolvedStaff(List<Employee> involvedStaff) {

        LOGGER.debug("setInvolvedStaff(List<Employee>)");
        this.involvedStaff = involvedStaff;
    }

    @Override
    public boolean equals(Object o) {

        LOGGER.debug("equals(Object)");

        if (this == o) return true;
        if (!(o instanceof Investigation)) return false;

        Investigation that = (Investigation) o;

        return Objects.equals(investigationId, that.investigationId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(startInvestigationDate, that.startInvestigationDate) &&
                Objects.equals(endInvestigationDate, that.endInvestigationDate) &&
                Objects.equals(involvedStaff, that.involvedStaff);
    }

    @Override
    public int hashCode() {

        LOGGER.debug("hashCode()");
        return Objects.hash(investigationId, title, description, startInvestigationDate, endInvestigationDate, involvedStaff);
    }

    @Override
    public String toString() {

        LOGGER.debug("toString()");
        return "Investigation{" +
                "investigationId=" + investigationId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startInvestigationDate=" + startInvestigationDate +
                ", endInvestigationDate=" + endInvestigationDate +
                ", involvedStaff=" + involvedStaff +
                '}';
    }
}

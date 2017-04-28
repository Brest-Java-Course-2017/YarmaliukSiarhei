package com.segniertomato.work.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.OffsetDateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class Investigation {

    private static Logger LOGGER = LogManager.getLogger();

    private Integer investigationId = -1;
    private String name;
    private String description;

    private OffsetDateTime startInvestigationDate;
    private OffsetDateTime endInvestigationDate;

    private List<Employee> involvedStaff = new LinkedList<>();

    public Investigation(String name, String description, OffsetDateTime startInvestigationDate, OffsetDateTime endInvestigationDate) {

        LOGGER.debug("constructor Investigation(String, String, OffsetDateTime, OffsetDateTime)");

        this.name = name;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
    }

    public Investigation(String name, String description, OffsetDateTime startInvestigationDate,
                         OffsetDateTime endInvestigationDate, List<Employee> involvedStaff) {

        LOGGER.debug("constructor Investigation(String, String, OffsetDateTime, OffsetDateTime, List<Employee>)");

        this.name = name;
        this.description = description;
        this.startInvestigationDate = startInvestigationDate;
        this.endInvestigationDate = endInvestigationDate;
        this.involvedStaff = involvedStaff;
    }

    public Integer getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(Integer investigationId) {

        LOGGER.debug("setInvestigationId(Integer)");
        this.investigationId = investigationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        LOGGER.debug("setName(String)");
        this.name = name;
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
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(startInvestigationDate, that.startInvestigationDate) &&
                Objects.equals(endInvestigationDate, that.endInvestigationDate) &&
                Objects.equals(involvedStaff, that.involvedStaff);
    }

    @Override
    public int hashCode() {

        LOGGER.debug("hashCode()");
        return Objects.hash(investigationId, name, description, startInvestigationDate, endInvestigationDate, involvedStaff);
    }

    @Override
    public String toString() {

        LOGGER.debug("toString()");
        return "Investigation{" +
                "investigationId=" + investigationId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startInvestigationDate=" + startInvestigationDate +
                ", endInvestigationDate=" + endInvestigationDate +
                ", involvedStaff=" + involvedStaff +
                '}';
    }
}

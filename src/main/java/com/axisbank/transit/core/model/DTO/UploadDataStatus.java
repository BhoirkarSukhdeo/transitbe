package com.axisbank.transit.core.model.DTO;

import java.util.Set;

public class UploadDataStatus {
    String label;
    String currentStatus;
    String startTime;
    Set<String> errors;
    Set<String> processed;

    public UploadDataStatus() {
    }

    public UploadDataStatus(String label, Set<String> errors, Set<String> processed, String currentStatus, String startTime) {
        this.label = label;
        this.errors = errors;
        this.processed = processed;
        this.currentStatus = currentStatus;
        this.startTime = startTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public void setErrors(Set<String> errors) {
        this.errors = errors;
    }

    public Set<String> getProcessed() {
        return processed;
    }

    public void setProcessed(Set<String> processed) {
        this.processed = processed;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}

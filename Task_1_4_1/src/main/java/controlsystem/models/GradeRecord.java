package controlsystem.models;

import controlsystem.enums.ControlType;
import controlsystem.enums.Grade;

public class GradeRecord {
    private final String subject;
    private final int semester;
    private final ControlType controlType;
    private final Grade grade;
    private final boolean finalForSubject;

    public GradeRecord(String subject, int semester,
                       ControlType controlType, Grade grade,
                       boolean finalForSubject) {
        this.subject = subject;
        this.semester = semester;
        this.controlType = controlType;
        this.grade = grade;
        this.finalForSubject = finalForSubject;
    }

    public String getSubject() {
        return this.subject;
    }

    public int getSemester() {
        return this.semester;
    }

    public ControlType getControlType() {
        return this.controlType;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public boolean isFinalForSubject() {
        return this.finalForSubject;
    }
}


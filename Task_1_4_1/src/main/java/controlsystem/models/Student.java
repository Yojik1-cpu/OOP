package controlsystem.models;

import controlsystem.enums.EducationForm;

public class Student {
    private final String name;
    private EducationForm form;
    private final Gradebook gradebook = new Gradebook();

    public Student(String name, EducationForm form) {
        this.name = name;
        this.form = form;
    }

    public Gradebook getGradebook() {
        return gradebook;
    }

    public EducationForm getForm() {
        return form;
    }

    public boolean canTransferToBudget() {
        if (form == EducationForm.BUDGET) {
            return false;
        }
        return gradebook.hasGoodLastTwoSessions();
    }

    public void transferToBudget() {
        if (canTransferToBudget()) {
            this.form = EducationForm.BUDGET;
        }
    }

    public String getName() {
        return name;
    }
}


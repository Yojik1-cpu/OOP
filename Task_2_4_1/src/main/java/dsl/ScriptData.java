package dsl;

import model.Assignment;
import model.Checkpoint;
import model.GlobalSettings;
import model.LabTask;
import model.StudentGroup;

import java.util.ArrayList;
import java.util.List;

public class ScriptData {
    private final List<LabTask> tasks = new ArrayList<>();
    private final List<StudentGroup> groups = new ArrayList<>();
    private final List<Assignment> assignments = new ArrayList<>();
    private final List<Checkpoint> checkpoints = new ArrayList<>();
    private final GlobalSettings globalSettings = new GlobalSettings();

    public List<LabTask> getTasks() {
        return tasks;
    }

    public List<StudentGroup> getGroups() {
        return groups;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }
}

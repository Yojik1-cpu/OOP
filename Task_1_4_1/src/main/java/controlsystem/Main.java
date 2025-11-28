package controlsystem;

import controlsystem.enums.ControlType;
import controlsystem.enums.EducationForm;
import controlsystem.enums.Grade;
import controlsystem.models.GradeRecord;
import controlsystem.models.Gradebook;
import controlsystem.models.Student;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Student s = new Student("Ян Даздраперма Кукуцаполевна", EducationForm.PAID);
        Gradebook gb = s.getGradebook();

        // cеместр 1: все экзы 5
        gb.addRecord(new GradeRecord("Математика", 1,
                ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("Прога", 1,
                ControlType.EXAM, Grade.EXCELLENT, true));

        // cеместр 2: одна 4
        gb.addRecord(new GradeRecord("Физика", 2,
                ControlType.EXAM, Grade.GOOD, true));
        gb.addRecord(new GradeRecord("Тервер", 2,
                ControlType.EXAM, Grade.EXCELLENT, true));

        System.out.println("Средний балл: " + gb.getCurrentGPA());
        System.out.println("Можно ли на повышенную в 1 семестре? "
                + gb.canGetIncScholarship(1));
        System.out.println("Можно ли на повышенную во 2 семестре? "
                + gb.canGetIncScholarship(2));
        System.out.println("Можно ли перевестись на бюджет? "
                + s.canTransferToBudget());
        System.out.println("Шанс на красный диплом: "
                + gb.canGetHonorsDiploma());

        String subjectToShow = "Математика";
        System.out.println("\nОценки по предмету: " + subjectToShow);

        var mathRecords = gb.getRecordsBySubject(subjectToShow);
        if (mathRecords.isEmpty()) {
            System.out.println("Записей по этому предмету пока нет.");
        } else {
            for (GradeRecord r : mathRecords) {
                System.out.printf(
                        "Семестр %d, вид контроля: %s, оценка: %s%n",
                        r.getSemester(),
                        r.getControlType(),
                        r.getGrade()
                );
            }
        }
    }
}


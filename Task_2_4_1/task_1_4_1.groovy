task {
    id "Task_1_4_1"
    title "GradeBook"
    maxScore 1
    softDeadline "2025-11-29"
    hardDeadline "2025-11-29"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_4_1"
    }
}

task {
    id "Task_1_1_3"
    title "OpWithEq"
    maxScore 2
    softDeadline "2025-10-04"
    hardDeadline "2025-10-11"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_1_3"
    }
}

task {
    id "Task_1_2_1"
    title "Graph"
    maxScore 2
    softDeadline "2025-10-18"
    hardDeadline "2025-11-01"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_2_1"
    }
}

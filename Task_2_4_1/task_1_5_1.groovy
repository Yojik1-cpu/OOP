task {
    id "Task_1_5_1"
    title "Markdown"
    maxScore 4
    softDeadline "2025-12-13"
    hardDeadline "2025-12-27"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_5_1"
    }
}

task {
    id "Task_1_1_2"
    title "BlackJack"
    maxScore 2
    softDeadline "2025-09-20"
    hardDeadline "2025-09-27"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_1_2"
    }
}

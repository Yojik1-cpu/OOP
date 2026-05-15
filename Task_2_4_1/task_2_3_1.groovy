task {
    id "Task_2_3_1"
    title "SnakeGame"
    maxScore 1
    softDeadline "2026-04-03"
    hardDeadline "2026-04-10"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_2_3_1"
    }
}

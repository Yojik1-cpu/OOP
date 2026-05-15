task {
    id "Task_2_2_1"
    title "Pizzeria"
    maxScore 1
    softDeadline "2026-03-07"
    hardDeadline "2026-03-14"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_2_2_1"
    }
}

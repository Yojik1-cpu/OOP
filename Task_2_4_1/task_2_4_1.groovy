task {
    id "Task_2_4_1"
    title "AutoTaskCheck"
    maxScore 1
    softDeadline "2026-04-25"
    hardDeadline "2026-05-16"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_2_4_1"
    }
}

task {
    id "Task_2_1_1"
    title "PrimeNumbers"
    maxScore 1
    softDeadline "2026-02-14"
    hardDeadline "2026-02-21"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_2_1_1"
    }
}

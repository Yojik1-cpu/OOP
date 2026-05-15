task {
    id "Task_2_1_2"
    title "PrimeNumbersAdv"
    maxScore 1
    softDeadline "2026-05-23"
    hardDeadline "2026-05-30"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_2_1_2"
    }
}

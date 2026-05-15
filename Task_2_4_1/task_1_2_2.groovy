task {
    id "Task_1_2_2"
    title "HashTable"
    maxScore 2
    softDeadline "2025-11-08"
    hardDeadline "2025-11-15"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "pkrasnyanskii", "NetscapeNav", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_2_2"
    }
}

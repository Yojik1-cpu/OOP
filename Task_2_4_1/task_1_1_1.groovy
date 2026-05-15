task {
    id "Task_1_1_1"
    title "PyrSort"
    maxScore 1
    softDeadline "2025-09-13"
    hardDeadline "2025-09-13"
}

def students = ["VlanAni", "agrentseva", "DeshinMichael", "Proletcultist", "dmObraztsov", "Yojik1-cpu", "NikRo12", "Marat-nsu", "chebupelka332-pro"]

students.each { nick ->
    assignment {
        studentGithubNick nick
        taskId "Task_1_1_1"
    }
}
assignment {
    studentGithubNick "pkrasnyanskii"
    taskId "Task_1_1_1"
    projectPath "Task-1-1"
}
assignment {
    studentGithubNick "NetscapeNav"
    taskId "Task_1_1_1"
    projectPath "Task_1_1"
}

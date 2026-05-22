@echo off
echo Starting local distributed cluster...

set NUM_WORKERS=2
set MASTER_PORT=8080

echo Starting Master...
start "Master" cmd /k "java -cp build\classes\java\main foundPrime.runScripts.RunMaster %NUM_WORKERS% %MASTER_PORT%"

timeout /t 2 /nobreak > nul

echo Starting Worker 1...
start "Worker 1" cmd /k "java -cp build\classes\java\main foundPrime.network.Worker 127.0.0.1 %MASTER_PORT%"
echo Starting Worker 2...
start "Worker 2" cmd /k "java -cp build\classes\java\main foundPrime.network.Worker 127.0.0.1 %MASTER_PORT%"
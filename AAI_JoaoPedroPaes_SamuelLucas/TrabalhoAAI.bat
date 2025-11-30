@echo off
title Executando Chat DSD
echo.
echo --- Iniciando Chat DSD Messaging ---
echo.

:: Verifica se o JAR existe na pasta target (padrao do Maven)
if exist "target\dsdmessaging-1.0-SNAPSHOT.jar" (
    echo [INFO] Encontrado em target/
    java -jar target\dsdmessaging-1.0-SNAPSHOT.jar
) else (
    :: Se nao, tenta na pasta atual
    if exist "dsdmessaging-1.0-SNAPSHOT.jar" (
        echo [INFO] Encontrado na pasta raiz
        java -jar dsdmessaging-1.0-SNAPSHOT.jar
    ) else (
        echo [ERRO] Arquivo dsdmessaging-1.0-SNAPSHOT.jar nao encontrado.
        echo [DICA] Execute 'mvn clean package' antes de rodar este script.
    )
)

echo.
echo Aplicacao encerrada.
pause
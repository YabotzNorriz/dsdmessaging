#!/bin/bash

echo "--- Iniciando Chat DSD Messaging ---"

# Verifica se o arquivo existe na pasta target
if [ -f "target/dsdmessaging-1.0-SNAPSHOT.jar" ]; then
    echo "[INFO] Executando a partir de target/"
    java -jar target/dsdmessaging-1.0-SNAPSHOT.jar
elif [ -f "dsdmessaging-1.0-SNAPSHOT.jar" ]; then
    echo "[INFO] Executando a partir da pasta atual"
    java -jar dsdmessaging-1.0-SNAPSHOT.jar
else
    echo "[ERRO] dsdmessaging-1.0-SNAPSHOT.jar não encontrado."
    echo "[DICA] Execute 'mvn clean package' para gerar o jar."
    exit 1
fi
#!/bin/bash
set -e

CONFIG_FILE="/tmp/topics-config.yml"

if [ ! -f "$CONFIG_FILE" ]; then
  echo "No se encontró el archivo $CONFIG_FILE"
  exit 1
fi

echo "Esperando Kafka en kafka:29092..."
cub kafka-ready -b kafka:29092 1 60

echo "Leyendo configuración desde $CONFIG_FILE"

topic=""
partitions=""
replication=""

while IFS= read -r line; do
  clean=$(echo "$line" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')

  if [[ "$clean" =~ ^-?[[:space:]]*name: ]]; then
    if [ -n "$topic" ]; then
      echo "Creando tópico: $topic (partitions=${partitions:-1}, replication=${replication:-1})"
      kafka-topics --create         --topic "$topic"         --partitions "${partitions:-1}"         --replication-factor "${replication:-1}"         --bootstrap-server kafka:29092         --if-not-exists
    fi
    topic=$(echo "$clean" | sed 's/^-*[[:space:]]*name:[[:space:]]*//' | tr -d '"')
    partitions=""
    replication=""
  elif [[ "$clean" =~ ^partitions: ]]; then
    partitions=$(echo "$clean" | cut -d':' -f2 | tr -d '[:space:]')
  elif [[ "$clean" =~ ^replication: ]]; then
    replication=$(echo "$clean" | cut -d':' -f2 | tr -d '[:space:]')
  fi
done < "$CONFIG_FILE"

if [ -n "$topic" ]; then
  echo "Creando tópico: $topic (partitions=${partitions:-1}, replication=${replication:-1})"
  kafka-topics --create     --topic "$topic"     --partitions "${partitions:-1}"     --replication-factor "${replication:-1}"     --bootstrap-server kafka:29092     --if-not-exists
fi

echo "Proceso terminado."

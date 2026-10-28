# VidaSalud — Evaluación Parcial N.º 3

## Integración

Se conserva el frontend Angular, BFF y microservicios originales. Se agregan `ms-vidasalud-notifications` y `ms-vidasalud-rabbit-admin`. `ms-vidasalud-appointments` publica eventos después del commit de creación o cambio de estado. El publicador es opcional y queda desactivado fuera del entorno RabbitMQ mediante `RABBITMQ_ENABLED=false`.

## Ejecutar

Desde `infra/apps`, crear `.env` con las variables de `.env.rabbitmq.example` y ejecutar:

```powershell
docker compose -f compose.local.yml up -d --build rabbitmq notifications rabbit-admin
```

Para habilitar eventos reales de citas, iniciar también `catalog` y `appointments`:

```powershell
docker compose -f compose.local.yml up -d --build catalog appointments
```

El BFF y Angular no se alteraron. El login sigue necesitando una configuración válida de Entra ID. El administrador REST utiliza un encabezado `X-Admin-Key` y se publica únicamente en loopback local, puerto 8084. La consola RabbitMQ está en `http://localhost:15672`.

## Topología

| Recurso | Nombre |
|---|---|
| Exchange direct | `vidasalud.appointments.exchange` |
| Cola de creación | `vidasalud.notifications.queue` |
| Routing key | `appointment.created` |
| Cola de estados | `vidasalud.status.queue` |
| Routing key | `appointment.status.changed` |
| Dead-letter exchange direct | `vidasalud.appointments.dlx` |
| DLQ | `vidasalud.notifications.dlq` |
| Routing key DLQ | `appointment.failed` |

Los consumidores tienen ACK explícito tras procesamiento y NACK con `requeue=false` ante error. Los mensajes inválidos pasan a DLQ. `DeadLetterConsumer` tiene `autoStartup=false` para mantener los mensajes en la DLQ para inspección en RabbitMQ Management. Se registran los errores en logs.

## Administrador REST

Todos requieren `X-Admin-Key` con el valor de `RABBIT_ADMIN_KEY` configurado localmente.

| Método | Ruta | JSON |
|---|---|---|
| POST | `/api/rabbit-admin/queues` | `{"name":"demo.queue","durable":true}` |
| DELETE | `/api/rabbit-admin/queues/demo.queue` | — |
| POST | `/api/rabbit-admin/exchanges` | `{"name":"demo.exchange","type":"direct"}` |
| DELETE | `/api/rabbit-admin/exchanges/demo.exchange` | — |
| POST | `/api/rabbit-admin/bindings` | `{"exchange":"demo.exchange","queue":"demo.queue","routingKey":"demo.created"}` |
| DELETE | `/api/rabbit-admin/bindings` | Igual que POST |

## Comprobación

1. Verificar el panel RabbitMQ, exchanges, queues y bindings.
2. Crear una cita a través del flujo original y comprobar en logs de notifications el evento `appointment.created`.
3. Cambiar el estado de una cita y comprobar `appointment.status.changed`.
4. Para probar DLQ, publicar manualmente un JSON con `simulateFailure:true` en el exchange principal y routing key `appointment.created`, y comprobar que queda en `vidasalud.notifications.dlq`.
5. Crear y borrar recursos `demo.*` con la API del administrador, verificando en el panel que se apliquen.
6. Confirmar que las citas siguen persistiendo y sus reglas de estados no han cambiado.

## Consideraciones

La publicación después del commit no garantiza entrega si RabbitMQ no está disponible. Para garantías fuertes entre base de datos y broker sería necesario implementar patrón outbox; esto no forma parte de la integración básica. El administrador usa una clave independiente y está destinado únicamente a redes de desarrollo confiables. No publicar sus endpoints ni el panel de RabbitMQ a Internet. Para AWS será necesario adaptar infraestructura, secretos y políticas de acceso.

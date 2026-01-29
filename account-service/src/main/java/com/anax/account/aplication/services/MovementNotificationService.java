package com.anax.account.aplication.services;

import com.anax.account.domain.model.Movement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovementNotificationService {

    // Convertir a String para enviar el texto plano de la notificación
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String NOTIFICATION_TOPIC = "notificacion-movements";

    @KafkaListener(topics = "movement-events", groupId = "notification-group")
    public void consumeMovementEvent(Movement movement) {
        log.info("Consumiendo evento para notificar. ID Movimiento: {}", movement.getId());

        try {
            // Construir mensaje de notificación
            String message = String.format(
                    "NOTIFICACIÓN: Se ha realizado un %s por valor de %.2f. Saldo disponible: %.2f",
                    movement.getMovementType(),
                    movement.getValue(),
                    movement.getBalance()
            );

            // Publicar en el nuevo tópico
            kafkaTemplate.send(NOTIFICATION_TOPIC, message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Notificación enviada exitosamente: {}", message);
                        } else {
                            log.error("Error al enviar notificación", ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Error procesando el evento de movimiento", e);
        }
    }
}
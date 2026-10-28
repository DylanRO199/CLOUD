package cl.duoc.vidasalud.rabbitadmin.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class,
            jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<Map<String, String>> invalidInput(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Parámetros inválidos"));
    }
    @ExceptionHandler(org.springframework.amqp.AmqpException.class)
    public ResponseEntity<Map<String, String>> rabbitFailure(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "Operación RabbitMQ fallida"));
    }
}

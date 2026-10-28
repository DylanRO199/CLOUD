package cl.duoc.vidasalud.notifications.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationServiceTest {
    private final NotificationService service = new NotificationService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void processesValidAppointmentEvent() throws Exception {
        String json = """
                {"eventId":"evt-001","eventType":"appointment.created","appointment":{"id":1001}}
                """;
        assertThatCode(() -> service.process(mapper.readTree(json))).doesNotThrowAnyException();
    }

    @Test
    void rejectsSimulatedFailureForDlq() throws Exception {
        String json = """
                {"eventId":"evt-002","eventType":"appointment.created","appointment":{"id":1002},"simulateFailure":true}
                """;
        assertThatThrownBy(() -> service.process(mapper.readTree(json)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DLQ");
    }

    @Test
    void rejectsEventWithoutRequiredFields() throws Exception {
        String json = """
                {"eventType":"appointment.created"}
                """;
        assertThatThrownBy(() -> service.process(mapper.readTree(json)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

package cl.duoc.vidasalud.rabbitadmin.controller;

import cl.duoc.vidasalud.rabbitadmin.dto.*;
import cl.duoc.vidasalud.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/rabbit-admin")
public class RabbitAdminController {
    private final RabbitAdminService service;

    public RabbitAdminController(RabbitAdminService service) { this.service = service; }

    @PostMapping("/queues")
    @ResponseStatus(HttpStatus.CREATED)
    public void createQueue(@Valid @RequestBody QueueRequest request) { service.createQueue(request); }

    @DeleteMapping("/queues/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQueue(@PathVariable @Pattern(regexp="[a-zA-Z0-9][a-zA-Z0-9._-]{0,127}") String name) { service.deleteQueue(name); }

    @PostMapping("/exchanges")
    @ResponseStatus(HttpStatus.CREATED)
    public void createExchange(@Valid @RequestBody ExchangeRequest request) { service.createExchange(request); }

    @DeleteMapping("/exchanges/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExchange(@PathVariable @Pattern(regexp="[a-zA-Z0-9][a-zA-Z0-9._-]{0,127}") String name) { service.deleteExchange(name); }

    @PostMapping("/bindings")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBinding(@Valid @RequestBody BindingRequest request) { service.createBinding(request); }

    @DeleteMapping("/bindings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBinding(@Valid @RequestBody BindingRequest request) { service.deleteBinding(request); }
}

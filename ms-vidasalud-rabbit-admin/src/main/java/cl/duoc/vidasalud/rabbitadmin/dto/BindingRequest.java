package cl.duoc.vidasalud.rabbitadmin.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record BindingRequest(@NotBlank @Pattern(regexp="[a-zA-Z0-9][a-zA-Z0-9._-]{0,127}") String exchange,
                             @NotBlank @Pattern(regexp="[a-zA-Z0-9][a-zA-Z0-9._-]{0,127}") String queue,
                             @Pattern(regexp="[a-zA-Z0-9.*#_-]{0,128}") String routingKey) {}

package cl.duoc.vidasalud.rabbitadmin.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record ExchangeRequest(@NotBlank @Pattern(regexp="[a-zA-Z0-9][a-zA-Z0-9._-]{0,127}") String name,
                              @NotBlank @Pattern(regexp="direct|topic|fanout") String type) {}

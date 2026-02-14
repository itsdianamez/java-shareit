package ru.practicum.shareit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestCreateDto {
    @NotBlank
    private String description;
}

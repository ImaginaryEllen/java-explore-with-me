package ru.practicum.ewm.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private Long event;
    @NotNull
    private Long user;
    @NotNull
    private Boolean eventLike;
}

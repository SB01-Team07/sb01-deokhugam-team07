package com.part3.team07.sb01deokhugamteam07.dto.notification.request;

import jakarta.validation.constraints.NotNull;

public record NotificationUpdateRequest (
    @NotNull
    boolean confirmed
){
}

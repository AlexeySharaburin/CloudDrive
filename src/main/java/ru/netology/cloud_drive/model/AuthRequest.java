package ru.netology.cloud_drive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class AuthRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Login required!")
    @Size(min = 6, message = "Login must contain at least 4 characters")
    private String login;

    @NotNull(message = "Password required!")
    @Size(min = 4, message = "Password must contain at least 4 characters")
    private String password;
}

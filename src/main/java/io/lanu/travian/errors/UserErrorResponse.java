package io.lanu.travian.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserErrorResponse {
    private int status;
    private String message;
    private long timestamp;

}

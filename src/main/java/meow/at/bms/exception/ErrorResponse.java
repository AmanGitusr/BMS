package meow.at.bms.exception;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

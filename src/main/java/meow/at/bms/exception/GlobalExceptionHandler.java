package meow.at.bms.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException resourceNotFoundException, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                resourceNotFoundException.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<?> seatUnavailableException(SeatUnavailableException seatUnavailableException, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                seatUnavailableException.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception exception, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                exception.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}

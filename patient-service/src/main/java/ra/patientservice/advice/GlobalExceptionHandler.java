package ra.patientservice.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ra.patientservice.dto.response.ApiResponse;
import ra.patientservice.exception.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Dữ liệu không hợp lệ!",
                null,
                errors,
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Dữ liệu nhập vào không hợp lệ!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Dữ liệu đã tồn tại!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(DataDecodingException.class)
    public ResponseEntity<ApiResponse<?>> handleDataDecodingException(DataDecodingException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Lỗi giải mã dữ liệu!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataEncodingException.class)
    public ResponseEntity<ApiResponse<?>> handleDataEncodingException(DataEncodingException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Lỗi mã hóa dữ liệu!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Dữ liệu không tồn tại!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Lỗi server!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

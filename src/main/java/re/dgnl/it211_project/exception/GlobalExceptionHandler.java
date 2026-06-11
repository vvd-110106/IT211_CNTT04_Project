package re.dgnl.it211_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAllExceptions(Exception exception) {
//        Map<String, Object> responseMap = new HashMap<>();
//        responseMap.put("success", false);
//        responseMap.put("message", exception.getMessage());
//        return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception exception) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", false);
        responseMap.put("message", "Đã có lỗi xảy ra: " + exception.getMessage());

        HttpStatus status = (exception instanceof RuntimeException) ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        String validationMessage = exception.getBindingResult().getFieldError().getDefaultMessage();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", false);
        responseMap.put("message", validationMessage);
        return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
    }

}
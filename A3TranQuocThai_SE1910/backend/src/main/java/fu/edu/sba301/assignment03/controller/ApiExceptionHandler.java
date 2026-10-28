package fu.edu.sba301.assignment03.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<?> invalid(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}
 @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
 public ResponseEntity<?> validation(org.springframework.web.bind.MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("message",e.getBindingResult().getAllErrors().getFirst().getDefaultMessage()));}
}

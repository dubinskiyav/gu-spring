package biz.gelicon.guspring.utils;

import biz.gelicon.guspring.exceptions.FetchQueryException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@ControllerAdvice(annotations = RestController.class)
//  аннотация для обработки исключений в приложении Spring MVC
public class RestControllerExceptionHandler {

    // Ошибка при выборке данных
    public static final int FETCH_ERROR = 224;
    // Ошибка при сохранении данных
    public static final int POST_ERROR = 225;
    // Отсутствует сортировка при наличии пагинации
    public static final int BAD_PAGING_NO_SORT = 226;

    @ExceptionHandler(Exception.class)
    // Аннотация работает на уровне контроллера , и он активен только для этого конкретного контроллера
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {

        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        errorResponse.setTimeStamp(new Date().getTime()); // Установим датувремя
        errorResponse.setExceptionClassName(e.getClass().getName()); // установим имя класса
        if (e instanceof FetchQueryException) {
            errorResponse.setErrorCode(FETCH_ERROR);
        }
        errorResponse.setErrorCause(e.getCause().getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        return new ResponseEntity<ErrorResponse>(errorResponse, headers, HttpStatus.OK);
    }

}

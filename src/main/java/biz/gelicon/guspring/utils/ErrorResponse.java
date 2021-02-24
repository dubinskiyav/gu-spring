package biz.gelicon.guspring.utils;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "JSON ответа с ошибкой")
public class ErrorResponse {

    @Schema(description = "Код ошибки<br><br>"
            + "Типовые коды ошибок:<br>"
            + "224 - Ошибка при выборке данных<br>"
            + "225 - Ошибка при сохранении данных<br>"
            + "226 - Отсутствует сортировка при наличии пагинации<br>"
            + "227 - Запись не найдена")
    private Integer errorCode;
    @Schema(description = "Дата-время ошибки")
    private Long timeStamp;
    @Schema(description = "Сообщение об ощибке")
    private String errorMessage;
    @Schema(description = "Класс, вызвавший ошибку")
    private String exceptionClassName;
    @Schema(description = "Причина ошибки, как правило - текст с сервера")
    private String errorCause;
    @Schema(description = "Расшифровка ошибок полей, Map<String,String>, key - имя поля, value - текст ошибки")
    private Map<String, String> fieldErrors;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

}

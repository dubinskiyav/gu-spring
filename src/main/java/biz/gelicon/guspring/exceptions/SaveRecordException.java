package biz.gelicon.guspring.exceptions;

/**
 * Исключение при попытке выбрать одну запись
 */
// Не забыть добавить обработку в RestControllerExceptionHandler
public class SaveRecordException extends RuntimeException {

    private static final String errText = "Ошибка модификации данных в базе. ";

    public SaveRecordException(
            Throwable err
    ) {
        super(errText, err);
    }

    public SaveRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}

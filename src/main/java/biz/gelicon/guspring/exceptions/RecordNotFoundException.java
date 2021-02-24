package biz.gelicon.guspring.exceptions;

/**
 * Исключение при попытке выбрать одну запись
 */
// Не забыть добавить обработку в RestControllerExceptionHandler
public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(
            Throwable err
    ){
        super("Запись не найдена", err);
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}

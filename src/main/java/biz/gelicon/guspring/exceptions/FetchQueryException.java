package biz.gelicon.guspring.exceptions;

/**
 * Исключение при SELECT
 */
public class FetchQueryException extends RuntimeException {

    public FetchQueryException(
            Throwable err
    ){
        super("Ошибка при выборе данных из базы", err);
    }

}

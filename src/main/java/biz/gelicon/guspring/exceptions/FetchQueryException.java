package biz.gelicon.guspring.exceptions;

public class FetchQueryException extends Exception {

    public FetchQueryException(
            Throwable err
    ){
        super("Ошибка при выборе данных из базы", err);
    }

}

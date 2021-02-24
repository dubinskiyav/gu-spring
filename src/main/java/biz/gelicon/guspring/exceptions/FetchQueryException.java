package biz.gelicon.guspring.exceptions;

import org.springframework.validation.BindingResult;

public class FetchQueryException extends Exception {
    private BindingResult bindingResult;

    public FetchQueryException(
            BindingResult bindingResult,
            Throwable err
    ){
        super("Ошибка при выборе данных из базы", err);
        this.bindingResult = bindingResult;
    }

}

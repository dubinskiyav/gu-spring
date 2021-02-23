package biz.gelicon.guspring.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Общие методы конвертирования данных
 */
public class ConvertUnils {

    private static final Logger logger = LoggerFactory.getLogger(ConvertUnils.class);
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final static SimpleDateFormat datetimeFormat = new SimpleDateFormat(
            "dd.MM.yyyy HH:mm:ss");

    /**
     * Возвращает true для пустой или пробельной строки и null
     *
     * @param s строка
     * @return результат
     */
    public static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Конвертирует дату в строку формата "31.11.2020"
     *
     * @param d дата
     * @return результат
     */
    public static String dateToStr(Date d) {
        return d == null ? null : dateFormat.format(d);
    }

    /**
     * Конвертирует строку формата "31.11.2020" в дату
     *
     * @param s строка
     * @return результат
     */
    public static Date strToDate(String s) {
        if (empty(s)) {return null;}
        try {
            return dateFormat.parse(s);
        } catch (ParseException e) {
            String errText = String.format("Converting %s to date filed", s);
            logger.error(errText, e);
            throw new RuntimeException(errText, e);
        }
    }

    /**
     * округляет дату-время до даты
     *
     * @param d датавремя
     * @return результат
     */
    // округляет дату-время до даты
    public static Date datetimeToDate(Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMinDate() {
        try {
            return dateFormat.parse("01.01.1900");
        } catch (ParseException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Date getMaxDate() {
        try {
            return dateFormat.parse("31.12.2099");
        } catch (ParseException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Вспомогательный метод
     *
     * @param iterator
     * @param <T>
     * @return
     */
    public static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * Строит секцию ORDER BY
     *
     * @param gelRequestParam
     * @return
     */
    public static String buildOrderBySection(
            GelRequestParam gelRequestParam,
            Map<String, String> fieldMap   // Соответствия полей в базе с полями в клиентах
    ) {
        if (gelRequestParam != null && gelRequestParam.getSort() != null
                && gelRequestParam.getSort().size() > 0) {
            // Соответствия полей в базе с полями в клиентах
            return gelRequestParam.getSort().stream()
                    .map(s -> {
                        // заменим поле name на edizm_name
                        String fieldName =
                                (fieldMap != null && fieldMap.containsKey(s.getField())) ?
                                        fieldMap.get(s.getField()) : s.getField();
                        // Заменим направление сортировки
                        String order = (s.getOrder() != null && s.getOrder().equals("descend")) ? "DESC" : "";
                        return fieldName + " " + order;
                    })
                    .collect(Collectors.joining(",\n         ", "\nORDER BY ", ""));
        }
        return "";
    }

    /**
     * Составляет секцию LIMIT и OFFSET
     *
     * @param gelRequestParam
     * @return
     */
    public static String buildLimitSection(GelRequestParam gelRequestParam) {
        if (gelRequestParam != null && gelRequestParam.getPageSize() != null) {
            // Ограницим число строк размером страницы
            String limitPart = "\nLIMIT " + gelRequestParam.getPageSize();
            if (gelRequestParam.getPageNumber() != null
                    && gelRequestParam.getPageNumber() > 0) { // Это не первая страница
                // Сместимсся на столько записей, сколько их в предудущих страницах
                limitPart = limitPart + " OFFSET "
                        + gelRequestParam.getPageNumber() * gelRequestParam.getPageSize();
            }
            return limitPart;
        }
        return "";
    }

    /**
     * Преобразовывает текст SQL с учетом пагинации и сортировки
     *
     * @param sqlText
     * @param gelRequestParam
     * @param fieldMap
     * @return
     */
    public static String buildPaginationSection(
            String sqlText,
            GelRequestParam gelRequestParam,
            Map<String, String> fieldMap   // Соответствия полей в базе с полями в клиентах
    ) {
        if (DatabaseUtils.isPostgreSQL()) {
            String orderBy = buildOrderBySection(gelRequestParam, fieldMap);
            String limit = buildLimitSection(gelRequestParam);
            return sqlText + orderBy + limit;
        } else {
            throw new RuntimeException(
                    "Не реализовано для данного типа СУБД: " + DatabaseUtils.getDbType());
        }
    }

}

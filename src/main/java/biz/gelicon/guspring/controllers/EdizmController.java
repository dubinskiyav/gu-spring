package biz.gelicon.guspring.controllers;

import biz.gelicon.guspring.entities.EdizmEntity;
import biz.gelicon.guspring.exceptions.FetchQueryException;
import biz.gelicon.guspring.utils.ConvertUnils;
import biz.gelicon.guspring.utils.GelRequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Единицы измерения", description = "Контроллер для справочника единиц измерения")
@RequestMapping(value = "/edizm",    // задаёт "каталог", в котором будут размещаться методы контроллера
        consumes = "application/json; charset=UTF-8", // определяет, что Content-Type запроса клиента должен быть "application/json"
        produces = "application/json; charset=UTF-8")
// определяет, что возвращать будет "application/json"
public class EdizmController {

    private static final Logger logger = LoggerFactory.getLogger(EdizmController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Operation(
            summary = "Список единиц измерения",
            description = "Возвращает список единиц измерения"
    )
    @RequestMapping(value = "read", method = RequestMethod.POST)
    public List<EdizmEntity> read(
            @RequestBody(required = false) GelRequestParam gelRequestParam
    ) throws FetchQueryException {
        logger.info("Edizm - read: gelRequestParam = {}", gelRequestParam);
        // Выборка
        String sqlText = ""
                + "SELECT edizm_id,\n"
                + "       edizm_name,\n"
                + "       edizm_notation1,\n"
                + "       edizm_blockflag,\n"
                + "       edizm_code \n"
                + "FROM   edizm\n"
                + "WHERE  1=1\n"
                + "/*F01*/ -- Фильтр по заблокированным\n"
                + "/*F02*/ -- Фильтр по флагу блокировки";
        // Сформируем секцию пагинации
        // Соответствия полей в базе с полями в клиентах
        // todo надо вытащить из аннотаций
        // Соответствия полей в базе с полями в клиентах
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("id", "edizm_id");
        fieldMap.put("name", "edizm_name");
        fieldMap.put("notation", "edizm_notation");
        fieldMap.put("blockflag", "edizm_blockflag");
        fieldMap.put("code", "edizm_code");
        sqlText = ConvertUnils.buildPaginationSection(sqlText, gelRequestParam, fieldMap);
        // Фильтры
        if (gelRequestParam.getFilters() != null) {
            // Только заблокированные
            boolean onlyBlock = Boolean.valueOf(gelRequestParam.getFilters().get("onlyBlock"));
            if (onlyBlock) {
                // Заменим фрагмент на условие
                sqlText = sqlText.replace("/*F01*/", "  AND  edizm_blockflag = 1");
            }
            // По значению поля флаг блокировки
            int blockFlag = Integer.parseInt(gelRequestParam.getFilters().get("blockFlag"));
            switch (blockFlag) {
                case (2): // Заблокированные
                    sqlText = sqlText.replace("/*F02*/", "  AND  edizm_blockflag = 1");
                    break;
                case (3): // Не запблкированные
                    sqlText = sqlText.replace("/*F02*/", "  AND  edizm_blockflag = 0");
                    break;
                default:
                    break;
            }
        }
        logger.info(sqlText);
        if (true) {
            try {
                return jdbcTemplate.query(sqlText,
                        (rs, rowNum) ->
                                new EdizmEntity(
                                        rs.getInt("edizm_id"),
                                        rs.getString("edizm_name"),
                                        rs.getString("edizm_notation"),
                                        rs.getInt("edizm_blockflag"),
                                        rs.getString("edizm_code")
                                )
                );
            } catch (Exception e) {
                throw new FetchQueryException(new Throwable());
            }
        } else {
            try {
                return jdbcTemplate.query(sqlText,
                        (rs, rowNum) ->
                                new EdizmEntity(
                                        rs.getInt("edizm_id"),
                                        rs.getString("edizm_name"),
                                        rs.getString("edizm_notation"),
                                        rs.getInt("edizm_blockflag"),
                                        rs.getString("edizm_code")
                                )
                );
            } catch (Exception e) {
                EdizmEntity error = new EdizmEntity();
                error.notation = "errorCode=38";
                error.name = e.getMessage();
                return Collections.singletonList(error);
            }
        }
    }


}

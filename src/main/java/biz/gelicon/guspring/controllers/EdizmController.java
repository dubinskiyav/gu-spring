package biz.gelicon.guspring.controllers;

import biz.gelicon.guspring.config.SpringJdbcConfig;
import biz.gelicon.guspring.entities.EdizmEntity;
import biz.gelicon.guspring.exceptions.FetchQueryException;
import biz.gelicon.guspring.exceptions.RecordNotFoundException;
import biz.gelicon.guspring.exceptions.SaveRecordException;
import biz.gelicon.guspring.utils.ConvertUnils;
import biz.gelicon.guspring.utils.DatabaseUtils;
import biz.gelicon.guspring.utils.ErrorResponse;
import biz.gelicon.guspring.utils.GelRequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Единицы измерения", description = "Контроллер для справочника единиц измерения")
@RequestMapping(value = "/edizm",    // задаёт "каталог", в котором будут размещаться методы контроллера
        // определяет, что Content-Type запроса клиента должен быть "application/json"
        //consumes = "application/json; charset=UTF-8",
        produces = "application/json; charset=UTF-8")
// определяет, что возвращать будет "application/json"
@Transactional(propagation = Propagation.REQUIRED) // Тип транзакций для всего класса
public class EdizmController {

    private static final Logger logger = LoggerFactory.getLogger(EdizmController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SpringJdbcConfig springJdbcConfig;

    @Operation(
            summary = "Список единиц измерения",
            description = "Возвращает список единиц измерения <br><br>"
                    + "Фильтры:<br>"
                    + "По id: key=id, value=значение_ид - вернет одну запись<br>"
                    + "По полю флаг блокировки: key=blockFlag, "
                    + "value= (1 - Все, 2 - Только заблокированные, 3 - Только не заблокированные)<br>"
                    + "Только заблокированные: key=onlyBlock value=true"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успех"),
            @ApiResponse(responseCode = "224", description = "Ошибка при выборке данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )})
    @RequestMapping(value = "read", method = RequestMethod.POST)
    public List<EdizmEntity> read(
            @RequestBody(required = false) GelRequestParam gelRequestParam
    ) {
        logger.info("Edizm - read: gelRequestParam = {}", gelRequestParam);
        // Выборка
        String sqlText = ""
                + "SELECT edizm_id,\n"
                + "       edizm_name,\n"
                + "       edizm_notation,\n"
                + "       edizm_blockflag,\n"
                + "       edizm_code \n"
                + "FROM   edizm\n"
                + "WHERE  1=1\n"
                + "/*F00*/ -- Фильтр по id - вернет одну запись\n"
                + "/*F01*/ -- Фильтр по заблокированным\n"
                + "/*F02*/ -- Фильтр по флагу блокировки";
        // Сформируем секцию пагинации
        // Соответствия полей в базе с полями в клиентах
        // todo надо вытащить из аннотаций
        // Проба отправки в репозиторий
        // Соответствия полей в базе с полями в клиентах
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("id", "edizm_id");
        fieldMap.put("name", "edizm_name");
        fieldMap.put("notation", "edizm_notation");
        fieldMap.put("blockflag", "edizm_blockflag");
        fieldMap.put("code", "edizm_code");
        int id = 0; // ид
        // Фильтры
        if (gelRequestParam.getFilters() != null && gelRequestParam.getFilters().size() > 0) {
            // По id
            try {
                id = Integer.parseInt(gelRequestParam.getFilters().get("id"));
            } catch (Exception ignored) {
            }
            if (id != 0) { // Есть фильтр по id
                sqlText = sqlText.replace("/*F00*/", "  AND  edizm_id = " + id);
            } else { // Смысл в остальных фильтрах есть только если нет фильтра по id
                // Только заблокированные
                boolean onlyBlock = Boolean
                        .parseBoolean(gelRequestParam.getFilters().get("onlyBlock"));
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
        }
        if (id == 0) {
            // Смысл в пагинации есть только если не выбираем по id
            sqlText = ConvertUnils.buildPaginationSection(sqlText, gelRequestParam, fieldMap);
        }
        logger.info(sqlText);
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
            throw new FetchQueryException(new Throwable(e));
        }
    }

    @Operation(
            summary = "Возвращает единицу измерения по id",
            description = "Возвращаемые ошибки такие же как в read"
    )
    @RequestMapping(
            value = "readbyid/{id}",
            method = RequestMethod.GET)
    public EdizmEntity readById(
            @Parameter(description = "edizm_id")
            @PathVariable("id") Integer id
    ) {
        //id = 15;
        GelRequestParam gelRequestParam = new GelRequestParam(); // Параметры запроса
        gelRequestParam.setFilters(Map.of("id", String.valueOf(id))); // добавим фильтр по id
        List<EdizmEntity> edizmEntityList = read(gelRequestParam);
        if (edizmEntityList.size() == 1) {
            return edizmEntityList.get(0); // Вернем первый элемент
        }
        throw new RecordNotFoundException("Запись с edizm_id = " + id + " не найдена");
    }

    @Operation(
            summary = "Возвращает единицу измерения для добавления",
            description = "Возвращаемые ошибки такие же как в read"
    )
    @RequestMapping(
            value = "readforadd",
            method = RequestMethod.GET)
    public EdizmEntity readForAdd(
    ) {
        EdizmEntity edizmEntity = new EdizmEntity();
        //edizmEntity.name = "Наименование"; // просто чтобы попробовать
        return edizmEntity;
    }

    @Operation(
            summary = "Добавление единимцы измерения в базу данных",
            description = "Добавляет единицу измерения в базу данных"
    )
    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public EdizmEntity insert(
            @RequestBody EdizmEntity edizmEntity
    ) {
        // https://www.baeldung.com/spring-jdbc-jdbctemplate
        //DataBinder dataBinder = new DataBinder(edizmEntity);
        // todo Тут можно вставить валидатор
        if (edizmEntity.id == null || edizmEntity.id == 0) { // Получим id
            edizmEntity.id = DatabaseUtils.getSequenceNextValue("edizm_id_gen", jdbcTemplate);
        }
        if (edizmEntity.blockflag == null) edizmEntity.blockflag = 0;
        String sqlText = ""
                + "INSERT INTO edizm ("
                + "  edizm_id,\n"
                + "  edizm_name,\n"
                + "  edizm_notation,\n"
                + "  edizm_blockflag,\n"
                + "  edizm_code\n"
                + ") VALUES (?, ?, ?, ?, ?)";
        try {
            Integer i = jdbcTemplate.update(sqlText,
                    edizmEntity.id,
                    edizmEntity.name,
                    edizmEntity.notation,
                    edizmEntity.blockflag,
                    edizmEntity.code);
            logger.info(sqlText + " " + edizmEntity.toString() + " code = " + i);
        } catch (Exception e) {
            throw new SaveRecordException("Ошибка при добавлении записи в таблицу edizm",
                    new Throwable(e));
        }
        return edizmEntity;
    }

    @Operation(
            summary = "Изменение единимцы измерения в базе данных",
            description = "Изменяет единицу измерения в базе данных"
    )
    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EdizmEntity update(
            @RequestBody EdizmEntity edizmEntity
    ) {
        logger.info("updating " + edizmEntity.toString());
        //DataBinder dataBinder = new DataBinder(edizmEntity);
        // todo Тут можно вставить валидатор
        String sqlText = ""
                + "UPDATE edizm SET"
                + "  edizm_name = ?,\n"
                + "  edizm_notation = ?,\n"
                + "  edizm_blockflag = ?,\n"
                + "  edizm_code = ?\n"
                + "WHERE edizm_id = ?";
        try {
            Integer i = jdbcTemplate.update(sqlText,
                    edizmEntity.name,
                    edizmEntity.notation,
                    edizmEntity.blockflag,
                    edizmEntity.code,
                    edizmEntity.id
            );
            logger.info(sqlText + " " + edizmEntity.toString() + " code = " + i);
        } catch (Exception e) {
            throw new SaveRecordException("Ошибка при изменении записи в таблицу edizm",
                    new Throwable(e));
        }
        return edizmEntity;
    }

    @Operation(
            summary = "Удаление единимцы измерения из базы данных",
            description = "Удаляет единицы измерения из базы данных, перечисленные через запятую в url"
    )
    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "delete/{ids}", method = RequestMethod.POST)
    public void delete(
            @Parameter(description = "список edizm_id через запятую", example = "1,2,3")
            @PathVariable("ids") String ids
    ) {
        String sqlText = ""
                + "DELETE FROM edizm \n"
                + "WHERE edizm_id = ?";
        for (String s : ids.replaceAll("\\s+", "").split(",")) {
            Integer id = Integer.parseInt(s);
            try {
                jdbcTemplate.update(sqlText,id);
            } catch (Exception e) {
                throw new SaveRecordException(
                        "Ошибка при удалении записи из таблицы edizm с edizm_id = " + id,
                        new Throwable(e));
            }
        }
    }

}

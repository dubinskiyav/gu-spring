package biz.gelicon.guspring.controllers;

import biz.gelicon.guspring.entities.EdizmEntity;
import biz.gelicon.guspring.utils.GelRequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @RequestBody GelRequestParam gelRequestParam
    ) {
        logger.info("Edizm - read: gelRequestParam = " + "gelRequestParam");
        // Выборка
        String sqlText = "\n"
                + "SELECT edizm_id,\n"
                + "       edizm_name,\n"
                + "       edizm_notation,\n"
                + "       edizm_blockflag,\n"
                + "       edizm_code \n"
                + "FROM   edizm ";
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
    }


}

package biz.gelicon.guspring.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// https://tproger.ru/translations/useful-postgresql-commands/

/**
 * Различные методы работы с базой данных
 */
public class DatabaseUtils {

    static Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    public static String dbType = null; // "postgresql"; // Тип текущей БД
    public static Boolean trySequence = true; // Пробуем получить id из последовательностей

    /**
     * Формирует текст ошибки из исключения в читаемом виде todo Не доделана
     *
     * @param e Исключение
     * @return читаемый текст
     */
    public static String makeErrorMessage(Exception e) {
        if (e == null) {return "Ошибка";}
        String s = e.getMessage();
        if (e instanceof DuplicateKeyException) {
            s = "Нарушение уникальности";
        } else if (e instanceof DataIntegrityViolationException) {
            s = "Нарушение ссылочной целостности. Возможно, на запись есть ссылки.";
        }
        return s;
    }

    /**
     * Формирует текст ошибки как он вернулся из базы
     *
     * @param e Исключение
     * @return текст
     */
    public static String makeErrorMessageFull(Exception e) {
        if (e == null) {return "Ошибка";}
        return e.getMessage();
    }

    /**
     * Возвращает следующее значение генератора <p> Для PostgreSQL пытается получить из
     * последовательности, иначе - случайное целое число
     *
     * @param sequenceName имя последодвательности, например town_id_gen
     * @param jdbcTemplate любая рабочая
     * @return следующее значение для пк
     */
    public static Integer getSequenceNextValue(
            String sequenceName,
            JdbcTemplate jdbcTemplate
    ) {
        if (trySequence) { // Если хотим через последовательности
            if (sequenceName != null && jdbcTemplate != null) { // Есть откуда и есть через что
                if (isPostgreSQL(jdbcTemplate)) { // Сделано пока только доя PostgreSQL
                    // Проверим, есть ли такая последовательность
                    if (jdbcTemplate.queryForObject(""
                                    + " SELECT COUNT(*) "
                                    + " FROM   information_schema.sequences\n"
                                    + " WHERE  sequence_name = '" + sequenceName + "'",
                            Integer.class) == 1) {
                        // Есть
                        return jdbcTemplate.queryForObject(
                                "SELECT nextval('" + sequenceName + "')",
                                Integer.class);
                    }
                }
            }
        }
        // Иначе и в далльшейшем полагаемся на случай
        //trySequence = false;
        // integer	                              -2147483648...+2147483647
        return ThreadLocalRandom.current().nextInt(1000000000) + 1000000000;
    }

    /**
     * Возвращает тип базы данных
     *
     * @return тип базы данных (значение поля dbType)
     */
    public static String getDbType() {
        if (dbType == null) {
            // Установим переменную
            // todo - сделать
            dbType = "postgresql";
        }
        return dbType;
    }

    /**
     * Возвращает наименование драйвера для JdbcTemplate
     *
     * @param jdbcTemplate
     * @return наименование драйвера базы данных
     */
    public static String getDbType(JdbcTemplate jdbcTemplate) {
        // todo сделать
        if (false) {
            DataSource dataSource = jdbcTemplate.getDataSource();
            try {
                Connection connection = dataSource.getConnection();
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                String databaseProductName = databaseMetaData.getDatabaseProductName();
                String driverName = databaseMetaData.getDriverName();
                return driverName;
                //return jdbcTemplate.getDataSource().getConnection().getMetaData().getDriverName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return "postgresql";
    }


    /**
     * Проверяет, не PostgreSQL ли использутеся
     *
     * @return true если PostgreSQL
     */
    public static Boolean isPostgreSQL() {
        return getDbType().contains("postgresql");
    }

    /**
     * Проверяет, не PostgreSQL ли использутеся для JdbcTemplate
     *
     * @param jdbcTemplate Темплейта
     * @return true если PostgreSQL
     */
    public static Boolean isPostgreSQL(JdbcTemplate jdbcTemplate) {
        return getDbType(jdbcTemplate).toLowerCase().contains("postgresql");
    }

    /**
     * Устанавливает тип СУБД из jdbcTemplate
     *
     * @param jdbcTemplate Темплейта
     */
    public static void setDbType(JdbcTemplate jdbcTemplate) {
        dbType = null; // Обнулим все что было
        if (jdbcTemplate == null || jdbcTemplate.getDataSource() == null) {
            return;
        }
        Connection connection;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
        } catch (SQLException e) {
            logger.error("jdbcTemplate.getDataSource().getConnection() filed", e);
            throw new RuntimeException("jdbcTemplate.getDataSource().getConnection() filed", e);
        }
        String dbDriverName;
        try {
            dbDriverName = connection.getMetaData().getDriverName().toLowerCase();
        } catch (SQLException e) {
            logger.error("connection.getMetaData().getDriverName().toLowerCase() filed", e);
            throw new RuntimeException(
                    "connection.getMetaData().getDriverName().toLowerCase() filed", e);
        }
        if (dbDriverName.contains("postgresql")) {
            dbType = "postgresql";
        }
        logger.info("DbType=" + dbType);
    }

    /**
     * Возвращает connection из jdbcTemplate
     *
     * @param jdbcTemplate Темплейта
     * @return connection
     */
    public static Connection getJdbcTemplateConnection(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null || jdbcTemplate.getDataSource() == null) {
            logger.error("jdbcTemplate.getDataSource().getConnection() filed");
            throw new RuntimeException("No connection");
        }
        Connection connection;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
        } catch (SQLException e) {
            logger.error("jdbcTemplate.getDataSource().getConnection() filed", e);
            throw new RuntimeException("jdbcTemplate.getDataSource().getConnection() filed", e);
        }
        if (connection == null) {
            logger.error("connection is null");
            throw new RuntimeException("connection is null");
        }
        return connection;
    }

    /**
     * List внешних ключей таблицы
     *
     * @param tableName    имя таблицы
     * @param jdbcTemplate Темплейта
     * @return List<String> из внешних ключей таблицы
     */
    public static List<String> getForeignKeyTable(
            String tableName,
            JdbcTemplate jdbcTemplate
    ) {
        if (tableName == null || jdbcTemplate == null) {return null;}
        Connection connection = getJdbcTemplateConnection(jdbcTemplate);
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(""
                    + " SELECT TC.table_name,                                   "
                    + "        KCU.column_name,                                 "
                    + "        CCU.table_name master_table_name,                "
                    + "        CCU.column_name master_column_name,              "
                    + "        TC.constraint_name                               "
                    + " FROM   information_schema.table_constraints TC,         "
                    + "        information_schema.key_column_usage KCU,         "
                    + "        information_schema.constraint_column_usage CCU   "
                    + " WHERE  TC.table_name = ?                                "
                    + "   AND  TC.constraint_type = 'FOREIGN KEY'               "
                    + "   AND  KCU.constraint_name = TC.constraint_name         "
                    + "   AND  KCU.table_schema = TC.table_schema               "
                    + "   AND  CCU.constraint_name = TC.constraint_name         "
                    + "   AND  CCU.table_schema = TC.table_schema               ");
            //SELECT T.relname table_name,
            //       A.attname column_name,
            //       TF.relname master_table_name,
            //       AM.attname master_column_name,
            //       TC.conname constraint_name
            //FROM   pg_catalog.pg_class T,
            //       pg_catalog.pg_constraint TC,
            //       pg_catalog.pg_class TF,
            //       pg_catalog.pg_attribute A,
            //       pg_catalog.pg_constraint TM,
            //       pg_catalog.pg_attribute AM
            //WHERE  T.relname = 'statement'
            //  AND  TC.conrelid = T.oid
            //  AND  TC.contype = 'f'
            //  AND  TF.oid = TC.confrelid
            //  AND  A.attrelid = T.oid
            //  AND  A.attnum = TC.conkey[1]
            //  AND  A.attname = 'methoddeliv_id'
            //  AND  TM.conrelid = TF.oid
            //  AND  TM.contype = 'p'
            //  AND  AM.attrelid = TM.conrelid
            //  AND  AM.attnum = 1


            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                list.add(rs.getString("table_name")
                        + "," + rs.getString("column_name")
                        + "," + rs.getString("master_table_name")
                        + "," + rs.getString("master_column_name")
                        + "," + rs.getString("constraint_name"));
            }
            ps.close();
        } catch (Exception e) {
            String errText = String.format("Retriving foreign key for table %s filed", tableName);
            logger.error(errText, e);
            throw new RuntimeException(errText, e);
        }
        return list;
    }

    /**
     * List внешних ключей на таблицу
     *
     * @param masterTableName Таблица, на которую хотим получить внешние ключи
     * @param jdbcTemplate    Темплейта
     * @return List<String> в формате <p> table_name,column_name,master_table_name,master_column_name,constraint_name
     */
    public static List<String> getForeignKeyMasterTable(
            String masterTableName,
            JdbcTemplate jdbcTemplate
    ) {
        if (masterTableName == null || jdbcTemplate == null) {return null;}
        Connection connection = getJdbcTemplateConnection(jdbcTemplate);
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(""
                    + " SELECT TC.table_name, "
                    + "        KCU.column_name, "
                    + "        CCU.table_name master_table_name, "
                    + "        CCU.column_name master_column_name, "
                    + "        TC.constraint_name "
                    + " FROM   information_schema.table_constraints TC, "
                    + "        information_schema.key_column_usage KCU, "
                    + "        information_schema.constraint_column_usage CCU "
                    + " WHERE  CCU.table_name = ? "
                    + "   AND  TC.constraint_type = 'FOREIGN KEY' "
                    + "   AND  KCU.constraint_name = TC.constraint_name  "
                    + "   AND  KCU.table_schema = TC.table_schema "
                    + "   AND  CCU.constraint_name = TC.constraint_name  "
                    + "   AND  CCU.table_schema = TC.table_schema ");
            ps.setString(1, masterTableName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                list.add(rs.getString("table_name")
                        + "," + rs.getString("column_name")
                        + "," + rs.getString("master_table_name")
                        + "," + rs.getString("master_column_name")
                        + "," + rs.getString("constraint_name"));
            }
            ps.close();
        } catch (Exception e) {
            String errText = String
                    .format("Retriving foreign key for master table %s filed", masterTableName);
            logger.error(errText, e);
            throw new RuntimeException(errText, e);
        }
        return list;
    }

    public static void setSequence(String sequenceName, Integer value, JdbcTemplate jdbcTemplate) {
        if (!isPostgreSQL(jdbcTemplate)) {
            throw new RuntimeException("Установка последовательности не реализована для данного типа СУБД");
        }
        String sqlText = " ALTER SEQUENCE " + sequenceName + " RESTART WITH " + value;
        jdbcTemplate.update(sqlText);
    }

    public static boolean checkSequenceExist(String sequenceName, JdbcTemplate jdbcTemplate) {
        if (!isPostgreSQL(jdbcTemplate)) {
            throw new RuntimeException("Проверка существования последовательности не реализована для данного типа СУБД");
        }
        String sqlText = ""
                + " SELECT COUNT(*)\n"
                + " FROM   pg_class \n"
                + " WHERE  relname = '" + sequenceName + "'";
        return jdbcTemplate.queryForObject(sqlText, Integer.class) == 1;
    }

    /**
     * Выполнение SQL операбота
     * @param sqlText
     * @param jdbcTemplate
     */
    public static void executeSql(String sqlText, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update(sqlText);
    }

    public static boolean checkTableExist(String tableName, JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        String sqlText = "SELECT COUNT(*) \n"
                + "FROM   information_schema.tables\n"
                + "WHERE  table_name = '" + tableName + "'";
        return jdbcTemplate.queryForObject(sqlText, Integer.class) == 1;
    }

}

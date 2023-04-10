package org.catsonkeyboard;

import com.google.gson.*;
import io.jooby.Jooby;

import io.jooby.hibernate.HibernateModule;
import io.jooby.hikari.HikariModule;
import io.jooby.json.GsonModule;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

public class App extends Jooby {
    {
        install(new HikariModule());

        install(new HibernateModule());

        install(new GsonModule());

        post("/query", ctx -> {
            Gson gson = require(Gson.class);
            JsonObject requestBody = new JsonParser().parse(ctx.body().value().toString()).getAsJsonObject();
            JsonObject responseBody = new JsonObject();
            for(var topic : requestBody.entrySet()) {
                String topicStr = topic.getKey();
                JsonObject topicValue = topic.getValue().getAsJsonObject();
                String filter = topicValue.get("filter").getAsString();
                Integer lut = topicValue.get("lut").getAsInt();
                EntityManager em = require(EntityManager.class);
                String queryColumns = "SELECT column_name FROM information_schema.COLUMNS WHERE table_name = '" + topicStr + "' AND table_schema = (SELECT DATABASE()) ORDER BY ordinal_position";
                Session session = em.unwrap(Session.class);
                List columns = session.createSQLQuery(queryColumns).list();
                String columnsStr = String.join(",", columns);
                String queryStr;
                if(filter != null && filter.length() > 0) {
                    queryStr = "SELECT "+ columnsStr +" FROM " + topicStr + " WHERE " + filter + " AND datachange_last_time >= '" + getDateTimeOfTimestamp(lut) + "'";
                } else {
                    queryStr = "SELECT " + columnsStr + " FROM " + topicStr + " WHERE datachange_last_time >= '" + getDateTimeOfTimestamp(lut) + "'";
                }
                List topicData = session.createSQLQuery(queryStr).list();
                JsonArray jsonArray = new JsonArray();
                //将数据组装成json
                for(var data : topicData) {
                    JsonObject topicDataJson = new JsonObject();
                    var columnDataList = (Object[])data;
                    int index = 0;
                    for(var columnData : columnDataList) {
                        if("datachange_last_time".equals(columns.get(index)) || "datachange_create_time".equals(columns.get(index))) {
                            columnData = ((Timestamp)columnData).getTime();
                        }
                        JsonElement jsonElement = gson.toJsonTree(columnData);
                        topicDataJson.add((String) columns.get(index), jsonElement);
                        index++;
                    }
                    jsonArray.add(topicDataJson);
                }
                responseBody.add(topicStr, jsonArray);
            }
            return responseBody;
        });
    }

    public Long getCurrentTimeTimestamp() {
        return LocalDateTime.now().atZone(ZoneOffset.of("+8")).toEpochSecond();
    }

    public long getTimestampFromLocalDateTime(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.ofHours(8));
    }

    public  LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
    }

    public static void main(String[] args) {
        runApp(args, App::new);
    }
}
package com.urlino.urlino.repository;

//import com.google.cloud.bigtable.hbase.BigtableConfiguration;
//import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.client.*;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.springframework.stereotype.Repository;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;
import com.urlino.urlino.entity.UrlMappingEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UrlMappingRepository {
    private static final String PROJECT_ID = "test-project";
    private static final String INSTANCE_ID = "test-instance";
    private static final String TABLE_NAME = "url_mapping";

    private BigtableDataClient dataClient;

    public UrlMappingRepository() throws Exception {
//        dataClient = BigtableDataClient.create(PROJECT_ID, INSTANCE_ID);
        BigtableDataSettings settings = BigtableDataSettings.newBuilderForEmulator(8086) // 使用本地 Emulator，端口 8086
                .setProjectId(PROJECT_ID)
                .setInstanceId(INSTANCE_ID)
                .build();
        dataClient = BigtableDataClient.create(settings);
    }

    public void saveMapping(UrlMappingEntity entity) throws Exception {
        RowMutation rowMutation = RowMutation.create(TABLE_NAME, entity.getShortUrl())
                .setCell("mapping", "long_url", entity.getLongUrl())
                .setCell("mapping", "user_id", entity.getUserId())
                .setCell("mapping", "create_time", String.valueOf(entity.getCreateTime().getTime()))
                .setCell("mapping", "click_count", String.valueOf(entity.getClickCount()));
        dataClient.mutateRow(rowMutation);
    }

    public UrlMappingEntity getMapping(String shortUrl) throws Exception {
        Row row = dataClient.readRow(TABLE_NAME, ByteString.copyFromUtf8(shortUrl));
        if (row == null) {
            return null;
        }
        String longUrl = row.getCells("mapping", "long_url").get(0).getValue().toStringUtf8();
        String userId = row.getCells("mapping", "user_id").get(0).getValue().toStringUtf8();
        long createTimeMillis = Long.parseLong(row.getCells("mapping", "create_time").get(0).getValue().toStringUtf8());
        int clickCount = Integer.parseInt(row.getCells("mapping", "click_count").get(0).getValue().toStringUtf8());

        UrlMappingEntity entity = new UrlMappingEntity();
        entity.setShortUrl(shortUrl);
        entity.setLongUrl(longUrl);
        entity.setUserId(userId);
        entity.setCreateTime(new java.util.Date(createTimeMillis));
        entity.setClickCount(clickCount);
        return entity;
    }

    /**
     * Delete row in urlmapping table
     * @param shortUrl
     * @throws Exception
     */
    public void deleteMapping(String shortUrl) throws Exception {
        RowMutation rowMutation = RowMutation.create(TABLE_NAME, shortUrl).deleteRow();
        dataClient.mutateRow(rowMutation);
    }

    public void incrementClick(String shortUrl) throws Exception {
        UrlMappingEntity entity = getMapping(shortUrl);
        if (entity == null) return;
        int count = entity.getClickCount() + 1;
        RowMutation rowMutation = RowMutation.create(TABLE_NAME, shortUrl)
                .setCell("mapping", "click_count", String.valueOf(count));
        dataClient.mutateRow(rowMutation);
    }

    public void close() throws Exception {
        if (dataClient != null) {
            dataClient.close();
        }
    }
}

//@Repository
//public class UrlMappingRepository {
//
//    private static final String PROJECT_ID = "test-project";
//    private static final String INSTANCE_ID = "test-instance";
//    private static final String TABLE_NAME = "url_mapping";
//    private static final String CF_MAPPING = "mapping";
//
//    private Connection connection;
//
//    public UrlMappingRepository() throws Exception {
//        this.connection = BigtableConfiguration.connect(PROJECT_ID, INSTANCE_ID);
//    }
//
//    public void createMapping(String userId, String shortUrl, String longUrl) throws Exception {
//        String compositeRowKey = userId + "#" + shortUrl;
//        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
//        Put put = new Put(Bytes.toBytes(compositeRowKey));
//        put.addColumn(Bytes.toBytes(CF_MAPPING), Bytes.toBytes("long_url"), Bytes.toBytes(longUrl));
//        put.addColumn(Bytes.toBytes(CF_MAPPING), Bytes.toBytes("click_count"), Bytes.toBytes(0));
//        put.addColumn(Bytes.toBytes(CF_MAPPING), Bytes.toBytes("create_time"), Bytes.toBytes(System.currentTimeMillis()));
//        table.put(put);
//        table.close();
//    }
//
//    public String getLongUrl(String userId, String shortUrl) throws Exception {
//        String compositeRowKey = userId + "#" + shortUrl;
//        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
//        Get get = new Get(Bytes.toBytes(compositeRowKey));
//        Result result = table.get(get);
//        table.close();
//        byte[] value = result.getValue(Bytes.toBytes(CF_MAPPING), Bytes.toBytes("long_url"));
//        return value != null ? Bytes.toString(value) : null;
//    }
//
//    public void incrementClick(String userId, String shortUrl) throws Exception {
//        String compositeRowKey = userId + "#" + shortUrl;
//        Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
//        Get get = new Get(Bytes.toBytes(compositeRowKey));
//        Result result = table.get(get);
//        int clickCount = 0;
//        if (!result.isEmpty()) {
//            byte[] countBytes = result.getValue(Bytes.toBytes(CF_MAPPING), Bytes.toBytes("click_count"));
//            if (countBytes != null) {
//                clickCount = Bytes.toInt(countBytes);
//            }
//        }
//        clickCount++;
//        Put put = new Put(Bytes.toBytes(compositeRowKey));
//        put.addColumn(Bytes.toBytes(CF_MAPPING), Bytes.toBytes("click_count"), Bytes.toBytes(clickCount));
//        table.put(put);
//        table.close();
//    }
//
//    public void close() throws Exception {
//        if (connection != null) {
//            connection.close();
//        }
//    }
//}

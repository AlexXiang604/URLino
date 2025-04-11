package com.urlino.urlino.repository;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;
import com.urlino.urlino.entity.ReverseMappingEntity;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class ReverseMappingRepository {
    private static final String PROJECT_ID = "rice-comp-539-spring-2022";
    private static final String INSTANCE_ID = "comp-539-bigtable";
    private static final String TABLE_NAME = "URLino_reverse_mapping";
//    private static final String CF_INDEX = "index";
//    private static final String COL_SHORT_URL = "short_url";

    private BigtableDataClient dataClient;

    public ReverseMappingRepository() throws IOException {
//        dataClient = BigtableDataClient.create(PROJECT_ID, INSTANCE_ID);
//        BigtableDataSettings settings = BigtableDataSettings.newBuilderForEmulator(8086) // 使用本地 Emulator，端口 8086
        BigtableDataSettings settings = BigtableDataSettings.newBuilder()
                .setProjectId(PROJECT_ID)
                .setInstanceId(INSTANCE_ID)
                .build();
        dataClient = BigtableDataClient.create(settings);
    }

    /**
     * 保存反向索引记录，行键格式："userId#hash(longUrl)"
     */
    public void saveIndex(ReverseMappingEntity entity) {
        RowMutation rowMutation = RowMutation.create(TABLE_NAME, entity.getIndexKey())
                .setCell("index", "short_url", entity.getShortUrl());
        dataClient.mutateRow(rowMutation);
    }

    /**
     * Delete row in reversemapping table
     * @param indexKey
     * @throws Exception
     */
    public void deleteIndex(String indexKey) throws Exception {
        RowMutation rowMutation = RowMutation.create(TABLE_NAME, indexKey).deleteRow();
        dataClient.mutateRow(rowMutation);
    }

    /**
     * 根据索引行键获取短链
     */
    public String getShortUrl(String indexKey) {
        Row row = dataClient.readRow(TABLE_NAME, indexKey);
        if (row == null) return null;
        List<RowCell> cells = row.getCells("index", ByteString.copyFromUtf8("short_url"));
        if (cells == null || cells.isEmpty()) return null;
        return cells.get(0).getValue().toStringUtf8();
    }

    public void close() throws Exception {
        if (dataClient != null) {
            dataClient.close();
        }
    }
}

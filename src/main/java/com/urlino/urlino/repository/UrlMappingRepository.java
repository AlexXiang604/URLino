package com.urlino.urlino.repository;

import com.urlino.urlino.model.UrlMapping;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class UrlMappingRepository {
    private static final String TABLE_ID = "team4URLino";
    private static final String COLUMN_FAMILY = "Mapping";

    private BigtableDataClient Client;

    public void BigtableManager(String projectId, String instanceId) throws IOException {
        this.Client = BigtableDataClient.create(projectId, instanceId);
    }

    void close() {
        this.Client.close();
    }

    public void saveMapping(UrlMapping urlMapping) {
        RowMutation mutation = RowMutation.create(TABLE_ID, urlMapping.getId()).setCell(
                COLUMN_FAMILY, "longurl", urlMapping.getLongUrl()
        );
        Client.mutateRow(mutation);
    }

    public String findLongUrlById(String id) {
        // Retrieve the row from Bigtable using the row key (id)
        Row row = Client.readRow(TABLE_ID, id);

        // If no row is found, return null or handle the error as needed
        if (row == null) {
            return null;
        }

        // Retrieve the cells for the specific column family and column qualifier "longUrl"
        List<RowCell> cells = row.getCells(COLUMN_FAMILY, "longUrl");

        // If no cells are found, return null
        if (cells == null || cells.isEmpty()) {
            return null;
        }

        // Return the value from the first cell as a UTF-8 string
        RowCell cell = cells.get(0);
        return cell.getValue().toStringUtf8();
    }

    public void deleteMapping(String id) {
        RowMutation mutation = RowMutation.create(TABLE_ID, id).deleteRow();
        Client.mutateRow(mutation);
    }
}
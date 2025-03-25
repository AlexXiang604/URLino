package com.urlino.urlino.repository;

import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.*;
import com.urlino.urlino.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private BigtableDataClient dataClient;

    private static final String PROJECT_ID = "test-project";
    private static final String INSTANCE_ID = "test-instance";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_FAMILY_INFO = "info";
    private static final String COLUMN_FAMILY_METADATA = "metadata";

    public UserRepository() throws Exception {
        BigtableDataSettings settings = BigtableDataSettings.newBuilderForEmulator(8086) // 使用本地 Emulator，端口 8086
                .setProjectId(PROJECT_ID)
                .setInstanceId(INSTANCE_ID)
                .build();
        dataClient = BigtableDataClient.create(settings);
    }

    /**
     * 保存用户信息
     */
    public void save(UserEntity user) {
        RowMutation rowMutation = RowMutation.create(TABLE_NAME, user.getUserId())
                .setCell(COLUMN_FAMILY_INFO, "email", user.getEmail())
                .setCell(COLUMN_FAMILY_INFO, "username", user.getUsername())
                .setCell(COLUMN_FAMILY_INFO, "passwordHash", user.getPasswordHash())
                .setCell(COLUMN_FAMILY_METADATA, "isPremium", String.valueOf(user.isPremium()))
                .setCell(COLUMN_FAMILY_METADATA, "createdAt", String.valueOf(user.getCreatedAt()))
                .setCell(COLUMN_FAMILY_METADATA, "updatedAt", String.valueOf(user.getUpdatedAt()));

        dataClient.mutateRow(rowMutation);
    }

    /**
     * 根据用户 ID 查找用户
     */
    public UserEntity findById(String userId) {
        Row row = dataClient.readRow(TABLE_NAME, userId);
        if (row == null) {
            return null;
        }

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setEmail(row.getCells(COLUMN_FAMILY_INFO, "email").get(0).getValue().toStringUtf8());
        user.setUsername(row.getCells(COLUMN_FAMILY_INFO, "username").get(0).getValue().toStringUtf8());
        user.setPasswordHash(row.getCells(COLUMN_FAMILY_INFO, "passwordHash").get(0).getValue().toStringUtf8());
        user.setPremium(Boolean.parseBoolean(row.getCells(COLUMN_FAMILY_METADATA, "isPremium").get(0).getValue().toStringUtf8()));
        user.setCreatedAt(Long.parseLong(row.getCells(COLUMN_FAMILY_METADATA, "createdAt").get(0).getValue().toStringUtf8()));
        user.setUpdatedAt(Long.parseLong(row.getCells(COLUMN_FAMILY_METADATA, "updatedAt").get(0).getValue().toStringUtf8()));

        return user;
    }

    /**
     * 根据用户名查找用户
     */
    public UserEntity findByUsername(String username) {
        Query query = Query.create(TABLE_NAME)
                .filter(Filters.FILTERS.family().exactMatch(COLUMN_FAMILY_INFO))  // 过滤列族
                .filter(Filters.FILTERS.qualifier().exactMatch("username"))       // 过滤列名（username）
                .filter(Filters.FILTERS.value().exactMatch(username));            // 过滤值（username）

        ServerStream<Row> rows = dataClient.readRows(query);
        for (Row row : rows) {
            UserEntity user = new UserEntity();
            user.setUserId(row.getKey().toStringUtf8());  // 设置用户 ID
            user.setEmail(row.getCells(COLUMN_FAMILY_INFO, "email").get(0).getValue().toStringUtf8());  // 设置邮箱
            user.setUsername(row.getCells(COLUMN_FAMILY_INFO, "username").get(0).getValue().toStringUtf8());  // 设置用户名
            user.setPasswordHash(row.getCells(COLUMN_FAMILY_INFO, "passwordHash").get(0).getValue().toStringUtf8());  // 设置密码哈希
            return user;
        }
        return null;  // 如果未找到用户，返回 null
    }


//    public UserEntity findByEmail(String email) {
//        Query query = Query.create(TABLE_NAME)
//                .filter(Filters.FILTERS.family().exactMatch("info"))
//                .filter(Filters.FILTERS.qualifier().exactMatch("email"))
//                .filter(Filters.FILTERS.value().exactMatch(email));
//
//        ServerStream<Row> rows = dataClient.readRows(query);
//        for (Row row : rows) {
//            // 获取存储 email 的 cells 列表
//            List<RowCell> emailCells = row.getCells("info", "email");
//            if (emailCells == null || emailCells.isEmpty()) {
//                // 没有找到 email cell，跳过当前 row
//                continue;
//            }
//
//            // 获取 email 字段值
//            String storedEmail = emailCells.get(0).getValue().toStringUtf8();
//            if (!storedEmail.equals(email)) {
//                continue;
//            }
//
//            // 如果匹配，创建 UserEntity，并安全地获取其他列数据
//            UserEntity user = new UserEntity();
//            user.setUserId(row.getKey().toStringUtf8());
//            user.setEmail(storedEmail);
//
//            // 获取 username（同样要做空判断）
//            List<RowCell> usernameCells = row.getCells("info", "username");
//            if (usernameCells != null && !usernameCells.isEmpty()) {
//                user.setUsername(usernameCells.get(0).getValue().toStringUtf8());
//            }
//
//            // 获取 passwordHash（同样检查）
//            List<RowCell> passwordCells = row.getCells("info", "passwordHash");
//            if (passwordCells != null && !passwordCells.isEmpty()) {
//                user.setPasswordHash(passwordCells.get(0).getValue().toStringUtf8());
//            }
//
//            // 获取其他元数据，同样要检查，例子如下
//            List<RowCell> createdAtCells = row.getCells("metadata", "createdAt");
//            if (createdAtCells != null && !createdAtCells.isEmpty()) {
//                user.setCreatedAt(Long.parseLong(createdAtCells.get(0).getValue().toStringUtf8()));
//            }
//
//            // ... 同理处理 updatedAt, isPremium 等字段
//
//            return user;
//        }
//        return null;
//    }

    public UserEntity findByEmail(String email) {
        // 1. 先找出包含 email 的行（只查 email 字段）
        Query query = Query.create(TABLE_NAME)
                .filter(Filters.FILTERS.chain()
                        .filter(Filters.FILTERS.family().exactMatch("info"))
                        .filter(Filters.FILTERS.qualifier().exactMatch("email"))
                        .filter(Filters.FILTERS.value().exactMatch(email))
                )
                .limit(1); // 最多返回一行

        ServerStream<Row> rows = dataClient.readRows(query);

        for (Row row : rows) {
            String rowKey = row.getKey().toStringUtf8();

            // 2. 用 row key 再读一整行（包含所有字段）
            Row fullRow = dataClient.readRow(TABLE_NAME, rowKey);
            if (fullRow == null) return null;

            UserEntity user = new UserEntity();
            user.setUserId(rowKey);

            List<RowCell> emailCells = fullRow.getCells("info", "email");
            if (!emailCells.isEmpty()) user.setEmail(emailCells.get(0).getValue().toStringUtf8());

            List<RowCell> usernameCells = fullRow.getCells("info", "username");
            if (!usernameCells.isEmpty()) user.setUsername(usernameCells.get(0).getValue().toStringUtf8());

            List<RowCell> passwordCells = fullRow.getCells("info", "passwordHash");
            if (!passwordCells.isEmpty()) user.setPasswordHash(passwordCells.get(0).getValue().toStringUtf8());

            List<RowCell> createdAtCells = fullRow.getCells("metadata", "createdAt");
            if (!createdAtCells.isEmpty()) user.setCreatedAt(Long.parseLong(createdAtCells.get(0).getValue().toStringUtf8()));

            List<RowCell> updatedAtCells = fullRow.getCells("metadata", "updatedAt");
            if (!updatedAtCells.isEmpty()) user.setUpdatedAt(Long.parseLong(updatedAtCells.get(0).getValue().toStringUtf8()));

            List<RowCell> premiumCells = fullRow.getCells("metadata", "isPremium");
            if (!premiumCells.isEmpty()) user.setPremium(Boolean.parseBoolean(premiumCells.get(0).getValue().toStringUtf8()));

            return user;
        }

        return null; // 没找到
    }




//    /**
//     * 根据邮箱和hash的密码查找用户
//     */
//    public UserEntity findByEmailAndPassword(String email, String passwordHash) {
//        // 1. 根据邮箱查找用户
//        Query query = Query.create(TABLE_NAME)
//                .filter(Filters.FILTERS.family().exactMatch(COLUMN_FAMILY_INFO))  // 过滤列族
//                .filter(Filters.FILTERS.qualifier().exactMatch("email"))          // 过滤列名（email）
//                .filter(Filters.FILTERS.value().exactMatch(email));               // 过滤值（email）
//
//        ServerStream<Row> rows = dataClient.readRows(query);
//
//        // 2. 遍历查询结果
//        for (Row row : rows) {
//            // 获取数据库中存储的密码哈希值
//            String storedPasswordHash = row.getCells(COLUMN_FAMILY_INFO, "passwordHash").get(0).getValue().toStringUtf8();
//
//            // 3. 验证密码
//            if (storedPasswordHash.equals(passwordHash)) {
//                // 如果密码匹配，返回用户信息
//                UserEntity user = new UserEntity();
//                user.setUserId(row.getKey().toStringUtf8());
//                user.setEmail(row.getCells(COLUMN_FAMILY_INFO, "email").get(0).getValue().toStringUtf8());
//                user.setUsername(row.getCells(COLUMN_FAMILY_INFO, "username").get(0).getValue().toStringUtf8());
//                user.setPasswordHash(storedPasswordHash);
//                user.setPremium(Boolean.parseBoolean(row.getCells(COLUMN_FAMILY_METADATA, "isPremium").get(0).getValue().toStringUtf8()));
//                user.setCreatedAt(Long.parseLong(row.getCells(COLUMN_FAMILY_METADATA, "createdAt").get(0).getValue().toStringUtf8()));
//                user.setUpdatedAt(Long.parseLong(row.getCells(COLUMN_FAMILY_METADATA, "updatedAt").get(0).getValue().toStringUtf8()));
//                return user;
//            }
//        }
//
//        // 4. 如果未找到用户或密码不匹配，返回 null
//        return null;
//    }


//    public UserEntity findByEmail(String email) {
//        Query query = Query.create(TABLE_NAME)
//                .filter(Filters.FILTERS.family().exactMatch(COLUMN_FAMILY_INFO))
//                .filter(Filters.FILTERS.qualifier().exactMatch("email"))
//                .filter(Filters.FILTERS.value().exactMatch(email));
//
//        ServerStream<Row> rows = dataClient.readRows(query);
//        for (Row row : rows) {
//            UserEntity user = new UserEntity();
//            user.setUserId(row.getKey().toStringUtf8());
//            user.setEmail(row.getCells(COLUMN_FAMILY_INFO, "email").get(0).getValue().toStringUtf8());
//            user.setUsername(row.getCells(COLUMN_FAMILY_INFO, "username").get(0).getValue().toStringUtf8());
//            user.setPasswordHash(row.getCells(COLUMN_FAMILY_INFO, "passwordHash").get(0).getValue().toStringUtf8());
//            user.setPremium(Boolean.parseBoolean(row.getCells(COLUMN_FAMILY_METADATA, "isPremium").get(0).getValue().toStringUtf8()));
//            user.setCreatedAt(Long.parseLong(row.getCells(COLUMN_FAMILY_METADATA, "createdAt").get(0).getValue().toStringUtf8()));
//            user.setUpdatedAt(Long.parseLong(row.getCells(COLUMN_FAMILY_METADATA, "updatedAt").get(0).getValue().toStringUtf8()));
//            return user;
//        }
//        return null;
//    }




}

//===================================
////使用mongodb
//import com.urlino.urlino.entity.UserEntity;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import java.util.Optional;
//
//public interface UserRepository extends MongoRepository<UserEntity, String> {
//    /**
//     * 根据用户 ID 查找用户
//     *
//     * @param userId 用户 ID
//     * @return 用户信息
//     */
//    Optional<UserEntity> findById(String userId);
//
//    /**
//     * 根据邮箱查找用户
//     *
//     * @param email 邮箱
//     * @return 用户信息
//     */
//    Optional<UserEntity> findByEmail(String email);
//
//    /**
//     * 根据用户名查找用户
//     *
//     * @param username 用户名
//     * @return 用户信息
//     */
//    Optional<UserEntity> findByUsername(String username);
//}

//=========================================
//使用mysql的情况
//import com.urlino.urlino.entity.UserEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<UserEntity, String> {
//    /**
//     * 根据用户 ID 查找用户
//     *
//     * @param userId 用户 ID
//     * @return 用户信息
//     */
//    Optional<UserEntity> findById(String userId);
//}
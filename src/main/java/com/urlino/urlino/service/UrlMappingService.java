package com.urlino.urlino.service;

import com.urlino.urlino.dto.UrlMappingDTO;
import com.urlino.urlino.entity.ReverseMappingEntity;
import com.urlino.urlino.entity.UrlMappingEntity;
import com.urlino.urlino.repository.ReverseMappingRepository;
import com.urlino.urlino.repository.UrlMappingRepository;
import com.urlino.urlino.service.impl.UserServiceImpl;
import com.urlino.urlino.util.ShortUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Calendar;
import java.util.Date;

@Service
public class UrlMappingService {
    @Autowired
    private UrlMappingRepository mapRepo;

    @Autowired
    private ReverseMappingRepository reverseMapRepo;

    @Autowired
    private UserServiceImpl userService;  // 用于获取用户类型（免费/付费）

    /**
     * 创建 URL 映射
     * 如果 alias 存在且为5-12位，则采用 alias，否则随机生成8位短链
     * 先通过反向索引查重，如果存在则返回已有的短链
     */
    public String createMapping(String userId, String longUrl, Optional<String> aliasOpt) throws Exception {
        // 计算 longUrl 的 MD5 hash（取前8位）用于构造反向索引键
        String longUrlHash = ShortUrlGenerator.hashLongUrl(longUrl);
        String indexKey = userId + "#" + longUrlHash;

        // 如果提供了 alias，进行校验
        if (aliasOpt.isPresent()) {
            String alias = aliasOpt.get();

            // 校验 alias 长度
            if (alias.length() < 5 || alias.length() > 12) {
                throw new IllegalArgumentException("Alias must be 5-12 characters.");
            }

            // 校验 alias 格式（仅允许 Base62 字符）
            if (!alias.matches("^[a-zA-Z0-9]+$")) {
                throw new IllegalArgumentException("This alias format is invalid, only a-z, A-Z, 0-9 are allowed.");
            }

//            // 全局唯一检查：在映射表中查找该 alias 是否已存在
//            if (mapRepo.getMapping(alias) != null) {
//                throw new IllegalArgumentException("This alias has been taken, please try another one.");
//            }
            // 先检查当前用户是否已对该长链和 alias 建立映射; 没有的话对alias进行全局唯一检查：在映射表中查找该 alias 是否已存在
            String existingShortUrl = reverseMapRepo.getShortUrl(indexKey);
            if (existingShortUrl != null && existingShortUrl.equals(alias)) {
                return existingShortUrl;
            } else if (mapRepo.getMapping(alias) != null){
                throw new IllegalArgumentException("This alias has been taken, please try another one.");
            }
        }

//        // 计算 longUrl 的 MD5 hash（取前8位）用于构造反向索引键
//        String longUrlHash = ShortUrlGenerator.hashLongUrl(longUrl);
//        String indexKey = userId + "#" + longUrlHash;

        // 查询反向索引表，看是否已存在记录
        String existingShortUrl = reverseMapRepo.getShortUrl(indexKey);
        if (existingShortUrl != null) {
            return existingShortUrl;
        }

        // 采用 alias（如果符合条件）或生成唯一的随机短链
        String shortUrl = aliasOpt.orElse(generateUniqueShortUrl());

        // 非付费用户生成短链10秒过期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 20);
        Date expireAt = calendar.getTime();
        System.out.println(expireAt);

        // 保存到全局映射表
        UrlMappingEntity mappingEntity = new UrlMappingEntity(shortUrl, longUrl, userId, expireAt);
        mapRepo.saveMapping(mappingEntity);

        // 保存反向索引记录
        ReverseMappingEntity indexEntity = new ReverseMappingEntity(indexKey, shortUrl);
        reverseMapRepo.saveIndex(indexEntity);

        return shortUrl;
    }

    /**
     * 生成唯一的随机短链
     */
    private String generateUniqueShortUrl() throws Exception {
        String shortUrl;
        int maxAttempts = 5;  // 最大尝试次数，避免无限循环
        int attempts = 0;

        do {
            shortUrl = ShortUrlGenerator.generateShortUrl();
            attempts++;
        } while (mapRepo.getMapping(shortUrl) != null && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            throw new Exception("Failed to generate a unique short URL after " + maxAttempts + " attempts.");
        }

        return shortUrl;
    }

    /**
     * Allow paid user to edit the shorturl for a longurl, but only allow one specific shorturl
     * for each longurl, no matter the shorturl is randomly genrated or alias
     *
     * @param userId
     * @param longUrl
     * @param aliasOpt
     * @return
     * @throws Exception
     */
    public String editShortUrl(String userId, String longUrl, Optional<String> aliasOpt) throws Exception {
        // 仅限付费用户
        if (!userService.isPremiumUser(userId)) {
            throw new IllegalArgumentException("Only premium users can edit short URLs.");
        }

        // 计算 longUrl 的 MD5 hash（取前8位）用于构造反向索引键
        String longUrlHash = ShortUrlGenerator.hashLongUrl(longUrl);
        String indexKey = userId + "#" + longUrlHash;

        // 如果提供了 alias，进行校验
        if (aliasOpt.isPresent()) {
            String alias = aliasOpt.get();

            // 校验 alias 长度
            if (alias.length() < 5 || alias.length() > 12) {
                throw new IllegalArgumentException("Alias must be 5-12 characters.");
            }

            // 校验 alias 格式（仅允许 Base62 字符）
            if (!alias.matches("^[a-zA-Z0-9]+$")) {
                throw new IllegalArgumentException("This alias format is invalid, only a-z, A-Z, 0-9 are allowed.");
            }

//            // 全局唯一检查：在映射表中查找该 alias 是否已存在
//            if (mapRepo.getMapping(alias) != null) {
//                throw new IllegalArgumentException("This alias has been taken, please try another one.");
//            }

            // 先检查当前用户是否已对该长链和 alias 建立映射; 没有的话对alias进行全局唯一检查：在映射表中查找该 alias 是否已存在
            String existingShortUrl = reverseMapRepo.getShortUrl(indexKey);
            if (existingShortUrl != null && existingShortUrl.equals(alias)) {
                return existingShortUrl;
            } else if (mapRepo.getMapping(alias) != null){
                throw new IllegalArgumentException("This alias has been taken, please try another one.");
            }
        }

//        // 计算 longUrl 的 MD5 hash（取前8位）用于构造反向索引键
//        String longUrlHash = ShortUrlGenerator.hashLongUrl(longUrl);
//        String indexKey = userId + "#" + longUrlHash;

        // 查询反向索引表，获取旧的短链
        String oldShortUrl = reverseMapRepo.getShortUrl(indexKey);
        if (oldShortUrl == null) {
            // 采用 alias（如果符合条件）或生成唯一的随机短链
            String shortUrl = aliasOpt.orElse(generateUniqueShortUrl());

            // 付费用户生成短链180天过期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 180);
//            calendar.add(Calendar.SECOND, 30);
            Date expireAt = calendar.getTime();

            // 保存到全局映射表
            UrlMappingEntity mappingEntity = new UrlMappingEntity(shortUrl, longUrl, userId, expireAt);
            mapRepo.saveMapping(mappingEntity);

            // 保存反向索引记录
            ReverseMappingEntity indexEntity = new ReverseMappingEntity(indexKey, shortUrl);
            reverseMapRepo.saveIndex(indexEntity);

            return shortUrl;
        }

        // 如果旧短链存在，删除旧的短链记录
        mapRepo.deleteMapping(oldShortUrl);
        reverseMapRepo.deleteIndex(indexKey);

        // 生成新的短链
        String newShortUrl = aliasOpt.orElse(generateUniqueShortUrl());

        // 付费用户生成短链180天过期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 180);
//        calendar.add(Calendar.SECOND, 30);
        Date expireAt = calendar.getTime();

        // 保存到全局映射表
        UrlMappingEntity newMapping = new UrlMappingEntity(newShortUrl, longUrl, userId, expireAt);
        mapRepo.saveMapping(newMapping);

        // 保存反向索引记录
        ReverseMappingEntity newIndex = new ReverseMappingEntity(indexKey, newShortUrl);
        reverseMapRepo.saveIndex(newIndex);

        return newShortUrl;
    }

    /**
     * 根据全局短链查找对应长链接
     * @return 如果链接未过期，返回长链接；如果链接已过期，返回 "URL_EXPIRED"
     */
    public String retrieveLongUrl(String shortUrl) throws Exception {
        UrlMappingEntity entity = mapRepo.getMapping(shortUrl);
        if (entity == null) {
            return null;
        }
        
        // 检查链接是否过期
        Date expireAt = entity.getExpireAt();
        Date calendar = Calendar.getInstance().getTime();
        if (calendar.compareTo(expireAt) > 0) {
            return "URL_EXPIRED";
        }
        
        return entity.getLongUrl();
    }

    public boolean if_expired(String shortUrl) throws Exception {
        UrlMappingEntity entity = mapRepo.getMapping(shortUrl);
        Date expireAt = entity.getExpireAt();
        Date calendar = Calendar.getInstance().getTime();
        return calendar.compareTo(expireAt) > 0;
    }


    /**
     * 更新点击次数
     */
    public void incrementClick(String shortUrl) throws Exception {
        mapRepo.incrementClick(shortUrl);
    }

    /**
     * 获取用户的所有 URL mapping 记录，并计算每个 URL 的过期天数
     * @param userId 用户ID
     * @return 包含过期天数信息的 URL mapping 列表
     */
    public List<UrlMappingDTO> getUserMappings(String userId) throws Exception {
        List<UrlMappingEntity> mappings = mapRepo.getMappingsByUserId(userId);
        List<UrlMappingDTO> result = new ArrayList<>();
        
        // 当前时间
        long now = System.currentTimeMillis();
        
        // 一天的毫秒数
        final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        
        for (UrlMappingEntity entity : mappings) {
            UrlMappingDTO dto = new UrlMappingDTO();
            dto.setShortUrl(entity.getShortUrl());
            dto.setLongUrl(entity.getLongUrl());
            dto.setClickCount(entity.getClickCount());

//            dto.setCreateTime(entity.getCreateTime());
            dto.setCreateTimeFormatted(
                    entity.getCreateTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .toString() // 返回 "YYYY-MM-DD"
            );

//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            String formattedDate = entity.getCreateTime()
//                    .toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate()
//                    .format(formatter);
//
//            dto.setCreateTimeFormatted(formattedDate);


            // 使用实际的过期时间计算剩余天数
            long expireAt = entity.getExpireAt().getTime();
            int daysLeft = (int)((expireAt - now) / MILLIS_PER_DAY);
            dto.setDaysUntilExpiry(daysLeft);
            
            result.add(dto);
        }
        
        return result;
    }
}

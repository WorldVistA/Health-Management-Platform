package EXT.DOMAIN.cpe.vpr.pom.jds;

import EXT.DOMAIN.cpe.jsonc.JsonCCollection;
import EXT.DOMAIN.cpe.vpr.pom.DefaultNamingStrategy;
import EXT.DOMAIN.cpe.vpr.pom.IGenericPOMObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.INamingStrategy;
import EXT.DOMAIN.cpe.vpr.pom.IPOMObject;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdsGenericPOMObjectDAO extends JdsDaoSupport implements IGenericPOMObjectDAO {

    private INamingStrategy namingStrategy = new DefaultNamingStrategy();

    @Override
    public <T extends IPOMObject> T save(T entity) {
        if (StringUtils.hasText(entity.getUid())) {
            // TODO: do some uid validation here? match against an expected regex?
            String[] pieces = entity.getUid().split(":");
            String collectionName = pieces[2];

            jdsTemplate.postForLocation("/data", entity);

            logger.debug("saved {} with uid {}", collectionName, entity.getUid());
        } else {
            String collectionName = getCollectionName(entity.getClass());
            URI itemUri = jdsTemplate.postForLocation("/data/" + collectionName, entity);
            String[] pieces = itemUri.getPath().split("/");
            String uid = pieces[2];
            entity.setData("uid", uid);

            logger.debug("saved {} with uid {}", collectionName, entity.getUid());
        }
        return entity;
    }

    @Override
    public <T extends IPOMObject> void delete(T entity) {
        Assert.notNull(entity, "[Assertion failed] - 'entity' argument is required; it must not be null");

        deleteByUID(entity.getClass(), entity.getUid());
    }

    @Override
    public <T extends IPOMObject> void deleteByUID(Class<T> type, String uid) {
        Assert.notNull(uid, "[Assertion failed] - 'uid' argument is required; it must not be null");

        jdsTemplate.delete("/data/" + uid);
    }

    @Override
    public <T extends IPOMObject> void deleteAll(Class<T> type) {
        // TODO: implement me
        throw new NotImplementedException();
    }

    @Override
    public <T extends IPOMObject> int count(Class<T> type) {
        String collectionName = getCollectionName(type);
        JsonCCollection<Map<String, Object>> jsonc = jdsTemplate.getForJsonC("/data/count/" + collectionName);
        return jsonc.getTotalItems();
    }

    @Override
    public <T extends IPOMObject> T findByUID(Class<T> type, String uid) {
        Assert.notNull(uid, "[Assertion failed] - 'uid' argument is required; it must not be null");

        return jdsTemplate.getForObject("/data/" + uid, type);
    }

    @Override
    public <T extends IPOMObject> List<T> findAll(Class<T> type) {
        return findAll(type, (Sort) null);
    }

    @Override
    public <T extends IPOMObject> List<T> findAll(Class<T> type, Sort sort) {
        String collectionName = getCollectionName(type);
        UriComponentsBuilder uri = UriComponentsBuilder.fromPath("/data/find/" + collectionName);
        addSortQueryParams(sort, uri);
        return findAllInternal(type, uri.build().toUriString()).getContent();
    }


    @Override
    public <T extends IPOMObject> Page<T> findAll(Class<T> type, Pageable pageable) {
        String collectionName = getCollectionName(type);
        UriComponentsBuilder uri = UriComponentsBuilder.fromPath("/data/find/" + collectionName);
        addPaginationQueryParams(pageable, uri);
        return findAllInternal(type, uri.build().toUriString());
    }

    private <T extends IPOMObject> Page<T> findAllInternal(Class<T> type, String uri) {
        JsonCCollection<Map<String, Object>> jsonc = jdsTemplate.getForJsonC(uri);
        if (jsonc == null) throw new DataRetrievalFailureException("JDS getForJsonC at '" + uri + "' returned null");

        List<T> list = new ArrayList<T>(jsonc.getItems().size());
        for (Map<String, Object> item : jsonc.getItems()) {
            list.add(jsonMapper.convertValue(item, type));
        }
        if (jsonc.getStartIndex() != null && jsonc.getItemsPerPage() != null && jsonc.getItemsPerPage() > 0) {
            int pageNum = jsonc.getStartIndex() / jsonc.getItemsPerPage();
            return new PageImpl<T>(list, new PageRequest(pageNum, jsonc.getItemsPerPage()), jsonc.getTotalItems());
        } else {
            return new PageImpl<T>(list, null, jsonc.getTotalItems());
        }
    }

    private void addPaginationQueryParams(Pageable pageable, UriComponentsBuilder uri) {
        if (pageable == null) return;

        uri.queryParam("start", pageable.getOffset());
        uri.queryParam("limit", pageable.getPageSize());

        addSortQueryParams(pageable.getSort(), uri);
    }

    private void addSortQueryParams(Sort sort, UriComponentsBuilder uri) {
        if (sort == null) return;

        for (Sort.Order order : sort) {
            uri.queryParam("order", order.getProperty());
        }
    }

    private String getCollectionName(Class type) {
        return namingStrategy.collectionName(type);
    }
}

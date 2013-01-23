package org.osehra.cpe.vpr.dao;

import org.osehra.cpe.dao.HmpRepository;
import org.osehra.cpe.vpr.SyncError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Map;

public interface ISyncErrorDao extends HmpRepository<SyncError, String> {

    SyncError save(SyncError error);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    Page<SyncError> findAll(Pageable pageable);

    Integer countByPatientId(String pid);

    Integer countAllPatientIds();

    Page<SyncError> findAllByPatientId(String pid, Pageable pageable);

    int deleteByPatientId(String pid);
}

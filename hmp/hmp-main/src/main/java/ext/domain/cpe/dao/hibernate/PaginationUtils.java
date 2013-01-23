package org.osehra.cpe.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static void setPaginationCriteria(Criteria c, Pageable pageable) {
        // add the pagination stuff
        c.setFirstResult(pageable.getOffset());
        c.setMaxResults(pageable.getPageSize());

        // add the sorting stuff
        if (pageable.getSort() != null) {
            setSortCriteria(c, pageable.getSort());
        }
    }

    public static void setSortCriteria(Criteria c, Sort sort) {
        for (Sort.Order sortOrder : sort) {
            if (sortOrder.isAscending()) {
                c.addOrder(Order.asc(sortOrder.getProperty()));
            } else {
                c.addOrder(Order.desc(sortOrder.getProperty()));
            }
        }
    }
}

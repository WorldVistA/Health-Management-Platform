package EXT.DOMAIN.cpe.vpr.dao.hibernate;

import EXT.DOMAIN.cpe.dao.hibernate.HibernateHmpRepository;
import EXT.DOMAIN.cpe.vpr.LastViewed;
import EXT.DOMAIN.cpe.vpr.dao.ILastViewedDao;

import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

class HibernateLastViewedDao extends HibernateHmpRepository<LastViewed, String> implements ILastViewedDao {
	
	public HibernateLastViewedDao() {
        super(LastViewed.class);
    }

    @Override
    protected Session getSession() {
        return super.getSession().getSession(EntityMode.POJO);
    }

    @Override
    @Transactional(readOnly = true)
	public LastViewed findByUidAndUserId(String uid, String userId) {
		List<LastViewed> list = getSession().createCriteria(LastViewed.class)
			.add(Restrictions.eq("uid", uid))
			.add(Restrictions.eq("userId", userId))
			.setMaxResults(1).list();
		// TODO Auto-generated method stub
		if(list != null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}
}

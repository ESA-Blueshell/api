package net.blueshell.api.business.signature;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static net.blueshell.api.db.DatabaseManager.getSessionFactory;

@Component
@DependsOn("dataSource")
public class SignatureDao extends SessionWrapper<Signature> implements Dao<Signature> {


    public SignatureDao() {
        super(Signature.class);
    }


    /**
     * Checks if a signature exists by its name.
     *
     * @param name the name of the signature to check
     * @return true if a signature with the given name exists, false otherwise
     */
    public boolean existsByName(String name) {
        Session session = getSessionFactory().openSession();
        try {
            String hql = "SELECT COUNT(s) FROM Signature s WHERE s.name = :name";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("name", name);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } finally {
            session.close();
        }
    }
}

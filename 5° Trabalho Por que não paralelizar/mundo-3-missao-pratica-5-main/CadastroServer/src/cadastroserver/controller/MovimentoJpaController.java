
package cadastroserver.controller;

import cadastroserver.controller.exceptions.NonexistentEntityException;
import cadastroserver.model.Movimento;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import cadastroserver.model.Pessoa;
import cadastroserver.model.Produto;
import cadastroserver.model.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class MovimentoJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public MovimentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimento movimento) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa person = movimento.getPersonId();
            if (person != null) {
                person = em.getReference(person.getClass(), person.getPersonId());
                movimento.setPersonId(person);
            }
            Product productId = movimento.getProductId();
            if (productId != null) {
                productId = em.getReference(productId.getClass(), productId.getProductId());
                movimento.setProductId(productId);
            }
            User userId = movimento.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getUserId());
                movimento.setUserId(userId);
            }
            em.persist(movimento);
            if (person != null) {
                person.getMovementCollection().add(movimento);
                person = em.merge(person);
            }
            if (productId != null) {
                productId.getMovementCollection().add(movimento);
                productId = em.merge(productId);
            }
            if (userId != null) {
                userId.getMovementCollection().add(movimento);
                userId = em.merge(userId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movimento movimento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento persistentMovement = em.find(Movimento.class, movimento.getMovementId());
            Person personOld = persistentMovement.getPersonId();
            Person personNew = movimento.getPersonId();
            Product productOld = persistentMovement.getProductId();
            Product productNew = movimento.getProductId();
            User userOld = persistentMovement.getUserId();
            User userNew = movimento.getUserId();
            if (personNew != null) {
                personNew = em.getReference(personNew.getClass(), personNew.getPersonId());
                movimento.setPersonId(personNew);
            }
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getProductId());
                movimento.setProductId(productNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUserId());
                movimento.setUserId(userNew);
            }
            movimento = em.merge(movimento);
            if (personOld != null && !personOld.equals(personNew)) {
                personOld.getMovementCollection().remove(movimento);
                personOld = em.merge(personOld);
            }
            if (personNew != null && !personNew.equals(personOld)) {
                personNew.getMovementCollection().add(movimento);
                personNew = em.merge(personNew);
            }
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getMovementCollection().remove(movimento);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getMovementCollection().add(movimento);
                productNew = em.merge(productNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getMovementCollection().remove(movimento);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getMovementCollection().add(movimento);
                userNew = em.merge(userNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movimento.getMovementId();
                if (findMovement(id) == null) {
                    throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento movimento;
            try {
                movimento = em.getReference(Movimento.class, id);
                movimento.getMovementId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.", enfe);
            }
            Person person = movimento.getPersonId();
            if (person != null) {
                person.getMovementCollection().remove(movimento);
                person = em.merge(person);
            }
            Product product = movimento.getProductId();
            if (product != null) {
                product.getMovementCollection().remove(movimento);
                product = em.merge(product);
            }
            User user = movimento.getUserId();
            if (user != null) {
                user.getMovementCollection().remove(movimento);
                user = em.merge(user);
            }
            em.remove(movimento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movimento> findMovementEntities() {
        return findMovementEntities(true, -1, -1);
    }

    public List<Movimento> findMovementEntities(int maxResults, int firstResult) {
        return findMovementEntities(false, maxResults, firstResult);
    }

    private List<Movimento> findMovementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movimento.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Movimento findMovement(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimento.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movimento> rt = cq.from(Movimento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}

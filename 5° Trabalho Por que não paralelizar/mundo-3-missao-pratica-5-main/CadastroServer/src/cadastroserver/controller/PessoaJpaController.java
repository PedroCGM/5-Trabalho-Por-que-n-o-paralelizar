
package cadastroserver.controller;

import cadastroserver.controller.exceptions.IllegalOrphanException;
import cadastroserver.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import cadastroserver.model.PessoaFisica;
import cadastroserver.model.PessoaJuridica;
import cadastroserver.model.Movimento;
import cadastroserver.model.Pessoa;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) {
        if (pessoa.getMovimentoCollection() == null) {
            pessoa.setMovimentoCollection(new ArrayList<Movimento>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PessoaFisica pessoaFisica = pessoa.getPessoaFisica();
            if (pessoaFisica != null) {
                pessoaFisica = em.getReference(pessoaFisica.getClass(), pessoaFisica.getIdPessoa());
                pessoa.setPessoaFisica(pessoaFisica);
            }
            PessoaJuridica pessoaJuridica = pessoa.getPessoaJuridica();
            if (pessoaJuridica != null) {
                pessoaJuridica = em.getReference(pessoaJuridica.getClass(), pessoaJuridica.getIdPessoa());
                pessoa.setPessoaJuridica(pessoaJuridica);
            }
            Collection<Movimento> attachedMovimentoCollection = new ArrayList<Movimento>();
            for (Movimento movimentoCollectionMovimentoToAttach : pessoa.getMovimentoCollection()) {
                movimentoCollectionMovimentoToAttach = em.getReference(movimentoCollectionMovimentoToAttach.getClass(), movimentoCollectionMovimentoToAttach.getIdMovimento());
                attachedMovimentoCollection.add(movimentoCollectionMovimentoToAttach);
            }
            pessoa.setMovimentoCollection(attachedMovimentoCollection);
            em.persist(pessoa);
            if (pessoaFisica != null) {
                Pessoa oldpessoaOfPessoaFisica = pessoaFisica.getpessoa();
                if (oldpessoaOfPessoaFisica != null) {
                    oldpessoaOfPessoaFisica.setPessoaFisica(null);
                    oldpessoaOfPessoaFisica = em.merge(oldpessoaOfPessoaFisica);
                }
                pessoaFisica.setpessoa(pessoa);
                pessoaFisica = em.merge(pessoaFisica);
            }
            if (pessoaJuridica != null) {
                Pessoa oldpessoaOfPessoaJuridica = pessoaJuridica.getpessoa();
                if (oldpessoaOfPessoaJuridica != null) {
                    oldpessoaOfPessoaJuridica.setPessoaJuridica(null);
                    oldpessoaOfPessoaJuridica = em.merge(oldpessoaOfPessoaJuridica);
                }
                pessoaJuridica.setpessoa(pessoa);
                pessoaJuridica = em.merge(pessoaJuridica);
            }
            for (Movimento movimentoCollectionMovimento : pessoa.getMovimentoCollection()) {
                Pessoa oldIdPessoaOfMovimentoCollectionMovimento = movimentoCollectionMovimento.getIdPessoa();
                movimentoCollectionMovimento.setIdPessoa(pessoa);
                movimentoCollectionMovimento = em.merge(movimentoCollectionMovimento);
                if (oldIdPessoaOfMovimentoCollectionMovimento != null) {
                    oldIdPessoaOfMovimentoCollectionMovimento.getMovimentoCollection().remove(movimentoCollectionMovimento);
                    oldIdPessoaOfMovimentoCollectionMovimento = em.merge(oldIdPessoaOfMovimentoCollectionMovimento);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa persistentpessoa = em.find(Pessoa.class, pessoa.getIdPessoa());
            PessoaFisica pessoaFisicaOld = persistentpessoa.getPessoaFisica();
            PessoaFisica pessoaFisicaNew = pessoa.getPessoaFisica();
            PessoaJuridica pessoaJuridicaOld = persistentpessoa.getPessoaJuridica();
            PessoaJuridica pessoaJuridicaNew = pessoa.getPessoaJuridica();
            Collection<Movimento> movimentoCollectionOld = persistentpessoa.getMovimentoCollection();
            Collection<Movimento> movimentoCollectionNew = pessoa.getMovimentoCollection();
            List<String> illegalOrphanMessages = null;
            if (pessoaFisicaOld != null && !pessoaFisicaOld.equals(pessoaFisicaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain PessoaFisica " + pessoaFisicaOld + " since its pessoa field is not nullable.");
            }
            if (pessoaJuridicaOld != null && !pessoaJuridicaOld.equals(pessoaJuridicaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain PessoaJuridica " + pessoaJuridicaOld + " since its pessoa field is not nullable.");
            }
            for (Movimento movimentoCollectionOldMovimento : movimentoCollectionOld) {
                if (!movimentoCollectionNew.contains(movimentoCollectionOldMovimento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movimento " + movimentoCollectionOldMovimento + " since its idPessoa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pessoaFisicaNew != null) {
                pessoaFisicaNew = em.getReference(pessoaFisicaNew.getClass(), pessoaFisicaNew.getIdPessoa());
                pessoa.setPessoaFisica(pessoaFisicaNew);
            }
            if (pessoaJuridicaNew != null) {
                pessoaJuridicaNew = em.getReference(pessoaJuridicaNew.getClass(), pessoaJuridicaNew.getIdPessoa());
                pessoa.setPessoaJuridica(pessoaJuridicaNew);
            }
            Collection<Movimento> attachedMovimentoCollectionNew = new ArrayList<Movimento>();
            for (Movimento movimentoCollectionNewMovimentoToAttach : movimentoCollectionNew) {
                movimentoCollectionNewMovimentoToAttach = em.getReference(movimentoCollectionNewMovimentoToAttach.getClass(), movimentoCollectionNewMovimentoToAttach.getIdMovimento());
                attachedMovimentoCollectionNew.add(movimentoCollectionNewMovimentoToAttach);
            }
            movimentoCollectionNew = attachedMovimentoCollectionNew;
            pessoa.setMovimentoCollection(movimentoCollectionNew);
            pessoa = em.merge(pessoa);
            if (pessoaFisicaNew != null && !pessoaFisicaNew.equals(pessoaFisicaOld)) {
                Pessoa oldpessoaOfPessoaFisica = pessoaFisicaNew.getpessoa();
                if (oldpessoaOfPessoaFisica != null) {
                    oldpessoaOfPessoaFisica.setPessoaFisica(null);
                    oldpessoaOfPessoaFisica = em.merge(oldpessoaOfPessoaFisica);
                }
                pessoaFisicaNew.setpessoa(pessoa);
                pessoaFisicaNew = em.merge(pessoaFisicaNew);
            }
            if (pessoaJuridicaNew != null && !pessoaJuridicaNew.equals(pessoaJuridicaOld)) {
                Pessoa oldpessoaOfPessoaJuridica = pessoaJuridicaNew.getpessoa();
                if (oldpessoaOfPessoaJuridica != null) {
                    oldpessoaOfPessoaJuridica.setPessoaJuridica(null);
                    oldpessoaOfPessoaJuridica = em.merge(oldpessoaOfPessoaJuridica);
                }
                pessoaJuridicaNew.setpessoa(pessoa);
                pessoaJuridicaNew = em.merge(pessoaJuridicaNew);
            }
            for (Movimento movimentoCollectionNewMovimento : movimentoCollectionNew) {
                if (!movimentoCollectionOld.contains(movimentoCollectionNewMovimento)) {
                    Pessoa oldIdPessoaOfMovimentoCollectionNewMovimento = movimentoCollectionNewMovimento.getIdPessoa();
                    movimentoCollectionNewMovimento.setIdPessoa(pessoa);
                    movimentoCollectionNewMovimento = em.merge(movimentoCollectionNewMovimento);
                    if (oldIdPessoaOfMovimentoCollectionNewMovimento != null && !oldIdPessoaOfMovimentoCollectionNewMovimento.equals(pessoa)) {
                        oldIdPessoaOfMovimentoCollectionNewMovimento.getMovimentoCollection().remove(movimentoCollectionNewMovimento);
                        oldIdPessoaOfMovimento
                        package cadastroserver.controller;

                        import cadastroserver.controller.exceptions.IllegalOrphanException;
                        import cadastroserver.controller.exceptions.NonexistentEntityException;
                        import java.io.Serializable;
                        import javax.persistence.Query;
                        import javax.persistence.EntityNotFoundException;
                        import javax.persistence.criteria.CriteriaQuery;
                        import javax.persistence.criteria.Root;
                        import cadastroserver.model.PessoaFisica;
                        import cadastroserver.model.PessoaJuridica;
                        import cadastroserver.model.Movimento;
                        import cadastroserver.model.Pessoa;
                        import java.util.ArrayList;
                        import java.util.Collection;
                        import java.util.List;
                        import javax.persistence.EntityManager;
                        import javax.persistence.EntityManagerFactory;
                        
                        public class PessoaJpaController implements Serializable {
                        
                            public PessoaJpaController(EntityManagerFactory emf) {
                                this.emf = emf;
                            }
                            private EntityManagerFactory emf = null;
                        
                            public EntityManager getEntityManager() {
                                return emf.createEntityManager();
                            }
                        
                            public void create(Pessoa pessoa) {
                                if (pessoa.getMovimentoCollection() == null) {
                                    pessoa.setMovimentoCollection(new ArrayList<Movimento>());
                                }
                                EntityManager em = null;
                                try {
                                    em = getEntityManager();
                                    em.getTransaction().begin();
                                    PessoaFisica pessoaFisica = pessoa.getPessoaFisica();
                                    if (pessoaFisica != null) {
                                        pessoaFisica = em.getReference(pessoaFisica.getClass(), pessoaFisica.getIdPessoa());
                                        pessoa.setPessoaFisica(pessoaFisica);
                                    }
                                    PessoaJuridica pessoaJuridica = pessoa.getPessoaJuridica();
                                    if (pessoaJuridica != null) {
                                        pessoaJuridica = em.getReference(pessoaJuridica.getClass(), pessoaJuridica.getIdPessoa());
                                        pessoa.setPessoaJuridica(pessoaJuridica);
                                    }
                                    Collection<Movimento> attachedMovimentoCollection = new ArrayList<Movimento>();
                                    for (Movimento movimentoCollectionMovimentoToAttach : pessoa.getMovimentoCollection()) {
                                        movimentoCollectionMovimentoToAttach = em.getReference(movimentoCollectionMovimentoToAttach.getClass(), movimentoCollectionMovimentoToAttach.getIdMovimento());
                                        attachedMovimentoCollection.add(movimentoCollectionMovimentoToAttach);
                                    }
                                    pessoa.setMovimentoCollection(attachedMovimentoCollection);
                                    em.persist(pessoa);
                                    if (pessoaFisica != null) {
                                        Pessoa oldpessoaOfPessoaFisica = pessoaFisica.getpessoa();
                                        if (oldpessoaOfPessoaFisica != null) {
                                            oldpessoaOfPessoaFisica.setPessoaFisica(null);
                                            oldpessoaOfPessoaFisica = em.merge(oldpessoaOfPessoaFisica);
                                        }
                                        pessoaFisica.setpessoa(pessoa);
                                        pessoaFisica = em.merge(pessoaFisica);
                                    }
                                    if (pessoaJuridica != null) {
                                        Pessoa oldpessoaOfPessoaJuridica = pessoaJuridica.getpessoa();
                                        if (oldpessoaOfPessoaJuridica != null) {
                                            oldpessoaOfPessoaJuridica.setPessoaJuridica(null);
                                            oldpessoaOfPessoaJuridica = em.merge(oldpessoaOfPessoaJuridica);
                                        }
                                        pessoaJuridica.setpessoa(pessoa);
                                        pessoaJuridica = em.merge(pessoaJuridica);
                                    }
                                    for (Movimento movimentoCollectionMovimento : pessoa.getMovimentoCollection()) {
                                        Pessoa oldIdPessoaOfMovimentoCollectionMovimento = movimentoCollectionMovimento.getIdPessoa();
                                        movimentoCollectionMovimento.setIdPessoa(pessoa);
                                        movimentoCollectionMovimento = em.merge(movimentoCollectionMovimento);
                                        if (oldIdPessoaOfMovimentoCollectionMovimento != null) {
                                            oldIdPessoaOfMovimentoCollectionMovimento.getMovimentoCollection().remove(movimentoCollectionMovimento);
                                            oldIdPessoaOfMovimentoCollectionMovimento = em.merge(oldIdPessoaOfMovimentoCollectionMovimento);
                                        }
                                    }
                                    em.getTransaction().commit();
                                } finally {
                                    if (em != null) {
                                        em.close();
                                    }
                                }
                            }
                        
                            public void edit(Pessoa pessoa) throws IllegalOrphanException, NonexistentEntityException, Exception {
                                EntityManager em = null;
                                try {
                                    em = getEntityManager();
                                    em.getTransaction().begin();
                                    Pessoa persistentpessoa = em.find(Pessoa.class, pessoa.getIdPessoa());
                                    PessoaFisica pessoaFisicaOld = persistentpessoa.getPessoaFisica();
                                    PessoaFisica pessoaFisicaNew = pessoa.getPessoaFisica();
                                    PessoaJuridica pessoaJuridicaOld = persistentpessoa.getPessoaJuridica();
                                    PessoaJuridica pessoaJuridicaNew = pessoa.getPessoaJuridica();
                                    Collection<Movimento> movimentoCollectionOld = persistentpessoa.getMovimentoCollection();
                                    Collection<Movimento> movimentoCollectionNew = pessoa.getMovimentoCollection();
                                    List<String> illegalOrphanMessages = null;
                                    if (pessoaFisicaOld != null && !pessoaFisicaOld.equals(pessoaFisicaNew)) {
                                        if (illegalOrphanMessages == null) {
                                            illegalOrphanMessages = new ArrayList<String>();
                                        }
                                        illegalOrphanMessages.add("You must retain PessoaFisica " + pessoaFisicaOld + " since its pessoa field is not nullable.");
                                    }
                                    if (pessoaJuridicaOld != null && !pessoaJuridicaOld.equals(pessoaJuridicaNew)) {
                                        if (illegalOrphanMessages == null) {
                                            illegalOrphanMessages = new ArrayList<String>();
                                        }
                                        illegalOrphanMessages.add("You must retain PessoaJuridica " + pessoaJuridicaOld + " since its pessoa field is not nullable.");
                                    }
                                    for (Movimento movimentoCollectionOldMovimento : movimentoCollectionOld) {
                                        if (!movimentoCollectionNew.contains(movimentoCollectionOldMovimento)) {
                                            if (illegalOrphanMessages == null) {
                                                illegalOrphanMessages = new ArrayList<String>();
                                            }
                                            illegalOrphanMessages.add("You must retain Movimento " + movimentoCollectionOldMovimento + " since its idPessoa field is not nullable.");
                                        }
                                    }
                                    if (illegalOrphanMessages != null) {
                                        throw new IllegalOrphanException(illegalOrphanMessages);
                                    }
                                    if (pessoaFisicaNew != null) {
                                        pessoaFisicaNew = em.getReference(pessoaFisicaNew.getClass(), pessoaFisicaNew.getIdPessoa());
                                        pessoa.setPessoaFisica(pessoaFisicaNew);
                                    }
                                    if (pessoaJuridicaNew != null) {
                                        pessoaJuridicaNew = em.getReference(pessoaJuridicaNew.getClass(), pessoaJuridicaNew.getIdPessoa());
                                        pessoa.setPessoaJuridica(pessoaJuridicaNew);
                                    }
                                    Collection<Movimento> attachedMovimentoCollectionNew = new ArrayList<Movimento>();
                                    for (Movimento movimentoCollectionNewMovimentoToAttach : movimentoCollectionNew) {
                                        movimentoCollectionNewMovimentoToAttach = em.getReference(movimentoCollectionNewMovimentoToAttach.getClass(), movimentoCollectionNewMovimentoToAttach.getIdMovimento());
                                        attachedMovimentoCollectionNew.add(movimentoCollectionNewMovimentoToAttach);
                                    }
                                    movimentoCollectionNew = attachedMovimentoCollectionNew;
                                    pessoa.setMovimentoCollection(movimentoCollectionNew);
                                    pessoa = em.merge(pessoa);
                                    if (pessoaFisicaNew != null && !pessoaFisicaNew.equals(pessoaFisicaOld)) {
                                        Pessoa oldpessoaOfPessoaFisica = pessoaFisicaNew.getpessoa();
                                        if (oldpessoaOfPessoaFisica != null) {
                                            oldpessoaOfPessoaFisica.setPessoaFisica(null);
                                            oldpessoaOfPessoaFisica = em.merge(oldpessoaOfPessoaFisica);
                                        }
                                        pessoaFisicaNew.setpessoa(pessoa);
                                        pessoaFisicaNew = em.merge(pessoaFisicaNew);
                                    }
                                    if (pessoaJuridicaNew != null && !pessoaJuridicaNew.equals(pessoaJuridicaOld)) {
                                        Pessoa oldpessoaOfPessoaJuridica = pessoaJuridicaNew.getpessoa();
                                        if (oldpessoaOfPessoaJuridica != null) {
                                            oldpessoaOfPessoaJuridica.setPessoaJuridica(null);
                                            oldpessoaOfPessoaJuridica = em.merge(oldpessoaOfPessoaJuridica);
                                        }
                                        pessoaJuridicaNew.setpessoa(pessoa);
                                        pessoaJuridicaNew = em.merge(pessoaJuridicaNew);
                                    }
                                 
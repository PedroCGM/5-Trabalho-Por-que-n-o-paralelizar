
package inventoryserver.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "legal_person")
@NamedQueries({
    @NamedQuery(name = "LegalPerson.findAll", query = "SELECT p FROM LegalPerson p"),
    @NamedQuery(name = "LegalPerson.findById", query = "SELECT p FROM LegalPerson p WHERE p.id = :id"),
    @NamedQuery(name = "LegalPerson.findByCnpj", query = "SELECT p FROM LegalPerson p WHERE p.cnpj = :cnpj")
})
public class LegalPerson implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "cnpj")
    private String cnpj;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Person person;

    public LegalPerson() {
    }

    public LegalPerson(Integer id) {
        this.id = id;
    }

    public LegalPerson(Integer id, String cnpj) {
        this.id = id;
        this.cnpj = cnpj;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof LegalPerson)) {
            return false;
        }
        LegalPerson other = (LegalPerson) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "inventoryserver.model.LegalPerson[ id=" + id + " ]";
    }
}

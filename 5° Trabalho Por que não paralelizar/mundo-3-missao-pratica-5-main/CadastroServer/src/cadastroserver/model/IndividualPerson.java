
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
@Table(name = "individual_person")
@NamedQueries({
    @NamedQuery(name = "IndividualPerson.findAll", query = "SELECT p FROM IndividualPerson p"),
    @NamedQuery(name = "IndividualPerson.findById", query = "SELECT p FROM IndividualPerson p WHERE p.id = :id"),
    @NamedQuery(name = "IndividualPerson.findByCpf", query = "SELECT p FROM IndividualPerson p WHERE p.cpf = :cpf")
})
public class IndividualPerson implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "cpf")
    private String cpf;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Person person;

    public IndividualPerson() {
    }

    public IndividualPerson(Integer id) {
        this.id = id;
    }

    public IndividualPerson(Integer id, String cpf) {
        this.id = id;
        this.cpf = cpf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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
        if (!(object instanceof IndividualPerson)) {
            return false;
        }
        IndividualPerson other = (IndividualPerson) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "inventoryserver.model.IndividualPerson[ id=" + id + " ]";
    }
}

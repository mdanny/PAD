package InfoNode.nodeinfoservice;

import InfoNode.nodeinfoservice.model.Employee;

import java.util.concurrent.CopyOnWriteArraySet;

public class InfoService {

    private CopyOnWriteArraySet<Employee> employees = new CopyOnWriteArraySet<Employee>();

    public CopyOnWriteArraySet<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(CopyOnWriteArraySet<Employee> employees) {
        this.employees = employees;
    }

    public boolean add(Employee e) {
        return employees.add(e);
    }

    public boolean remove(Employee e) {
        return employees.remove(e);
    }

    public Object[] toArray() {
        return employees.toArray();
    }
}

package AppManager;

import java.sql.Date;
import java.util.Objects;

public class Employee extends Person {

    private final Date hireDate;

    private String job;
    private String workplace;
    private int salary;

    public Employee(String name, String surname, String birthDateStr, String address, String email, String phoneNumber, String hireDateStr, String job, String workplace, String id, int salary) {

        super(name, surname, birthDateStr, address, email, phoneNumber, id);

        if(!Validator.pastDateOk(hireDateStr) || !hireOk(job, salary, workplace)){}

        this.hireDate = Date.valueOf(hireDateStr);
        this.job = job;
        this.workplace = workplace;
        this.salary = salary;
    }

    public Employee(String name, String surname, String birthDateStr, String address, String email, String phoneNumber, String hireDateStr, String job, String workplace, int salary) {
        super(name, surname, birthDateStr, address, email, phoneNumber);

        if(!Validator.pastDateOk(hireDateStr) || !hireOk(job, salary, workplace)){}

        this.hireDate = Date.valueOf(hireDateStr);
        this.job = job;
        this.workplace = workplace;
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        Employee employee = (Employee) o;
        return salary == employee.salary && hireDate.equals(employee.hireDate) && Objects.equals(job, employee.job) && Objects.equals(workplace, employee.workplace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hireDate, job, workplace, salary);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "hireDate=" + hireDate +
                ", job='" + job + '\'' +
                ", workplace='" + workplace + '\'' +
                ", salary=" + salary +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", id='" + id + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    /// local validator(s)

    private boolean hireOk(String job, int salary, String workplace){

        if(workplace != "REMOTE" && !workplace.matches("office [a-z][0-9]{3}"))
            return false;

        switch(job){

            case "SECRETARY":
                return workplace.equals("REMOTE") && salary >= 1000 && salary <= 4000;

            case "ECONOMIST":
                return salary >= 2000 && salary <= 6000;

            case "SALESMAN":
                return workplace.equals("REMOTE") && salary >= 1900 && salary <= 5000;

            case "INFORMATICIAN":
                return salary >= 2400 && salary <= 7000;

            default:
                return false;
        }
    }
}

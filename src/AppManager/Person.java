package AppManager;

import java.sql.Date;
import java.util.Objects;

public abstract class Person {

    protected final String name;
    protected final String surname;

    protected final String id;

    protected final Date birthDate;

    protected String address;
    protected String email;
    protected String phoneNumber;

    public Person(String name, String surname, String birthDateStr, String address, String email, String phoneNumber, String id) {

        if(!Validator.nameOk(name) || !Validator.nameOk(surname)
                || !Validator.emailOk(email) || !Validator.phoneNumberOk(phoneNumber)
                ||  !Validator.pastDateOk(birthDateStr)){}

        this.name = name;
        this.surname = surname;

        this.id = id;

        this.birthDate = Date.valueOf(birthDateStr);

        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Person(String name, String surname, String birthDateStr, String address, String email, String phoneNumber) {

        if(!Validator.nameOk(name) || !Validator.nameOk(surname)
                || !Validator.emailOk(email) || !Validator.phoneNumberOk(phoneNumber)
                ||  !Validator.pastDateOk(birthDateStr)){}

        this.name = name;
        this.surname = surname;

        this.id = IdGenerator.getPersonId();

        this.birthDate = Date.valueOf(birthDateStr);

        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Person(String name, String surname, String birthDateStr) {

        if(!Validator.nameOk(name) || !Validator.nameOk(surname) || !Validator.pastDateOk(birthDateStr)){}

        this.name = name;
        this.surname = surname;

        this.id = IdGenerator.getPersonId();

        this.birthDate = Date.valueOf(birthDateStr);
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAge(){

        Date currentDate = new Date(System.currentTimeMillis());
        long milisecAge = currentDate.getTime() - this.birthDate.getTime();

        return (int)(milisecAge / ((long)365 * 24 * 60 * 60 * 1000));
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return name.equals(person.name) && surname.equals(person.surname) && id.equals(person.id) && birthDate.equals(person.birthDate) && Objects.equals(address, person.address) && Objects.equals(email, person.email) && Objects.equals(phoneNumber, person.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, id, birthDate, address, email, phoneNumber);
    }
}

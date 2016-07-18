/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.herocode.ecmat.model;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.uol.pagseguro.domain.Address;
import br.com.uol.pagseguro.domain.Phone;
import io.github.herocode.ecmat.entity.Participant;
import io.github.herocode.ecmat.entity.Payment;
import io.github.herocode.ecmat.enums.ErrorMessages;
import io.github.herocode.ecmat.enums.RegularExpressions;
import io.github.herocode.ecmat.enums.Titrations;
import io.github.herocode.ecmat.interfaces.ParticipantDao;
import io.github.herocode.ecmat.persistence.ParticipantDaoImpl;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Victor Hugo <victor.hugo.origins@gmail.com>
 */
public class ParticipantBuilder {

    private final String    name;
    private final String    titration;
    private final String    cpf;
    private final String    email;
    private final Address   address;
    
    private LocalDate   birthDate;
    private Payment     payment;
    private Phone       phone;
    private String      password;
    
    private ParticipantDao dao;

    public ParticipantBuilder(String name, String titration, String cpf, String email, Address address) {
        this.name       = name;
        this.titration  = titration;
        this.cpf        = cpf;
        this.email      = email;
        this.address    = address;
    }

    public ParticipantBuilder setBirthDate(LocalDate birthDate) {

        this.birthDate = birthDate;

        return this;
    }

    public ParticipantBuilder setPayment(Payment payment) {
        
        this.payment = payment;
        
        return this;
    }

    public ParticipantBuilder setPassword(String password) {

        this.password = password;
        
        return this;
    }
    
    public ParticipantBuilder setPhone(Phone phone) {
        
        this.phone = phone;
        
        return this;
    }
    
    private void validateAddress() throws IllegalArgumentException{
        
        if(stringIsEmpty(address.getCountry())){
            throw new IllegalArgumentException(ErrorMessages.EMPTY_COUNTRY.getErrorMessage());
        }
        
        if(stringIsEmpty(address.getCity())){
            throw new IllegalArgumentException(ErrorMessages.EMPTY_CITY.getErrorMessage());
        }
        
        if(stringIsEmpty(address.getState())){
            throw new IllegalArgumentException(ErrorMessages.EMPTY_STATE.getErrorMessage());
        }
        
        
    }
    
    private void validateTitration() throws IllegalArgumentException {
        
        if(stringIsEmpty(titration)){
            throw new IllegalArgumentException(ErrorMessages.EMPTY_TITRATION.getErrorMessage());
        }
        
        boolean equals = false;
        
        for(Titrations t : Titrations.values()){
            if(t.getTitration().equals(titration)){
                equals = true;
                break;
            }
        }
        
        if(!equals){
            throw new IllegalArgumentException(ErrorMessages.INVALID_TITRATION.getErrorMessage());
        }

    }
    
    private void validatePassword() throws IllegalArgumentException {
        
        if(stringIsEmpty(password)){
            throw new IllegalArgumentException(ErrorMessages.INVALID_PASSWORD.getErrorMessage());
        }
        
        Pattern pattern = Pattern.compile(RegularExpressions.PASSWORD_PATTERN.getRegex());

        Matcher matcher = pattern.matcher(password);
        
        if (!matcher.matches()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_PASSWORD.getErrorMessage());
        }
        
        password = DigestUtils.sha1Hex(password);
        
    }
    
    private void validateCpf() throws IllegalArgumentException {

        if (stringIsEmpty(cpf)) {
            throw new IllegalArgumentException(ErrorMessages.EMPTY_CPF.getErrorMessage());
        }

        CPFValidator cpfValidator = new CPFValidator();

        try {
            cpfValidator.assertValid(cpf);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_CPF.getErrorMessage());
        }
        System.out.println("vou olhar se o cpf e repetido");
        if(dao.existsCpf(cpf)){
            System.out.println("era sim");
            throw new IllegalArgumentException(ErrorMessages.EXISTING_CPF.getErrorMessage());
        }
        System.out.println("nao era");
    }

    private void validateName() throws IllegalArgumentException {

        if (stringIsEmpty(name)) {
            throw new IllegalArgumentException(ErrorMessages.EMPTY_NAME.getErrorMessage());
        }

    }

    private void validateEmail() throws IllegalArgumentException {

        if (stringIsEmpty(email)) {
            throw new IllegalArgumentException(ErrorMessages.EMPTY_EMAIL.getErrorMessage());
        }

        Pattern pattern = Pattern.compile(RegularExpressions.EMAIL_PATTERN.getRegex());

        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_EMAIL.getErrorMessage());
        }
        
        if(dao.existsEmail(email)){
            throw new IllegalArgumentException(ErrorMessages.EXISTING_EMAIL.getErrorMessage());
        }

    }

    private boolean stringIsEmpty(String string) {

        return (string == null || string.trim().isEmpty());
    }

    public Participant build() throws IllegalArgumentException {

        validateName();
        validatePassword();
        validateTitration();
        validateAddress();

        dao = new ParticipantDaoImpl();
        validateEmail();
        validateCpf();
        
        Participant participant = new Participant();

        participant.setName(name);
        participant.setCpf(cpf);
        participant.setEmail(email);
        participant.setPhone(phone);
        participant.setAddress(address);
        participant.setTitration(titration);
        participant.setPassword(password);
        participant.setBirthDate(birthDate);

        return participant;
    }

}

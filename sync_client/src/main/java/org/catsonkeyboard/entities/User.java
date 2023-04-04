package org.catsonkeyboard.entities;

import jakarta.persistence.*;
import org.catsonkeyboard.annotation.ServerTable;
import org.catsonkeyboard.annotation.SystemField;

@Entity
@Table(name = "user")
@ServerTable
public class User extends BaseModel {
//    @Id
//    //@SequenceGenerator(name = "studentSeq", sequenceName = "student_id_seq", allocationSize = 1, initialValue = 1)
//    //@GeneratedValue(generator = "studentSeq")
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    public Long id() {
//        return id;
//    }

    /**
     * 主键
     */
    @Id
    @SystemField
    private String key;

    /**
     * 用户唯一标识
     */
    private String uid;

    @Column(name = "airlines_iata")
    private String airlinesIATA;

    @Column(name = "airport_iata")
    private String airportIATA;

    private String department;

    private String duty;
    @Column(name = "expiry_date")
    private String expiryDate;
    private String mobile;
    private String sex;
    private Integer status;
    private String truename;
    private String nfcId;
    private String password;
    private String salt;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAirlinesIATA() {
        return airlinesIATA;
    }

    public void setAirlinesIATA(String airlinesIATA) {
        this.airlinesIATA = airlinesIATA;
    }


    public String getAirportIATA() {
        return airportIATA;
    }

    public void setAirportIATA(String airportIATA) {
        this.airportIATA = airportIATA;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getNfcId() {
        return nfcId;
    }

    public void setNfcId(String nfcId) {
        this.nfcId = nfcId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
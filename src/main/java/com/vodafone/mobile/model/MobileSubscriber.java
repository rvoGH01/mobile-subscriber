package com.vodafone.mobile.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "mobile_subscriber")
@DynamicUpdate
public class MobileSubscriber implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="msisdn", nullable = false)
    private String msisdn;

    @Column(name="customer_id_owner", nullable = false)
    private String customerIdOwner;

    @Column(name="customer_id_user", nullable = false)
    private String customerIdUser;

    @Enumerated(EnumType.STRING)
    @Column(name="service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name="service_start_date", nullable = false)
    private long serviceStartDate; // in millis

    public enum ServiceType {
        MOBILE_PREPAID, MOBILE_POSTPAID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getCustomerIdOwner() {
        return customerIdOwner;
    }

    public void setCustomerIdOwner(String customerIdOwner) {
        this.customerIdOwner = customerIdOwner;
    }

    public String getCustomerIdUser() {
        return customerIdUser;
    }

    public void setCustomerIdUser(String customerIdUser) {
        this.customerIdUser = customerIdUser;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public long getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(long serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }
}
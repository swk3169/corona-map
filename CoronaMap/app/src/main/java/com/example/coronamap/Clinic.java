package com.example.coronamap;

import java.io.Serializable;

public class Clinic implements Serializable {
    private String number;      // 연번
    private String sample;      // 채취가능여부
    private String city;        // 도시명
    private String district;    // 시군구
    private String name;        // 병원명
    private String address;     // 주소
    private String phoneNumber; // 대표전화번호

//    Clinic(){};
//    Clinic (String number, String sample, String city, String district, String name, String address, String phoneNumber) {
//        this.number = number;
//        this.sample = sample;
//        this.city = city;
//        this.district = district;
//        this.name = name;
//        this.address = address;
//        this.phoneNumber = phoneNumber;
//    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getSample() {
        return sample;
    }
    public void setSample(String sample) {
        this.sample = sample;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    // 선별진료소 이름을 매개변수로 할당받아 값의 동일 여부를 확인하고 마커를 진료소 순으로 나타내기 위하여 number를 반환, 아닌 경우 null
    public String findIndex(String name) {
        if(this.name.equals(name)) {
            return number;
        }
        else return null;
    }
}
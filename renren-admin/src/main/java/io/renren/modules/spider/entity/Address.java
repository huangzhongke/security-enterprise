package io.renren.modules.spider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kee
 * @version 1.0
 * @date 2022/2/22 17:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String address1;
    private String cityTown;
    private String companyName;
    private String country;
    private String email;
    private String firstName;
    private String id;
    private String lastName;
    private String phone;
    private String singleLine;
    private String vatNo;

    @Override
    public String toString() {
        return "{" +
                "\"address1\":\"" + address1 + '\"' +
                ", \"cityTown\":\"" + cityTown + '\"' +
                ", \"companyName\":\"" + companyName + '\"' +
                ", \"country\":\"" + country + '\"' +
                ", \"email\":\"" + email + '\"' +
                ", \"firstName\":\"" + firstName + '\"' +
                ", \"id\":\"" + id + '\"' +
                ", \"lastName\":\"" + lastName + '\"' +
                ", \"phone\":\"" + phone + '\"' +
                ", \"singleLine\":\"" + singleLine + '\"' +
                ", \"vatNo\":\"" + vatNo + '\"' +
                '}';
    }
}

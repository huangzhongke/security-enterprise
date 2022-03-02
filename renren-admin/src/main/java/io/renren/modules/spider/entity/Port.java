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
public class Port {
    private String id;
    private Boolean fmc;
    private String iata;
    private String matched;
    private String matchedCountry;
    private String mode;
    private String unCode;

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"fmc\":" + fmc +
                ", \"iata\":\"" + iata + '\"' +
                ", \"matched\":\"" + matched + '\"' +
                ", \"matchedCountry\":\"" + matchedCountry + '\"' +
                ", \"mode\":\"" + mode + '\"' +
                ", \"unCode\":\"" + unCode + '\"' +
                '}';
    }
}

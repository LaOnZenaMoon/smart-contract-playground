package me.lozm.app.contract.code;

import lombok.Getter;
import me.lozm.utils.code.EnumModel;

@Getter
public enum TokenSearchType implements EnumModel {

    ON_SALE("ON_SALE", "판매중"),
    PRIVATE("PRIVATE", "개인별"),
    ;

    private final String code;
    private final String description;

    TokenSearchType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getKey() {
        return code;
    }

    @Override
    public String getValue() {
        return description;
    }

}

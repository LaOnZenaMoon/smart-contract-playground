package me.lozm.app.contract.code;

import me.lozm.utils.code.EnumModel;

public enum TokenStatus implements EnumModel {

    SALE("SALE", "판매중"),
    NOT_SALE("NOT_SALE", "미판매중"),
    ;

    private final String code;
    private final String description;

    TokenStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

}

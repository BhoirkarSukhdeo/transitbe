package com.axisbank.transit.transitCardAPI.model.DTO;

public class BlockCardDTO {

    private Boolean result;
    private String BlockType;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getBlockType() {
        return BlockType;
    }

    public void setBlockType(String blockType) {
        BlockType = blockType;
    }
}

package com.axisbank.transit.transitCardAPI.model.request;

public class BlockCardRequest {

    private String blockType;

    private Boolean block;

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }
}

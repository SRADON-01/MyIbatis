package org.sradon.pojo.entity;

public class Types {
    private Long id;
    private String typeNum;
    private Integer typeSortDesc;
    private String typeName;

    @Override
    public String toString() {
        return "Types{" +
                "id=" + id +
                ", typeNum='" + typeNum + '\'' +
                ", typeSortDesc=" + typeSortDesc +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public Types() {
    }

    public Types(Long id, String typeNum, Integer typeSortDesc, String typeName) {
        this.id = id;
        this.typeNum = typeNum;
        this.typeSortDesc = typeSortDesc;
        this.typeName = typeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(String typeNum) {
        this.typeNum = typeNum;
    }

    public Integer getTypeSortDesc() {
        return typeSortDesc;
    }

    public void setTypeSortDesc(Integer typeSortDesc) {
        this.typeSortDesc = typeSortDesc;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

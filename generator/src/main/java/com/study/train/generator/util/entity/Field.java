package com.study.train.generator.util.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Field {

    private String name; // 字段名：course_id
    private String nameHump; // 字段名小驼峰：courseId
    private String nameBigHump; // 字段名大驼峰：CourseId
    private String nameCn; // 中文名：课程
    private String type; // 字段类型：char(8)
    private String javaType; // java类型：String
    private String comment; // 注释：课程|ID
    private Boolean nullAble; // 是否可为空
    private Integer length; // 字符串长度
    private Boolean enums; // 是否是枚举
    private String enumsConst; // 枚举常量 COURSE_LEVEL

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Field{");
        sb.append("name='").append(name).append('\'');
        sb.append(", nameHump='").append(nameHump).append('\'');
        sb.append(", nameBigHump='").append(nameBigHump).append('\'');
        sb.append(", nameCn='").append(nameCn).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", javaType='").append(javaType).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", nullAble=").append(nullAble);
        sb.append(", length=").append(length);
        sb.append(", enums=").append(enums);
        sb.append(", enumsConst='").append(enumsConst).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

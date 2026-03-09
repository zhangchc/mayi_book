package com.mayi.book.dto;

import java.math.BigDecimal;

/**
 * 支出排行榜：按分类汇总
 */
public class CategoryExpenseRankDTO {

    private Long categoryId;
    private String categoryName;
    private String icon;
    private BigDecimal amount;
    /** 占总支出的百分比，0-100 */
    private BigDecimal percentage;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}

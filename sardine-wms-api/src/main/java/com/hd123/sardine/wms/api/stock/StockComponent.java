/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	wms-wms-api
 * 文件名：	StockComponent.java
 * 模块说明：	
 * 修改历史：
 * 2014-3-21 - Gao JingYu - 创建。
 */
package com.hd123.sardine.wms.api.stock;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.common.entity.SourceBill;
import com.hd123.sardine.wms.common.entity.UCN;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;
import com.hd123.sardine.wms.common.validator.Validator;

/**
 * 库存部件
 * <p>
 * BK = owner + companyUuid + supplierUuid + binUuid + containerBarCode +
 * articleUuid + stockBatch + state + productionDate + operateBill + qpcStr;
 * 
 * BK = stockBatch + binUuid + containerBarCode + state + operateBill
 * 
 * @author Gao JingYu
 */
public class StockComponent implements Serializable, Validator {
  private static final long serialVersionUID = 3620065303232951398L;

  /** UUID最大长度限制 */
  public static final int LENGTH_UUID = 32;
  /** 属性{@link #getQpcStr()}最大长度限制 */
  public static final int LENGTH_QPC_STR = 30;
  /** 属性{@link #getMeasureUnit()}最大长度限制 */
  public static final int LENGTH_MEASURE_UNIT = 30;
  /** 属性{@link #getProductionBatch()}最大长度限制 */
  public static final int LENGTH_PRODUCTION_BATCH = 30;
  /** 属性{@link #getStockBatch()}最大长度限制 */
  public static final int LENGTH_STOCK_BATCH = 30;

  public static final String DEFAULT_OPERATE_BILL = "-";

  private String owner;
  private String companyUuid;
  private UCN article;
  private String articleSpec;
  private String binCode;
  private String containerBarcode;
  private Date productionDate;
  private Date validDate;
  private String productionBatch;
  private String stockBatch;
  private UCN supplier;
  private SourceBill sourceBill;
  private SourceBill operateBill = new SourceBill(DEFAULT_OPERATE_BILL, DEFAULT_OPERATE_BILL,
      DEFAULT_OPERATE_BILL);
  private StockState state = StockState.normal;
  private BigDecimal qty;
  private String qpcStr;
  private String munit;
  private Date instockTime = new Date();
  private BigDecimal price;
  
  public UCN getArticle() {
    return article;
  }

  public void setArticle(UCN article) {
    this.article = article;
  }

  public String getArticleSpec() {
    return articleSpec;
  }

  public void setArticleSpec(String articleSpec) {
    this.articleSpec = articleSpec;
  }

  public UCN getSupplier() {
    return supplier;
  }

  public void setSupplier(UCN supplier) {
    this.supplier = supplier;
  }

  public String getMunit() {
    return munit;
  }

  public void setMunit(String munit) {
    this.munit = munit;
  }

  /** 货主 */
  public String getOwner() {
    return owner;
  }

  /**
   * 设置货主。
   * 
   * @param owner
   *          not null，最大长度不能超过 {@link #LENGTH_UUID}。
   * @throws IllegalArgumentException
   */
  public void setOwner(String owner) {
    Assert.assertArgumentNotNull(owner, "owner");
    Assert.assertStringNotTooLong(owner, LENGTH_UUID, "owner");

    this.owner = owner;
  }

  public String getCompanyUuid() {
    return companyUuid;
  }

  public void setCompanyUuid(String companyUuid) {
    Assert.assertArgumentNotNull(companyUuid, "companyUuid");
    Assert.assertStringNotTooLong(companyUuid, LENGTH_UUID, "companyUuid");
    this.companyUuid = companyUuid;
  }

  /** 货位代码 */
  public String getBinCode() {
    return binCode;
  }

  public void setBinCode(String binCode) {
    Assert.assertArgumentNotNull(binCode, "binCode");
    Assert.assertStringNotTooLong(binCode, LENGTH_UUID, "binCode");
    this.binCode = binCode;
  }

  /** 容器条码 */
  public String getContainerBarcode() {
    return containerBarcode;
  }

  /**
   * 设置容器条码。
   * 
   * @param containerBarCode
   *          not null, 最大长度不能超过{@link #LENGTH_UUID}。
   * @throws IllegalArgumentException
   */
  public void setContainerBarcode(String containerBarcode) {
    Assert.assertArgumentNotNull(containerBarcode, "containerBarcode");
    Assert.assertStringNotTooLong(containerBarcode, LENGTH_UUID, "containerBarcode");
    this.containerBarcode = containerBarcode;
  }

  /** 批次 */
  public String getStockBatch() {
    return stockBatch;
  }

  /**
   * 设置批次
   * 
   * @param stockBatch
   *          not null, 最大长度不能超过{@link #LENGTH_STOCK_BATCH}。
   * @throws IllegalArgumentException
   */
  public void setStockBatch(String stockBatch) {
    Assert.assertArgumentNotNull(stockBatch, "stockBatch");
    Assert.assertStringNotTooLong(stockBatch, LENGTH_STOCK_BATCH, "stockBatch");
    this.stockBatch = stockBatch;
  }

  /** 生产日期 */
  public Date getProductionDate() {
    return productionDate;
  }

  /**
   * 设置生产日期。
   * 
   * @param productionDate
   *          not null。
   * @throws IllegalArgumentException
   */
  public void setProductionDate(Date productionDate) {
    Assert.assertArgumentNotNull(productionDate, "productionDate");
    this.productionDate = productionDate;
  }
  
  public String getProductionBatch() {
    return productionBatch;
  }

  public void setProductionBatch(String productionBatch) {
    this.productionBatch = productionBatch;
  }

  /** 来源入库单据 */
  public SourceBill getSourceBill() {
    return sourceBill;
  }

  /**
   * 设置来源单号
   * 
   * @param sourceBill
   *          not null。
   * @throws IllegalArgumentException
   */
  public void setSourceBill(SourceBill sourceBill) {
    Assert.assertArgumentNotNull(sourceBill, "sourceBill");
    this.sourceBill = sourceBill;
  }

  /** 到效期 */
  public Date getValidDate() {
    return validDate;
  }

  /**
   * 设置到效期。
   * 
   * @param validDate
   *          not null。
   * @throws IllegalArgumentException
   */
  public void setValidDate(Date validDate) {
    Assert.assertArgumentNotNull(validDate, "validDate");
    this.validDate = validDate;
  }

  /** 库存状态 */
  public StockState getState() {
    return state;
  }

  /**
   * 设置库存状态。
   * 
   * @param state
   *          not null。
   * @throws IllegalArgumentException
   */
  public void setState(StockState state) {
    Assert.assertArgumentNotNull(state, "state");
    this.state = state;
  }

  /** 数量 */
  public BigDecimal getQty() {
    return qty;
  }

  /**
   * 设置数量
   * 
   * @param qty
   *          not null,大于零。
   * @throws IllegalArgumentException
   */
  public void setQty(BigDecimal qty) {
    Assert.assertArgumentNotNull(qty, "qty");
    if (qty.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("数量不能小于零");
    }
    this.qty = qty;
  }

  /** 包装规格 */
  public String getQpcStr() {
    return qpcStr;
  }

  /**
   * 操作单据
   * 
   * 正常状态的库存，默认‘-’， 在单量库存的产生，是产生该变化的单据。
   */
  public SourceBill getOperateBill() {
    return operateBill;
  }

  public void setOperateBill(SourceBill operateBill) {
    this.operateBill = operateBill;
  }

  /**
   * 设置包装规格。
   * 
   * @param qpcStr
   *          not null, 最大长度不能超过{@link #LENGTH_QPC_STR}。
   * @throws IllegalArgumentException
   */
  public void setQpcStr(String qpcStr) {
    Assert.assertArgumentNotNull(qpcStr, "qpcStr");
    Assert.assertStringNotTooLong(qpcStr, LENGTH_QPC_STR, "qpcStr");
    this.qpcStr = qpcStr;
  }

  /** 入库时间 */
  public Date getInstockTime() {
    return instockTime;
  }

  /**
   * 设置入库时间
   * 
   * @param instockTime
   *          not null。
   * @throws IllegalArgumentException
   */
  public void setInstockTime(Date instockTime) {
    Assert.assertArgumentNotNull(instockTime, "instockTime");
    this.instockTime = instockTime;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public void validate() {
    Assert.assertArgumentNotNull(companyUuid, "companyUuid");
    Assert.assertArgumentNotNull(supplier, "supplier");
    Assert.assertArgumentNotNull(binCode, "binCode");
    Assert.assertArgumentNotNull(containerBarcode, "containerBarcode");
    Assert.assertArgumentNotNull(article, "article");
    Assert.assertArgumentNotNull(stockBatch, "stockBatch");
    Assert.assertArgumentNotNull(productionDate, "productionDate");
    Assert.assertArgumentNotNull(validDate, "validDate");
    Assert.assertArgumentNotNull(productionBatch, "productionBatch");
    Assert.assertArgumentNotNull(sourceBill, "sourceBill");
    Assert.assertArgumentNotNull(sourceBill.getBillNumber(), "sourceBill.billnumber");
    Assert.assertArgumentNotNull(sourceBill.getBillType(), "sourceBill.billType");
    Assert.assertArgumentNotNull(sourceBill.getBillUuid(), "sourceBill.billUuid");
    Assert.assertArgumentNotNull(state, "state");
    Assert.assertArgumentNotNull(qpcStr, "qpcStr");
    Assert.assertArgumentNotNull(qty, "qty");
    Assert.assertArgumentNotNull(munit, "munit");
    Assert.assertArgumentNotNull(instockTime, "instockTime");

    if (StringUtil.isNullOrBlank(owner))
      owner = ApplicationContextUtil.getCompanyUuid();
  }
}

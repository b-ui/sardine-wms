/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-api
 * 文件名：	AlcNtcBill.java
 * 模块说明：	
 * 修改历史：
 * 2017年5月31日 - yangwenzhu - 创建。
 */
package com.hd123.sardine.wms.api.out.alcntc;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.sardine.wms.common.entity.StandardEntity;
import com.hd123.sardine.wms.common.entity.UCN;
import com.hd123.sardine.wms.common.utils.QpcHelper;

/**
 * 配货通知单|实体类
 * 
 * @author yangwenzhu
 *
 */
public class AlcNtcBill extends StandardEntity {
  private static final long serialVersionUID = 8831468624891551855L;
  public static final String CAPTION = "配货通知单";
  private static int LENGTH_BILLNUMBER = 30;
  private static int LENGTH_REASON = 100;
  private static int LENGTH_REMARK = 255;

  private String billNumber;
  private AlcNtcBillState state = AlcNtcBillState.initial;
  private String deliveryReason = "正常";
  private DeliverySystem deliverySystem = DeliverySystem.tradition;
  private String deliveryMode;
  private String sourceBillNumber;
  private String sourceBillType;
  private UCN wrh;
  private String companyUuid;
  private String remark;
  private UCN customer;
  private String waveBillNumber;
  private String totalCaseQtyStr;
  private String planTotalCaseQtyStr;
  private String realTotalCaseQtyStr;
  private BigDecimal totalAmount = BigDecimal.ZERO;
  private BigDecimal realAmount = BigDecimal.ZERO;
  private Date alcDate = new Date();

  private List<AlcNtcBillItem> items = new ArrayList<AlcNtcBillItem>();

  /** 单号 */
  public String getBillNumber() {
    return billNumber;
  }

  public void setBillNumber(String billNumber) {
    this.billNumber = billNumber;
  }

  /** 配单状态 */
  public AlcNtcBillState getState() {
    return state;
  }

  public void setState(AlcNtcBillState state) {
    Assert.assertArgumentNotNull(state, "state");
    this.state = state;
  }

  /** 配货原因 */
  public String getDeliveryReason() {
    return deliveryReason;
  }

  public void setDeliveryReason(String deliveryReason) {
    Assert.assertArgumentNotNull(deliveryReason, "deliveryReason");
    Assert.assertStringNotTooLong(deliveryReason, LENGTH_REASON, "deliveryReason");
    this.deliveryReason = deliveryReason;
  }

  /** 配货体系 */
  public DeliverySystem getDeliverySystem() {
    return deliverySystem;
  }

  public void setDeliverySystem(DeliverySystem deliverySystem) {
    this.deliverySystem = deliverySystem;
  }

  /** 配货方式 */
  public String getDeliveryMode() {
    return deliveryMode;
  }

  public void setDeliveryMode(String deliveryMode) {
    Assert.assertArgumentNotNull(deliveryMode, "deliveryMode");
    Assert.assertStringNotTooLong(deliveryMode, LENGTH_REASON, "deliveryMode");
    this.deliveryMode = deliveryMode;
  }

  /** 来源单号 */
  public String getSourceBillNumber() {
    return sourceBillNumber;
  }

  public void setSourceBillNumber(String sourceBillNumber) {
    Assert.assertStringNotTooLong(sourceBillNumber, LENGTH_BILLNUMBER, "sourceBillNumber");
    this.sourceBillNumber = sourceBillNumber;
  }

  /** 来源单据类型 */
  public String getSourceBillType() {
    return sourceBillType;
  }

  public void setSourceBillType(String sourceBillType) {
    Assert.assertStringNotTooLong(sourceBillType, LENGTH_REASON, "sourceBillType");
    this.sourceBillType = sourceBillType;
  }

  /** 仓位 */
  public UCN getWrh() {
    return wrh;
  }

  public void setWrh(UCN wrh) {
    Assert.assertArgumentNotNull(wrh, "wrh");
    this.wrh = wrh;
  }

  /** 组织 */
  public String getCompanyUuid() {
    return companyUuid;
  }

  public void setCompanyUuid(String companyUuid) {
    this.companyUuid = companyUuid;
  }

  public BigDecimal getRealAmount() {
    return realAmount;
  }

  public void setRealAmount(BigDecimal realAmount) {
    Assert.assertArgumentNotNull(realAmount, "realAmount");
    this.realAmount = realAmount;
  }

  /** 说明 */
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    Assert.assertStringNotTooLong(remark, LENGTH_REMARK, "remark");
    this.remark = remark;
  }

  /** 客户 */
  public UCN getCustomer() {
    return customer;
  }

  public void setCustomer(UCN customer) {
    Assert.assertArgumentNotNull(customer, "customer");
    this.customer = customer;
  }

  /** 作业号 */
  public String getWaveBillNumber() {
    return waveBillNumber;
  }

  public void setWaveBillNumber(String waveBillNumber) {
    this.waveBillNumber = waveBillNumber;
  }

  /** 明细 */
  public List<AlcNtcBillItem> getItems() {
    return items;
  }

  public void setItems(List<AlcNtcBillItem> items) {
    this.items = items;
  }

  public String getTotalCaseQtyStr() {
    return totalCaseQtyStr;
  }

  public void setTotalCaseQtyStr(String totalCaseQtyStr) {
    Assert.assertArgumentNotNull(totalCaseQtyStr, "totalCaseQtyStr");
    this.totalCaseQtyStr = totalCaseQtyStr;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    Assert.assertArgumentNotNull(totalAmount, "totalAmount");
    this.totalAmount = totalAmount;
  }

  public String getPlanTotalCaseQtyStr() {
    return planTotalCaseQtyStr;
  }

  public void setPlanTotalCaseQtyStr(String planTotalCaseQtyStr) {
    this.planTotalCaseQtyStr = planTotalCaseQtyStr;
  }

  public String getRealTotalCaseQtyStr() {
    return realTotalCaseQtyStr;
  }

  public void setRealTotalCaseQtyStr(String realTotalCaseQtyStr) {
    this.realTotalCaseQtyStr = realTotalCaseQtyStr;
  }

  /** 配货时间 */
  public Date getAlcDate() {
    return alcDate;
  }

  public void setAlcDate(Date alcDate) {
    this.alcDate = alcDate;
  }

  public void refreshTotalInfo() {
    totalCaseQtyStr = "0";
    totalAmount = BigDecimal.ZERO;
    realAmount = BigDecimal.ZERO;
    for (AlcNtcBillItem item : items) {
      if (item.getPrice() == null)
        item.setPrice(BigDecimal.ZERO);
      item.setCaseQtyStr(QpcHelper.qtyToCaseQtyStr(item.getQty(), item.getQpcStr()));
      totalCaseQtyStr = QpcHelper.caseQtyStrAdd(totalCaseQtyStr, item.getCaseQtyStr());
      totalAmount = totalAmount.add(item.getPrice().multiply(item.getQty()));
    }
  }

  public void validate() {
    Assert.assertArgumentNotNull(state, "state");
    Assert.assertArgumentNotNull(wrh, "wrh");
    Assert.assertArgumentNotNull(customer, "customer");
    Assert.assertArgumentNotNull(deliveryReason, "deliveryReason");
    Assert.assertArgumentNotNull(deliveryMode, "deliveryMode");
    Assert.assertArgumentNotNull(realAmount, "realAmount");
    if (CollectionUtils.isEmpty(items))
      throw new IllegalArgumentException("明细行不能为空");

    for (int i = 0; i < items.size(); i++) {
      AlcNtcBillItem item = items.get(i);
      item.validate();
      for (int j = i + 1; j < items.size(); j++) {
        AlcNtcBillItem jItem = items.get(j);
        jItem.validate();
        if (item.getArticle().getUuid().equals(jItem.getArticle().getUuid())
            && item.getQpcStr().equals(jItem.getQpcStr()))
          throw new IllegalArgumentException(MessageFormat.format("第{0}行和第{1}行，商品{2}规格{3}不能重复", i,
              j, item.getArticle().getUuid(), item.getQpcStr()));
      }
    }
  }
}

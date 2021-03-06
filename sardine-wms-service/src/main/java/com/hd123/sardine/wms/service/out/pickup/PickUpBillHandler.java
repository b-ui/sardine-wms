/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-service
 * 文件名：	PickUpBillHandler.java
 * 模块说明：	
 * 修改历史：
 * 2017年8月25日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.service.out.pickup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.bin.Bin;
import com.hd123.sardine.wms.api.basicInfo.bin.BinService;
import com.hd123.sardine.wms.api.basicInfo.bin.BinState;
import com.hd123.sardine.wms.api.basicInfo.container.Container;
import com.hd123.sardine.wms.api.basicInfo.container.ContainerService;
import com.hd123.sardine.wms.api.basicInfo.container.ContainerState;
import com.hd123.sardine.wms.api.out.acceptance.AcceptanceBillService;
import com.hd123.sardine.wms.api.out.alcntc.AlcNtcBillService;
import com.hd123.sardine.wms.api.out.pickup.PickUpBill;
import com.hd123.sardine.wms.api.out.pickup.PickUpBillItem;
import com.hd123.sardine.wms.api.out.pickup.PickUpBillService;
import com.hd123.sardine.wms.api.out.pickup.PickUpBillState;
import com.hd123.sardine.wms.api.out.pickup.PickUpBillStockItem;
import com.hd123.sardine.wms.api.out.pickup.PickUpItemState;
import com.hd123.sardine.wms.api.out.wave.WaveBill;
import com.hd123.sardine.wms.api.out.wave.WaveBinUsage;
import com.hd123.sardine.wms.api.stock.StockChangement;
import com.hd123.sardine.wms.api.stock.StockComponent;
import com.hd123.sardine.wms.api.stock.StockService;
import com.hd123.sardine.wms.api.stock.StockShiftIn;
import com.hd123.sardine.wms.api.stock.StockShiftRule;
import com.hd123.sardine.wms.api.stock.StockState;
import com.hd123.sardine.wms.common.entity.UCN;
import com.hd123.sardine.wms.common.exception.WMSException;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;
import com.hd123.sardine.wms.common.utils.QpcHelper;
import com.hd123.sardine.wms.common.utils.UUIDGenerator;
import com.hd123.sardine.wms.dao.out.pickup.PickUpBillDao;
import com.hd123.sardine.wms.dao.out.pickup.PickUpBillItemDao;
import com.hd123.sardine.wms.dao.out.pickup.PickUpBillStockItemDao;

/**
 * @author zhangsai
 *
 */
public class PickUpBillHandler {

  @Autowired
  private PickUpBillService pickUpBillService;

  @Autowired
  private PickUpBillDao pickUpBillDao;

  @Autowired
  private PickUpBillItemDao pickUpBillItemDao;

  @Autowired
  private PickUpBillStockItemDao pickUpBillStockItemDao;

  @Autowired
  private StockService stockService;

  @Autowired
  private BinService binService;

  @Autowired
  private ContainerService containerService;

  @Autowired
  private AlcNtcBillService alcNtcBillService;

  @Autowired
  private AcceptanceBillService acceptanceBillService;

  /**
   * 更新拣货单明细的实际拣货数量和状态，同时回写配单明细的实际数量
   * 
   * @param pickUpItems
   *          拣货单明细，not null
   * @param toBinCode
   *          拣货的目标位置， not null
   * @param toContainerBarcode
   *          拣货的目标容器， not null
   * @param picker
   *          拣货人
   * @throws WMSException
   */
  public void updatePickUpBillItem(List<PickUpBillItem> pickUpItems, String toBinCode,
      String toContainerBarcode, UCN picker, BigDecimal qty) throws WMSException {
    Assert.assertArgumentNotNull(toBinCode, "toBinCode");
    Assert.assertArgumentNotNull(toContainerBarcode, "toContainerBarcode");
    if (CollectionUtils.isEmpty(pickUpItems))
      return;

    for (PickUpBillItem item : pickUpItems) {
      if (item.getState().equals(PickUpItemState.initial) == false)
        throw new WMSException("当前指令已处理，请刷新重试！");
      item.setState(PickUpItemState.finished);
      if (qty == null)
        item.setRealQty(item.getQty());
      else
        item.setRealQty(qty);
      item.setRealCaseQtyStr(item.getCaseQtyStr());
      item.setPickTime(new Date());
      item.setToBinCode(toBinCode);
      item.setToContainerBarcode(toContainerBarcode);
      if (picker != null)
        item.setPicker(picker);
      else
        item.setPicker(ApplicationContextUtil.getLoginUser());
      pickUpBillItemDao.updateRealQty(item);

      PickUpBill pickUpBill = pickUpBillDao.get(item.getPickUpBillUuid());
      assert pickUpBill != null;

      if (pickUpBill.getSourceBill().getBillType().equals(WaveBill.CAPTION))
        alcNtcBillService.pickUp(item.getAlcNtcBillItemUuid(), item.getRealQty());
      else
        acceptanceBillService.pickUp(item.getAlcNtcBillItemUuid(), item.getRealQty());
    }
  }

  /**
   * 更新拣货明细对应的拣货单状态，拣货完成的状态为已审核，否则 位进行中
   * 
   * @param uuids
   *          拣货单UUID
   * @return 修改后的拣货单集合
   * @throws WMSException
   */
  public List<PickUpBill> updatePickUpBill(Collection<String> uuids) throws WMSException {
    if (CollectionUtils.isEmpty(uuids))
      return new ArrayList<PickUpBill>();

    List<PickUpBill> bills = new ArrayList<PickUpBill>();
    for (String uuid : uuids) {
      PickUpBill bill = pickUpBillService.get(uuid);
      if (bill == null)
        throw new WMSException("拣货指令存在异常，请刷新重试！");
      if (bill.getState().equals(PickUpBillState.approved) == false
          && bill.getState().equals(PickUpBillState.inProgress) == false)
        throw new WMSException("当前拣货指令异常。请刷新重试！");
      boolean finish = true;
      for (PickUpBillItem item : bill.getItems()) {
        if (item.getState().equals(PickUpItemState.initial)
            || item.getState().equals(PickUpItemState.inProgress))
          finish = false;
      }
      if (finish)
        bill.setState(PickUpBillState.audited);

      if (bill.getState().equals(PickUpBillState.approved))
        bill.setState(PickUpBillState.inProgress);
      bill.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());
      pickUpBillDao.saveModify(bill);
      bills.add(bill);
    }
    return bills;
  }

  /**
   * 管理拣货的货位和容器
   * <p>
   * 根据拣货的来源货位和容器的库存情况，调整货位和容器的状态
   * 
   * @param pickUpItems
   *          拣货单明细
   * @param toBinCode
   *          目标货位
   * @param toContainerBarcode
   *          目标容器
   * @throws WMSException
   */
  public void manageBinAndContainer(List<PickUpBillItem> pickUpItems, String toBinCode,
      String toContainerBarcode) throws WMSException {
    Assert.assertArgumentNotNull(toBinCode, "toBinCode");
    if (CollectionUtils.isEmpty(pickUpItems))
      return;

    Bin toBin = binService.getBinByCode(toBinCode);
    if (toBin == null)
      throw new WMSException("目标货位" + toBinCode + "不存在！");
    if (BinState.free.equals(toBin.getState()))
      binService.changeState(toBin.getUuid(), toBin.getVersion(), BinState.using);

    if (StringUtil.isNullOrBlank(toContainerBarcode) == false) {
      Container toContainer = containerService.getByBarcode(toContainerBarcode);
      if (toContainer == null)
        throw new WMSException("目标容器" + toContainerBarcode + "不存在！");
      if (toContainer.getState().equals(ContainerState.STACONTAINERLOCK) == false)
        throw new WMSException("目标容器" + toContainerBarcode + "被占用，请重试！");
      containerService.change(toContainer.getUuid(), toContainer.getVersion(),
          ContainerState.STACONTAINERUSEING, toBinCode);
    }

    List<String> binCodes = new ArrayList<String>();
    List<String> containerBarcodes = new ArrayList<String>();
    for (PickUpBillItem item : pickUpItems) {
      if (binCodes.contains(item.getSourceBinCode()) == false) {
        Bin sourceBin = binService.getBinByCode(item.getSourceBinCode());
        if (sourceBin == null)
          throw new WMSException("拣货的来源货位" + item.getSourceBinCode() + "不存在！");
        if (stockService.hasBinStock(item.getSourceBinCode()) == false)
          binService.changeState(sourceBin.getUuid(), sourceBin.getVersion(), BinState.free);
        binCodes.add(item.getSourceBinCode());
      }

      if (containerBarcodes.contains(item.getSourceContainerBarcode()))
        continue;

      if (StringUtil.isNullOrBlank(item.getSourceContainerBarcode())
          || Objects.equals(item.getSourceContainerBarcode(), Container.VIRTUALITY_CONTAINER))
        continue;

      Container sourceContainer = containerService.getByBarcode(item.getSourceContainerBarcode());
      if (sourceContainer == null)
        throw new WMSException("拣货的来源容器" + item.getSourceContainerBarcode() + "不存在！");
      containerService.recycle(sourceContainer.getUuid(), sourceContainer.getVersion());
    }
  }

  /**
   * 根据拣货单明细生成拣货单的库存明细并处理库存的转移
   * 
   * @param pickUpItems
   *          拣货单明细， not null
   * @param bills
   *          拣货单集合
   * @param toBinCode
   *          目标货位
   * @param toContainerBarcode
   *          目标容器
   * @throws WMSException
   */
  public void stockOutAndGenerStockItem(List<PickUpBillItem> pickUpItems, List<PickUpBill> bills,
      String toBinCode, String toContainerBarcode) throws WMSException {
    Assert.assertArgumentNotNull(toBinCode, "toBinCode");
    Assert.assertArgumentNotNull(toContainerBarcode, "toContainerBarcode");
    if (CollectionUtils.isEmpty(pickUpItems))
      return;

    for (PickUpBillItem item : pickUpItems) {
      PickUpBill bill = findPickupBill(bills, item.getPickUpBillUuid());
      assert bill != null;
      StockShiftRule shiftRule = new StockShiftRule();
      shiftRule.setArticleUuid(item.getArticle().getUuid());
      shiftRule.setBinCode(item.getSourceBinCode());
      shiftRule.setCompanyUuid(ApplicationContextUtil.getCompanyUuid());
      shiftRule.setContainerBarcode(item.getSourceContainerBarcode());
      shiftRule.setOperateBillUuid(bill.getSourceBill().getBillUuid());
      shiftRule.setQpcStr(item.getQpcStr());
      shiftRule.setQty(item.getQty());
      shiftRule.setOwner(ApplicationContextUtil.getCompanyUuid());
      if (item.getBinUsage().equals(WaveBinUsage.FixBin) == false) {
        shiftRule.setState(StockState.locked);
        stockService.changeState(bill.getSourceBill(), Arrays.asList(shiftRule), StockState.locked,
            StockState.normal);
      } else {
        shiftRule.setState(StockState.forPick);
        stockService.shiftOut(bill.getSourceBill(), Arrays.asList(shiftRule));
      }

      shiftRule.setState(StockState.normal);
      shiftRule.setOperateBillUuid(null);
      shiftRule.setQty(item.getRealQty());
      List<StockChangement> changements = stockService.shiftOut(bill.getSourceBill(),
          Arrays.asList(shiftRule));
      List<PickUpBillStockItem> stockItems = new ArrayList<PickUpBillStockItem>();
      List<StockShiftIn> shiftIns = new ArrayList<StockShiftIn>();
      for (StockChangement changement : changements) {
        PickUpBillStockItem stockItem = new PickUpBillStockItem();
        StockComponent sc = changement.getStockComponent();
        stockItem.setArticleUuid(item.getArticle().getUuid());
        stockItem.setPickUpBillItemUuid(item.getUuid());
        stockItem.setProductionBatch(sc.getProductionBatch());
        stockItem.setProductionDate(sc.getProductionDate());
        stockItem.setQpcStr(item.getQpcStr());
        stockItem.setQty(sc.getQty());
        stockItem
            .setCaseQtyStr(QpcHelper.qtyToCaseQtyStr(stockItem.getQty(), stockItem.getQpcStr()));
        stockItem.setStockBatch(sc.getStockBatch());
        stockItem.setSupplierUuid(sc.getSupplier().getUuid());
        stockItem.setTargetContainerBarcode(toContainerBarcode);
        stockItem.setUuid(UUIDGenerator.genUUID());
        stockItem.setValidDate(sc.getValidDate());
        stockItems.add(stockItem);

        StockShiftIn shiftIn = new StockShiftIn();
        shiftIn.setStockComponent(changement.getStockComponent());
        shiftIn.getStockComponent().setBinCode(toBinCode);
        shiftIn.getStockComponent().setOwner(bill.getCustomer().getUuid());
        shiftIn.getStockComponent().setContainerBarcode(toContainerBarcode);
        shiftIns.add(shiftIn);
      }
      pickUpBillStockItemDao.saveNew(stockItems);
      stockService.shiftIn(bill.getSourceBill(), shiftIns);
    }
  }

  private PickUpBill findPickupBill(List<PickUpBill> bills, String pickupBillUuid) {
    assert bills != null;

    for (PickUpBill bill : bills) {
      if (bill.getUuid().equals(pickupBillUuid))
        return bill;
    }
    return null;
  }

}

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-dao
 * 文件名：	RtnSupplierNtcBillDao.java
 * 模块说明：	
 * 修改历史：
 * 2017年8月15日 - yangwenzhu - 创建。
 */
package com.hd123.sardine.wms.dao.rtn.rtnsupplierntc;

import java.util.List;

import com.hd123.sardine.wms.api.rtn.rtnsupplierntc.RtnSupplierNtcBill;
import com.hd123.sardine.wms.api.rtn.rtnsupplierntc.RtnSupplierNtcBillItem;
import com.hd123.sardine.wms.api.stock.Stock;
import com.hd123.sardine.wms.common.dao.BaseDao;

/**
 * @author yangwenzhu
 *
 */
public interface RtnSupplierNtcBillDao extends BaseDao<RtnSupplierNtcBill> {

  RtnSupplierNtcBill getByBillNumber(String billNumber);

  void insertItems(List<RtnSupplierNtcBillItem> items);

  void removeItmes(String billUuid);

  List<RtnSupplierNtcBillItem> queryItems(String uuid);

  RtnSupplierNtcBillItem getItem(String uuid);

  void updateItem(RtnSupplierNtcBillItem item);

  /**
   * 查询待下架的库存
   * 
   * @param wrhUuid
   *          仓库UUID
   * @param articleUuid
   *          商品UUID
   * @param supplierUuid
   *          供应商UUID
   * @return 库存集合
   */
  List<Stock> queryWaitUnShelveStocks(String wrhUuid, String articleUuid, String supplierUuid);
}

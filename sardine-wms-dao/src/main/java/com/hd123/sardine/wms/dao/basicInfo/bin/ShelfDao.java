/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-dao
 * 文件名：	ShelfDao.java
 * 模块说明：	
 * 修改历史：
 * 2017年2月13日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.dao.basicInfo.bin;

import java.util.List;

import com.hd123.sardine.wms.api.basicInfo.bin.Shelf;

/**
 * 货架Dao接口
 * 
 * @author zhangsai
 *
 */
public interface ShelfDao {

  void insert(Shelf shelf);

  List<Shelf> query(String companyUuid, String pathUuid);

  Shelf getByCode(String companyUuid, String code);

  Shelf get(String uuid, String companyUuid);
}

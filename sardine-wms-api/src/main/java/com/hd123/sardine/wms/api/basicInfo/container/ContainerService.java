/**
 * 版权所有(C)，LHWMS项目组，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-api
 * 文件名：	ContainerService.java
 * 模块说明：	
 * 修改历史：
 * 2017年2月7日 - Jing - 创建。
 */
package com.hd123.sardine.wms.api.basicInfo.container;

import com.hd123.sardine.wms.common.exception.VersionConflictException;
import com.hd123.sardine.wms.common.exception.WMSException;
import com.hd123.sardine.wms.common.query.PageQueryDefinition;
import com.hd123.sardine.wms.common.query.PageQueryResult;

/**
 * @author Jing
 *
 */
public interface ContainerService {
  public static final String QUERY_BARCODE_FIELD = "barcode";
  public static final String QUERY_STATE_FIELD = "state";
  public static final String QUERY_POSITION_FIELD = "position";
  public static final String QUERY_TOPOSITION_FIELD = "toPosition";
  public static final String QUERY_TYPECODE_FIELD = "typeCode";

  /***
   * 新增容器
   * 
   * @param containerTypeUuid
   *          not null
   */
  void saveNew(String containerTypeUuid) throws WMSException;

  /***
   * 根据容器条码和组织查询容器
   * 
   * @param barcode
   *          容器条码
   * @return 容器
   */
  Container getByBarcode(String barcode);

  /***
   * 分页查询容器列表
   * 
   * @param definition
   * @return 容器列表
   */
  PageQueryResult<Container> query(PageQueryDefinition definition);

  /**
   * 修改容器状态和目标位置
   * <p>
   * 状态和目标位置不能同时为空
   * 
   * @param uuid
   *          UUID，not null
   * @param version
   *          版本号
   * @param state
   *          状态
   * @param position
   *          目标位置
   * @throws IllegalArgumentException
   * @throws VersionConflictException
   * @throws WMSException
   */
  void change(String uuid, long version, ContainerState state, String position)
      throws IllegalArgumentException, VersionConflictException, WMSException;

  /**
   * 回收容器，查询容器库存，存在库存则改为已使用，不存在库存则改为空闲或者作废
   * 
   * @param uuid
   *          容器UUID
   * @param version
   *          容器版本号
   * @throws WMSException
   */
  void recycle(String uuid, long version) throws WMSException;

  /**
   * 锁定容器，临时状态防止容器同时被使用
   * 
   * @param uuid
   *          容器UUID
   * @param version
   *          容器版本号
   * @throws WMSException
   */
  void lock(String uuid, long version) throws WMSException;

  /**
   * 使用容器，查询容器库存，存在库存则改为已使用，不存在库存则改为空闲
   * 
   * @param uuid
   *          容器UUID
   * @param version
   *          容器版本号
   * @param position
   * @throws WMSException
   */
  void using(String uuid, long version, String position) throws WMSException;
}

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-service
 * 文件名：	SerialArchServiceImpl.java
 * 模块说明：	
 * 修改历史：
 * 2017年6月14日 - yangwenzhu - 创建。
 */
package com.hd123.sardine.wms.service.tms.serialarch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.customer.Customer;
import com.hd123.sardine.wms.api.basicInfo.customer.CustomerService;
import com.hd123.sardine.wms.api.tms.serialarch.SerialArch;
import com.hd123.sardine.wms.api.tms.serialarch.SerialArchInfo;
import com.hd123.sardine.wms.api.tms.serialarch.SerialArchLine;
import com.hd123.sardine.wms.api.tms.serialarch.SerialArchLineCustomer;
import com.hd123.sardine.wms.api.tms.serialarch.SerialArchService;
import com.hd123.sardine.wms.common.entity.UCN;
import com.hd123.sardine.wms.common.exception.WMSException;
import com.hd123.sardine.wms.common.query.PageQueryDefinition;
import com.hd123.sardine.wms.common.query.PageQueryResult;
import com.hd123.sardine.wms.common.query.PageQueryUtil;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;
import com.hd123.sardine.wms.common.utils.UUIDGenerator;
import com.hd123.sardine.wms.dao.tms.serialarch.SerialArchDao;
import com.hd123.sardine.wms.service.ia.BaseWMSService;
import com.hd123.sardine.wms.service.log.EntityLogger;

/**
 * @author yangwenzhu
 *
 */
public class SerialArchServiceImpl extends BaseWMSService implements SerialArchService {

  @Autowired
  private SerialArchDao dao;
  @Autowired
  private EntityLogger logger;
  @Autowired
  private CustomerService customerService;

  @Override
  public String saveNewSerialArch(SerialArch arch) throws WMSException {
    Assert.assertArgumentNotNull(arch, "arch");

    arch.validate();
    SerialArch s = dao.getByCode(arch.getCode());
    if (s != null)
      throw new WMSException(MessageFormat.format("当前组织已存在代码为{0}的线路体系", arch.getCode()));

    arch.setUuid(UUIDGenerator.genUUID());
    arch.setCompanyUuid(ApplicationContextUtil.getCompanyUuid());
    arch.setCreateInfo(ApplicationContextUtil.getOperateInfo());
    arch.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());

    dao.insert(arch);

    logger.injectContext(this, arch.getUuid(), SerialArch.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_ADDNEW, "新增线路体系");
    return arch.getUuid();
  }

  @Override
  public PageQueryResult<SerialArch> query(PageQueryDefinition definition) {
    Assert.assertArgumentNotNull(definition, "definition");

    definition.setCompanyUuid(ApplicationContextUtil.getCompanyUuid());
    List<SerialArch> list = dao.query(definition);
    PageQueryResult<SerialArch> pqr = new PageQueryResult<>();
    PageQueryUtil.assignPageInfo(pqr, definition);
    pqr.setRecords(list);
    return pqr;
  }

  @Override
  public String saveNewSerialArchLine(SerialArchLine line) throws WMSException {
    Assert.assertArgumentNotNull(line, "line");

    line.validate();

    //// SerialArch arch = dao.get(line.getSerialArch().getUuid());
    // if (Objects.isNull(arch))
    // throw new WMSException(
    // MessageFormat.format("运输线路对应的线路体系{0}不存在",
    //// line.getSerialArch().getCode()));
    SerialArchLine l = dao.getLineByCode(line.getCode());
    if (Objects.nonNull(l))
      throw new WMSException(MessageFormat.format("已存在代码为{0}的运输线路，不能重复使用", line.getCode()));
    // verifyLineCustomer(line);

    line.setUuid(UUIDGenerator.genUUID());
    line.setCreateInfo(ApplicationContextUtil.getOperateInfo());
    line.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());
    line.setSerialArch(new UCN(SerialArch.DEFAULT_UUID, "", ""));

    for (SerialArchLineCustomer c : line.getCustomers()) {
      if (StringUtil.isNullOrBlank(c.getCustomer().getUuid()))
        c.getCustomer().setUuid((UUIDGenerator.genUUID()));
      c.setSerialArchLineUuid(line.getUuid());
      dao.insertLineCustomer(c);
    }

    dao.insertSerialArchLine(line);
    logger.injectContext(this, line.getUuid(), SerialArchLine.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_ADDNEW, "新增运输线路");
    return line.getUuid();
  }

  private void verifyLineCustomer(SerialArchLine line) throws WMSException {
    assert line != null;

    StringBuffer errorMsg = new StringBuffer();
    for (int i = 0; i < line.getCustomers().size(); i++) {
      SerialArchLineCustomer c = line.getCustomers().get(i);
      Customer customer = customerService.get(c.getCustomer().getUuid());
      if (customer == null)
        throw new WMSException("要保存到运输线路中的客户" + c.getCustomer().getName() + "不存在");
      List<SerialArchLine> lineList = dao.getLineByCustomerUuid(c.getCustomer().getUuid());
      for (SerialArchLine l : lineList) {
        if (l != null && l.getUuid().equals(line.getUuid()) == false
            && l.getSerialArch().getUuid().equals(line.getSerialArch().getUuid()))
          errorMsg.append("客户" + customer.getName() + "已经存在于其他路线上");
        // if
        // (l.getSerialArch().getUuid().equals(line.getSerialArch().getUuid())
        // == false)
        // errorMsg.append("客户" + customer.getName() + "不能同时存在多个线路体系中");
      }

      for (int j = i + 1; j < line.getCustomers().size(); j++) {
        SerialArchLineCustomer jC = line.getCustomers().get(j);
        if (c.getCustomer().getUuid().equals(jC.getCustomer().getUuid()))
          errorMsg.append(
              "第" + c.getOrder() + "行与第" + jC.getOrder() + "行的客户" + customer.getName() + "重复");
      }
    }
    if (errorMsg.length() > 0)
      throw new WMSException(errorMsg.toString());
  }

  @Override
  public List<SerialArchLine> getLinesByArchUuid(String archUuid) {
    if (StringUtil.isNullOrBlank(archUuid))
      return new ArrayList<>();
    return dao.getLineByArchUuid(archUuid);
  }

  @Override
  public List<SerialArchInfo> queryTreeData() {
    // String companyUuid = ApplicationContextUtil.getCompanyUuid();
    // PageQueryDefinition definition = new PageQueryDefinition();
    // definition.setCompanyUuid(companyUuid);
    // definition.setPageSize(0);
    // definition.setSortField("code");
    // definition.setOrderDir(OrderDir.asc);
    // List<SerialArch> list = dao.query(definition);// 线路体系
    List<SerialArchInfo> tree = new ArrayList<>();
    // SerialArchInfo info = new SerialArchInfo();
    // info.setKey(serialArch.toFriendString());
    // info.setTitle(serialArch.getUuid());
    List<SerialArchLine> lines = dao.getLineByCompanyUuid();// 运输线路
    List<SerialArchInfo> children = new ArrayList<>();
    for (SerialArchLine l : lines) {
      SerialArchInfo i = new SerialArchInfo();
      i.setKey(l.toFriendString());
      i.setTitle(l.getUuid());
      i.setChildren(null);
      children.add(i);
      tree.add(i);
    }
    // info.setChildren(children);
    return tree;
  }

  @Override
  public SerialArchLine getLineByCode(String code) throws WMSException {
    if (StringUtil.isNullOrBlank(code))
      return null;
    SerialArchLine line = dao.getLineByCode(code);
    if (Objects.isNull(line))
      return null;
    List<SerialArchLineCustomer> customers = dao.getCustomerByLine(line.getUuid());
    for (SerialArchLineCustomer c : customers) {
      Customer customer = customerService.get(c.getCustomer().getUuid());
      if (Objects.isNull(customer))
        throw new WMSException("线路中的客户" + c.getCustomer().getUuid() + "不存在");
      c.setCustomer(new UCN(customer.getUuid(), customer.getCode(), customer.getName()));
    }
    line.setCustomers(customers);
    return line;
  }

  @Override
  public String saveLineCustomer(String lineUuid, List<String> customerUuids) throws WMSException {
    Assert.assertArgumentNotNull(lineUuid, "lineUuid");
    Assert.assertArgumentNotNull(customerUuids, "customerUuids");

    SerialArchLine line = dao.getLine(lineUuid);
    if (Objects.isNull(line))
      throw new WMSException("运输线路" + lineUuid + "不存在");
    verifyLineCustomer(line);
    List<SerialArchLineCustomer> list = dao.getCustomerByLine(lineUuid);
    int size = list.size();
    if (customerUuids.isEmpty())
      return null;
    SerialArchLineCustomer lineCustomer = new SerialArchLineCustomer();
    for (int i = 0; i < customerUuids.size(); i++) {
      Customer customer = customerService.get(customerUuids.get(i));
      if (Objects.isNull(customer))
        throw new WMSException("要添加的客户" + customerUuids.get(i) + "不存在");
      lineCustomer.setCustomer(new UCN(customer.getUuid(), customer.getCode(), customer.getName()));
      lineCustomer.setOrder(size + i + 1);
      lineCustomer.setSerialArchLineUuid(lineUuid);
      dao.insertLineCustomer(lineCustomer);

      logger.injectContext(this, lineUuid, SerialArchLineCustomer.class.getName(),
          ApplicationContextUtil.getOperateContext());
      logger.log(EntityLogger.EVENT_ADDNEW, "新增线路客户");
    }
    return line.getCode();
  }

  @Override
  public List<SerialArchLineCustomer> getCustomersByLineUuid(String lineUuid) {
    if (StringUtil.isNullOrBlank(lineUuid))
      return new ArrayList<>();
    List<SerialArchLineCustomer> list = dao.getCustomerByLine(lineUuid);
    for (SerialArchLineCustomer c : list) {
      Customer customer = customerService.get(c.getCustomer().getUuid());
      if (Objects.nonNull(customer))
        c.setCustomer(new UCN(customer.getUuid(), customer.getCode(), customer.getName()));
    }
    return list;
  }

  @Override
  public SerialArchLine getLine(String lineUuid) throws WMSException {
    if (StringUtil.isNullOrBlank(lineUuid))
      return null;
    SerialArchLine line = dao.getLine(lineUuid);
    if (Objects.isNull(line))
      return null;
    List<SerialArchLineCustomer> customers = dao.getCustomerByLine(line.getUuid());
    for (SerialArchLineCustomer c : customers) {
      Customer customer = customerService.get(c.getCustomer().getUuid());
      if (Objects.isNull(customer))
        throw new WMSException("线路中的客户" + c.getCustomer().getUuid() + "不存在");
      c.setCustomer(new UCN(customer.getUuid(), customer.getCode(), customer.getName()));
    }
    line.setCustomers(customers);
    return line;
  }

  @Override
  public void removeCustomerFromLine(String customerUuid, String lineUuid) throws WMSException {
    Assert.assertArgumentNotNull(customerUuid, "customerUuid");
    Assert.assertArgumentNotNull(lineUuid, "lineUuid");

    SerialArchLineCustomer customer = dao.getCustomer(lineUuid, customerUuid);
    if (Objects.isNull(customer))
      throw new WMSException("该线路中不存在要踢出的客户");
    dao.removeCustomer(lineUuid, customerUuid);
    List<SerialArchLineCustomer> list = dao.queryCustomerOrderLess(lineUuid, customer.getOrder());
    for (SerialArchLineCustomer c : list) {
      c.setOrder(c.getOrder() - 1);
      dao.updateLineCustomer(c);
    }

    logger.injectContext(this, customerUuid, SerialArchLineCustomer.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_REMOVE, "删除运输线路中的客户");

  }

  @Override
  public void upOrder(String customerUuid, String lineUuid, int order) throws WMSException {
    Assert.assertArgumentNotNull(customerUuid, "customerUuid");
    Assert.assertArgumentNotNull(lineUuid, "lineUuid");
    Assert.assertArgumentNotNull(order, "order");

    SerialArchLineCustomer customer = dao.getCustomer(lineUuid, customerUuid);
    if (Objects.isNull(customer))
      throw new WMSException("调整序号的客户不存在");
    if (order != customer.getOrder())
      throw new WMSException("当前线路的客户顺序已被其他用户修改");
    if (order == 1)
      return;
    order -= 1;
    SerialArchLineCustomer c = new SerialArchLineCustomer();
    c.setCustomer(new UCN(customerUuid, "", ""));
    c.setOrder(order);
    c.setSerialArchLineUuid(lineUuid);
    SerialArchLineCustomer c2 = dao.getCustomerByLineAndOrder(lineUuid, order);
    if (Objects.isNull(c2))
      throw new WMSException("调整序号的上一客户不存在");
    dao.updateLineCustomer(c);

    c2.setOrder(c2.getOrder() + 1);
    dao.updateLineCustomer(c2);

    logger.injectContext(this, lineUuid, SerialArchLineCustomer.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "调整运输线路中的客户顺序");
  }

  @Override
  public void downOrder(String customerUuid, String lineUuid, int order) throws WMSException {
    Assert.assertArgumentNotNull(customerUuid, "customerUuid");
    Assert.assertArgumentNotNull(lineUuid, "lineUuid");
    Assert.assertArgumentNotNull(order, "order");

    SerialArchLineCustomer customer = dao.getCustomer(lineUuid, customerUuid);
    if (Objects.isNull(customer))
      throw new WMSException("调整序号的客户不存在");
    if (order != customer.getOrder())
      throw new WMSException("当前线路的客户顺序已被其他用户修改");
    List<SerialArchLineCustomer> list = dao.getCustomerByLine(lineUuid);
    if (order == list.size())
      return;
    order += 1;
    SerialArchLineCustomer c = new SerialArchLineCustomer();
    c.setCustomer(new UCN(customerUuid, "", ""));
    c.setOrder(order);
    c.setSerialArchLineUuid(lineUuid);
    SerialArchLineCustomer c2 = dao.getCustomerByLineAndOrder(lineUuid, order);
    if (Objects.isNull(c2))
      throw new WMSException("调整序号的下一客户不存在");
    dao.updateLineCustomer(c);

    c2.setOrder(c2.getOrder() - 1);
    dao.updateLineCustomer(c2);

    logger.injectContext(this, lineUuid, SerialArchLineCustomer.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "调整运输线路中的客户顺序");

  }

  @Override
  public void stickCustomer(String customerUuid, String lineUuid, int order) throws WMSException {
    Assert.assertArgumentNotNull(customerUuid, "customerUuid");
    Assert.assertArgumentNotNull(lineUuid, "lineUuid");
    Assert.assertArgumentNotNull(order, "order");

    SerialArchLineCustomer customer = dao.getCustomer(lineUuid, customerUuid);
    if (Objects.isNull(customer))
      throw new WMSException("置顶的客户不存在");
    if (order != customer.getOrder())
      throw new WMSException("当前线路的客户顺序已被其他用户修改");
    List<SerialArchLineCustomer> list = dao.queryCustomerOrderMore(lineUuid, order);
    if (order == 1)
      return;
    order = 1;
    SerialArchLineCustomer c = new SerialArchLineCustomer();
    c.setCustomer(new UCN(customerUuid, "", ""));
    c.setOrder(order);
    c.setSerialArchLineUuid(lineUuid);
    dao.updateLineCustomer(c);

    for (SerialArchLineCustomer c2 : list) {
      c2.setOrder(c2.getOrder() + 1);
      dao.updateLineCustomer(c2);
    }

    logger.injectContext(this, lineUuid, SerialArchLineCustomer.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "置顶客户");

  }

  @Override
  public void postponeCustomer(String customerUuid, String lineUuid, int order)
      throws WMSException {
    Assert.assertArgumentNotNull(customerUuid, "customerUuid");
    Assert.assertArgumentNotNull(lineUuid, "lineUuid");
    Assert.assertArgumentNotNull(order, "order");

    SerialArchLineCustomer customer = dao.getCustomer(lineUuid, customerUuid);
    if (Objects.isNull(customer))
      throw new WMSException("置后的客户不存在");
    if (order != customer.getOrder())
      throw new WMSException("当前线路的客户顺序已被其他用户修改");
    List<SerialArchLineCustomer> list = dao.getCustomerByLine(lineUuid);
    List<SerialArchLineCustomer> lessList = dao.queryCustomerOrderLess(lineUuid, order);
    if (order == list.size())
      return;
    order = list.size();
    SerialArchLineCustomer c = new SerialArchLineCustomer();
    c.setCustomer(new UCN(customerUuid, "", ""));
    c.setOrder(order);
    c.setSerialArchLineUuid(lineUuid);
    dao.updateLineCustomer(c);
    for (SerialArchLineCustomer c2 : lessList) {
      c2.setOrder(c2.getOrder() - 1);
      dao.updateLineCustomer(c2);
    }

    logger.injectContext(this, lineUuid, SerialArchLineCustomer.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "置后客户");

  }

  @Override
  public PageQueryResult<SerialArchLineCustomer> queryCustomerByLine(PageQueryDefinition definition)
      throws WMSException {
    Assert.assertArgumentNotNull(definition, "definition");

    PageQueryResult<SerialArchLineCustomer> pqr = new PageQueryResult<>();
    List<SerialArchLineCustomer> list = dao.queryCustomerByLine(definition);
    for (SerialArchLineCustomer c : list) {
      Customer customer = customerService.get(c.getCustomer().getUuid());
      c.setCustomer(new UCN(customer.getUuid(), customer.getCode(), customer.getName()));
    }
    PageQueryUtil.assignPageInfo(pqr, definition);
    pqr.setRecords(list);
    return pqr;
  }

  @Override
  public SerialArch get(String uuid) {
    if (StringUtil.isNullOrBlank(uuid))
      return null;
    return dao.get(uuid);
  }

  @Override
  public void removeLine(String code) throws WMSException {
    Assert.assertArgumentNotNull(code, "code");

    SerialArchLine line = dao.getLineByCode(code);
    ;
    if (Objects.isNull(line))
      throw new WMSException(MessageFormat.format("要删除的线路{0}不存在", code));
    List<SerialArchLineCustomer> lineCustomers = dao.getCustomerByLine(line.getUuid());
    if (CollectionUtils.isEmpty(lineCustomers) == false)
      throw new WMSException("线路下包含有门店，不能删除");

    dao.removeLine(line.getUuid());

  }

  @Override
  public PageQueryResult<Customer> queryCustomerWithoutLine(PageQueryDefinition definition) {
    Assert.assertArgumentNotNull(definition, "definition");

    List<Customer> list = dao.queryCustomerWithoutLine(definition);
    PageQueryResult<Customer> pqr = new PageQueryResult<>();
    PageQueryUtil.assignPageInfo(pqr, definition);
    pqr.setRecords(list);
    return pqr;
  }

}

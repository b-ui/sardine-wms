/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-web
 * 文件名：	CustomerController.java
 * 模块说明：	
 * 修改历史：
 * 2017年1月13日 - yangwenzhu - 创建。
 */
package com.hd123.sardine.wms.web.basicInfo.customer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.customer.Customer;
import com.hd123.sardine.wms.api.basicInfo.customer.CustomerService;
import com.hd123.sardine.wms.api.basicInfo.customer.CustomerState;
import com.hd123.sardine.wms.common.http.ErrorRespObject;
import com.hd123.sardine.wms.common.http.RespObject;
import com.hd123.sardine.wms.common.http.RespStatus;
import com.hd123.sardine.wms.common.query.OrderDir;
import com.hd123.sardine.wms.common.query.PageQueryDefinition;
import com.hd123.sardine.wms.common.query.PageQueryResult;
import com.hd123.sardine.wms.web.base.BaseController;

/**
 * @author yangwenzhu
 *
 */
@RestController
@RequestMapping("/basicinfo/customer")
public class CustomerController extends BaseController {
  @Autowired
  private CustomerService customerService;
  List<Customer> list = new ArrayList<Customer>();

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public @ResponseBody RespObject get(@RequestParam(value = "customerUuid") String customerUuid,
      @RequestParam(value = "token") String token) {
    RespObject resp = new RespObject();
    try {
      Customer customer = customerService.get(customerUuid);
      resp.setObj(customer);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (Exception e) {
      return new ErrorRespObject("查询客户失败：" + e.getMessage());
    }
    return resp;
  }

  @RequestMapping(value = "/getbycode", method = RequestMethod.GET)
  public @ResponseBody RespObject getByCode(
      @RequestParam(value = "customerCode") String customerCode,
      @RequestParam(value = "token") String token) {
    RespObject resp = new RespObject();
    try {
      Customer customer = customerService.getByCode(customerCode);
      resp.setObj(customer);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (Exception e) {
      return new ErrorRespObject("查询客户失败：" + e.getMessage());
    }
    return resp;
  }

  @RequestMapping(value = "/query", method = RequestMethod.GET)
  public @ResponseBody RespObject query(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize,
      @RequestParam(value = "sort", required = false) String sort,
      @RequestParam(value = "order", required = false, defaultValue = "asc") String sortDirection,
      @RequestParam(value = "token", required = false) String token,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "state", required = false) String state) {
    RespObject resp = new RespObject();
    try {
      PageQueryDefinition definition = new PageQueryDefinition();
      definition.setPage(page);
      definition.setPageSize(pageSize);
      definition.setSortField(sort);
      definition.setOrderDir(OrderDir.valueOf(sortDirection));
      definition.put(CustomerService.QUERY_CODE_FIELD, code);
      definition.put(CustomerService.QUERY_NAME_FIELD, name);
      definition.put(CustomerService.QUERY_STATE_FIELD,
          StringUtil.isNullOrBlank(state) ? null : CustomerState.valueOf(state));
      PageQueryResult<Customer> result = customerService.query(definition);
      resp.setObj(result);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (Exception e) {
      return new ErrorRespObject("分页查询客户失败：" + e.getMessage());
    }
    return resp;
  }

  @RequestMapping(value = "/insert", method = RequestMethod.POST)
  public @ResponseBody RespObject insert(
      @RequestParam(value = "token", required = true) String token,
      @RequestBody Customer customer) {
    RespObject resp = new RespObject();
    try {
      String uuid = customerService.insert(customer);
      resp.setObj(uuid);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (Exception e) {
      return new ErrorRespObject("新增客户失败：" + e.getMessage());
    }
    return resp;
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public @ResponseBody RespObject update(
      @RequestParam(value = "token", required = true) String token,
      @RequestBody Customer customer) {
    RespObject resp = new RespObject();
    try {
      customerService.update(customer);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (IllegalArgumentException e) {
      return new ErrorRespObject("参数异常：" + e.getMessage());
    } catch (Exception e) {
      return new ErrorRespObject("修改客户失败：" + e.getMessage());
    }
    return resp;
  }

  @RequestMapping(value = "/offline", method = RequestMethod.PUT)
  public @ResponseBody RespObject offline(@RequestParam(value = "uuid", required = true) String uuid,
      @RequestParam(value = "token", required = true) String token,
      @RequestParam(value = "version", required = true) long version) {
    RespObject resp = new RespObject();
    try {
      customerService.offline(uuid, version);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (IllegalArgumentException e) {
      return new ErrorRespObject("参数异常：" + e.getMessage());
    } catch (Exception e) {
      return new ErrorRespObject("禁用客户失败：" + e.getMessage());
    }
    return resp;
  }

  @RequestMapping(value = "/online", method = RequestMethod.PUT)
  public @ResponseBody RespObject online(
      @RequestParam(value = "uuid", required = true) String uuid,
      @RequestParam(value = "token", required = true) String token,
      @RequestParam(value = "version", required = true) long version) {
    RespObject resp = new RespObject();
    try {
      customerService.online(uuid, version);
      resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
    } catch (IllegalArgumentException e) {
      return new ErrorRespObject("参数异常：" + e.getMessage());
    } catch (Exception e) {
      return new ErrorRespObject("启用客户失败：" + e.getMessage());
    }
    return resp;
  }

}

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-web
 * 文件名：	DecIncController.java
 * 模块说明：	
 * 修改历史：
 * 2017年5月23日 - yangwenzhu - 创建。
 */
package com.hd123.sardine.wms.web.inner.decinc;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.article.Article;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleQpc;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleService;
import com.hd123.sardine.wms.api.inner.decincinv.DecIncInvBill;
import com.hd123.sardine.wms.api.inner.decincinv.DecIncInvBillItem;
import com.hd123.sardine.wms.api.inner.decincinv.DecIncInvBillService;
import com.hd123.sardine.wms.api.inner.decincinv.DecIncInvBillState;
import com.hd123.sardine.wms.api.inner.decincinv.DecIncInvBillType;
import com.hd123.sardine.wms.api.stock.StockExtendInfo;
import com.hd123.sardine.wms.api.stock.StockFilter;
import com.hd123.sardine.wms.api.stock.StockService;
import com.hd123.sardine.wms.common.entity.UCN;
import com.hd123.sardine.wms.common.exception.NotLoginInfoException;
import com.hd123.sardine.wms.common.exception.WMSException;
import com.hd123.sardine.wms.common.http.ErrorRespObject;
import com.hd123.sardine.wms.common.http.RespObject;
import com.hd123.sardine.wms.common.http.RespStatus;
import com.hd123.sardine.wms.common.query.OrderDir;
import com.hd123.sardine.wms.common.query.PageQueryDefinition;
import com.hd123.sardine.wms.common.query.PageQueryResult;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;
import com.hd123.sardine.wms.web.base.BaseController;

/**
 * @author yangwenzhu
 *
 */
@RestController
@RequestMapping("/inner/decInc")
public class DecIncController extends BaseController {

    @Autowired
    private DecIncInvBillService service;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private StockService stockService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public @ResponseBody RespObject insert(
            @RequestParam(value = "token", required = true) String token,
            @RequestBody DecIncInvBill decIncInvBill) {
        RespObject resp = new RespObject();
        try {
            for (DecIncInvBillItem item : decIncInvBill.getItems()) {
                Article article = articleService.get(item.getArticle().getUuid());

                assert item.getProductionDate() != null;
                Calendar c = Calendar.getInstance();
                c.setTime(item.getProductionDate());
                c.add(Calendar.DATE, article.getExpDays());
                item.setExpireDate(c.getTime());
                for (ArticleQpc qpc : article.getQpcs()) {
                    if (qpc.getQpcStr().equals(item.getQpcStr())) {
                        item.setMeasureUnit(qpc.getMunit());
                    }
                }
                item.setAmount(item.getPrice().subtract(item.getQty()).abs());
            }
            ApplicationContextUtil.setCompany(getLoginCompany(token));
            decIncInvBill.setCompanyUuid(getLoginCompany(token).getUuid());
            String uuid = service.saveNew(decIncInvBill);
            resp.setObj(uuid);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (Exception e) {
            return new ErrorRespObject("新建损溢单失败", e.getMessage());
        }
        return resp;

    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public @ResponseBody RespObject query(
            @RequestParam(value = "token", required = true) String token,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "order", required = false,
                    defaultValue = "asc") String sortDirection,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "wrh", required = false) String wrh) {
        RespObject resp = new RespObject();
        try {
            PageQueryDefinition definition = new PageQueryDefinition();
            definition.setPage(page);
            definition.setPageSize(pageSize);
            definition.setSortField(sort);
            definition.setOrderDir(OrderDir.valueOf(sortDirection));
            definition.setCompanyUuid(getLoginCompany(token).getUuid());
            definition.put(DecIncInvBillService.QUERY_BILLNUMBER_LIKE, billNumber);
            definition.put(DecIncInvBillService.QUERY_STATE_EQUALS,
                    StringUtil.isNullOrBlank(state) ? null : DecIncInvBillState.valueOf(state));
            definition.put(DecIncInvBillService.QUERY_WRHCODE_LIKE, wrh);
            definition.put(DecIncInvBillService.QUERY_TYPE_EQUALS,
                    StringUtil.isNullOrBlank(type) ? null : DecIncInvBillType.valueOf(type));

            PageQueryResult<DecIncInvBill> result = service.query(definition);
            resp.setObj(result);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (Exception e) {
            return new ErrorRespObject("分页查询损溢单失败", e.getMessage());
        }
        return resp;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public @ResponseBody RespObject get(
            @RequestParam(value = "token", required = true) String token,
            @RequestParam(value = "uuid") String uuid) {
        RespObject resp = new RespObject();
        try {
            ApplicationContextUtil.setCompany(getLoginCompany(token));
            DecIncInvBill bill = service.get(uuid);
            resp.setObj(bill);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (Exception e) {
            return new ErrorRespObject("获取损溢单失败", e.getMessage());
        }
        return resp;

    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public @ResponseBody RespObject update(
            @RequestParam(value = "token", required = true) String token,
            @RequestBody DecIncInvBill decIncInvBill) {
        RespObject resp = new RespObject();
        try {
            for (DecIncInvBillItem item : decIncInvBill.getItems()) {
                Article article = articleService.get(item.getArticle().getUuid());

                assert item.getProductionDate() != null;
                Calendar c = Calendar.getInstance();
                c.setTime(item.getProductionDate());
                c.add(Calendar.DATE, article.getExpDays());
                item.setExpireDate(c.getTime());
                for (ArticleQpc qpc : article.getQpcs()) {
                    if (qpc.getQpcStr().equals(item.getQpcStr())) {
                        item.setMeasureUnit(qpc.getMunit());
                    }
                }
                item.setAmount(item.getPrice().subtract(item.getQty()).abs());
            }
            ApplicationContextUtil.setCompany(getLoginCompany(token));
            service.saveModify(decIncInvBill);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (NotLoginInfoException e) {
            return new ErrorRespObject("登录信息为空，请重新登录", e.getMessage());
        } catch (WMSException e) {
            return new ErrorRespObject("修改损溢单失败", e.getMessage());
        }
        return resp;

    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public @ResponseBody RespObject remove(
            @RequestParam(value = "token", required = true) String token,
            @RequestParam(value = "uuid", required = true) String uuid,
            @RequestParam(value = "version", required = true) long version) {
        RespObject resp = new RespObject();
        try {
            ApplicationContextUtil.setCompany(getLoginCompany(token));
            service.remove(uuid, version);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (WMSException e) {
            return new ErrorRespObject("删除损溢单失败！", e.getMessage());
        }
        return resp;

    }

    @RequestMapping(value = "/audit", method = RequestMethod.PUT)
    public @ResponseBody RespObject audit(
            @RequestParam(value = "token", required = true) String token,
            @RequestParam(value = "uuid", required = true) String uuid,
            @RequestParam(value = "version", required = true) long version) {
        RespObject resp = new RespObject();
        try {
            ApplicationContextUtil.setCompany(getLoginCompany(token));
            ApplicationContextUtil.setOperateContext(getOperateContext(token));
            service.audit(uuid, version);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (Exception e) {
            return new ErrorRespObject("审核损溢单失败！", e.getMessage());
        }
        return resp;

    }

    @RequestMapping(value = "/queryStockExtendInfo", method = RequestMethod.GET)
    public @ResponseBody RespObject queryStockExtendInfo(
            @RequestParam(value = "token", required = true) String token,
            @RequestParam(value = "binCode", required = false) String binCode,
            @RequestParam(value = "containerBarcode", required = false) String containerBarcode,
            @RequestParam(value = "articleUuid", required = false) String articleUuid,
            @RequestParam(value = "qpcStr", required = false) String qpcStr) {
        RespObject resp = new RespObject();
        try {
            StockFilter filter = new StockFilter();
            filter.setArticleUuid(articleUuid);
            filter.setBinCode(binCode);
            filter.setCompanyUuid(getLoginCompany(token).getUuid());
            filter.setContainerBarcode(containerBarcode);
            filter.setQpcStr(qpcStr);
            filter.setPageSize(0);
            List<StockExtendInfo> stocks = stockService.queryStocks(filter);

            Set<UCN> suppliers = new HashSet<>();
            for (StockExtendInfo info : stocks) {
                suppliers.add(info.getSupplier());
            }
            resp.setObj(suppliers);
            resp.setStatus(RespStatus.HTTP_STATUS_SUCCESS);
        } catch (NotLoginInfoException e) {
            return new ErrorRespObject("登录信息为空，请重新登录", e.getMessage());
        } catch (Exception e) {
            return new ErrorRespObject("查询库存信息失败", e.getMessage());
        }

        return resp;

    }

}

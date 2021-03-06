/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-service
 * 文件名：	PutawayServiceImpl.java
 * 模块说明：	
 * 修改历史：
 * 2017年6月20日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.service.in.putaway;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.article.Article;
import com.hd123.sardine.wms.api.basicInfo.article.ArticlePutawayBin;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleService;
import com.hd123.sardine.wms.api.basicInfo.bin.Bin;
import com.hd123.sardine.wms.api.basicInfo.bin.BinService;
import com.hd123.sardine.wms.api.basicInfo.bin.BinState;
import com.hd123.sardine.wms.api.basicInfo.bin.BinUsage;
import com.hd123.sardine.wms.api.basicInfo.category.Category;
import com.hd123.sardine.wms.api.basicInfo.category.CategoryService;
import com.hd123.sardine.wms.api.basicInfo.config.articleconfig.ArticleConfig;
import com.hd123.sardine.wms.api.basicInfo.config.articleconfig.ArticleConfigService;
import com.hd123.sardine.wms.api.basicInfo.config.articleconfig.PickBinStockLimit;
import com.hd123.sardine.wms.api.basicInfo.config.categorystorageareaconfig.CategoryStorageAreaConfig;
import com.hd123.sardine.wms.api.basicInfo.config.categorystorageareaconfig.CategoryStorageAreaConfigService;
import com.hd123.sardine.wms.api.basicInfo.config.pickareastorageareaconfig.PickAreaStorageAreaConfigService;
import com.hd123.sardine.wms.api.basicInfo.pickarea.PickArea;
import com.hd123.sardine.wms.api.basicInfo.pickarea.PickAreaService;
import com.hd123.sardine.wms.api.basicInfo.supplier.Supplier;
import com.hd123.sardine.wms.api.basicInfo.supplier.SupplierService;
import com.hd123.sardine.wms.api.in.putaway.PutawayService;
import com.hd123.sardine.wms.api.stock.Stock;
import com.hd123.sardine.wms.api.stock.StockFilter;
import com.hd123.sardine.wms.api.stock.StockService;
import com.hd123.sardine.wms.common.exception.WMSException;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;

/**
 * 上架算法实现
 * 
 * @author zhangsai
 *
 */
public class PutawayServiceImpl implements PutawayService {

  @Autowired
  private ArticleConfigService articleConfigService;

  @Autowired
  private ArticleService articleService;

  @Autowired
  private StockService stockService;

  @Autowired
  private BinService BinService;

  @Autowired
  private CategoryStorageAreaConfigService categoryStorageAreaConfigService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private PickAreaStorageAreaConfigService pickAreaStorageAreaConfigService;

  @Autowired
  private SupplierService supplierService;

  @Autowired
  private PickAreaService pickAreaService;

  @Override
  public String fetchPutawayTargetBinByArticle(String articleUuid, BigDecimal qty)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "待上架的商品");
    Assert.assertArgumentNotNull(qty, "上架商品的数量");

    if (BigDecimal.ZERO.compareTo(qty) >= 0)
      return null;

    Article article = articleService.get(articleUuid);
    if (article == null)
      throw new WMSException("商品id不正确。找不到待上架的商品！");
    if (ArticlePutawayBin.StorageBin.equals(article.getPutawayBin()))
      return getTargetBinWhenStorageBin(articleUuid);
    if (ArticlePutawayBin.PickUpBin.equals(article.getPutawayBin()))
      return getTargetBinWhenPickupBin(articleUuid);

    return getTargetBinWhenFirstPickupBin(articleUuid, qty);
  }

  @Override
  public String fetchPutawayTargetBinByContainer(String containerBarcode)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(containerBarcode, "待上架的容器");

    StockFilter stockFilter = new StockFilter();
    stockFilter.setContainerBarcode(containerBarcode);
    stockFilter.setCompanyUuid(ApplicationContextUtil.getCompanyUuid());
    stockFilter.setPageSize(0);
    List<Stock> stocks = stockService.query(stockFilter);
    if (stocks.isEmpty())
      return null;

    return fetchPutawayTargetBinByArticle(stocks.get(0).getStockComponent().getArticle().getUuid(),
        stocks.get(0).getStockComponent().getQty());
  }

  @Override
  public void verifyNewTargetBin(String newTargetBinCode, String articleUuid)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(newTargetBinCode, "新上架货位");

    Bin newTargetBin = BinService.getBinByCode(newTargetBinCode);
    if (newTargetBin == null)
      throw new WMSException("新上架货位不存在！");

    if (BinUsage.StorageBin.equals(newTargetBin.getUsage()) == false
        && BinUsage.PickUpStorageBin.equals(newTargetBin.getUsage()) == false)
      throw new WMSException("只有存储位和拣货存储位才能作为上架的目标货位！");
  }

  private String getTargetBinWhenStorageBin(String articleUuid) throws WMSException {
    assert articleUuid != null;

    String targetBinArea = null;
    ArticleConfig articleConfig = articleConfigService.getArticleConfig(articleUuid);
    if (articleConfig != null)
      targetBinArea = articleConfig.getStorageArea();
    String currentCategoryUuid = null;
    if (StringUtil.isNullOrBlank(targetBinArea)) {
      Article article = articleService.get(articleUuid);
      if (article == null) {
        throw new WMSException("上架商品不存在！");
      }
      currentCategoryUuid = article.getCategory().getUuid();
      CategoryStorageAreaConfig categorycConfig = categoryStorageAreaConfigService
          .getCategoryStorageAreaConfig(currentCategoryUuid);
      if (categorycConfig != null)
        targetBinArea = categorycConfig.getStorageArea();
    }

    if (StringUtil.isNullOrBlank(targetBinArea)) {
      String parentCategoryUuid = categoryService.getParentCategoryUuid(currentCategoryUuid);
      while (StringUtil.isNullOrBlank(parentCategoryUuid) == false
          && Category.DEFAULT_ROOTCATEGORY.equals(parentCategoryUuid) == false) {
        CategoryStorageAreaConfig categorycConfig = categoryStorageAreaConfigService
            .getCategoryStorageAreaConfig(parentCategoryUuid);
        if (categorycConfig != null)
          targetBinArea = categorycConfig.getStorageArea();
        if (StringUtil.isNullOrBlank(targetBinArea) == false)
          break;
        parentCategoryUuid = categoryService.getParentCategoryUuid(parentCategoryUuid);
      }
    }

    if (StringUtil.isNullOrBlank(targetBinArea) && articleConfig != null) {
      targetBinArea = pickAreaStorageAreaConfigService
          .getStorageAreaByFixedPickBin(articleConfig.getFixedPickBin());
    }

    if (StringUtil.isNullOrBlank(targetBinArea))
      return null;

    List<String> targetBinCodes = BinService.queryBinByScopeAndUsage(targetBinArea,
        BinUsage.StorageBin, BinState.free);
    return allocateAwayFromFixedBin(targetBinCodes,
        articleConfig == null ? null : articleConfig.getFixedPickBin());
  }

  private String allocateAwayFromFixedBin(List<String> targetBinCodes, String fixedBinCode) {
    assert targetBinCodes != null;

    if (targetBinCodes.isEmpty())
      return null;
    if (StringUtil.isNullOrBlank(fixedBinCode))
      return targetBinCodes.get(0);

    String targetBincode = null;
    long distance = 99999999;
    for (String bincode : targetBinCodes) {
      long newDistance = Long.valueOf(bincode) - Long.valueOf(fixedBinCode);
      if (newDistance < 0)
        newDistance = -newDistance;
      if (newDistance < distance) {
        targetBincode = bincode;
        distance = newDistance;
      }
    }
    return targetBincode;
  }

  private String getTargetBinWhenPickupBin(String articleUuid) {
    assert articleUuid != null;

    ArticleConfig articleConfig = articleConfigService.getArticleConfig(articleUuid);
    if (articleConfig != null)
      return articleConfig.getFixedPickBin();
    return null;
  }

  private String getTargetBinWhenFirstPickupBin(String articleUuid, BigDecimal qty)
      throws WMSException {
    assert articleUuid != null;
    assert qty != null;

    PickBinStockLimit stockLimit = null;
    ArticleConfig articleConfig = articleConfigService.getArticleConfig(articleUuid);
    if (articleConfig != null) {
      if (StringUtil.isNullOrBlank(articleConfig.getFixedPickBin()))
        return getTargetBinWhenStorageBin(articleUuid);
      stockLimit = articleConfig.getPickBinStockLimit();
    } else {
      return getTargetBinWhenStorageBin(articleUuid);
    }
    BigDecimal highQty = stockLimit == null ? BigDecimal.ZERO : stockLimit.getHighQty();
    if (highQty == null || BigDecimal.ZERO.compareTo(highQty) >= 0)
      return getTargetBinWhenStorageBin(articleUuid);

    StockFilter stockFilter = new StockFilter();
    stockFilter.setCompanyUuid(ApplicationContextUtil.getCompanyUuid());
    stockFilter.setArticleUuid(articleUuid);
    stockFilter.setBinCode(articleConfig.getFixedPickBin());
    List<Stock> stocks = stockService.query(stockFilter);
    if (allocateTotalQty(stocks).add(qty).compareTo(highQty) > 0)
      return getTargetBinWhenStorageBin(articleUuid);
    return articleConfig.getFixedPickBin();
  }

  private BigDecimal allocateTotalQty(List<Stock> stocks) {
    assert stocks != null;

    BigDecimal totalQty = BigDecimal.ZERO;
    for (Stock stock : stocks) {
      totalQty = totalQty.add(stock.getStockComponent().getQty());
    }
    return totalQty;
  }

  @Override
  public String fetchRtnPutawayTargetBinBySupplier(String supplierUuid)
      throws IllegalArgumentException, WMSException {
    if (StringUtil.isNullOrBlank(supplierUuid))
      return null;
    Supplier supplier = supplierService.get(supplierUuid);
    if (Objects.isNull(supplier))
      throw new WMSException(MessageFormat.format("供应商{0}不存在", supplierUuid));
    if (StringUtil.isNullOrBlank(supplier.getStorageArea()))
      return null;
    StockFilter filter = new StockFilter();
    filter.setSupplierUuid(supplierUuid);
    filter.setCompanyUuid(ApplicationContextUtil.getCompanyUuid());
    filter.setPageSize(0);
    PickArea pickArea = pickAreaService.getByStorageArea(supplier.getStorageArea());
    if (Objects.isNull(pickArea) || StringUtil.isNullOrBlank(pickArea.getBinScope()))
      return null;
    List<BinState> binStates = new ArrayList<>();
    binStates.add(BinState.free);
    binStates.add(BinState.using);
    List<String> binCodes = BinService.queryBinByScopeAndUsageAndStates(pickArea.getBinScope(),
        BinUsage.SupplierStorageBin, null);
    List<String> resultBinCodes = new ArrayList<>();
    List<Stock> list = stockService.query(filter);
    if (CollectionUtils.isEmpty(list) == false) {
      for (Stock stock : list) {
        String binCode = stock.getStockComponent().getBinCode();
        if (binCodes.contains(binCode)) {
          resultBinCodes.add(binCode);
          continue;
        }
      }
      if (CollectionUtils.isEmpty(resultBinCodes))
        resultBinCodes = binCodes;
    } else {
      resultBinCodes = binCodes;
    }
    if (CollectionUtils.isEmpty(resultBinCodes))
      return null;
    resultBinCodes.sort(new Comparator<String>() {

      @Override
      public int compare(String o1, String o2) {
        return Integer.parseInt(o1) - Integer.parseInt(o2);
      }
    });

    return resultBinCodes.get(0);
  }

}

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-service
 * 文件名：	ArticleServiceImpl.java
 * 模块说明：	
 * 修改历史：
 * 2017年2月7日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.service.basicInfo.article;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.article.Article;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleBarcode;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleQpc;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleService;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleState;
import com.hd123.sardine.wms.api.basicInfo.article.ArticleSupplier;
import com.hd123.sardine.wms.api.basicInfo.bin.Bin;
import com.hd123.sardine.wms.api.basicInfo.bin.BinService;
import com.hd123.sardine.wms.api.basicInfo.bin.BinUsage;
import com.hd123.sardine.wms.api.basicInfo.category.Category;
import com.hd123.sardine.wms.api.basicInfo.category.CategoryService;
import com.hd123.sardine.wms.api.basicInfo.config.articleconfig.ArticleConfig;
import com.hd123.sardine.wms.api.basicInfo.config.articleconfig.ArticleConfigService;
import com.hd123.sardine.wms.api.basicInfo.supplier.Supplier;
import com.hd123.sardine.wms.api.basicInfo.supplier.SupplierService;
import com.hd123.sardine.wms.common.entity.UCN;
import com.hd123.sardine.wms.common.exception.WMSException;
import com.hd123.sardine.wms.common.query.PageQueryDefinition;
import com.hd123.sardine.wms.common.query.PageQueryResult;
import com.hd123.sardine.wms.common.query.PageQueryUtil;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;
import com.hd123.sardine.wms.common.utils.PersistenceUtils;
import com.hd123.sardine.wms.common.utils.UUIDGenerator;
import com.hd123.sardine.wms.common.validator.ValidateHandler;
import com.hd123.sardine.wms.common.validator.ValidateResult;
import com.hd123.sardine.wms.dao.basicInfo.article.ArticleBarcodeDao;
import com.hd123.sardine.wms.dao.basicInfo.article.ArticleDao;
import com.hd123.sardine.wms.dao.basicInfo.article.ArticleFixedPickBinDao;
import com.hd123.sardine.wms.dao.basicInfo.article.ArticleQpcDao;
import com.hd123.sardine.wms.dao.basicInfo.article.ArticleSupplierDao;
import com.hd123.sardine.wms.service.basicInfo.article.validator.ArticleInsertValidateHandler;
import com.hd123.sardine.wms.service.basicInfo.article.validator.ArticleUpdateValidateHandler;
import com.hd123.sardine.wms.service.ia.BaseWMSService;
import com.hd123.sardine.wms.service.log.EntityLogger;

/**
 * @author zhangsai
 *
 */
public class ArticleServiceImpl extends BaseWMSService implements ArticleService {

  @Autowired
  private ArticleDao articleDao;

  @Autowired
  private ArticleQpcDao articleQpcDao;

  @Autowired
  private ArticleBarcodeDao articleBarcodeDao;

  @Autowired
  private ArticleSupplierDao articleSupplierDao;

  @Autowired
  private ValidateHandler<Article> articleInsertValidateHandler;

  @Autowired
  private ValidateHandler<Article> articleUpdateValidateHandler;

  @Autowired
  private ValidateHandler<ArticleQpc> articleQpcInsertValidateHandler;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private SupplierService supplierService;

  @Autowired
  private BinService binService;

  @Autowired
  private ArticleConfigService articleConfigService;

  @Autowired
  private ArticleFixedPickBinDao articleFixedPickBinDao;

  @Autowired
  private EntityLogger logger;

  @Override
  public String insert(Article article) throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(article, "article");

    Article existsArticle = articleDao.getByCode(article.getCode());
    Category category = categoryService
        .get(article.getCategory() == null ? null : article.getCategory().getUuid());

    ValidateResult insertResult = articleInsertValidateHandler
        .putAttribute(ArticleInsertValidateHandler.KEY_CODEEXISTS_ARTICLE, existsArticle)
        .putAttribute(ArticleInsertValidateHandler.KEY_CATEGORY, category).validate(article);
    checkValidateResult(insertResult);

    article.setCompanyUuid(ApplicationContextUtil.getParentCompanyUuid());
    article.setUuid(article.getCompanyUuid() + article.getCode());
    article.setCreateInfo(ApplicationContextUtil.getOperateInfo());
    article.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());
    articleDao.insert(article);

    saveArticleConfig(article);

    saveArticleQpcAndBarcode(article);// 新增商品时默认添加商品条码=商品代码，规格为1*1*1

    logger.injectContext(this, article.getUuid(), Article.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_ADDNEW, "新增商品");
    return article.getUuid();
  }

  private void saveArticleQpcAndBarcode(Article article) {
    assert article != null;
    ArticleQpc qpc = new ArticleQpc();
    qpc.setArticleUuid(article.getUuid());
    qpc.setDefault_(true);
    qpc.setQpcStr(ArticleQpc.DEFAULT_QPCSTR);
    qpc.setUuid(UUIDGenerator.genUUID());

    ArticleBarcode barcode = new ArticleBarcode();
    barcode.setArticleUuid(article.getUuid());
    barcode.setBarcode(article.getCode());
    barcode.setQpcStr(ArticleQpc.DEFAULT_QPCSTR);
    barcode.setUuid(UUIDGenerator.genUUID());
    articleQpcDao.insert(qpc);
    articleBarcodeDao.insert(barcode);
  }

  @Override
  public void update(Article article) throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(article, "article");

    Article existsArticle = articleDao.getByCode(article.getCode());
    Article updateArticle = articleDao.get(article.getUuid());
    Category category = categoryService
        .get(article.getCategory() == null ? null : article.getCategory().getUuid());

    ValidateResult insertResult = articleUpdateValidateHandler
        .putAttribute(ArticleUpdateValidateHandler.KEY_CODEEXISTS_ARTICLE, existsArticle)
        .putAttribute(ArticleUpdateValidateHandler.KEY_UPDATE_ARTICLE, updateArticle)
        .putAttribute(ArticleUpdateValidateHandler.KEY_CATEGORY, category).validate(article);
    checkValidateResult(insertResult);

    article.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());
    articleDao.update(article);

    saveArticleConfig(article);

    logger.injectContext(this, article.getUuid(), Article.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "修改商品");
  }

  private void saveArticleConfig(Article article) throws WMSException {
    ArticleConfig articleConfig = new ArticleConfig();
    articleConfig.setArticle(new UCN(article.getUuid(), article.getCode(), article.getName()));
    if (StringUtil.isNullOrBlank(article.getFixedPickBin()) == false) {
      Bin pickUpBin = binService.getBinByCode(article.getFixedPickBin());
      if (Objects.isNull(pickUpBin))
        throw new WMSException("固定拣货位" + article.getFixedPickBin() + "不存在");
    }
    articleConfig.setPutawayBin(article.getPutawayBin());
    articleConfig.setStorageArea(article.getStorageArea());
    articleConfigService.saveArticleConfig(articleConfig);
  }

  @Override
  public Article get(String uuid) {
    Article article = articleDao.get(uuid);
    if (article != null) {
      article.setQpcs(articleQpcDao.queryByList(uuid));
      article.setArticleSuppliers(articleSupplierDao.queryByList(uuid));
      article.setBarcodes(articleBarcodeDao.queryByList(uuid));
      ArticleConfig articleConfig = articleConfigService.getArticleConfig(uuid);
      if (articleConfig != null) {
        article.setFixedPickBin(articleConfig.getFixedPickBin());
        article.setPutawayBin(articleConfig.getPutawayBin());
        article.setStorageArea(articleConfig.getStorageArea());
      }
    }
    return article;
  }

  @Override
  public Article getByCode(String code) {
    Article article = articleDao.getByCode(code);
    if (article != null) {
      article.setQpcs(articleQpcDao.queryByList(article.getUuid()));
      article.setArticleSuppliers(articleSupplierDao.queryByList(article.getUuid()));
      article.setBarcodes(articleBarcodeDao.queryByList(article.getUuid()));
      ArticleConfig articleConfig = articleConfigService.getArticleConfig(article.getUuid());
      if (articleConfig != null) {
        article.setFixedPickBin(articleConfig.getFixedPickBin());
        article.setPutawayBin(articleConfig.getPutawayBin());
        article.setStorageArea(articleConfig.getStorageArea());
      }
    }
    return article;
  }

  @Override
  public Article getByBarcode(String barcode) {
    Article article = articleDao.getByBarcode(barcode);
    if (article != null) {
      article.setQpcs(articleQpcDao.queryByList(article.getUuid()));
      article.setArticleSuppliers(articleSupplierDao.queryByList(article.getUuid()));
      article.setBarcodes(articleBarcodeDao.queryByList(article.getUuid()));
      ArticleConfig articleConfig = articleConfigService.getArticleConfig(article.getUuid());
      if (articleConfig != null) {
        article.setFixedPickBin(articleConfig.getFixedPickBin());
        article.setPutawayBin(articleConfig.getPutawayBin());
        article.setStorageArea(articleConfig.getStorageArea());
      }
    }
    return article;
  }

  @Override
  public PageQueryResult<Article> query(PageQueryDefinition definition)
      throws IllegalArgumentException {
    Assert.assertArgumentNotNull(definition, "definition");

    PageQueryResult<Article> pgr = new PageQueryResult<Article>();
    List<Article> list = articleDao.query(definition);
    PageQueryUtil.assignPageInfo(pgr, definition);
    pgr.setRecords(list);
    return pgr;
  }

  @Override
  public void insertArticleQpc(ArticleQpc qpc) throws IllegalArgumentException, WMSException {
    ValidateResult insertResult = articleQpcInsertValidateHandler.validate(qpc);
    checkValidateResult(insertResult);

    Article article = articleDao.get(qpc.getArticleUuid());
    if (article == null)
      throw new WMSException("商品" + qpc.getArticleUuid() + "不存在。");

    List<ArticleQpc> qpcs = articleQpcDao.queryByList(qpc.getArticleUuid());
    ArticleQpc oldQpc = null;
    for (ArticleQpc qpcc : qpcs) {
      if (qpcc.getUuid().equals(qpc.getUuid()))
        oldQpc = qpcc;
      else {
        if (qpcc.getQpcStr().equals(qpc.getQpcStr()))
          throw new WMSException("同一商品不能存在相同的规格。");
      }
    }
    if (oldQpc == null) {
      articleQpcDao.insert(qpc);
    } else {
      articleQpcDao.remove(qpc.getUuid());
      articleQpcDao.insert(qpc);
    }

    logger.injectContext(this, qpc.getUuid(), ArticleQpc.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_ADDNEW, "新增商品规格");
  }

  @Override
  public void insertArticleBarcode(ArticleBarcode barcode)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(barcode, "barcode");

    ArticleQpc qpc = articleQpcDao.getByQpcStr(barcode.getArticleUuid(), barcode.getQpcStr());
    if (qpc == null)
      throw new WMSException("规格" + barcode.getQpcStr() + "不存在。");
    Article article = articleDao.get(barcode.getArticleUuid());
    if (article == null)
      throw new WMSException("商品" + barcode.getArticleUuid() + "不存在。");
    Article articleByBarcode = articleDao.getByBarcode(barcode.getBarcode());
    if (articleByBarcode != null
        && articleByBarcode.getUuid().equals(barcode.getArticleUuid()) == false)
      throw new WMSException("商品条码" + barcode.getBarcode() + "已被其他商品占用。");

    List<ArticleBarcode> barcodes = articleBarcodeDao.queryByList(barcode.getArticleUuid());
    ArticleBarcode oldAb = null;
    for (ArticleBarcode ab : barcodes) {
      if (ab.getUuid().equals(barcode.getUuid()))
        oldAb = ab;
      else {
        if (ab.getBarcode().equals(barcode.getBarcode()))
          throw new WMSException("同一商品不能存在相同的规格。");
      }
    }
    if (oldAb == null) {
      articleBarcodeDao.insert(barcode);
    } else {
      articleBarcodeDao.remove(barcode.getUuid());
      articleBarcodeDao.insert(barcode);
    }

    logger.injectContext(this, barcode.getUuid(), ArticleBarcode.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_ADDNEW, "新增商品规格");
  }

  @Override
  public void insertArticleSupplier(ArticleSupplier supplier)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(supplier, "supplier");

    Supplier sup = supplierService.get(supplier.getSupplierUuid());
    if (sup == null)
      throw new WMSException("供应商" + supplier.getSupplierCode() + "不存在。");
    Article article = articleDao.get(supplier.getArticleUuid());
    if (article == null)
      throw new WMSException("商品" + supplier.getArticleUuid() + "不存在。");

    List<ArticleSupplier> ass = articleSupplierDao.queryByList(supplier.getArticleUuid());
    ArticleSupplier as = null;
    for (ArticleSupplier aas : ass) {
      if (aas.getUuid().equals(supplier.getUuid()))
        as = aas;
      else if (aas.getSupplierUuid().equals(supplier.getSupplierUuid()))
        throw new WMSException("同一商品不能存在相同的供应商。");
    }

    if (as == null) {
      as = new ArticleSupplier();
      as.setUuid(supplier.getUuid());
      as.setArticleUuid(supplier.getArticleUuid());
      as.setSupplierUuid(sup.getUuid());
      as.setSupplierCode(sup.getCode());
      as.setSupplierName(sup.getName());
      as.setDefault_(false);
      articleSupplierDao.insert(as);
    } else {
      as.setSupplierUuid(sup.getUuid());
      as.setSupplierCode(sup.getCode());
      as.setSupplierName(sup.getName());
      as.setDefault_(false);
      articleSupplierDao.remove(supplier.getUuid());
      articleSupplierDao.insert(as);
    }

    logger.injectContext(this, supplier.getUuid(), ArticleSupplier.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_ADDNEW, "新增商品供应商");
  }

  @Override
  public void deleteArticleQpc(String articleUuid, String qpcUuid)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "articleUuid");
    Assert.assertArgumentNotNull(qpcUuid, "qpcUuid");

    Article article = articleDao.get(articleUuid);
    if (article == null)
      throw new WMSException("商品" + articleUuid + "不存在。");
    ArticleQpc articleQpc = articleQpcDao.get(qpcUuid);
    if (Objects.isNull(articleQpc))
      throw new WMSException("商品规格不存在");
    if (articleQpc.isDefault_())
      throw new WMSException("默认规格不允许删除");
    ArticleBarcode barcode = articleBarcodeDao.getByArticleAndQpc(articleUuid,
        articleQpc.getQpcStr());
    if (Objects.nonNull(barcode))
      throw new WMSException(MessageFormat.format("存在条码{0}与规格{1}对应，请先删除条码{2}", barcode,
          articleQpc.getQpcStr(), barcode));
    articleQpcDao.remove(qpcUuid);

    logger.injectContext(this, qpcUuid, ArticleQpc.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_REMOVE, "删除商品规格");
  }

  @Override
  public void deleteArticleBarcode(String articleUuid, String barcodeUuid)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "articleUuid");
    Assert.assertArgumentNotNull(barcodeUuid, "barcodeUuid");

    Article article = articleDao.get(articleUuid);
    if (article == null)
      throw new WMSException("商品" + articleUuid + "不存在。");
    ArticleBarcode barcode = articleBarcodeDao.get(barcodeUuid);
    if (Objects.isNull(barcode))
      throw new WMSException("要删除的商品条码不存在");
    articleBarcodeDao.remove(barcodeUuid);

    logger.injectContext(this, barcodeUuid, ArticleBarcode.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_REMOVE, "删除商品条码");
  }

  @Override
  public void deleteArticleSupplier(String articleUuid, String articleSupplierUuid)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "articleUuid");
    Assert.assertArgumentNotNull(articleSupplierUuid, "articleSupplierUuid");

    Article article = articleDao.get(articleUuid);
    if (article == null)
      throw new WMSException("商品" + articleUuid + "不存在。");
    articleSupplierDao.remove(articleSupplierUuid);

    logger.injectContext(this, articleSupplierUuid, ArticleSupplier.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_REMOVE, "删除商品供应商");
  }

  @Override
  public void setDefaultArticleSupplier(String articleUuid, String articleSupplierUuid)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "articleUuid");
    Assert.assertArgumentNotNull(articleSupplierUuid, "articleSupplierUuid");

    Article article = articleDao.get(articleUuid);
    if (article == null)
      throw new WMSException("商品" + articleUuid + "不存在。");
    articleSupplierDao.setUnDefault(articleUuid);
    articleSupplierDao.setDefault(articleSupplierUuid);

    logger.injectContext(this, articleUuid, Article.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "设置商品默认供应商");
  }

  @Override
  public void setDefaultArticleQpc(String articleUuid, String qpcUuid)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "articleUuid");
    Assert.assertArgumentNotNull(qpcUuid, "qpcUuid");

    Article article = articleDao.get(articleUuid);
    if (article == null)
      throw new WMSException("商品" + articleUuid + "不存在。");
    articleQpcDao.setUnDefault(articleUuid);
    articleQpcDao.setDefault(qpcUuid);

    logger.injectContext(this, articleUuid, Article.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "设置商品默认规格");
  }

  @Override
  public PageQueryResult<UCN> queryInStocks(PageQueryDefinition definition)
      throws IllegalArgumentException {
    Assert.assertArgumentNotNull(definition, "definition");
    PageQueryResult<UCN> pgr = new PageQueryResult<>();
    List<UCN> list = articleDao.queryInStocks(definition);
    PageQueryUtil.assignPageInfo(pgr, definition);
    pgr.setRecords(list);
    return pgr;
  }

  @Override
  public void updateArticleFixedPickBin(String articleUuid, String fixedPickBin)
      throws IllegalArgumentException, WMSException {
    Assert.assertArgumentNotNull(articleUuid, "articleUuid");
    Assert.assertArgumentNotNull(fixedPickBin, "fixedPickBin");

    verifyArticleFixedPickBin(fixedPickBin);
    articleFixedPickBinDao.removeByArticleCompany(articleUuid);
    if (StringUtil.isNullOrBlank(fixedPickBin))
      return;
    articleFixedPickBinDao.insert(articleUuid, fixedPickBin);

    logger.injectContext(this, articleUuid, Article.class.getName(),
        ApplicationContextUtil.getOperateContext());
    logger.log(EntityLogger.EVENT_MODIFY, "设置商品固定拣货位");
  }

  private void verifyArticleFixedPickBin(String fixedPickBin) throws WMSException {
    if (StringUtil.isNullOrBlank(fixedPickBin))
      return;
    Bin bin = binService.getBinByCode(fixedPickBin);
    if (bin == null)
      throw new WMSException("商品固定拣货位" + fixedPickBin + "不存在");
    if (BinUsage.PickUpStorageBin.equals(bin.getUsage()) == false)
      throw new WMSException("商品固定拣货位必须是" + BinUsage.PickUpStorageBin.getCaption());
  }

  @Override
  public List<ArticleQpc> queryArticleQpcs(String articleUuid) throws IllegalArgumentException {
    if (StringUtil.isNullOrBlank(articleUuid))
      return new ArrayList<ArticleQpc>();
    List<ArticleQpc> list = articleQpcDao.queryByList(articleUuid);
    return list;
  }

  @Override
  public List<Article> queryArticles(List<String> aticleUuids) {
    return articleDao.queryArticles(aticleUuids);
  }

  @Override
  public ArticleQpc getArticleQpcByArticleUuidAndQpcStr(String articleUuid, String qpcStr) {
    if (StringUtil.isNullOrBlank(articleUuid))
      return null;
    return articleQpcDao.getByQpcStr(articleUuid, qpcStr);
  }

  @Override
  public void online(String uuid, long version) throws WMSException {
    Assert.assertArgumentNotNull(uuid, "uuid");
    Assert.assertArgumentNotNull(version, "version");

    Article article = articleDao.get(uuid);
    if (Objects.isNull(article))
      throw new WMSException("要启用的商品不存在");
    PersistenceUtils.checkVersion(version, article, "商品", uuid);
    if (ArticleState.normal.equals(article.getState()))
      return;

    article.setState(ArticleState.normal);
    article.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());

    articleDao.update(article);
  }

  @Override
  public void offline(String uuid, long version) throws WMSException {
    Assert.assertArgumentNotNull(uuid, "uuid");
    Assert.assertArgumentNotNull(version, "version");

    Article article = articleDao.get(uuid);
    if (Objects.isNull(article))
      throw new WMSException("要停用的商品不存在");
    PersistenceUtils.checkVersion(version, article, "商品", uuid);
    if (ArticleState.offline.equals(article.getState()))
      return;

    article.setState(ArticleState.offline);
    article.setLastModifyInfo(ApplicationContextUtil.getOperateInfo());

    articleDao.update(article);
  }
}

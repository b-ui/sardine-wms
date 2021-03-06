/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-dao
 * 文件名：	WrhDaoImpl.java
 * 模块说明：	
 * 修改历史：
 * 2017年2月13日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.dao.basicInfo.bin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.bin.Bin;
import com.hd123.sardine.wms.api.basicInfo.bin.BinState;
import com.hd123.sardine.wms.api.basicInfo.bin.BinUsage;
import com.hd123.sardine.wms.common.dao.NameSpaceSupport;
import com.hd123.sardine.wms.common.query.PageQueryDefinition;
import com.hd123.sardine.wms.common.utils.ApplicationContextUtil;
import com.hd123.sardine.wms.common.utils.PersistenceUtils;
import com.hd123.sardine.wms.dao.basicInfo.bin.BinDao;

/**
 * 货位Dao实现类
 * 
 * @author zhangsai
 *
 */
public class BinDaoImpl extends NameSpaceSupport implements BinDao {

	private static final String GETBYCODE = "getByCode";
	private static final String GETBINBYWRHANDUSAGE = "getBinByWrhAndUsage";
	private static final String CHANGESTATE = "changeState";
	private static final String QUERYBINCODESBYSCOPE = "queryBincodesByScope";
	private static final String QUERYBINCODESBYSCOPEANDSTATES = "queryBinCodesBuScopeAndStates";
	private static final String QUERYBINBYUSAGEANDSTATE = "queryBinByUsageAndState";
	private static final String GETWRHBYBIN = "getWrhByBin";
	private static final String QUERYBINBYWRHANDSTATE = "queryBinByWrhAndState";
	private static final String GETBINCOUNTBYSCOPEANDWRH = "getBinCountByScopeAndWrh";
	private static final String QUERYBINBYBINSCOPEANDSTATE = "queryBinByBinScopeAndState";

	@Override
	public void insert(Bin bin) {
		getSqlSession().insert(generateStatement(MAPPER_INSERT), bin);
	}

	@Override
	public List<Bin> query(PageQueryDefinition definition) {
		return getSqlSession().selectList(generateStatement(MAPPER_QUERY_BYPAGE), definition);
	}

	@Override
	public void remove(String uuid, long version) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", uuid);
		map.put("version", version);
		int i = getSqlSession().delete(generateStatement(MAPPER_REMOVE), map);
		PersistenceUtils.optimisticVerify(i);
	}

	@Override
	public Bin getByCode(String companyUuid, String code) {
		if (StringUtil.isNullOrBlank(companyUuid) || StringUtil.isNullOrBlank(code))
			return null;

		Map<String, String> map = new HashMap<String, String>();
		map.put("companyUuid", companyUuid);
		map.put("code", code);
		return getSqlSession().selectOne(generateStatement(GETBYCODE), map);
	}

	@Override
	public Bin get(String uuid, String companyUuid) {
		if (StringUtil.isNullOrBlank(companyUuid) || StringUtil.isNullOrBlank(uuid))
			return null;

		Map<String, String> map = new HashMap<String, String>();
		map.put("companyUuid", companyUuid);
		map.put("uuid", uuid);
		return getSqlSession().selectOne(generateStatement(MAPPER_GET), map);
	}

	@Override
	public Bin getBinByWrhAndUsage(String wrhUuid, BinUsage usage) {
		Map<String, Object> map = ApplicationContextUtil.map();
		map.put("wrhUuid", wrhUuid);
		map.put("usage", usage);

		List<Bin> result = getSqlSession().selectList(generateStatement(GETBINBYWRHANDUSAGE), map);
		if (CollectionUtils.isEmpty(result))
			return null;
		return result.get(0);
	}

	@Override
	public void changeState(String uuid, long version, BinState state) {
		Map<String, Object> map = ApplicationContextUtil.map();
		map.put("uuid", uuid);
		map.put("version", version);
		map.put("state", state);

		int updateRows = getSqlSession().update(generateStatement(CHANGESTATE), map);
		PersistenceUtils.optimisticVerify(updateRows);
	}

	@Override
	public List<String> queryBincodesByScope(String sql, BinUsage binUsage, BinState state) {
		Map<String, Object> map = ApplicationContextUtil.map();
		map.put("sql", sql);
		if (binUsage != null)
			map.put("binUsage", binUsage.name());
		if (state != null)
			map.put("state", state.name());
		return selectList(QUERYBINCODESBYSCOPE, map);
	}

	@Override
	public List<String> queryBincodesByScope(String sql, BinUsage binUsage, List<BinState> states) {
		Map<String, Object> map = ApplicationContextUtil.map();
		map.put("sql", sql);
		if (binUsage != null)
			map.put("binUsage", binUsage.name());
		if (CollectionUtils.isEmpty(states) == false) {
			List<String> binStates = new ArrayList<>();
			for (BinState binState : states) {
				binStates.add(binState.name());
			}
			map.put("binStates", binStates);
		}
		return selectList(QUERYBINCODESBYSCOPEANDSTATES, map);
	}

	@Override
	public List<String> queryBinByUsageAndState(BinUsage usage, List<BinState> states) {
		Map<String, Object> map = ApplicationContextUtil.map();
		if (Objects.nonNull(usage))
			map.put("usage", usage.name());
		if (CollectionUtils.isEmpty(states) == false) {
			List<String> binStates = new ArrayList<>();
			for (BinState binState : states) {
				binStates.add(binState.name());
			}
			map.put("binStates", binStates);
		}
		return selectList(QUERYBINBYUSAGEANDSTATE, map);
	}

	@Override
	public List<Bin> queryBinByWrhAndState(String wrhUuid, BinState state) {
		Assert.assertArgumentNotNull(wrhUuid, "wrhUuid");
		Assert.assertArgumentNotNull(state, "state");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("wrhUuid", wrhUuid);
		map.put("state", state);
		return selectList(QUERYBINBYWRHANDSTATE, map);

	}

	@Override
	public List<Bin> queryBinByBinScopeAndState(String code, BinState state) {
		Assert.assertArgumentNotNull(code, "code");
		Assert.assertArgumentNotNull(state, "state");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("state", state);
		return selectList(QUERYBINBYBINSCOPEANDSTATE, map);
	}
}

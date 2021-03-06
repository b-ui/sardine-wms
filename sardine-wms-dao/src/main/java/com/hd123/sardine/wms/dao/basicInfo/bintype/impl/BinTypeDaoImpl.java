/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-dao
 * 文件名：	BinTypeDaoImpl.java
 * 模块说明：	
 * 修改历史：
 * 2017年2月6日 - yangwenzhu - 创建。
 */
package com.hd123.sardine.wms.dao.basicInfo.bintype.impl;

import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.sardine.wms.api.basicInfo.bintype.BinType;
import com.hd123.sardine.wms.common.dao.impl.BaseDaoImpl;
import com.hd123.sardine.wms.dao.basicInfo.bintype.BinTypeDao;

/**
 * @author yangwenzhu
 *
 */
public class BinTypeDaoImpl extends BaseDaoImpl<BinType> implements BinTypeDao {
    public static final String MAPPER_GETBYCODE = "getByCode";

    @Override
    public BinType getByCode(String code) {
        if (StringUtil.isNullOrBlank(code))
            return null;
        return getSqlSession().selectOne(generateStatement(MAPPER_GETBYCODE), code);
    }

}

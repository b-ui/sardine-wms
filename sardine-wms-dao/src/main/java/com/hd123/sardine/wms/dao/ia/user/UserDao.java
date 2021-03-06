/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	sardine-wms-dao
 * 文件名：	UserDao.java
 * 模块说明：	
 * 修改历史：
 * 2016年12月15日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.dao.ia.user;

import com.hd123.sardine.wms.api.ia.user.User;
import com.hd123.sardine.wms.common.dao.BaseDao;
import com.hd123.sardine.wms.common.entity.OperateContext;

/**
 * 用户dao层
 * 
 * @author zhangsai
 *
 */
public interface UserDao extends BaseDao<User> {

  User getByCode(String userCode);

  User login(String userCode, String passwd);

  int updatePasswd(String userUuid, String oldPasswd, String newPasswd, OperateContext operCtx);

  void saveUserRole(String userUuid, String roleUuid);

  void removeRolesByUser(String userUuid);
}

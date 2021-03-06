/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2017，所有权利保留。
 * 
 * 项目名：	sardine-wms-api
 * 文件名：	ResourceService.java
 * 模块说明：	
 * 修改历史：
 * 2017年3月23日 - zhangsai - 创建。
 */
package com.hd123.sardine.wms.api.ia.resource;

import java.util.List;
import java.util.Set;

import com.hd123.sardine.wms.common.utils.UserType;

/**
 * 资源服务：接口
 * 
 * @author zhangsai
 *
 */
public interface ResourceService {

    /**
     * 根据用户查询该用户拥有的菜单资源权限
     * <p>
     * 只查询当前用户拥有的权限
     * 
     * @param userUuid
     *            用户UUID，not nul
     * @param type
     *            资源类型，菜单或者操作，为空时查询全部
     * @return 资源操作集合，用树形结构展示
     * 
     * @throws IllegalArgumentException
     */
    List<Resource> queryOwnedMenuResourceByUser(String userUuid) throws IllegalArgumentException;

    /**
     * 根据用户查询所有资源权限，包括用户拥有和不拥有的
     * <p>
     * 拥有的{@link Resource#isOwned()} == true，否则{@link Resource#isOwned()} ==
     * false
     * 
     * @param userUuid
     *            用户UUID，not nul
     * @return 资源操作集合，用树形结构展示
     * 
     * @throws IllegalArgumentException
     */
    List<Resource> queryAllResourceByUser(String userUuid) throws IllegalArgumentException;

    /**
     * 根据角色查询所有资源权限，包括角色拥有和不拥有的
     * <p>
     * 拥有的{@link Resource#isOwned()} == true，否则{@link Resource#isOwned()} ==
     * false
     * 
     * @param roleUuid
     *            角色UUID，not nul
     * @return 资源操作集合，用树形结构展示
     * 
     * @throws IllegalArgumentException
     */
    List<Resource> queryAllResourceByRole(String roleUuid) throws IllegalArgumentException;

    /**
     * 查询用户拥有的资源，非树型
     * 
     * @param roleUuid
     * @return 资源集合
     * @throws IllegalArgumentException
     */
    List<Resource> queryOwnedResourceByUuser(String userUuid) throws IllegalArgumentException;

    /**
     * 查询角色拥有的资源，非树型
     * 
     * @param roleUuid
     * @return 资源集合
     * @throws IllegalArgumentException
     */
    List<Resource> queryOwnedResourceByRole(String roleUuid) throws IllegalArgumentException;

    /**
     * 保存用户资源权限
     * <p>
     * 用户拥有的权限=资源集合中每个资源权限和它的子资源权限
     * 
     * @param userUuid
     *            用户UUID，not null
     * @param resourceUuids
     *            资源权限UUID集合，可为空
     * @throws IllegalArgumentException
     */
    void saveUserResource(String userUuid, List<String> resourceUuids)
            throws IllegalArgumentException;

    /**
     * 保存角色资源权限
     * <p>
     * 角色拥有的权限=资源集合中每个资源权限和它的子资源权限
     * 
     * @param roleUuid
     *            角色UUID，not null
     * @param resourceUuids
     *            资源权限UUID集合，可为空
     * @throws IllegalArgumentException
     */
    void saveRoleResource(String roleUuid, List<String> resourceUuids)
            throws IllegalArgumentException;

    /**
     * 根据用户uuid删除用户资源对应关系
     * 
     * @param userUuid
     * @throws IllegalArgumentException
     */
    void removeResourceByUser(String userUuid) throws IllegalArgumentException;

    /**
     * 根据组织uuid删除角色资源对应关系
     * 
     * @param userUuid
     * @throws IllegalArgumentException
     */
    void removeResourceByRole(String roleUuid) throws IllegalArgumentException;

    /**
     * 根据父资源ID查询所有下级资源
     * 
     * @param upperUuid
     *            父资源ID，为空时返回空集合
     * @return 资源自合
     */
    List<Resource> queryByUpperResource(String upperUuid);

    /**
     * 查询用户拥有的所有操作权限，不包含菜单
     * 
     * @param userUuid
     *            用户ID，为空时返回空集合
     * @return 操作集合
     */
    List<Resource> queryOwnedOperateByUser(String userUuid);

    List<Resource> queryOwnedOperateByUserType(UserType userType);

    List<Resource> queryOwnedMenuByUserType(UserType userType);

    /**
     * 查询资源所有的上级资源
     * 
     * @param resourceUuid
     *            资源UUID，为空返回空集合
     * @return 上级资源UUID集合
     */
    Set<String> queryResourceAllParentResource(String resourceUuid);

}

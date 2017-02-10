// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.hd123.sardine.wms.service.test.basicinfo.containertype;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.sardine.wms.api.basicInfo.containertype.BarCodeType;
import com.hd123.sardine.wms.api.basicInfo.containertype.ContainerType;
import com.hd123.sardine.wms.common.entity.OperateInfo;
import com.hd123.sardine.wms.common.entity.Operator;
import com.hd123.sardine.wms.common.utils.POJORandomInitionlizer;

public class ContainerTypeBuilder extends ContainerTypeBuilderBase<ContainerTypeBuilder> {
    public static ContainerTypeBuilder containerType() {
        return new ContainerTypeBuilder();
    }

    public ContainerTypeBuilder() {
        super(new ContainerType());
    }

    public ContainerType build() {
        return getInstance();
    }
}

class ContainerTypeBuilderBase<GeneratorT extends ContainerTypeBuilderBase<GeneratorT>> {
    private ContainerType instance;

    protected ContainerTypeBuilderBase(ContainerType aInstance) {
        instance = aInstance;
        POJORandomInitionlizer.randomInit(instance);
    }

    protected ContainerType getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withCode(String aValue) {
        instance.setCode(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withName(String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withBarCodePrefix(String aValue) {
        instance.setBarCodePrefix(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withBarCodeLength(int aValue) {
        instance.setBarCodeLength(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withInLength(BigDecimal aValue) {
        instance.setInLength(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withInWidth(BigDecimal aValue) {
        instance.setInWidth(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withInHeight(BigDecimal aValue) {
        instance.setInHeight(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withOutLength(BigDecimal aValue) {
        instance.setOutLength(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withOutWidth(BigDecimal aValue) {
        instance.setOutWidth(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withOutHeight(BigDecimal aValue) {
        instance.setOutHeight(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withWeight(BigDecimal aValue) {
        instance.setWeight(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withBearingWeight(BigDecimal aValue) {
        instance.setBearingWeight(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withShip(boolean aValue) {
        instance.setShip(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withBarCodeType(BarCodeType aValue) {
        instance.setBarCodeType(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withRate(BigDecimal aValue) {
        instance.setRate(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withCompanyUuid(String aValue) {
        instance.setCompanyUuid(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withCreateInfo(OperateInfo aValue) {
        instance.setCreateInfo(aValue);

        return (GeneratorT) this;
    }

    public CreateInfoOperateInfoBuilder withCreateInfo() {
        OperateInfo obj = new OperateInfo();

        withCreateInfo(obj);

        return new CreateInfoOperateInfoBuilder(obj);
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withLastModifyInfo(OperateInfo aValue) {
        instance.setLastModifyInfo(aValue);

        return (GeneratorT) this;
    }

    public LastModifyInfoOperateInfoBuilder withLastModifyInfo() {
        OperateInfo obj = new OperateInfo();

        withLastModifyInfo(obj);

        return new LastModifyInfoOperateInfoBuilder(obj);
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withVersion(long aValue) throws UnsupportedOperationException {
        instance.setVersion(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withToken(String aValue) {
        instance.setToken(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withUuid(String aValue) throws UnsupportedOperationException {
        instance.setUuid(aValue);

        return (GeneratorT) this;
    }

    public class CreateInfoOperateInfoBuilder
            extends OperateInfoBuilderBase<CreateInfoOperateInfoBuilder> {
        public CreateInfoOperateInfoBuilder(OperateInfo aInstance) {
            super(aInstance);
        }

        @SuppressWarnings("unchecked")
        public GeneratorT endCreateInfo() {
            return (GeneratorT) ContainerTypeBuilderBase.this;
        }
    }

    public class LastModifyInfoOperateInfoBuilder
            extends OperateInfoBuilderBase<LastModifyInfoOperateInfoBuilder> {
        public LastModifyInfoOperateInfoBuilder(OperateInfo aInstance) {
            super(aInstance);
        }

        @SuppressWarnings("unchecked")
        public GeneratorT endLastModifyInfo() {
            return (GeneratorT) ContainerTypeBuilderBase.this;
        }
    }

    public static class OperateInfoBuilderBase<GeneratorT extends OperateInfoBuilderBase<GeneratorT>> {
        private OperateInfo instance;

        protected OperateInfoBuilderBase(OperateInfo aInstance) {
            instance = aInstance;
        }

        protected OperateInfo getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withTime(Date aValue) {
            instance.setTime(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withOperator(Operator aValue) {
            instance.setOperator(aValue);

            return (GeneratorT) this;
        }

        public OperatorOperatorBuilder withOperator() {
            Operator obj = new Operator();

            withOperator(obj);

            return new OperatorOperatorBuilder(obj);
        }

        public class OperatorOperatorBuilder extends OperatorBuilderBase<OperatorOperatorBuilder> {
            public OperatorOperatorBuilder(Operator aInstance) {
                super(aInstance);
            }

            @SuppressWarnings("unchecked")
            public GeneratorT endOperator() {
                return (GeneratorT) OperateInfoBuilderBase.this;
            }
        }
    }

    public static class OperatorBuilderBase<GeneratorT extends OperatorBuilderBase<GeneratorT>> {
        private Operator instance;

        protected OperatorBuilderBase(Operator aInstance) {
            instance = aInstance;
        }

        protected Operator getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withId(String aValue) {
            instance.setId(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withCode(String aValue) {
            instance.setCode(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withFullName(String aValue) {
            instance.setFullName(aValue);

            return (GeneratorT) this;
        }
    }
}
// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.hd123.sardine.wms.service.test.basicinfo.bintype;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.sardine.wms.api.basicInfo.bintype.BinType;
import com.hd123.sardine.wms.common.entity.OperateInfo;
import com.hd123.sardine.wms.common.entity.Operator;
import com.hd123.sardine.wms.common.utils.POJORandomInitionlizer;

public class BinTypeBuilder extends BinTypeBuilderBase<BinTypeBuilder> {
    public static BinTypeBuilder binType() {
        return new BinTypeBuilder();
    }

    public BinTypeBuilder() {
        super(new BinType());
    }

    public BinType build() {
        return getInstance();
    }
}

class BinTypeBuilderBase<GeneratorT extends BinTypeBuilderBase<GeneratorT>> {
    private BinType instance;

    protected BinTypeBuilderBase(BinType aInstance) {
        instance = aInstance;
        POJORandomInitionlizer.randomInit(instance);
    }

    protected BinType getInstance() {
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
    public GeneratorT withLength(BigDecimal aValue) {
        instance.setLength(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withWidth(BigDecimal aValue) {
        instance.setWidth(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withHeight(BigDecimal aValue) {
        instance.setHeight(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withPlotRatio(BigDecimal aValue) {
        instance.setPlotRatio(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withBearing(BigDecimal aValue) {
        instance.setBearing(aValue);

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
            return (GeneratorT) BinTypeBuilderBase.this;
        }
    }

    public class LastModifyInfoOperateInfoBuilder
            extends OperateInfoBuilderBase<LastModifyInfoOperateInfoBuilder> {
        public LastModifyInfoOperateInfoBuilder(OperateInfo aInstance) {
            super(aInstance);
        }

        @SuppressWarnings("unchecked")
        public GeneratorT endLastModifyInfo() {
            return (GeneratorT) BinTypeBuilderBase.this;
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
